package com.example.player;

public class MainPlayer2 {
    public static void main(String[] args) {
        int serverPort;
        int clientPort;

        if (args.length < 2) {
            serverPort = 6666;
            clientPort = 6667;
        } else {
            serverPort = Integer.parseInt(args[0]);
            clientPort = Integer.parseInt(args[1]);
        }

        int messageLimit = 10;
        String name = "Player2";


        try {
            new Player(name, serverPort, clientPort, messageLimit).start();
        } catch (Exception e) {
            return;
        } finally {
            System.out.printf("%s finished.%n", name);
            System.exit(0);
        }
    }
}
