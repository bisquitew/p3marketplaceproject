# 🛠️ Handymen Marketplace

> A full-stack Java service marketplace platform with JavaFX GUI, MySQL database, and real-time booking system.

[![Java](https://img.shields.io/badge/Java-11%2B-orange?style=flat-square&logo=java)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![JavaFX](https://img.shields.io/badge/JavaFX-GUI-brightgreen?style=flat-square)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📋 Overview

Handymen Marketplace is a comprehensive Java-based platform that connects customers with skilled handymen. The application features a modern JavaFX interface, a robust backend architecture, and a complete booking management system. Built with clean design patterns and scalable architecture, it demonstrates enterprise-level Java development practices.

---

## ✨ Key Features

### 🎯 Core Functionality

- **Service Marketplace**: Browse and discover services by category and location
- **Booking System**: Real-time booking creation, tracking, and management
- **Review & Rating System**: Leave feedback and view service quality ratings
- **Dark Mode**: Eye-friendly dark theme for extended usage
- **Background Notifications**: Automated reminder system for upcoming bookings
- **Multi-threaded Architecture**: Concurrent request handling for optimal performance

### 👥 User Roles & Capabilities

#### Customer
- 🔍 Search services by category and city
- 📋 View detailed service information
- 📅 Make and manage bookings
- ⭐ Leave reviews and ratings for completed services
- 🕐 Track booking status in real-time
- 💬 View handyman ratings and reviews

#### Handyman
- ➕ Create and manage services (title, description, pricing, category, location)
- ⚙️ Toggle services active/inactive
- 📊 View assigned bookings and customer details
- ✅ Accept, complete, reschedule, or cancel bookings
- ⭐ View rating breakdown and feedback
- 🎯 Manage service categories and pricing

#### Admin
- 👤 Monitor all users and their activities
- 🛠️ View all services across the platform
- 📊 Platform oversight and management

---

## 🏗️ Architecture

### Technology Stack

| Layer | Technology |
|---|---|
| **UI** | JavaFX with FXML & CSS |
| **Backend** | Java (Core + Multithreading) |
| **Database** | MySQL 8.0+ |
| **Design Patterns** | DAO, MVC, Singleton |
| **Communication** | Socket Programming (Client-Server) |

### Project Structure

```text
src/
├── model/                     # Domain entities
│   ├── User.java
│   ├── Service.java
│   ├── Booking.java
│   └── Review.java
├── repository/                # Data Access Objects (DAO)
│   ├── UserRepository.java
│   ├── ServiceRepository.java
│   ├── BookingRepository.java
│   └── ReviewRepository.java
├── controller/                # JavaFX Controllers (MVC)
│   ├── LoginController.java
│   ├── CustomerDashboardController.java
│   ├── HandymanDashboardController.java
│   ├── ServiceBrowserController.java
│   └── BookingsController.java
├── view/                       # FXML UI Layouts
│   ├── login.fxml
│   ├── customer-dashboard.fxml
│   ├── handyman-dashboard.fxml
│   ├── services-browser.fxml
│   ├── bookings.fxml
│   └── styles.css
├── app/                        # Application Core
│   ├── MainFX.java             # Entry point
│   ├── SceneManager.java       # Scene navigation
│   ├── Server.java             # Server implementation
│   ├── Client.java             # Client implementation
│   └── ReminderThread.java     # Background notifications
└── test/                       # Unit Tests (JUnit)
```

---

## 🗄️ Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'HANDYMAN', 'ADMIN')),
    rating DOUBLE DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Services Table

```sql
CREATE TABLE services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    handyman_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (handyman_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Bookings Table

```sql
CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    scheduled_datetime DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED')),
    address VARCHAR(255) NOT NULL,
    total_price DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);
```

### Reviews Table

```sql
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL UNIQUE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);
```

---

## 🖥️ User Interface

### JavaFX Components

- **Responsive Layouts**: Adapts to different screen sizes
- **Smooth Animations**: Professional transitions between scenes
- **Dark Theme**: Built-in dark mode support
- **Real-time Updates**: Live booking status and notifications

### Main Screens

| Screen | Description |
|---|---|
| Login / Register | User authentication and account creation |
| Customer Dashboard | Browse services, manage bookings, leave reviews |
| Handyman Dashboard | Manage services, accept/complete bookings, view ratings |
| Services Browser | Search and filter services by category/location |
| Bookings Page | View and manage all booking statuses |
| Admin Panel | Monitor users and services |

---

## ⚙️ Installation & Setup

### Prerequisites

- Java 11+ installed
- MySQL 8.0+ database server
- JavaFX SDK configured in your IDE

### Step 1: Clone Repository

```bash
git clone https://github.com/bisquitew/Handymen-Marketplace.git
cd Handymen-Marketplace
```

### Step 2: Database Setup

```bash
mysql -u root -p
```

```sql
CREATE DATABASE handymen_marketplace;
USE handymen_marketplace;
-- Run the SQL scripts from the database schema section above
```

### Step 3: Configure Database Connection

Update database credentials in the repository configuration file:

```java
// Example: src/app/DatabaseConfig.java
DATABASE_URL = "jdbc:mysql://localhost:3306/handymen_marketplace";
DATABASE_USER = "root";
DATABASE_PASSWORD = "your_password";
```

### Step 4: Build & Run

```bash
# Using Maven
mvn clean javafx:run

# Or using your IDE's run configuration
```

---

## 📄 License

This project is licensed under the MIT License.
