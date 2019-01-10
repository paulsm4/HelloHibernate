HB=/opt/hibernate/lib
CP=$HB/required/antlr-2.7.7.jar:\
$HB/required/byte-buddy-1.9.5.jar:\
$HB/required/classmate-1.3.4.jar:\
$HB/required/dom4j-2.1.1.jar:\
$HB/required/FastInfoset-1.2.15.jar:\
$HB/required/hibernate-commons-annotations-5.1.0.Final.jar:\
$HB/required/hibernate-core-5.4.0.Final.jar:\
$HB/required/istack-commons-runtime-3.0.7.jar:\
$HB/required/jandex-2.0.5.Final.jar:\
$HB/required/javassist-3.24.0-GA.jar:\
$HB/required/javax.activation-api-1.2.0.jar:\
$HB/required/javax.persistence-api-2.2.jar:\
$HB/required/jaxb-api-2.3.1.jar:\
$HB/required/jaxb-runtime-2.3.1.jar:\
$HB/required/jboss-logging-3.3.2.Final.jar:\
$HB/required/jboss-transaction-api_1.2_spec-1.1.1.Final.jar:\
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
