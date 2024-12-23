package com.nilsgke.jumpKingMinigame.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import name.panitz.game2d.Vertex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameMap {
  static private final String FILE_PATH = "map.json";

  public Platform[] platforms;

  GameMap(Platform[] platforms) {
    this.platforms = platforms;
  }

  static public GameMap fromJson() throws FileNotFoundException {
    // Parse JSON file
    Map<String, Object> mapData = parseJSON(FILE_PATH);

    // platforms
    @SuppressWarnings("unchecked") List<Map<String, Object>> jsonPlatforms = (List<Map<String, Object>>) mapData.get("platforms");
    ArrayList<Platform> platforms = new java.util.ArrayList<>();

    for (Map<String, Object> platform : jsonPlatforms) {
      int x = (int) platform.get("x");
      int y = (int) platform.get("y");
      int width = (int) platform.get("width");
      int height = (int) platform.get("height");
      platforms.add(new Platform(new Vertex(x, y), new Vertex(0, 0), (double) height, (double) width));
    }

    return new GameMap(platforms.toArray(Platform[]::new));
  }

  public static Map<String, Object> parseJSON(String filePath) {

    ObjectMapper objectMapper = new ObjectMapper();


    try {
      InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
      if (inputStream == null) {
        throw new FileNotFoundException("Datei nicht gefunden im Klassenpfad: " + filePath);
      }

      return objectMapper.readValue(inputStream, Map.class);
    } catch (IOException e) {
      System.err.println("Konnte JSON-Datei nicht parsen:");
      e.printStackTrace();
      throw new RuntimeException("JSON-Ladevorgang fehlgeschlagen: " + filePath);
    }

  }
}
