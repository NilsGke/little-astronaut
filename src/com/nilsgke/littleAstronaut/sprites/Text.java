package com.nilsgke.littleAstronaut.sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
      textMap = ImageHelper.readImageFileAt("/assets/text/text-white.png");
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

  public static void paintTo(Graphics2D g, String text, int x, int y, int scale) {
    paintTo(g, text, x, y, scale, Color.WHITE);
  }

  public static int getHeight(int scale) {
    return CHAR_HEIGHT * scale;
  }

  public static int getCharWidth(int scale) {
    return CHAR_WIDTH * scale;
  }

  public static void paintTo(Graphics2D g, String text, int x, int y, int scale, Color color) {
    paintTo(g, text, x, y, scale, color, new Color(0,0,0,0));
  }

  public static void paintToWithBackgroundColor(Graphics2D g, String text, int x, int y, int scale,  Color backgroundColor) {
    paintTo(g, text, x, y, scale, Color.WHITE, backgroundColor);
  }

  // Static method to paint text at a specified position
  public static void paintTo(Graphics2D g, String text, int x, int y, int scale, Color color, Color backgroundColor) {
    text = text.toLowerCase().replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue");

    // Check if the combined image is already cached
    StringColorKey key = new StringColorKey(text, color);
    BufferedImage combinedImage = combinedImageCache.get(key);
    if (combinedImage == null) {
      combinedImage = createCombinedImage(text, scale, color);
      combinedImageCache.put(key, combinedImage);
    }

    g.setColor(backgroundColor);
    int spaceCount = text.length() - text.replace(" ", "").length();
    g.fillRoundRect(x,y, (int) (combinedImage.getWidth() - spaceCount * getCharWidth(scale) * 0.3), combinedImage.getHeight(),8,8);

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
    if (cachedImage != null)
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

  private record CharColorKey(char character, Color color) {

    @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharColorKey(char character1, Color color1))) return false;
        return character == character1 && color.equals(color1);
      }

  }

  private record StringColorKey(String text, Color color) {

    @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringColorKey(String text1, Color color1))) return false;
        return text.equals(text1) && color.equals(color1);
      }

  }
}
