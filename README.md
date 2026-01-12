# 🛠️ Handyman Marketplace (Industrial Edition)

[cite_start]A professional, full‑stack Java application featuring a **Refined Industrial** UI, MySQL integration, multithreading, and a robust client-server architecture[cite: 1538, 1539].

[cite_start]This project implements a comprehensive service marketplace where customers can discover professionals, handymen manage business operations, and administrators maintain platform integrity[cite: 1539].

## 🚀 Key Features

### 👤 User Roles
* **Customer**:
    * [cite_start]Search services by category and city with real-time filtering[cite: 1540, 1651].
    * [cite_start]Dynamic booking system with address and date-time selection[cite: 1540, 1724].
    * [cite_start]Leave reviews for completed services that automatically update handyman ratings[cite: 1540, 1738].
    * [cite_start]Manage and view personal booking history[cite: 1540, 1727].
* **Handyman**:
    * [cite_start]Professional Dashboard with average rating and star breakdown (1★ to 5★)[cite: 1540, 1948].
    * [cite_start]Create and publish services with titles, descriptions, and pricing[cite: 1540, 1749].
    * [cite_start]Accept, complete, cancel, or reschedule customer bookings[cite: 1540, 1744].
    * [cite_start]Toggle service visibility (Active/Inactive)[cite: 1540, 1762].
* **Admin**:
    * [cite_start]Platform-wide oversight of all registered users[cite: 1540, 1766].
    * [cite_start]View all services currently listed on the marketplace[cite: 1540, 1768].

### 🎨 Modern Professional UI
* [cite_start]**Industrial Gold Theme**: A high-contrast palette featuring Slate Navy and Safety Gold for an enterprise-grade aesthetic[cite: 2237].
* [cite_start]**Professional Dark Mode**: Deep midnight backgrounds with optimized white text visibility across all dashboards and data tables[cite: 2246, 2251].
* [cite_start]**Smooth Animations**: Fluid scene switching using combined Fade and Scale transitions for a modern desktop feel[cite: 1879].

### ⚙️ Technical Highlights
* [cite_start]**Database Sync**: Includes a `DatabaseSyncService` to bridge data between in-memory structures and MySQL[cite: 1618].
* [cite_start]**Concurrency**: A background `BookingReminderThread` monitors upcoming appointments and alerts the system 30 minutes prior to the start[cite: 1580, 1583].
* [cite_start]**Socket Communication**: Multi-threaded `ClientHandler` and `MarketplaceServer` for remote command-line interactions[cite: 1590, 1671].
* [cite_start]**DAO Architecture**: Implementation of the Data Access Object pattern for clean separation of database logic[cite: 1539, 2056].

---

## 🖥️ Project Structure

