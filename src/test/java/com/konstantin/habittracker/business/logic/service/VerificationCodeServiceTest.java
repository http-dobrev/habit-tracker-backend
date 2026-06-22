package com.konstantin.habittracker.business.logic.service;

import com.konstantin.habittracker.exception.InvalidCredentialsException;
import com.konstantin.habittracker.model.User;
import com.konstantin.habittracker.model.UserRole;
import com.konstantin.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock UserRepository userRepository;
    @Mock JavaMailSender mailSender;

    @InjectMocks VerificationCodeService verificationCodeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John", "john@example.com", "hashed", UserRole.USER);
    }

    @Test
    void sendCode_setsSixDigitVerificationCodeOnUser() {
        verificationCodeService.sendCode(user);

        assertThat(user.getVerificationCode()).matches("\\d{6}");
    }

    @Test
    void sendCode_setsExpiryFifteenMinutesFromNow() {
        Instant before = Instant.now();

        verificationCodeService.sendCode(user);

        Instant expectedExpiry = before.plus(15, ChronoUnit.MINUTES);
        long diffSeconds = Math.abs(
                user.getVerificationCodeExpiry().getEpochSecond() - expectedExpiry.getEpochSecond());
        assertThat(diffSeconds).isLessThan(5);
    }

    @Test
    void sendCode_savesUserWithUpdatedCode() {
        verificationCodeService.sendCode(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue()).isSameAs(user);
        assertThat(userCaptor.getValue().getVerificationCode()).isEqualTo(user.getVerificationCode());
    }

    @Test
    void sendCode_sendsEmailToUserWithCodeInBody() {
        verificationCodeService.sendCode(user);

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage sentMessage = mailCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("john@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Verify your email");
        assertThat(sentMessage.getText()).contains(user.getVerificationCode());
        assertThat(sentMessage.getText()).contains("15 minutes");
    }

    @Test
    void verifyCode_throwsWhenNoCodeHasBeenSet() {
        assertThatThrownBy(() -> verificationCodeService.verifyCode(user, "123456"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Request a new one");
    }

    @Test
    void verifyCode_throwsWhenCodeIsSetButExpiryIsNull() {
        user.setVerificationCode("123456");

        assertThatThrownBy(() -> verificationCodeService.verifyCode(user, "123456"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void verifyCode_throwsWhenCodeHasExpired() {
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiry(Instant.now().minusSeconds(60));

        assertThatThrownBy(() -> verificationCodeService.verifyCode(user, "123456"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void verifyCode_throwsWhenCodeDoesNotMatch() {
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiry(Instant.now().plusSeconds(60));

        assertThatThrownBy(() -> verificationCodeService.verifyCode(user, "000000"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid verification code");
    }

    @Test
    void verifyCode_succeedsWhenCodeMatchesAndNotExpired() {
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiry(Instant.now().plusSeconds(60));

        verificationCodeService.verifyCode(user, "123456");
        // reaching this line without an exception is the assertion
    }
}