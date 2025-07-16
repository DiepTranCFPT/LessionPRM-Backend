package com.lessionprm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitingConfig {
    
    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Bean
    public RateLimitService rateLimitService() {
        // Clean up old entries every minute
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            requestCounters.entrySet().removeIf(entry -> 
                currentTime - entry.getValue().getLastRequestTime() > TimeUnit.MINUTES.toMillis(1));
        }, 1, 1, TimeUnit.MINUTES);
        
        return new RateLimitService(requestCounters);
    }
    
    public static class RateLimitService {
        private final ConcurrentHashMap<String, RequestCounter> requestCounters;
        private static final int MAX_REQUESTS_PER_MINUTE = 60;
        
        public RateLimitService(ConcurrentHashMap<String, RequestCounter> requestCounters) {
            this.requestCounters = requestCounters;
        }
        
        public boolean isAllowed(String clientId) {
            long currentTime = System.currentTimeMillis();
            RequestCounter counter = requestCounters.computeIfAbsent(clientId, 
                k -> new RequestCounter(currentTime));
            
            synchronized (counter) {
                // Reset counter if it's been more than a minute
                if (currentTime - counter.getFirstRequestTime() >= TimeUnit.MINUTES.toMillis(1)) {
                    counter.reset(currentTime);
                }
                
                if (counter.getRequestCount() >= MAX_REQUESTS_PER_MINUTE) {
                    return false;
                }
                
                counter.increment(currentTime);
                return true;
            }
        }
        
        public int getRemainingRequests(String clientId) {
            RequestCounter counter = requestCounters.get(clientId);
            if (counter == null) {
                return MAX_REQUESTS_PER_MINUTE;
            }
            
            synchronized (counter) {
                return Math.max(0, MAX_REQUESTS_PER_MINUTE - counter.getRequestCount());
            }
        }
    }
    
    public static class RequestCounter {
        private int requestCount;
        private long firstRequestTime;
        private long lastRequestTime;
        
        public RequestCounter(long currentTime) {
            this.requestCount = 0;
            this.firstRequestTime = currentTime;
            this.lastRequestTime = currentTime;
        }
        
        public void increment(long currentTime) {
            this.requestCount++;
            this.lastRequestTime = currentTime;
        }
        
        public void reset(long currentTime) {
            this.requestCount = 0;
            this.firstRequestTime = currentTime;
            this.lastRequestTime = currentTime;
        }
        
        public int getRequestCount() {
            return requestCount;
        }
        
        public long getFirstRequestTime() {
            return firstRequestTime;
        }
        
        public long getLastRequestTime() {
            return lastRequestTime;
        }
    }
}