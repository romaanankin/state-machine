# state machine

State machine allows you to create entities 
and modify them in runtime by changing their state according to 
predefined state matrix which also can be fulfilled and overridden in runtime.

## Getting Started

These instructions will get you a copy of the proje
ct up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software 

```
jdk 8
```
```
sbt 1.3.2 +
```
```
docker-compose 
```


### Installing

A step by step series of examples that tell you how to get a development env running

Get project

```
git pull
```

build it

```
sbt assembly
```
start docker containers 

```
docker-compose up
```

### Explore 

After setting uo with container you will see a message like this one below:
 
 ```
 Server online at http://${GENERATED_HOST}:
 ```

Use this link to communicate with container.

### API

Routes description

send requests to this url ${GENERATED_HOST}:9000

create Entity
````
POST /entity
````
````
{
  "name": "somename"
}
````
response will be created Entity with state from "init" to "pending"

````
{
"entity_id":"1569872444943-node-name",
"from":{"state":"init"},
"name":"somename",
"to":{"state":"pending"}
}
````
create your possible state transition 

````
POST /state-matrix
````
````
{
"state":{"state":"pending"},
"transitions":["super","class","class1"]
}`
````

check "state" possible transitions
````
GET /state-matrix/${STATE_NAME}
````
response
````
{
"state":{"state":"pending"},
"transitions":["super","class","class1"]
}`
````
modify Entity state 

````
POST /state/${entity_id}
````
````
{
"state": "${state name}"
}
````
response will be Entity with changed state

````
{
"entity_id":"1569872444943-node-name",
"from":{"state":"pending"},
"name":"somename",
"to":{"state":"${state name}"}
}
````
get transition history

````
GET /history
````
you will get a json array of transitions

````
["{\"entity\":{\"entity_id\":\"1569773308254-node-name\",
\"from\":{\"state\":\"init\"},\"name\":\"to transition\",
\"to\":{\"state\":\"pending\"}},
\"transition_id\":\"1569773308350-name-node\"}",

"{\"entity\":{\"entity_id\":\"1569773393914-node-name\",
\"from\":{\"state\":\"init\"},\"name\":
\"to transition\",
\"to\":{\"state\":\"pending\"}},
\"transition_id\":\"1569773393921-name-node\"}]"
````

ERRORS

you trying to change state with invalid value   
````
{
	"error": "No such state to change"
}
````

incorrect entity Id
````
{
	"error": "No such entity"
}
````

no such transition option
````
{
	"error": "state"
}
````





