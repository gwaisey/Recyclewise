# ♻ RecycleWise — Recycling & Waste Guide

A full-stack web application built with **Spring Boot + Thymeleaf** that helps users find out how to correctly dispose of any household item.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Template Engine | Thymeleaf |
| Database | H2 (in-memory, dev) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Frontend | HTML5, CSS3, Vanilla JS |
| Fonts | Syne + DM Sans (Google Fonts) |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the App

```bash
cd recyclewise
mvn spring-boot:run
```

Then open: **http://localhost:8080**

### H2 Console (Dev)
Access the in-memory database at: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:recyclewise`
- Username: `sa` | Password: *(empty)*

---

## 📄 Pages

| Route | Description |
|---|---|
| `/` | Home — hero, category overview, top eco tips |
| `/guide` | Waste Guide — search & filter all items by category |
| `/guide/{id}` | Item Detail — full disposal instructions |
| `/tips` | Eco Tips — all tips filtered by 3R category |

---

## 🌱 Features

- **Search** any waste item by name
- **Filter** by category: Recyclable, Organic, Hazardous, General Waste
- **Detailed disposal instructions** for 15+ common items
- **8 eco tips** ranked by environmental impact score
- **Responsive design** — works on mobile, tablet, desktop
- Pre-seeded database (auto-loads on startup)

---

## 📦 Project Structure

```
recyclewise/
├── src/main/java/com/recyclewise/
│   ├── RecycleWiseApplication.java
│   ├── controller/
│   │   ├── HomeController.java
│   │   ├── GuideController.java
│   │   └── TipsController.java
│   ├── model/
│   │   ├── WasteItem.java
│   │   └── RecyclingTip.java
│   └── service/
│       ├── WasteService.java
│       ├── WasteItemRepository.java
│       ├── RecyclingTipRepository.java
│       └── DataSeeder.java
├── src/main/resources/
│   ├── templates/
│   │   ├── fragments/
│   │   │   ├── header.html
│   │   │   └── footer.html
│   │   └── pages/
│   │       ├── home.html
│   │       ├── guide.html
│   │       ├── item-detail.html
│   │       └── tips.html
│   ├── static/
│   │   ├── css/main.css
│   │   └── js/main.js
│   └── application.properties
└── pom.xml
```

---

## 🔮 Extending the App

For production, swap H2 for PostgreSQL or MySQL:

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recyclewise
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Add the PostgreSQL dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```
