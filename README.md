# Habiko — Habit Tracker Backend

REST API backend for the Habiko habit tracking mobile application.
Built with Java, Spring Boot, and PostgreSQL. Deployed on a virtual
machine with HTTPS and a custom domain.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Build Tool:** Gradle
- **Database:** PostgreSQL
- **Deployment:** Virtual machine with custom domain + HTTPS
- **CI/CD:** GitHub Actions

## Features

- User registration and authentication (JWT-based)
- Full CRUD operations for habits
- Secure HTTPS endpoints
- PostgreSQL database integration

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |
| GET | `/auth/me` | Get current authenticated user |

### Habits
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/habits` | Get all habits for authenticated user |
| POST | `/api/habits` | Create a new habit |
| PUT | `/api/habits/{id}` | Update an existing habit |
| DELETE | `/api/habits/{id}` | Delete a habit |

### Daily Habits
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/daily-habits` | Get today's habits for authenticated user |
| PATCH | `/api/daily-habits/{habitId}/completion` | Update completion status of a habit for today |

### Habit Completions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/habit-completions` | Get all habit completion records for authenticated user |
### Setup

1. Clone the repository
```bash
git clone https://github.com/http-dobrev/habit-tracker-backend.git
cd habit-tracker-backend
```

2. Configure your database in `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/habittracker
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Run the application
```bash
./gradlew bootRun
```

The API will start on `http://localhost:8080`

## Project Structure

```
src/
└── main/
    └── java/com/konstantin/habittracker/
        ├── controller/       # REST controllers
        ├── business/logic/
        │   └── service/      # Business logic
        ├── repository/       # Database access
        ├── model/            # Entity classes
        ├── dto/
        │   ├── request/      # Request DTOs
        │   └── response/     # Response DTOs
        └── security/         # JWT and auth config
```
