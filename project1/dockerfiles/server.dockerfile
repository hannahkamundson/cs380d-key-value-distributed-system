FROM sekwonlee/kvs:base

USER root

COPY server.py $KVS_HOME/

WORKDIR $KVS_HOME

CMD python3 server.py -i $SERVER_ID
