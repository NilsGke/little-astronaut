package com.nilsgke.jumpKingMinigame;

import name.panitz.game2d.Vertex;

// cam position is centered to player
public record Camera(Vertex pos, Vertex velocity, Vertex acceleration) {

  public Camera(Vertex pos) {
    this(pos, new Vertex(0,0), new Vertex(0,0));
  }

  public void update(int deltaTimeMs) {
    // update velocity
    this.velocity.x = this.velocity.x + this.acceleration.x * deltaTimeMs / 1000.0;
    this.velocity.y = this.velocity.y + this.acceleration.y * deltaTimeMs / 1000.0;

    // update pos
    this.pos.x = this.pos.x + this.velocity.x * deltaTimeMs / 100.0;
    this.pos.y = this.pos.y + this.velocity.y * deltaTimeMs / 100.0;
  }
}
