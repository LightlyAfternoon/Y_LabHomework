# Finance Tracker App
This is a simple application for tracking financial transactions.
## Launching
### Requirements
- Git (to dowload the repository using the console)
- Docker Desktop
- JDK 17 or newer
### Steps
1. Download repository from site and unzip in some place on disk or clone repository by command on the command line:
   
   ```bash
   git clone https://github.com/LightlyAfternoon/Y_LabHomework.git
   ```
   
3. Run Docker Desktop
4. Execute command on the command line in Y_LabHomework folder:

   ```bash
   docker-compose -f docker-compose.yml up
   ```

## Work with service
For working with service you can:
### 1. Use such instrument as Postman - sending HTTP requests on URL <http://localhost:8083/>;
### 2. Use special console application:
- Execute command on the new command line in Y_LabHomework folder:

   ```bash
   java -jar console_app\target\console_app-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

- Execute the commands you need from the menu - /login for enter in the app or /register for create a new account and other commands.
## Documentation
For API documentation after up docker containers you can visit http://localhost:8083/swagger-ui/index.html
