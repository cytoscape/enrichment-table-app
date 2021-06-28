package org.nrnb.gsoc.enrichment.RequestEngine;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For handling API requests to gProfiler. The api firing request must be a task which is run on the task thread
 */
public class HTTPRequestEngine {

    private final String basicURL = "https://biit.cs.ut.ee/gprofiler/api/";
    HashMap<String,String> defaultParameters;

    public HTTPRequestEngine(){
        /**
         * Initializing default parameters
         * Reference for values: https://github.com/PathwayCommons/app-ui/blob/master/src/server/external-services/gprofiler/gprofiler.js
         */
        defaultParameters = new HashMap<>();
        defaultParameters.put("organism",new String("hsapiens"));
        defaultParameters.put("sources","['GO:BP', 'REAC']");
        defaultParameters.put("user_threshold","0.05");
        defaultParameters.put("all_results","false");
        defaultParameters.put("ordered","false");
        defaultParameters.put("combined", "false");
        defaultParameters.put("measure_underrepresentation", "false");
        defaultParameters.put("no_iea", "false");
        defaultParameters.put("domain_scope","annotated");
        defaultParameters.put("numeric_ns","ENTREZGENE_ACC");
        defaultParameters.put("significance_threshold_method","g_SCS");
        defaultParameters.put("background","[]");
        defaultParameters.put("no_evidences", "false");

    }

    public List<String> makePostRequest(String endpoint , Map<String,String> parameters, TaskMonitor monitor) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringBuffer urlConverter = new StringBuffer();
        urlConverter.append(this.basicURL);
        urlConverter.append(endpoint);
        String url = urlConverter.toString();
        HttpPost httpPost = new HttpPost(url);

        String jsonBody = JSONValue.toJSONString(parameters);
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode!=200 && statusCode!=202){
            monitor.showMessage(TaskMonitor.Level.ERROR, "Got "+
                    response.getStatusLine().getStatusCode()+" code from server");
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> strings = new ArrayList<>();
        String line=null;
        while(true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            strings.add(line);
        }
        return strings;
    }
};
