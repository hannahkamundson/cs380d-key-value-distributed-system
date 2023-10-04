FROM ubuntu:20.04

USER root

ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

RUN apt-get update && apt-get install -y git

# TODO
# RUN git clone https://github.com/vijay03/cs380d-f23.git

ENV KVS_HOME /cs380d-f23/project1

# Install dependencies
COPY scripts/dependencies2.sh ${KVS_HOME}/scripts/dependencies2.sh
RUN bash ${KVS_HOME}/scripts/dependencies2.sh