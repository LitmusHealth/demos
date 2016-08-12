# google-appengine
The google-appengine demo consists of a server that is to be run on Google 
App Engine (GAE), and a client (appengineDemo.go) that attemps to put two entries 
into it.

## Code

### person.go
The demo consists of a "Person" type that implements GAE's PropertyLoadSaver
interface. GAE can save a regular Go struct into the datastore or a type that
implements the PropertyLoadSaver interface. The advantage of the interface is
the increased flexibility of checking that the struct is consistent and valid,
as well as avoiding type reflection.

### server.go
The server implements an HTTP handler to be run on GAE. The server is the 
actuall "app". The handler accepts a JSON-encoded Person object to be stored in
the GAE datastore. The object will not be stored if the person.Save() returns
an error.

### appengineDemo.go
The demo simply creates two Person objects and asks the server to store them.
It prints out the server's responses to the POST requests. The effect of the
code can be seen by accessing the datastore viewer as part of the GAE system.

## Usage

To test the demo locally, you must have the Go Google App Engine SDK installed.
Then simply start the server on the app engine:
```
/path/to/google-appengine-go/dev_appserver.py --clear_datastore=yes server
```

Then run the demo:
```
go run appengineDemo.go
```

And you can view the datastore entries:
```
http://localhost:8000/datastore
```
