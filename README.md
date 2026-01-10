# 🛠️ Handyman Marketplace

Handyman Marketplace is a professional, full-stack desktop application designed to bridge the gap between service providers (Handymen) and clients (Customers). Built with **JavaFX** and **MySQL**, it offers a sleek, modern interface with high-performance backend logic to manage local services, real-time bookings, and quality assurance through user reviews.

---

## 🌟 Key Features

### 👤 User Management
* [cite_start]**Dual-Role System**: Users can register as either a **Customer** or a **Handyman** with distinct interface experiences tailored to their needs[cite: 158, 1146].
* [cite_start]**Secure Authentication**: A robust login system verifies credentials against a MySQL database to ensure data security[cite: 499, 656].
* [cite_start]**Dynamic Rating System**: Handymen earn public ratings based on client feedback, which are automatically calculated and updated by the backend[cite: 211, 603].

### 🛠️ Service & Booking Engine
* [cite_start]**Smart Filtering**: Customers can search for services by specific categories (e.g., Plumbing, Electrical) or by city[cite: 184, 1184].
* [cite_start]**Real-Time Lifecycle**: Handymen can accept, reschedule, or complete bookings, with all status changes reflected instantly[cite: 452, 453, 580].
* [cite_start]**Automated Reminders**: A background daemon thread monitors upcoming appointments and alerts users for bookings starting within 30 minutes[cite: 45, 47].

### 🎨 Modern UI/UX
* [cite_start]**Glassmorphism Design**: A sleek, high-contrast interface featuring smooth gradients and "alive" transitions[cite: 759, 1165].
* [cite_start]**Full-Screen Optimization**: Designed for a high-resolution desktop experience with a split-screen landing page[cite: 355].
* [cite_start]**Dynamic Dark Mode**: A complete "Tech Night" theme that can be toggled instantly across all scenes[cite: 369, 550].

---

## 🏗️ Technical Architecture



### Frontend (JavaFX)
* [cite_start]**MVC Pattern**: Decoupled FXML views and Java controllers maintain a clean separation of concerns[cite: 1, 3, 5].
* [cite_start]**Scene Management**: A centralized `SceneManager` handles parallel transitions (fade and slide) for a premium feel[cite: 359, 1156].
* [cite_start]**Session Control**: Uses a global `Session` object to track the current user and their permissions across the app[cite: 553, 1319].

### Backend (Java)
* [cite_start]**DAO Pattern**: Clean separation of database logic using specialized Data Access Objects for Users, Services, Bookings, and Reviews[cite: 4, 825].
* [cite_start]**Multi-Threading**: Includes a `BookingReminderThread` for background monitoring and `ClientHandler` for potential server-client communication[cite: 44, 54].
* [cite_start]**Robust Validation**: Implements a `Validatable` interface and custom exceptions to ensure data integrity before persistence[cite: 12, 122, 350].

### Database (MySQL)
* [cite_start]**Relational Schema**: Manages complex relationships between users, services, and historical bookings[cite: 78, 899].
* [cite_start]**Advanced Queries**: Uses `JOIN` operations to link bookings to service providers and calculate average ratings[cite: 573, 599].

---

## 📂 Project Structure

```text
└── src/
    [cite_start]├── app/           # Main entry (MainFX) and Scene Manager [cite: 1, 822]
    ├── controller/    # JavaFX UI Logic and Session handling [cite: 3, 824]
    ├── model/         # Core entities and custom Exceptions [cite: 1, 122]
    ├── repository/    # MySQL Data Access Objects (DAOs) [cite: 4, 825]
    ├── util/          # DB Connector and Validation utilities [cite: 78, 104]
    └── view/          # FXML layouts and style.css [cite: 5, 826]
