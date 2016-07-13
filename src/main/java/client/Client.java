package client;

import edu.jhu.hlt.concrete.search.Search;
import edu.jhu.hlt.concrete.search.SearchQuery;
import edu.jhu.hlt.concrete.search.SearchResults;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ted on 7/13/16.
 */
public class Client {

    public static void main(String [] args) throws TException {

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter in the hostname:");
        String inputString = input.nextLine();

        TTransport transport = new TSocket(inputString, 8088);
        transport.open();
        //create the transport means and open the port for communication

        TCompactProtocol protocol = new TCompactProtocol(transport);
        Search.Client client = new Search.Client(protocol);
        //create the protocol and initialize the client

        while(true) {
            System.out.println("Please enter a query here, type exit to quit: ");

            inputString = input.nextLine();
            if (inputString.toLowerCase().equals("exit")) {
                break;
            }
            SearchQuery q = new SearchQuery();
            List<String> queryList = Arrays.asList(inputString.split(" "));
            q.setKeywords(queryList);

            //get a result with the "search" method
            SearchResults r = client.searchSentences(q);
            System.out.println("Result: " + r);
            //print output to user
        }

        transport.close();
        //THIS NEEDS TO BE HERE OR ELSE BAD THINGS HAPPEN. memory leaks, security shit

    }
}
