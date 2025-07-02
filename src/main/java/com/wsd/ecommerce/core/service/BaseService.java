package com.wsd.ecommerce.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsd.ecommerce.core.logger.EcommerceServiceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class BaseService {
    protected EcommerceServiceLogger logger;
    protected ObjectMapper objectMapper;


    @Autowired
    public void setLogger(EcommerceServiceLogger logger) {
        this.logger = logger;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public <T> String writeJsonString(T obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return "";
    }
}
