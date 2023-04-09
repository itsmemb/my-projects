# the Model view controller (MVC) pattern

Get /about HTTP/1.1
Host: 127.0.0.1

## Routers
Matchers fro the url that is requested

GET for "/about"
I see you requested "/about", we'll give that to the about controller to handle

## Models
Databsase wrapper 

User
* query for records
* wrap individual records

## Views
Your response content
* HTML
* CSV
* PDF
* XML

This is what gets sent back to the browser and is displayed

## Controllers
Decide how to process a request and define a response
