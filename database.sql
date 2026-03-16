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

-- Add default administrator
-- Updated to root/123456 based on user preference/DB credentials
INSERT INTO UserAccount (username, password, role) 
VALUES ('root', '123456', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

-- Optional: Initial Teachers
INSERT INTO Teacher (FullName, Phone, Email, Specialty, HireDate, Status) VALUES
('John Doe', '1234567890', 'john.doe@email.com', 'IELTS', CURDATE(), 'Active'),
('Jane Smith', '0987654321', 'jane.smith@email.com', 'TOEIC', CURDATE(), 'Active');

-- Optional: Initial Courses
INSERT INTO Course (CourseName, Description, Level, Duration, Fee, Status) VALUES
('IELTS Masterclass', 'Advanced IELTS preparation', 'Advanced', 60, 500.00, 'Active'),
('TOEIC Intensive', 'Intensive TOEIC training', 'Intermediate', 45, 300.00, 'Active');
