package com.example.jobsearch.external;
import com.example.jobsearch.entity.Extraction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.jobsearch.entity.ExtractRequestBody;
import com.example.jobsearch.entity.ExtractResponseItem;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {
    private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "07a3d15c03e46ba56355e0705c3de7adb1a51a6b";

    public List<Set<String>> extract(List<String> articles){ //input : multiple texts; output: list of text's keywords, set makes all keywords unique
        //The Java ObjectMapper is part of the Jackson library
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost(EXTRACT_URL);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Authorization", "Token " + AUTH_TOKEN);
        ExtractRequestBody body = new ExtractRequestBody(articles, 3);

        String jsonBody;
        //transfer body to json
        try {
            //.writeValueAsSting turns Java to Json
            jsonBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }



        try {
            request.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            return Collections.emptyList();
        }
        //transfer response to the type we want, in this case, response --> List<Set<String>>
        ResponseHandler<List<Set<String>>> responseHandler = response -> {
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList(); //
            }
            //.readValue turns Json to Java
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);
            List<Set<String>> keywordList = new ArrayList<>();
            //transfer the extracted keywords to List<Set<String>>
            for (ExtractResponseItem result : results) {
                Set<String> keywords = new HashSet<>();
                for (Extraction extraction : result.extractions) {
                    keywords.add(extraction.parsedValue);
                }
                keywordList.add(keywords);
            }
            return keywordList;
        };

        try {
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();

    }

    //the main let us run MonkeyLearnClient under its own configuration, to let us
    //run under web configuration, we need to switch it back to Tomcat, and add a private
    //method in GitHubClient
    public static void main(String[] args) {

        List<String> articles = Arrays.asList(
                "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.",
                "Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
                "The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
        );

        MonkeyLearnClient client = new MonkeyLearnClient();

        List<Set<String>> keywordList = client.extract(articles);
        System.out.println(keywordList);
    }

}

