package com.recyclewise.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body, HttpSession session) {
        String userMessage = body.get("message");

        // Initialize or retrieve chat history from session
        List<Map<String, String>> history = (List<Map<String, String>>) session.getAttribute("chat_history");
        if (history == null) {
            history = new ArrayList<>();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://recyclewise.biz.id");
        headers.set("X-Title", "RecycleWise EcoBot");

        List<Map<String, Object>> messages = new ArrayList<>();

        // 1. System Prompt (Education & Constraints)
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are EcoBot. " +
                "STRICT UNIVERSAL RULES: " +
                "1. UNIVERSAL TRANSLATION: Detect the user's exact language. Translate EVERYTHING (including bin names like Red, Yellow, Blue, Green, Black) into that language. " +
                "2. NO EXCEPTIONS: Never use English words if the user is speaking another language. " +
                "3. BIN SYSTEM (SOURCE TRUTH): Red (E-waste), Yellow (Plastic), Blue (Paper), Green (Organic), Black (General). " +
                "4. CONCISE: Max 15 words. No fluff.");
        messages.add(systemMsg);

        // 2. Add History (context)
        for (Map<String, String> h : history) {
            messages.add(new HashMap<>(h));
        }

        // 3. Add Current User Message
        Map<String, Object> currentUserMsg = new HashMap<>();
        currentUserMsg.put("role", "user");
        currentUserMsg.put("content", userMessage);
        messages.add(currentUserMsg);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "meta-llama/llama-3.1-8b-instruct");
        request.put("max_tokens", 150);
        request.put("temperature", 0.1);
        request.put("messages", messages);

        System.out.println(">>> EcoBot Call: Using Key [" + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey) + "] Model [" + request.get("model") + "]");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {
            };
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://openrouter.ai/api/v1/chat/completions", java.util.Objects.requireNonNull(HttpMethod.POST), entity, typeRef);

            Map<String, Object> bodyResp = response.getBody();
            if (bodyResp == null || !bodyResp.containsKey("choices")) {
                throw new RuntimeException("AI API returned an empty or invalid response body.");
            }
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) bodyResp.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("AI API response contained no choices.");
            }
            Map<String, Object> assistantMessage = (Map<String, Object>) choices.get(0).get("message");
            String reply = (String) assistantMessage.get("content");

            // Update history and save to session
            history.add(Map.of("role", "user", "content", userMessage));
            history.add(Map.of("role", "assistant", "content", reply));

            // Keep history lean (last 4 messages to avoid language stickiness)
            if (history.size() > 4) {
                history = history.subList(history.size() - 4, history.size());
            }
            session.setAttribute("chat_history", history);

            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("AI API ERROR BODY: " + e.getResponseBodyAsString());
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return ResponseEntity.ok(Map.of("reply", "Sorry, I'm having trouble connecting to the brain. Please try again! ⚠️"));
        } catch (Exception e) {
            System.err.println(">>> GENERAL ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("reply", "Sorry, I'm having trouble connecting to the brain. Please try again! ⚠️"));
        }
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/chat/history")
    public ResponseEntity<List<Map<String, String>>> getHistory(HttpSession session) {
        List<Map<String, String>> history = (List<Map<String, String>>) session.getAttribute("chat_history");
        return ResponseEntity.ok(history != null ? history : Collections.emptyList());
    }
}
