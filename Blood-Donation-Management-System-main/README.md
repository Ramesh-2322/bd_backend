# Blood Donation Management System

This repository now contains a full **Spring Boot backend** and **React frontend** implementation of the Blood Donation Management System (BDMS). The legacy Django project remains in `Code/` for reference, while the new stack lives in `backend/` and `frontend/`.

---

## Features

- **Donor Registration and Management**
  - Donor signup with profile and image upload
  - Availability toggle (ready / not ready)
  - Profile editing

- **Patient Blood Requests**
  - Submit requests with blood group and contact details
  - Browse all active requests

- **Donor Discovery**
  - Search donors by blood group
  - Filter to show only donors who are ready to donate

- **Authentication**
  - JWT-based login and protected profile endpoints

---

## Tech Stack

**Backend**
- Spring Boot 3 (Java 17)
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL (default)

**Frontend**
- React 18 + Vite
- Axios + React Router

---

## Project Structure

- `backend/` Spring Boot API
- `frontend/` React UI
- `Code/` Legacy Django app (original project)

---

## Backend Setup (Spring Boot)

1. Configure PostgreSQL and create a database:
   - Database: `bdms`
   - User: `bdms`
   - Password: `bdms`

2. Update connection details in:
   - `backend/src/main/resources/application.yml`

3. Run the backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

The API starts on `http://localhost:8080`.

---

## Frontend Setup (React)

1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Start the dev server:
   ```bash
   npm run dev
   ```

The UI starts on `http://localhost:5173`.

---

## API Endpoints (Summary)

**Public**
- `GET /api/public/blood-groups`
- `GET /api/public/donors?bloodGroupId=&readyOnly=`
- `GET /api/public/donors/{id}`
- `POST /api/public/requests`
- `GET /api/public/requests`

**Auth**
- `POST /api/auth/register` (multipart/form-data)
- `POST /api/auth/login`

**Donor (JWT required)**
- `GET /api/profile`
- `PUT /api/profile` (multipart/form-data)
- `POST /api/profile/toggle-ready`

---

## Notes

- Uploaded donor images are stored under `backend/uploads/` and served at `/uploads/*`.
- The backend seeds standard blood groups automatically via `data.sql`.

---

## Legacy Django App

The original Django version is preserved in `Code/` and can still be used independently.

---

## Contributors

- Original Django project and migration by the repository authors.
