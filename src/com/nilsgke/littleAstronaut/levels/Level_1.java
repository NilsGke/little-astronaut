package com.nilsgke.littleAstronaut.levels;


import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.minigames.TicTacToe;
import name.panitz.game2d.Vertex;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {
  static ImageTileset grassTileset = new ImageTileset("assets/level1/grass-repeating.png", "assets/level1/grass-end-left.png", "assets/level1/grass-end-right.png");

  public int minCamY() {return 500;}

  public Level_1() {

    super(
            new Platform[]{
                    new Platform(-100, 700, 1100, 100, grassTileset),
                    new Platform(1000, 690, 1000, 110, grassTileset),
                    new Platform(2000, 600, 1000, 200, grassTileset),
            },
            new TicTacToe(),
            new Vertex(0, 700),
            new Platform(800, 400, 200, 200)
    );

  }
}
