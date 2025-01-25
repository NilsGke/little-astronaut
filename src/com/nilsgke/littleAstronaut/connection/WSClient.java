package com.nilsgke.littleAstronaut.connection;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WSClient {
  public enum Status {
    IDLE, CONNECTING, CONNECTED, ERROR
  }

  private String host;
  private int port;
  private Status status = Status.IDLE;

  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  public static void main(String[] args) {
    WSClient client = new WSClient();
    client.connect("localhost", 8080);
    client.sendPosition("player1", Math.random() * 800, Math.random() * 600);
    client.listenForMessages();
  }

  public void connect(String host, int port) {
    this.status = Status.CONNECTING;
    System.out.println("connecting to server: " + host + ":" + port);
    try {
      // Resolve the server's hostname
      InetAddress address = InetAddress.getByName(host);
      socket = new Socket(address, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Perform WebSocket handshake
      String key = Base64.getEncoder().encodeToString("randomKey".getBytes());
      out.println("GET / HTTP/1.1");
      out.println("Host: " + host);
      out.println("Upgrade: websocket");
      out.println("Connection: Upgrade");
      out.println("Sec-WebSocket-Key: " + key);
      out.println("Sec-WebSocket-Version: 13");
      out.println();

      // Read the server's response
      String line;
      while (!(line = in.readLine()).isEmpty()) {
        System.out.println("Server: " + line);
      }

      // Validate the handshake
      String acceptKey = generateAcceptKey(key);
      System.out.println("Expected Accept Key: " + acceptKey);

      this.status = Status.CONNECTED;

    } catch (IOException e) {
      e.printStackTrace();
      this.status = Status.ERROR;
    }
  }

  public void disconnect() {
    try {
      if (in != null) in.close();
      if (out != null) out.close();
      if (socket != null) socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      this.status = Status.IDLE;
    }
  }

  public Status getStatus() {
    return this.status;
  }


  public void setStatusToError() {
    this.status = Status.ERROR;
  }

  public void sendPosition(String playerId, double x, double y) {
    String message = playerId + "," + x + "," + y;
    out.println(message);
  }

  public void listenForMessages() {
    new Thread(() -> {
      try {
        String message;
        while ((message = readMessage()) != null) {
          System.out.println("Received: " + message);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private String readMessage() throws IOException {
    int b1 = in.read();
    int b2 = in.read();
    if (b1 == -1 || b2 == -1) return null;

    int payloadLength = b2 & 127;
    if (payloadLength == 126) {
      payloadLength = in.read() << 8 | in.read();
    } else if (payloadLength == 127) {
      // Handle very large payloads if necessary
      return null;
    }

    char[] payload = new char[payloadLength];
    in.read(payload, 0, payloadLength);
    return new String(payload);
  }

  private String generateAcceptKey(String key) {
    String acceptKey = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] hash = digest.digest(acceptKey.getBytes());
      return Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static class WSServer {
    public enum Status {
      STOPPED, STARTING, RUNNING
    }

    private static final int PORT = 8080;
    private static final Set<Socket> clients = Collections.synchronizedSet(new HashSet<>());
    private Status status = Status.STOPPED;
    private ServerSocket serverSocket;
    private Thread serverThread;

    public static void main(String[] args) {
      var server = new WSServer();
      server.start();
    }

    public Status getStatus() {
      return status;
    }

    public void start() {
      if (status == Status.RUNNING) {
        System.out.println("Server is already running.");
        return;
      }

      status = Status.STARTING;
      serverThread = new Thread(() -> {
        try {
          serverSocket = new ServerSocket(PORT);
          status = Status.RUNNING;
          String serverIp = getServerIpAddress();
          System.out.println("WebSocket server started on " + serverIp + ":" + PORT);
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
      serverThread.start();
    }

    public void stop() {
      if (status != Status.RUNNING) {
        System.out.println("Server is not running.");
        return;
      }

      status = Status.STOPPED;
      try {
        if (serverSocket != null && !serverSocket.isClosed()) {
          serverSocket.close();
        }
        for (Socket client : clients) {
          client.close();
        }
        clients.clear();
        serverThread.join(); // Wait for the server thread to finish
        System.out.println("WebSocket server stopped.");
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }

    public String getWebSocketURI() {
      return getServerIpAddress() + ":" + PORT; // Use server's IP address
    }

    private String getServerIpAddress() {
      try {
        InetAddress localHost = InetAddress.getLocalHost();
        return localHost.getHostAddress(); // Get the server's IP address
      } catch (UnknownHostException e) {
        e.printStackTrace();
        return "localhost"; // Fallback to localhost if unable to determine IP
      }
    }

    private static class ClientHandler implements Runnable {
      private final Socket clientSocket;

      public ClientHandler(Socket socket) {
        this.clientSocket = socket;
      }

      @Override
      public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

          // Get the client's IP address
          String clientIp = clientSocket.getInetAddress().getHostAddress();
          System.out.println("Client connected: " + clientIp);

          // Perform WebSocket handshake
          String line;
          while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Sec-WebSocket-Key")) {
              String key = line.split(": ")[1];
              String responseKey = generateAcceptKey(key);
              out.println("HTTP/1.1 101 Switching Protocols");
              out.println("Upgrade: websocket");
              out.println("Connection: Upgrade");
              out.println("Sec-WebSocket-Accept: " + responseKey);
              out.println();
              break;
            }
          }

          // Handle messages from the client
          String message;
          while ((message = readMessage(in)) != null) {
            System.out.println("Received from " + clientIp + ": " + message);
            broadcast(message);
          }
        } catch (IOException | NoSuchAlgorithmException e) {
          e.printStackTrace();
        } finally {
          try {
            clientSocket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          clients.remove(clientSocket);
        }
      }

      private String readMessage(BufferedReader in) throws IOException {
        // Read the WebSocket frame
        int b1 = in.read();
        int b2 = in.read();
        if (b1 == -1 || b2 == -1) return null;

        int payloadLength = b2 & 127;
        if (payloadLength == 126) {
          payloadLength = in.read() << 8 | in.read();
        } else if (payloadLength == 127) {
          // Handle very large payloads if necessary
          return null;
        }

        char[] payload = new char[payloadLength];
        in.read(payload, 0, payloadLength);
        return new String(payload);
      }

      private void broadcast(String message) {
        for (Socket client : clients) {
          if (client != clientSocket) {
            try {
              PrintWriter out = new PrintWriter(client.getOutputStream(), true);
              out.println(message);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }

      private String generateAcceptKey(String key) throws NoSuchAlgorithmException {
        String acceptKey = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(acceptKey.getBytes())
        );
      }
    }
  }
}
