package com.nilsgke.jumpKingMinigame.levels;

import com.nilsgke.jumpKingMinigame.Player;
import com.nilsgke.jumpKingMinigame.map.Platform;
import com.nilsgke.jumpKingMinigame.minigames.Minigame;
import name.panitz.game2d.Vertex;

public abstract class Level {
  public Vertex startPos;
  public Platform[] platforms;
  public Minigame minigame;
  public Platform completeZone;
  public boolean minigameStarted = false;


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
