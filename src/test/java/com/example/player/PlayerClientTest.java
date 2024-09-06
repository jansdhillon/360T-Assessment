package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PlayerClientTest {

    private PlayerClient playerClient;
    private PlayerServer playerServer;
    private int serverPort;
    private CountDownLatch serverReadyLatch;

    @BeforeEach
    void setUp() throws IOException {
        serverReadyLatch = new CountDownLatch(1);
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }
        playerClient = new PlayerClient();
        playerServer = new PlayerServer("Player1", 10);
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                playerServer.start(serverPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                serverReadyLatch.countDown();
            }
        });
        serverThread.start();

        try {
            serverReadyLatch.await(500, TimeUnit.MILLISECONDS);
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
    void testSendMessageWithServerResponse() throws IOException {
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