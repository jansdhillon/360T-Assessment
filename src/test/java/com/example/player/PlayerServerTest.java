package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServerTest {

    private PlayerServer playerServer;
    private CyclicBarrier barrier;
    private int serverPort;

    @BeforeEach
    void setUp() throws IOException {
        barrier = new CyclicBarrier(2);
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }
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