# avro
The avro demo consists of a client and server that exchange Avro records that
are encoded and decoded to byte arrays by an Avro codec, based on a schema.

## Code

### server.go
The server implements a listening server with an HTTP handler. The server 
is initialized with the proper Avro codec and then accepts encoded Avro
records of a suitable schema. It decodes the record, prints it, and then 
encodes it again to be sent back to the client.

### client.go
The client sends and recieves Avro records to the server. The client must
also be initialized with the correct codec, so that both the server and client
can encode and decode the records.

### avroDemo.go
The demo simply creates two Avro records from a JSON formatted schema file, then
uses the client to encode and send those records to the server. The demo prints
the records that it sends and the records that it recieves back from the server.

## Usage

Run the demo:
```
go run avroDemo.go schema/schema.json 8080
```

Then compare the outputs to make sure that each set of outputs is identical:
```
Server Recieved: {Foo: [field1: 3, field2: test]}
Client Sent    : {Foo: [field1: 3, field2: test]}
Client Recieved: {Foo: [field1: 3, field2: test]}

Server Recieved: {Foo: [field1: 1, field2: test2]}
Client Sent    : {Foo: [field1: 1, field2: test2]}
Client Recieved: {Foo: [field1: 1, field2: test2]}
```
