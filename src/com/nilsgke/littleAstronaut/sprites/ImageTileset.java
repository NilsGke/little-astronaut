package com.nilsgke.littleAstronaut.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTileset {
  public BufferedImage tile;
  public BufferedImage leftEnd;
  public BufferedImage rightEnd;

  public ImageTileset(String tileImgPath, String leftEndImgPath, String rightEndImgPath){
    try {
      this.tile = ImageHelper.readImageFileAt(tileImgPath);
      this.leftEnd = ImageHelper.readImageFileAt(leftEndImgPath);
      this.rightEnd = ImageHelper.readImageFileAt(rightEndImgPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

