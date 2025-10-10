package com.smartorder.orderservice.infrastructure.catalog;

import feign.Response;
import feign.codec.ErrorDecoder;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
class CatalogErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            UUID productId = extractProductId(response.request().url());
            return new CatalogNotFoundException(productId);
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }

    private UUID extractProductId(String url) {
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash == -1) {
            return null;
        }
        try {
            return UUID.fromString(url.substring(lastSlash + 1));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

