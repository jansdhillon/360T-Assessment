package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The PlayerClientTest class contains unit tests to validate the behavior of the PlayerClient class.
 * Responsibilities:
 * - Tests client-server communication and message sending functionality.
 * - Verifies that the client increments the message count correctly.
 * - Ensures the client can establish and stop connections properly.
 */
class PlayerClientTest {

    private PlayerClient playerClient;
    private PlayerServer playerServer;
    private int serverPort;

    @BeforeEach
    void setUp() throws IOException {
        CountDownLatch serverReadyLatch = new CountDownLatch(1);
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }
        playerClient = new PlayerClient();
        playerServer = new PlayerServer("Initiator", 10, serverReadyLatch);
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                playerServer.start(serverPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

    }

    @Test
    void testClientInitialization() {
        assertNotNull(playerClient);
        assertEquals(0, playerClient.getMessagesSent());
    }

    @Test
    void testClientStartAndStopConnection() {
        startServer();
        assertDoesNotThrow(() -> {
            playerClient.startConnection("127.0.0.1", serverPort);
            playerClient.stopConnection();
        });
    }

    @Test
    void testSendMessageWithServerResponse() {
        startServer();
        assertDoesNotThrow(() -> {
            playerClient.startConnection("127.0.0.1", serverPort);
            String response = playerClient.sendMessage("Test Message");
            assertNotNull(response);
            playerClient.stopConnection();
        });
    }

    @Test
    void testIncrementMessagesSent() {
        assertEquals(0, playerClient.getMessagesSent());
        playerClient.incrementMessagesSent();
        assertEquals(1, playerClient.getMessagesSent());
    }
}