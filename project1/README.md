# Distributed Key Value Store

## General Info
We wrote our key value store in Java.

## Architecture
Just like the original Python architecture, we have a [server](./java/server) and [frontend](./java/frontend). Both of 
these use [shared](./java/shared) code. The docker images in the [dockerfile folder](./dockerfiles) have been changed
to accomodate the Java, though this shouldn't matter when testing. Additionally, the [yaml files](./yaml) have been 
changed to point to these image names.

### Frontend
The frontend code starts in the [App.java](./java/frontend/src/main/java/io/digit/App.java) file, which sets up a web
server that handles XML RPC requests. It also initializes a 
[heartbeat](./java/frontend/src/main/java/io/digit/HeartBeat.java) which runs in a parallel thread for the duration of 
the application. The XML RPC recognizes the [FrontendRPC](./java/frontend/src/main/java/io/digit/FrontendRPC.java) which
is implemented in [FrontendRPCImpl](./java/frontend/src/main/java/io/digit/FrontendRPCImpl.java). These have the same
functions as the original Python code. The main difference is instead of directly calling the RPC functions, you need
to call it through the default path (more info below). The 
[FrontendRPCImpl](./java/frontend/src/main/java/io/digit/FrontendRPCImpl.java) stores server information in
[ServerList](./java/frontend/src/main/java/io/digit/server/ServersList.java).

### Server
The server code starts in the [App.java](./java/server/src/main/java/io/digit/App.java) file, which sets up a web
server that handles XML RPC requests. It recognizes the 
[ServerRPC](./java/shared/src/main/java/io/digit/server/ServerRPC.java) which is implemented in
[ServerRPCImpl](./java/server/src/main/java/io/digit/ServerRPCImpl.java). This is the same as the original Python
code for the server.

## How to Run
**Note:** The only change that needs to be made in the original Python code is the frontend needs to call default first.
Anywhere that has
```python
frontend = xmlrpc.client.ServerProxy("http://localhost:8001")
```
needs to be changed to
```python
frontend = xmlrpc.client.ServerProxy("http://localhost:8001").default
```

This has been done in all the Python code in this repo.

Other than the one above difference, there is nothing that needs to be run differently by the grader. Just like the 
Python code, this code can be run by building the docker images with `scripts/clean_build_docker.sh` and running the
cluster with `run_cluster.py` (given the frontend change above has been made in `run_cluster.py`).