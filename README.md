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

java -jar client/target/client-1.0-SNAPSHOT.jar


TO RUN:

1. Import project to Eclipse IDE
2. Add the JavaFX library to the build path
3. Add this VM command to the Run configuration for the Client : --module-path "C:\Program Files\Java\javafx-sdk-20/lib" --add-modules=javafx.controls,javafx.fxml
4. Run the server then the client.