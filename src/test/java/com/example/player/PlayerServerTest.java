package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServerTest {

    private PlayerServer playerServer;
    private int serverPort;
    private CountDownLatch serverReadyLatch;

    @BeforeEach
    void setUp() throws IOException {
        serverReadyLatch = new CountDownLatch(1);
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }
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
    void testServerInitialization() {
        assertNotNull(playerServer);
        assertEquals(0, playerServer.getMessagesReceived());
    }

    @Test
    void testServerStartAndStop() {
        startServer();

        assertDoesNotThrow(() -> {
            Socket clientSocket = new Socket("127.0.0.1", serverPort);
            clientSocket.getOutputStream().write("Test Message\n".getBytes());
            clientSocket.getOutputStream().flush();

            playerServer.stop();
            assertTrue(clientSocket.isConnected());
            clientSocket.close();
        });
    }

    @Test
    void testIncrementMessagesReceived() {
        assertEquals(0, playerServer.getMessagesReceived());
        playerServer.incrementMessagesReceived();
        assertEquals(1, playerServer.getMessagesReceived());
    }
}