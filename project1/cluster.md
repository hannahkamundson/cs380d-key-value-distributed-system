## Tested environment

We tested the current setup guide and implementations in the following environment only.
To prevent other potential issues, we strongly recommend that you proceed with the 
project in the same environment.

- Ubuntu18.04
- Python 3.6.9

## Set up cluster
We use [Kubernetes](https://kubernetes.io/) as a cluster orchestration tool.
We will build container images for each instance pod and make Kubernetes
automatically manage them. We use [Kubespray](https://github.com/kubernetes-sigs/kubespray) to generate 
our KVS cluster orchestrated by Kubernetes over a baremetal machine.

### Download source code

```
$ cd ~/; mkdir projects; cd projects
$ git clone https://github.com/vijay03/cs380d-f23.gitsudo
$ cd cs380d-f23/project1
```

### Install dependencies
<!-- 10.0.2.15 -->
```
$ bash scripts/dependencies.sh
```

### Configure OS environment variables & SSH
ss
There are a few environment variables that should be configured according to your own environment.

1. `$KVS_HOME`: Path to the project root directory.
2. `$USER_NAME`: Your machine's user name. The python script (`create_cluster.py`) to generate the
cluster uses this user name to access the local machine over ssh and to install required packages automatically.

```
$ export KVS_HOME=/home/cc/projects/cs380d-f23/project1
$ export USER_NAME=cc
```

Register a ssh public key as an authorized key to the local machine.

```
$ ssh-keygen
Copy and paste the public key in ~/.ssh/id_rsa.pub to ~/.ssh/authorized_keys
```

Add the public key to `known_hosts`.
```
$ ssh-keyscan [your local machine's ip address] >> $HOME/.ssh/known_hosts
```

10.0.2.15
### Install Kubernetes

Change the ip addresses in the following file to the ip address of your local machine.
```
$ vi kubespray/inventory/kvs_cluster/inventory.ini
[all]
master0 ansible_host=[your local machine's ip address] ip=[your local machine's ip address]
```
Run the following python script to install Kubernetes cluster configurations over a baremetal machine.
```
$ python3 create_cluster.py
```

Check if the cluster configurations are completed properly. The following command should show 
the list of the nodes currently managed by Kubernetes. In our case, only one node should be shown.

```
$ kubectl get nodes
NAME      STATUS   ROLES                  AGE   VERSION
master0   Ready    control-plane,master   20h   v1.22.2
```

172.17.0.1

### Build docker images
We employ multiple docker container instances to simulate distributed environments in our local
machine. These container instances are managed and monitored by Kubernetes. Kubernetes will download 
necessary container images from a designated repository in [Dockerhub](https://hub.docker.com/) and spawns them into the cluster.
Please register an account at [Dockerhub](https://hub.docker.com/) and make a public repository used to store container images 
for this project. Please change docker repository paths (`[your account]/[repository name]`) to yours
in pod configuration files under `yaml/pods` as well as `scrips/clean_build_docker.sh`.
The following script builds Docker container images for cluster instance pods and upload them to 
your Dockerhub repo. Please use this script whenever you need to update your container images.

```
$ bash scripts/clean_build_docker.sh
```

## Run cluster
We now run our KVS cluster. `run_cluster.py` will initialize the cluster using the
given number of clients and servers. After the initialization, it will execute an  `event trigger`
that allows you to enter and execute kvs API commands.

```
Usage: python3 run_cluster.py -c <# of clients> -s <# of servers>
$ python3 run_cluster.py -c 3 -s 2
Creating a frontend pod...
Creating server pods...
Creating client pods...
Enter a command: listServer
0, 1
Enter a command: addClient
Enter a command: addServer
Enter a command: listServer
0, 1, 2
Enter a command: put:1:1
[Server 1] Receive a put request: Key = 1, Val = 1
Enter a command: put:2:2
[Server 2] Receive a put request: Key = 2, Val = 2
Enter a command: get:1
[Server 1] Receive a get request: Key = 1
Enter a command: get:2
[Server 2] Receive a get request: Key = 2
Enter a command: shutdownServer:0
[Server 0] Receive a request for a normal shutdown
Enter a command: listServer
1, 2
Enter a command: killServer:1
Enter a command: listServer
2
Enter a command: terminate
```

## Monitor cluster instances

```
$ sudo kubectl get pods
NAME           READY   STATUS    RESTARTS   AGE
client-pod-0   1/1     Running   0          45s
client-pod-1   1/1     Running   0          43s
client-pod-2   1/1     Running   0          41s
frontend-pod   1/1     Running   0          51s
server-pod-0   1/1     Running   0          49s
server-pod-1   1/1     Running   0          47s
```

## Clean up cluster
Use the following script to clean up cluster if necessary. It will delete all the container pods
that exist in the cluster.

```
$ bash scripts/delete_pods.sh
```

## Test KVS
We have a `testKVS` command to test our KVS implementation.
This command receives the following arguments.

1. num_keys: total number of keys to be inserted and tested.
2. num_threads: total number of concurrent threads sending requests.
3. num_requests: total number of requests to be sent to the KVS.
4. put_ratio (0 - 100): ratio of put requests among all requests.
5. test_consistency (0 or 1): 0 disables the consistency check, 1 enables it.
If it is disabled, we simply test put/get operations and the following 
arguments (`crash_server`, `add_server`, `remove_server`) will be ignored.
6. crash_server (0 or 1): check consistency while a server crash undergoes (0: disable, 1: enable).
7. add_server (0 or 1): check consistency while a new server addition undergoes (0: disable, 1: enable).
8. remove_server (0 or 1): check consistency while a server removal undergoes (0: disable, 1: enable).

```
$ python run_cluster.py -c 1 -s 2
Creating a frontend pod...
Creating server pods...
Creating client pods...
Enter a command: testKVS:1000:4:1000:0:0:0:0:0
[Warning] Clients should exist more than # of threads
[Warning] Add 3 more clients
Load throughput = 70.4ops/sec
Run throughput = 233.3ops/sec
Enter a command:
```
