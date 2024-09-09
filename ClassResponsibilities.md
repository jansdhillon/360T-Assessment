# Class Responsibilities

## `Main.java`
**Responsibilities:**
- Initializes and runs two players (`Initiator` and `Player2`) in the same process.
- Dynamically allocates ports for both players.
- Runs each player in a separate thread.
- Waits for both players to complete communication before exiting.
- Terminates the program after the required number of messages has been exchanged.

---

## `MainPlayer1.java`
**Responsibilities:**
- Initializes the `Initiator` player in a separate process.
- Uses predefined or command-line-specified ports for communication.
- Manages the message exchange between `Initiator` and `Player2`.
- Terminates the process gracefully after the message limit is reached.

---

## `MainPlayer2.java`
**Responsibilities:**
- Initializes `Player2` in a separate process.
- Uses predefined or command-line-specified ports for communication.
- Manages the message exchange between `Player2` and `Initiator`.
- Terminates the process gracefully after the message limit is reached.

---

## `Player.java`
**Responsibilities:**
- Represents a player in the message exchange game.
- Acts as both a client and a server by initializing `PlayerServer` and `PlayerClient`.
- Synchronizes the client and server startup.
- Sends and receives messages in a communication loop.
- Gracefully stops the server and client after the message exchange is complete.
- Verifies that the correct number of messages has been exchanged before terminating.

---

## `PlayerClient.java`
**Responsibilities:**
- Manages the client-side communication with another player's server.
- Establishes a connection to the server with a retry mechanism if needed.
- Sends messages to the server and receives responses.
- Tracks the number of messages sent using an atomic counter.
- Closes the connection and streams gracefully after communication is complete.

---

## `PlayerServer.java`
**Responsibilities:**
- Manages the server-side communication for a player.
- Listens for incoming connections from the client.
- Receives messages and sends responses with a message counter.
- Tracks the number of messages received using an atomic counter.
- Signals when the server is ready to accept connections (synchronization).
- Closes connections and resources gracefully after communication is complete.

---

## `PlayerTest.java`
**Responsibilities:**
- Contains unit tests to verify that the `Player` class initializes correctly and that players can communicate in separate threads.
- Verifies that the communication process completes without exceptions.

---

## `PlayerServerTest.java`
**Responsibilities:**
- Contains unit tests to validate the behavior of `PlayerServer`, including server initialization, message receipt, and server shutdown.
- Tests the server's ability to increment the message count correctly.

---

## `PlayerClientTest.java`
**Responsibilities:**
- Contains unit tests to validate the behavior of `PlayerClient`, including client-server communication and message sending.
- Verifies that the client increments the message count and handles communication properly.
