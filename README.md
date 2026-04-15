# ♻ RecycleWise — Recycling & Waste Guide

A full-stack web application built with **Spring Boot + Thymeleaf** that helps Jakarta citizens correctly dispose of household waste, earn points by submitting trash to official stations, and redeem those points for real government-backed rewards.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17+, Spring Boot 3.2 |
| Template Engine | Thymeleaf |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + BCrypt |
| Build Tool | Maven |
| Frontend | HTML5, Vanilla CSS, JS |
| UI Style | Organic Brutalism (Custom System) |
| Fonts | Syne (Headings), DM Sans (Body) |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (recommended: Java 17 or 21)
- Maven 3.8+

### Run the App
```bash
# Development (with H2 console)
.\mvnw spring-boot:run

# Production (set environment variables first)
H2_CONSOLE_ENABLED=false OPENROUTER_API_KEY=your-key .\mvnw spring-boot:run
```

Then open: **http://localhost:8080**

### H2 Console (Dev Only)
Access at: **http://localhost:8080/h2-console** (requires `H2_CONSOLE_ENABLED=true`)

> ⚠️ H2 console is disabled by default. Enable only in development.

---

## 👤 Default Accounts

### Regular Users
Register at `/register` with any email and password.

### Admin Accounts (Pre-seeded with BCrypt passwords)

| Role | Email | Password | Access |
|---|---|---|---|
| Super Admin | `superadmin@recyclewise.id` | `changeme` | All stations, staff management |
| Station Staff | `budi@recyclewise.id` | `changeme` | Central Park Station only |
| Station Staff | `siti@recyclewise.id` | `changeme` | Kebon Jeruk Eco Hub only |
| Station Staff | `ahmad@recyclewise.id` | `changeme` | Kemang Green Point only |
| Station Staff | `dewi@recyclewise.id` | `changeme` | Kelapa Gading RecycleHub only |
| Station Staff | `reza@recyclewise.id` | `changeme` | Cibubur Waste Center only |
| Station Staff | `nur@recyclewise.id` | `changeme` | Menteng Eco Station only |

> Admin portal is accessible at `/admin/login` — separate from user login.

> ⚠️ **Important**: On production deployment (e.g., Koyeb), you must reset admin passwords with BCrypt hashes. See [Admin Password Reset](#admin-password-reset) below.

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

## 🎨 Design System: Organic Brutalism

RecycleWise uses a custom design system that blends raw, high-contrast layouts with earthy, eco-conscious tones.

### Color Palette
- **Forest Green** (`#0d2b1a`): Primary brand color, used for headers, dark surfaces, and primary text.
- **Acid Lime** (`#b7f34d`): The main accent color for buttons, highlights, and interactive hover states.
- **Warm Cream** (`#f5f0e8`): The subtle, eye-friendly neutral background for all pages.
- **Muted Sage** (`#4a5c4e`): Used for secondary text, metadata, and subtle borders.
- **Pure White** (`#ffffff`): Card surfaces and high-fidelity light highlights.

### Typography
- **Syne**: Used for all major headings. A bold, architectural typeface that gives the brand its unique character.
- **DM Sans**: Used for body copy and UI elements. Optimized for readability and clarity on any device.

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

## 🔐 Security Features

The app includes production-ready security measures:

| Feature | Status | Description |
|---|---|---|
| Password Hashing | ✅ Enabled | BCrypt for all passwords (users & admins) |
| CSRF Protection | ✅ Enabled | Cookie-based CSRF tokens on all forms |
| Security Headers | ✅ Enabled | CSP, X-Frame-Options, Referrer-Policy, etc. |
| Session Hardening | ✅ Enabled | 30-min timeout, secure/HttpOnly cookies |
| Rate Limiting | ✅ Enabled | 5 login attempts/min, 30 API requests/min |
| Input Validation | ✅ Enabled | Bean validation on all user inputs |
| H2 Console | ❌ Disabled by default | Enable only for local dev |

### Environment Variables for Production

```bash
# Database (if using file-based H2)
H2_PASSWORD=your-secure-password

# Disable H2 console in production
H2_CONSOLE_ENABLED=false

# AI Chat (required for EcoBot)
OPENROUTER_API_KEY=sk-or-v1-your-key

# Admin password reset secret key (temporary)
ADMIN_RESET_KEY=your-secret-key
```

### Admin Password Reset

Since passwords are BCrypt-hashed (one-way), you cannot "decode" them. To reset admin passwords:

**Option 1: Quick Reset Endpoint (Recommended for Koyeb)**

1. Set an environment variable `ADMIN_RESET_KEY` with a secret key (e.g., `mysecret123`)
2. Deploy the app
3. Visit this URL (replace with your actual domain):
   ```
   https://your-app.koyeb.app/admin/reset-passwords?key=mysecret123
   ```
4. You'll see JSON response:
   ```json
   {"success":true,"message":"All 7 admin passwords reset to: changeme","password":"changeme","accounts_updated":7}
   ```
5. **Important**: After resetting, either:
   - Remove the endpoint from code and redeploy, OR
   - Remove the `ADMIN_RESET_KEY` environment variable

**Option 2: Direct Database Update**

1. Connect to your database
2. Generate a BCrypt hash online: https://bcrypt-generator.com/
3. Run SQL:
```sql
UPDATE admin_users 
SET password = '$2a$10$YourBcryptHashHere' 
WHERE email = 'superadmin@recyclewise.id';
```

**Option 3: Via H2 Console (Local Dev Only)**
1. Enable `H2_CONSOLE_ENABLED=true`
2. Go to `/h2-console`
3. Run the same UPDATE query above

**Option 4: Create Migration Script**

Add to `DataSeeder.java` or create a Flyway migration:
```java
// In DataSeeder.java - runs on every startup in dev
@PostConstruct
public void resetAdminPasswords() {
    String bcryptHash = passwordEncoder.encode("changeme");
    adminUserRepository.findAll().forEach(admin -> {
        admin.setPassword(bcryptHash);
        adminUserRepository.save(admin);
    });
}
```

---

## 🔮 Production Setup

### Database Configuration

For production, swap H2 for PostgreSQL:

```properties
# Set via environment variables
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### Koyeb Deployment

1. Set environment variables in Koyeb dashboard:
   - `H2_CONSOLE_ENABLED=false`
   - `OPENROUTER_API_KEY=your-key`
   - `DATABASE_URL=postgresql://...` (if using managed PostgreSQL)

2. Add PostgreSQL dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

3. **Reset admin passwords** after deployment using one of the methods above.

---

## 🧪 Testing the Admin System

### Test Super Admin
1. Go to `/admin/login`
2. Login with `superadmin@recyclewise.id` / `changeme`
3. You should land on `/admin/dashboard` with:
   - Stats cards showing pending, confirmed, and rejected submission counts
   - A staff accounts table listing all 6 station staff members
4. Try adding a new staff member by filling in the form at the bottom — assign them to any station
5. The new account will be created with default password `changeme`
6. Try deactivating a staff account — they will no longer be able to log in

### Test Station Staff
1. Logout from Super Admin (`/admin/logout`)
2. Login with `budi@recyclewise.id` / `changeme`
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

MIT License - See LICENSE file for details.
