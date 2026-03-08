package com.languagecenter.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    /**
     * Build the SessionFactory exactly once (Thread-safe implicitly via static state initialization, 
     * but synchronized to ensure thread safety during multiple fast concurrent accesses).
     */
    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry from hibernate.cfg.xml
                registry = new StandardServiceRegistryBuilder()
                        .configure()
                        .build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Build SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
