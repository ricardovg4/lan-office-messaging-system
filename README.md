Project using Maven as the build tool, maven can be run by:

## To compile:

On linux:
mvn clean dependency:copy-dependencies package

OR

./mvnw clean dependency:copy-dependencies package


## To run:
# Run server first with:
java -jar server/target/server-1.0-SNAPSHOT.jar

# Run client:
To run the client, javafx passes through Eclipse, the javafx libraries need to be added, and the VM command to run the config as follows:

--module-path "insert path here" --add-modules=javafx.controls,javafx.fxml
