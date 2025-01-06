package com.nilsgke.littleAstronaut;

import name.panitz.game2d.Vertex;

// cam position is centered to player
public record Camera(Vertex pos, Vertex velocity, Vertex acceleration) {

  public Camera(Vertex pos) {
    this(pos, new Vertex(0,0), new Vertex(0,0));
  }

  public void update(int deltaTimeMs) {

    // decelerate cam
    this.acceleration().x *= 0.9;
    this.acceleration().y *= 0.9;
    this.velocity().x *= 0.9;
    this.velocity().y *= 0.9;

    // update velocity
    this.velocity.x = this.velocity.x + this.acceleration.x * deltaTimeMs / 1000.0;
    this.velocity.y = this.velocity.y + this.acceleration.y * deltaTimeMs / 1000.0;

    // update pos
    this.pos.x = this.pos.x + this.velocity.x * deltaTimeMs / 100.0;
    this.pos.y = this.pos.y + this.velocity.y * deltaTimeMs / 100.0;
  }
}
