# InternTrack — Internship & Placement Tracker

A full-stack, production-style web application for college students to **track internship applications**, **discover opportunities**, **analyze resumes**, **detect internship scams**, and **connect with mentors** — built with Spring Boot 3 + React + TypeScript + MySQL.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│         React + Vite + TypeScript + Tailwind        │ ← Port 5173
│  (SPA with React Router, Recharts, Lucide Icons)    │
└───────────────────────┬─────────────────────────────┘
                        │ /api/* proxy
┌───────────────────────▼─────────────────────────────┐
│       Spring Boot 3.3 + Spring Security + JDBC      │ ← Port 8080
│   (JWT Auth, BCrypt, PDFBox, REST Controllers)      │
└───────────────────────┬─────────────────────────────┘
                        │ JDBC
┌───────────────────────▼─────────────────────────────┐
│              MySQL 8.0 (interntrack DB)             │ ← Port 3306
│  22 tables, seed data with 5 demo users             │
└─────────────────────────────────────────────────────┘
```

## ✨ Features

| Module | Description |
|--------|-------------|
| **Authentication** | JWT-based with BCrypt password hashing, role-based access (Student/Mentor/Admin) |
| **Application Tracker** | CRUD applications, status pipeline (Saved→Applied→Assessment→Interview→Offer/Rejected), reminders |
| **Opportunity Board** | Browse verified internships/placements with filters, eligibility checking, save & quick-apply |
| **Resume Analyzer** | PDF upload (PDFBox), ATS completeness scoring, JD-match analysis with skill extraction |
| **Scam Detector** | Heuristic risk assessment of job postings and recruiter messages, risk score + recommendations |
| **Community Hub** | Posts with categories, upvotes, comments, bookmarks; content reporting |
| **Referral System** | Students request referrals from verified mentors; mentors accept/decline with responses |
| **Profile Management** | Complete student/mentor profiles with skills, interests, links, password change |
| **Admin Dashboard** | Platform metrics, content moderation, opportunity report resolution |

## 📋 Prerequisites

- **Java 21+** (JDK) and **Maven 3.9+**
- **Node.js 20.19+** (or 22.12+) and **npm**
- **MySQL 8.0+** with a dedicated user permitted to create/alter tables in the `interntrack` database

## 🚀 Quick Start (Windows PowerShell)

Create the `interntrack` database using MySQL Workbench or this MySQL client command (it will prompt for the MySQL administrator password):

```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS interntrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Then open PowerShell at the repository root and set your local backend environment (keep credentials out of source control):

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'interntrack'
$env:DB_USERNAME = '<your-mysql-user>'
$env:DB_PASSWORD = '<your-mysql-password>'
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Keep this terminal open. Start the backend; Flyway applies the version-controlled schema in `database/migrations` and records applied versions:

```powershell
Set-Location .\backend
mvn spring-boot:run
```

The backend is at **http://localhost:8080**. For an existing non-empty database, back it up before startup. Flyway baselines an untracked existing schema at version 1 (the existing V1 schema) and applies only later migrations; an empty database receives V1. Seed data is optional and is not loaded automatically. To apply the demo seed to a local development database, use a second terminal at the repository root:

```powershell
mysql -u <your-mysql-user> -p interntrack < .\database\seed\V2__seed_demo_data.sql
```

The demo SQL password hashes have not been verified as usable credentials. The application no longer changes demo passwords at startup. Use public registration to create and log in with an account, or verify the demo hashes before using seeded accounts. Do not run the legacy `database/seed/fix_passwords.sql` file.

In another PowerShell terminal, start the frontend:

```powershell
Set-Location .\frontend
npm install
npm run dev
```

Open **http://localhost:5173**. Vite proxies `/api` requests to the backend.

Run the available checks:

```powershell
Set-Location .\backend
mvn test
Set-Location ..\frontend
npm run build
```

The backend integration test uses in-memory H2 and does not verify connectivity to your MySQL server. The backend must be started against your configured MySQL instance to verify the live connection.

## 🔧 Configuration

All config is in `backend/src/main/resources/application.yml` with env-var overrides:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_USERNAME` | Required | MySQL username |
| `DB_PASSWORD` | Required | MySQL password |
| `DB_MIGRATIONS_ENABLED` | `true` | Apply Flyway migrations at startup |
| `JWT_SECRET` | Required | Base64 key that decodes to at least 32 random bytes |
| `COOKIE_SECURE` | `false` | Set `true` when deployed behind HTTPS |
| `UPLOAD_DIR` | `./storage/resumes` | Resume PDF upload directory |
| `AI_API_KEY` | (empty) | OpenAI API key for AI-powered analysis (falls back to rule-based) |

## 📁 Project Structure

```
Interntrack/
├── backend/                          # Spring Boot 3 backend
│   ├── pom.xml                       # Maven with Spring Boot, JDBC, Security, JWT, PDFBox
│   └── src/main/java/com/interntrack/
│       ├── InternTrackApplication.java
│       ├── admin/                    # Admin controller, service, DAO
│       ├── applications/             # Application CRUD, status tracking, reminders
│       ├── auth/                     # Registration, login, JWT auth
│       ├── common/                   # ApiResponse, exceptions, global handler
│       ├── community/               # Posts, comments, upvotes, reports
│       ├── config/                   # Security, CORS, DB, OpenAPI, DemoDataInitializer
│       ├── opportunities/           # Opportunity board, eligibility, save/apply
│       ├── referrals/               # Referral requests between students and mentors
│       ├── resumes/                 # PDF upload, text extraction, analysis
│       ├── scamdetector/            # Heuristic scam risk assessment
│       ├── security/                # JWT filter, UserPrincipal, JwtUtil
│       └── users/                   # User, profiles, mentor listing
├── frontend/                         # React + Vite + TypeScript + Tailwind CSS 4
│   ├── src/
│   │   ├── context/AuthContext.tsx   # Global auth state management
│   │   ├── components/              # Sidebar, AppLayout
│   │   ├── pages/                   # All 10 pages (Login, Dashboard, etc.)
│   │   ├── services/                # API service layer (axios)
│   │   └── types/                   # TypeScript interfaces
│   ├── package.json
│   └── vite.config.ts               # Dev proxy to backend
└── database/
    ├── migrations/V1__init_schema.sql  # 22 tables with indexes & FKs
    └── seed/V2__seed_demo_data.sql     # Demo users, profiles, opportunities, posts
```

## 🔌 API Documentation

Swagger UI is available at: **http://localhost:8080/swagger-ui.html**

## 🛡️ Security

- **JWT Authentication** with configurable expiration
- **BCrypt** password hashing (10 rounds)
- **CORS** configured for dev (localhost:5173)
- **Role-based access**: Student, Mentor, Admin
- **CSRF disabled** (stateless JWT)
- **Input validation** via Spring Validation
- **SQL injection protection** via parameterized JDBC queries
- **File upload validation** (PDF only, 5MB max, MIME type check)

For production, use HTTPS, set `COOKIE_SECURE=true`, provide secrets through the deployment environment/secret manager, and use a database account restricted to this application schema.

## 🧪 Testing

```bash
cd backend
mvn test
```

---

Built with ❤️ for college placement cells and students.
