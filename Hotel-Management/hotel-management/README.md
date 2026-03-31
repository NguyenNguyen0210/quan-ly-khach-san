# Hotel Management System

Desktop application for hotel management built with Java Swing, Hibernate ORM, Maven, and MySQL.

The project supports the full core flow of a small hotel management system:
- manage room types and rooms
- manage customers, employees, and services
- create bookings
- check in guests
- add service usage to active bookings
- create invoices
- process payments
- check out guests
- view invoice history and reports

## Main Features

### 1. Authentication and Role-Based Access
- Employee login with username and password
- Role-based navigation and permissions
- Supported roles:
  - `MANAGER`
  - `RECEPTIONIST`
  - `SERVICE_STAFF`

### 2. Room and Room Type Management
- Create, update, and delete room types
- Create, update, and delete rooms
- Display room status visually on the Home dashboard

### 3. Booking Management
- Create bookings for available rooms
- Auto-suggest existing customers by name or phone
- Auto-create a new customer if the customer does not already exist
- Support booking lifecycle:
  - `BOOKED`
  - `CHECKED_IN`
  - `CHECKED_OUT`

### 4. Service Management
- Create, update, and delete service catalog items
- Add services to checked-in bookings
- Track service quantity in stock

### 5. Invoice and Payment
- Create invoice for checked-in bookings
- Generate invoice details for room charge and service usage
- Support multiple payments
- Invoice status flow:
  - `ISSUED`
  - `PARTIALLY_PAID`
  - `PAID`
  - `CLOSED`

### 6. Reporting
- Invoice history tab
- Filter invoice report by date
- View invoice details and processed-by employee

## Technology Stack

- Java 21
- Java Swing
- Hibernate ORM 6
- Jakarta Persistence API
- MySQL 8
- Maven

## Project Structure

The project follows a layered architecture:

```text
src/main/java/com/hotel
├── controller   // UI-to-service coordination
├── dao          // database access
├── entity       // JPA entities
├── security     // login session and employee roles
├── service      // business logic
├── util         // Hibernate utility
├── view         // Swing UI screens
└── Main.java    // application entry point
```

## Database Configuration

Database config is loaded from:

- [src/main/resources/hibernate.cfg.xml](src/main/resources/hibernate.cfg.xml)

Default settings:

```xml
jdbc:mysql://localhost:3306/hotel_management
username: root
password: 123456
```

If your local MySQL account is different, update `hibernate.cfg.xml` before running the app.

## Requirements

Before running the project, make sure you have:

- JDK 21
- Maven 3.9+
- MySQL 8+

## How to Run

### 1. Create the database

```sql
CREATE DATABASE hotel_management;
```

### 2. Run the app once so Hibernate creates/updates the tables

```bash
mvn exec:java
```

### 3. Seed demo data

You can use the SQL files in [database](database):

- [database/clear.sql](database/clear.sql): remove all current data
- [database/seed.sql](database/seed.sql): insert demo data
- [database/reset_all.sql](database/reset_all.sql): clear + seed

Recommended flow in MySQL Workbench:

1. Open [database/clear.sql](database/clear.sql) and run it
2. Open [database/seed.sql](database/seed.sql) and run it

If you use MySQL CLI, you can also run:

```sql
SOURCE database/reset_all.sql;
```

### 4. Start the application

```bash
mvn exec:java
```

## Demo Accounts

After running the seed script, you can log in using:

### Manager
- Username: `admin`
- Password: `admin123`

### Receptionist
- Username: `reception1`
- Password: `123456`

### Service Staff
- Username: `service1`
- Password: `123456`

## Business Flow

Typical usage flow:

1. Log in as `MANAGER`
2. Review or manage `Rooms`, `Services`, and `Employees`
3. Log in as `RECEPTIONIST`
4. Create a booking in the `Book` tab
5. On the `Home` screen, right-click a booked room and choose `Check-in`
6. Log in as `SERVICE_STAFF`
7. Add service usage to checked-in bookings
8. Log in as `RECEPTIONIST`
9. Open `Check Out`, create invoice, process payment, and complete checkout
10. Log in as `MANAGER` to review invoice history in `Invoices`

## Seeded Demo Scenario

The seed file provides:

- rooms in `AVAILABLE`, `BOOKED`, and `OCCUPIED` states
- customers and employees
- services with stock quantity
- bookings in different lifecycle stages
- invoices with:
  - issued
  - partially paid
  - closed

This allows quick testing without entering all data manually.

## Role Permissions

### Manager
- Home
- Book
- Rooms
- Service
- Customer
- Employee
- Invoices
- Checkout
- Add Service

### Receptionist
- Book
- Customer
- Checkout
- Check-in from Home

### Service Staff
- Service
- Add Service

Note:
- `SERVICE_STAFF` is redirected to the `Service` tab after login
- `SERVICE_STAFF` cannot access `Home`

## Important Business Rules

- A booking must be `CHECKED_IN` before services can be added
- Invoice is created only for checked-in bookings
- Once invoice payment has started, service changes are restricted
- Checkout is allowed only when the invoice has been fully paid
- Invoice is closed when checkout is completed

## UI Notes

The application includes:

- validation status bar on key forms
- field highlighting for invalid input
- role-aware navigation
- customer suggestions in the booking form
- invoice detail dialog from the checkout screen

## SQL Utilities

The SQL helper files are located in:

- [database/clear.sql](database/clear.sql)
- [database/seed.sql](database/seed.sql)
- [database/reset_all.sql](database/reset_all.sql)

## Build Command

Compile the project:

```bash
mvn -q -DskipTests compile
```

Run the project:

```bash
mvn exec:java
```

## Notes for Submission

This project is intended as a course project / desktop management application.  
The current implementation focuses on a complete academic demo flow rather than production-grade deployment.

Current strengths:
- complete end-to-end hotel workflow
- role-based login
- invoice and payment tracking
- invoice history/report screen
- demo seed data ready for presentation

Current limitations:
- no automated test suite yet
- passwords are stored in plain text for simplicity
- intended for local desktop use with a MySQL database

## Author

Student project for hotel management using Java Swing, Hibernate, Maven, and MySQL.
