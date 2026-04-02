# BDMS Node Backend

Blood Donation Management System backend built with Node.js, Express, MongoDB (Mongoose), JWT auth, bcrypt password hashing, and multer file upload.

## Features

- User authentication (`register`, `login`)
- Blood request CRUD flow (create/list/get/update status)
- Appointment create/list
- Report upload with multer (`/uploads` static serving)
- JWT protected routes
- Request validation with `express-validator`
- Pagination on list endpoints
- Centralized error handling middleware

## Project Structure

```text
node-backend/
  config/
  controllers/
  middleware/
  models/
  routes/
  validators/
  uploads/
  app.js
  server.js
```

## Setup

1. Install dependencies:

```bash
npm install
```

2. Create `.env` from `.env.example` and update values.

3. Run development server:

```bash
npm run dev
```

4. Run production mode:

```bash
npm start
```

## API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Requests (JWT required)

- `POST /api/requests`
- `GET /api/requests?page=1&limit=10&status=Pending`
- `GET /api/requests/:id`
- `PUT /api/requests/:id/status`

### Appointments (JWT required)

- `POST /api/appointments`
- `GET /api/appointments?page=1&limit=10&status=Pending`

### Reports (JWT required)

- `POST /api/reports/upload` (form-data key: `file`)

## Example Request Bodies

### Register

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

### Login

```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

### Create Blood Request

```json
{
  "patientName": "Patient A",
  "bloodGroup": "O+",
  "unitsRequired": 2,
  "hospital": "City Hospital",
  "location": "Downtown",
  "contactNumber": "9876543210"
}
```

### Update Request Status

```json
{
  "status": "Approved"
}
```

### Create Appointment

```json
{
  "hospital": "City Hospital",
  "date": "2026-04-01",
  "time": "10:30",
  "status": "Pending"
}
```
