package com.taller.tallerweb.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    private final String API_KEY = "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"; // ⚠️ Reemplazá con tu API key
    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String obtenerRespuesta(String pregunta) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Sos un asistente técnico automotriz. Respondé de forma clara y profesional dudas sobre mecánica, errores de diagnóstico, aceites, mantenimientos, etc."),
                Map.of("role", "user", "content", pregunta)
        );

        body.put("messages", messages);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Hubo un error al consultar OpenAI: " + e.getMessage();
        }
    }
}
