package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.Text;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FinishLevel extends Level {
  private static final Animation planetAnimation;

  static {
    try {
      planetAnimation = new Animation(
              ImageIO.read(new File("assets/planets/earth.png")),
              100, 20000, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public FinishLevel() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-500, 0, 16, Tilesets.grassFloor),
                    Platform.createHorizontalTilePlatform(-500, -30, 3, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-500, -60, 2, Tilesets.defaultPlatform),
                    Platform.createHorizontalTilePlatform(244, -30, 3, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(336, -60, 2, Tilesets.defaultPlatform),
            },
            new Vertex(0, 65),
            new Vertex(0, 1000),
            new Vertex(-600, 300),
            planetAnimation
    );
  }

  @Override
  public int minCamY() {
    return -200;
  }

  @Override
  public Color backgroundColor() {
    return new Color(123, 207, 255);
  }

  @Override
  public void additionalPaint(Graphics g) {
    Text.paintTo(g, "Vielen Dank fürs Spielen!", -400, -380, 4);
    Text.paintTo(g, "Das wars. Du hast alle 6 Planeten erkundigt.", -335, -310, 2);
    Text.paintTo(g, "- Nils Goeke", -100, -270, 2);

    Level.paintPlanetSign(g, new Vertex(-500, -260), Level_1.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(-300, -200), Level_2.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(100, -200), Level_3.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(300, -260), Level_4.planetAnimation);
  }
}
