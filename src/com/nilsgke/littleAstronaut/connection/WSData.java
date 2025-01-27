package com.nilsgke.littleAstronaut.connection;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

public class WSData {

  public record ID(byte id) {
    public static final byte IDENTIFIER = (byte) 0b00000010;

    // 1 byte for id

    public static ID decode(byte[] bytes) {
      return new ID(bytes[0]);
    }

    public static byte[] encodeWithIdentifier(byte id) {
      return new byte[]{IDENTIFIER, id};
    }
  }

  public record Player(byte id, byte level, double x, double y) {
    public static final byte IDENTIFIER = (byte) 0b00000001;
    public static final int BYTES = 2 + 8 * 2;
    // 1 byte for id
    // 1 byte for level
    // 8 bytes for x (double)
    // 8 bytes for y (double)

    public byte[] encode() {
      return ByteBuffer.allocate(2 + 8 * 2).put(0, id).put(1, level).putDouble(2, x).putDouble(2 + 8, y).array();
    }

    public static byte[] encodeWithIdentifier(byte id, byte level, double x, double y) {
      return ByteBuffer.allocate(3 + 8 * 2).put(0, IDENTIFIER).put(1, id).put(2, level).putDouble(3, x).putDouble(3 + 8, y).array();
    }

    public static Player decode(byte[] bytes) {
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      byte id = buffer.get(0);
      byte level = buffer.get(1);
      double x = buffer.getDouble(2);
      double y = buffer.getDouble(2 + 8);
      return new Player(id, level, x, y);
    }
  }

  public record PlayerList(Player[] players) {
    public static final byte IDENTIFIER = (byte) 0b00000011;

    // n bytes per player concatenated

    public static PlayerList decode(byte[] bytes) {
      int playerCount = bytes.length / Player.BYTES;
      Player[] list = new Player[playerCount];

      for (int i = 0; i < playerCount; i++)
        list[i] = Player.decode(Arrays.copyOfRange(bytes, i * Player.BYTES, i * Player.BYTES + Player.BYTES));

      return new PlayerList(list);
    }

    public byte[] encode() {
      var buffer = ByteBuffer.allocate(players.length * Player.BYTES);

      for (int i = 0; i < players.length; i++)
        buffer.put(i * Player.BYTES, players[i].encode());

      return buffer.array();
    }

    public byte[] encodeWithIdentifier() {
      var buffer = ByteBuffer.allocate(1 + players.length * Player.BYTES);

      buffer.put(0, IDENTIFIER);

      for (int i = 0; i < players.length; i++)
        buffer.put(1 + i * Player.BYTES, players[i].encode());

      return buffer.array();
    }

    public static byte[] encodeWithIdentifierFromPlayerMap(Map<Byte, WSData.Player> playerMap) {
      var buffer = ByteBuffer.allocate(1 + playerMap.size() * Player.BYTES);

      buffer.put(0, IDENTIFIER);

      int counter = 0;
      for (var player : playerMap.entrySet()) {
        buffer.put(1 + counter * Player.BYTES, player.getValue().encode());
        counter++;
      }

      return buffer.array();
    }


  }

  public class IDRequest {
    public static final byte IDENTIFIER = (byte) 0b00000100;
  }

}