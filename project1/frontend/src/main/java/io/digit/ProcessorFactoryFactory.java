package io.digit;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

public class ProcessorFactoryFactory implements RequestProcessorFactoryFactory {
    private final RequestProcessorFactory factory = new ProcessorFactory();
    private final FrontendRPC echo;

    public ProcessorFactoryFactory(FrontendRPC echo) {
        this.echo = echo;
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class aClass) throws XmlRpcException {
        return factory;
    }

    private class ProcessorFactory implements RequestProcessorFactory {
        public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest)
                throws XmlRpcException {
            return echo;
        }
    }
}
