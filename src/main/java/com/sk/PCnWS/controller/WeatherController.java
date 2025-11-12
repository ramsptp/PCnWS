package com.sk.PCnWS.controller;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final RestTemplate restTemplate;

    public WeatherController(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(7))
                .build();
    }

    /**
     * Robust proxy for reverse geocoding:
     * 1) Try Open-Meteo geocoding reverse
     * 2) If the response is non-2xx or indicates an error, fallback to Nominatim (OpenStreetMap)
     *
     * Returns the JSON body from whichever service succeeded (as plain JSON).
     *
     * Example: /api/geocode?latitude=9.91&longitude=76.27
     */
    @GetMapping("/geocode")
        public ResponseEntity<String> reverseGeocode(
                @RequestParam double latitude,
                @RequestParam double longitude) {

            String openMeteoUrl = "https://geocoding-api.open-meteo.com/v1/reverse?latitude="
                    + latitude + "&longitude=" + longitude;

            log.info("Attempting Open-Meteo reverse geocode: {}", openMeteoUrl);

            try {
                ResponseEntity<String> resp = restTemplate.exchange(
                        URI.create(openMeteoUrl),
                        HttpMethod.GET,
                        null,
                        String.class);

                HttpStatusCode status = resp.getStatusCode();   // <-- use HttpStatusCode here
                String body = resp.getBody();

                if (status.is2xxSuccessful() && body != null && !body.isBlank()) {
                    log.info("Open-Meteo geocode successful ({}). Returning response to client.", status.value());
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    return new ResponseEntity<>(body, headers, HttpStatus.OK);
                } else {
                    log.warn("Open-Meteo geocode returned non-success {} or empty body. Falling back.", status.value());
                }
            } catch (HttpClientErrorException hcee) {
                log.warn("Open-Meteo geocode HTTP error: {} - falling back to Nominatim. Body: {}", hcee.getStatusCode(), hcee.getResponseBodyAsString());
            } catch (RestClientException rce) {
                log.error("Open-Meteo geocode request failed: {}", rce.getMessage());
            } catch (Exception ex) {
                log.error("Unexpected error while calling Open-Meteo geocode", ex);
            }

            // fallback code (Nominatim) remains unchanged...
            try {
                String nominatimUrl = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat="
                        + latitude + "&lon=" + longitude + "&zoom=10&addressdetails=1";

                log.info("Attempting Nominatim reverse geocode: {}", nominatimUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.USER_AGENT, "PCnWS/1.0 (your-email@example.com)");
                headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<String> nomResp = restTemplate.exchange(
                        URI.create(nominatimUrl),
                        HttpMethod.GET,
                        entity,
                        String.class);

                if (nomResp.getStatusCode().is2xxSuccessful() && nomResp.getBody() != null) {
                    log.info("Nominatim reverse geocode successful. Returning response to client.");
                    HttpHeaders outHeaders = new HttpHeaders();
                    outHeaders.setContentType(MediaType.APPLICATION_JSON);
                    return new ResponseEntity<>(nomResp.getBody(), outHeaders, HttpStatus.OK);
                } else {
                    log.warn("Nominatim returned non-success {}.", nomResp.getStatusCodeValue());
                }
            } catch (RestClientException ex) {
                log.error("Nominatim geocode request failed: {}", ex.getMessage(), ex);
            } catch (Exception ex) {
                log.error("Unexpected error calling Nominatim", ex);
            }

            String err = "{\"error\":true, \"reason\":\"Failed to resolve location from coordinates\"}";
            log.error("Both geocoding providers failed for {},{}", latitude, longitude);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(err, headers, HttpStatus.BAD_GATEWAY);
        }

    /**
     * Optional helper endpoint that returns both weather and (best-effort) location in one call.
     * Frontend can use this to make only one network trip if desired.
     */
    @GetMapping("/weather-and-location")
    public ResponseEntity<String> weatherAndLocation(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
            ResponseEntity<String> weatherResp = restTemplate.getForEntity(URI.create(weatherUrl), String.class);

            // Reuse the /geocode logic by calling the method (but avoid recursion). Simpler: call internal geocode method.
            ResponseEntity<String> geoResp = reverseGeocode(latitude, longitude);

            String weatherJson = weatherResp.getStatusCode().is2xxSuccessful() && weatherResp.getBody() != null ? weatherResp.getBody() : "{}";
            String geoJson = geoResp.getStatusCode().is2xxSuccessful() && geoResp.getBody() != null ? geoResp.getBody() : "{}";

            String combined = String.format("{\"weather\": %s, \"geocode\": %s}", weatherJson, geoJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(combined, headers, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error fetching weather+geocode", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to fetch weather or geocode\"}");
        }
    }
}
