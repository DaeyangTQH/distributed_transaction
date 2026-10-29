package com.haichan.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haichan.exception.BusinessException;
import com.haichan.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = readMessage(response);
        return switch (response.status()) {
            case 400, 409 -> new BusinessException(message);
            case 404 -> new ResourceNotFoundException(message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String readMessage(Response response) {
        if (response.body() == null) {
            return "Remote service error";
        }
        try (InputStream body = response.body().asInputStream()) {
            JsonNode node = objectMapper.readTree(body);
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
            return "Remote service error";
        } catch (IOException ex) {
            return "Remote service error";
        }
    }
}
