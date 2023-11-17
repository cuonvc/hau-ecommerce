package com.kientruchanoi.hauecommerce.service.impl;

import com.kientruchanoi.hauecommerce.service.FileImageService;
import com.kientruchanoi.hauecommerce.utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FileImageServiceImpl implements FileImageService {

    private final FileUploadUtils fileUploadUtils;

    @Override
    public byte[] readFileContent(Path path) {
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return StreamUtils.copyToByteArray(resource.getInputStream());
            } else {
                throw new RuntimeException("Could not read file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read file");
        }
    }
}
