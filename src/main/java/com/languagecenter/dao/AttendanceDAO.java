package com.languagecenter.dao;

import com.languagecenter.entity.Attendance;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class AttendanceDAO {

    private static AttendanceDAO instance;

    private AttendanceDAO() {}

    public static AttendanceDAO getInstance() {
        if (instance == null) {
            instance = new AttendanceDAO();
        }
        return instance;
    }


    public List<Attendance> getAllAttendances() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Attendance", Attendance.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addAttendance(Attendance attendance) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(attendance);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateAttendance(Attendance attendance) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(attendance);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteAttendance(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Attendance attendance = session.get(Attendance.class, id);
            if (attendance != null) {
                session.remove(attendance);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Attendance getAttendanceById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Attendance.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Attendance> getAttendancesByClassAndDate(int classId, java.time.LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Attendance a WHERE a.courseClass.id = :classId AND a.attendanceDate = :date", Attendance.class)
                    .setParameter("classId", classId)
                    .setParameter("date", date)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
