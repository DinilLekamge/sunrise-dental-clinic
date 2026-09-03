# Sunrise Dental Clinic - Appointment and Patient Management System

A three-tier Spring Boot web application for CIS6003 Advanced Programming.

- Presentation layer: Spring MVC controllers, Thymeleaf templates, Bootstrap 5
- Business layer: service classes holding all business rules and validation
- Data layer: JPA entities and Spring Data repositories on MySQL 8
- Web services: read-only REST endpoints under `/api`

## Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 21 |
| Maven | 3.9 or later (or the Maven bundled with IntelliJ IDEA) |
| MySQL | 8.x, running on `localhost:3306` |
| IDE | IntelliJ IDEA (Community edition is enough) |

The database `sunrise_dental_clinic` with the six tables must exist before the
first run. `docs/schema.sql` contains a reference copy of the schema.

## Where to put your MySQL username and password

Open `src/main/resources/application.properties` and replace the two
placeholders:

```
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Nothing else needs to change for a standard local MySQL installation.

## Opening the project in IntelliJ IDEA

1. Start IntelliJ IDEA and choose **Open**.
2. Select the extracted `sunrise-dental-clinic` folder (the folder containing
   `pom.xml`) and choose **Open as Project**.
3. IntelliJ detects the Maven project and downloads the dependencies. Wait until
   the progress bar at the bottom finishes.
4. Go to **File > Project Structure > Project** and make sure the SDK is JDK 21.
5. Edit `application.properties` with your MySQL username and password.

## Running the application

From a terminal in the project folder:

```
mvn spring-boot:run
```

Or in IntelliJ, open `SunriseDentalClinicApplication` and press the green run
arrow.

Then open:

```
http://localhost:8080
```

You are redirected to the login page.

## Demonstration accounts

The accounts are created automatically the first time the application starts,
but only while the `users` table is empty. The passwords are read from
`application.properties` and are hashed with BCrypt before they are stored.

| Username   | Password        | Role |
|------------|-----------------|------|
| admin      | Admin@123       | ADMIN |
| reception  | Reception@123   | RECEPTIONIST |
| dentist    | Dentist@123     | DENTIST |

To use different passwords, change these lines in `application.properties`
**before the first run**:

```
app.demo.admin-password=Admin@123
app.demo.receptionist-password=Reception@123
app.demo.dentist-password=Dentist@123
```

If the accounts already exist, either delete the rows from the `users` table and
restart, or change the password from **Users > Edit** while signed in as admin.

Set `app.demo.data-enabled=false` to switch the initializer off completely.

## Running the tests

```
mvn clean test
```

The tests use JUnit 5, Mockito and Spring Boot Test. They do not need MySQL,
because the repositories are mocked.

## REST endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/patients` | All patients as JSON |
| GET | `/api/patients/{id}` | One patient |
| GET | `/api/appointments` | All appointments as JSON |
| GET | `/api/appointments/{appointmentNumber}` | One appointment by its number |

The endpoints need authentication. HTTP Basic is enabled, so from Postman or
curl you can use:

```
curl -u reception:Reception@123 http://localhost:8080/api/patients
```

Password hashes are never returned by the API.

## Business rules enforced in the service layer

1. Every appointment gets a generated unique appointment number (`APT-...`).
2. A dentist cannot have two appointments on the same date and time.
3. Appointments cannot be created for a date in the past.
4. Patient, dentist and treatment must all be selected.
5. Only active dentists and active treatments can be selected.
6. Total amount = treatment fee + consultation fee.
7. Only one bill can exist for each appointment, and not for a cancelled one.

## Documented assumptions

- The `users` table has no foreign key to the `dentists` table, so a dentist
  account is not linked to a specific dentist record. A user with the DENTIST
  role therefore sees the appointment list in read-only mode and uses the
  dentist filter to see one dentist's schedule.
- The "Exit system" requirement of the scenario is met by the logout function,
  which ends the HTTP session.
- Appointment records are never deleted. Cancelling sets the status to
  CANCELLED so the history is kept for reporting.

## Project structure

```
sunrise-dental-clinic
 ├── pom.xml
 ├── docs/schema.sql
 └── src
     ├── main
     │   ├── java/com/sunrise/clinic
     │   │   ├── SunriseDentalClinicApplication.java
     │   │   ├── config/       SecurityConfig, DataInitializer
     │   │   ├── controller/   MVC controllers + GlobalExceptionHandler
     │   │   ├── rest/         REST controllers + RestExceptionHandler
     │   │   ├── service/      business layer
     │   │   ├── repository/   Spring Data JPA repositories
     │   │   ├── entity/       JPA entities mapped to the existing tables
     │   │   ├── dto/          form objects and JSON records
     │   │   └── exception/    ResourceNotFoundException, BusinessRuleException
     │   └── resources
     │       ├── application.properties
     │       ├── static/css/app.css
     │       └── templates/    Thymeleaf pages
     └── test/java/com/sunrise/clinic
         ├── service/          PatientServiceTest, AppointmentServiceTest, BillServiceTest
         └── controller/       PatientAccessControlTest
```
