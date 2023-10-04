FROM sekwonlee/kvs:base

USER root

COPY client.py $KVS_HOME/

WORKDIR $KVS_HOME

CMD python3 client.py -i $CLIENT_ID
