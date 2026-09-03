# \# Sunrise Dental Clinic - Appointment and Patient Management System

# 

# A web-based appointment and patient management system developed for the \*\*CIS6003 Advanced Programming\*\* module.

# 

# The system is designed for Sunrise Dental Clinic to replace paper-based patient records, appointment scheduling, and billing processes with a secure and structured digital solution. It provides role-based access for administrators, receptionists, and dentists and supports patient management, appointment scheduling, billing, reporting, and REST-based data access.

# 

# \---

# 

# \## Features

# 

# \### Authentication and Role-Based Access

# \- Secure username and password authentication

# \- Passwords stored using BCrypt hashing

# \- Role-based authorization using Spring Security

# \- Three user roles:

# &#x20; - Administrator

# &#x20; - Receptionist

# &#x20; - Dentist

# \- Session-based authentication

# \- Restricted pages and functions based on user role

# 

# \### Patient Management

# \- Register new patients

# \- Search existing patients

# \- Update patient information

# \- Input validation for required fields and contact numbers

# 

# \### Appointment Management

# \- Generate unique appointment numbers automatically

# \- Register new appointments

# \- Search appointments

# \- View appointment details

# \- Update appointments

# \- Cancel appointments while retaining appointment history

# \- Prevent appointments from being created in the past

# \- Prevent dentist double-booking for the same date and time

# \- Validate active dentists and treatments

# 

# \### Billing

# \- Generate bills for appointments

# \- Automatically calculate:

# 

# &#x20; `Total Amount = Treatment Fee + Consultation Fee`

# 

# \- Prevent duplicate billing for the same appointment

# \- Prevent billing of cancelled appointments

# \- Generate printable receipts

# 

# \### Reports

# \- Appointment reports

# \- Billing reports

# \- Filtering options for report data

# 

# \### Additional Features

# \- Help section for system users

# \- Secure logout

# \- REST API for patient and appointment data

# \- User-friendly validation and error messages

# 

# \---

# 

# \## Technology Stack

# 

# | Technology | Purpose |

# |------------|---------|

# | Java 21 | Main programming language |

# | Spring Boot | Application framework |

# | Spring MVC | Web application architecture |

# | Spring Security | Authentication and authorization |

# | Spring Data JPA | Data-access layer |

# | Hibernate | Object-relational mapping |

# | Thymeleaf | Server-side HTML templates |

# | Bootstrap 5 | User-interface styling |

# | MySQL 8 | Relational database |

# | Maven | Dependency management and build automation |

# | JUnit 5 | Automated testing |

# | Mockito | Unit-test mocking |

# | Git | Version control |

# | GitHub | Remote repository and collaboration |

# | GitHub Actions | Continuous Integration |

# 

# \---

# 

# \## System Architecture

# 

# The application follows a \*\*three-tier architecture\*\*.

# 

# \### Presentation Layer

# Implemented using:

# \- Spring MVC controllers

# \- Thymeleaf templates

# \- Bootstrap 5

# \- HTML/CSS/JavaScript

# 

# This layer provides the user interface and handles interaction between users and the application.

# 

# \### Business Layer

# Implemented using service classes.

# 

# The service layer contains the main business rules and validation, including:

# \- Appointment validation

# \- Dentist availability checking

# \- Appointment status rules

# \- Billing calculations

# \- Duplicate billing prevention

# 

# \### Data Access Layer

# Implemented using:

# \- JPA entities

# \- Spring Data JPA repositories

# \- Hibernate

# \- MySQL 8

# 

# The repository layer separates database operations from the application's business logic.

# 

# \### Web Services

# Read-only REST endpoints are provided under:

# 

# ```text

# /api

# ```

# 

# These endpoints provide authenticated JSON access to patient and appointment information.

# 

# \---

# 

# \## Prerequisites

# 

# Before running the application, install:

# 

# | Tool | Version |

# |------|---------|

