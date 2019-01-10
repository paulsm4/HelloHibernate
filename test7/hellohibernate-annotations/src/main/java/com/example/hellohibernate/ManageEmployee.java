package com.example.hellohibernate;

import java.util.List;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class ManageEmployee {
	private static SessionFactory factory;
	
	/**
     * Create SessionFactory Object: Hibernate 4.x and higher
     * REFERENCE: https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-load-example/
     */
	protected static SessionFactory buildSessionFactory() {
	    // Creating Configuration Instance & Passing Hibernate Configuration File
	    Configuration configuration = new Configuration().
	    	configure("hibernate.cfg.xml").
	    	addAnnotatedClass(Employee.class);
	    
	    // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
	    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build(); 

	    // Creating Hibernate SessionFactory Instance
	    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	    return sessionFactory;
	}
	
	public static void main(String[] args) {
	   
	   try {
		   /* NOTE: org.hibernate.cfg.AnnotationConfiguration is deprecated, as of Hibernate 3.6
		    * factory = new AnnotationConfiguration().
		    *   configure().
		    *   //addPackage("com.xyz") //add package if used.
		    *   addAnnotatedClass(Employee.class).
		    *   buildSessionFactory();		   
		    */
		   factory = buildSessionFactory ();
		} catch (Throwable ex) { 
	      System.err.println("Failed to create sessionFactory object." + ex);
	      throw new ExceptionInInitializerError(ex); 
	   }
	   
	   ManageEmployee ME = new ManageEmployee();
	
	   /* Add few employee records in database */
	   Integer empID1 = ME.addEmployee("Zara", "Ali", 1000);
	   Integer empID2 = ME.addEmployee("Daisy", "Das", 5000);
	   Integer empID3 = ME.addEmployee("John", "Paul", 10000);
	
	   /* List down all the employees */
	   ME.listEmployees();
	
	   /* Update employee's records */
	   ME.updateEmployee(empID1, 5000);
	
	   /* Delete an employee from the database */
	   ME.deleteEmployee(empID2);
	
	   /* List down new list of the employees */
	   ME.listEmployees();
	}
	
	/* Method to CREATE an employee in the database */
	public Integer addEmployee(String fname, String lname, int salary){
	   Session session = factory.openSession();
	   Transaction tx = null;
	   Integer employeeID = null;
	   
	   try {
	      tx = session.beginTransaction();
	      Employee employee = new Employee();
	      employee.setFirstName(fname);
	      employee.setLastName(lname);
	      employee.setSalary(salary);
	      employeeID = (Integer) session.save(employee); 
	      tx.commit();
	   } catch (HibernateException e) {
	      if (tx!=null) tx.rollback();
	      e.printStackTrace(); 
	   } finally {
	      session.close(); 
	   }
	   return employeeID;
	}
	
	/* Method to  READ all the employees */
	public void listEmployees( ){
	   Session session = factory.openSession();
	   Transaction tx = null;
	   
	   try {
	      tx = session.beginTransaction();
	      List employees = session.createQuery("FROM Employee").list(); 
	      for (Iterator iterator = employees.iterator(); iterator.hasNext();){
	         Employee employee = (Employee) iterator.next(); 
	         System.out.print("First Name: " + employee.getFirstName()); 
	         System.out.print("  Last Name: " + employee.getLastName()); 
	         System.out.println("  Salary: " + employee.getSalary()); 
	      }
	      tx.commit();
	   } catch (HibernateException e) {
	      if (tx!=null) tx.rollback();
	      e.printStackTrace(); 
	   } finally {
	      session.close(); 
	   }
	}
	
	/* Method to UPDATE salary for an employee */
	public void updateEmployee(Integer EmployeeID, int salary ){
	   Session session = factory.openSession();
	   Transaction tx = null;
	   
	   try {
	      tx = session.beginTransaction();
	      Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
	      employee.setSalary( salary );
			 session.update(employee); 
	      tx.commit();
	   } catch (HibernateException e) {
	      if (tx!=null) tx.rollback();
	      e.printStackTrace(); 
	   } finally {
	      session.close(); 
	   }
	}
	
	/* Method to DELETE an employee from the records */
	public void deleteEmployee(Integer EmployeeID){
	   Session session = factory.openSession();
	   Transaction tx = null;
	   
	   try {
	      tx = session.beginTransaction();
	      Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
	      session.delete(employee); 
	      tx.commit();
	   } catch (HibernateException e) {
	      if (tx!=null) tx.rollback();
	      e.printStackTrace(); 
	   } finally {
	      session.close(); 
	   }
	}
}