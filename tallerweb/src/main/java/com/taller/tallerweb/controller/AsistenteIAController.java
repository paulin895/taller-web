package com.taller.tallerweb.controller;

import com.taller.tallerweb.service.OpenAIService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*") // permite que el HTML externo pueda hacer la consulta
public class AsistenteIAController {

    private final OpenAIService openAIService;

    // Constructor con inyección del servicio
    public AsistenteIAController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping
    public Map<String, String> responderPregunta(@RequestBody Map<String, String> payload) {
        String pregunta = payload.get("pregunta");
        String respuesta = openAIService.obtenerRespuesta(pregunta);

        Map<String, String> response = new HashMap<>();
        response.put("respuesta", respuesta.trim());
        return response;
    }
}
