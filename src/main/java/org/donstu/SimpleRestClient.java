package org.donstu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.donstu.domain.Delivery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleRestClient {

    private HttpClient client = new DefaultHttpClient();

    public void listDeliveries() {
        HttpGet request = new HttpGet("http://localhost:8091/deliveries/list");
        executeRequest(request);
    }

    public void getDeliveryById(String id) {
        HttpGet request = new HttpGet("http://localhost:8091/deliveries/" + id);
        executeRequest(request);
    }

    public void addDelivery(Delivery delivery) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(delivery);

            HttpPost request = new HttpPost("http://localhost:8091/deliveries/create");
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            executeRequest(request);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting Delivery object to JSON", e);
        }
    }

    private void executeRequest(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while (true) {
                if ((line = reader.readLine()) == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SimpleRestClient deliveryClient = new SimpleRestClient();
        System.out.println("List all deliveries");
        deliveryClient.listDeliveries();
        System.out.println("Delivery by id ");
        deliveryClient.getDeliveryById("1");
        System.out.println("Add new delivery");
        deliveryClient.addDelivery(new Delivery());
    }
}
