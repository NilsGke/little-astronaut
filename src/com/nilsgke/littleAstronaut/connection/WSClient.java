package com.nilsgke.littleAstronaut.connection;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;

public class WSClient {
  public enum Status {
    IDLE, CONNECTING, CONNECTED, ERROR
  }

  private Status status = Status.IDLE;
  public byte id;

  public final Set<WSData.Player> players = Collections.synchronizedSet(new HashSet<>());

  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  public static void main(String[] args) {
    WSClient client = new WSClient();
    client.connect("localhost", "8080");
    client.listenForMessages();
  }

  // GPT 4o
  public void connect(String host, String port) {
    this.status = Status.CONNECTING;

    System.out.println("connecting to server: " + host + ":" + port);
    try {
      // Resolve the server's hostname
      InetAddress address = InetAddress.getByName(host);
      socket = new Socket(address, Integer.parseInt(port));
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

      // request ID
      sendBytes(new byte[]{WSData.IDRequest.IDENTIFIER});

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

  // GPT 4o
  public void sendBytes(byte[] data) throws IOException {
    OutputStream outputStream = socket.getOutputStream();

    // Write the first byte: FIN (final frame) + Opcode (0x2 for binary)
    outputStream.write(0x82); // 0x80 (FIN) | 0x02 (binary)

    // Write the payload length
    if (data.length <= 125) {
      outputStream.write(data.length); // Single-byte payload length
    } else if (data.length <= 65535) {
      outputStream.write(126); // 126 indicates a 16-bit payload length follows
      outputStream.write((data.length >> 8) & 0xFF); // Most significant byte
      outputStream.write(data.length & 0xFF);        // Least significant byte
    } else {
      outputStream.write(127); // 127 indicates a 64-bit payload length follows
      for (int i = 7; i >= 0; i--) {
        outputStream.write((int) (data.length >> (i * 8)) & 0xFF); // Write each byte of the length
      }
    }

    // Write the payload (binary data)
    outputStream.write(data);
    outputStream.flush();
  }

  // GPT 4o
  public void listenForMessages() {
    new Thread(() -> {
      try {
        while (true) {
          byte[] message = readMessage();
          if (message == null) {
            System.out.println("Server closed the connection.");
            disconnect();
            break;
          }

          handleMessage(message);
        }
      } catch (IOException e) {
        System.out.println("Error while listening for messages.");
        e.printStackTrace();
        disconnect();
      }
    }).start();
  }

  private void handleMessage(byte[] message) {
    byte identifier = message[0];
    byte[] data = Arrays.copyOfRange(message, 1, message.length);
    switch (identifier) {
      case WSData.ID.IDENTIFIER -> {
        this.id = WSData.ID.decode(data).id();
        System.out.println("received id: " + this.id);
      }
      case WSData.PlayerList.IDENTIFIER -> {
        players.clear();
        var playerList = WSData.PlayerList.decode(data);
        Collections.addAll(players, playerList.players());
      }
      default -> System.err.println("unsupported message type");
    }
  }

  // GPT 4o
  private byte[] readMessage() throws IOException {
    InputStream inputStream = socket.getInputStream();

    // Read the WebSocket frame header
    int b1 = inputStream.read();
    int b2 = inputStream.read();
    if (b1 == -1 || b2 == -1) return null; // Connection closed by server

    // Check for FIN bit and opcode (assuming only text messages for now)
    boolean fin = (b1 & 0x80) != 0; // FIN bit
    int opcode = b1 & 0x0F;         // Opcode

    if (opcode == 8) {
      // Close frame received
      System.out.println("Close frame received.");
      return null;
    }

    // Determine the payload length
    int payloadLength = b2 & 0x7F;
    if (payloadLength == 126) {
      // Extended payload (16-bit length)
      payloadLength = (inputStream.read() << 8) | inputStream.read();
    } else if (payloadLength == 127) {
      // Extended payload (64-bit length) - not handled for simplicity
      throw new UnsupportedOperationException("Payloads larger than 65535 are not supported.");
    }

    // Read the payload
    byte[] payload = new byte[payloadLength];
    int bytesRead = inputStream.read(payload, 0, payloadLength);
    if (bytesRead != payloadLength) {
      throw new IOException("Incomplete WebSocket frame received.");
    }

    // Return payload
    return payload;
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
