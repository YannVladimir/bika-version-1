package com.bika.service;

public interface RedisService {
    // Add methods as needed for your application
    void set(String key, Object value);
    Object get(String key);
    void delete(String key);
} 