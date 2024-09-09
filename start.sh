#!/bin/bash

printf "\nRunning both players in the same process:\n\n"

mvn clean compile exec:java -Dexec.mainClass="com.example.player.Main"

printf "\nRunning players in separate processes:\n\n"

mvn compile exec:java -Dexec.mainClass="com.example.player.MainPlayer1" &
pid1=$!

mvn compile exec:java -Dexec.mainClass="com.example.player.MainPlayer2"

wait $pid1

printf "\nCompleted successfully! Exiting...\n"
