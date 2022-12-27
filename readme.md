# SamKnows Test Project

## Requirements:
The following are the requirements gathered for the given project.

### App server configuration:
Need to configure the app server before Spring Boot is launched:
1) A Properties file to configure the app server properties.
2) An XML file and respective schema to define each request that could hit the server. Each request that hits the server should be validated against its entry in this file.

### Request Preprocessor:
Need a request preprocessor to do the following tasks:
1) Attachment type verification: Only allowed types should be found in the request attachment. JSON for the given application.
2) Check if the file is well-formed. Ex: Validate JSON for the given application.

### Response Builder Utilities:
Need a Response Builder which should be used in building all the responses for the application:
1) Should support custom Application status codes and messages.
2) Should support multiple MediaType. JSON and JSON files for this application.

### Controller Class:
1) Only one request for the Application. Using GET REQUEST.

### Logging
1) Using Log4j.
2) Logs should be appended to a file (log.out).

### Testing
1) Should test the response JSON.
2) Test the WebServer Responses.
3) Test the content and content types in Multi Media attachments.


