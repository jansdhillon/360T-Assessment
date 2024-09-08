package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServerTest {

    private PlayerServer playerServer;
    private int serverPort;

    @BeforeEach
    void setUp() throws IOException {
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
    void testServerStartAndStop() {
        // Start the server in a separate thread
        new Thread(this::startServer).start();

        // Wait for a short time to allow the server to start (you can increase this if needed)
        try {
            Thread.sleep(500); // wait for 500ms (increase if the server takes longer to start)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertDoesNotThrow(() -> {
            Socket clientSocket = new Socket("127.0.0.1", serverPort);

            // Send a message to the server
            clientSocket.getOutputStream().write("Test Message\n".getBytes());
            clientSocket.getOutputStream().flush();

            // Stop the server
            playerServer.stop();

            // Assert that the client is still connected
            assertTrue(clientSocket.isConnected());

            // Close the client socket
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