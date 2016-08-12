/*
The avroDemo application spawns a server that recieves an encoded Avro record,
decodes it, re-encodes it, and echos it back to the client. It then creates
two test records, and sends them to the server. It outputs the record it creates,
the record as decoded by the server, and the record it recieves back from the
server. These should all be identical.
*/
package main

import (
	"fmt"
	"github.com/litmushealth/demos/avro/client"
	"github.com/litmushealth/demos/avro/server"
	"io/ioutil"
	"log"
	"os"
	"strconv"

	"github.com/linkedin/goavro"
)

// printRecordComp takes the sent record and the recieved record and prints them.
func printRecordComp(rec1 *goavro.Record, rec2 *goavro.Record) {
	fmt.Println("Client Sent    : " + rec1.String())
	fmt.Println("Client Recieved: " + rec2.String())
	fmt.Println()
}

func main() {
	args := os.Args[1:]
	if len(args) < 1 || len(args) > 2 {
		fmt.Println("Usage: testAvro <schema_file> <port_number>")
		os.Exit(1)
	}

	// Default port is 8080. Otherwise use what user specifies
	var port int
	var err error
	if len(os.Args) > 1 {
		port, err = strconv.Atoi(os.Args[2])
		if err != nil {
			log.Fatal(err)
		}
	} else {
		port = 8080
	}

	// Load Avro schema from file in JSON syntax.
	schema, err := ioutil.ReadFile(os.Args[1])
	if err != nil {
		log.Fatal(err)
	}

	// Codec is used to encode/decode Avro records
	codec, err := goavro.NewCodec(string(schema))
	if err != nil {
		log.Fatal(err)
	}

	// The server and client both need to be told what codec to use
	server.SetCodec(&codec)
	client.SetCodec(&codec)

	// Launch the server in another thread to await requests
	go server.SpawnServer(port)

	// Create Avro records for the client+server to encode+decode
	// Then print out the results to see if we didn't mangle them
	recordReq := client.CreateTestRecord(3, "test")
	recordResp := client.SendHTTPRequestToServer(port, recordReq)
	printRecordComp(recordReq, recordResp)

	recordReq = client.CreateTestRecord(1, "test2")
	recordResp = client.SendHTTPRequestToServer(port, recordReq)
	printRecordComp(recordReq, recordResp)
}
