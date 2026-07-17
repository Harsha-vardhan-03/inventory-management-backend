 
package com.inventory.modules.chatbot.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.chatbot.dto.ChatRequest;
import com.inventory.modules.chatbot.dto.ChatResponse;
import com.inventory.modules.chatbot.services.ChatbotService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    @PostMapping("/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatResponse>> processMessage(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody ChatRequest request) {
        log.info("Processing chat message from user: {}", currentUser.getId());
        ChatResponse response = chatbotService.processMessage(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/suggestions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<String> suggestions = chatbotService.getSuggestions(currentUser);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }
    
    @PostMapping("/clear-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> clearHistory(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        chatbotService.clearHistory(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Chat history cleared"));
    }
}