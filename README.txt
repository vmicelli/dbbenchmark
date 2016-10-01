This simple java application executes benchmarks on a PostgreSQL or MS SQLServer 
database. The application uses the PreparedStatement API to execute prepared 
statements.
The application includes a configuration.properties file (in folder 
src/main/resources) that have to be used to setup the data for db connection and 
the number of statements to execute.


INSERT STATEMENTS

The application executes insert statements in batches of X statements. The 
commit operation is performed every X statements. The X value can be configured
in the configuration.properties file using the property 
com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfInsertStatementsPerTransaction.

The application performs N batch executions and provide statistics 
(min, max, avg time). 
The N value can be configured in the configuration.properties file using the 
property com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfBatchInsertExecutions.


SELECT STATEMENTS

After the insert phase, the application execute M select statement executions 
and provide statistics (min, max, avg time). 
The M value can be configured in the configuration.properties file using the 
property com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfSelectExecutions.


WARMUP 

Before the actual executions, the application perform a number Z of warmup 
execution. Warmup executions are identical to the test that is going to be 
performed, but the times collected are not considered in the actual statistics.
The Z value can be configured in the configuration.properties file using the 
property com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfWarmupExecutions.
The warmup is performed before the insert test executing warmup insert 
statements and then before the select test executing warmup select statements.


JAVA PROJECT DESCRIPTION

The main class of the project is the class DbBenchmarkApp which uses a 
BenchmarkManager (DbBenchmarkManager) to execute the tests.

BenchmarkManager is a generic class with the logic to execute tests and print 
test results. This class is extended by DbBenchmarkManager which performs the 
needed initialization (creation of test table and Tester classes which will 
actually exec the tests).

BenchmarkManager classes have a set of "tester" classes and iterate over them 
to exec tests and print results. 
The testers are classes that implement the Tester interface. 
They have methods to execute tests, get results, and get info about the test 
being performed (name and general info).

The project includes the abstract class BaseTester an implementation of Tester 
which implement the main logic to perform a specific number of test iterations, 
collect execution times and statistics.

DbTester extends BaseTester to implement init/finish functions to open/close 
connection to db. DbInsertTester and DbSelectTester extend DbTester to actually
execute the statemens.
Db testers uses implementations of the abstract class DbHelper 
(PostgreSqlDbHelper and SqlServerDbHelper) to connect to db and execute prepared
statements using the PreparedStatement API.

The configuration of the application is read from the file 
configuration.properties (which resides in reside in src/main/resources folder) 
by the object ConfigurationHelper.

The application also includes a sample test for the class BaseTester.

NOTE

The solution developed for this simple java app is not intended to be a 
comprehensive solution for benchmark applications.
Executing reliable benchmarks is a task that has to take into account many 
factors besides a warmup phase (which is performed in this simple app).
Well known frameworks that address this topic can be used to execute reliable 
benchmarks. 
A well known framework is JMH http://openjdk.java.net/projects/code-tools/jmh/
An interesting reading on the topic from Oracle website: 
http://www.oracle.com/technetwork/articles/java/architect-benchmarking-2266277.html.


COMPILE AND RUN

From a console go to the main folder of the project.
Unfortunately Microsoft does not provide the jdbc driver via the maven repository
so exec the following command to install it in the local repository:

mvn install:install-file -Dfile=lib/sqljdbc4-4.2.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.2 -Dpackaging=jar

Then open the file configuration.properties (in folder src/main/resources) to 
setup the data for db connection and the number of statements to execute.

Then exec the command:

mvn package

Then exec the command:

java -cp target/dbbenchmark-1.0-SNAPSHOT.jar;lib/* com.vin.dbbenchmark.app.DbBenchmarkApp

(folder lib contains postgresql and sql server jdbc drivers )

Alternatively open the project using NetBeans.

When you exec the application you should see an output like this:


Executing tester: Insert Statements
-----------------------------------------------------
Result for tester: Insert Statements

Warmup Executions: 100
Batch Insert Executions: 10000
Num of inserts per batch: 10

min-warmup (to insert a batch of records): 617579 ns/op
max-warmup (to insert a batch of records): 7462146 ns/op
avg-warmup (to insert a batch of records): 1203939 ns/op
min (to insert a batch of records): 283189 ns/op
max (to insert a batch of records): 8022170 ns/op
avg (to insert a batch of records): 444263 ns/op
time per record in the bacth with min time: 28318 ns/op
time per record in the bacth with max time: 802217 ns/op
avg (to insert a record): 44426 ns/op
-----------------------------------------------------


Executing tester: Select Statements
-----------------------------------------------------
Result for tester: Select Statements

Warmup Executions: 100
Executions: 50000

min-warmup: 128883 ns/op
max-warmup: 1233746 ns/op
avg-warmup: 259174 ns/op
min: 42372 ns/op
max: 2944887 ns/op
avg: 81844 ns/op
-----------------------------------------------------


