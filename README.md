<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=250&section=header&text=NOVA%20ENGLISH&fontSize=70&animation=fadeIn&fontAlignY=35&desc=Advanced%20Language%20Center%20ERP&descAlignY=55&descSize=24" width="100%" />

# 🌟 Nova English: Language Center Management System 🌟

<p align="center">
    <b>A high-performance, enterprise-grade desktop solution for managing educational centers.</b><br>
    <i>Engineered with Java 21, Hibernate ORM, and a sleek modern UI to streamline your academic operations.</i>
  </p>

<p align="center">
    <a href="https://java.com/"><img src="https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 21" /></a>
    <a href="https://hibernate.org/"><img src="https://img.shields.io/badge/Hibernate-6.4-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate" /></a>
    <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" /></a>
    <a href="https://mysql.com/"><img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" /></a>
- [Java 21 LTS](https://java.com/)
- [Hibernate 6.4](https://hibernate.org/)
... and more.

> [!NOTE]
> **Tài liệu tiếng Việt**: Bạn có thể xem hướng dẫn sử dụng chi tiết bằng tiếng Việt tại [README_VN.md](./README_VN.md).

---

## 🚀 Key Modules & Features

Nova English is designed to handle every facet of language center administration with precision and style.

### 👥 Academic Management
- **Student Profiles**: Comprehensive records, registration tracking, and course history.
- **Teacher Portals**: Expertise management, assigned classes, and scheduling overview.
- **Enrollment System**: Seamlessly register students into specific classes with status tracking (Enrolled, Ongoing, Graduated).
- **Attendance Tracking**: Digital registers for classes with support for *Present*, *Absent*, and *Late* statuses.

### 📅 Operational Logistics
- **Course & Class Orchestration**: Manage multi-tier courses and assign them to specific classroom environments.
- **Smart Scheduling**: Generate full-term schedules automatically based on weekly patterns (e.g., Mon-Wed-Fri) and date ranges.
- **Room Management**: Track classroom availability and prevent scheduling conflicts.

### 💰 Financials & Reporting
- **Payment Processing**: Precise tuition fee tracking using `BigDecimal` for financial accuracy.
- **Automated Invoicing**: Link payments directly to enrollments and monitor outstanding balances.
- **Real-time Refresh**: Global listeners ensure all panels reflect the latest data changes immediately without manual refreshes.

---

## 🎨 Modern UI/UX Experience

Driven by **FlatLaf** design principles, the application offers a premium feel comparable to modern web apps.

- **Sliding Detail Panels**: View and edit entity details (Students, Teachers, Classes) without losing your place in the main table.
- **Context-Aware Controls**: Interactive checkboxes, dynamic dropdowns, and status-dependent UI elements.
- **Vibrant Rendering**: Custom cell renderers for status badges (Strategy Pattern) and modern iconography.
- **Adaptive Search**: Instant, realtime filtering across all management modules.

---

## 🛠️ Tech Stack & Architecture

### 🏗️ Design Patterns Applied
- **Builder Pattern**: Used for robust entity construction and cleaner code.
- **Strategy Pattern**: Implemented for dynamic UI rendering of statuses and roles.
- **DAO Pattern**: Centralized database logic using Hibernate's `Session` API for thread-safe operations.

### 💻 Infrastructure
- **Core**: Java 21 (LTS)
- **UI Framework**: Java Swing + MigLayout + FlatLaf (Custom Material/macOS styles)
- **Persistance**: Hibernate 6.4 (Jakarta Persistence)
- **Database**: MySQL 8.0
- **Build Tool**: Apache Maven

---

## 🏁 Getting Started

### 1️⃣ Prerequisites
- **Java JDK 21+**
- **Maven 3.x**
- **MySQL 8.x**

### 2️⃣ Database Setup
Import the provided `database.sql` to initialize your schema, or let Hibernate auto-generate it.

```sql
CREATE DATABASE language_centerdb;
```

Update your credentials in `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">USER</property>
<property name="hibernate.connection.password">PASSWORD</property>
```

### 3️⃣ Build & Launch
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.languagecenter.Main"
```

---

## 📈 Project Status
For detailed implementation progress, see [TienDoThucHien.md](./TienDoThucHien.md).

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=100&section=footer" width="100%" />
  <br/>
  <p>Crafted with ❤️ by Vũ Toàn Thắng & Đặng Ngọc Tài</p>
</div>
