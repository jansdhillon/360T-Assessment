#!/bin/bash

# Start Player 1 in the background
java -cp target/360TAssessment-1.0-SNAPSHOT.jar com.example.player.MainPlayer1 &

# Start Player 2 in the background
java -cp target/360TAssessment-1.0-SNAPSHOT.jar com.example.player.MainPlayer2