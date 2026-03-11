package com.example.demo.service;

import com.example.demo.entity.Connection;
import com.example.demo.entity.User;

import java.util.List;

public interface ConnectionService {

    void sendRequest(String currentUsername, String targetUsername);

    void acceptRequest(Long connectionId, String currentUsername);

    void rejectRequest(Long connectionId, String currentUsername);

    String getConnectionStatus(String currentUsername, String targetUsername);

    long getConnectionCount(String username);

    List<Connection> getPendingRequests(String username);
    
    Long getPendingRequestId(String currentUsername, String targetUsername);
    
    List<User> getAcceptedConnections(String username);
    
    void removeConnection(String currentUsername, String targetUsername);
    
    void cancelRequest(String currentUsername, String targetUsername);
    
    List<Connection> getSentRequests(String username);
    
    
}