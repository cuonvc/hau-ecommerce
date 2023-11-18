package com.kientruchanoi.hauecommerce.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

public interface FileImageService {

    byte[] readFileContent(Path path);
}
