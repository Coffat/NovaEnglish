package com.languagecenter.dao;

import com.languagecenter.entity.Schedule;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class ScheduleDAO {

    private static ScheduleDAO instance;

    private ScheduleDAO() {}

    public static ScheduleDAO getInstance() {
        if (instance == null) {
            instance = new ScheduleDAO();
        }
        return instance;
    }


    public List<Schedule> getAllSchedules() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Schedule", Schedule.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addSchedule(Schedule schedule) {
        if (schedule == null) return;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(schedule);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi khi thêm lịch: " + e.getMessage(), e);
        }
    }

    public void updateSchedule(Schedule schedule) {
        if (schedule == null) return;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(schedule);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteSchedule(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Schedule schedule = session.get(Schedule.class, id);
            if (schedule != null) {
                session.remove(schedule);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Schedule getScheduleById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Schedule.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void batchSaveSchedules(List<Schedule> schedules) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (Schedule s : schedules) {
                session.persist(s);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteSchedulesByClassId(int classId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Schedule WHERE courseClass.id = :classId")
                   .setParameter("classId", classId)
                   .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi khi xóa lịch: " + e.getMessage(), e);
        }
    }

    public void replaceSchedulesForClass(int classId, List<Schedule> newSchedules) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Delete existing
            session.createMutationQuery("DELETE FROM Schedule WHERE courseClass.id = :classId")
                   .setParameter("classId", classId)
                   .executeUpdate();
            
            // Save new
            int count = 0;
            for (Schedule s : newSchedules) {
                session.persist(s);
                if (++count % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi khi thay đổi toàn bộ lịch: " + e.getMessage(), e);
        }
    }

    public long countHeldSessions(int classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(s) FROM Schedule s WHERE s.courseClass.id = :classId AND s.scheduleDate <= CURRENT_DATE", Long.class)
                    .setParameter("classId", classId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
