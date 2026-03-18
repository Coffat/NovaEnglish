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
</p>
</div>

---

# 📕 CẨM NANG SỬ DỤNG TOÀN DIỆN (USER MANUAL)

Chào mừng bạn đến với hướng dẫn sử dụng chi tiết dành cho dự án **Nova English**. Tài liệu này giúp bạn làm chủ mọi tính năng, từ các bảng nhập liệu nhanh (Side Panels) đến các hộp thoại hồ sơ chuyên sâu (Profile Dialogs).

---

## 🔑 1. Đăng Nhập & Khởi Động
Khi khởi chạy ứng dụng, màn hình đăng nhập sẽ xuất hiện. Dưới đây là các tài khoản mặc định có sẵn trong dữ liệu mẫu:
-   **Admin**: `admin` / `123456` (Toàn quyền hệ thống)
-   **Staff**: `staff` / `123456` (Quản lý nghiệp vụ)
-   **Root**: `root` / `123456` (Quyền cao nhất)

---

## 👨‍🎓 2. Quản Lý Học Viên (Student)

### A. Màn Hình Chính
![Giao diện Quản lý Học viên](./docs/screenshots/students.png)
Hiển thị danh sách tất cả học viên. Bạn có thể tìm kiếm nhanh theo Tên, Email hoặc Số điện thoại ở thanh công cụ phía trên.

### B. Thêm/Sửa Học Viên (Student Side Panel)
![Side Panel Học viên](./docs/screenshots/sidepanel_student.png)
Khi nhấn **"Add Student"** hoặc biểu tượng **Edit**, bảng này sẽ hiện ra:
-   **Full Name**: Tên đầy đủ (Bắt buộc).
-   **DOB**: Chọn ngày sinh từ lịch.
-   **Phone**: Định dạng chuẩn 10 số, bắt đầu bằng số 0.
-   **Select Courses**: Bạn có thể tích chọn nhiều khóa để ghi danh ngay khi tạo học viên.

### C. Hồ Sơ Chi Tiết (Student Profile Dialog)
![Hồ sơ Học viên](./docs/screenshots/dialog_student_profile.png)
**Thao tác**: Double-click vào một dòng trên bảng.
-   **Hệ thống thẻ (Cards)**: Hiển thị thông tin cá nhân (Địa chỉ, Giới tính, Email).
-   **Lịch sử học tập**: Liệt kê các lớp đã tham gia, kết quả và trạng thái chuyên cần.

---

## 📚 3. Module: Khóa Học (Course)

### A. Màn Hình Chính
![Giao diện Quản lý Khóa học](./docs/screenshots/courses.png)

### B. Thêm/Sửa Khóa Học (Course Side Panel)
![Side Panel Khóa học](./docs/screenshots/sidepanel_course.png)
-   **Course Name**: Tên khóa học (ví dụ: IELTS 6.5+).
-   **Duration**: Tổng số buổi học.
-   **Fee**: Học phí trọn khóa.

### C. Hồ Sơ Khóa Học (Course Profile Dialog)
![Hồ sơ Khóa học](./docs/screenshots/dialog_course_profile.png)
Hiển thị mô tả chi tiết và **Danh sách các lớp** đang mở thuộc khóa học này.

---

## 👩‍🏫 4. Module: Quản Lý Giáo Viên (Teacher)

### A. Màn Hình Chính
![Giao diện Quản lý Giáo viên](./docs/screenshots/teachers.png)

### B. Thêm/Sửa Giáo Viên (Teacher Side Panel)
![Side Panel Giáo viên](./docs/screenshots/sidepanel_teacher.png)
-   **Specialty**: Chuyên môn chính (IELTS, TOEIC...).
-   **Status**: Nếu `Inactive`, giáo viên sẽ không xuất hiện khi tạo lớp mới.

### C. Hồ Sơ Giáo Viên (Teacher Profile Dialog)
![Hồ sơ Giáo viên](./docs/screenshots/dialog_teacher_profile.png)
Hiển thị thông tin chuyên môn và **Lịch sử giảng dạy**.

---

## 🏫 5. Lớp Học & Ghi Danh (Class)

### A. Màn Hình Chính
![Giao diện Quản lý Lớp học](./docs/screenshots/classes.png)

### B. Chi Tiết Lớp Học (Class Side Panel)
![Side Panel Lớp học](./docs/screenshots/sidepanel_class.png)
-   **Course Class**: Chọn khóa học (Tự điền học phí).
-   **Schedule Pattern**: Chọn `2-4-6` hoặc `3-5-7`.
-   **End Date (Calculated)**: Tự động tính toán ngày kết thúc dựa trên mẫu lịch và tổng số buổi.

### C. Quản Lý Học Viên Trong Lớp (Enrollments Dialog)
![Quản lý Ghi danh](./docs/screenshots/dialog_enrollments.png)
**Thao tác**: Nhấn biểu tượng 👥 (Students) ở cột Actions.
-   **Add to Class**: Thêm học viên vào lớp.
-   **Result**: Nhập điểm số trực tiếp vào bảng.

### D. Điểm Danh (Attendance Dialog)
![Điểm danh](./docs/screenshots/dialog_attendance.png)
**Thao tác**: Nhấn biểu tượng 📅 (Attendance) ở cột Actions.
-   Chọn ngày và trạng thái: `Present`, `Absent`, `Late`.

---

## 📅 6. Lịch Học (Schedule)

### A. Màn Hình Chính
![Giao diện Lịch học](./docs/screenshots/schedules.png)

### B. Xếp Lịch Thông Minh (Schedule Side Panel)
![Side Panel Xếp lịch](./docs/screenshots/sidepanel_schedule.png)
-   **Batch Generate**: Tự động sinh toàn bộ lịch học cho cả khóa dựa trên mẫu lịch.
-   **Preview**: Cho phép xem trước các ngày học sẽ được tạo.

---

## 💰 7. Thanh Toán & Học Phí (Payment)

### A. Màn Hình Chính
![Giao diện Thanh toán](./docs/screenshots/payments.png)

### B. Lập Phiếu Thu (Payment Side Panel)
![Side Panel Thanh toán](./docs/screenshots/sidepanel_payment.png)
-   **Amount**: Số tiền đóng (VNĐ).
-   **Payment Method**: Tiền mặt, Chuyển khoản, Thẻ.

---

## 📈 8. Báo Cáo & Thống Kê (Reports)
![Giao diện Báo cáo](./docs/screenshots/reports.png)
Dashboard tổng quan về Doanh thu, Tăng trưởng học viên và Thống kê lớp học.

---

## 🛠️ Tech Stack & Architecture

### 🏗️ Design Patterns Applied
- **Builder Pattern**: Entity construction.
- **Strategy Pattern**: Dynamic UI rendering (Status badges).
- **DAO Pattern**: Hibernate-based data access.

### 💻 Infrastructure
- **Core**: Java 21 (LTS)
- **UI Framework**: Java Swing + MigLayout + FlatLaf
- **Database**: MySQL 8.0 + Hibernate 6.4

---

## 🏁 Getting Started (Cài đặt)

### 1️⃣ Database Setup
Import `database.sql` và cập nhật thông tin tại `src/main/resources/hibernate.cfg.xml`.

### 2️⃣ Build & Launch
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.languagecenter.Main"
```

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=100&section=footer" width="100%" />
  <br/>
  <p>Crafted with ❤️ by Vũ Toàn Thắng & Đặng Ngọc Tài</p>
</div>
