/**
 * Use AssertAnnotations/ReflectTool => NO-GO
 * 
 * REFERENCE:
 * https://dzone.com/articles/unit-testing-jpastop-integration-testing
 *
 * Console:
 *   TaskTest::tableTest()    PASS
 *   TaskTest::entityTest()   PASS
 *   TaskTest::idTest()       FAIL: GeneratedValue a = ReflectTool.getMethodAnnotation() return null => NPE
 *   TaskTest::summaryTest()  FAIL: Column c = ReflectTool.getMethodAnnotation() returns null => NPE
 *   TaskTest::methodsTest()  FAIL: AssertionError: Expected 2 annotations, but found 0
 *   TaskTest::typeTest()     PASS
 *   TaskTest::updatesTest()  FAIL: OneToMany a = ReflectTool.getMethodAnnotation() returns null => NPE 
 *   TaskTest::fieldsTest()   FAIL: AssertionError: Expected 0 annotations, but found 3
 */
package com.example.hellohibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.junit.Assert;
import org.junit.Test;
import com.example.hellohibernate.entities.Task;

public class TaskTest {

	@Test
	public void typeTest() {
		System.out.println("TaskTest::typeTest()");
		AssertAnnotations.assertType(Task.class, Entity.class, Table.class);
	}
	
	  @Test
	  public void fieldsTest() {
		  System.out.println("TaskTest::fieldsTest()");
		  AssertAnnotations.assertField(Task.class, "id");
		  AssertAnnotations.assertField(Task.class, "summary");
		  AssertAnnotations.assertField(Task.class, "priority");
		  AssertAnnotations.assertField(Task.class, "status");
		  AssertAnnotations.assertField(Task.class, "date");
		  AssertAnnotations.assertField(Task.class, "updates");
	  }
	
	  @Test
	  public void methodsTest() {
		  System.out.println("TaskTest::methodsTest()");
		  AssertAnnotations.assertMethod(Task.class, "getId", Id.class, GeneratedValue.class);
		  AssertAnnotations.assertMethod(Task.class, "getSummary", Column.class);
		  AssertAnnotations.assertMethod(Task.class, "getPriority", Column.class);
		  AssertAnnotations.assertMethod(Task.class, "getStatus", Column.class);
		  AssertAnnotations.assertMethod(Task.class, "getDate", Column.class);
		  AssertAnnotations.assertMethod(Task.class, "getUpdates", OneToMany.class);
	  }

	  @Test
	  public void entityTest() {
		  System.out.println("TaskTest::entityTest()");
		  Entity a = ReflectTool.getClassAnnotation(Task.class, Entity.class);
		  Assert.assertEquals("", a.name());
	  }

	  @Test
	  public void tableTest() {
		  System.out.println("TaskTest::tableTest()");
		  Table t = ReflectTool.getClassAnnotation(Task.class, Table.class);
		  Assert.assertEquals("task", t.name());
	  }

	  @Test
	  public void idTest() {
		  System.out.println("TaskTest::idTest()");
		  GeneratedValue a = ReflectTool.getMethodAnnotation(Task.class, "getId", GeneratedValue.class);
		  Assert.assertEquals("", a.generator());
		  Assert.assertEquals(GenerationType.AUTO, a.strategy());
	  }

	  @Test
	  public void summaryTest() {
		  System.out.println("TaskTest::summaryTest()");
		  Column c = ReflectTool.getMethodAnnotation(Task.class, "getSummary", Column.class);
		  Assert.assertEquals("Summary", c.name());
	  }

	  @Test
	  public void updatesTest() {
		  System.out.println("TaskTest::updatesTest()");
		  OneToMany a = ReflectTool.getMethodAnnotation(Task.class, "getUpdates", OneToMany.class);
		  Assert.assertEquals("updates", a.mappedBy());
		  Assert.assertEquals(FetchType.LAZY, a.fetch());
	  }

}
