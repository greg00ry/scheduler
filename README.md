# Scheduler API

REST API for employee schedule management built with Spring Boot. Allows managers to create work schedules, assign shifts to employees, and track availability and absences.

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security + JWT
- H2 (in-memory database)
- Lombok
- Bean Validation

## Getting Started

```bash
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`

Set the following environment variables before running:
```
JWT_SECRET=your-secret-key-minimum-32-characters
```

## API Endpoints

### Auth `/api/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login and receive JWT token |

### Users `/api/user`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user/{id}` | Get user by ID |
| GET | `/api/user/all` | Get all users |
| GET | `/api/user/{id}/details` | Get user with absences and working hours |
| GET | `/api/user/available?date={date}` | Get available users by date |
| GET | `/api/user/by-role?role={role}` | Get users by role |
| POST | `/api/user` | Create new user |
| PUT | `/api/user/update` | Update user |

### Schedules `/api/schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedule/{id}` | Get schedule by ID |
| GET | `/api/schedule/all` | Get all schedules |
| POST | `/api/schedule/create` | Create schedule with shifts |
| GET | `/api/schedule/{id}/shifts` | Get all shifts for a schedule |
| GET | `/api/schedule/shift/{id}` | Get shift by ID |
| GET | `/api/schedule/shift/all` | Get all shifts |

### Absence `/api/absence`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/absence` | Report absence |
| DELETE | `/api/absence/{id}` | Delete absence |

### Availability `/api/availability`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/availability` | Add availability |
| DELETE | `/api/availability/{id}` | Delete availability |

## Roles

- `EMPLOYEE` - regular worker
- `MANAGER` - can create and manage schedules

## Deployment

Deployed on Oracle Cloud Infrastructure (OCI). Author holds OCI Associate certification.
(in progress)

## Roadmap

- Role-based authorization (in progress)
- Work schedule validator — Polish labor code compliance (in progress)
- Auto-scheduling microservice (in progress)
- CI/CD pipeline with OCI Container Registry
