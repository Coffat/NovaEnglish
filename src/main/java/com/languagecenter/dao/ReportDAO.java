package com.languagecenter.dao;

import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;

public class ReportDAO {

    private static ReportDAO instance;

    private ReportDAO() {}

    public static ReportDAO getInstance() {
        if (instance == null) {
            instance = new ReportDAO();
        }
        return instance;
    }


    public long getTotalStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(s) FROM Student s", Long.class).uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getTotalTeachers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(t) FROM Teacher t", Long.class).uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getTotalCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(c) FROM Course c", Long.class).uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getActiveClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(c) FROM CourseClass c WHERE c.status = 'On-going'", Long.class).uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public BigDecimal getTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'Completed' OR p.status = 'Paid'", BigDecimal.class).uniqueResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public java.util.List<Object[]> getStudentsByCourse() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT e.courseClass.course.name, COUNT(e.student.id) FROM Enrollment e GROUP BY e.courseClass.course.id", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<Object[]> getRevenueByMonth() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                "SELECT DATE_FORMAT(PaymentDate, '%Y-%m') as month, SUM(Amount) as total " +
                "FROM Payment WHERE Status IN ('Completed', 'Paid') " +
                "GROUP BY month ORDER BY month", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<Object[]> getEnrollmentStatusDistribution() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT e.status, COUNT(e.id) FROM Enrollment e GROUP BY e.status", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<Object[]> getStudentRegistrationTrend() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                "SELECT DATE_FORMAT(RegistrationDate, '%Y-%m') as month, COUNT(StudentID) as count " +
                "FROM Student " +
                "GROUP BY month ORDER BY month LIMIT 12", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<Object[]> getTeacherLoad() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT t.fullName, COUNT(c.id) FROM CourseClass c JOIN c.teacher t GROUP BY t.id", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<Object[]> getClassStatusDistribution() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c.status, COUNT(c.id) FROM CourseClass c GROUP BY c.status", Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
