package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.Text;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Level_2 extends Level {
  public static final Animation planetAnimation;

  static {
    try {
      planetAnimation = new Animation(ImageIO.read(new File("assets/planets/desert.png")), 50, 10000, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public Level_2() {
    super(new Platform[]{Platform.createHorizontalTilePlatform(-700, 0, 45, Tilesets.desertFloor), Platform.createHorizontalTilePlatform(250, -200, 2, Tilesets.defaultPlatform), Platform.createHorizontalTilePlatform(0, -400, 1, Tilesets.defaultPlatform), Platform.createHorizontalTilePlatform(-300, -400, 1, Tilesets.defaultPlatform), Platform.createHorizontalTilePlatform(-500, -600, 1, Tilesets.defaultPlatform), Platform.createHorizontalTilePlatform(-300, -800, 1, Tilesets.defaultPlatform), Platform.createHorizontalTilePlatform(-100, -1000, 10, Tilesets.defaultPlatform)}, new Vertex(0, -30), new Vertex(400, -1135), new Vertex(-300, -200), planetAnimation);
  }

  @Override
  public int minCamY() {
    return -300;
  }

  public Color backgroundColor() {
    return new Color(255, 168, 109);
  }

  @Override
  public void additionalPaint(Graphics g) {
    Text.paintTo(g, "Hier ist nichts!", 1000, -200, 2);
    Text.paintTo(g, "Wirklich nicht!", 2000, -200, 2);
  }

  @Override
  public void additionalChecks(long deltaTime, Player p) {
  }
}
