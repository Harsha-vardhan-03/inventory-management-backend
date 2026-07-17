package com.inventory.modules.chatbot.services;

import com.inventory.common.enums.Role;
import com.inventory.modules.chatbot.dto.ChatRequest;
import com.inventory.modules.chatbot.dto.ChatResponse;
import com.inventory.modules.chatbot.utils.IntentClassifier;
import com.inventory.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    
    private final IntentClassifier intentClassifier;
    private final Map<Long, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    
    public ChatResponse processMessage(UserDetailsImpl currentUser, ChatRequest request) {
        String userMessage = request.getMessage().toLowerCase();
        log.info("Processing message: {} from user: {}", userMessage, currentUser.getId());
        
        storeMessage(currentUser.getId(), "user", userMessage);
        String intent = intentClassifier.classifyIntent(userMessage);
        log.info("Classified intent: {}", intent);
        
        ChatResponse response = processIntent(currentUser, userMessage, intent);
        storeMessage(currentUser.getId(), "bot", response.getMessage());
        
        return response;
    }
    
    private ChatResponse processIntent(UserDetailsImpl currentUser, String message, String intent) {
        // Convert Role enum to String for comparison
        String role = currentUser.getRole().name();
        
        switch (intent) {
            case "GREETING":
                return handleGreeting(currentUser);
                
            case "THANKS":
                return handleThanks(currentUser);
                
            case "REGISTER_OWNER":
                return ChatResponse.builder()
                    .message("To register as a Company Owner, please use the registration form.\nClick 'Register as Company Owner' on the login page.")
                    .intent("REGISTER_OWNER")
                    .actions(List.of("navigate", "/register/owner"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "REGISTER_SUPPLIER":
                return ChatResponse.builder()
                    .message("To register as a Supplier, please use the registration form.\nClick 'Register as Supplier' on the login page.")
                    .intent("REGISTER_SUPPLIER")
                    .actions(List.of("navigate", "/register/supplier"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "LOGIN":
                return ChatResponse.builder()
                    .message("Please go to the login page to access your account.")
                    .intent("LOGIN")
                    .actions(List.of("navigate", "/login"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "VIEW_PRODUCTS":
                String productPath = role.equals("SUPPLIER") ? "/supplier/products" : "/inventory";
                return ChatResponse.builder()
                    .message("Navigating to Products page.")
                    .intent("VIEW_PRODUCTS")
                    .actions(List.of("navigate", productPath))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "ADD_PRODUCT":
                if (role.equals("SUPPLIER")) {
                    return ChatResponse.builder()
                        .message("Navigate to Add Product page to add new products to your catalog.")
                        .intent("ADD_PRODUCT")
                        .actions(List.of("navigate", "/supplier/products/add"))
                        .timestamp(LocalDateTime.now())
                        .build();
                } else {
                    return ChatResponse.builder()
                        .message("Only suppliers can add products. Please register as a supplier.")
                        .timestamp(LocalDateTime.now())
                        .build();
                }
                
            case "LOW_STOCK":
                return ChatResponse.builder()
                    .message("Showing products with low stock.")
                    .intent("LOW_STOCK")
                    .actions(List.of("navigate", "/inventory/products/low-stock"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "CHECK_STOCK":
                return ChatResponse.builder()
                    .message("Navigate to Inventory page to check stock levels.")
                    .intent("CHECK_STOCK")
                    .actions(List.of("navigate", "/inventory"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "STOCK_MOVEMENTS":
                return ChatResponse.builder()
                    .message("Navigate to Stock Movements page to view history.")
                    .intent("STOCK_MOVEMENTS")
                    .actions(List.of("navigate", "/inventory/movements"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "CREATE_PURCHASE":
                if (!role.equals("SUPPLIER")) {
                    return ChatResponse.builder()
                        .message("Navigate to Create Purchase Request page.")
                        .intent("CREATE_PURCHASE")
                        .actions(List.of("navigate", "/purchase-requests/create"))
                        .timestamp(LocalDateTime.now())
                        .build();
                } else {
                    return ChatResponse.builder()
                        .message("Suppliers receive purchase requests from companies. You cannot create them.")
                        .timestamp(LocalDateTime.now())
                        .build();
                }
                
            case "VIEW_PURCHASES":
                String purchasePath = role.equals("SUPPLIER") ? "/supplier/purchase-requests" : "/purchase-requests";
                return ChatResponse.builder()
                    .message("Navigate to Purchase Requests page.")
                    .intent("VIEW_PURCHASES")
                    .actions(List.of("navigate", purchasePath))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "CREATE_SALE":
                return ChatResponse.builder()
                    .message("Navigate to Create Sale page to make a new sale.")
                    .intent("CREATE_SALE")
                    .actions(List.of("navigate", "/sales/create"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "VIEW_SALES":
                return ChatResponse.builder()
                    .message("Navigate to Sales Orders page.")
                    .intent("VIEW_SALES")
                    .actions(List.of("navigate", "/sales"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "ADD_CUSTOMER":
                return ChatResponse.builder()
                    .message("Navigate to Customers page to add new customers.")
                    .intent("ADD_CUSTOMER")
                    .actions(List.of("navigate", "/sales/customers"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "VIEW_CUSTOMERS":
                return ChatResponse.builder()
                    .message("Navigate to Customers page.")
                    .intent("VIEW_CUSTOMERS")
                    .actions(List.of("navigate", "/sales/customers"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "VIEW_REPORT":
                return ChatResponse.builder()
                    .message("Navigate to Reports Dashboard.")
                    .intent("VIEW_REPORT")
                    .actions(List.of("navigate", "/reports/dashboard"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "DASHBOARD":
                return ChatResponse.builder()
                    .message("Navigate to Dashboard.")
                    .intent("DASHBOARD")
                    .actions(List.of("navigate", "/dashboard"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "VIEW_SUPPLIERS":
                return ChatResponse.builder()
                    .message("Navigate to Suppliers page.")
                    .intent("VIEW_SUPPLIERS")
                    .actions(List.of("navigate", "/suppliers"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
            case "HELP":
                return getHelpMessage(role);
                
            default:
                return ChatResponse.builder()
                    .message("I'm not sure how to help with that. 😕\n\n" + getRoleSpecificSuggestions(role) + "\n\nType 'Help' to see all available commands.")
                    .intent("UNKNOWN")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
    
    private ChatResponse handleGreeting(UserDetailsImpl currentUser) {
        String role = currentUser.getRole().name();
        String name = currentUser.getName();
        String greeting;
        
        String[] greetings = {
            "Hello %s! 👋 How can I assist you with your inventory today?",
            "Hi %s! 👋 Welcome back! What can I help you with?",
            "Greetings %s! 👋 Ready to manage your inventory?",
            "Hey %s! 👋 How can I help you today?",
            "Namaste %s! 👋 What would you like to do?",
            "Good to see you %s! 👋 How may I assist you?",
            "Welcome back %s! 👋 What brings you here today?"
        };
        
        Random random = new Random();
        greeting = String.format(greetings[random.nextInt(greetings.length)], name);
        
        String suggestions = getRoleSpecificSuggestions(role);
        
        return ChatResponse.builder()
            .message(greeting + "\n\n" + suggestions)
            .intent("GREETING")
            .actions(new ArrayList<>())
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    private ChatResponse handleThanks(UserDetailsImpl currentUser) {
        String[] thanks = {
            "You're welcome! 😊 Is there anything else I can help you with?",
            "Happy to help! 👍 Need anything else?",
            "My pleasure! 🎯 What would you like to do next?",
            "Anytime! 💪 Let me know if you need anything else.",
            "Glad I could help! 😊 What's next on your mind?"
        };
        
        Random random = new Random();
        String response = thanks[random.nextInt(thanks.length)];
        
        return ChatResponse.builder()
            .message(response)
            .intent("THANKS")
            .actions(new ArrayList<>())
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    private String getRoleSpecificSuggestions(String role) {
        if (role.equals("SUPPLIER")) {
            return "💡 Here's what I can help you with:\n" +
                   "• Add new products to your catalog\n" +
                   "• View your product list\n" +
                   "• Check low stock items\n" +
                   "• View incoming purchase requests\n" +
                   "• Track stock movements\n" +
                   "• Generate sales reports";
        } else if (role.equals("OWNER") || role.equals("ADMIN")) {
            return "💡 Here's what I can help you with:\n" +
                   "• Create new sales orders\n" +
                   "• View all sales orders\n" +
                   "• Create purchase requests\n" +
                   "• Check inventory stock levels\n" +
                   "• Add new customers\n" +
                   "• View suppliers catalog\n" +
                   "• Generate reports and analytics";
        } else {
            return "💡 Please login or register to access all features:\n" +
                   "• Register as Company Owner\n" +
                   "• Register as Supplier\n" +
                   "• Login to your existing account";
        }
    }
    
    private ChatResponse getHelpMessage(String role) {
        String helpText;
        
        if (role.equals("SUPPLIER")) {
            helpText = "🤖 I can help you with:\n" +
                "• 'Add product' - Add new products to your catalog\n" +
                "• 'View my products' - See all your products\n" +
                "• 'Low stock' - View products with low stock\n" +
                "• 'Stock movements' - View stock history\n" +
                "• 'View purchase requests' - See incoming orders\n" +
                "• 'View connections' - See connected companies\n" +
                "• 'View reports' - See sales and inventory reports\n" +
                "• 'Help' - Show this message\n\n" +
                "💡 Just type what you want to do!";
        } else if (role.equals("OWNER") || role.equals("ADMIN")) {
            helpText = "🤖 I can help you with:\n" +
                "• 'Add customer' - Register a new customer\n" +
                "• 'Create sale' - Create a new sales order\n" +
                "• 'View sales' - See all sales orders\n" +
                "• 'Create purchase' - Create purchase request\n" +
                "• 'View products' - See inventory products\n" +
                "• 'Check stock' - View current stock levels\n" +
                "• 'View reports' - See sales and inventory reports\n" +
                "• 'View suppliers' - Browse and connect with suppliers\n" +
                "• 'Help' - Show this message\n\n" +
                "💡 Just type what you want to do!";
        } else {
            helpText = "🤖 I can help you with:\n" +
                "• 'Register as owner' - Register a new company\n" +
                "• 'Register as supplier' - Register as a supplier\n" +
                "• 'Login' - Access your account\n" +
                "• 'Help' - Show this message\n\n" +
                "💡 Please login or register to access all features!";
        }
        
        return ChatResponse.builder()
            .message(helpText)
            .intent("HELP")
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    private void storeMessage(Long userId, String sender, String message) {
        Map<String, String> chatMessage = new HashMap<>();
        chatMessage.put("sender", sender);
        chatMessage.put("message", message);
        chatMessage.put("timestamp", LocalDateTime.now().toString());
        
        conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(chatMessage);
        
        List<Map<String, String>> history = conversationHistory.get(userId);
        if (history.size() > 50) {
            history.remove(0);
        }
    }
    
    public List<String> getSuggestions(UserDetailsImpl currentUser) {
        String role = currentUser.getRole().name();
        
        if (role.equals("SUPPLIER")) {
            return Arrays.asList(
                "Add product", "View my products", "Low stock", 
                "Stock movements", "View purchase requests", "View reports", "Help"
            );
        } else if (role.equals("OWNER") || role.equals("ADMIN")) {
            return Arrays.asList(
                "Create sale", "View sales", "Create purchase", "View products",
                "Check stock", "View reports", "Add customer", "View suppliers", "Help"
            );
        } else {
            return Arrays.asList(
                "Register as owner", "Register as supplier", "Login", "Help"
            );
        }
    }
    
    public void clearHistory(Long userId) {
        conversationHistory.remove(userId);
    }
}