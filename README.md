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
- **Academic**: Student Profiles, Teacher Portals, Enrollment System, Attendance Tracking.
- **Logistics**: Course & Class Orchestration, Smart Scheduling (Batch Generation), Room Management.
- **Financials**: Payment Processing, Automated Invoicing, Revenue Reporting.
- **Modern UI**: Sliding Detail Panels, Adaptive Search, Real-time Data Sync.

---

## 🏁 Getting Started (Cài đặt & Khởi chạy)

### 1️⃣ Database Setup
- Cài đặt MySQL 8.0.
- Tạo database: `CREATE DATABASE language_centerdb;`
- Import file `database.sql`.
- Cấu hình tại `src/main/resources/hibernate.cfg.xml`.

### 2️⃣ Build & Launch
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.languagecenter.Main"
```

---

# 📕 CẨM NANG SỬ DỤNG CHI TIẾT (USER GUIDE)

## 🔑 1. Đăng Nhập & Khởi Động
Hệ thống hỗ trợ 3 vai trò mặc định (Mật khẩu: `123456`):
- **Admin**: Quản trị viên (Toàn quyền).
- **Staff**: Nhân viên quản lý.
- **Root**: Tài khoản cấp cao nhất.

---

## 👨‍🎓 2. Module: Học Viên (Student)
![Giao diện Quản lý Học viên](./docs/screenshots/students.png)

![Side Panel Học viên](./docs/screenshots/sidepanel_student.png) | ![Hồ sơ Học viên](./docs/screenshots/dialog_student_profile.png)
--- | ---
**Side Panel**: Nhập nhanh thông tin & đăng ký khóa học ngay lập tức. | **Profile Dialog**: Xem toàn bộ lịch sử học tập & thanh toán chi tiết.

---

## 👩‍🏫 3. Module: Giáo Viên (Teacher)
![Giao diện Quản lý Giáo viên](./docs/screenshots/teachers.png)

![Side Panel Giáo viên](./docs/screenshots/sidepanel_teacher.png) | ![Hồ sơ Giáo viên](./docs/screenshots/dialog_teacher_profile.png)
--- | ---
**Side Panel**: Cập nhật thông tin chuyên môn, trạng thái. | **Profile Dialog**: Hiển thị hồ sơ và danh sách lớp giảng dạy.

---

## 📚 4. Module: Khóa Học (Course)
![Giao diện Quản lý Khóa học](./docs/screenshots/courses.png)

![Side Panel Khóa học](./docs/screenshots/sidepanel_course.png) | ![Hồ sơ Khóa học](./docs/screenshots/dialog_course_profile.png)
--- | ---
**Side Panel**: Định nghĩa tên khóa, học phí, thời lượng. | **Profile Dialog**: Mô tả chi tiết và các lớp thuộc khóa học.

---

## 🏫 5. Module: Lớp Học & Ghi Danh (Class)
Đây là module cốt lõi kết nối Học viên, Giáo viên và Khóa học.

### A. Quản Lý Lớp Học
![Giao diện Quản lý Lớp học](./docs/screenshots/classes.png)
- **Side Panel**: ![Side Panel Lớp học](./docs/screenshots/sidepanel_class.png)
- **Tự động hóa**: Hệ thống tự tính toán ngày kết thúc dựa trên ngày bắt đầu và tổng số buổi học.

### B. Ghi Danh & Kết Quả (Enrollment)
![Quản lý Ghi danh](./docs/screenshots/dialog_enrollments.png)
- **Thử Thách Ghi Danh**: Nhấn biểu tượng 👥 (Students) để thêm/xóa học viên vào lớp.
- **Nhập Điểm**: Cho phép nhập điểm trực tiếp vào cột `Result` và lưu lại nhanh chóng.

### C. Điểm Danh (Attendance)
![Điểm danh](./docs/screenshots/dialog_attendance.png)
- **Thực Hiện**: Nhấn biểu tượng 📅 (Attendance) bên cạnh mỗi lớp.
- **Trạng Thái**: Hỗ trợ 3 trạng thái `Present` (Có mặt), `Absent` (Vắng), `Late` (Muộn).

---

## 📅 6. Quản Lý Lịch Học (Schedule)
![Giao diện Lịch học](./docs/screenshots/schedules.png)

![Side Panel Sửa lịch](./docs/screenshots/sidepanel_schedule_single.png) | ![Side Panel Xếp lịch hàng loạt](./docs/screenshots/sidepanel_schedule_batch.png)
--- | ---
**Single Session**: Chỉnh sửa hoặc thêm mới một buổi học lẻ (đổi phòng, đổi giờ). | **Batch Generate**: Tự động sinh lịch học cho toàn bộ khóa dựa trên mẫu lịch và ngày bắt đầu/kết thúc.

---

## 💰 7. Tài Chính & Báo Cáo (Payment & Reports)

### A. Thanh Toán Học Phí
![Giao diện Thanh toán](./docs/screenshots/payments.png) | ![Side Panel Thanh toán](./docs/screenshots/sidepanel_payment.png)
--- | ---

### B. Dashboard Báo Báo Chiến Lược
![Giao diện Báo cáo](./docs/screenshots/reports.png)
Theo dõi doanh thu, tăng trưởng học viên và hiệu suất lớp học qua các biểu đồ trực quan.

---

## 💡 Mẹo Sử Dụng
- **Instant Search**: Kết quả lọc ngay lập tức khi bạn gõ, không cần nhấn Enter.
- **Tự động refresh**: Dữ liệu đồng bộ realtime trên tất cả các tab.

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=100&section=footer" width="100%" />
  <br/>
  <p>Crafted with ❤️ by Vũ Toàn Thắng & Đặng Ngọc Tài</p>
</div>
