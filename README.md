### This is a sample GitHub API based on the OpenAPI 3.0.

This API use _https://api.github.com/_ to retrieve data from GitHub.

###
To run the application you simply need to execute this command in your command line tool:

    mvn spring-boot:run

###
You can also use Dockerfile to build and run the image:

    docker build -t github-api .
    docker run -p 8080:8080 github-api

###
There is available only one endpoint for now:
    
    http://localhost:8080/repos/{username}

###
There is also Swagger UI available (generated from file - resources/static/swagger.yaml):

    http://localhost:8080/swagger-ui/index.html

###
You can try this query:

    curl -X 'GET' 'http://localhost:8080/repos/tomlisow'  -H 'accept: application/json'

Or you can try example requests from "**postman**" catalog in Postman application.

### As a result you should receive two repositories named:
* ### ***git-test*** with 2 branches: ***develop*** and ***master***
* ### ***github-api*** with 1 branch: ***develop***
