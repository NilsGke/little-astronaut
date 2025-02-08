package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Text;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {
  public static final Animation planetAnimation;

  static {
    try {
      planetAnimation = new Animation(
              ImageIO.read(new File("assets/planets/earth.png")),
              100, 20000, true);
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
                    Platform.createHorizontalTilePlatform(2300, 600, 60, Tilesets.grassFloor),
            },
            new Vertex(3700, 500),
//            new Vertex(0, 700),
            new Vertex(3900, 430),
            new Vertex( -300, 500),
            planetAnimation
    );
  }

  public void additionalPaint(Graphics g) {
    Text.paintTo(g, "Hallo kleiner Astronaut!", -470, 300, 5);
    Text.paintTo(g, "Du wurdest auserwählt um 6", -330, 370, 3);
    Text.paintTo(g, "verschiedene Planeten zu erkunden.", -400, 400, 3);
    Text.paintTo(g, "Laufe mit a und d oder den Pfeiltasten ->", -350, 470, 2);

    Text.paintTo(g, "Drücke zum Springen die Leertaste", 700, 400, 3);
    Text.paintTo(g, "Um in eine Richtung zu springen, musst du die ", 770, 440, 2);
    Text.paintTo(g, "Richtung (a,d) beim loslassen der Leertaste gedrückt halten", 650, 470, 2);


    Text.paintTo(g, "Um höher zu springen musst du", 1750, 400, 3);
    Text.paintTo(g, "die Leertaste länger drücken", 1750, 430, 3);

    Text.paintTo(g, "Du hast das Level geschafft,", 3000, 300, 3);
    Text.paintTo(g, "wenn du bei der Rakete angekommen bist.", 2900, 330, 3);

  }

  @Override
  public void additionalChecks(long deltaTime, Player p) {}
}
