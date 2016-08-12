/* Package client holds an Avro codec as a state variable and offers functions
to send requests to the Avro demo server.
*/
package client

import (
	"bytes"
	"errors"
	"log"
	"net/http"
	"strconv"

	"github.com/linkedin/goavro"
)

var avroCodec *goavro.Codec = nil

// SetCodec updates the client's Avro codec used for encoding/decoding Avro records.
func SetCodec(codec *goavro.Codec) {
	avroCodec = codec
}

// CreateTestRecord creates an Avro record from the default schema included in the demo.
// The default schema has two fields: an int32 and a string.
func CreateTestRecord(i int32, s string) *goavro.Record {
	record, err := goavro.NewRecord(goavro.RecordSchema((*avroCodec).Schema()))
	if err != nil {
		log.Fatal(err)
	}
	record.Set("field1", i)
	record.Set("field2", s)

	return record
}

// SendHTTPRequestToServer takes a port number and an Avro record.
// It sends the record to the Avro demo server on the given port. It then
// returns the record that is sent back by the server.
func SendHTTPRequestToServer(port int, record *goavro.Record) *goavro.Record {
	if avroCodec == nil {
		log.Fatal(errors.New("Need to set a valid Avro codec before querying the server."))
	}

	// Create a bytes buffer for the encoder to write to.
	buf := new(bytes.Buffer)
	err := (*avroCodec).Encode(buf, record)
	if err != nil {
		log.Fatal(err)
	}

	// Send the data in the buffer as a POST request to the server
	resp, err := http.Post("http://localhost:"+strconv.Itoa(port)+"/avro", "application/avro+binary", buf)
	if err != nil {
		log.Fatal(err)
	}

	// Close the response body when the function returns
	defer resp.Body.Close()

	// Decode the server's response. Should be the same as the original record
	decoded, err := (*avroCodec).Decode(resp.Body)
	if err != nil {
		log.Fatal(err)
	}

	// The decoder returns in interface{}, so we must type cast it.
	return decoded.(*goavro.Record)
}
