package com.nilsgke.jumpKingMinigame.map;

import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.Graphics;

public record Platform(
        Vertex pos,
        Vertex velocity,
        double height,
        double width
) implements GameObj {

  public void paintTo(Graphics g) {
    g.setColor(java.awt.Color.GREEN);
    g.fillRect((int) pos.x, (int) pos.y, (int) width, (int) height);
  }

}