# 🛠️ Handyman Marketplace  
A full‑stack Java application with MySQL, JavaFX UI, multithreading, and a console interface.

This project implements a complete service marketplace where customers can book services, handymen can manage their bookings and publish services, and admins can oversee the platform.  
It includes:

- JavaFX GUI  
- Console interface  
- MySQL database  
- DAO architecture  
- Booking system  
- Reviews & ratings  
- Service categories  
- Dark mode  
- Background reminder thread  
- Client–server socket communication  

---

## 🚀 Features

### 👤 **User Roles**
- **Customer**
  - Search services by category & city  
  - View service details  
  - Make bookings  
  - Leave reviews for completed bookings  
  - Manage their bookings  

- **Handyman**
  - Create services (title, description, price, category, city)  
  - Toggle service active/inactive  
  - View assigned bookings  
  - Accept / complete / cancel / reschedule bookings  
  - View rating breakdown  

- **Admin**
  - View all users  
  - View all services  

---

## 🖥️ JavaFX UI

The GUI includes:

- Login / Register  
- Customer Dashboard  
- Handyman Dashboard  
- Services Browser  
- Bookings Page  
- Dark Mode  
- Smooth transitions & animations  
- Responsive layouts  

FXML files are located in:

src/view/


Controllers are located in:

src/controller/


---

## 🗄️ Database (MySQL)

### 📌 Required Tables

```sql
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


src/
 ├── model/               # Entities (User, Service, Booking, Review)
 ├── repository/          # DAO classes (MySQL)
 ├── controller/          # JavaFX controllers
 ├── view/                # FXML + CSS
 ├── app/                 # MainFX, SceneManager, Server, Client
 ├── test/                # JUnit tests
