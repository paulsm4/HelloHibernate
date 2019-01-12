package com.example.hellohibernate;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.hellohibernate.entities.Task;
import com.example.hellohibernate.entities.Task.Priority;
import com.example.hellohibernate.entities.Task.Status;

public class TestTask {

	@BeforeClass
	public static void beforeClass() throws Exception {
		HibernateUtil.initSessionFactory();
	}
	
	@AfterClass
	public static void afterAll() {
		HibernateUtil.shutdown();
	}

	@Test
	public void testNewTask() {
		System.out.println("TestTask::testNewTask()");
		Task task = new Task("CCC", Priority.LOW, Status.PENDING);
		assertNotNull(task);

		System.out.println("  task: id=" + task.getId() + ", summary=" + task.getSummary() + ", priority=" + task.getPriority() + ", status=" + task.getStatus());
		assert(task.getId() == 0);
		assert(task.getSummary() == "CCC");
		assert(task.getPriority().equals(Priority.LOW.toString()));
		assert(task.getStatus().equals(Status.PENDING.toString()));
		assert(task.getDate() == null);
	}

	@Test
	public void testSaveTask() {
		System.out.println("TestTask::testSaveTask()");
		Task task = new Task("DDD", Priority.NORMAL, Status.PENDING);
		assertNotNull(task);
		
		System.out.println("  task: id=" + task.getId() + ", summary=" + task.getSummary() + ", priority=" + task.getPriority());
		assert(task.getId() == 0);
		assert(task.getSummary() == "DDD");
		assert(task.getPriority().equals(Priority.NORMAL.toString()));
		assert(task.getStatus().equals(Status.PENDING.toString()));
		
		Session session = HibernateUtil.openSession();
		assertNotNull(session);
		int id = (int)session.save(task);
		System.out.println("  session.save() id=" + id + ", task.getId()=" + task.getId());
		assert(id == task.getId());
		session.close();
	}

	@Test
	public void testQueryTask() {
		System.out.println("TestTask::testQueryTask()");
		Task task = new Task("EEE", Priority.NORMAL, Status.PENDING);
		assertNotNull(task);
		
		System.out.println("  task: id=" + task.getId() + ", summary=" + task.getSummary() + ", priority=" + task.getPriority());
		assert(task.getId() == 0);
		assert(task.getSummary() == "EEE");
		assert(task.getPriority().equals(Priority.NORMAL.toString()));
		assert(task.getStatus().equals(Status.PENDING.toString()));
		
		Session session = HibernateUtil.openSession();
		assertNotNull(session);
		int id = (int)session.save(task);
		assert(id == task.getId());
		
		Query query = session.createQuery("FROM Task WHERE id= :taskId");
		query.setParameter("taskId", id);
		List<Task> result = (List<Task>)query.getResultList();
		assert(result.size() == 1);

		Task t = result.get(0);
		System.out.println("  result: id=" + t.getId() + ", summary=" + t.getSummary() + ", priority=" + t.getPriority() + ", date=" + t.getDate());
		assert(t.getId() == id);
		assert(t.getSummary() == "EEE");
		assert(t.getPriority().equals(Priority.NORMAL.toString()));
		assert(t.getStatus().equals(Status.PENDING.toString()));
		session.close();
	}

	@Test
	public void testListTasks() {
		System.out.println("TestTask::testListTasks()");
		
		Session session = HibernateUtil.openSession();
		assertNotNull(session);

		Query query = session.createQuery("FROM Task");
		List<Task> result = (List<Task>)query.getResultList();
		// H2 init script inserts two test records
		System.out.println("  #/records=" + result.size());
		assert(result.size() >= 2);
		
		for (Task t : result) {
			System.out.println("  result: id=" + t.getId() + ", summary=" + t.getSummary() + ", priority=" + t.getPriority() + ", date=" + t.getDate());
			assertNotNull(t.getId());
			assertNotNull(t.getSummary());
			assertNotNull(t.getPriority());
			assertNotNull(t.getStatus());			
		}
		session.close();
	}
	
}
