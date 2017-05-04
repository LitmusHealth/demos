#!/bin/bash
set -e

if [ "$EUID" -ne 0 ]; then
    echo "This script uses functionality which requires root privileges"
    exit 1
fi

# Start the build with an empty ACI
acbuild --debug begin

# In the event of the script exiting, end the build
acbuildEnd() {
    export EXIT=$?
    acbuild --debug end && exit $EXIT
}
trap acbuildEnd EXIT

# Name the ACI
acbuild --debug set-name litmushealth.com/hash-calculator

# Based on alpine
acbuild --debug dep add quay.io/coreos/alpine-sh

# Install nginx
acbuild --debug run apk update
acbuild --debug run apk add nginx

# Add a port for http traffic over port 80
acbuild --debug port add http tcp 80

# Bring in the hash-calculator page
acbuild --debug copy-to-dir $PWD/base64-calculator/* /usr/share/nginx/html

# Run nginx in the foreground
acbuild --debug set-exec -- /usr/sbin/nginx -g "daemon off;"

# Metadata stuff
acbuild --debug label add version 0.0.1

# Save the ACI and finish
acbuild --debug write --overwrite base64-calculator-container.aci
