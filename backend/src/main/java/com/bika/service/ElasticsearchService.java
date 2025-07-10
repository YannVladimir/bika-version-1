package com.bika.service;

public interface ElasticsearchService {
    // Add methods as needed for your application
    void indexDocument(String index, String id, Object document);
    Object getDocument(String index, String id);
    void deleteDocument(String index, String id);
} 