```text
src/
 ├── app/               # MainFX, SceneManager, and Multithreaded Server/Client
 ├── controller/        # JavaFX Controllers (Login, Dashboards, Services, Bookings)
 ├── model/             # Core Entities (User, Service, Booking, Review, Message)
 ├── repository/        # MySQL DAO classes (UserDAO, ServiceDAO, BookingDAO, ReviewDAO)
 ├── util/              # Database connectors and validation utilities
 └── view/              # FXML layouts and Professional Industrial CSS
test/                   # JUnit 5 test suites for models and repositories

🗄️ Database Schema (MySQL)
Configure your MySQL instance with a database named handyman_marketplace.

SQL

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    full_name VARCHAR(100),
    email VARCHAR(100),
    role VARCHAR(20),
    rating DOUBLE DEFAULT 0
);

CREATE TABLE services (
    id BIGINT PRIMARY KEY,
    handyman_id BIGINT,
    title VARCHAR(100),
    description TEXT,
    price DOUBLE,
    category VARCHAR(50),
    city VARCHAR(50),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT,
    service_id BIGINT,
    scheduled_datetime DATETIME,
    status VARCHAR(20),
    address VARCHAR(255),
    total_price DOUBLE
);

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY,
    booking_id BIGINT,
    rating INT,
    comment TEXT
);

Here is the full content for your README.md file in Markdown format, updated to reflect your current project state, including the professional Industrial Gold theme, MySQL integration, and full visibility fixes for Dark Mode.

Markdown

# 🛠️ Handyman Marketplace (Industrial Edition)

[cite_start]A professional, full‑stack Java application featuring a **Refined Industrial** UI, MySQL integration, multithreading, and a robust client-server architecture[cite: 1538, 1539].

[cite_start]This project implements a comprehensive service marketplace where customers can discover professionals, handymen manage business operations, and administrators maintain platform integrity[cite: 1539].

## 🚀 Key Features

### 👤 User Roles
* **Customer**:
    * [cite_start]Search services by category and city with real-time filtering[cite: 1540, 1651].
    * [cite_start]Dynamic booking system with address and date-time selection[cite: 1540, 1724].
    * [cite_start]Leave reviews for completed services that automatically update handyman ratings[cite: 1540, 1738].
    * [cite_start]Manage and view personal booking history[cite: 1540, 1727].
* **Handyman**:
    * [cite_start]Professional Dashboard with average rating and star breakdown (1★ to 5★)[cite: 1540, 1948].
    * [cite_start]Create and publish services with titles, descriptions, and pricing[cite: 1540, 1749].
    * [cite_start]Accept, complete, cancel, or reschedule customer bookings[cite: 1540, 1744].
    * [cite_start]Toggle service visibility (Active/Inactive)[cite: 1540, 1762].
* **Admin**:
    * [cite_start]Platform-wide oversight of all registered users[cite: 1540, 1766].
    * [cite_start]View all services currently listed on the marketplace[cite: 1540, 1768].

### 🎨 Modern Professional UI
* [cite_start]**Industrial Gold Theme**: A high-contrast palette featuring Slate Navy and Safety Gold for an enterprise-grade aesthetic[cite: 2237].
* [cite_start]**Professional Dark Mode**: Deep midnight backgrounds with optimized white text visibility across all dashboards and data tables[cite: 2246, 2251].
* [cite_start]**Smooth Animations**: Fluid scene switching using combined Fade and Scale transitions for a modern desktop feel[cite: 1879].

### ⚙️ Technical Highlights
* [cite_start]**Database Sync**: Includes a `DatabaseSyncService` to bridge data between in-memory structures and MySQL[cite: 1618].
* [cite_start]**Concurrency**: A background `BookingReminderThread` monitors upcoming appointments and alerts the system 30 minutes prior to the start[cite: 1580, 1583].
* [cite_start]**Socket Communication**: Multi-threaded `ClientHandler` and `MarketplaceServer` for remote command-line interactions[cite: 1590, 1671].
* [cite_start]**DAO Architecture**: Implementation of the Data Access Object pattern for clean separation of database logic[cite: 1539, 2056].

---

## 🖥️ Project Structure

```text
src/
 ├── app/               # MainFX, SceneManager, and Multithreaded Server/Client
 ├── controller/        # JavaFX Controllers (Login, Dashboards, Services, Bookings)
 ├── model/             # Core Entities (User, Service, Booking, Review, Message)
 ├── repository/        # MySQL DAO classes (UserDAO, ServiceDAO, BookingDAO, ReviewDAO)
 ├── util/              # Database connectors and validation utilities
 └── view/              # FXML layouts and Professional Industrial CSS
test/                   # JUnit 5 test suites for models and repositories
🗄️ Database Schema (MySQL)
Configure your MySQL instance with a database named handyman_marketplace.

SQL

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    full_name VARCHAR(100),
    email VARCHAR(100),
    role VARCHAR(20),
    rating DOUBLE DEFAULT 0
);

CREATE TABLE services (
    id BIGINT PRIMARY KEY,
    handyman_id BIGINT,
    title VARCHAR(100),
    description TEXT,
    price DOUBLE,
    category VARCHAR(50),
    city VARCHAR(50),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT,
    service_id BIGINT,
    scheduled_datetime DATETIME,
    status VARCHAR(20),
    address VARCHAR(255),
    total_price DOUBLE
);

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY,
    booking_id BIGINT,
    rating INT,
    comment TEXT
);

🛠️ Setup & Installation

Database: Import the SQL schema above. Update DatabaseConnector.java with your local MySQL credentials.


Dependencies: Managed via Maven. Key dependencies include mysql-connector-java, javafx-controls, and junit-jupiter.


Run: Launch MainFX.java for the GUI or Main.java for the full MySQL console version.
