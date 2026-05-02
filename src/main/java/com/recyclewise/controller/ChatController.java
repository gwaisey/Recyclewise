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

    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int MAX_TOTAL_HISTORY = 2000;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body, HttpSession session) {
        String userMessage = body.get("message");

        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Please enter a message."));
        }

        String sanitizedMessage = sanitizeInput(userMessage);
        if (sanitizedMessage.length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Message too long. Max " + MAX_MESSAGE_LENGTH + " characters."));
        }

        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("reply", "Please log in to use EcoBot."));
        }

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

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are EcoBot. " +
                "STRICT UNIVERSAL RULES: " +
                "1. UNIVERSAL TRANSLATION: Detect the user's exact language. Translate EVERYTHING (including bin names like Red, Yellow, Blue, Green, Black) into that language. " +
                "2. NO EXCEPTIONS: Never use English words if the user is speaking another language. " +
                "3. BIN SYSTEM (SOURCE TRUTH): Red (E-waste), Yellow (Plastic), Blue (Paper), Green (Organic), Black (General). " +
                "4. CONCISE: Max 15 words. No fluff.");
        messages.add(systemMsg);

        for (Map<String, String> h : history) {
            messages.add(new HashMap<>(h));
        }

        Map<String, Object> currentUserMsg = new HashMap<>();
        currentUserMsg.put("role", "user");
        currentUserMsg.put("content", sanitizedMessage);
        messages.add(currentUserMsg);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "meta-llama/llama-3.1-8b-instruct");
        request.put("max_tokens", 150);
        request.put("temperature", 0.1);
        request.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            if (apiKey == null || apiKey.isBlank() || apiKey.equals("not-configured")) {
               System.err.println("⚠️ EcoBot Error: OpenRouter API key is not configured.");
               return ResponseEntity.ok(Map.of("reply", handleLocalFallback(userMessage)));
            }

            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {};
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

            history.add(Map.of("role", "user", "content", sanitizedMessage));
            history.add(Map.of("role", "assistant", "content", reply));

            if (history.size() > 4) {
                history = history.subList(history.size() - 4, history.size());
            }

            if (getTotalHistoryLength(history) > MAX_TOTAL_HISTORY) {
                history = new ArrayList<>();
            }

            session.setAttribute("chat_history", history);

            return ResponseEntity.ok(Map.of("reply", reply != null ? reply : "No response"));
        } catch (Exception e) {
            System.err.println("⚠️ EcoBot API Exception: " + e.getMessage());
            // Fallback for user experience during API outages or missing keys
            return ResponseEntity.ok(Map.of("reply", handleLocalFallback(userMessage)));
        }
    }

    private String handleLocalFallback(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("inhaler") || msg.contains("medicine")) {
            return "RED bin (E-waste/Hazardous). Inhalers contain metal and residual medicine. Dispose safely! ⚠️";
        } else if (msg.contains("plastic") || msg.contains("bottle")) {
            return "YELLOW bin (Plastic). Make sure to rinse and dry! ♻️";
        } else if (msg.contains("paper") || msg.contains("cardboard")) {
            return "BLUE bin (Paper). Keep it dry and flat! 📄";
        } else if (msg.contains("organic") || msg.contains("food") || msg.contains("leaf")) {
            return "GREEN bin (Organic). Perfect for composting! 🌿";
        } else if (msg.contains("battery") || msg.contains("electronic")) {
            return "RED bin (E-waste). Contains hazardous materials! 🔋";
        }
        return "I'm having trouble connecting to my AI brain, but I'm here! For most items: Red (Hazardous), Yellow (Plastic), Blue (Paper), Green (Organic).";
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        return input
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("/", "&#x2F;")
            .trim();
    }

    private int getTotalHistoryLength(List<Map<String, String>> history) {
        int total = 0;
        for (Map<String, String> msg : history) {
            total += msg.values().stream().mapToInt(v -> v != null ? v.length() : 0).sum();
        }
        return total;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/chat/history")
    public ResponseEntity<List<Map<String, String>>> getHistory(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        List<Map<String, String>> history = (List<Map<String, String>>) session.getAttribute("chat_history");
        return ResponseEntity.ok(history != null ? history : Collections.emptyList());
    }
}
