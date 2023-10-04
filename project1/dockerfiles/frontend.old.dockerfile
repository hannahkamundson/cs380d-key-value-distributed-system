FROM sekwonlee/kvs:base

USER root

COPY frontend.py $KVS_HOME/

WORKDIR $KVS_HOME

CMD python3 frontend.py
