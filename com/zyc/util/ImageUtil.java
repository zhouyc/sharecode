package com.zyc.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.poi.hpsf.Thumbnail;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author zhou
 * @version v0.1
 * @date 2018-10-15 14:44
 * descr: 图片处理工具
 *
 */
public class ImageUtil {

    /**
     * Created by zhou on 2018-10-15
     * Descr: 缩放图片，
     *
     */
    public static boolean scaleImg(File imgFile,String destFile,int maxHeight,int maxWidth,boolean extrude){
        try {
            double scale = 1.0d;

            BufferedImage originalImage = ImageIO.read(imgFile);
//            System.out.println("height:"+originalImage.getHeight());
//            System.out.println("width:"+originalImage.getWidth());

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            //不允许拉伸并且宽度高度都小于指定的值，这种情况无法缩放处理，原图另存
            if(!extrude&&height<maxHeight&&width<maxWidth){
                System.out.println("不满足缩放要求，原图另存");
                Thumbnails.of(imgFile).scale(1.0).toFile(destFile);
                return true;
            }

            double ratio = 1.0;

            /*
                1、图片宽度高度大于指定的宽度高度，按较大边计算缩放比例
                2、图片宽度大于指定宽度，高度小于指定高度，按指定宽度计算缩放比例
                3、图片宽度小于指定宽度，高度大于指定高度，按指定高度计算缩放比例
                4、图片宽度高度小于指定宽度高度，判断是否拉伸，
                    如果是：取宽度/高度较小值进行缩放计算，如果否：就原图另存不做处理

             */

            double   ratioHeight = (new Integer(maxHeight)).doubleValue()/ height;
            double   ratioWidth = (new Integer(maxWidth)).doubleValue()/ width;
            if(height>maxHeight&&width>maxWidth){

                if(height>width){
                    ratio= ratioHeight;
                }else{
                    ratio= ratioWidth;
                }
            }else if(width>maxWidth&&height<maxHeight){
                ratio = ratioWidth;
            }else if(width<maxWidth&&height>maxHeight){
                ratio = ratioHeight;
            }else{
                if(extrude){
                    if(ratioHeight>ratioWidth){
                        ratio= ratioWidth;
                    }else{
                        ratio= ratioHeight;
                    }
                }else {
                    ratio = 1.0;
                }
            }

            //quality为设置图片质量，(0,1]，数字越大图片质量越高
            Thumbnails.of(imgFile).scale(ratio).allowOverwrite(true).outputQuality(0.5f).toFile(destFile);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 裁剪图片方法
     * @param startX 裁剪开始x坐标
     * @param startY 裁剪开始y坐标
     * @param endX 裁剪结束x坐标
     * @param endY 裁剪结束y坐标
     * @return
     */
    public static boolean cropImage(File imgFile,String destFile, int startX, int startY, int endX, int endY) {
        try {
            BufferedImage originalImage = ImageIO.read(imgFile);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            if (startX == -1) {
                startX = 0;
            }
            if (startY == -1) {
                startY = 0;
            }
            if (endX == -1) {
                endX = width - 1;
            }
            if (endY == -1) {
                endY = height - 1;
            }
            BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
            for (int x = startX; x < endX; ++x) {
                for (int y = startY; y < endY; ++y) {
                    int rgb = originalImage.getRGB(x, y);
                    result.setRGB(x - startX, y - startY, rgb);
                }
            }

            ImageIO.write(result,"jpg",new File(destFile));

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Created by zhou on 2018-10-15
     * Descr: 自定义裁切比例
     * ratio：宽:高的值
     *
     */
    public static boolean cropImage(File imgFile,String destFile,double ratio){
        try {

            BufferedImage originalImage = ImageIO.read(imgFile);
//            System.out.println("height:"+originalImage.getHeight());
//            System.out.println("width:"+originalImage.getWidth());

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            /*
                ratio一般是4:3/16:9/1:1等值
                计算图片宽/高的值
                1、结果大于ratio，说明实际比例太大，以高度为基准计算宽度的实际值
                2、结果小于ratio，说明实际比例太小，以宽度为基准计算高度的实际值
                3、结果等于ratio，保持不变，另存即可
             */

            if(ratio<=0){
                ratio = 1.0d;
            }

            double actualRatio = (new Integer(width)).doubleValue()/ height;

            int newWidth = width;
            int newHeight = height;

            if(actualRatio>ratio){
                newWidth = new Double(height*ratio).intValue();
            }else if(actualRatio<ratio){
                newHeight = new Double(width/ratio).intValue();
            }


            Thumbnails.of(imgFile).sourceRegion(Positions.CENTER,newWidth,newHeight).size(newWidth,newHeight).allowOverwrite(true).outputQuality(0.9f).toFile(destFile);

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
/*

    public static void main(String[] args) {
        scaleImg(new File("D:\\pdf\\d9db142c95964f449f209e821aa0957b.jpg"),"d:\\pdf\\ddddddddddd.jpg",300,500,false);
    }
*/

}
