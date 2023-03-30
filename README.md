Project using Maven as the build tool, maven can be run by:

##To compile:

On linux:
mvn clean dependency:copy-dependencies package

OR

./mvnw clean dependency:copy-dependencies package


##To run:
java -jar target/loms-1.0-SNAPSHOT.jar
