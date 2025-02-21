package com.nilsgke.littleAstronaut.sprites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {

  public static BufferedImage readImageFileAt(String path) throws IOException {
    InputStream is = ImageHelper.class.getResourceAsStream(path);
    if (is == null) throw new IOException("image not found at path: " + path);
    System.out.println("readFile: " + path);
    return ImageIO.read(is);
  }



  /**
   * Converts a given Image into a BufferedImage
   *
   * @param img The Image to be converted
   * @return The converted BufferedImage
   */
  public static BufferedImage toBufferedImage(Image img) {
    if (img instanceof BufferedImage) return (BufferedImage) img;


    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
  }

  /**
   * Rotates a given BufferedImage by 90 degrees clockwise.
   *
   * @param src the BufferedImage to be rotated
   * @return a new BufferedImage that is the original image rotated by 90 degrees
   */
  public static BufferedImage rotateImage(BufferedImage src) {
    int width = src.getWidth();
    int height = src.getHeight();

    @SuppressWarnings("SuspiciousNameCombination") BufferedImage dest = new BufferedImage(height, width, src.getType());

    Graphics2D graphics2D = dest.createGraphics();
    graphics2D.translate((height - width) / 2, (height - width) / 2);
    graphics2D.rotate(Math.PI / 2, (double) height / 2, (double) width / 2);
    graphics2D.drawRenderedImage(src, null);

    return dest;
  }


}
