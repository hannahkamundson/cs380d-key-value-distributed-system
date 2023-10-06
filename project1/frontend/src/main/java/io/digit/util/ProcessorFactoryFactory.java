package io.digit.util;

import io.digit.FrontendRPC;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

public class ProcessorFactoryFactory implements RequestProcessorFactoryFactory {
    private final RequestProcessorFactory factory = new ProcessorFactory();
    private final FrontendRPC rpc;

    public ProcessorFactoryFactory(FrontendRPC rpc) {
        this.rpc = rpc;
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class aClass) throws XmlRpcException {
        return factory;
    }

    private class ProcessorFactory implements RequestProcessorFactory {
        public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest)
                throws XmlRpcException {
            return rpc;
        }
    }
}
