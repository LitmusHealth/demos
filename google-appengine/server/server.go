/* Package server creates an http handler for Google App Engine that stores
valid "Person" entities into the datastore.
*/
package server

import (
	"encoding/json"
	"io"
	"log"
	"net/http"

	"github.com/litmushealth/google_appengine/person"
	"golang.org/x/net/context"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
)

// init creates handler to take datastore requests at http://<host>:<port>/data
func init() {
	http.HandleFunc("/data", handleDS)
}

// updateEntity takes a person object and either adds it to the datastore or
// updates the entity if its key already exists in the datastore.
func updateEntity(ctx context.Context, newP *person.Person, key *datastore.Key) int {
	oldP := new(person.Person)

	err := datastore.Get(ctx, key, oldP)
	if err != nil && err != datastore.ErrNoSuchEntity {
		log.Print(err)
		return http.StatusInternalServerError
	}

	_, err = datastore.Put(ctx, key, newP)
	if err != nil {
		log.Print(err)
		return http.StatusInternalServerError
	}

	return http.StatusOK
}

// handleDS is the HTTP handler that take POSTed "Person" objects and puts
// them into the datastore.
func handleDS(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	if r.Method == http.MethodPost {
		length := r.ContentLength
		if length > 0 {
			b := make([]byte, length)
			_, err := r.Body.Read(b)
			if err != nil && err != io.EOF {
				log.Print(err)
				w.WriteHeader(http.StatusBadRequest)
				return
			}

			p := new(person.Person)
			err = json.Unmarshal(b, p)
			if err != nil {
				log.Print(err)
				w.WriteHeader(http.StatusBadRequest)
				return
			}

			key := datastore.NewKey(ctx, "Person", p.Name, 0, nil)

			w.WriteHeader(updateEntity(ctx, p, key))
		} else {
			log.Print("Uknown length of HTTP Request.")
			w.WriteHeader(http.StatusBadRequest)
		}
	} else if r.Method == http.MethodGet {
		// No op
	} else {
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
}
