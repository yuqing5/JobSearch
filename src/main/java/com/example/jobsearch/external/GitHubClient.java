package com.example.jobsearch.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.jobsearch.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class GitHubClient {
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";  //%s : String 占位符
    private static final String DEFAULT_KEYWORD = "engineer";

    public List<Item> search(double lat, double lon, String keyword) {
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD; //if keyword == null, then will set it as the DEFAULT_KEYWORD, "engineer"
        }


        // eg. “hello world” => “hello%20world”
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");    //transfer input keyword to URL
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_TEMPLATE, keyword, lat, lon); //format URL from above

        CloseableHttpClient httpclient = HttpClients.createDefault(); //create a new httpclient object

        // Create a custom response handler, get response in our ideal format
        ResponseHandler<List<Item>> responseHandler = response -> {
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList(); //no results
            }
            HttpEntity entity = response.getEntity(); //entity: content in response
            if (entity == null) {
                return Collections.emptyList(); //no results
            }
            //return EntityUtils.toString(entity); //entity type : application/json
            ObjectMapper mapper = new ObjectMapper();
//            Item[] itemArray = mapper.readValue(entity.getContent(), Item[].class); //.class : is used when there isn't an instance of the class available.
//                                                                                    //also is an object that represents the class Item[] on runtime.
//            return Arrays.asList(itemArray);
            List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            //we didn't extract keywords from previous items, so now we return the items after extracted keywords
            extractKeywords(items);
            return items;

        };

        try {
            return httpclient.execute(new HttpGet(url), responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    //we use this private method to use extract in web by GitHubClient
    private static void extractKeywords(List<Item> items) {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();
        //The articles we used for extracting
        List<String> descriptions = new ArrayList<>();
        for (Item item : items) {
            String description = item.getDescription().replace("·", " ");
            descriptions.add(description);

        }
        //Extract articles, and get keywords as response
        List<Set<String>> keywordList = monkeyLearnClient.extract(descriptions);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setKeywords(keywordList.get(i));
        }
    }
}

