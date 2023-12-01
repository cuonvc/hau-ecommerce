package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.kientruchanoi.ecommerce.authservicecore.service.FileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileImageServiceImpl implements FileImageService {

    @Value("${application.firebase.bucket-name}")
    private String bucketName;

    @Value("${application.firebase.image-url}")
    private String imageUrl;

    @EventListener
    public void init(ApplicationReadyEvent event) {

        // initialize Firebase
        try {

            ClassPathResource serviceAccount = new ClassPathResource("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(bucketName)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String saveImageFile(byte[] fileBytes)  {
        Bucket bucket = StorageClient.getInstance().bucket();
        String name = UUID.randomUUID().toString() + "png";
        try {
            bucket.create(name, fileBytes, "image/png");
            return String.format(imageUrl,
                    bucketName, name);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
