/*
Package server spawns a listening server that recieves encoded Avro records,
decodes them according to its stored codec, re-encodes them, and then echoes
them back to the client.
*/
package server

import (
	"errors"
	"fmt"
	"log"
	"net/http"
	"strconv"

	"github.com/linkedin/goavro"
)

var avroCodec *goavro.Codec = nil

// echoAvroHandler decodes an Avro record sent to it, prints it, and then
// echoes it back to the client.
func echoAvroHandler(w http.ResponseWriter, r *http.Request) {
	// Only accept POST requests
	if r.Method == http.MethodPost {
		if r.ContentLength <= 0 {
			log.Print("Uknown length of HTTP Request.")
			w.WriteHeader(http.StatusBadRequest)
		}

		// Decode the record using the server's codec
		record, err := (*avroCodec).Decode(r.Body)
		if err != nil {
			log.Print(err)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		// Print the results of the decoding
		fmt.Println("Server Recieved: " + record.(*goavro.Record).String())

		// Encode the record again and send it back to client
		err = (*avroCodec).Encode(w, record)
		if err != nil {
			log.Print(err)
		}
	} else {
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
}

// SetCodec tells the server what Avro codec to use for encoding/decoding records.
func SetCodec(codec *goavro.Codec) {
	avroCodec = codec
}

// SpawnServer spins up the listening server at a given port number. Avro
// records will be echoed back by the server when POSTed to localhost:<port>/avro.
func SpawnServer(portNumber int) {
	if avroCodec == nil {
		log.Fatal(errors.New("Need to set a valid Avro codec before spawning a server."))
	}

	http.HandleFunc("/avro", echoAvroHandler)

	portStr := ":" + strconv.Itoa(portNumber)

	err := http.ListenAndServe(portStr, nil)
	if err != nil {
		log.Fatal(err)
	}
}
