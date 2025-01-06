package com.nilsgke.jumpKingMinigame.levels;

import com.nilsgke.jumpKingMinigame.Player;
import com.nilsgke.jumpKingMinigame.map.Platform;
import com.nilsgke.jumpKingMinigame.minigames.Minigame;
import name.panitz.game2d.Vertex;

public abstract class Level {
  public Vertex startPos;
  public Platform[] platforms;
  Minigame minigame;
  public Platform completeZone;
  public boolean minigameStarted = false;


  Level(Platform[] platforms, Minigame minigame, Vertex startPos, Platform completeZone) {
    this.platforms = platforms;
    this.minigame = minigame;
    this.startPos = startPos;
    this.completeZone = completeZone;
  }

  public abstract void doChecks(int deltaTime, Player player);

  public void checkCompleted(Player player) {
    if(completeZone.touches(player)) {
      try {
        minigame.init();
      } catch (Exception e) {
        System.err.println("could not start minigame");
        throw new RuntimeException(e);
      }
    }
  }

  public void startMinigame() {
    this.minigameStarted = true;
    try {
      this.minigame.play();
    } catch (Exception e) {
      System.err.println("could not start minigame");
      throw new RuntimeException(e);
    }
  }
}
