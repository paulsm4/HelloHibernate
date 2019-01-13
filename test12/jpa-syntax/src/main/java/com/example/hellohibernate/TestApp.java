package com.example.hellohibernate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.example.hellohibernate.entities.Task;
import com.example.hellohibernate.entities.Task.Priority;
import com.example.hellohibernate.entities.Task.Status;
import com.example.hellohibernate.entities.TaskUpdate;

public class TestApp {

	public static final String PERSISTENCE_UNIT = "com.example.hellohibernate.jpa";

	private static EntityManagerFactory entityManagerFactory;

	protected static EntityManagerFactory createEntityManagerFactory() {
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		return entityManagerFactory;
	}

	public void closeEntityManagerFactory() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
	}

	public int addTask(String summary, Priority priority) {
		EntityManager em = null;
		try {
			em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Task task = new Task(summary, priority, Status.PENDING);
			em.persist(task);
			em.flush();
			Integer id = (Integer) task.getId();
			em.getTransaction().commit();
			return (int) id;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em != null)
				em.close();
		}
	}

	public Task listTask(int taskId) {
		EntityManager em = null;
		try {
			em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Query query = em.createQuery("FROM Task WHERE id= :taskId");
			query.setParameter("taskId", taskId);
			Task task = (Task) query.getResultList();
			em.getTransaction().commit();
			return task;
		} finally {
			if (em != null)
				em.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Task> listTasks() {
		EntityManager em = null;
		try {
			em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Query q = em.createQuery("from Task");
			List<Task> tasks = (List<Task>) q.getResultList();
			em.getTransaction().commit();
			return tasks;
		} finally {
			if (em != null)
				em.close();
		}
	}

	public int updateTask(int taskId, String updateText) {
		EntityManager em = null;
		try {
			em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			TaskUpdate update = new TaskUpdate(taskId, updateText);
			em.persist(update);
			em.flush();
			Integer id = (Integer) update.getId();
			em.getTransaction().commit();
			return (int) id;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em != null)
				em.close();
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
			entityManagerFactory = createEntityManagerFactory();

			System.out.println("Creating data...");
			int tid1= testApp.addTask("Test task", Priority.LOW);
			int uid1 = testApp.updateTask(tid1, "This is an update");
			int tid2= testApp.addTask("Test task two", Priority.LOW);
			testApp.updateTask(tid2, "This, too, is an update");
			testApp.updateTask(tid2, "Another update");

			System.out.println("Querying data...");
			List<Task> tasks = testApp.listTasks();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/YY hh:mm:ss");
			for (Task t : tasks) {
				Date d = t.getDate();
				String s = (d == null) ? "null" : sdf.format(d);
				System.out.println("Task id: " + t.getId() + ": " + t.getSummary() + 
						", priority: " + t.getPriority()
						+ ", status: " + t.getStatus());
				for (TaskUpdate u : t.getUpdates()) {
					d = u.getDate();
					s = (d == null) ? "null" : sdf.format(d);
					System.out.println("  uid: " + u.getId() + ", " + s + ": " + u.getText());
				}
			}

			System.out.println("Closing em...");
			testApp.closeEntityManagerFactory();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("<<TestApp: Done.");
	}

}
