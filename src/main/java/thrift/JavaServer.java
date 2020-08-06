package thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import tutorial.Calculator;

public class JavaServer {

    public static CalculatorHandler handler;

    public static Calculator.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new CalculatorHandler();
            processor = new Calculator.Processor(handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor);
                }
            };

            Runnable secure = new Runnable() {
                @Override
                public void run() {
                    secure(processor);
                }
            };

            new Thread(simple).start();
            new Thread(secure).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void simple(Calculator.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void secure(Calculator.Processor processor) {
        try {
            TSSLTransportFactory.TSSLTransportParameters params =
                    new TSSLTransportFactory.TSSLTransportParameters();
            params.setKeyStore("../../lib/java/test/.keystore",
                    "thrift",
                    null,
                    null);

            TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(
                    9091,
                    0,
                    null,
                    params);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the secure server...");
            server.serve();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
