# HireHub AI - Job Portal Application

A full-stack Spring Boot job portal web application for connecting employers and job seekers with role-based access control, interactive dashboards, job postings, and application management.

## 🚀 Features

- **User Authentication & Authorization**: Spring Security integration with Role-Based Access Control (RBAC) for `ROLE_EMPLOYER` and `ROLE_JOBSEEKER`.
- **Employer Dashboard**: Create, edit, and manage job listings; view and track applicant status.
- **Job Seeker Dashboard**: Search active job listings, apply with cover letters & resumes, track application status.
- **Auto Data Seeding**: Initial demo accounts pre-configured for instant testing.
- **Responsive UI**: Clean Bootstrap 5 interface tailored for mobile and desktop views.

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.5 (Web, Data JPA, Security, Validation)
- **Frontend**: Thymeleaf, HTML5, CSS3, Bootstrap 5
- **Database**: H2 (In-Memory for Dev/Demo), MySQL (Production-ready configuration)
- **Build Tool**: Maven
- **Containerization & Deployment**: Docker, Render (`render.yaml`)

## 🔑 Demo Accounts

When the application starts, the database is automatically seeded with initial demo credentials:

| Role | Email | Password |
| --- | --- | --- |
| Employer | `employer@hirehub.com` | `password123` |
| Job Seeker | `jobseeker@hirehub.com` | `password123` |

## 💻 Running Locally

### Prerequisites
- JDK 21 or higher
- Git

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/hirehub-ai.git
   cd hirehub-ai
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the web application:**
   Open your browser and navigate to `http://localhost:8080`.

4. **Access the H2 Database Console (Optional):**
   Navigate to `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:hirehubdb`
   - Username: `sa`
   - Password: *(leave blank)*

## 🌐 Deploying to Render

This project includes pre-configured deployment files (`render.yaml` & `Dockerfile`).

1. Push your repository to GitHub.
2. Log into [Render](https://render.com/).
3. Click **New +** -> **Blueprint**.
4. Connect your GitHub repository. Render will automatically detect `render.yaml` and deploy the service.

## 📁 Project Structure

```
hirehub-ai/
├── src/main/java/com/hirehub/
│   ├── config/          # Security & Data Initializer configurations
│   ├── controller/      # Web MVC controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA Entity definitions
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Business logic services
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML templates
│   └── application.yaml # Application configurations
├── Dockerfile           # Multi-stage Docker build configuration
├── render.yaml          # Render Blueprint deployment definition
└── pom.xml              # Maven dependencies & build setup
```
