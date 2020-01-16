* Hibernate tutorial:
  https://www.tutorialspoint.com/hibernate

* Official documentation:

  - Java Persistence is the API for the management for persistence and object/relational mapping.
    https://docs.oracle.com/javaee/7/tutorial/partpersist.htm
    https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html

  - Hibernate ORM
    https://hibernate.org/orm/documentation/5.4/

* Background:

===================================================================================================

* test1: Simple Hibernate app:

  1. Download Hibernate (https://hibernate.org) and MySql/Connector (https://dev.mysql.com/downloads)

  2. Create hibernate.cfg.xml, Employee.hbm.xml

  3. Create Employee.java (POJO) and ManageEmployee.java 

  4. Create test database and table:

     mysql -u*** -p*** mysql
      create database test;
      use test;
create table EMPLOYEE (
   id INT NOT NULL auto_increment,
   first_name VARCHAR(20) default NULL,
   last_name  VARCHAR(20) default NULL,
   salary     INT  default NULL,
   PRIMARY KEY (id)
);
   <= Created manually
      ALT: Could specify "<property name="hibernate.hbm2ddl.auto">create</property>" in hibernate.cnf.xml

  5. Build and execute ManageEmployee test app:
     - mk.sh =>
HB=/opt/hibernate/lib
CP=$HB/required/antlr-2.7.7.jar:\
$HB/required/byte-buddy-1.9.5.jar:\
...
$HB/required/hibernate-core-5.4.0.Final.jar:\
...
$HB/required/stax-ex-1.8.jar:\
$HB/required/txw2-2.3.1.jar
MYSQLJAR=/usr/share/java/mysql-connector-java-8.0.13.jar
BIN=bin

if [ ! -d $BIN ]; then
  mkdir $BIN
  cp hibernate.cfg.xml Employee.hbm.xml $BIN
fi
echo Compiling...
javac -d $BIN -verbose -cp $CP *.java 
echo Executing...
java -cp $BIN:$MYSQLJAR:$CP ManageEmployee
echo Done.

  - SAMPLE OUTPUT:
...
First Name: Zara  Last Name: Ali  Salary: 5000
First Name: John  Last Name: Paul  Salary: 10000
Done.

===================================================================================================

* test2: Same app, using Eclipse/Maven
  - pom.xml:
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>hellohibernate</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>hellohibernate</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
  </properties>

  <dependencies>
    <!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-core -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.4.0.Final</version>
    </dependency>  
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
   <= Set mysql-connector-java in Debug/Run classpath (vs. pom.xml)

===================================================================================================

* test3 (O/R Mapping) many-to-one:

  1. Cloned test2 => test3-many-to-one

  2. Added MySql to pom.xml:
<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
</dependency>

  3. Created new "Address.java" POJO, "ADDRESS" Mysql table:
     - Address.java:
       ------------
public class Address{
   private int id;
   private String street;     
   private String city;     
   private String state;    
   private String zipcode; 

   public Address() {}
   
   public Address(String street, String city, String state, String zipcode) {
      this.street = street; 
      this.city = city; 
      this.state = state; 
      this.zipcode = zipcode; 
   } 
   <= getXXX(), setXXX()

     - MySql DDL:
       ---------
create table ADDRESS (
   id INT NOT NULL auto_increment,
   street_name VARCHAR(40) default NULL,
   city_name VARCHAR(40) default NULL,
   state_name VARCHAR(40) default NULL,
   zipcode VARCHAR(10) default NULL,
   PRIMARY KEY (id)
);

  4. Modified Employee.java, dropped and re-created EMPLOYEE table:
     <= Added "private Address address;", getAddress(), setAddress()
     - MySql DDL:
       ---------
create table EMPLOYEE (
   id INT NOT NULL auto_increment,
   first_name VARCHAR(20) default NULL,
   last_name  VARCHAR(20) default NULL,
   salary     INT  default NULL,
   address    INT NOT NULL,
   PRIMARY KEY (id)
);

  5. Renamed "Employee.hbm.xml" => "HelloHibernate.hbm.xml"; added both Employee and Address mappings
     <= "Employee" mapping includes:
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD//EN" "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd"> 

<hibernate-mapping>
   <class name = "com.example.hellohibernate.Employee" table = "EMPLOYEE">
      ...
      <id name = "id" type = "int" column = "id">
         <generator class="native"/>
      </id>      
      <property name = "firstName" column = "first_name" type = "string"/>
      <property name = "lastName" column = "last_name" type = "string"/>
      <property name = "salary" column = "salary" type = "int"/>
      <many-to-one 
         name = "address" 
         column = "address" 
         class="com.example.hellohibernate.Address" 
         not-null="true"/>
        ...
   <class name = "com.example.hellohibernate.Address" table="ADDRESS">
      ...
      <id name = "id" type = "int" column = "id">
         <generator class="native"/>
      </id>      
      <property name = "street" column = "street_name" type = "string"/>
      <property name = "city" column = "city_name" type = "string"/>
      <property name = "state" column = "state_name" type = "string"/>
      <property name = "zipcode" column = "zipcode" type = "string"/>

  6. Updated ManageEmployees.java to reflect new operations

===================================================================================================

* test4 (O/R Mapping) one-to-one
  1. Copied/imported test3/many-to-one verbatim =>
       test4/one-to-one

  2. Updated "test4/one-to-one/.project" => 
       ...
       <name>one-to-one</name>

  3. {Employees, Address} POJO/MySQL => OK as-is

  4. HelloHibernate.hbm.xml:
     ----------------------
<?xml version = "1.0" encoding = "utf-8"?>
...
<hibernate-mapping>
   <class name = "com.example.hellohibernate.Employee" table = "EMPLOYEE">
        ...
        <!-- If unique="true", then One-to-One; else Many-to-One -->
        <many-to-one 
            name = "address" 
            column = "address" 
            class="com.example.hellohibernate.Address" 
            not-null="true"
            unique="true" />

  5. Mysql:
       delete from EMPLOYEE;
       delete from ADDRESS;

  6. Run ManageEmployee.java =>
     main()
       SessionFactory factory = new Configuration().configure().buildSessionFactory();
       ManageEmployee ME = new ManageEmployee();
       // Note: private static SessionFactory factory; is private static to the driver app
       Address address = ME.addAddress("Kondapur","Hyderabad","AP","532");
         Session session = factory.openSession();
         Transaction tx = session.beginTransaction();
         Address address = new Address(street, city, state, zipcode);
         // Note: "session" is generic; "save()" is overloaded by type
         Integer addressID = (Integer) session.save(address); 
         tx.commit();
         session.close(); 
       Integer empID1 = ME.addEmployee("Manoj", "Kumar", 4000, address);
         Session session = factory.openSession();
         Transaction tx = session.beginTransaction();
         Employee employee = new Employee(fname, lname, salary, address);
         employeeID = (Integer) session.save(employee); 
         tx.commit();
         session.close();
       Integer empID2 = ME.addEmployee("Dilip", "Kumar", 3000, address);
         ...
       ME.listEmployees();
         Session session = factory.openSession();
         Transaction tx = session.beginTransaction();
         List employees = session.createQuery("FROM Employee").list(); 
         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.print("First Name: " + employee.getFirstName()); 
            System.out.print("  Last Name: " + employee.getLastName()); 
            System.out.println("  Salary: " + employee.getSalary());
            Address add = employee.getAddress();
            System.out.println("Address ");
         session.close();
       ME.deleteEmployee(empID2);
         Session session = factory.openSession();
         Transaction tx = session.beginTransaction();
         Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
         session.delete(employee); 
         tx.commit();       
         session.close();


===================================================================================================

* test5 (O/R Mapping) one-to-many
  1. This example uses java.util.Set (specifically, a HashSet)

  2. Copied/imported => test5/one-to-many; updated test5/project =>one-to-many; imported into Eclipse

  3. Drop EMPLOYEE; create CERTIFICATE:
create table EMPLOYEE (
   id INT NOT NULL auto_increment,
   first_name VARCHAR(20) default NULL,
   last_name  VARCHAR(20) default NULL,
   salary     INT  default NULL,
   PRIMARY KEY (id)
);

create table CERTIFICATE (
   id INT NOT NULL auto_increment,
   certificate_name VARCHAR(30) default NULL,
   employee_id INT default NULL,
   PRIMARY KEY (id)
);

  4. Update Employee.java, create Certificate.java
     <= Certificate needs to implement equals() and hashCode()

  5. HelloHibernate.hbm.xml:
...
<hibernate-mapping>
   <class name = "com.example.hellohibernate.Employee" table = "EMPLOYEE">
      <id name = "id" type = "int" column = "id">
         <generator class="native"/>
      </id>      
      <property name = "firstName" column = "first_name" type = "string"/>
      <property name = "lastName" column = "last_name" type = "string"/>
      <property name = "salary" column = "salary" type = "int"/>
      <set name = "certificates" cascade="all">
         <key column = "employee_id"/>
         <one-to-many class="com.example.hellohibernate.Certificate"/>
      </set>
      ...
   <class name = "com.example.hellohibernate.Certificate" table = "CERTIFICATE">
      ...

  6. ManageEmployee.java:
     main()
       SessionFactory factory = new Configuration().configure().buildSessionFactory();
       ManageEmployee ME = new ManageEmployee();
       HashSet set1 = new HashSet();
       set1.add(new Certificate("MCA"));
       set1.add(new Certificate("MBA"));
       set1.add(new Certificate("PMP"));
       Integer empID1 = ME.addEmployee("Manoj", "Kumar", 4000, set1);
          ...
          tx = session.beginTransaction();
          Employee employee = new Employee(fname, lname, salary);
          employee.setCertificates(cert);
          employeeID = (Integer) session.save(employee);
          tx.commit();
       ME.listEmployees();
          ...
            tx = session.beginTransaction();
            List<Employee> employees = session.createQuery("FROM Employee").list();
            for (Iterator<Employee> iterator1 = employees.iterator(); iterator1.hasNext();) {
                ...
                for (Iterator iterator2 = certificates.iterator(); iterator2.hasNext();) {
                    Certificate certName = (Certificate) iterator2.next();
                    System.out.println("Certificate: " + certName.getName());
                }
     - SAMPLE OUTPUT:
First Name: Manoj  Last Name: Kumar  Salary: 5000
Certificate: MCA
Certificate: PMP
Certificate: MBA
       ...

===================================================================================================

* test6 (O/R Mapping) many-to-many
  1. This example also uses java.util.Set

  2. Copied/imported => test6/many-to-many; updated test6/project =>many-to-many; imported into Eclipse

  3. Drop and re-create EMPLOYEE, CERTIFICATE; add table EMP_CERT:
create table EMPLOYEE (
   id INT NOT NULL auto_increment,
   first_name VARCHAR(20) default NULL,
   last_name  VARCHAR(20) default NULL,
   salary     INT  default NULL,
   PRIMARY KEY (id)
);

create table CERTIFICATE (
   id INT NOT NULL auto_increment,
   certificate_name VARCHAR(30) default NULL,
   PRIMARY KEY (id)
);

create table EMP_CERT (
   employee_id INT NOT NULL,
   certificate_id INT NOT NULL,
   PRIMARY KEY (employee_id,certificate_id)
);

  4. Update Employee.java, Certificate.java; do *NOT* need a third class for EMP_CERT
     <= Override equals() and hashCode() in Certificate, as before...

  5. Update HelloHibernate.hbm.xml:
   ...
   <class name = "com.example.hellohibernate.Employee" table = "EMPLOYEE">
      ...
      <set name = "certificates" cascade="save-update" table="EMP_CERT">
         <key column = "employee_id"/>
         <many-to-many column = "certificate_id" class="com.example.hellohibernate.Certificate"/>
      </set>
      <= This relationship uses the third MySQL table, "EMP_CERT"

  5. Update ManageEmployee.java:
     - addEmployee(String fname, String lname, int salary, Set cert){
          ...
          tx = session.beginTransaction();
          Employee employee = new Employee(fname, lname, salary);
          employee.setCertificates(cert);
          employeeID = (Integer) session.save(employee); 
          tx.commit();
   


===================================================================================================

* test7: Annotations
  - Annotations are *required* for EJB 3.0++ conformance; .hbm XML files still give more "control"
  - Annotations are now part of javax.persistence - you do *NOT* need any additional dependencies in pom.xml

  1. Create database:
     mkdb.mysql.sql:
     --------------
drop table if exists EMPLOYEE;
create table EMPLOYEE (
   id INT NOT NULL auto_increment,
   first_name VARCHAR(20) default NULL,
   last_name  VARCHAR(20) default NULL,
   salary     INT  default NULL,
   PRIMARY KEY (id)
);

  2. HelloHibernate.hbm.xml
     <= DELETE.  <hibernate-mapping>, <class..> definitions no longer needed.

  3. hibernate.cfg.xml:
     <= <session-factory> connection info only.
        DELETE <mapping resource> entries

  4. Employee.java => Annotated POJO:
...
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
   @Id @GeneratedValue
   @Column(name = "id")
   private int id;

   @Column(name = "first_name")
   private String firstName;
   ...
   <= Only no-arg constructor, plus all getters/setters...

  5. ManageEmployee
     <= Effectively the same as before
     
  NO-GO: There were SIGNIFICANT changes/deprecations between Hibernate 3.x and 4.x onwards:
  -  The example code uses "factory = new AnnotationConfiguration()...".
     This was *DEPRECATED* as of Hibernate 3.6++; it's *OBSOLETE* in current versions (5.4++).
https://stackoverflow.com/questions/3942880/hibernate-annotationconfiguration-deprecated

  - ".buildSessionFactory()" has been deprecated, in favor of ".buildSessionFactory(ServiceRegistry)" in Hibernate 4.x++
https://stackoverflow.com/questions/23974514/what-is-the-importance-of-standardserviceregistry-in-hibernate-4

  -  @GeneratedValue semantics changed in Hibernate 3.6++
https://stackoverflow.com/questions/20603638/what-is-the-use-of-annotations-id-and-generatedvaluestrategy-generationtype
https://www.baeldung.com/hibernate-identifiers
https://vladmihalcea.com/why-should-not-use-the-auto-jpa-generationtype-with-mysql-and-hibernate/

  6. FINAL CODE:
     - Create database: OK as-is

     - HelloHibernate.hbm.xml: OK as-is: no <hibernate-mapping> or <class> elements

     - Employee.hbm.xml: DELETED

     - Employee.java:
       -------------
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "EMPLOYEE")
public class Employee {
   @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private int id;

   @Column(name = "first_name")
   private String firstName;

   @Column(name = "last_name")
   private String lastName;

   @Column(name = "salary")
   private int salary;  

   public Employee() {}
   <= No-arg constructor + getters/setters.
      Import *ONLY* "javax.persistence" annotations.  Do *NOT* inadvertantly pick up any org.hibernate synonyms.
      @GeneratedValue() should be "IDENTITY". It defaults to "AUTO", which requires a hibernate "sequence" table.

     - ManageEmployee.java:
       -------------------
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

     - SAMPLE OUTPUT:
First Name: Zara  Last Name: Ali  Salary: 1000
First Name: Daisy  Last Name: Das  Salary: 5000
First Name: John  Last Name: Paul  Salary: 10000
First Name: Zara  Last Name: Ali  Salary: 5000
First Name: John  Last Name: Paul  Salary: 10000

===================================================================================================

* Hibernate Query Language (HQL)
  - FROM Clause:
    EX: String hql = "FROM Employee";
         ... OR ...
        String hql = "FROM com.hibernatebook.criteria.Employee";
        Query query = session.createQuery(hql);
        List results = query.list();

  - AS Clause:
    EX: String hql = "FROM Employee AS E";
        ...
        List results = query.list();

  - SELECT Clause:
    EX: String hql = "SELECT E.firstName FROM Employee E";
        ...

  - ORDER BY Clause:
    EX: String hql = "FROM Employee E WHERE E.id > 10 ORDER BY E.salary DESC";

  - GROUP BY Clause:
    EX: String hql = "SELECT SUM(E.salary), E.firtName FROM Employee E " +
                     "GROUP BY E.firstName";

  - Named Parameters:
    EX: String hql = "FROM Employee E WHERE E.id = :employee_id";
        Query query = session.createQuery(hql);
        query.setParameter("employee_id",10);
        List results = query.list();

  - UPDATE Clause:
    EX: String hql = "UPDATE Employee set salary = :salary "  + 
                     "WHERE id = :employee_id";
        Query query = session.createQuery(hql);
        query.setParameter("salary", 1000);
        query.setParameter("employee_id", 10);
        int result = query.executeUpdate();

  - DELETE Clause:
    EX: String hql = "DELETE FROM Employee "  + 
                     "WHERE id = :employee_id";
        Query query = session.createQuery(hql);
        query.setParameter("employee_id", 10);
        int result = query.executeUpdate();

  - INSERT Clause:
    EX: String hql = "INSERT INTO Employee(firstName, lastName, salary)"  + 
                     "SELECT firstName, lastName, salary FROM old_employee";
        Query query = session.createQuery(hql);
        int result = query.executeUpdate();

  - Aggregate Methods:
    - avg(property name)
      count(property name or *)
      max(property name)
      min(property name)
      sum(property name)
      ...
    EX: 

  - Pagination using Query:
    - Query setFirstResult(int startPosition)
        This method takes an integer that represents the first row in your result set, starting with row 0.
    - Query setMaxResults(int maxResult)
        This method tells Hibernate to retrieve a fixed number maxResults of objects.
    EX: String hql = "FROM Employee";
      Query query = session.createQuery(hql);
      query.setFirstResult(1);
      query.setMaxResults(10);
      List results = query.list();

===================================================================================================

* test8:  Hibernate Criteria Queries:
   - Hibernate "Criteria API" allows you to build up a criteria query object programmatically where you can apply filtration rules and logical conditions.
   - Intended as a type-safe alternative to HQL, JPQL and native SQL queries.
   - Criteria API is *DEPRECATED*:
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#appendix-legacy-criteria
     <= "New development should focus on the JPA javax.persistence.criteria.CriteriaQuery API.
         Eventually, Hibernate-specific criteria features will be ported as extensions to the JPA javax.persistence.criteria.CriteriaQuery."

   - JPA/Hibernate Dynamic query fetching example:
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#fetching-strategies-dynamic-fetching
     EX: CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Employee> query = builder.createQuery( Employee.class );
      Root<Employee> root = query.from( Employee.class );
      root.fetch( "projects", JoinType.LEFT);
      query.select(root).where(
          builder.and(
              builder.equal(root.get("username"), username),
              builder.equal(root.get("password"), password)
          )
      );
      Employee employee = entityManager.createQuery(query).getSingleResult();

   - JPA/Hibernate equivalent JPQL Dynamic query:
     EX: Employee employee = entityManager.createQuery(
          "select e " +
          "from Employee e " +
          "left join fetch e.projects " +
          "where " +
          "    e.username = :username and " +
          "    e.password = :password",
          Employee.class)
            .setParameter( "username", username)
            .setParameter( "password", password)
            .getSingleResult();

   - JPA/Hibernate Criteria filtering/pagination:
     EX: List<Customer> customers = AuditReaderFactory
           .get( entityManager )
           .createQuery()
           .forRevisionsOfEntity( Customer.class, true, true )
           .addOrder( AuditEntity.property( "lastName" ).desc() )
           .add( AuditEntity.relatedId( "address" ).eq( 1L ) )
           .setFirstResult( 1 )
           .setMaxResults( 2 )
           .getResultList();

3) Test program (using old .hbm XML descriptors  and deprecated org.hibernate.Criteria API):
   - Employee.java: ordinary POJO; no annotations

   - Employee.hbm.xml:
     ----------------
<?xml version = "1.0" encoding = "utf-8"?>
...
<hibernate-mapping>
   <class name = "com.example.hellohibernate.Employee" table = "EMPLOYEE">
      ...
      <id name = "id" type = "int" column = "id">
         <generator class="native"/>
      </id>
      
      <property name = "firstName" column = "first_name" type = "string"/>
      <property name = "lastName" column = "last_name" type = "string"/>
      <property name = "salary" column = "salary" type = "int"/>
      ...

   - ManageEmployee.java:
     --------------------
   public void listEmployees( ) {
      Session session = factory.openSession();
      Transaction tx = null;
      try {
         tx = session.beginTransaction();
         Criteria cr = session.createCriteria(Employee.class);
         // Add restriction.
         cr.add(Restrictions.gt("salary", 2000));
         List employees = cr.list();
         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.print("First Name: " + employee.getFirstName() + ", Last Name: " + employee.getLastName() + ", Salary: " + employee.getSalary()); 
         }
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }

    - SAMPLE OUTPUT:
First Name: Daisy, Last Name: Das, Salary: 5000
First Name: John, Last Name: Paul, Salary: 5000
First Name: Mohd, Last Name: Yasee, Salary: 3000
Jan 09, 2019 2:46:41 PM org.hibernate.internal.SessionImpl createCriteria
Jan 09, 2019 2:49:16 PM org.hibernate.internal.SessionImpl createCriteria
WARN: HHH90000022: Hibernate's legacy org.hibernate.Criteria API is deprecated; use the JPA javax.persistence.criteria.CriteriaQuery instead
Total Coint: 4
Jan 09, 2019 2:49:16 PM org.hibernate.internal.SessionImpl createCriteria
WARN: HHH90000022: Hibernate's legacy org.hibernate.Criteria API is deprecated; use the JPA javax.persistence.criteria.CriteriaQuery instead
Total Salary: 15000

===================================================================================================

* test9: Native SQL

  - EX (Entity query):
      String sql = "SELECT * FROM EMPLOYEE";
      SQLQuery query = session.createSQLQuery(sql);
      query.addEntity(Employee.class);
      List results = query.list();

  - EX (named parameter): 
      String sql = "SELECT * FROM EMPLOYEE WHERE id = :employee_id";
      SQLQuery query = session.createSQLQuery(sql);
      query.addEntity(Employee.class);
      query.setParameter("employee_id", 10);
      List results = query.list();

  - ManageEmployee.java:
    -------------------
   public void listEmployeesScalar( ){
      Session session = factory.openSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         String sql = "SELECT first_name, salary FROM EMPLOYEE";
         SQLQuery query = session.createSQLQuery(sql); 
         query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
         List data = query.list();

         for(Object object : data) {
            Map row = (Map)object;
            System.out.println("Name: " + row.get("first_name") + ", Salary: " + row.get("salary")); 
         }
         tx.commit();
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
      ...

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
            System.out.println("Name: " + employee.getFirstName() + " " +
                employee.getLastName() + ", Salary: " + employee.getSalary()); 
         }
         tx.commit();
         ...

  - SAMPLE OUTPUT:
Name: Zara, Salary: 2000
Name: Daisy, Salary: 5000
Name: John, Salary: 5000
Name: Mohd, Salary: 3000
Name: Zara Ali, Salary: 2000
Name: Daisy Das, Salary: 5000
Name: John Paul, Salary: 5000
Name: Mohd Yasee, Salary: 3000

  - NOTES:
    - Hibernate SessionFactory vs. EntityManagerFactory:
https://stackoverflow.com/questions/5640778/hibernate-sessionfactory-vs-entitymanagerfactory
    <= In general, prefer EntityManager (JPA) over SessionFactory (Hibernate-specific)

  - Tried alternate query:
    ManageEmployee.java:
    -------------------
   public void listEmployeesScalar2( ){
          Session session = factory.openSession();
          try {
             String sql = "SELECT first_name, salary FROM EMPLOYEE";
             Query query = session.createNativeQuery(sql, Employee.class);
             List<Employee> result =  query.getResultList();
          <= NO-GO:
WARN: SQL Error: 0, SQLState: S0022
Jan 09, 2019 3:30:11 PM org.hibernate.engine.jdbc.spi.SqlExceptionHelper logExceptions
ERROR: Column 'id' not found.

    - Next try:
   @SuppressWarnings("unchecked")
   public void listEmployeesScalar2( ){
       Session session = factory.openSession();
       try {
          String sql = "SELECT first_name, salary FROM EMPLOYEE";
          Query query = session.createNativeQuery(sql);
          List<Object> result =  query.getResultList();
          for(Object o : result) {
             Map row = (Map)o;  // <-- Gets this far
                                       "result" contains 6 items of two elements each (GOOD)...
                                       But "Map row" falls out of loop .. no exception ... but clearly !(o instanceof Map)

    - Prochain fois:
https://stackoverflow.com/questions/13700565/jpa-query-getresultlist-use-in-a-generic-way
      <= Q: How to get a result list without querying the whole darn entity (effectively "Select *")?
         Q: List<Object[]>

    - Winning combination:
   @SuppressWarnings("unchecked")
   public void listEmployeesScalar2( ){
       Session session = factory.openSession();
       try {
          String sql = "SELECT first_name, salary FROM EMPLOYEE";
          Query query = session.createNativeQuery(sql);
          List<Object[]> result =  query.getResultList();
          for(Object[] row : result) {
             System.out.println("row instanceof Object[]=" + (row instanceof Object[]) + ", #/items=" + row.length);
             System.out.println("Name: " + row[0] + ", Salary: " + row[1]); 
          }
       } catch (HibernateException e) {
          e.printStackTrace(); 
       } finally {
          session.close(); 
       }

    - SAMPLE OUTPUT:
row instanceof Object[]=true, #/items=2
Name: Simon, Salary: 5000
row instanceof Object[]=true, #/items=2
Name: Garfunkel, Salary: 4000

    - JBoss documentation:
https://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/querysql.html
    <= Scalar and Entity queries

    - Updated Entity query:
   @SuppressWarnings("unchecked")
   public void listEmployeesEntity2( ){
      Session session = factory.openSession();    
      try {
         String sql = "SELECT * FROM EMPLOYEE";
         Query query = session.createSQLQuery(sql).addEntity(Employee.class);
         List<Employee> employees = query.getResultList();
         for (Iterator<Employee> iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.println("Name: " + employee.getFirstName() + " " +
                employee.getLastName() + ", Salary: " + employee.getSalary()); 
         }
      } catch (HibernateException e) {
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
   }
   <= Included all of:
      - {Deprecated SQLQuery/createSQLQuery, Updated Query/createNativeQuery} APIs
      - {Scalar (returns Map or Object[]), Entity (returns entity class)} queries 

===================================================================================================

* Hibernate - Caching:
  - First-level Cache: the Session cache and is a mandatory cache through which all requests must pass. 
    Hibernate tries to delay doing the update as long as possible to reduce the number of update SQL statements issued.

  - Second level cache: an optional cache
    The first-level cache will always be consulted before any attempt is made to locate an object in the second-level cache.
    The second level cache can be configured on a per-class and per-collection basis and mainly responsible for caching objects across sessions.

  - org.hibernate.cache.CacheProvider interface: Allows integrating third-party cache with Hibernate.

  - Query-level Cache: optional cache for query resultsets; integrates closely with the second-level cache. 
    Only useful for queries that are run frequently with the same parameters.

  - Concurrency Strategies:
    - Transactional: For read-mostly data where it is critical to prevent stale data in concurrent transactions, in the rare case of an update.
    - Read-write: For read-mostly data where it is critical to prevent stale data in concurrent transactions, in the rare case of an update.
    - Nonstrict-read-write: This strategy makes no guarantee of consistency between the cache and the database. 
                            Use this strategy if data hardly ever changes and a small likelihood of stale data is not of critical concern.
    - Read-only: A concurrency strategy suitable for data, which never changes. 
                 Use it for reference data only.

  - Cache Provider:  single cache provider for the whole application:
Strategy/Provider  Read-only Nonstrictread-write Read-write Transactional  Notes:
----------------- --------- -------------------- ---------- -------------  -----
EHCache            X        X                    X                         It can cache in memory or on disk and clustered caching.
                                                                           Supports the optional Hibernate query result cache.    
OSCache            X        X                    X                         Supports caching to memory and disk in a single JVM   
SwarmCache         X        X                                              A cluster cache based on JGroups       
JBoss Cache        X                                        X              A fully transactional replicated clustered cache

  - Example hibernate.cfg.xml:
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE hibernate-mapping PUBLIC  "-//Hibernate/Hibernate Mapping DTD//EN" "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd"> 
<hibernate-mapping>
   <class name = "Employee" table = "EMPLOYEE">
      ...            
      <id name = "id" type = "int" column = "id">
         <generator class="native"/>
      </id>
      <property name = "firstName" column = "first_name" type = "string"/>
      <property name = "lastName" column = "last_name" type = "string"/>
      <property name = "salary" column = "salary" type = "int"/>
      ...
      <cache usage = "read-write"/>
      ...
      <property name = "hibernate.cache.provider_class">
         org.hibernate.cache.EhCacheProvider
      </property>
      ...

  - NOTE: EHCache has its own configuration file, ehcache.xml, which should be in the CLASSPATH of the application.
    EXAMPLE ehcache.xml:
<diskStore path="java.io.tmpdir"/>

<defaultCache
maxElementsInMemory = "1000"
eternal = "false"
timeToIdleSeconds = "120"
timeToLiveSeconds = "120"
overflowToDisk = "true"
/>

<cache name = "Employee"
maxElementsInMemory = "500"
eternal = "true"
timeToIdleSeconds = "0"
timeToLiveSeconds = "0"
overflowToDisk = "false"
/>

  - Query-level Cache code example:
    - Hibernate config: hibernate.cache.use_query_cache="true" 
      XML: <property name = "hibernate.cache.use_query_cache">true</property>
      <= *MUST* enable query-level caching globally...

    - Java examples:
      // JPA EntityManager:
      entityManager.createQuery("select f from Foo f")
        .setHint("org.hibernate.cacheable", true)
        .getResultList();
      ...
      // Legacy Hibernate
      Session session = SessionFactory.openSession();
      Query query = session.createQuery("FROM EMPLOYEE");
      query.setCacheable(true);
      query.setCacheRegion("employee");
      List users = query.list();
      SessionFactory.closeSession();
      <= ... then specify per-session (JPA entityManager, or legacy Hibernate sessionFactory)

  - Cache region
      // Legacy Hibernate
      Session session = SessionFactory.openSession();
      Query query = session.createQuery("FROM EMPLOYEE");
      query.setCacheable(true);
      query.setCacheRegion("employee");
      List users = query.list();
      SessionFactory.closeSession();                
      <= Hibernate also supports very fine-grained cache support through the concept of a cache region.
         A cache region is part of the cache that's given a name.

===================================================================================================

* test10: Batches:
  - Pathological example:
      Session session = SessionFactory.openSession();
      Transaction tx = session.beginTransaction();
      for ( int i=0; i<100000; i++ ) {
         Employee employee = new Employee(.....);
         session.save(employee);
      }
      tx.commit();
      session.close();
      <= By default, Hibernate will cache all the persisted objects in the session-level cache...
         ... and ultimately fall over with an OutOfMemoryException long before completion...

  - "Batch" counter-example:
    - Config: hibernate.jdbc.batch_size=NNN
    - Java:
      Session session = SessionFactory.openSession();
      Transaction tx = session.beginTransaction();
      for ( int i=0; i<100000; i++ ) {
         Employee employee = new Employee(.....);
         session.save(employee);
         if( i % 50 == 0 ) { // Same as the JDBC batch size
            //flush a batch of inserts and release memory:
            session.flush();
            session.clear();
         }
      }
      tx.commit();
      session.close();

  1. hibernate.cfg.xml:
<hibernate-configuration>
   <session-factory>
      <property name = "hibernate.jdbc.batch_size">50</property>
      ...

  2. ManageEmployee.java:
     -------------------
   public void addEmployees( ){
      Session session = factory.openSession();
      Transaction tx = null;
      final int MAXCOUNT = 1000;
      final int FLUSHCOUNT = 100;
      
      try {
    	 System.out.println(">>addEmployees()...");
         tx = session.beginTransaction();
         for ( int i=0; i < MAXCOUNT; i++ ) {
            String fname = "First Name " + i;
            String lname = "Last Name " + i;
            Integer salary = i;
            Employee employee = new Employee(fname, lname, salary);
            session.save(employee);
         	if( i % FLUSHCOUNT == 0 ) {
               System.out.println("  flush(" + i + ")...");
               session.flush();
               session.clear();
            }
         }
         tx.commit();
    	 System.out.println("<<addEmployees(): Done.");
      } catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } finally {
         session.close(); 
      }
      return ;
   }

===================================================================================================

* test11: Hibernate - Interceptors:
  - In Hibernate, interceptors are used to inspect the changes in entity’s property values before they are written and after they are read from a database.
    You can use the Hibernate interceptor to perform the various operations such as logging, auditing, profiling etc.

  - In Hibernate, an interceptor can be either Session-scoped or SessionFactory-scoped.
    Session-scoped interceptors are used when a Session is opened.

  - Interceptor interface and EmptyInterceptor class are Hibernate-only:
https://www.baeldung.com/database-auditing-jpa
    <= JPA doesn’t explicitly contain an auditing API, but the functionality can be achieved using entity lifecycle events.

  - You can either:
    a) implement Interceptor interface, or
    b) extend EmptyInterceptor class.  

  - Interceptor lifecycle events:
Method:           Purpose:
-------           --------
findDirty()       When the flush() method is called on a Session object.
instantiate()     When a persisted class is instantiated.
isUnsaved()       When an object is passed to the saveOrUpdate() method/
onDelete()        Called before an object is deleted.
onFlushDirty()    When Hibernate detects that an object is dirty (i.e. have been changed) during a flush i.e. update operation.
onLoad()          Called before an object is initialized.
onSave()          Called before an object is saved.
postFlush()       Called after a flush has occurred and an object has been updated in memory.
preFlush()        Called before a flush.

  - Example MyInterceptor.java:
    ---------------------------
public class MyInterceptor extends EmptyInterceptor {
   ...
   public boolean onFlushDirty(Object entity, Serializable id,
      Object[] currentState, Object[] previousState, String[] propertyNames,
      Type[] types) {
         if ( entity instanceof Employee ) {
            System.out.println("Update Operation");
            return true; 
         }
         return false;
   }
	
   public boolean onLoad(Object entity, Serializable id,
      Object[] state, String[] propertyNames, Type[] types) {
         // do nothing
         return true;
   }
   
   // This method is called when Employee object gets created.
   public boolean onSave(Object entity, Serializable id,
      Object[] state, String[] propertyNames, Type[] types) {
         if ( entity instanceof Employee ) {
            System.out.println("Create Operation");
            return true; 
         }
         return false;
   }
   
   //called before commit into database
   public void preFlush(Iterator iterator) {
      System.out.println("preFlush");
   }
   ...

  -  ManageEmployee.java:
     --------------------
   ...
   public Integer addEmployee(String fname, String lname, int salary){
      Session session = factory.openSession( new MyInterceptor() );
      <= ERROR: The method openSession() in the type SessionFactory is not applicable for the arguments (MyInterceptor)

     - ALTERNATIVE:
	protected Session openSessionWithInterceptor() {
		Session session = factory.withOptions().interceptor(new MyInterceptor()).openSession();
		return session;
	}

===================================================================================================

* test12: JPA-compliant, standalone example.
  - https://www.baeldung.com/database-auditing-jpa
    http://www.vogella.com/tutorials/JavaPersistenceAPI/article.html
    https://www.mkyong.com/hibernate/hibernate-one-to-many-relationship-example-annotation/
    https://vladmihalcea.com/the-best-way-to-map-an-enum-type-with-jpa-and-hibernate/
    https://dzone.com/articles/testing-databases-junit-and

  - TBD:
    - Create base app: Hibernate, MySQL; "Task list" schema; manually create tables (mkdb.mysql.sql)
    - Automate settings/getters (Lombock)
    - Auto-generate tables:
        <property name="hibernate.hbm2ddl.auto">create</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
    - SQL Trace/logging
    - JPA-compliant auditing: https://www.baeldung.com/database-auditing-jpa
    - JUnit tests

  1. Create base app:
     a. Create project folder:
        mkdir -p $PROJ/test12/base-app
        mkdir -p $PROJ/test12/extended-app
        cd test12/base-app

     b. Create "mkdb" database script:
        mkdb.mysql.sql:
        --------------
drop table if exists task;

-- priority: LOW, NORMAL, HIGH
-- status: PENDING, INPROGRESS, COMPLETED, BLOCKED, REOPENED
create table task (
  id int NOT NULL auto_increment,
  summary varchar(20) NOT NULL,
  priority varchar(10) NOT NULL default 'NORMAL', 
  status varchar(10) NOT NULL default 'PENDING',
  date timestamp default current_timestamp,
  PRIMARY KEY(id)
);

drop table if exists task_update;

create table task_update (
  id int NOT NULL auto_increment,
  taskid int NOT NULL,
  text varchar(255) NULL,
  date timestamp default current_timestamp,
  PRIMARY KEY(id),
  FOREIGN KEY fk_task(taskid)
  REFERENCES task(id)
);

     c. Create Eclipse project for initial version, "base-app"
        Eclipse > File > New > Maven project >
          Simple project= Y, Location= $PROJ/test12/base-app, Working set= HelloHibernate >
          GroupID= com.example, ArtificateId= base-app, Packaging= .jar, Name= Base app

     d. Update pom.xml:
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  ...
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<maven.compiler.source>1.8</maven.compiler.source>
	<maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
	<!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-core -->
    ...
	<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
    ...
	<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
    ...
    <!-- https://mvnrepository.com/artifact/junit/junit -->
    ...
    <!-- https://mvnrepository.com/artifact/com.h2database/h2 -->
    ...
     - NOTE: JUnit and H2 are "test" scope 

     e. Create hibernate.cfg.xml:
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE hibernate-configuration SYSTEM "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
   <session-factory>
      <property name = "hibernate.dialect">
         org.hibernate.dialect.MySQLDialect
      ...
      <property name = "hibernate.connection.driver_class">
         com.mysql.jdbc.Driver
      <property name = "hibernate.connection.url">
         jdbc:mysql://localhost/test
      ...
      <property name = "hibernate.connection.username">
         test
      ...
      <property name = "hibernate.connection.password">
         test123
      ...
   </session-factory>
</hibernate-configuration>

     - NOTES: 
       - JPA-only would be "META-INF/persistence.xml"
       - Eclipse > Project > hibernate.cfg.xml > Configure Build Path >
         <= Verify "hellobase/src/main/java" is a "Source folder", and "Included: (All), Excluded: (None)

  2. Create entities:
     - Task.java
       ---------
...
import lombok.Data;   // Use Lombok @Data to auto-generate setter/getter methods
...
@Entity
@Table(name = "task")
@Data
public class Task {
	public Task () {}
	
	public Task (String summary, Priority priority, Status status) {
		this.summary = summary;
		this.priority = priority.toString();
		this.status = status.toString();
	}
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "summary")
	private String summary;

	@Column(name = "priority")
	private String priority;

	@Column(name = "status")
	private String status;

	@Column(name = "date")
	private Date date;
	
	// Note: we always want to fetch all updates whenever we fetch a task
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name="taskid")
	private List<TaskUpdate> updates = new ArrayList<>();
...

     - TaskUpdate.java:
       ---------------
...
import lombok.Data;  // Use Lombok to auto-generate setting/getter methods
...
@Entity
@Table(name = "task_update")
@Data
public class TaskUpdate {

   public TaskUpdate() {}
   
   public TaskUpdate (int taskId, String updateText) {
	   this.taskId = taskId;
	   this.text = updateText;
   }
   
   @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private int id;

   @Column(name = "taskid")
   private int taskId;

   @Column(name = "text")
   private String text;

   @Column(name = "date")
   private Date date;
...

  3. Create JUnit tests:
     - NOTE: 
       - For these tests, we'll use H2 (instead of a mock database)
         <= Not exactly a "unit" test ... but will allow us to verify we're getting the expected results with Hibernate

    a. Copy hibernate.cfg.xml and mkdb.mysql.sql from src/test/resources; modify for H2 (vs. MySql):
       - hibernate.cfg.xml:
            -----------------
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE hibernate-configuration SYSTEM "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
	<session-factory>
		<property name="hibernate.connection.driver_class">org.h2.Driver</property>
		<property name="hibernate.connection.username">sa</property>
		<property name="hibernate.connection.password"></property>
		<property name="hibernate.connection.url">jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:mkdb.h2.sql'</property>
		<property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
		<property name="show_sql">true</property>
		<property name="connection.pool_size">5</property>
		<property name="hibernate.id.new_generator_mappings">false</property>
        ...
    <= NOTES: 
       - We do NOT want to "enable hbm2dll.auto" (create, update or verify)
       - Instead, well invoke "mkdb.h2.sql" each time a JUnit test class is invoked.

          - mkdb.h2.sql:
            ------------
CREATE TABLE TASK (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  SUMMARY VARCHAR(20) NOT NULL,
  PRIORITY VARCHAR(10) NOT NULL DEFAULT 'NORMAL', 
  STATUS VARCHAR(10) NOT NULL DEFAULT 'PENDING',
  DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(ID)
);                                  

CREATE TABLE TASK_UPDATE (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  TASKID BIGINT NOT NULL,
  TEXT VARCHAR(255) NULL,
  DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(ID)
);
  
INSERT INTO TASK (SUMMARY) VALUES ('AAA');
INSERT INTO TASK (SUMMARY, PRIORITY, STATUS) VALUES ('BBB', 'LOW', 'REOPENED');
    <= NOTES: 
       - We create our two tables, and insert a couple of test records

     b. Create a "HibernateUtil.java" class to simplify creating a Hibernate SessionFactory once; and opening Hibernate Sessions as-needed
        - HibernateUtil.java:
          ------------------
public class HibernateUtil {
	protected static SessionFactory sessionFactory;
    ...
	public static void initSessionFactory() throws Exception { ... }
    public static void shutdown() {...}
    public static SessionFactory getSessionFactory () { return sessionFactory; }	
	public static Session openSession() { return sessionFactory.openSession(); }
    ...
         
     c. Create and execute JUnit tests

  4. Complete application:
     - TestApp.java

===================================================================================================

* test12: JPA (vs. Hibernate) syntax

  1. Create separate, "test2", mysql database:
     <= We're going to allow auto-generation (vs. manual mkdb*.sql file)

  2. Copy $PROJ/test12/base-app => jpa-syntax

  3. $PROJ/test12/jpa-syntax/pom.xml:
  <dependencies>
	<!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-core -->
	<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
	<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
    <!-- https://mvnrepository.com/artifact/junit/junit -->
    <!-- https://mvnrepository.com/artifact/org.mockito/mockito-all -->
    <= NOTE: We'll be using Mockito (instead of H2 in-memory database) for JUnit tests


  4. $PROJ/test12/jpa-syntax/src/main/resources/META-INF/persistence.xml:
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1"
    xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="com.example.hellohibernate.jpa" transaction-type="RESOURCE_LOCAL">
        <class>com.example.hellohibernate.Task</class>      
        <class>com.example.hellohibernate.TaskUpdate</class>      
        <properties>
            <!-- Configuring The Database Connection Details -->
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/test2" />
            <property name="javax.persistence.jdbc.user" value="test" />
            <property name="javax.persistence.jdbc.password" value="test123" /> 
			<!-- First time only,  create from schema: -->
			<property name="javax.persistence.schema-generation.database.action" value="create" />
			<property name="javax.persistence.schema-generation.create-source" value="script"/>
			<property name="javax.persistence.schema-generation.create-script-source" value="META-INF/mkdb.mysql.sql" />
			
			<!-- Re-create each time from script: 
			<property name="javax.persistence.schema-generation.database.action" value="drop-and-create" />
			<property name="javax.persistence.schema-generation.create-source" value="script"/>
			<property name="javax.persistence.schema-generation.create-script-source" value="META-INF/mkdb.mysql.sql" />
			 -->           			
			<!-- After DB created:
			<property name="javax.persistence.schema-generation.database.action" value="none" />
			  -->
        </properties>
    </persistence-unit>
</persistence>
     - NOTES: 
       - "persistence.xml" (vs. "hibernate.cfg.xml")
       - in "META-INF/" (vs. root directory)
       - We'll be using "test2" (instead of "test") DB
       - Specify all entity classes (Java "Task" and "TaskUpdate") in persistence unit "com.example.hellohibernate.jpa"
       - javax.persistence.schema-generation.database.action= {none|create|drop-and-create}
       - We can optionally choose:
         a) "auto-generate schema" (default= No),
         b) auto-generate with SQL script or reflect entities (default= parse entities for schema definition)

  5. $PROJ/test12/jpa-syntax/src/main/resources/META-INF/mkdb.mysql.sql:
drop table if exists task_update;
drop table if exists task;

-- priority: LOW, NORMAL, HIGH
-- status: PENDING, INPROGRESS, COMPLETED, BLOCKED, REOPENED
create table task (  id int NOT NULL auto_increment,  summary varchar(20) NOT NULL,  priority varchar(10) NOT NULL default 'NORMAL',  status varchar(10) NOT NULL default 'PENDING',  date timestamp default current_timestamp,  PRIMARY KEY(id));
create table task_update (  id int NOT NULL auto_increment,  taskid int NOT NULL,  text varchar(255) NULL,  date timestamp default current_timestamp,  PRIMARY KEY(id),  FOREIGN KEY fk_task(taskid)  REFERENCES task(id));

     - NOTES: 
       - Originally got this:
         ERROR: GenerationTarget encountered exception accepting command : Error executing DDL
       - Cause: Hibernate parser seems to accept only single-line SQL statements (?!?)
       - References:
https://stackoverflow.com/questions/43407411/generationtarget-encountered-exception-accepting-command-error-executing-ddl-v
https://stackoverflow.com/questions/54163671/generationtarget-encountered-exception-accepting-command-error-executing-ddl

  6. Entities: Task.java, TaskUpdate.java
     <= NO CHANGE: OK as-is

  7. Driver app: 
     <= $PROJ/test12/jpa-syntax/src/main/; many changes:

     TestApp.java:
     ------------
	public static final String PERSISTENCE_UNIT = "com.example.hellohibernate.jpa";

    private static EntityManagerFactory entityManagerFactory;
	protected static EntityManagerFactory createEntityManagerFactory() {
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		return entityManagerFactory;

    public void closeEntityManagerFactory() {
		entityManagerFactory.close();

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
		...

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
			...

	public List<Task> listTasks() {
		EntityManager em = null;
		try {
			em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Query q = em.createQuery("from Task");
			List<Task> tasks = (List<Task>) q.getResultList();
			em.getTransaction().commit();
			return tasks;
			...

===================================================================================================

* test12/jpa-syntax: Revisit JUnit testing

  1. In test12/base-app, we wrote JUnit tests against an in-memory H2 database
     <= This works ... but it's not really "unit testing"

  2. In test12/jpa-syntax, we tried two approaches:
     a) AssertAnnotations/ReflectTool: a DZone article that "unit tests" an entity by using Java reflection
        <= A good idea ... but I couldn't get it to work.

     b) Use Mockito to mock out javax.Persistence EntityManagerFactory, EntityManager, etc.
        <= This didn't work either ...
           ... because the module in question ("TestApp.java") didn't really lend itself to Mockito testing
           A more "realistic" scenario would have DAOs between my JPA entities and the application...
           ... and I'd use Mockito to unit test the DAOs

  3. RESULTS:
     a) AssertAnnotations/ReflectTool tests
        JUnit results: 
          Runs 8/8,  Errors: 3, Failures: 2
          
        Console:
          TaskTest::tableTest()    PASS
          TaskTest::entityTest()   PASS
          TaskTest::idTest()       FAIL: GeneratedValue a = ReflectTool.getMethodAnnotation() return null => NPE
          TaskTest::summaryTest()  FAIL: Column c = ReflectTool.getMethodAnnotation() returns null => NPE
          TaskTest::methodsTest()  FAIL: AssertionError: Expected 2 annotations, but found 0
          TaskTest::typeTest()     PASS
          TaskTest::updatesTest()  FAIL: OneToMany a = ReflectTool.getMethodAnnotation() returns null => NPE 
          TaskTest::fieldsTest()   FAIL: AssertionError: Expected 0 annotations, but found 3

     b) Mockito tests:
        EXAMPLE RUN:
          java.lang.NullPointerException
            at com.example.hellohibernate.TestApp.addTask(TestApp.java:47)  <-- try/catch handler
            at com.example.hellohibernate.TestAppTest.happyPathScenario(TestAppTest.java:42)
            at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            ...
        Console:
          TestAppTest::happyPathScenario()
          WARNING: An illegal reflective access operation has occurred
          WARNING: Illegal reflective access by org.mockito.cglib.core.ReflectUtils$2 (file:/home/paulsm/.m2/repository/org/mockito/mockito-all/1.9.5/mockito-all-1.9.5.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
          WARNING: Please consider reporting this to the maintainers of org.mockito.cglib.core.ReflectUtils$2
          WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
          WARNING: All illegal access operations will be denied in a future release

  4. TBD:
     - Refine Michael Remijan's "AssertAnnotations/ReflectTool" into simpler, more robust helper class(es)
     - Revisit Mockito with more realistic scenarios (e.g. Spring Boot apps, with bona fide DAOs)

===================================================================================================

* More realistic examples with Hibernate/Mockito:

/**
 * SampleServiceTest.java
 * 
 * REFERENCE:
 * https://www.luckyryan.com/2013/06/28/unit-testing-with-mockito/
 */
...
   @Test
   public void testSaveForm() {
 
       SignupForm signupForm = new SignupForm();
       signupForm.setName("AAA");
       signupForm.setEmail("AAA@test.com");
 
       when(userDao.save(any(User.class)))
               .thenAnswer(new Answer<User>() {
                   @Override
                   public User answer(InvocationOnMock invocation) throws Throwable {
                       User user = (User) invocation.getArguments()[0];
                       user.setId(1L);
                       return user;
                   }
               });
 
       assertNull(signupForm.getId());
 
       signupForm = sampleService.saveFrom(signupForm);
 
       assertNotNull(signupForm.getId());
       assertTrue(signupForm.getId() > 0);
   }     

===================================================================================================
* One-to-Many example:
  https://mkyong.com/hibernate/hibernate-one-to-many-relationship-example/

  - Changes from original:
    - Renamed "HibernateExample" => "OneToManyXml"
    - pom.xml: MySqlDB => Derby 10.15.1.3, Hibernate 6.3.Final => 5.4.0.Final, Java version/maven-compiler-plugin
    - hibernate.cfg.xml => Configured for Derby (vs. MySql), URL= jdbc:derby:d:/temp/mkyongdb

  - One::Many:
    - stock:
        STOCK_ID ---+    stock_daily_record:
        Stock_Code  |      DAILY_RECORD_ID
        Stock_Name  |      Price_Open, Price_Close, Price_Change, Volume, Date
                    +----> Stock_ID (FK)

  - DBeaver:
      New Connection > SQL > Type= Derby Embedded >
        Path= D:\temp\mkyongdb;create=true, User= app, password= BLANK
      [Test Connection] => Download/load drivers (derby, derbytools, derbyshared 10.14.1.3)
         <= No-go: driver compiled with class files 53.0 (Java 9), JRE only class files 52 (Java 8)...
    - Updated eclipse.ini
      <= Changed JRE from jdk1.8.0_121 to jdk-13.0.1, ran "eclipse -clean"
    - DBeaver > New Connection > SQL > Type= Derby Embedded >
        Path= D:\temp\mkyongdb;create=true, User= app, password= BLANK
      [Test Connection] => OK this time
        Path= D:\temp\mkyongdb, [Finish]

  - DBeaver > Projects > OneToManyXml > Connections > Derby Embedded > SQL Editor >

 CREATE TABLE APP.stock (
  STOCK_ID integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  STOCK_CODE varchar(10) NOT NULL,
  STOCK_NAME varchar(20) NOT NULL,
  PRIMARY KEY (STOCK_ID),
  UNIQUE (STOCK_NAME, STOCK_CODE)
) 

CREATE TABLE  APP.stock_daily_record (
  RECORD_ID integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  PRICE_OPEN float DEFAULT NULL,
  PRICE_CLOSE float DEFAULT NULL,
  PRICE_CHANGE float DEFAULT NULL,
  VOLUME bigint DEFAULT NULL,
  DATE date NOT NULL,
  STOCK_ID integer NOT NULL,
  PRIMARY KEY (RECORD_ID),
  CONSTRAINT FK_STOCK_TRANSACTION_STOCK_ID FOREIGN KEY (STOCK_ID)
  REFERENCES APP.stock (STOCK_ID) ON DELETE CASCADE
)
   <= Several constraints in the original MySQL DDL are incompatible with Derby syntax...
   <<Saved to Scripts/Script.sql>>

  - src > main > resources > hibernate.cfg.xml:
    ------------------------------------------
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
	<session-factory>
	<!-- 
	 	<property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
		<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/mkyongdb</property>
		<property name="hibernate.connection.username">root</property>
		<property name="hibernate.connection.password">password</property>
		<property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
	 -->
		<property name="hibernate.connection.driver_class">org.apache.derby.jdbc.EmbeddedDriver</property>
		<property name="hibernate.connection.username">app</property>
		<property name="hibernate.connection.password"></property>
		<property name="hibernate.connection.url">jdbc:derby:d:/temp/mkyongdb</property>
		<property name="hibernate.dialect">org.hibernate.dialect.DerbyTenSevenDialect</property>
		<!-- Auto-generate tables: create|validate|update|create-drop
		<property name="hibernate.hbm2ddl.auto">create</property>
		-->
		<property name="show_sql">true</property>
		<property name="format_sql">true</property>
		<mapping resource="com/mkyong/stock/Stock.hbm.xml" />
		<mapping resource="com/mkyong/stock/StockDailyRecord.hbm.xml" />

  - src > main > java > App.java:
public class App {
	public static void main(String[] args) {
		System.out.println("Hibernate one to many (XML Mapping)");
		Session session = HibernateUtil.getSessionFactory().openSession();

		session.beginTransaction();

		Stock stock = new Stock();
        stock.setStockCode("7052");
        stock.setStockName("PADINI");
        session.save(stock);
        
        StockDailyRecord stockDailyRecords = new StockDailyRecord();
        stockDailyRecords.setPriceOpen(new Float("1.2"));
        stockDailyRecords.setPriceClose(new Float("1.1"));
        stockDailyRecords.setPriceChange(new Float("10.0"));
        stockDailyRecords.setVolume(3000000L);
        stockDailyRecords.setDate(new Date());
        
        stockDailyRecords.setStock(stock);        
        stock.getStockDailyRecords().add(stockDailyRecords);

        session.save(stockDailyRecords);

		session.getTransaction().commit();
		System.out.println("Done");
    }
}  <= Saves "Stock" parent and "StockDailyRecord" children separately
      "Read" not shown...

    << Refactored App.java:
         static void main(String[] args)
         void saveStock(String[] stockData, String[] reportData)
         List<Stock> getStocks()
         void printStocks(List<Stock> stocks)
      >>
  
  - Eclipse > OneToManyXml > Debug configuration >
      Name= App, class= com.mkyong.App, Stop in main= Y, JRE= jdk-13.0.1

    - public void saveStock(String[] stockData, String[] reportData)
2020-01-15_17:09:40.887 WARN  org.hibernate.orm.deprecation - HHH90000012: Recognized obsolete hibernate namespace http://hibernate.sourceforge.net/hibernate-mapping. Use namespace http://www.hibernate.org/dtd/hibernate-mapping instead.  Support for obsolete DTD/XSD namespaces may be removed at any time.
      <= Otherwise, executed perfectly!
         No problem reading "children"; either inside or outside of session.

    <<See console-log-200115a.txt>>

        


