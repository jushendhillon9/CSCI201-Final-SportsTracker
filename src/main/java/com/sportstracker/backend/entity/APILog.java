package com.sportstracker.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "API_Log")
public class APILog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;
    
    @Column(length = 50)
    private String source;
    
    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;
    
    @Column(length = 20)
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    // Constructors
    public APILog() {}
    
    public APILog(String source, String status, String message) {
        this.source = source;
        this.status = status;
        this.message = message;
        this.fetchedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getLogId() {
        return logId;
    }
    
    public void setLogId(Integer logId) {
        this.logId = logId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }
    
    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}