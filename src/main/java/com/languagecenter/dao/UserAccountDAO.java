package com.languagecenter.dao;

import com.languagecenter.entity.UserAccount;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserAccountDAO {

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
}
