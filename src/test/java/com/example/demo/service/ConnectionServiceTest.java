package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repo.ConnectionRepository;
import com.example.demo.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ConnectionServiceTest {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @MockBean
    private NotificationService notificationService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {

        user1 = new User();
        user1.setUsername("conn_user1");
        user1.setEmail("c1@test.com");
        user1.setPassword("123");
        user1.setRole(Role.PERSONAL);
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("conn_user2");
        user2.setEmail("c2@test.com");
        user2.setPassword("123");
        user2.setRole(Role.PERSONAL);
        userRepository.save(user2);
    }

    // ✅ POSITIVE — SEND REQUEST
    @Test
    void sendRequest_shouldCreatePendingConnection() {

        connectionService.sendRequest("conn_user1", "conn_user2");

        assertEquals("PENDING",
                connectionService.getConnectionStatus("conn_user1", "conn_user2"));
    }

    // ✅ POSITIVE — ACCEPT REQUEST
    @Test
    void acceptRequest_shouldChangeStatusToAccepted() {

        connectionService.sendRequest("conn_user1", "conn_user2");

        Connection connection =
                connectionRepository.findByRequesterAndReceiver(user1, user2)
                        .orElseThrow();

        connectionService.acceptRequest(connection.getId(), "conn_user2");

        assertEquals("ACCEPTED",
                connectionService.getConnectionStatus("conn_user1", "conn_user2"));
    }

    // ❌ NEGATIVE — SELF CONNECTION
    @Test
    void sendRequest_shouldNotAllowSelfConnection() {

        connectionService.sendRequest("conn_user1", "conn_user1");

        assertEquals("NONE",
                connectionService.getConnectionStatus("conn_user1", "conn_user1"));
    }

    // ❌ NEGATIVE — UNAUTHORIZED ACCEPT
    @Test
    void acceptRequest_shouldThrowExceptionIfNotReceiver() {

        connectionService.sendRequest("conn_user1", "conn_user2");

        Connection connection =
                connectionRepository.findByRequesterAndReceiver(user1, user2)
                        .orElseThrow();

        assertThrows(RuntimeException.class, () ->
                connectionService.acceptRequest(connection.getId(), "conn_user1"));
    }

    // ❌ NEGATIVE — CANCEL WHEN NOT PENDING
    @Test
    void cancelRequest_shouldDoNothingIfNoRequest() {

        connectionService.cancelRequest("conn_user1", "conn_user2");

        assertEquals("NONE",
                connectionService.getConnectionStatus("conn_user1", "conn_user2"));
    }
}