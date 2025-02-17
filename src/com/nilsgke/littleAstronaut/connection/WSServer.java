package com.nilsgke.littleAstronaut.connection;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.Toasts.Toaster;
import name.panitz.game2d.Vertex;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;


public class WSServer {
  public enum Status {
    STOPPED, STARTING, RUNNING
  }

  private static final int PORT = 8080;
  private static final Set<Socket> clients = new CopyOnWriteArraySet<>();
  public static final Map<Byte, Player> players = new ConcurrentHashMap<>();
  private Status status = Status.STOPPED;
  private ServerSocket serverSocket;
  private Thread serverThread;
  private ScheduledExecutorService broadcastScheduler;
  private String URI;
  private final Player player;

  private static byte idCounter = 0; // reflects current id, in use; user, hosting the server is id 0

  public WSServer(Player player) {
    this.player = player;
  }

  public static void main(String[] args) {
    var player = new Player((byte) 0, new Vertex(0, 0), new Vertex(0, 0));
    var server = new WSServer(player);
    server.start();
  }

  public Status getStatus() {
    return status;
  }

  public String getURI() {
    return URI;
  }

  public void start() {
    if (status == Status.RUNNING) {
      System.out.println("Server is already running.");
      return;
    }
    status = Status.STARTING;
    players.put((byte) 0, player);
    System.out.println("starting server...");
    serverThread = new Thread(() -> {
      System.out.println("got thread");
      try {
        serverSocket = new ServerSocket(PORT);
        System.out.println("created socket");
        status = Status.RUNNING;

        Toaster.info("Server gestartet", 4000);

        updateUriInBackground();
        System.out.println("starting broadcast");
        // Broadcast player positions every 20 ms (~60 fps)
        broadcastScheduler = Executors.newSingleThreadScheduledExecutor();
        broadcastScheduler.scheduleAtFixedRate(() -> {
          if (!players.isEmpty())
            broadcastBytes(WSData.PlayerList.encodeWithIdentifierFromPlayerMap(players));
        }, 0, 20, TimeUnit.MILLISECONDS);
        System.out.println("scheduled broadcast (running)");

        // accept users connecting
        while (status == Status.RUNNING) {
          Socket clientSocket = serverSocket.accept();
          clients.add(clientSocket);
          new Thread(new ClientHandler(clientSocket)).start();
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        stop(); // Ensure resources are cleaned up
      }
    });
    System.out.println("start thread");
    serverThread.start();
  }

  public void stop() {
    if (status != Status.RUNNING) {
      System.out.println("Server is not running.");
      return;
    }

    System.out.println("stopping server");
    status = Status.STOPPED;
    try {
      if (broadcastScheduler != null) {
        broadcastScheduler.shutdown();
        try {
          if (!broadcastScheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
            broadcastScheduler.shutdownNow();
          }
          System.out.println("stopped broadcast");
        } catch (InterruptedException e) {
          broadcastScheduler.shutdownNow();
        }
      }

      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        System.out.println("socket closed");
      }
      for (Socket client : clients) {
        client.close();

      }
      System.out.println("clients closed");
      clients.clear();
      serverThread.interrupt(); // Wait for the server thread to finish

      System.out.println("WebSocket server stopped.");

      Toaster.error("Server gestopt", 4000);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateUriInBackground() {
    System.out.println("getting server uri");
    new Thread(() -> {
      System.out.println("in thread");
      URI = getServerIpAddress() + ":" + PORT;
      System.out.println("done in thread");
    }).start();
    System.out.println("done with method");

  }

  private static String getServerIpAddress() {
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      return localHost.getHostAddress(); // Get the server's IP address
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return "localhost"; // Fallback to localhost if unable to determine IP
    }
  }

  private void broadcastBytes(byte[] message) {
    for (Socket client : clients) sendMessageToClient(client, message);
  }

  private static void sendMessageToClient(Socket client, byte[] message) {
    try {
      OutputStream out = client.getOutputStream();
      sendMessageToClient(out, message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // GPT-4o
  private static void sendMessageToClient(OutputStream out, byte[] message) {
    try {
      out.write(0x82); // FIN + Binary opcode
      if (message.length <= 125) {
        out.write(message.length);
      } else if (message.length <= 65535) {
        out.write(126);
        out.write((message.length >> 8) & 0xFF);
        out.write(message.length & 0xFF);
      }
      out.write(message);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // base generated by GPT 4o (run, generateAcceptKey and readMessage)
  private class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
      this.clientSocket = socket;
    }

    @Override
    public void run() {
      try (InputStream in = clientSocket.getInputStream(); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

        // Get the client's IP address
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        System.out.println("Client connected: " + clientIp);

        Toaster.info("Spieler beigetreten", 4000);

        // Perform WebSocket handshake
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
          if (line.startsWith("Sec-WebSocket-Key")) {
            String key = line.split(": ")[1];
            String responseKey = generateAcceptKey(key);
            out.println("HTTP/1.1 101 Switching Protocols");
            out.println("Upgrade: websocket");
            out.println("Connection: Upgrade");
            out.println("Sec-WebSocket-Accept: " + responseKey);
            out.println();
            out.flush(); // Ensure headers are sent immediately

            // Wait a brief moment for handshake to complete
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }

            // Assign and send ID to client
            idCounter++;
            sendMessageToClient(clientSocket.getOutputStream(), WSData.ID.encodeWithIdentifier(idCounter));
            break;
          }
        }


        // Handle messages from the client
        byte[] message;
        while ((message = readMessage(in)) != null) handleMessage(message);

      } catch (IOException | NoSuchAlgorithmException e) {
        e.printStackTrace();
      } finally {
        System.out.println("Client disconnected: " + clientSocket.getLocalAddress());
        Toaster.error("Spieler verlassen", 4000);
        try {
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        clients.remove(clientSocket);
        players.clear(); // clear player set so all players are removed and will be back on next client message
        players.put((byte) 0, player);
      }
    }

    private byte[] readMessage(InputStream in) throws IOException {
      int firstByte = in.read();
      if (firstByte == -1) return null;

      boolean fin = (firstByte & 0x80) != 0; // FIN flag
      int opcode = firstByte & 0x0F; // Opcode

      if (opcode == 8) {
        System.out.println("Client requested to close connection.");
        return null;
      }

      int secondByte = in.read();
      if (secondByte == -1) return null;

      boolean masked = (secondByte & 0x80) != 0;
      int payloadLength = secondByte & 0x7F;

      if (payloadLength == 126) {
        payloadLength = (in.read() << 8) | in.read();
      } else if (payloadLength == 127) {
        for (int i = 0; i < 6; i++) in.read(); // Skip the first 6 bytes (only last 2 are used)
        payloadLength = (in.read() << 8) | in.read();
      }

      byte[] maskingKey = new byte[4];
      if (masked) {
        in.read(maskingKey, 0, 4);
      }

      byte[] payload = new byte[payloadLength];
      int bytesRead = 0;
      while (bytesRead < payloadLength) {
        int read = in.read(payload, bytesRead, payloadLength - bytesRead);
        if (read == -1) return null;
        bytesRead += read;
      }

      if (masked) {
        for (int i = 0; i < payload.length; i++) {
          payload[i] ^= maskingKey[i % 4]; // Unmask payload
        }
      }

      return payload;  // This now contains just the actual message data, not the WebSocket frame
    }

    private void handleMessage(byte[] message) {
      byte identifier = message[0];
      byte[] data = Arrays.copyOfRange(message, 1, message.length);

      switch (identifier) {
        case WSData.IDRequest.IDENTIFIER -> {
          System.out.println("got id request");
          idCounter++;
          try {
            var messageOut = clientSocket.getOutputStream();
            sendMessageToClient(messageOut, WSData.ID.encodeWithIdentifier(idCounter));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          System.out.println("id request handled");
        }
        case WSData.Player.IDENTIFIER -> {
          var remotePlayer = WSData.Player.decode(data);
          System.out.println(remotePlayer);


          Player existing = players.get(remotePlayer.id());

          if (existing != null) { // update player, if already in map
            existing.level = remotePlayer.level();
            existing.pos().moveTo(new Vertex(remotePlayer.x(), remotePlayer.y()));
            existing.velocity().moveTo(new Vertex(remotePlayer.xVel(), remotePlayer.yVel()));
          } else { // create new player if not in map
            Player newPlayer = new Player(
                    remotePlayer.id(),
                    new Vertex(remotePlayer.x(), remotePlayer.y()),
                    new Vertex(0, 0)
            );

            players.put(remotePlayer.id(), newPlayer);
          }


        }
        default -> System.err.println("unsupported message received on server!\nIdentifier: " + identifier);

      }

    }

    private String generateAcceptKey(String key) throws NoSuchAlgorithmException {
      String acceptKey = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      return Base64.getEncoder().encodeToString(java.security.MessageDigest.getInstance("SHA-1").digest(acceptKey.getBytes()));
    }
  }
}
