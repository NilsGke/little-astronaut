package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Level_3 extends Level {
  public static final Animation planetAnimation;

  static {
    try {
      planetAnimation = new Animation(
              ImageIO.read(new File("assets/planets/ice.png")),
              100, 20000, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  public Level_3() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-700, 0, 45, Tilesets.iceFloor),
                    Platform.createHorizontalTilePlatform(250, -200, 2, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(450, -400, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(230, -480, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-200, -480, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-350, -730, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-100, -930, 10, Tilesets.stonePlatform),
            },
            new Vertex(0, -30),
            new Vertex(400, -1065),
            new Vertex(-300, -200),
            planetAnimation
    );
  }

  @Override
  public int minCamY() {
    return -300;
  }

  public Color backgroundColor() {
    return new Color(185, 233, 255);
  }

  @Override
  public void additionalPaint(Graphics g) {
  }
}
