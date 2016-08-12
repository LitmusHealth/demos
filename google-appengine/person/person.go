/*
Package person defines a property type that implements the PropertyLoadSaver
interface for use with Google App Engine.
*/
package person

import (
	"errors"
	"google.golang.org/appengine/datastore"
)

// Person is a data type that has a Name, Age, and CanDrink (alcohol, US) tag.
// The CanDrink tag can be deduced from the Age, so we wont store it in the
// datastore.
type Person struct {
	Name     string
	Age      int
	CanDrink bool `datastore:"-"`
}

// Load populates p with the corresponding entry from the datastore
func (p *Person) Load(ps []datastore.Property) error {
	err := datastore.LoadStruct(p, ps)
	if err != nil {
		return err
	}

	if p.Age >= 21 {
		p.CanDrink = true
	} else {
		p.CanDrink = false
	}

	return nil
}

// Save puts p into the datastore
func (p *Person) Save() ([]datastore.Property, error) {
	if p.Age >= 21 && p.CanDrink == false || p.Age < 21 && p.CanDrink == true {
		return []datastore.Property{}, errors.New("Property has wrong CanDrink field.")
	}

	return datastore.SaveStruct(p)
}
