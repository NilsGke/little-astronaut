package com.nilsgke.littleAstronaut.sprites;

public class Tilesets {


  public static ImageTileset defaultPlatform = new ImageTileset(
          "/assets/platforms/normal/normal.png",
          "/assets/platforms/normal/end-left.png",
          "/assets/platforms/normal/end-right.png"
  );

  public static ImageTileset stonePlatform = new ImageTileset(
          "/assets/platforms/stone/stone.png",
          "/assets/platforms/stone/end-left.png",
          "/assets/platforms/stone/end-right.png"
  );

  public static ImageTileset darkRedPlatform = new ImageTileset(
          "/assets/platforms/darkred/darkRed.png",
          "/assets/platforms/darkred/end-left.png",
          "/assets/platforms/darkred/end-right.png"
  );

  // floors
  public static ImageTileset grassFloor = new ImageTileset(
          "/assets/floors/grass/grass-repeating.png",
          "/assets/floors/grass/grass-end-left.png",
          "/assets/floors/grass/grass-end-right.png"
  );

  public static ImageTileset desertFloor = new ImageTileset(
          "/assets/floors/desert/desert-repeating.png",
          "/assets/floors/desert/desert-end-left.png",
          "/assets/floors/desert/desert-end-right.png"
  );

  public static ImageTileset iceFloor = new ImageTileset(
          "/assets/floors/ice/ice-repeating.png",
          "/assets/floors/ice/ice-end-left.png",
          "/assets/floors/ice/ice-end-right.png"
  );

  public static ImageTileset netherFloor = new ImageTileset(
          "/assets/floors/nether/nether-repeating.png",
          "/assets/floors/nether/nether-end-left.png",
          "/assets/floors/nether/nether-end-right.png"
  );

}
