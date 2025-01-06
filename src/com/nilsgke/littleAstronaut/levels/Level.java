package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.minigames.Minigame;
import name.panitz.game2d.Vertex;

public abstract class Level {
  public Vertex startPos;
  public Platform[] platforms;
  public Minigame minigame;
  public Platform completeZone;
  public boolean minigameStarted = false;

  abstract public int minCamY();


  public Level(Platform[] platforms, Minigame minigame, Vertex startPos, Platform completeZone) {
    this.platforms = platforms;
    this.minigame = minigame;
    this.startPos = startPos;
    this.completeZone = completeZone;
  }

  public boolean checkIfInCompletionZone(Player player) {
    if (!this.minigameStarted && player.touches(this.completeZone)) {
      System.out.println("start minigame");
      this.startMinigame();
      return true;
    }
    return false;
  }

  public void startMinigame() {
    this.minigameStarted = true;

    minigame.play();


  }
}
