package com.midevip.common.util;

import com.itextpdf.text.pdf.PdfReader;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

/**
 * 使用pdfbox提取pdf文档的文字和图片内容
 * pdfbox官网：https://pdfbox.apache.org/
 * maven依赖如下：
 *          <dependency>
 *             <groupId>org.apache.pdfbox</groupId>
 *             <artifactId>fontbox</artifactId>
 *             <version>2.0.1</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.apache.pdfbox</groupId>
 *             <artifactId>pdfbox</artifactId>
 *             <version>2.0.1</version>
 *         </dependency>
 *        <dependency>
 *             <groupId>com.itextpdf</groupId>
 *             <artifactId>itextpdf</artifactId>
 *             <version>5.5.13</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>net.coobird</groupId>
 *             <artifactId>thumbnailator</artifactId>
 *             <version>0.4.8</version>
 *         </dependency>
 */
public class PdfTest {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 此方法示例代码来源于官方文档：
     * https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/util/
     * @param pdfFilePath
     * @throws Exception
     */
    public static void extractText(String pdfFilePath) throws Exception{
        try (PDDocument document = PDDocument.load(new File(pdfFilePath)))
        {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent())
            {
                throw new IOException("You do not have permission to extract text");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p)
            {
                // 这里分为一页一页的提取，如果不设置，默认会把所有页的内容一次性提取出来，根据需要选择
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                //提取内容就这一行代码
                //提取内容很彻底，包括了页眉页脚的内容也都会被提出来
                String text = stripper.getText(document);

                String pageStr = String.format("page %d:", p);
                System.out.println(pageStr);
                //为了打印出来更美观
                for (int i = 0; i < pageStr.length(); ++i)
                {
                    System.out.print("-");
                }
                System.out.println();
                System.out.println(text.trim());
                System.out.println();
            }
        }
    }

    public static void pdfParse(String pdfPath) throws Exception {
        InputStream input = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(pdfPath));

            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("标题:" + info.getTitle());
            System.out.println("主题:" + info.getSubject());
            System.out.println("作者:" + info.getAuthor());
            System.out.println("关键字:" + info.getKeywords());

            System.out.println("应用程序:" + info.getCreator());
            System.out.println("pdf 制作程序:" + info.getProducer());

            System.out.println("作者:" + info.getTrapped());

            System.out.println("创建时间:" + dateFormat(info.getCreationDate()));
            System.out.println("修改时间:" + dateFormat(info.getModificationDate()));


            //获取内容信息
            PDFTextStripper pts = new PDFTextStripper();
            String content = pts.getText(document);
            System.out.println("内容:" + content);

            /** 文档页面信息 **/
            PDDocumentCatalog cata = document.getDocumentCatalog();
            int count = 1;
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage page = document.getPage(i);
                if (null != page) {
                    //获取到所有rescourse信息
                    PDResources res = page.getResources();
                    Iterable<COSName> xit = res.getXObjectNames();
                    Iterator<COSName> iterator = xit.iterator();
                    while (iterator.hasNext()){
                        COSName cosName = iterator.next();
                        System.out.println(cosName.getName());
                        //判断是否图片资源，这个提取图片也很彻底，包括页眉页脚的图片也会被获取到
                        if(res.isImageXObject(cosName)){
                            PDImageXObject pdImageXObject = (PDImageXObject)res.getXObject(cosName);
                            //这里保存图片我用了谷歌的thumbnailator框架，也可以用自己的方法去保存BufferedImage对象到本地图片
                            Thumbnails.of(pdImageXObject.getImage()).scale(0.9).toFile(new File("D:\\pdf\\"+System.currentTimeMillis()+".jpg"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != input)
                input.close();
            if (null != document)
                document.close();
        }
    }

    /***
     * PDF文件转PNG图片，全部页数
     *
     * @param PdfFilePath pdf完整路径
     * @param dpi dpi越大转换后越清晰，相对转换速度越慢
     * @return
     */
    private static boolean pdf2Image(String PdfFilePath, String dstImgFolder, int dpi) {
        File file = new File(PdfFilePath);
        PDDocument pdDocument;
        try {
            String imgPDFPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
            String imagePDFName = file.getName().substring(0, dot); // 获取图片文件名
            String imgFolderPath = null;
            if (dstImgFolder.equals("")) {
                imgFolderPath = imgPDFPath + File.separator + imagePDFName;// 获取图片存放的文件夹路径
            } else {
                imgFolderPath = dstImgFolder + File.separator + imagePDFName;
            }

            if (createDirectory(imgFolderPath)) {

                pdDocument = PDDocument.load(file);
                PDFRenderer renderer = new PDFRenderer(pdDocument);
                /* dpi越大转换后越清晰，相对转换速度越慢 */
                PdfReader reader = new PdfReader(PdfFilePath);
                int pages = reader.getNumberOfPages();
                StringBuffer imgFilePath = null;
                for (int i = 0; i < pages; i++) {
                    String imgFilePathPrefix = imgFolderPath + File.separator + imagePDFName;
                    imgFilePath = new StringBuffer();
                    imgFilePath.append(imgFilePathPrefix);
                    imgFilePath.append("_");
                    imgFilePath.append(String.valueOf(formatNumber(i+1)));
                    imgFilePath.append(".jpg");
                    File dstFile = new File(imgFilePath.toString());
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);


                    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                    writer.setOutput(ImageIO.createImageOutputStream(dstFile));
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.3f);
                    writer.write(null, new IIOImage(image, null, null), param);

//                    ImageIO.write(image, "jpg", dstFile);
                }
                System.out.println("PDF文档转图片成功！"+dstImgFolder);
                return true;
            } else {
                System.out.println("PDF文档转图片失败：" + "创建" + imgFolderPath + "失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String formatNumber(int i){
        if(i<10){
            return "00"+i;
        }else if(i<100){
            return "0"+i;
        }else{
            return i+"";
        }
    }

    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }

    public static String dateFormat(Calendar calendar) throws Exception {
        if (null == calendar)
            return null;
        String date = null;
        try {
            String pattern = DATE_FORMAT;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            date = format.format(calendar.getTime());
        } catch (Exception e) {
            throw e;
        }
        return date == null ? "" : date;
    }

}
