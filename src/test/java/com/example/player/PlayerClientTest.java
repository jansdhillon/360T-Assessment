package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.*;

class PlayerClientTest {

    private PlayerClient playerClient;
    private PlayerServer playerServer;
    private int serverPort;

    @BeforeEach
    void setUp() throws IOException {
        CyclicBarrier barrier = new CyclicBarrier(2);
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }
        playerClient = new PlayerClient();
        playerServer = new PlayerServer("Player1", 10, barrier);
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                playerServer.start(serverPort);
            } catch (IOException | BrokenBarrierException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
    void testSendMessageWithNoServerResponse() throws IOException {
        startServer();
        assertDoesNotThrow(() -> {
            playerClient.startConnection("127.0.0.1", serverPort);
            String response = playerClient.sendMessage("Test Message");
            assertEquals("No response from server.", response);
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