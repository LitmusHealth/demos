/*
The appengineDemo application creates two "Person" objects, and sends them
to the server appengine app for entry into the datastore.
*/
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/litmushealth/demos/google-appengine/person"
	"log"
	"net/http"
)

func main() {
	p1 := &person.Person{
		Name:     "Rob",
		Age:      28,
		CanDrink: true,
	}

	p2 := &person.Person{
		Name:     "James",
		Age:      20,
		CanDrink: false,
	}

	b, err := json.Marshal(p1)
	if err != nil {
		log.Fatal(err)
	}

	resp, err := http.Post("http://localhost:8080/data", "application/json", bytes.NewBuffer(b))
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(resp)

	b, err = json.Marshal(p2)
	if err != nil {
		log.Fatal(err)
	}

	resp, err = http.Post("http://localhost:8080/data", "application/json", bytes.NewBuffer(b))
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(resp)
}
