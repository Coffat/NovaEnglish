package com.languagecenter.dao;

import com.languagecenter.entity.Student;
import com.languagecenter.entity.Course;
import com.languagecenter.entity.Payment;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class StudentDAO {

    private static StudentDAO instance;

    private StudentDAO() {}

    public static StudentDAO getInstance() {
        if (instance == null) {
            instance = new StudentDAO();
        }
        return instance;
    }


    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Student", Student.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void saveStudentWithPayments(Student student, List<Course> selectedCourses) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(student);
            
            if (selectedCourses != null && !selectedCourses.isEmpty()) {
                java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
                for (Course course : selectedCourses) {
                    if (course.getFee() != null) {
                        totalAmount = totalAmount.add(course.getFee());
                    }
                }
                
                Payment payment = new Payment();
                payment.setStudent(student);
                payment.setAmount(totalAmount);
                payment.setStatus("Unpaid");
                payment.setPaymentDate(LocalDate.now());
                payment.setEnrollment(null); // Explicitly set to null because no enrollment yet
                
                session.persist(payment);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to save student with payments", e);
        }
    }

    public void updateStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.remove(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Student getStudentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Student student = session.get(Student.class, id);
            if (student != null) {
                // Initialize collections and nested properties before session close
                org.hibernate.Hibernate.initialize(student.getEnrollments());
                for (com.languagecenter.entity.Enrollment e : student.getEnrollments()) {
                    org.hibernate.Hibernate.initialize(e.getCourseClass());
                    if (e.getCourseClass() != null) {
                        org.hibernate.Hibernate.initialize(e.getCourseClass().getCourse());
                    }
                }
                org.hibernate.Hibernate.initialize(student.getPayments());
            }
            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
