package com.nilsgke.littleAstronaut.sprites;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageTileset {
  public BufferedImage tile;
  public BufferedImage leftEnd;
  public BufferedImage rightEnd;

  public ImageTileset(String tileImgPath, String leftEndImgPath, String rightEndImgPath){
    try {
      this.tile = ImageIO.read(new File(tileImgPath));
      this.leftEnd = ImageIO.read(new File(leftEndImgPath));
      this.rightEnd = ImageIO.read(new File(rightEndImgPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

