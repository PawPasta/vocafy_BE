# 📘 Vocabulary Learning System (EN – JP – VI)

## Overview
A simple **Vocabulary Learning System** that helps users learn **English – Japanese – Vietnamese** vocabulary using **flashcards**, **daily learning**, and **spaced repetition**.

The backend is built with **Kotlin + Spring Boot**, focusing on clean architecture and easy extension (AI, payment, tracking).

---

## 🛠 Tech Stack
- **Backend:** Kotlin, Spring Boot
- **Database:** MySQL
- **API:** RESTful APIs
- **Build Tool:** Gradle
- **Other Tools:** Docker (optional)

---

## ✅ Prerequisites
Make sure you have the following installed:
- JDK 17 or later
- Gradle 7.x or later
- MySQL
- Docker (optional)
- IntelliJ IDEA (recommended)

---

## ⚙️ Installation

### 1. Clone the repository
```bash
git clone <your-repository-url>
```

### 2. Set up the database
Create a MySQL database:
```sql
CREATE DATABASE IF NOT EXISTS vocafy
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vocafy
spring.datasource.username=root
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build the project
```bash
./gradlew build
```

### 4. Run the application
```bash
./gradlew bootRun
```

---

## 🌐 Access the API
- Application runs at: **http://localhost:8080**
- Test APIs using **Postman** or **cURL**

---

## 📁 Project Structure
```
vocafy-backend/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── controller/    # REST controllers
│   │   │   ├── service/       # Business logic
│   │   │   ├── repository/    # Data access layer
│   │   │   ├── model/         # Entities / domain models
│   │   │   ├── dto/           # Request / Response DTOs
│   │   └── resources/
│   │       └── application.properties
├── build.gradle
├── README.md
└── docker-compose.yml       # Optional
```

---

## 📚 Core Features
- Syllabus → Course → Vocabulary structure
- Flashcard learning
- User self-evaluation (Forgot / Hard / Remember / Easy)
- Spaced repetition review
- Daily learning & streak tracking

---

## 🔌 API Endpoints
> Pending (to be updated)

---

## 📄 License
For educational and personal use.
