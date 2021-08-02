package org.nrnb.gsoc.enrichment.RequestEngine;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author ighosh98
 * @description Handles all the API requests to gProfiler. The api firing request must be a task which is run on the task thread
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

    public JSONArray makeGetRequest(String endpoint) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringBuffer urlConverter = new StringBuffer();
        urlConverter.append(this.basicURL);
        urlConverter.append(endpoint);
        String url = urlConverter.toString();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode!=200 && statusCode!=202){
            return null;
        }
        JSONArray jsonResponse=null;
        try {
            jsonResponse = (JSONArray) new JSONParser().parse(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public JSONObject makePostRequest(CyNetwork network,String endpoint , Map<String,Object> parameters, TaskMonitor monitor) {
        boolean runDetailedQuery = false;

        if(ModelUtils.getNetUserThreshold(network)!=null){
            runDetailedQuery = true;
            parameters.put("user_threshold",ModelUtils.getNetUserThreshold(network));
            System.out.println(defaultParameters.get("user_threshold"));
        }
        if(ModelUtils.getNetUserThreshold(network)!=null){
            runDetailedQuery = true;
            parameters.put("all_results",ModelUtils.getNetAllResults(network));
            System.out.println(defaultParameters.get("all_results"));
        }
        if(ModelUtils.getNetNoIEA(network)!=null){
            runDetailedQuery = true;
            parameters.put("no_iea",ModelUtils.getNetNoIEA(network));
        }
        if(ModelUtils.getNetMeasureUnderrepresentation(network)!=null){
            runDetailedQuery = true;
            parameters.put("measure_underrepresentation",ModelUtils.getNetMeasureUnderrepresentation(network));
        }

        if(ModelUtils.getNetDomainScope(network)!=null){
            runDetailedQuery = true;
            parameters.put("domain_scope",ModelUtils.getNetDomainScope(network));
            System.out.println(parameters.get("domain_scope"));
        }

        if(ModelUtils.getNetSignificanceThresholdMethod(network)!=null){
            runDetailedQuery = true;
            parameters.put("significance_threshold_method",ModelUtils.getNetSignificanceThresholdMethod(network));
            System.out.println(parameters.get("significance_threshold_method"));
        }

        StringBuffer backgroundNodes = new StringBuffer("");
        List<CyNode> nodeList = network.getNodeList();
        Set<String> nodeNameList = new HashSet<>();
        for (CyNode node : nodeList) {
            String canonicalName;
            if(ModelUtils.getNetGeneIDColumn(network)==null){
                canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
            } else{
                canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(ModelUtils.getNetGeneIDColumn(network), String.class);
            }
            if(canonicalName!=null && canonicalName.length()>0 && !canonicalName.contains(" ")){
                nodeNameList.add(canonicalName);
            }
        }
        Iterator<String> setIterator = nodeNameList.iterator();
        while(setIterator.hasNext()){
            backgroundNodes.append(setIterator.next());
            if(setIterator.hasNext()){
                backgroundNodes.append(" ");
            }
        }
        parameters.put("background",backgroundNodes.toString());
        System.out.println(backgroundNodes.toString());
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringBuffer urlConverter = new StringBuffer();
        urlConverter.append(this.basicURL);
        urlConverter.append(endpoint);
        String url = urlConverter.toString();
        HttpPost httpPost = new HttpPost(url);
        String jsonBody = JSONValue.toJSONString(parameters);
        System.out.println(jsonBody);
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            monitor.setStatusMessage("Could not fetch data. Check your internet connection");
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            monitor.setStatusMessage("Could not fetch data. Check your internet connection");
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode!=200 && statusCode!=202){
            monitor.showMessage(TaskMonitor.Level.ERROR, "Got "+
                    response.getStatusLine().getStatusCode()+" code from server");
            monitor.setStatusMessage("Invalid Query Parameters");
            return null;
        }
        JSONObject jsonResponse=null;
        try {
            jsonResponse = (JSONObject) new JSONParser().parse(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
            monitor.setStatusMessage("Could not fetch data. Check your internet connection");
        } catch (ParseException e) {
            e.printStackTrace();
            monitor.setStatusMessage("Could not fetch data. Check your internet connection");
        }
        return jsonResponse;
    }
};
