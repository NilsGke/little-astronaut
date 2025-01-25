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
}
