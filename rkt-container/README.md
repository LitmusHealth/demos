# rkt-container

This demo is a script and instructions for setting up a containerized version of the 
front-end/base64-calculator demo app.

It installs a base Alpine Linux image, install Nginx to it, binds it to port 80,
and then pulls in the calculator app to Nginx's html/ area to be served statically.

# Instructions

1. Install `rkt` and `acbuild`
2. Checkout and build the base64-calc demo
3. `bash base64-calc-rkt.sh`
4. Run with `rkt run --net=host --insecure-options=image base64-calculator-container.aci`
