package com.inventory.modules.chatbot.utils;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class IntentClassifier {
    
    private static final Map<Pattern, String> INTENT_PATTERNS = new HashMap<>();
    
    static {
        // Greeting Intents
        INTENT_PATTERNS.put(Pattern.compile("^(hi|hello|hey|hii|hiii|hello there|hi there|hey there|greetings|namaste|hola|good morning|good afternoon|good evening).*", Pattern.CASE_INSENSITIVE), "GREETING");
        
        // Authentication Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(register|create).*(owner|company).*", Pattern.CASE_INSENSITIVE), "REGISTER_OWNER");
        INTENT_PATTERNS.put(Pattern.compile(".*(register|create).*(supplier).*", Pattern.CASE_INSENSITIVE), "REGISTER_SUPPLIER");
        INTENT_PATTERNS.put(Pattern.compile(".*(login|sign in|log in).*", Pattern.CASE_INSENSITIVE), "LOGIN");
        INTENT_PATTERNS.put(Pattern.compile(".*(logout|sign out|log out).*", Pattern.CASE_INSENSITIVE), "LOGOUT");
        
        // Product Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(add|create|new).*(product|item).*", Pattern.CASE_INSENSITIVE), "ADD_PRODUCT");
        INTENT_PATTERNS.put(Pattern.compile(".*(view|show|list|get|my).*(products|items|catalog).*", Pattern.CASE_INSENSITIVE), "VIEW_PRODUCTS");
        INTENT_PATTERNS.put(Pattern.compile(".*(update|edit|modify).*(product|item).*", Pattern.CASE_INSENSITIVE), "UPDATE_PRODUCT");
        INTENT_PATTERNS.put(Pattern.compile(".*(delete|remove).*(product|item).*", Pattern.CASE_INSENSITIVE), "DELETE_PRODUCT");
        INTENT_PATTERNS.put(Pattern.compile(".*(low stock|stock alert|reorder|insufficient).*", Pattern.CASE_INSENSITIVE), "LOW_STOCK");
        
        // Inventory Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(check|view|see).*(stock|inventory|quantity).*", Pattern.CASE_INSENSITIVE), "CHECK_STOCK");
        INTENT_PATTERNS.put(Pattern.compile(".*(adjust|update|change).*(stock|quantity).*", Pattern.CASE_INSENSITIVE), "ADJUST_STOCK");
        INTENT_PATTERNS.put(Pattern.compile(".*(stock movement|movement history|stock history).*", Pattern.CASE_INSENSITIVE), "STOCK_MOVEMENTS");
        
        // Purchase Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(create|raise|make).*(purchase|order|po).*", Pattern.CASE_INSENSITIVE), "CREATE_PURCHASE");
        INTENT_PATTERNS.put(Pattern.compile(".*(view|show).*(purchase|orders|po).*", Pattern.CASE_INSENSITIVE), "VIEW_PURCHASES");
        INTENT_PATTERNS.put(Pattern.compile(".*(approve|accept).*(purchase|order).*", Pattern.CASE_INSENSITIVE), "APPROVE_PURCHASE");
        
        // Sales Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(create|new|make).*(sale|invoice|bill).*", Pattern.CASE_INSENSITIVE), "CREATE_SALE");
        INTENT_PATTERNS.put(Pattern.compile(".*(view|show).*(sales|orders|invoices).*", Pattern.CASE_INSENSITIVE), "VIEW_SALES");
        INTENT_PATTERNS.put(Pattern.compile(".*(invoice|bill|receipt).*", Pattern.CASE_INSENSITIVE), "VIEW_INVOICE");
        
        // Customer Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(add|create|new).*(customer|client|buyer).*", Pattern.CASE_INSENSITIVE), "ADD_CUSTOMER");
        INTENT_PATTERNS.put(Pattern.compile(".*(view|show).*(customers|clients).*", Pattern.CASE_INSENSITIVE), "VIEW_CUSTOMERS");
        
        // Report Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(report|analytics|summary|statistics).*", Pattern.CASE_INSENSITIVE), "VIEW_REPORT");
        INTENT_PATTERNS.put(Pattern.compile(".*(dashboard|home|main).*", Pattern.CASE_INSENSITIVE), "DASHBOARD");
        
        // Supplier Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(view|show).*(suppliers|vendors).*", Pattern.CASE_INSENSITIVE), "VIEW_SUPPLIERS");
        INTENT_PATTERNS.put(Pattern.compile(".*(connect|link).*(supplier|vendor).*", Pattern.CASE_INSENSITIVE), "CONNECT_SUPPLIER");
        
        // Thanks Intents
        INTENT_PATTERNS.put(Pattern.compile(".*(thank|thanks|appreciate|thank you).*", Pattern.CASE_INSENSITIVE), "THANKS");
        
        // Help Intent
        INTENT_PATTERNS.put(Pattern.compile(".*(help|support|what can you do|capabilities).*", Pattern.CASE_INSENSITIVE), "HELP");
    }
    
    public String classifyIntent(String message) {
        String lowerMessage = message.toLowerCase().trim();
        
        for (Map.Entry<Pattern, String> entry : INTENT_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(lowerMessage).matches()) {
                return entry.getValue();
            }
        }
        
        return "UNKNOWN";
    }
}