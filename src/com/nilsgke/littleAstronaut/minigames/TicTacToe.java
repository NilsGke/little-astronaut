package com.nilsgke.littleAstronaut.minigames;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

enum PLAYER {
  USER, COMPUTER
}

public class TicTacToe extends Minigame {
  PLAYER[][] board = new PLAYER[3][3];
  PLAYER turn = PLAYER.USER;
  PLAYER winner = null;

  int width = 300;
  int height = 300;


  private boolean isValidMove(int row, int col) {
    return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == null;
  }

  private boolean isGameOver() {
    return getWinner() != null || isBoardFull();
  }

  private boolean isBoardFull() {
    for (var row : board)
      for (var field : row)
        if (field == null) return false;

    return true;
  }

  private PLAYER getWinner() {
    // horizontal
    for (int i = 0; i < 3; i++)
      if (board[i][0] != null && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];


    // vertical
    for (int i = 0; i < 3; i++)
      if (board[0][i] != null && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];


    // diagonally
    if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];

    if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];

    return null;
  }


  @Override
  public void mouseClick(MouseEvent mouseEvent) {
    if(ended()) return;

    int x = mouseEvent.getX();
    int y = mouseEvent.getY();
    int boardX = (int) (x / (double) width * 3);
    int boardY = (int) (y / (double) height * 3);

    if (!isValidMove(boardX, boardY)) return;

    board[boardX][boardY] = turn;

    turn = turn == PLAYER.COMPUTER ? PLAYER.USER : PLAYER.COMPUTER;

    if (isGameOver()) {
      winner = getWinner();
      System.out.println(winner);
    }

  }

  @Override
  public boolean won() {
    return this.winner == PLAYER.USER;
  }

  @Override
  public boolean lost() {
    return this.winner == PLAYER.COMPUTER;
  }

  @Override
  public void paintTo(Graphics g) {
    // draw grid
    g.drawLine(0, height / 3, width, height / 3);
    g.drawLine(0, 2 * height / 3, width, 2 * height / 3);
    g.drawLine(width / 3, 0, width / 3, height);
    g.drawLine(2 * width / 3, 0, 2 * width / 3, height);
    g.setFont(new Font("Arial", Font.BOLD, 30));


    for (int x = 0; x < board.length; x++) {
      var row = board[x];
      for (int y = 0; y < row.length; y++) {
        var field = row[y];
        String symbol = field == null ? " " : field == PLAYER.USER ? "X" : "O";
        g.drawString(symbol, x * (width / 3) + 40, y * (width / 3) + 60);
      }
    }
  }

  public static void main(String[] args) {
    var game = new TicTacToe();
    game.play();
  }


  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public void init() {
  }

  @Override
  public void doChecks(int deltaTime) {

  }

  @Override
  public void keyPressedReaction(KeyEvent keyEvent) {

  }

  @Override
  public void keyReleasedReaction(KeyEvent keyEvent) {

  }

}