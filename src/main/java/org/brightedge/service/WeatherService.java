package org.brightedge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.brightedge.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherService {

    @Value("${owm.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherResponse getWeather(String city) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing OWM API key");
        }

        System.out.println("Using OWM API Key: " + apiKey);
        System.out.println("Fetching weather for city: " + city);

        try {
            // Step 1: Call OpenWeatherMap API
            String rawResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.openweathermap.org")
                            .path("/data/2.5/weather")
                            .queryParam("q", city)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), response ->
                            response.bodyToMono(String.class).flatMap(body -> {
                                System.out.println("API returned non-2xx status: " + body);
                                return reactor.core.publisher.Mono.error(
                                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid city"));
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Raw API response:");
            System.out.println(rawResponse);

            // Step 2: Detect city not found response (cod = 404)
            if (rawResponse.contains("\"cod\":\"404\"") || rawResponse.contains("\"cod\":404")) {
                System.out.println("City not found: " + city);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
            }

            // Step 3: Parse valid response into WeatherResponse
            WeatherResponse response = objectMapper.readValue(rawResponse, WeatherResponse.class);
            return response;

        } catch (WebClientResponseException e) {
            System.out.println("WebClientResponseException: " + e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error calling weather API");
            }
        } catch (ResponseStatusException e) {
            throw e; // Re-throw known handled cases
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error calling weather API");
        }
    }
}