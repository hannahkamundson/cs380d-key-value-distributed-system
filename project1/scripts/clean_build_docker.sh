#!/bin/bash -ex

# sudo docker image rm $(sudo docker image ls --format '{{.Repository}} {{.ID}}' | grep 'sekwonlee' | awk '{print $2}')

sudo docker build . -f dockerfiles/base.dockerfile -t sekwonlee/kvs:base --network=host

sudo docker build . -f dockerfiles/client.dockerfile -t hannah:client --network=host

sudo docker build . -f dockerfiles/frontend.dockerfile -t hannah:frontend --network=host

sudo docker build . -f dockerfiles/server.dockerfile -t hannah:server --network=host