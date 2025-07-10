package com.bika.service;

public interface MinioService {
    // Add methods as needed for your application
    void uploadFile(String bucketName, String objectName, byte[] data);
    byte[] downloadFile(String bucketName, String objectName);
    void deleteFile(String bucketName, String objectName);
} 