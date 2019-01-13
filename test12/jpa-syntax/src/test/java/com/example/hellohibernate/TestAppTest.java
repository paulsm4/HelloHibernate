/*
 * Use Mockito => NO-GO
 * 
 * EXAMPLE RUN:
 *   java.lang.NullPointerException
 *     at com.example.hellohibernate.TestApp.addTask(TestApp.java:47)  <-- try/catch handler
 *     at com.example.hellohibernate.TestAppTest.happyPathScenario(TestAppTest.java:42)
 *     at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 *     ...
 * Console:
 *   TestAppTest::happyPathScenario()
 *   WARNING: An illegal reflective access operation has occurred
 *   WARNING: Illegal reflective access by org.mockito.cglib.core.ReflectUtils$2 (file:/home/paulsm/.m2/repository/org/mockito/mockito-all/1.9.5/mockito-all-1.9.5.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
 *   WARNING: Please consider reporting this to the maintainers of org.mockito.cglib.core.ReflectUtils$2
 *   WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
 *   WARNING: All illegal access operations will be denied in a future release
 */
package com.example.hellohibernate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Test;

import com.example.hellohibernate.entities.Task;
import com.example.hellohibernate.entities.Task.Priority;
import com.example.hellohibernate.entities.Task.Status;

public class TestAppTest {
	
	@Test
	public void happyPathScenario () {
		System.out.println("TestAppTest::happyPathScenario()");
		EntityManagerFactory emf = mock(EntityManagerFactory.class);
		EntityManager em = mock(EntityManager.class);
		when(emf.createEntityManager()).thenReturn(em);

		Task t = new Task("AAA", Priority.LOW, Status.PENDING);
		assertNotNull(t);
		
		TestApp app = new TestApp();
		assertNotNull(app);
		
		int tid = app.addTask("AAA", Priority.LOW);
		assert(tid == 0);
		
		
	}

}
