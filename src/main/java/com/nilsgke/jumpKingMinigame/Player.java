package com.nilsgke.jumpKingMinigame;

import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.Graphics;


public record Player(
        Vertex pos,
        Vertex velocity,
        double width,
        double height
) implements GameObj {


  @Override
  public void paintTo(Graphics g) {
    g.drawPolygon(
            new int[]{(int) (this.pos.x + this.width / 2), (int) (this.pos.x), (int) (this.pos.x + this.width)},
            new int[]{(int) this.pos.y, (int) (this.pos.y + this.height), (int) (this.pos.y + this.height)},
            3
    );
  }
}

