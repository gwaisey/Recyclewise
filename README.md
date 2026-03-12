# ♻ RecycleWise — Recycling & Waste Guide

A full-stack web application built with **Spring Boot + Thymeleaf** that helps Jakarta citizens correctly dispose of household waste, earn points by submitting trash to official stations, and redeem those points for real government-backed rewards.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Template Engine | Thymeleaf |
| Database | H2 (file-based, persistent across restarts) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Frontend | HTML5, Inline CSS, Vanilla JS |
| Fonts | Syne + DM Sans (Google Fonts) |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the App
```bash
cd Recyclewise
mvn spring-boot:run
```

Then open: **http://localhost:8080**

### H2 Console (Dev)
Access the persistent file-based database at: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:file:./data/recyclewise`
- Username: `sa` | Password: *(empty)*

> ⚠ To reset all data, delete the `data/` folder and restart the app.

---

## 👤 Default Accounts

### Regular Users
Register at `/register` with any email and password.

### Admin Accounts (Pre-seeded)

| Role | Email | Password | Access |
|---|---|---|---|
| Super Admin | `superadmin@recyclewise.id` | `Admin@2026` | All stations, staff management |
| Station Staff | `budi@recyclewise.id` | `Staff@2026` | Central Park Station only |
| Station Staff | `siti@recyclewise.id` | `Staff@2026` | Kebon Jeruk Eco Hub only |
| Station Staff | `ahmad@recyclewise.id` | `Staff@2026` | Kemang Green Point only |
| Station Staff | `dewi@recyclewise.id` | `Staff@2026` | Kelapa Gading RecycleHub only |
| Station Staff | `reza@recyclewise.id` | `Staff@2026` | Cibubur Waste Center only |
| Station Staff | `nur@recyclewise.id` | `Staff@2026` | Menteng Eco Station only |

> Admin portal is accessible at `/admin/login` — separate from the user login.

---

## 📄 Pages

### Public Pages
| Route | Description |
|---|---|
| `/` | Home — hero, category overview, top eco tips |
| `/guide` | Waste Guide — search & filter all items by category |
| `/guide/{id}` | Item Detail — full disposal instructions |
| `/tips` | Eco Tips — all tips filtered by 3R category |
| `/login` | User login |
| `/register` | User registration |

### Authenticated User Pages
| Route | Description |
|---|---|
| `/dashboard` | Personal dashboard — points, submission history, vouchers |
| `/stations` | Browse 6 trash stations across Jakarta |
| `/stations/{id}/submit` | Submit trash at a station and earn points |
| `/rewards` | Browse redeemable rewards by category |
| `/rewards/{id}/redeem` | Redeem points for a voucher |
| `/rewards/{id}/mark-used` | Mark a voucher as used after redemption |

### Admin Pages
| Route | Description | Access |
|---|---|---|
| `/admin/login` | Admin portal login | All staff |
| `/admin/dashboard` | Stats overview + staff management | Super Admin only |
| `/admin/submissions` | Review, confirm, or reject submissions | All staff (filtered by station) |

---

## 🌱 Features

### For Users
- **Search** any waste item by name
- **Filter** by category: Recyclable, Organic, Hazardous, General Waste
- **Detailed disposal instructions** for 15+ common household items
- **8 eco tips** ranked by environmental impact score
- **Submit trash** at 6 official Jakarta stations to earn points
- **Points system** — earn based on waste category and weight:
  - ♻ Recyclable: 15 pts/kg
  - 🌿 Organic: 10 pts/kg
  - ⚠ Hazardous: 20 pts/kg
  - 🗑 General: 5 pts/kg
- **Rewards redemption** — redeem points for:
  - 🍱 Food & Drinks (coffee, snacks, lunch boxes)
  - 🚌 Public Transport (bus pass, MRT credit, weekly pass)
  - 💡 Bill Discounts (water, electricity, bundle)
- **Voucher system** — unique codes with 30-day expiry, expiry countdown, mark-as-used flow
- **Responsive design** — works on mobile, tablet, desktop

### For Admins
- **Role-based access** — Super Admin vs Station Staff
- **Super Admin** can:
  - View all submissions across all stations
  - Create and deactivate staff accounts
  - Assign staff to specific stations
  - View system-wide stats (pending, confirmed, rejected)
- **Station Staff** can:
  - View only their assigned station's submissions
  - Confirm submissions → points awarded to user
  - Reject submissions → no points awarded
- Points are **only awarded on admin confirmation** — not on submission

---

## 📦 Project Structure

```
Recyclewise/
├── src/main/java/com/recyclewise/
│   ├── RecycleWiseApplication.java
│   ├── controller/
│   │   ├── HomeController.java
│   │   ├── GuideController.java
│   │   ├── TipsController.java
│   │   ├── AuthController.java        ← register/login/logout
│   │   ├── DashboardController.java   ← /dashboard
│   │   ├── StationController.java     ← /stations
│   │   ├── RewardController.java      ← /rewards
│   │   └── AdminController.java       ← /admin (role-based)
│   ├── model/
│   │   ├── WasteItem.java
│   │   ├── RecyclingTip.java
│   │   ├── User.java
│   │   ├── TrashStation.java
│   │   ├── TrashSubmission.java
│   │   ├── SubmissionItem.java
│   │   ├── Reward.java
│   │   ├── Redemption.java            ← includes expiresAt, ACTIVE/USED/EXPIRED
│   │   └── AdminUser.java             ← SUPER_ADMIN / STATION_STAFF roles
│   ├── repository/
│   │   ├── WasteItemRepository.java
│   │   ├── RecyclingTipRepository.java
│   │   ├── UserRepository.java
│   │   ├── TrashStationRepository.java
│   │   ├── TrashSubmissionRepository.java
│   │   ├── SubmissionItemRepository.java
│   │   ├── RewardRepository.java
│   │   ├── RedemptionRepository.java
│   │   └── AdminUserRepository.java
│   ├── service/
│   │   ├── WasteItemService.java
│   │   ├── TipService.java
│   │   ├── UserService.java
│   │   ├── StationService.java
│   │   ├── SubmissionService.java
│   │   ├── RewardService.java
│   │   ├── PointsCalculator.java
│   │   └── impl/
│   │       ├── WasteItemServiceImpl.java
│   │       ├── TipServiceImpl.java
│   │       ├── UserServiceImpl.java
│   │       ├── StationServiceImpl.java
│   │       ├── SubmissionServiceImpl.java
│   │       ├── RewardServiceImpl.java
│   │       └── DataSeeder.java        ← auto-seeds all data on startup
│   └── exception/
│       ├── ResourceNotFoundException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── templates/
│   │   ├── fragments/
│   │   │   ├── header.html            ← sticky navbar, session-aware
│   │   │   └── footer.html
│   │   └── pages/
│   │       ├── home.html
│   │       ├── guide.html
│   │       ├── item-detail.html
│   │       ├── tips.html
│   │       ├── error.html
│   │       ├── login.html
│   │       ├── register.html
│   │       ├── dashboard.html
│   │       ├── stations.html
│   │       ├── submit-trash.html
│   │       ├── rewards.html
│   │       ├── admin-login.html
│   │       ├── admin-dashboard.html   ← Super Admin only
│   │       └── admin-submissions.html ← filtered by role
│   ├── static/
│   │   ├── css/main.css
│   │   └── js/main.js
│   └── application.properties
└── pom.xml
```

---

## 🏗 Architecture & Design Principles

This project follows **SOLID principles** throughout:

| Principle | Application |
|---|---|
| **S** — Single Responsibility | Each service class handles one domain only (e.g. `RewardService` only manages rewards) |
| **O** — Open/Closed | Services are extended via interfaces, not modified directly |
| **L** — Liskov Substitution | All `ServiceImpl` classes are substitutable for their interfaces |
| **I** — Interface Segregation | Separate interfaces per domain (`UserService`, `StationService`, etc.) |
| **D** — Dependency Inversion | Controllers depend on service interfaces, not implementations |

---

## 🗃 Seeded Data

On first startup, the app auto-seeds:

| Data | Count |
|---|---|
| Waste Items | 15 |
| Eco Tips | 8 |
| Trash Stations | 6 (across Jakarta districts) |
| Rewards | 9 (Food, Transport, Bills) |
| Admin Users | 7 (1 Super Admin + 6 Station Staff) |

---

## 🔮 Production Setup

For production, swap H2 for PostgreSQL or MySQL:

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recyclewise
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

Add the PostgreSQL dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

> For production, replace the plain-text password storage in `AdminUser` and `User` with **BCrypt hashing** via Spring Security.

---

## 🧪 Testing the Admin System

### Test Super Admin
1. Go to `/admin/login`
2. Login with `superadmin@recyclewise.id` / `Admin@2026`
3. You should land on `/admin/dashboard` with:
   - Stats cards showing pending, confirmed, and rejected submission counts
   - A staff accounts table listing all 6 station staff members
4. Try adding a new staff member by filling in the form at the bottom — assign them to any station
5. The new account will be created with default password `Staff@2026`
6. Try deactivating a staff account — they will no longer be able to log in

### Test Station Staff
1. Logout from Super Admin (`/admin/logout`)
2. Login with `budi@recyclewise.id` / `Staff@2026`
3. You should land directly on `/admin/submissions` — **not** the dashboard
4. You should only see submissions from **Central Park Station** — other stations are hidden
5. Click **Confirm** on a pending submission:
   - Status changes to `CONFIRMED`
   - Points are awarded to the user immediately
   - Check the user's `/dashboard` — their total points should increase
6. Click **Reject** on a pending submission:
   - Status changes to `REJECTED`
   - No points are awarded

### Test Points Flow (End to End)
1. Register a new user account at `/register`
2. Go to `/stations` and click **Submit Trash Here** on any station
3. Enter some weights and submit — status will show `PENDING`, points stay at 0
4. Log into admin as the station's assigned staff
5. Confirm the submission — points are now added to the user's account
6. Log back in as the user — points appear on `/dashboard`
7. Go to `/rewards` — redeem a reward if you have enough points
8. Voucher appears in **My Vouchers** on dashboard with a 30-day expiry countdown
9. Click **Mark as Used** after redeeming at the partner location

---

## 📝 License

A full-stack web application with a Java Spring Boot backend, Thymeleaf server-side rendering, and H2 database. Frontend styling uses inline CSS due to static resource serving constraints in the development environment.
