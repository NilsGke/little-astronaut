package com.nilsgke.littleAstronaut;

import name.panitz.game2d.Vertex;

// cam position is centered to player
public record Camera(Vertex pos, Vertex velocity, Vertex acceleration) {

  public Camera(Vertex pos) {
    this(pos, new Vertex(0, 0), new Vertex(0, 0));
  }


  void updateCamera(long deltaTime, Player player, int width, int height) {
    // calc distance from cam to player
    double camXDistanceToPlayer = player.pos().x + player.width() / 2 - pos().x;
    double xMarginFromCenter = width / 2.0 - width * Main.CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camXDistanceToPlayer) > xMarginFromCenter)
      // accelerate camera to the direction of the player
      acceleration().x += (camXDistanceToPlayer - (Math.signum(camXDistanceToPlayer) * xMarginFromCenter)) * deltaTime / 100.0;


    double camYDistanceToPlayer = player.pos().y + player.height() / 2 - pos().y;
    double yMarginFromCenter = height / 2.0 - height * Main.CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camYDistanceToPlayer) > yMarginFromCenter)
      acceleration().y += (camYDistanceToPlayer - (Math.signum(camYDistanceToPlayer) * yMarginFromCenter)) * deltaTime / 14.0;
    else velocity().y *= 0.5; // prevents hitting ground camera bounce to bottom and back

    // move camera

    // decelerate cam
    this.acceleration().x *= 0.9;
    this.acceleration().y *= 0.9;
    this.velocity().x *= 0.9;
    this.velocity().y *= 0.9;

    // update velocity
    this.velocity.x = this.velocity.x + this.acceleration.x * deltaTime / 1000.0;
    this.velocity.y = this.velocity.y + this.acceleration.y * deltaTime / 1000.0;

    // update pos
    this.pos.x = this.pos.x + this.velocity.x * deltaTime / 100.0;
    this.pos.y = this.pos.y + this.velocity.y * deltaTime / 100.0;
  }

}
