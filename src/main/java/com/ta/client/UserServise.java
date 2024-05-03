package com.ta.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserServise {
    private String token;


    public void createUser(User user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> responseEntity;
        // Create an HttpEntity with the User object and headers
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8080" + "/auth/sign-up", HttpMethod.POST, request, String.class);
        // Print the response
        System.out.println("Server Response: " + response.getBody());


    }

    public ResponseEntity<Map> signIn(User user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> responseEntity;

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", user.getUsername());
        requestBody.put("password", user.getPassword());
        // Create a request entity with headers and body
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        responseEntity = restTemplate.exchange(
                "http://localhost:8080" + "/auth/sign-in", HttpMethod.POST, requestEntity, Map.class);
        System.out.println("sign-in " + responseEntity.getBody());

        return responseEntity;
    }

    public ResponseEntity<String> create(ResponseEntity<Map> response) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> responseBody = response.getBody();

        // Extract the token from the response body
        token = responseBody.get("token");
        HttpHeaders securedHeaders = new HttpHeaders();
        securedHeaders.setBearerAuth(token);


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("characterName", "New Character");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, securedHeaders);

        ResponseEntity<String> protectedResourceResponse = restTemplate.exchange(
                "http://localhost:8080/character/create",
                HttpMethod.POST,
                requestEntity,
                String.class);

        System.out.println("Protected Resource Response: " + protectedResourceResponse.getBody());
        return protectedResourceResponse;
    }

    public TerrainData[] choose() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpHeaders securedHeaders = new HttpHeaders();
        securedHeaders.setBearerAuth(token);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "1");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, securedHeaders);
        ResponseEntity<String> protectedResourceResponse = restTemplate.exchange(
                "http://localhost:8080/character/choose",
                HttpMethod.POST,
                requestEntity,
                String.class);
        // Check if the response is successful
        if (protectedResourceResponse.getStatusCode() == HttpStatus.OK) {
            String responseBody = protectedResourceResponse.getBody();

            System.out.println("choose response " + responseBody.toString());

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                TerrainData[] terrainData = objectMapper.readValue(responseBody, TerrainData[].class);
                return terrainData; // Return the terrainData array
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle unsuccessful response
            System.err.println("Failed to retrieve terrain data. Status code: " + protectedResourceResponse.getStatusCode());
        }
        return null; // Return null if something went wrong or there's no data
    }

    public Map<String, Integer> move(int x, int y) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpHeaders securedHeaders = new HttpHeaders();
        securedHeaders.setBearerAuth(token);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("x", String.valueOf(x));
        requestBody.put("y", String.valueOf(y));
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, securedHeaders);
        ResponseEntity<String> protectedResourceResponse = restTemplate.exchange(
                "http://localhost:8080/character/move",
                HttpMethod.POST,
                requestEntity,
                String.class);
        System.out.println("Move " + protectedResourceResponse.getBody());
        if (protectedResourceResponse.getStatusCode() == HttpStatus.OK) {
            String responseBody = protectedResourceResponse.getBody();

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Integer> charCoords = objectMapper.readValue(responseBody, new TypeReference<Map<String, Integer>>() {
                });
                return charCoords;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle unsuccessful response
            System.err.println("Failed to retrieve terrain data. Status code: " + protectedResourceResponse.getStatusCode());
        }
        return null; // Return null if something went wrong or there's no data
    }

    public void selectTarget(Integer id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpHeaders securedHeaders = new HttpHeaders();
        securedHeaders.setBearerAuth(token);

        Long requestBody = 10L;

        HttpEntity<Long> requestEntity = new HttpEntity<>(requestBody, securedHeaders);
        ResponseEntity<String> protectedResourceResponse = restTemplate.exchange(
                "http://localhost:8080/character/selectTarget",
                HttpMethod.POST,
                requestEntity,
                String.class);
        if (protectedResourceResponse.getStatusCode() == HttpStatus.OK) {
            String responseBody = protectedResourceResponse.getBody();
            System.out.println("select target " + responseBody);
        }

    }
}
