# Habiko — Backend API

REST API backend for the Habiko habit tracking application.
Built with Java, Spring Boot, and PostgreSQL.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Build tool:** Gradle
- **Database:** PostgreSQL
- **Deployment:** Hosted on a virtual machine with a custom domain and HTTPS

## Features

- User registration and authentication with JWT tokens
- Full CRUD operations for habits
- Secure endpoints — all habit routes require authentication
- PostgreSQL database for persistent storage
- HTTPS enforced on all endpoints

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
| GET | `/habits` | Get all habits for the authenticated user |
| POST | `/habits` | Create a new habit |
| PUT | `/habits/{id}` | Update a habit |
| DELETE | `/habits/{id}` | Delete a habit |

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL
- Gradle

### Setup

1. Clone the repository
```bash
