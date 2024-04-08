package org.donstu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.donstu.domain.Delivery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SimpleRestService {
    private static final int PORT = 8091;
    private static final int OK = 200;
    private static final int NOT_ALLOWED = 405;
    private static final int NOT_FOUND = 404;

    private static List<Delivery> deliveries = new ArrayList<>();
    private static Random RANDOM = new Random();

    static {
        // Add some initial deliveries
        deliveries.add(new Delivery("1", "Order-1", "123 Main St", new Date(), "In Transit"));
        deliveries.add(new Delivery("2", "Order-2", "456 Maple Ave", new Date(), "Delivered"));
    }

    public static void main(String[] args) {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/deliveries/create", SimpleRestService::handleCreate);
            httpServer.createContext("/deliveries/list", SimpleRestService::handleList);
            httpServer.createContext("/deliveries", SimpleRestService::handleGetDelivery);
            httpServer.setExecutor(null);
            System.out.println("Rest server started: http://localhost:" + PORT);
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleCreate(HttpExchange httpExchange) throws IOException {
        if ("POST".equals(httpExchange.getRequestMethod())) {
            ObjectMapper mapper = new ObjectMapper();
            Delivery delivery = mapper.readValue(httpExchange.getRequestBody(), Delivery.class);
            delivery.setDeliveryId("#"+(deliveries.size()+1));
            deliveries.add(delivery);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, delivery);
            byte[] body = baos.toByteArray();
            sendResponse(httpExchange, OK, body);
        } else {
            httpExchange.sendResponseHeaders(NOT_ALLOWED, -1);
        }
    }

    private static void handleList(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, deliveries);
            byte[] body = baos.toByteArray();
            sendResponse(httpExchange, OK, body);
        } else {
            httpExchange.sendResponseHeaders(NOT_ALLOWED, -1);
        }
    }

    private static void handleGetDelivery(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String[] requestParts = httpExchange.getRequestURI().getPath().split("/");
            if (requestParts.length == 3) {
                String deliveryId = requestParts[2];
                Delivery foundDelivery = null;
                for (Delivery delivery : deliveries) {
                    if (delivery.getDeliveryId().equalsIgnoreCase(deliveryId)) {
                        foundDelivery = delivery;
                        break;
                    }
                }
                if (foundDelivery != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mapper.writeValue(baos, foundDelivery);
                    byte[] body = baos.toByteArray();
                    sendResponse(httpExchange, OK, body);
                } else {
                    sendResponse(httpExchange, NOT_FOUND, "{}");
                }
            } else {
                httpExchange.sendResponseHeaders(NOT_FOUND, -1);
            }
        } else {
            httpExchange.sendResponseHeaders(NOT_ALLOWED, -1);
        }
    }

    private static void sendResponse(HttpExchange httpExchange, int statusCode, String response) throws IOException {
        sendResponse(httpExchange, statusCode, response.getBytes());
    }

    private static void sendResponse(HttpExchange httpExchange, int statusCode, byte[] response) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(statusCode, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
