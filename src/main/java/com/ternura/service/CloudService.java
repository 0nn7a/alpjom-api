package com.ternura.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface CloudService {
    String upload(MultipartFile file);
    void deleteBatch(List<String> fileUrls);
}
