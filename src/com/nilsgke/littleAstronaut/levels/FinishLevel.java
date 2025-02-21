package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import com.nilsgke.littleAstronaut.sprites.Text;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FinishLevel extends Level {
  private static final Animation planetAnimation;
  private static final BufferedImage backdrop;

  static {
    try {
      planetAnimation = new Animation(
              ImageHelper.readImageFileAt("/assets/planets/earth.png"),
              100, 20000, true);

      BufferedImage bd = ImageHelper.readImageFileAt("/assets/backdrops/finishLevel.png");
      System.out.println("read finish level bd");
      backdrop = ImageHelper.toBufferedImage(bd.getScaledInstance(bd.getWidth() *2, bd.getHeight() *2, Image.SCALE_DEFAULT));
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
                    new Platform(-550, -1000, 50, 1000),
                    new Platform(500, -1000, 50, 1000),
            },
            new Vertex(0, 65),
            new Vertex(0, 1000),
            new Vertex(-600, 300),
            planetAnimation,
            backdrop
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
  public void additionalPaint(Graphics2D g) {
    Color bgColor = new Color(0,0,0,50);
    Text.paintToWithBackgroundColor(g, "Vielen Dank fürs Spielen!", -400, -430, 4, bgColor);
    Text.paintToWithBackgroundColor(g, "Das wars. Du hast alle Planeten erkundigt.", -345, -360, 2, bgColor);
    Text.paintToWithBackgroundColor(g, "- Nils Goeke", -300, -320, 2, bgColor);
    Text.paintToWithBackgroundColor(g, "github.com/NilsGke", -50, -320, 2, bgColor);


    Level.paintPlanetSign(g, new Vertex(-500, -260), Level_1.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(-300, -200), Level_2.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(100, -200), Level_3.planetAnimation);
    Level.paintPlanetSign(g, new Vertex(300, -260), Level_4.planetAnimation);
  }

  @Override
  public void additionalChecks(long deltaTime, Player p) {}
}
