package com.nilsgke.littleAstronaut.sprites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Text {
  private static final BufferedImage textMap;
  private static final int CHAR_WIDTH = 8;
  private static final int CHAR_HEIGHT = 10;
  private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789.,:?!()+-<>";
  private static final Map<Character, BufferedImage> baseCharacters = new HashMap<>();
  private static final Map<CharColorKey, BufferedImage> coloredCharacterCache = new HashMap<>();
  private static final Map<StringColorKey, BufferedImage> combinedImageCache = new HashMap<>();


  static {
    try {
      textMap = ImageIO.read(new File("assets/text/text-white.png"));
      generateBaseCharacters();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load text-white image", e);
    }
  }

  private static void generateBaseCharacters() {
    int columns = textMap.getWidth() / CHAR_WIDTH;

    for (int i = 0; i < CHARACTERS.length(); i++) {
      char c = CHARACTERS.charAt(i);
      int sx = (i % columns) * CHAR_WIDTH;
      int sy = (i / columns) * CHAR_HEIGHT;
      BufferedImage charImage = textMap.getSubimage(sx, sy, CHAR_WIDTH, CHAR_HEIGHT);
      baseCharacters.put(c, charImage);
    }
  }

  public static void paintTo(Graphics g, String text, int x, int y, int scale) {
    paintTo(g, text, x, y, scale, Color.WHITE);
  }

  // Static method to paint text at a specified position
  public static void paintTo(Graphics g, String text, int x, int y, int scale, Color color) {
    text = text.toLowerCase().replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue");

    // Check if the combined image is already cached
    StringColorKey key = new StringColorKey(text, color);
    BufferedImage combinedImage = combinedImageCache.get(key);
    if (combinedImage == null) {
      combinedImage = createCombinedImage(text, scale, color);
      combinedImageCache.put(key, combinedImage);
    }

    // Draw the combined image
    g.drawImage(combinedImage, x, y, null);
  }

  private static BufferedImage createCombinedImage(String text, int scale, Color color) {
    int totalWidth = text.length() * CHAR_WIDTH * scale;
    int totalHeight = CHAR_HEIGHT * scale;

    BufferedImage combinedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = combinedImage.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int xOffset = 0;

    for (char c : text.toCharArray()) {
      BufferedImage charImage = color.equals(Color.WHITE) ? baseCharacters.get(c) : getColoredCharacter(c, color);

      if (charImage != null) {
        int scaledWidth = CHAR_WIDTH * scale;
        int scaledHeight = CHAR_HEIGHT * scale;
        g2d.drawImage(charImage, xOffset, 0, scaledWidth, scaledHeight, null);
      } else {
        xOffset -= (int) (CHAR_WIDTH * scale * .3); // make space smaller
      }

      xOffset += CHAR_WIDTH * scale; // Move to the next character position (scaled)
    }

    g2d.dispose();
    return combinedImage;
  }

  private static BufferedImage getColoredCharacter(char character, Color color) {
    // check cache first
    BufferedImage cachedImage = coloredCharacterCache.get(new CharColorKey(character, color));
    if(cachedImage != null)
      return cachedImage;


    BufferedImage baseImage = baseCharacters.get(character);
    if (baseImage == null) return null;

    BufferedImage recolored = recolorImage(baseImage, color);
    var key = new CharColorKey(character, color);
    coloredCharacterCache.put(key, recolored);

    return recolored;
  }

  private static BufferedImage recolorImage(BufferedImage originalImage, Color color) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = originalImage.getRGB(x, y);
        if ((pixel & 0xFFFFFF) == 0xFFFFFF) coloredImage.setRGB(x, y, color.getRGB());
        else coloredImage.setRGB(x, y, pixel);
      }
    }
    return coloredImage;
  }

  private static class CharColorKey {
    private final char character;
    private final Color color;

    public CharColorKey(char character, Color color) {
      this.character = character;
      this.color = color;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CharColorKey key)) return false;
      return character == key.character && color.equals(key.color);
    }

    @Override
    public int hashCode() {
      return Objects.hash(character, color);
    }
  }

  private static class StringColorKey {
    private final String text;
    private final Color color;

    public StringColorKey(String text, Color color) {
      this.text = text;
      this.color = color;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof StringColorKey key)) return false;
      return text.equals(key.text) && color.equals(key.color);
    }

    @Override
    public int hashCode() {
      return Objects.hash(text, color);
    }
  }
}
