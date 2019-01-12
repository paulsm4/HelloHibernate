package com.example.hellohibernate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.example.hellohibernate.entities.Task;
import com.example.hellohibernate.entities.Task.Priority;
import com.example.hellohibernate.entities.Task.Status;
import com.example.hellohibernate.entities.TaskUpdate;

public class TestApp {

	private static SessionFactory sessionFactory;
	
	/**
     * Create SessionFactory Object: Hibernate 4.x and higher
     */
	protected static SessionFactory buildSessionFactory() {
	    // Creating Configuration Instance & Passing Hibernate Configuration File
	    Configuration configuration = new Configuration()
	    	.configure("hibernate.cfg.xml")
	    	.addAnnotatedClass(Task.class)
	    	.addAnnotatedClass(TaskUpdate.class);
	    
	    // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
	    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build(); 

	    // Create singleton Hibernate SessionFactory instance
	    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	    return sessionFactory;
	}
	
	public void closeSessionFactory () {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}
	
	public int addTask (String summary, Priority priority) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Task task = new Task(summary, priority, Status.PENDING);
			Integer id = (Integer)session.save(task);
			tx.commit();
			return (int)id;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	public Task listTask (int taskId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("FROM Task WHERE id= :taskId");
			query.setParameter("taskId", taskId);
			Task task = (Task)query.getResultList();
			return task;		
		} finally {
			if (session != null)
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Task> listTasks () {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query q = session.createQuery("from Task");
			List<Task> tasks = (List<Task>)q.getResultList();
			return tasks;
		} finally {
			if (session != null)
			session.close();
		}
	}

	public int updateTask (int taskId, String updateText, Priority newPriority, Status newStatus) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			TaskUpdate update = new TaskUpdate(taskId, updateText);
			Integer id = (Integer)session.save(update);
			tx.commit();
			return (int)id;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * Test driver
	 */
	public static void main(String[] args) {
		System.out.println(">>TestApp::main()...");
		try {
			TestApp testApp = new TestApp();

			System.out.println("Initializing Hibernate...");
			testApp.sessionFactory = buildSessionFactory();

			System.out.println("Creating data...");
			int taskId = testApp.addTask("Test task", Priority.LOW);
			int uid1 = testApp.updateTask(taskId, "This is an update", Priority.NORMAL, Status.INPROGRESS);

			System.out.println("Querying data...");
			List<Task> tasks = testApp.listTasks();
			SimpleDateFormat sdf = new SimpleDateFormat ("MM/DD/YY hh:mm:ss");
			for (Task t : tasks) {
				//Object d1 = t.getDate();
				//Date d2 = t.getDate();
				//System.out.println(sdf.format(d2));
				System.out.println("Task id: " + t.getId()
				+ ": " + t.getSummary()
				+ ", priority: " + t.getPriority()
				+ ", status: " + t.getStatus());
				for (TaskUpdate u : t.getUpdates()) {
					Date d = u.getDate();
					String s = sdf.format(d);
					System.out.println("  uid: " + u.getId()
					+ ", " + s
					+ ": " + u.getText());
				}
			}

			System.out.println("Closing session...");
			testApp.closeSessionFactory();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("<<TestApp: Done.");
	}

}
