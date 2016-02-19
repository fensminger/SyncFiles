package org.fer.syncfiles;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.node.Node;

import java.util.Date;

import static org.elasticsearch.node.NodeBuilder.*;

/**
 * Created by fer on 19/01/15.
 */
public class TestElasticSearch {
    public static void main(String[] args) throws Exception {
        // 172.16.8.198
        // on startup

//        Node node = nodeBuilder().clusterName("elasticsearch").node();
//        Client client2 = node.client();

        // on shutdown

//        node.close();

        System.out.println("--------------------------------------------------------------------------");

        // on startup

        final String hostname = "172.16.8.3";
        Client client2 = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress(hostname, 9300))
                .addTransportAddress(new InetSocketTransportAddress(hostname, 9301))
                .addTransportAddress(new InetSocketTransportAddress(hostname, 9302))
                .addTransportAddress(new InetSocketTransportAddress(hostname, 9303));

        GetResponse response = client2.prepareGet("megacorp", "employee", "6")  // megacorp/employee/6
                .execute()
                .actionGet();
        System.out.println("OK:" + response.getSourceAsString());
        System.out.println("Source:" + response.getSource());


        IndexResponse response2 = client2.prepareIndex("megacorp", "employee", "11")
                .setSource(jsonBuilder()
                                .startObject()
                                .field("user", "kimchy")
                                .field("postDate", new Date())
                                .field("message", "trying out Elasticsearch")
                                .endObject()
                )
                .execute()
                .actionGet();
        System.out.println("Ajout:" + response2.getId());

        SearchResponse resp3 = client2.prepareSearch("megacorp")
                .setTypes("employee")
                .execute()
                .actionGet();
        System.out.println("Search:" + resp3);
        System.out.println("\nHits:" + resp3.getHits().getAt(0).getSourceAsString());


// on shutdown


        client2.close();
    }

}
