package com.eventhub.event_service.infrastructure.adapter.out.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:eventhub-branding-prod}")
    private String bucketName;

    @Value("${aws.s3.public-url:}")
    private String publicUrl;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String fileKey = folder + "/" + UUID.randomUUID() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Construcción correcta de la URL para AWS S3
            String fileUrl;
            if (publicUrl != null && !publicUrl.isBlank()) {
                // Elimina barra final si existe
                String cleanBaseUrl = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;

                // Si la URL base ya incluye el bucket en el subdominio, no lo duplicamos
                if (cleanBaseUrl.contains(bucketName)) {
                    fileUrl = cleanBaseUrl + "/" + fileKey;
                } else {
                    fileUrl = cleanBaseUrl + "/" + bucketName + "/" + fileKey;
                }
            } else {
                fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileKey);
            }

            log.info("Archivo subido exitosamente a S3/MinIO: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("Error al subir archivo a S3/MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Error al almacenar la imagen de branding", e);
        }
    }
}