# | JDK | 21 |

# | Maven | 3.9 or later |

# | MySQL | 8.x |

# | IDE | IntelliJ IDEA recommended |

# 

# MySQL should be running on:

# 

# ```text

# localhost:3306

# ```

# 

# The database:

# 

# ```text

# sunrise\_dental\_clinic

# ```

# 

# must exist before the application is started.

# 

# A reference copy of the database schema is available at:

# 

# ```text

# docs/schema.sql

# ```

# 

# \---

# 

# \## Database Configuration

# 

# The application is configured to obtain database credentials from environment variables rather than storing the MySQL password directly in the Git repository.

# 

# The relevant configuration in `application.properties` is:

# 

# ```properties

# spring.datasource.url=jdbc:mysql://localhost:3306/sunrise\_dental\_clinic?useSSL=false\&serverTimezone=UTC\&allowPublicKeyRetrieval=true

# spring.datasource.username=${DB\_USERNAME:root}

# spring.datasource.password=${DB\_PASSWORD:}

# ```

# 

# \### Windows Configuration

# 

# Open:

# 

# \*\*System Properties > Advanced > Environment Variables\*\*

# 

# Create the following user variables:

# 

# ```text

# DB\_USERNAME

# DB\_PASSWORD

# ```

# 

# Example:

# 

# ```text

# DB\_USERNAME=root

# DB\_PASSWORD=your\_mysql\_password

# ```

# 

# Do \*\*not\*\* commit real database passwords to the Git repository.

# 

# \---

# 

# \## Opening the Project in IntelliJ IDEA

# 

# 1\. Start IntelliJ IDEA.

# 2\. Select \*\*Open\*\*.

# 3\. Select the `sunrise-dental-clinic` folder containing `pom.xml`.

# 4\. Choose \*\*Open as Project\*\* if prompted.

# 5\. Allow IntelliJ IDEA to detect the Maven project and download the required dependencies.

# 6\. Go to:

# 

# &#x20;  \*\*File > Project Structure > Project\*\*

# 

# 7\. Confirm that the project SDK is \*\*JDK 21\*\*.

# 8\. Make sure MySQL is running.

# 9\. Make sure the required database and environment variables have been configured.

# 

# \---

# 

# \## Running the Application

# 

# Open a terminal in the project directory and run:

# 

# ```bash

# mvn spring-boot:run

# ```

# 

# Alternatively, open:

# 

# ```text

# SunriseDentalClinicApplication

# ```

# 

# in IntelliJ IDEA and click the green \*\*Run\*\* button.

# 

# Once the application has started, open:

# 

# ```text

# http://localhost:8080

# ```

# 

# The application redirects unauthenticated users to the login page.

# 

# \---

# 

# \## Demonstration Accounts

# 

# The following accounts are provided for \*\*development and demonstration purposes only\*\*.

# 

# | Username | Password | Role |

# |----------|----------|------|

# | admin | Admin@123 | ADMIN |

# | reception | Reception@123 | RECEPTIONIST |

# | dentist | Dentist@123 | DENTIST |

# 

# The demonstration accounts are automatically created when the application starts if the `users` table is empty.

# 

# Passwords are hashed using BCrypt before being stored in the database.

# 

# The demonstration passwords are configured using:

# 

# ```properties

# app.demo.admin-password=Admin@123

# app.demo.receptionist-password=Reception@123

# app.demo.dentist-password=Dentist@123

# ```

# 

# The demonstration data initializer can be disabled using:

# 

# ```properties

# app.demo.data-enabled=false

# ```

# 

# These credentials are intended only for local development and academic demonstration.

# 

# \---

# 

# \## User Roles

# 

# \### Administrator

# 

# The administrator can access administrative functions including:

# \- User management

# \- Dentist management

# \- Treatment management

# \- Reports

# \- Help

# \- Logout

# 

# \### Receptionist

# 

