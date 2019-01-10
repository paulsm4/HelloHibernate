package com.example.hellohibernate;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import java.util.Iterator; 
 
import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;

import org.hibernate.SQLQuery;

/**
 * SAMPLE OUTPUT:
 * >>Added test employees empID1=33, EMPID2=34
 * >>Deprecated SQLQuery/createSQLQuery API:
 *   Scalar query: Name: Simon, Salary: 5000
 *   Scalar query: Name: Garfunkel, Salary: 4000
 *   Entity query: Name: Simon Paul, Salary: 5000
 *   Entity query: Name: Garfunkel Art, Salary: 4000
 * >>Updated Query/createNativeQuery API:
 *   Scalar query: Name: Simon, Salary: 5000
 *   Scalar query: Name: Garfunkel, Salary: 4000
 *   Entity query: Name: Simon Paul, Salary: 5000
 *   Entity query: Name: Garfunkel Art, Salary: 4000
 */
public class ManageEmployee {
   private static SessionFactory factory; 
   
   /* Method to CREATE an employee in the database */
   public Integer addEmployee(String fname, String lname, int salary){
      Session session = factory.openSession();
      Transaction tx = null;
      Integer employeeID = null;
      
      try {
         tx = session.beginTransaction();
         Employee employee = new Employee(fname, lname, salary);
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

   /* Method to  READ all the employees using Scalar Query */
   public void listEmployeesScalar( ){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         String sql = "SELECT first_name, salary FROM EMPLOYEE";
         SQLQuery query = session.createSQLQuery(sql);  // SQLQuery: DEPRECATED
         query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);  // query.setResultTransformer(): DEPRECATED
         List data = query.list(); // query.list(): DEPRECATED

         for(Object object : data) {
            Map row = (Map)object;
            System.out.println("  Scalar query: Name: " + row.get("first_name") + ", Salary: " + row.get("salary")); 
         }
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }

   @SuppressWarnings("unchecked")
   public void listEmployeesScalar2( ){
	   Session session = factory.openSession();
	   try {
	      String sql = "SELECT first_name, salary FROM EMPLOYEE";
	      Query query = session.createNativeQuery(sql);
	      List<Object[]> result =  query.getResultList();
	      for(Object[] row : result) {
	         // System.out.println("row instanceof Object[]=" + (row instanceof Object[]) + ", #/items=" + row.length);  // DEBUG
	         System.out.println("  Scalar query: Name: " + row[0] + ", Salary: " + row[1]); 
	      }
	   } catch (HibernateException e) {
	      e.printStackTrace(); 
	   } finally {
	      session.close(); 
	   }
	}
   
   /* Method to READ all the employees using Entity Query */
   public void listEmployeesEntity( ){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         String sql = "SELECT * FROM EMPLOYEE";
         SQLQuery query = session.createSQLQuery(sql);  // SQLQuery: DEPRECATED
         query.addEntity(Employee.class);  // query.addEntity(): DEPRECATED
         List employees = query.list();  // query.list(): DEPRECATED

         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.println("  Entity query: Name: " + employee.getFirstName() + " " +
           		employee.getLastName() + ", Salary: " + employee.getSalary()); 
         }
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }
   
   @SuppressWarnings("unchecked")
   public void listEmployeesEntity2( ){
      Session session = factory.openSession();    
      try {
         String sql = "SELECT * FROM EMPLOYEE";
         Query query = session.createSQLQuery(sql).addEntity(Employee.class);
         List<Employee> employees = query.getResultList();
         for (Iterator<Employee> iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.println("  Entity query: Name: " + employee.getFirstName() + " " +
           		employee.getLastName() + ", Salary: " + employee.getSalary()); 
         }
      } catch (HibernateException e) {
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }

   /**
   * test driver
   */
   public static void main(String[] args) {
	      
      try {
         factory = new Configuration().configure().buildSessionFactory();
      } catch (Throwable ex) { 
         System.err.println("Failed to create sessionFactory object." + ex);
         throw new ExceptionInInitializerError(ex); 
      }
	      
      ManageEmployee ME = new ManageEmployee();
      
      /* Add few employee records in database */
      Integer empID1 = ME.addEmployee("Simon", "Paul", 5000);
      Integer empID2 = ME.addEmployee("Garfunkel", "Art", 4000);
      System.out.println(">>Added test employees empID1=" + empID1 + ", EMPID2=" + empID2);
      /* Deprecated Scalar and Entity Query */
      System.out.println(">>Deprecated SQLQuery/createSQLQuery API:");
      ME.listEmployeesScalar();
      ME.listEmployeesEntity();
      
      /* Updated Scalar and Entity Query */
      System.out.println(">>Updated Query/createNativeQuery API:");
      ME.listEmployeesScalar2();
      ME.listEmployeesEntity2();
      
   }
   
}