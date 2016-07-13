package server;

import edu.jhu.hlt.concrete.search.Search;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by ted on 7/13/16.
 */
public class Server {

    private static ServerHandler handler;

    private static TProcessor processor;

    public static void main(String[] args) {
        try {
            handler = new ServerHandler();
            processor = new Search.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private static void simple(TProcessor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(8088);
            TServer.Args args = new TServer.Args(serverTransport)
                    .processor(processor)
                    .inputProtocolFactory(new TCompactProtocol.Factory())
                    .outputProtocolFactory(new TCompactProtocol.Factory());


            TServer server = new TSimpleServer(args);

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
