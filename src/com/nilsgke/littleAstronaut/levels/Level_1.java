package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Text;
import name.panitz.game2d.Vertex;

import java.awt.*;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {
  static ImageTileset grassTileset = new ImageTileset(
          "assets/level1/grass-repeating.png",
          "assets/level1/grass-end-left.png",
          "assets/level1/grass-end-right.png"
  );

  static ImageTileset defaultPlatformTileset = new ImageTileset(
          "assets/platforms/normal/normal.png",
          "assets/platforms/normal/end-left.png",
          "assets/platforms/normal/end-right.png"
  );

  public int minCamY() {
    return 500;
  }

  public Level_1() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-600, 700, 60, grassTileset),
                    Platform.createHorizontalTilePlatform(1500 - 200, 690, 30, grassTileset),
                    Platform.createHorizontalTilePlatform(2300, 600, 60, grassTileset),
                    Platform.createHorizontalTilePlatform(1200, 500, 2, defaultPlatformTileset)
            },
            new Vertex(0, 700),
            new Vertex(3900, 465)
    );
  }

  public void additionalPaint(Graphics g) {
    Text.paintTo(g, "Hallo kleiner Astronaut!", -470, 300, 5);
    Text.paintTo(g, "Du wurdest auserwählt um 6", -330, 370, 3);
    Text.paintTo(g, "verschiedene Planeten zu erkunden.", -400, 400, 3);
    Text.paintTo(g, "Laufe mit a und d oder den Pfeiltasten ->", -350, 500, 2);

    Text.paintTo(g, "Drücke zum Springen die Leertaste", 700, 400, 3);
    Text.paintTo(g, "Um in eine Richtung zu springen, musst du die ", 770, 440, 2);
    Text.paintTo(g, "Richtung (a,d) beim loslassen der Leertaste gedrückt halten", 650, 470, 2);
    

    Text.paintTo(g, "Um höher zu springen musst du", 1750, 400, 3);
    Text.paintTo(g, "die Leertaste länger drücken", 1750, 430, 3);

    Text.paintTo(g, "Du hast das Level geschafft,", 3000, 300, 3);
    Text.paintTo(g, "wenn du bei der Rakete angekommen bist.", 2900, 330, 3);

  }
}
