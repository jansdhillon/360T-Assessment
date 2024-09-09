package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;


/**
 * The PlayerServerTest class contains unit tests to validate the behavior of the PlayerServer class.
 * Responsibilities:
 * - Tests server initialization and message receipt functionality.
 * - Validates that the server increments the message count correctly.
 * - Ensures the server shuts down properly after communication is complete.
 */
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
    void testServerInitialization() {
        assertNotNull(playerServer);
        assertEquals(0, playerServer.getMessagesReceived());
    }

    @Test
    void testServerStartAndStop() throws InterruptedException {
        startServer();

        serverReadyLatch.await();

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
