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
        playerServer = new PlayerServer("Initiator", 10);
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
        new Thread(this::startServer).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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