package com.example.testespringsqs;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/")
public class Controller {

    private final String AccessKey = "AKIASCEAZP62EGIQNG6T";
    private final String SecretKey = "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";
    private final String Region = "us-east-1";
    private final String Service = "sqs";

    @PostMapping
    public String sendMessage(@RequestBody String body) {

        return publicarMessage(body);

    }

    private String publicarMessage(String message) {
        try {
            TreeMap<String, String> params = new TreeMap<String, String>();

            TreeMap<String, String> headers = new TreeMap<String, String>();
            headers.put("host", "sqs.us-east-1.amazonaws.com");
            headers.put("content-type", "application/x-www-form-urlencoded");

            TreeMap<String, String> bodyParams = new TreeMap<>();
            bodyParams.put("Action", "SendMessage");
            bodyParams.put("MessageBody", message);
            bodyParams.put("MessageGroupId", "workflow");

            String payload = bodyParams.keySet().stream()
                    .map(key -> key + "=" + bodyParams.get(key))
                    .collect(Collectors.joining("&"));

            HttpClient client = HttpClient.newHttpClient();

            AWSV4Auth aWSV4Auth = new AWSV4Auth.Builder(AccessKey, SecretKey)
                    .regionName(Region)
                    .serviceName(Service)
                    .httpMethodName("POST")
                    .canonicalURI("/142004027316/nc-dev-workflow-saida.fifo")
                    .queryParametes(params)
                    .awsHeaders(headers)
                    .payload(payload)
                    .build();


            Map<String, String> bHeaders = aWSV4Auth.getHeaders();

            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .uri(URI.create("https://sqs.us-east-1.amazonaws.com/142004027316/nc-dev-workflow-saida.fifo"))
                    .POST(HttpRequest.BodyPublishers.ofString(payload));

            for (Map.Entry<String, String> item : bHeaders.entrySet()) {
                request.header(item.getKey(), item.getValue());
            }

            var bRequest = request.build();

            HttpResponse<String> response = null;

            response = client.send(
                    bRequest,
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