# The receptionist handles the main operational functions of the clinic:

# \- Patient registration and search

# \- Appointment registration

# \- Appointment search

# \- Appointment updates

# \- Appointment cancellation

# \- Bill generation

# \- Receipt printing

# \- Reports

# \- Help

# \- Logout

# 

# \### Dentist

# 

# The dentist has restricted, primarily read-only access:

# \- View appointments

# \- Search/view patient information

# \- Filter appointment information

# \- View help

# \- Logout

# 

# \---

# 

# \## Database Structure

# 

# The application uses six main database tables:

# 

# ```text

# users

# patients

# dentists

# treatments

# appointments

# bills

# ```

# 

# The database uses:

# \- Primary keys

# \- Foreign keys

# \- Unique constraints

# \- CHECK constraints

# \- Referential integrity

# \- Unique dentist/date/time appointment constraints

# 

# The database schema is available in:

# 

# ```text

# docs/schema.sql

# ```

# 

# \---

# 

# \## Business Rules

# 

# The following business rules are enforced by the application:

# 

# 1\. Every appointment receives a generated unique appointment number (`APT-...`).

# 2\. A dentist cannot have two appointments at the same date and time.

# 3\. Appointments cannot be created for a date in the past.

# 4\. A patient, dentist, and treatment must be selected when creating an appointment.

# 5\. Only active dentists can be selected.

# 6\. Only active treatments can be selected.

# 7\. The bill total is calculated as:

# 

# &#x20;  `Total Amount = Treatment Fee + Consultation Fee`

# 

# 8\. Only one bill can exist for each appointment.

# 9\. A cancelled appointment cannot be billed.

# 10\. Cancelled appointments are retained for historical and reporting purposes.

# 11\. Completed and cancelled appointments are protected from inappropriate modification according to the implemented business rules.

# 

# \---

# 

# \## REST API

# 

# The application provides authenticated read-only REST endpoints.

# 

# | Method | Endpoint | Description |

# |--------|----------|-------------|

# | GET | `/api/patients` | Retrieve all patients |

# | GET | `/api/patients/{id}` | Retrieve one patient |

# | GET | `/api/appointments` | Retrieve all appointments |

# | GET | `/api/appointments/{appointmentNumber}` | Retrieve an appointment by appointment number |

# 

# REST endpoints require authentication.

# 

# HTTP Basic authentication can be used with tools such as Postman or cURL.

# 

# Example:

# 

# ```bash

# curl -u reception:Reception@123 http://localhost:8080/api/patients

# ```

# 

# Sensitive password hashes are not returned by the API.

# 

# \---

# 

# \## Testing

# 

# The project contains automated tests using:

# 

# \- JUnit 5

# \- Mockito

# \- Spring Boot Test

# \- Spring Security Test

# 

# The automated test suite currently contains \*\*25 tests\*\* covering areas including:

# 

# \- Patient service operations

# \- Appointment business rules

# \- Dentist double-booking prevention

# \- Appointment status validation

# \- Billing calculations

# \- Duplicate billing prevention

# \- Cancelled appointment billing restrictions

# \- Role-based access control

# \- Authentication and authorization behaviour

# 

# Run the complete automated test suite using:

# 

# ```bash

# mvn clean test

# ```

# 

# The final local test execution produced:

# 

# ```text

# Tests run: 25

# Failures: 0

# Errors: 0

# Skipped: 0

# BUILD SUCCESS

# ```

# 

# The tests use mocked repositories where appropriate and therefore do not require the production MySQL database for the service-layer unit tests.

# 

# \---

# 

# \## Continuous Integration

# 

# The project uses \*\*GitHub Actions\*\* for Continuous Integration.

# 

# The workflow is located at:

# 

# ```text

# .github/workflows/maven.yml

# ```

# 

# The CI pipeline automatically runs the Maven test suite when changes are:

# 

# \- Pushed to `main`

