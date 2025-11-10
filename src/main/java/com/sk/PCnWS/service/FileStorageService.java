package com.sk.PCnWS.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class FileStorageService {

    // Inject secrets from application.properties
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service_key}")
    private String supabaseServiceKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String BUCKET_NAME = "plant-images"; // Your bucket name

    /**
     * Uploads a file to the Supabase bucket using the REST API.
     *
     * @param file The image file to upload
     * @return The public URL of the uploaded image
     * @throws IOException, InterruptedException
     */
    public String uploadFile(MultipartFile file) throws IOException, InterruptedException {

        // 1. Create a unique random filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 2. Get the file content (the raw bytes)
        byte[] fileBytes = file.getBytes();

        // 3. Build the Supabase Storage API URL
        // e.g., https://[project_id].supabase.co/storage/v1/object/plant-images/my-file.png
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        // 4. Build the HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + supabaseServiceKey)
                .header("Content-Type", file.getContentType()) // Use the content type from MultipartFile
                .header("x-upsert", "true") // <-- ADD THIS HEADER to allow overwriting files with the same name
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .build();

        // 5. Send the request
        // We use send() here, which is synchronous (waits for the upload to finish)
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 6. Check for errors
        if (response.statusCode() != 200) {
            // In a real app, you'd parse the JSON error
            throw new IOException("Failed to upload file. Status code: " + response.statusCode() + " | Body: " + response.body());
        }

        // 7. If successful, build and return the public URL.
        // NOTE: This assumes your bucket is "public".
        // e.g., https://[project_id].supabase.co/storage/v1/object/public/plant-images/my-file.png
        String publicUrl = supabaseUrl + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;

        return publicUrl;
    }
}