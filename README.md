# Scheduler API

REST API for employee schedule management built with Spring Boot. Allows managers to create work schedules, assign shifts to employees, and track availability and absences.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2 (in-memory database)
- Lombok
- Bean Validation

## Getting Started

```bash
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`

## API Endpoints

### Users `/api/user`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user?id={id}` | Get user by ID |
| GET | `/api/user/all` | Get all users |
| GET | `/api/user/details?id={id}` | Get user with absences and working hours |
| GET | `/api/user/available?date={date}` | Get available users by date |
| GET | `/api/user/by-role?role={role}` | Get users by role (EMPLOYEE / MANAGER) |
| POST | `/api/user` | Create new user |

### Schedules `/api/schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedule/{id}` | Get schedule by ID |
| GET | `/api/schedule/all` | Get all schedules |
| POST | `/api/schedule` | Create schedule with shifts |
| GET | `/api/schedule/{id}/shifts` | Get all shifts for a schedule |
| GET | `/api/schedule/shift/{id}` | Get shift by ID |
| GET | `/api/schedule/shift/all` | Get all shifts |

## Roles

- `EMPLOYEE` - regular worker
- `MANAGER` - can create and manage schedules

## Deployment
(in progress)
Deployed on Oracle Cloud Infrastructure (OCI). Author holds OCI Associate certification.

## Roadmap

- Spring Security + JWT authentication (in progress)
- Role-based authorization (in progress)
- Work schedule validator (Polish labor code compliance) (in progress)
- Auto-scheduling microservice (in progress)
