package com.languagecenter.dao;

import com.languagecenter.entity.UserAccount;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserAccountDAO {

    private static UserAccountDAO instance;

    private UserAccountDAO() {}

    public static UserAccountDAO getInstance() {
        if (instance == null) {
            instance = new UserAccountDAO();
        }
        return instance;
    }


    public UserAccount verifyCredentials(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<UserAccount> query = session.createQuery(
                    "FROM UserAccount WHERE username = :username AND password = :password", UserAccount.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUserAccount(UserAccount user) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e;
        }
    }
}
