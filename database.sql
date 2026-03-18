-- language_center_db.sql
-- Database creation script for LanguageCenterApp

-- Create Database
CREATE DATABASE IF NOT EXISTS LanguageCenterDB;
USE LanguageCenterDB;

-- 1. Table: UserAccount
-- Stores login credentials and roles for system users
CREATE TABLE IF NOT EXISTS UserAccount (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: Student
-- Stores detailed information about students
CREATE TABLE IF NOT EXISTS Student (
    StudentID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    DateOfBirth DATE,
    Gender VARCHAR(20),
    Phone VARCHAR(20),
    Email VARCHAR(255),
    Address VARCHAR(255),
    RegistrationDate DATE,
    Status VARCHAR(50) DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: Teacher
-- Stores teacher information and their specialties
CREATE TABLE IF NOT EXISTS Teacher (
    TeacherID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(255),
    Specialty VARCHAR(255),
    HireDate DATE,
    Status VARCHAR(50) DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: Course
-- Defines courses offered by the center
CREATE TABLE IF NOT EXISTS Course (
    CourseID INT AUTO_INCREMENT PRIMARY KEY,
    CourseName VARCHAR(255) NOT NULL,
    Description TEXT,
    Level VARCHAR(50),
    Duration INT, -- Duration in hours or lessons
    Fee DECIMAL(10, 2),
    Status VARCHAR(50) DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: Class (Entity: CourseClass)
-- Represents a specific class instance for a course
CREATE TABLE IF NOT EXISTS Class (
    ClassID INT AUTO_INCREMENT PRIMARY KEY,
    ClassName VARCHAR(255) NOT NULL,
    CourseID INT,
    TeacherID INT,
    StartDate DATE,
    EndDate DATE,
    MaxStudent INT,
    Status VARCHAR(50),
    CONSTRAINT fk_class_course FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE SET NULL,
    CONSTRAINT fk_class_teacher FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table: Enrollment
-- Records student registrations in classes
CREATE TABLE IF NOT EXISTS Enrollment (
    EnrollmentID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT NOT NULL,
    ClassID INT NOT NULL,
    EnrollmentDate DATE,
    Status VARCHAR(50),
    Result FLOAT DEFAULT 0.0,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_class FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Table: Payment
-- Tracks payments made by students for their enrollments
CREATE TABLE IF NOT EXISTS Payment (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT NOT NULL,
    EnrollmentID INT,
    Amount DECIMAL(10, 2) NOT NULL,
    PaymentDate DATE,
    PaymentMethod VARCHAR(50),
    Status VARCHAR(50),
    CONSTRAINT fk_payment_student FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    CONSTRAINT fk_payment_enrollment FOREIGN KEY (EnrollmentID) REFERENCES Enrollment(EnrollmentID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Table: Schedule
-- Stores specific timing and location for class sessions
CREATE TABLE IF NOT EXISTS Schedule (
    ScheduleID INT AUTO_INCREMENT PRIMARY KEY,
    ClassID INT NOT NULL,
    Date DATE NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    RoomID INT,
    CONSTRAINT fk_schedule_class FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Table: Attendance
-- Tracks student attendance for each class session
CREATE TABLE IF NOT EXISTS Attendance (
    AttendanceID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT NOT NULL,
    ClassID INT NOT NULL,
    Date DATE NOT NULL,
    Status VARCHAR(50),
    CONSTRAINT fk_attendance_student FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_class FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --- SEED DATA ---
-- Idempotent data seeding

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Attendance;
TRUNCATE TABLE Payment;
TRUNCATE TABLE Enrollment;
TRUNCATE TABLE Schedule;
TRUNCATE TABLE Class;
TRUNCATE TABLE Course;
TRUNCATE TABLE Teacher;
TRUNCATE TABLE UserAccount;
TRUNCATE TABLE Student;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. UserAccounts
INSERT INTO UserAccount (username, password, role) VALUES 
('admin', '123456', 'ADMIN'),
('staff', '123456', 'STAFF'),
('root', '123456', 'ADMIN');

-- 2. Teachers
INSERT INTO Teacher (TeacherID, FullName, Phone, Email, Specialty, HireDate, Status) VALUES
(1, 'Nguyễn Văn Hùng', '0912123456', 'hung.nv@language.edu.vn', 'IELTS Expert', '2021-03-10', 'Active'),
(2, 'Trần Thị Lan', '0912123457', 'lan.tt@language.edu.vn', 'TOEIC Specialist', '2023-05-15', 'Active'),
(3, 'Lê Văn Nam', '0912123458', 'nam.lv@language.edu.vn', 'Business English', '2022-11-20', 'Active'),
(4, 'Phạm Minh Tuấn', '0912123459', 'tuan.pm@language.edu.vn', 'English for Kids', '2024-01-05', 'Active'),
(5, 'Hoàng Mỹ Linh', '0912123460', 'linh.hm@language.edu.vn', 'TOEFL Specialist', '2020-09-12', 'Active');

-- 3. Courses
INSERT INTO Course (CourseID, CourseName, Description, Level, Duration, Fee, Status) VALUES
(1, 'IELTS Masterclass (Band 7.5+)', 'Advanced preparation for high-impact results.', 'Advanced', 60, 8500000.00, 'Active'),
(2, 'TOEIC Intensive 750+', 'Focus on listening and reading skills.', 'Intermediate', 45, 4200000.00, 'Active'),
(3, 'Business Communication', 'Professional English for modern workplace.', 'Advanced', 40, 6000000.00, 'Active'),
(4, 'General English A2', 'Basic communication for beginners.', 'Beginner', 30, 3500000.00, 'Active'),
(5, 'Placement Test', 'Assessment course for level placement.', 'Beginner', 1, 200000.00, 'Active');

-- 4. Classes (CourseClass)
INSERT INTO Class (ClassID, ClassName, CourseID, TeacherID, StartDate, EndDate, MaxStudent, Status) VALUES
(1, 'IELTS-2026-Q1', 1, 1, '2026-01-05', '2026-05-30', 15, 'On-going'),
(2, 'TOEIC-750-N2', 2, 2, '2026-02-10', '2026-06-15', 20, 'On-going'),
(3, 'IELTS-2025-WINTER', 1, 1, '2025-10-01', '2026-01-15', 12, 'Completed'),
(4, 'BUS-ENG-SPRING', 3, 3, '2026-04-01', '2026-07-10', 15, 'Opening');

-- 5. Students
INSERT INTO Student (StudentID, FullName, DateOfBirth, Gender, Phone, Email, Address, RegistrationDate, Status) VALUES
(1, 'Nguyễn Minh Anh', '2000-05-15', 'Female', '0901234567', 'anh.nm@gmail.com', 'Quận 1, TP.HCM', '2025-12-01', 'Active'),
(2, 'Trần Hoàng Nam', '1998-10-20', 'Male', '0902345678', 'nam.th@gmail.com', 'Quận 3, TP.HCM', '2025-12-05', 'Active'),
(3, 'Lê Thu Hà', '2002-01-12', 'Female', '0903456789', 'ha.lt@gmail.com', 'Quận 7, TP.HCM', '2025-12-10', 'Active'),
(4, 'Phạm Minh Đức', '1995-08-25', 'Male', '0904567890', 'duc.pm@gmail.com', 'Quận Bình Thạnh, TP.HCM', '2025-12-15', 'Active'),
(5, 'Vũ Thị Mai', '2001-03-30', 'Female', '0905678901', 'mai.vt@gmail.com', 'Quận Tân Bình, TP.HCM', '2025-12-20', 'Active');

-- 6. Enrollments
INSERT INTO Enrollment (EnrollmentID, StudentID, ClassID, EnrollmentDate, Status, Result) VALUES
(1, 1, 1, '2025-12-25', 'Ongoing', 0.0),
(2, 2, 1, '2025-12-26', 'Ongoing', 0.0),
(3, 3, 2, '2026-01-05', 'Ongoing', 0.0),
(4, 4, 3, '2025-09-20', 'Completed', 8.5),
(5, 5, 4, '2026-03-01', 'Enrolled', 0.0);

-- 7. Payments
INSERT INTO Payment (PaymentID, StudentID, EnrollmentID, Amount, PaymentDate, PaymentMethod, Status) VALUES
(1, 1, 1, 8500000.00, '2025-12-27', 'Transfer', 'Completed'),
(2, 2, 1, 8500000.00, '2025-12-28', 'Cash', 'Completed'),
(3, 3, 2, 4200000.00, '2026-01-07', 'Transfer', 'Completed'),
(4, 4, 3, 8500000.00, '2025-09-22', 'Transfer', 'Completed');

-- 8. Schedules (Sample sessions)
INSERT INTO Schedule (ClassID, Date, StartTime, EndTime, RoomID) VALUES
(1, '2026-01-05', '18:30:00', '20:30:00', 101),
(1, '2026-01-07', '18:30:00', '20:30:00', 101),
(1, '2026-01-09', '18:30:00', '20:30:00', 101),
(2, '2026-02-10', '19:00:00', '21:00:00', 102),
(2, '2026-02-12', '19:00:00', '21:00:00', 102),
(3, '2025-10-01', '08:00:00', '10:00:00', 103);

-- 9. Attendance (Sample)
INSERT INTO Attendance (StudentID, ClassID, Date, Status) VALUES
(1, 1, '2026-01-05', 'Present'),
(2, 1, '2026-01-05', 'Present'),
(1, 1, '2026-01-07', 'Late'),
(2, 1, '2026-01-07', 'Present'),
(3, 2, '2026-02-10', 'Present');
