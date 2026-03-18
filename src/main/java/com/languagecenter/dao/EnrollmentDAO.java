package com.languagecenter.dao;

import com.languagecenter.entity.Enrollment;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Course;
import com.languagecenter.entity.Payment;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class EnrollmentDAO {

    private static EnrollmentDAO instance;

    private EnrollmentDAO() {}

    public static EnrollmentDAO getInstance() {
        if (instance == null) {
            instance = new EnrollmentDAO();
        }
        return instance;
    }


    public List<Enrollment> getAllEnrollments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Enrollment", Enrollment.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(enrollment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean isEligibleForClass(int studentId, int classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CourseClass courseClass = session.get(CourseClass.class, classId);
            if (courseClass == null || courseClass.getCourse() == null) return false;
            
            Course course = courseClass.getCourse();
            int targetLevelWeight = Course.getLevelWeight(course.getLevel());
            
            // 1. Beginner level is always accessible
            if (targetLevelWeight <= 1) {
                return true;
            }
            
            // 2. Check student history
            String historyHql = "FROM Enrollment e WHERE e.student.id = :studentId";
            List<Enrollment> allEnrollments = session.createQuery(historyHql, Enrollment.class)
                .setParameter("studentId", studentId)
                .list();
            
            // If they have NO history at all, we consider them "New" and allow entry to their first chosen course
            // (Assuming they were placement-tested or manually vetted during registration)
            if (allEnrollments.isEmpty()) {
                return true; 
            }
            
            // 3. If they HAVE history, enforce prerequisite logic
            boolean hasPreceding = false;
            boolean hasPlacementTest = false;
            
            for (Enrollment e : allEnrollments) {
                if (!"Completed".equalsIgnoreCase(e.getStatus())) continue;
                
                Course c = e.getCourseClass().getCourse();
                if ("Placement Test".equalsIgnoreCase(c.getName())) {
                    hasPlacementTest = true;
                }
                if (Course.getLevelWeight(c.getLevel()) == targetLevelWeight - 1) {
                    hasPreceding = true;
                }
            }
            
            return hasPreceding || hasPlacementTest;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void enrollStudent(Enrollment enrollment) throws Exception {
        if (!isEligibleForClass(enrollment.getStudent().getId(), enrollment.getCourseClass().getId())) {
             throw new Exception("Student is not eligible for this class due to prerequisite requirements.");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(enrollment);
            
            String hql = "FROM Payment p WHERE p.student.id = :studentId AND p.enrollment IS NULL";
            List<Payment> payments = session.createQuery(hql, Payment.class)
                .setParameter("studentId", enrollment.getStudent().getId())
                .list();
                
            if (!payments.isEmpty()) {
                Payment paymentToLink = payments.get(0);
                paymentToLink.setEnrollment(enrollment);
                session.merge(paymentToLink);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e; 
        }
    }

    public void updateEnrollment(Enrollment enrollment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(enrollment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteEnrollment(int id) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null) {
                // 1. Nullify enrollment in Payment
                session.createMutationQuery("UPDATE Payment p SET p.enrollment = NULL WHERE p.enrollment.id = :enrollmentId")
                        .setParameter("enrollmentId", id)
                        .executeUpdate();

                // 2. Delete attendance records for this student and class
                int studentId = enrollment.getStudent().getId();
                int classId = enrollment.getCourseClass().getId();
                session.createMutationQuery("DELETE FROM Attendance a WHERE a.student.id = :studentId AND a.courseClass.id = :classId")
                        .setParameter("studentId", studentId)
                        .setParameter("classId", classId)
                        .executeUpdate();

                // 3. Remove the enrollment
                session.remove(enrollment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e;
        }
    }

    public Enrollment getEnrollmentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Enrollment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Enrollment> getEnrollmentsByClassId(int classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Enrollment e WHERE e.courseClass.id = :classId", Enrollment.class)
                    .setParameter("classId", classId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteEnrollmentsByStudentId(int studentId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // First, delete relevant attendance records
            session.createMutationQuery("DELETE FROM Attendance a WHERE a.student.id = :studentId")
                    .setParameter("studentId", studentId)
                    .executeUpdate();

            // Then, nullify the enrollment reference in Payment history
            session.createMutationQuery("UPDATE Payment p SET p.enrollment = NULL WHERE p.student.id = :studentId")
                    .setParameter("studentId", studentId)
                    .executeUpdate();
            
            // Finally delete the enrollments
            session.createMutationQuery("DELETE FROM Enrollment e WHERE e.student.id = :studentId")
                    .setParameter("studentId", studentId)
                    .executeUpdate();
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
