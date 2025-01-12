package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.minigames.TicTacToe;
import com.nilsgke.littleAstronaut.sprites.Text;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {
  static ImageTileset grassTileset = new ImageTileset(
          "assets/level1/grass-repeating.png",
          "assets/level1/grass-end-left.png",
          "assets/level1/grass-end-right.png"
  );


  public int minCamY() {
    return 500;
  }

  public Level_1() {
    super(
            new Platform[]{
                    new Platform(-600, 700, 1900, 100, grassTileset),
                    new Platform(1500 - 200, 690, 1300, 110, grassTileset),
                    new Platform(2300, 600, 1600, 200, grassTileset),
            },
            new TicTacToe(),
            new Vertex(0, 700),
            new Vertex(3750, 465)
    );
  }

  public void additionalPaint(Graphics g) {
    Text.paintTo(g, "Hallo kleiner Astronaut!", -470, 300, 5);
    Text.paintTo(g, "Du wurdest auserwählt um 6", -330, 370, 3);
    Text.paintTo(g, "verschiedene Planeten zu erkunden.", -400, 400, 3);
    Text.paintTo(g, "Laufe mit a und d oder den Pfeiltasten ->", -350, 500, 2);

    Text.paintTo(g, "Einen Sprung musst du Aufladen", 700, 400, 3);
    Text.paintTo(g, "Je länger du die Leertaste drückst,", 770, 430, 2);
    Text.paintTo(g, "desto höher springst du", 770, 430, 2);
    

    Text.paintTo(g, "Halte die Leertaste gedrückt", 1750, 400, 3);
    Text.paintTo(g, "um höher zu springen", 1850, 430, 3);

    Text.paintTo(g, "Am Ende jedes Levels musst du", 3000, 300, 3);
    Text.paintTo(g, "ein kleines Minispiel gewinnen", 2980, 330, 3);
    Text.paintTo(g, "um weiter zu kommen", 3110, 360, 3);

  }
}
