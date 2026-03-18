# 📕 CẨM NANG SỬ DỤNG TOÀN DIỆN: NOVA ENGLISH SYSTEM

Chào mừng bạn đến với hướng dẫn sử dụng chi tiết nhất dành cho dự án **Nova English**. Tài liệu này được thiết kế để giúp bạn làm chủ mọi tính năng, từ các bảng nhập liệu nhanh (Side Panels) đến các hộp thoại hồ sơ chuyên sâu (Profile Dialogs).

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
-   **Phone**: Định dạng chuẩn 10 số, bắt đầu bằng số 0. Hệ thống có bộ lọc ngăn nhập ký tự lạ.
-   **Select Courses**: Danh sách các khóa học hiện có. Bạn có thể tích chọn nhiều khóa để ghi danh ngay khi tạo học viên. Hệ thống sẽ tự tạo các bản ghi `Enrollment` và `Payment` tương ứng.

### C. Hồ Sơ Chi Tiết (Student Profile Dialog)
![Hồ sơ Học viên](./docs/screenshots/dialog_student_profile.png)
**Thao tác**: Double-click vào một dòng trên bảng.
-   **Hệ thống thẻ (Cards)**: Hiển thị thông tin cá nhân (Địa chỉ, Giới tính, Email).
-   **Lịch sử học tập**: Liệt kê các lớp đã tham gia, ngày đăng ký, kết quả học tập và trạng thái chuyên cần (tính theo số buổi điểm danh).
-   **Nút Edit**: Cho phép chuyển thẳng sang chế độ sửa thông tin mà không cần đóng hộp thoại.

---

## 📚 3. Module: Khóa Học (Course)

### A. Màn Hình Chính
![Giao diện Quản lý Khóa học](./docs/screenshots/courses.png)

### B. Thêm/Sửa Khóa Học (Course Side Panel)
![Side Panel Khóa học](./docs/screenshots/sidepanel_course.png)
-   **Course Name**: Tên khóa học (ví dụ: IELTS 6.5+, Tiếng Anh Giao Tiếp).
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
-   **Specialty**: Chuyên môn chính (IELTS, TOEIC, Giao tiếp...).
-   **Status**: Nếu để `Inactive`, giáo viên này sẽ không xuất hiện trong danh sách chọn khi tạo lớp học mới.

### C. Hồ Sơ Giáo Viên (Teacher Profile Dialog)
![Hồ sơ Giáo viên](./docs/screenshots/dialog_teacher_profile.png)
Hiển thị các thông tin chuyên môn và **Lịch sử giảng dạy** (Danh sách các lớp giáo viên này đã và đang phụ trách).

---

## 🏫 4. Lớp Học & Ghi Danh (Class)

### A. Màn Hình Chính
![Giao diện Quản lý Lớp học](./docs/screenshots/classes.png)

### B. Chi Tiết Lớp Học (Class Side Panel)
![Side Panel Lớp học](./docs/screenshots/sidepanel_class.png)
-   **Course Class**: Chọn khóa học (Hệ thống tự điền học phí).
-   **Schedule Pattern**: Chọn mẫu lịch `2-4-6` (Thứ 2-4-6) hoặc `3-5-7` (Thứ 3-5-7).
-   **End Date (Calculated)**: Tự động tính toán ngày kết thúc dựa trên: `Ngày bắt đầu` + `Mẫu lịch` + `Tổng số buổi của khóa học`. Bạn không thể sửa tay trường này để đảm bảo logic lịch dạy.

### C. Quản Lý Học Viên Trong Lớp (Enrollments Dialog)
![Quản lý Ghi danh](./docs/screenshots/dialog_enrollments.png)
**Thao tác**: Nhấn biểu tượng 👥 (Students) ở cột Actions.
-   **Add to Class**: Chọn học viên từ danh sách thả xuống để thêm vào lớp.
-   **Result**: Nhập điểm số trực tiếp vào bảng. Nhớ nhấn **"Save Changes"** để lưu điểm.
-   **Remove**: Xóa học viên khỏi lớp (Cần xác nhận).

### D. Điểm Danh (Attendance Dialog)
![Điểm danh](./docs/screenshots/dialog_attendance.png)
**Thao tác**: Nhấn biểu tượng 📅 (Attendance) ở cột Actions.
-   Chọn ngày cần điểm danh (Mặc định là ngày hôm nay).
-   Chọn trạng thái cho từng học viên: `Present`, `Absent`, `Late`.
-   Hệ thống sẽ lưu vết để tính toán tỉ lệ đi học chuyên cần trong hồ sơ học viên.

---

## 📅 6. Lịch Học (Schedule)

### A. Màn Hình Chính
![Giao diện Lịch học](./docs/screenshots/schedules.png)

### B. Xếp Lịch Thông Minh (Schedule Side Panel)
![Side Panel Xếp lịch](./docs/screenshots/sidepanel_schedule.png)
Có 2 chế độ (Mode):
1.  **Single Session**: Tạo/Sửa lẻ một buổi học (dùng khi cần bù giờ hoặc đổi phòng).
2.  **Batch Generate**: 
    -   Tự động quét toàn bộ thời gian của lớp (từ ngày bắt đầu đến ngày kết thúc).
    -   Lấy ra các ngày phù hợp với mẫu lịch (`2-4-6` hoặc `3-5-7`).
    -   **Preview**: Cho phép xem trước 10 ngày đầu tiên sẽ được tạo.
    -   Khi nhấn **Save**, hệ thống sẽ xóa các lịch cũ của lớp đó và tạo mới toàn bộ buổi học đồng bộ.

---

## 💰 7. Thanh Toán & Học Phí (Payment)

### A. Màn Hình Chính
![Giao diện Thanh toán](./docs/screenshots/payments.png)

### B. Lập Phiếu Thu (Payment Side Panel)
![Side Panel Thanh toán](./docs/screenshots/sidepanel_payment.png)
-   **Amount**: Số tiền đóng (VNĐ).
-   **Payment Method**: Tiền mặt, Chuyển khoản hoặc Thẻ tín dụng.
-   **Status**: Mặc định là `Completed`.

---

## 📈 8. Báo Cáo & Thống Kê (Reports)
![Giao diện Báo cáo](./docs/screenshots/reports.png)
Dashboard cung cấp cái nhìn tổng quan:
-   **Biểu đồ Tăng trưởng**: Số lượng học viên đăng ký theo từng tháng.
-   **Thống kê Lớp học**: Số lượng lớp học đang mở, tổng số học viên hiện tại.
-   **Quản lý Doanh thu**: Tổng tiền thu được từ các hóa đơn thực tế.

---

## 💡 Mẹo & Lưu Ý Quan Trọng
-   **Instant Search**: Ô tìm kiếm trên Header hoạt động ngay khi bạn gõ chữ, không cần nhấn Enter.
-   **Validation**: 
    -   Số điện thoại phải đủ 10 số.
    -   Email phải đúng định dạng (có @ và .).
    -   Không thể xóa những dữ liệu đang được sử dụng ở module khác (ví dụ: Không thể xóa giáo viên đang dạy một lớp `On-going`).
-   **Real-time Refresh**: Các bảng thông tin sẽ tự động tải lại dữ liệu mới nhất sau khi bạn thực hiện bất kỳ thao tác Lưu/Xóa nào.

---
_Tài liệu hướng dẫn sử dụng Nova English - Phiên bản Chi Tiết Toàn Diện._
