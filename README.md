# quan-ly-khach-san
# 🏨 Hotel Management System (Java Swing + Hibernate)

## 📌 Overview
Hotel Management System is a desktop application built with Java Swing to streamline hotel operations such as room management, booking, customer handling, and billing.  
The system follows the MVC architecture and uses Hibernate (JPA) for efficient and maintainable database interaction.

---

## 🎯 Objectives
- Manage hotel rooms, customers, and reservations
- Handle booking and check-in/check-out processes
- Generate invoices and track payments
- Provide a simple and intuitive user interface for staff

---

## 🛠️ Technologies Used
- Java (Core Java, OOP)
- Java Swing (UI)
- Hibernate ORM (JPA)
- MySQL (Database)
- Maven (Dependency Management)

---

## 🧩 System Architecture
The project follows a layered architecture:

- **View (Swing UI):** Handles user interaction  
- **Controller:** Processes events and coordinates between layers  
- **Service:** Contains business logic  
- **DAO:** Handles database operations using Hibernate  
- **Entity:** Maps Java objects to database tables  

---

## ⚙️ Core Features
- 🏠 Room Management (CRUD rooms, room types, availability)
- 👤 Customer Management (add, update, search customers)
- 📅 Booking Management (create, update, cancel reservations)
- 🔑 Check-in / Check-out
- 💳 Billing & Invoice Management
- 📊 Basic reporting (optional)

---

## 🗄️ Database Design
Main entities:
- Room
- RoomType
- Customer
- Booking
- Invoice
- Payment

Relationships:
- One-to-Many (RoomType → Room)
- One-to-Many (Customer → Booking)
- One-to-Many (Booking → Invoice)

