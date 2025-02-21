package com.nilsgke.littleAstronaut.map;

import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import name.panitz.game2d.Vertex;

public class Wall extends Platform {



  private Wall(int x, int y, double width, double height, ImageTileset tileset) {
    super(new Vertex(x, y), new Vertex(0, 0), width, height, tileset);

    // flip width and height
    double temp = this.width;
    //noinspection SuspiciousNameCombination
    this.width = this.height;
    this.height = temp;
    // the constructor generates a horizontal image that we need to rotate by 90 deg
    this.image = ImageHelper.rotateImage(this.image);
//    this.image = null;
  }


  /// takes in a normal horizontal tileset but rotates it 90 deg
  public static Wall createVerticalTileWall(int x, int y, int tileRepeat, ImageTileset tileset) {
    return new Wall(
            x,
            y,
            tileset.leftEnd.getWidth() * 2 + tileset.rightEnd.getWidth() * 2 + tileset.tile.getWidth() * 2 * tileRepeat,
            tileset.tile.getHeight() * 2,
            tileset
    );
  }


}

