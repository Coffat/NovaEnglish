<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=250&section=header&text=NOVA%20ENGLISH&fontSize=70&animation=fadeIn&fontAlignY=35&desc=Advanced%20Language%20Center%20ERP&descAlignY=55&descSize=24" width="100%" />

# 🌟 Nova English: Language Center Management System 🌟

<p align="center">
    <b>A high-performance, enterprise-grade desktop solution for managing educational centers.</b><br>
    <i>Engineered with Java 21, Hibernate ORM, and a sleek modern UI to streamline academic operations.</i>
  </p>

<p align="center">
    <a href="https://java.com/"><img src="https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 21" /></a>
    <a href="https://hibernate.org/"><img src="https://img.shields.io/badge/Hibernate-6.4-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate" /></a>
    <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" /></a>
    <a href="https://mysql.com/"><img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" /></a>
</p>
</div>

---

## 🚀 Key Modules & Features
Nova English handles every facet of language center administration with precision:
- **Academic**: Student Profiles, Teacher Portals, Enrollment System, Attendance Tracking.
- **Logistics**: Course & Class Orchestration, Smart Scheduling (Batch Generation), Room Management.
- **Financials**: Payment Processing (BigDecimal accuracy), Automated Invoicing.
- **Modern UI**: Sliding Detail Panels, Adaptive Search, Real-time Refresh.

---

## 🛠️ Tech Stack & Architecture

### 🏗️ Infrastructure & Patterns
- **Core Stack**: Java 21 (LTS), Maven 3.x, MySQL 8.0.
- **ORM**: Hibernate 6.4 (Jakarta Persistence).
- **UI Framework**: Java Swing + MigLayout + FlatLaf (Custom Styles).
- **Design Patterns**: Builder Pattern (Entities), Strategy Pattern (Status Rendering), DAO Pattern.

---

## 🏁 Getting Started (Cài đặt & Khởi chạy)

### 1️⃣ Database Setup
- Cài đặt MySQL 8.0.
- Tạo database: `CREATE DATABASE language_centerdb;`
- Import file `database.sql` để có cấu trúc bảng và dữ liệu mẫu (Seed data).
- Cập nhật thông tin kết nối tại `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">YOUR_USER</property>
<property name="hibernate.connection.password">YOUR_PASSWORD</property>
```

### 2️⃣ Build & Launch
Mở terminal tại thư mục gốc và chạy:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.languagecenter.Main"
```

---

# 📕 CẨM NANG SỬ DỤNG CHI TIẾT (USER GUIDE)

Tài liệu này hướng dẫn chi tiết cách vận hành hệ thống Nova English dành cho người dùng cuối.

## 🔑 1. Đăng Nhập & Khởi Động
Hệ thống hỗ trợ 3 vai trò mặc định (Mật khẩu: `123456`):
- **Admin**: Quản trị viên (Toàn quyền).
- **Staff**: Nhân viên quản lý.
- **Root**: Tài khoản cấp cao nhất.

---

## 👨‍🎓 2. Module: Học Viên (Student)

### A. Màn Hình Chính
![Giao diện Quản lý Học viên](./docs/screenshots/students.png)
Tìm kiếm nhanh theo Tên, Email hoặc Số điện thoại.

### B. Thêm/Sửa & Hồ Sơ (Side Panel & Dialog)
![Side Panel Học viên](./docs/screenshots/sidepanel_student.png) | ![Hồ sơ Học viên](./docs/screenshots/dialog_student_profile.png)
--- | ---
**Side Panel**: Nhập nhanh thông tin & đăng ký khóa học ngay lập tức. | **Profile Dialog**: Xem toàn bộ lịch sử học tập & thanh toán chi tiết.

---

## 📚 3. Module: Khóa Học & Lớp Học (Course & Class)

### A. Quản Lý Khóa Học
![Giao diện Quản lý Khóa học](./docs/screenshots/courses.png) | ![Hồ sơ Khóa học](./docs/screenshots/dialog_course_profile.png)
--- | ---

### B. Quản Lý Lớp Học & Ghi Danh
![Giao diện Quản lý Lớp học](./docs/screenshots/classes.png)
- **Enrollment**: Nhấn 👥 để quản lý danh sách học viên trong lớp, nhập điểm số (`Result`).
- **Attendance**: Nhấn 📅 để điểm danh hàng ngày (`Present`, `Absent`, `Late`).

---

## 👩‍🏫 4. Module: Giáo Viên (Teacher)
![Giao diện Quản lý Giáo viên](./docs/screenshots/teachers.png)
Quản lý hồ sơ giáo viên, chuyên môn (`Specialty`) và lịch sử giảng dạy.

---

## 📅 5. Quản Lý Lịch Học (Schedule)
![Side Panel Xếp lịch](./docs/screenshots/sidepanel_schedule.png)
- **Batch Generate**: Tính năng thông minh tự động sinh lịch học cho toàn bộ khóa dựa trên mẫu lịch (`2-4-6`, `3-5-7`) và ngày bắt đầu/kết thúc.

---

## 💰 6. Tài Chính & Báo Cáo (Payment & Reports)

### A. Thanh Toán Học Phí
![Giao diện Thanh toán](./docs/screenshots/payments.png)
Ghi nhận các khoản thu từ học viên qua Tiền mặt, Chuyển khoản hoặc Thẻ.

### B. Dashboard Báo Cáo Thông Minh
![Giao diện Báo cáo](./docs/screenshots/reports.png)
Theo dõi biểu đồ doanh thu theo tháng, tăng trưởng học viên và thống kê hiệu suất lớp học.

---

## 💡 Mẹo Sử Dụng
- **Instant Search**: Ô tìm kiếm hoạt động lập tức khi bạn gõ, không cần nhấn Enter.
- **Tự động refresh**: Dữ liệu sẽ tự động đồng bộ trên tất cả các tab mà không cần tải lại trang.
- **Xác thực dữ liệu**: Số điện thoại phải đủ 10 chữ số, Email phải đúng định dạng.

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=100&section=footer" width="100%" />
  <br/>
  <p>Crafted with ❤️ by Vũ Toàn Thắng & Đặng Ngọc Tài</p>
</div>
