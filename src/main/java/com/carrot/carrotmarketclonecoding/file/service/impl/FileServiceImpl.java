package com.carrot.carrotmarketclonecoding.file.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.carrot.carrotmarketclonecoding.common.exception.FileExtensionNotValidException;
import com.carrot.carrotmarketclonecoding.common.exception.FileUploadFailedException;
import com.carrot.carrotmarketclonecoding.common.utils.DecodeUtil;
import com.carrot.carrotmarketclonecoding.file.service.FileService;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final String[] SUPPORTED_FILE_EXTENSIONS = {"image/jpeg", "image/png"};

    private final AmazonS3 amazonS3Client;

    @Value("${s3.board.bucket}")
    private String bucket;

    @Override
    public String uploadImage(MultipartFile file) {
        ObjectMetadata objectMetadata = createObjectMetadata(file);
        String objectKey = createObjectKey(UUID.randomUUID().toString().toUpperCase(), file.getOriginalFilename());

        log.debug("파일을 업로드합니다! {}", file.getOriginalFilename());
        log.debug("확장자: {}", file.getContentType());
        validateExtension(file.getContentType());

        log.debug("originalFilename: {}", file.getOriginalFilename());
        uploadImageToS3(file, objectKey, objectMetadata);

        return amazonS3Client.getUrl(bucket, objectKey).toString();
    }

    @Override
    public void deleteUploadedImage(String url) {
        if (isUploadedImageExists(url)) {

            log.debug("기존 사진을 삭제합니다! {}", url);

            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, extractObjectKey(url)));
        }
    }

    private void validateExtension(String contentType) {
        if (!StringUtils.hasText(contentType) || Arrays.stream(SUPPORTED_FILE_EXTENSIONS).noneMatch(contentType::equals)) {
            throw new FileExtensionNotValidException();
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    private void uploadImageToS3(MultipartFile file, String objectKey, ObjectMetadata objectMetadata) {
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(
                            bucket,
                            objectKey,
                            file.getInputStream(),
                            objectMetadata
                    ));
        } catch (IOException e) {
            throw new FileUploadFailedException();
        }
    }

    private Boolean isUploadedImageExists(String url) {
        try {
            String objectKey = DecodeUtil.decodeByUtf8(extractObjectKey(url));
            amazonS3Client.getObject(bucket, objectKey);
            return true;
        } catch (Exception e) {
            log.debug("사진이 존재하지 않습니다!");
            return false;
        }
    }

    private String extractObjectKey(String url) {
        String[] parts = url.split("/");
        if (parts.length > 0) {
            return DecodeUtil.decodeByUtf8(parts[parts.length - 1]);
        }
        return "";
    }

    private String createObjectKey(String randomFilename, String originalFilename) {
        return randomFilename + "_" + originalFilename;
    }
}
