package org.nrnb.gsoc.enrichment.RequestEngine;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.concurrent.*;

public class ScheduledRequestEngine {

    private static FutureTask<CloseableHttpResponse> sentRequestTask = null;

    /**
     * Sends a HttpPost request through HttpClient, throws exception if time limit exceeds,
     * else returns the Response.
     *
     * @param httpClient Client through which Post Request to be sent.
     * @param httpPost   Post data to be sent.
     * @param timeout    Maximum time in seconds to wait until throw exception.
     * @return           {@code CloseableHttpResponse}, if response received within time limit.
     * @throws IOException If response not received within time limit.
     */
    public static CloseableHttpResponse sendPostRequestWithTimeLimit(CloseableHttpClient httpClient, HttpPost httpPost,
                                                                     long timeout)
            throws IOException, InterruptedException {

        sentRequestTask = new FutureTask<>(() -> httpClient.execute(httpPost));

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(sentRequestTask);

        try {
            return sentRequestTask.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException exception) {
            throw new IOException();
        }
    }

    /**
     * Cancels the current http request if sent.
     */
    public static void stopPostRequest() {
        if (sentRequestTask != null) {
            sentRequestTask.cancel(true);
        }
    }
}
