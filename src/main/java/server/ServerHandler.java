package server;

import edu.jhu.hlt.concrete.search.Search;
import edu.jhu.hlt.concrete.search.SearchQuery;
import edu.jhu.hlt.concrete.search.SearchResults;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import org.apache.thrift.TException;

/**
 * Created by ted on 7/13/16.
 */
public class ServerHandler implements Search.Iface{

    @Override
    public SearchResults searchCommunications(SearchQuery searchQuery) throws TException {
        return null;
    }

    public SearchResults searchSentences(SearchQuery query) throws TException {
        System.out.println("Your query is " + query);
        SearchResults SR = new SearchResults();
        SR.setSearchQuery(query);
        return SR;
    }

    @Override
    public ServiceInfo about() throws TException {
        return null;
    }

    @Override
    public boolean alive() throws TException {
        return false;
    }
}
