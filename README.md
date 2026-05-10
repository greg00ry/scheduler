# Scheduler

Employee scheduling system built with Spring Boot and React. Managers can create work schedules, assign shifts, and track employee availability and absences.

**Live demo:** [scheduler-demo.gt-processing.com](http://scheduler-demo.gt-processing.com)

## Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security + JWT
- H2 (demo) / PostgreSQL (production)
- Lombok

**Frontend**
- React + TypeScript
- Axios
- TanStack Query

**Infrastructure**
- Docker + Docker Compose
- NGINX (reverse proxy)
- Oracle Cloud Infrastructure (OCI VM)

## Architecture

```
Internet → NGINX (port 80) → React frontend
                           → Spring Boot API (port 8080)
                                          → H2 in-memory DB
```

## Getting Started

```bash
./mvnw spring-boot:run
```

Set environment variables before running:
```
JWT_SECRET=your-base64-secret-minimum-32-characters
ADMIN_PASSWORD=your-admin-password
```

API available at `http://localhost:8080`

## API Endpoints

### Auth `/api/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login, returns JWT token |

### Users `/api/user`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user/{id}` | Get user by ID |
| GET | `/api/user/all` | Get all users |
| GET | `/api/user/details?id={id}` | Get user with absences and working hours |
| GET | `/api/user/available?date={date}` | Get available users by date |
| GET | `/api/user/by-role?role={role}` | Get users by role |
| POST | `/api/user` | Create new user (MANAGER, ADMIN) |
| PUT | `/api/user/update` | Update user |
| DELETE | `/api/user/{id}` | Delete user (ADMIN only) |

### Schedules `/api/schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedule/{id}` | Get schedule by ID |
| GET | `/api/schedule/all` | Get all schedules |
| POST | `/api/schedule/create` | Create schedule with shifts (MANAGER, ADMIN) |
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

| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access, manages all companies |
| `MANAGER` | Creates schedules, manages employees |
| `EMPLOYEE` | Views own schedule, reports absences and availability |

## Deployment

Deployed on Oracle Cloud Infrastructure VM using Docker Compose.

```bash
# On the VM
docker compose up -d
```

## Roadmap

- Polish labor code compliance validator
- Multi-tenant support (Company model)
- WebSocket notifications
- RFID attendance tracking
- Mobile app (React Native)
- CI/CD with GitHub Actions