# \- Submitted to `main` through a Pull Request

# 

# The workflow:

# 1\. Checks out the repository.

# 2\. Configures Temurin JDK 21.

# 3\. Configures Maven dependency caching.

# 4\. Runs:

# 

# ```bash

# mvn --batch-mode clean test

# ```

# 

# This ensures automated tests are executed before changes are accepted into the main development branch.

# 

# \---

# 

# \## Version Control Workflow

# 

# The project uses Git and GitHub for source-code version control.

# 

# Development practices demonstrated include:

# \- Incremental commits

# \- Meaningful commit messages

# \- Feature branches

# \- Pull Requests

# \- Automated CI testing

# \- Merge workflow

# \- Version tags

# 

# The CI implementation was developed using the feature branch:

# 

# ```text

# feature/ci-pipeline

# ```

# 

# It was merged into `main` through a Pull Request after the automated tests passed.

# 

# \### Version Tags

# 

# The repository contains the following version tags:

# 

# ```text

# v0.1.0

# v0.2.0

# v1.0.0

# ```

# 

# These tags represent major stages in the development and release of the application.

# 

# \---

# 

# \## Documented Assumptions

# 

# \- The `users` table does not contain a foreign key to the `dentists` table. Therefore, a user with the `DENTIST` role is not directly associated with an individual dentist database record.

# 

# \- Dentist users have read-only appointment access and can use the dentist filter to view the required dentist's schedule.

# 

# \- The scenario's \*\*Exit System\*\* requirement is implemented through the logout function, which safely terminates the authenticated HTTP session.

# 

# \- Appointment records are not physically deleted when cancelled. Their status is changed to `CANCELLED` so that historical information remains available for reports and auditing.

# 

# \- Each appointment has one patient, one dentist, and one treatment.

# 

# \- A maximum of one bill can be associated with an appointment.

# 

# \---

# 

# \## Project Structure

# 

# ```text

# sunrise-dental-clinic

# ├── .github

# │   └── workflows

# │       └── maven.yml

# ├── docs

# │   └── schema.sql

# ├── pom.xml

# ├── README.md

# └── src

# &#x20;   ├── main

# &#x20;   │   ├── java/com/sunrise/clinic

# &#x20;   │   │   ├── SunriseDentalClinicApplication.java

# &#x20;   │   │   ├── config/

# &#x20;   │   │   ├── controller/

# &#x20;   │   │   ├── dto/

# &#x20;   │   │   ├── entity/

# &#x20;   │   │   ├── exception/

# &#x20;   │   │   ├── repository/

# &#x20;   │   │   ├── rest/

# &#x20;   │   │   └── service/

# &#x20;   │   └── resources

# &#x20;   │       ├── application.properties

# &#x20;   │       ├── static/

# &#x20;   │       └── templates/

# &#x20;   └── test

# &#x20;       └── java/com/sunrise/clinic

# &#x20;           ├── controller/

# &#x20;           └── service/

# ```

# 

# \---

# 

# \## Screenshots

# 

# Screenshots demonstrating the major system functionality can be stored under:

# 

# ```text

# docs/screenshots/

# ```

# 

# Recommended evidence includes:

# \- Login page

# \- Administrator dashboard

# \- Receptionist dashboard

# \- Patient registration

# \- Appointment management

# \- Double-booking validation

# \- Bill and printable receipt

# \- Reports

# \- Role-based dentist view

# 

# \---

# 

# \## Academic Project

# 

# This project was developed as part of the \*\*CIS6003 Advanced Programming\*\* module.

# 

# The application demonstrates:

# \- Object-oriented programming

# \- Three-tier architecture

# \- MVC

# \- Repository pattern

# \- Service layer pattern

# \- Relational database integration

# \- Authentication and role-based authorization

# \- Web services

# \- Automated testing

# \- Test-Driven Development practices

# \- Git version control

# \- Continuous Integration

