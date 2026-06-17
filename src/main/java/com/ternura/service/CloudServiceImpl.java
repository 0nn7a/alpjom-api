package com.ternura.service;

import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudServiceImpl implements CloudService {
    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    private final S3Client s3Client;

    @Override
    public String upload(MultipartFile file) {
        // 1. 空檔案及圖片格式、大小驗證
        validateFile(file);

        // 2. 生成隨機檔名
        String fileName = generateFileName(file);

        try {
            // 3. 構建上傳請求
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType()) // 這行很重要，否則瀏覽器會變下載而不是預覽
                    .build();

            // 4. 執行上傳
            s3Client.putObject(request,
                               RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 5. 回傳可訪問的 URL
            String url = publicUrl + "/" + fileName;
            log.info("圖片上傳成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("讀取上傳檔案失敗: {}", fileName, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "檔案讀取失敗！");
        } catch (Exception e) {
            log.error("R2 上傳失敗: {}", fileName, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "圖片上傳失敗！");
        }
    }

    /**
     * 驗證上傳檔案格式
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "請選擇要上傳的檔案！");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "只允許上傳圖片格式！");
        }

        long MAX_SIZE = 10 * 1024 * 1024; // 限制最大尺寸為 10MB
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "檔案大小不可超過 10MB！");
        }
    }

    /**
     * 產生唯一檔名，防止檔案被覆蓋
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = "";

        if (originalFilename != null) {
            int index = originalFilename.lastIndexOf(".");
            if (index != -1) {
                suffix = originalFilename.substring(index);
            }
        }

        String folder = TimeUtils.now().format(DateTimeFormatter.ofPattern("yyyy/MM/"));
        return folder + UUID.randomUUID() + suffix;
    }

    @Override
    public void deleteBatch(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }

        // 1. 去掉 public url 前綴，取得物件真正的 key
        List<ObjectIdentifier> objects = fileUrls.stream()
                .map(this::extractKey)
                .map(k -> ObjectIdentifier.builder().key(k).build())
                .toList();

        try {
            // 2. 建立批次刪除請求
            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objects).build())
                    .build();

            // 3. 執行刪除並取得回應結果
            DeleteObjectsResponse response = s3Client.deleteObjects(request);

            // * 因為 deleteObjects 若部分失敗不會丟例外，要自己檢查 errors
            if (response.hasErrors() && !response.errors().isEmpty()) {
                response.errors().forEach(err ->
                        log.error("R2 刪除失敗 key={}, code={}, msg={}", err.key(), err.code(), err.message()));
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部分刪除失敗！");
            }

            log.info("批次刪除成功，共 {} 個檔案", objects.size());
        } catch (BusinessException e) {
            throw e; // 上面手動丟的，直接往外拋
        } catch (Exception e) {
            log.error("R2 批次刪除失敗", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批次刪除失敗！");
        }

    }

    /**
     * 從可訪問的公共 URL 還原成 R2 物件 key
     */
    private String extractKey(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "請提供需刪除的檔案路徑！");
        }

        String prefix = publicUrl + "/";
        if (!fileUrl.startsWith(prefix)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "檔案路徑不合法！");
        }

        return fileUrl.substring(prefix.length());
    }
}