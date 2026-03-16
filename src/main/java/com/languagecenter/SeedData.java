package com.languagecenter;

import com.languagecenter.entity.*;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class SeedData {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Clear existing data (optional, handling manually)
            
            // Add Teachers
            Teacher t1 = Teacher.builder()
                .fullName("John Doe")
                .email("john.doe@email.com")
                .phone("1234567890")
                .specialty("IELTS")
                .hireDate(LocalDate.now().minusYears(1))
                .status("Active")
                .build();
            session.persist(t1);

            Teacher t2 = Teacher.builder()
                .fullName("Jane Smith")
                .email("jane.smith@email.com")
                .phone("0987654321")
                .specialty("TOEIC")
                .hireDate(LocalDate.now().minusMonths(6))
                .status("Active")
                .build();
            session.persist(t2);

            // Add Courses
            Course c1 = Course.builder()
                .name("IELTS Masterclass")
                .description("Advanced IELTS preparation")
                .level("Advanced")
                .duration(60)
                .fee(new BigDecimal("500.00"))
                .status("Active")
                .build();
            session.persist(c1);

            Course c2 = Course.builder()
                .name("TOEIC Intensive")
                .description("Intensive TOEIC training")
                .level("Intermediate")
                .duration(45)
                .fee(new BigDecimal("300.00"))
                .status("Active")
                .build();
            session.persist(c2);

            // Add Classes
            CourseClass class1 = new CourseClass();
            class1.setClassName("IELTS-2026A");
            class1.setCourse(c1);
            class1.setTeacher(t1);
            class1.setStartDate(LocalDate.now().minusDays(10));
            class1.setEndDate(LocalDate.now().plusDays(50));
            class1.setMaxStudent(20);
            class1.setStatus("On-going");
            session.persist(class1);

            CourseClass class2 = new CourseClass();
            class2.setClassName("TOEIC-2026B");
            class2.setCourse(c2);
            class2.setTeacher(t2);
            class2.setStartDate(LocalDate.now().minusMonths(1));
            class2.setEndDate(LocalDate.now().plusMonths(1));
            class2.setMaxStudent(30);
            class2.setStatus("On-going");
            session.persist(class2);
            
            CourseClass class3 = new CourseClass();
            class3.setClassName("IELTS-Old");
            class3.setCourse(c1);
            class3.setTeacher(t1);
            class3.setStartDate(LocalDate.now().minusMonths(3));
            class3.setEndDate(LocalDate.now().minusMonths(1));
            class3.setMaxStudent(20);
            class3.setStatus("Completed");
            session.persist(class3);

            // Add Students
            for (int i = 1; i <= 15; i++) {
                Student s = Student.builder()
                    .fullName("Student " + i)
                    .dateOfBirth(LocalDate.of(2000 + (i % 5), 1 + (i % 12), 1 + (i % 28)))
                    .gender(i % 2 == 0 ? "Male" : "Female")
                    .phone("55512340" + i)
                    .email("student" + i + "@email.com")
                    .address("City " + i)
                    .registrationDate(LocalDate.now().minusMonths(i % 6))
                    .status("Active")
                    .build();
                session.persist(s);

                // Add Enrollments & Payments
                Enrollment e = new Enrollment();
                e.setStudent(s);
                e.setCourseClass(i % 2 == 0 ? class1 : class2);
                e.setEnrollmentDate(LocalDate.now().minusDays(i * 2));
                e.setStatus("Enrolled");
                e.setResult(0.0f);
                session.persist(e);

                Payment p = new Payment();
                p.setStudent(s);
                p.setEnrollment(e);
                p.setAmount(i % 2 == 0 ? c1.getFee() : c2.getFee());
                p.setPaymentDate(LocalDate.now().minusDays(i * 2));
                p.setPaymentMethod(i % 3 == 0 ? "Cash" : "Credit Card");
                p.setStatus("Completed");
                session.persist(p);
            }

            transaction.commit();
            System.out.println("Seed data added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
