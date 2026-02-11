package com.laptophub.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.url}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"
                )
        );
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            // Formato típico: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{public_id}.{ext}
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            String path = url.substring(uploadIndex + "/upload/".length());
            if (path.startsWith("v")) {
                int slashIndex = path.indexOf("/");
                if (slashIndex > 1 && isAllDigits(path.substring(1, slashIndex))) {
                    path = path.substring(slashIndex + 1);
                }
            }

            int dotIndex = path.lastIndexOf(".");
            if (dotIndex > 0) {
                path = path.substring(0, dotIndex);
            }

            return path.isBlank() ? null : path;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAllDigits(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
