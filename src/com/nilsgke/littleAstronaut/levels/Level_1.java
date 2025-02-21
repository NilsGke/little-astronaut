package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.sprites.*;
import com.nilsgke.littleAstronaut.map.Platform;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {
  public static final Animation planetAnimation;
  private static final BufferedImage backdrop;

  static {
    try {
      planetAnimation = new Animation(
              ImageHelper.readImageFileAt("/assets/planets/earth.png"),
              100, 20000, true);

      BufferedImage bd = ImageHelper.readImageFileAt("/assets/backdrops/level_1.png");
      backdrop = ImageHelper.toBufferedImage(bd.getScaledInstance(bd.getWidth() *2, bd.getHeight() *2, Image.SCALE_DEFAULT));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public int minCamY() {
    return 500;
  }

  public Color backgroundColor() {
      return new Color(123, 207, 255);
  }

  public Level_1() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-600, 700, 60, Tilesets.grassFloor),
                    Platform.createHorizontalTilePlatform(1500 - 200, 690, 30, Tilesets.grassFloor),
                    Platform.createHorizontalTilePlatform(2300, 600, 80, Tilesets.grassFloor),
            },
            new Vertex(0, 700),
            new Vertex(4300, 440),
            new Vertex( -300, 500),
            planetAnimation,
            backdrop
    );
  }

  public void additionalPaint(Graphics2D g) {
    Color bgColor = new Color(0,0,0,50);
    Text.paintToWithBackgroundColor(g, "Hallo kleiner Astronaut!", -470, 300, 5, bgColor);
    Text.paintToWithBackgroundColor(g, "Du wurdest auserwählt um 3", -330, 370, 3, bgColor);
    Text.paintToWithBackgroundColor(g, "verschiedene Planeten zu erkunden.", -400, 400, 3, bgColor);
    Text.paintToWithBackgroundColor(g, "Laufe mit a und d oder den Pfeiltasten ->", -350, 470, 2, bgColor);

    Text.paintToWithBackgroundColor(g, "Drücke zum Springen die Leertaste", 700, 400, 3, bgColor);
    Text.paintToWithBackgroundColor(g, "Um in eine Richtung zu springen, musst du die ", 770, 440, 2, bgColor);
    Text.paintToWithBackgroundColor(g, "Richtung (a,d) beim loslassen der Leertaste gedrückt halten", 650, 470, 2, bgColor);


    Text.paintToWithBackgroundColor(g, "Um höher zu springen musst du", 1750, 400, 3, bgColor);
    Text.paintToWithBackgroundColor(g, "die Leertaste länger drücken", 1750, 430, 3, bgColor);

    Text.paintToWithBackgroundColor(g, "Übrigens kannst du mir escape", 2700, 300, 2, bgColor);
    Text.paintToWithBackgroundColor(g, "den multiplayer aktivieren", 2750, 330, 2, bgColor);

    Text.paintToWithBackgroundColor(g, "Du hast das Level geschafft,", 3600, 300, 3, bgColor);
    Text.paintToWithBackgroundColor(g, "wenn du bei der Rakete angekommen bist.", 3500, 330, 3, bgColor);

  }

  @Override
  public void additionalChecks(long deltaTime, Player p) {}
}
