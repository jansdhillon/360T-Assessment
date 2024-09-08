package com.example.player;

import java.io.IOException;
import java.util.concurrent.*;

public class MainPlayer1 {

    public static void main(String[] args) throws IOException {
        int serverPort;
        int clientPort;


        if (args.length < 2) {
            serverPort = 6667;
            clientPort = 6666;
        } else {
            serverPort = Integer.parseInt(args[0]);
            clientPort = Integer.parseInt(args[1]);
        }

        int messageLimit = 10;
        String name = "Initiator";


        try {
            new Player(name, serverPort, clientPort, messageLimit).start();
        } catch (IOException | InterruptedException  e) {
            e.printStackTrace();
        } finally {
            System.out.printf("%s finished.%n", name);
            System.exit(0);
        }
    }
}
