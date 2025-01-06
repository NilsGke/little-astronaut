package com.nilsgke.jumpKingMinigame.levels;

import com.nilsgke.jumpKingMinigame.Player;
import com.nilsgke.jumpKingMinigame.map.Platform;
import com.nilsgke.jumpKingMinigame.minigames.TicTacToe;
import name.panitz.game2d.Vertex;

/// **Earth**. <br>
/// In this level, the player will learn the basic controls and know how the game works. <br>
/// At the end, he will enter a small rocket and fly to the first planet
public class Level_1 extends Level {

  public Level_1() throws Exception {

    super(
            new Platform[]{
                    new Platform(-100, 700, 1100, 100),
                    new Platform(1000, 690, 1000, 110),
                    new Platform(2000, 600, 1000, 200),
            },
            new TicTacToe(),
            new Vertex(0, 700),
            new Platform(2800, 400, 200, 200)
    );

  }


  @Override
  public void doChecks(int deltaTime, Player player) {
    checkCompleted(player);
    if (!this.minigameStarted && player.touches(this.completeZone)) {
      System.out.println("start minigame");
      this.startMinigame();
    }
  }
}
