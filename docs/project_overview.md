# Project Overview: HelpHub

## Introduction
**HelpHub** is an On-Demand Multi-Service Application designed specifically for the Sri Lankan market. The platform provides a unified ecosystem connecting customers with various service providers (employees/dispatchers) across multiple service domains.

## Architecture
The backend application (`helphub_be`) is built using a modern technology stack to ensure scalability, maintainability, and security.

### Core Technologies
- **Framework:** Spring Boot (Java)
- **Architecture Pattern:** Layered Architecture (Domain-Driven Design inspired)
  - `Domain Layer`: Core business entities and rules.
  - `Application Layer`: Use cases, DTOs, and application services.
  - `Infrastructure Layer`: Database persistence, security configuration, and external integrations.
  - `Presentation Layer`: RESTful API controllers.
- **Database:** PostgreSQL
- **Database Migration:** Liquibase
- **Security:** Spring Security with JWT (JSON Web Tokens)

## Database Schema (Authentication Module)
The initial module focuses on robust user management and authentication. The database structure includes:

- **Users:** Core entity for all platform participants (e.g., customers, employees, admins). Includes personal details, contact information, and account status.
- **Roles & Permissions:** Role-Based Access Control (RBAC) to manage authorization across the platform. Users can have multiple roles, and roles encapsulate multiple permissions.
- **Password Resets:** Secure mechanism to handle password recovery.
- **Verification Documents:** To ensure the safety and reliability of the platform, service providers and users can upload verification documents.
- **Images:** Centralized management of images, avatars, and document scans, linked securely to users.

## Current Status & Next Steps
- [x] Defined initial database schema (Authentication Module).
- [x] Implement the database schema using Liquibase.
- [ ] Build the domain and infrastructure layers for the Authentication Module.
- [ ] Develop the Registration and Login REST APIs (`/register`, `/login`).
- [ ] Integrate advanced authorization (RBAC) checks within the application.
