package com.example.hellohibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.example.hellohibernate.entities.Task;
import com.example.hellohibernate.entities.TaskUpdate;

public class HibernateUtil {
	
	protected static SessionFactory sessionFactory;

	/**
     * Create SessionFactory Object: Hibernate 4.x and higher
     */
	public static void initSessionFactory() throws Exception {
		System.out.println("HibernateUtil::initSessionFactory()...");
		if (sessionFactory == null) {
			// Creating Configuration Instance & Passing Hibernate Configuration File
			Configuration configuration = new Configuration()
				.configure("hibernate.cfg.xml")
				.addAnnotatedClass(Task.class)
				.addAnnotatedClass(TaskUpdate.class);
	    
			// Since Hibernate Version 4.x, ServiceRegistry Is Being Used
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build(); 

			// Create singleton Hibernate SessionFactory instance
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
	}

	public static void shutdown() {
		System.out.println("HibernateUtil::shutdown()...");
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}

	public static SessionFactory getSessionFactory () { return sessionFactory; }
	
	public static Session openSession() { return sessionFactory.openSession(); }
		
}