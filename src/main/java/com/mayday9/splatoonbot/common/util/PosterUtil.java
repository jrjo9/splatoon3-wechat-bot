package com.mayday9.splatoonbot.common.util;


//import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;


/**
 * @author Lianjiannan
 * @since 2024/9/25 17:36
 **/
public class PosterUtil {

    public static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight, int type) {
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g = scaledImage.createGraphics();
        //消除文字锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Calculate the ratio between the original and scaled image size
        double scaleX = (double) targetWidth / originalImage.getWidth();
        double scaleY = (double) targetHeight / originalImage.getHeight();
        double scale = Math.min(scaleX, scaleY);
        // Now we perform the actual scaling
        int newWidth = (int) (originalImage.getWidth() * scaleX);
        int newHeight = (int) (originalImage.getHeight() * scaleY);
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;
        g.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), x, y, null);
        g.dispose();
        return scaledImage;
    }

    /**
     * 从新设置海报图片的宽和高
     *
     * @param originalImage 原始图片
     * @param targetWidth   宽
     * @param targetHeight  高
     * @return BufferedImage
     */
    public static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        return scaleImage(originalImage, targetWidth, targetHeight, type);
    }

    /**
     * &#064;Author
     * &#064;Description  海报横向文字写字换行算法
     * &#064;Date  18:08 2024/4/24
     * &#064;Param 参数 Graphics2D 对象 、font 字体设置 、 文字、 x轴左边、 y轴坐标 、每行字体的换行宽度
     **/
//    public static void drawWordAndLineFeed(Graphics2D g2d, Font font, String words, int wordsX, int wordsY, int wordsWidth) {
//        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
//        // 获取字符的最高的高度
//        int height = metrics.getHeight();
//
//        int width = 0;
//        int count = 0;
//        int total = words.length();
//        String subWords = words;
//        int b = 0;
//        for (int i = 0; i < total; i++) {
//            // 统计字符串宽度 并与 预设好的宽度 作比较
//            if (width <= wordsWidth) {
//                width += metrics.charWidth(words.charAt(i)); // 获取每个字符的宽度
//                count++;
//            } else {
//                // 画 除了最后一行的前几行
//                String substring = subWords.substring(0, count);
//                g2d.drawString(substring, wordsX, wordsY + (b * height));
//                subWords = subWords.substring(count);
//                b++;
//                width = 0;
//                count = 0;
//            }
//            // 画 最后一行字符串
//            if (i == total - 1) {
//                g2d.drawString(subWords, wordsX, wordsY + (b * height));
//            }
//        }
//    }

    /**
     * 将上传的个人头像裁剪成对应的海报比例的头像，并裁剪成圆形
     *
     * @param image 读取的头像找
     * @param size  宽高大小像素
     * @return BufferedImage
     */
    public static BufferedImage resizeAndClipToCircle(BufferedImage image, int size) {
        // 缩小图片
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, size, size, null);
        g2d.dispose();

        // 裁剪成圆形
        BufferedImage circularImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d2 = circularImage.createGraphics();
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, size, size);
        g2d2.setClip(ellipse);
        g2d2.drawImage(resizedImage, 0, 0, size, size, null);
        g2d2.dispose();

        return circularImage;
    }

    /**
     * 部分文字 垂直排序时，不满多列，最后一列居中显示
     *
     * @param textGraphics Graphics 对象
     * @param text         要传入的海报描述
     */
    public static void drawMyString(Graphics textGraphics, String text, Color color) {

        // 每列显示的汉字数量
        int columnSize = 7;
        // 文字之间的垂直间距
        int verticalSpacing = 75;

        // 获取字体渲染上下文
        FontMetrics fm = textGraphics.getFontMetrics();
        // 获取字体的高度
        int fontHeight = fm.getHeight();
        System.out.println(fontHeight);
        // 计算每列的宽度
        int columnWidth = fontHeight + verticalSpacing;

        // 设置初始位置
        int x = 280;
        int y = 450;
        Font fontFour = new Font(" Source Han Sans CN", Font.BOLD, 100);
        textGraphics.setFont(fontFour);
        textGraphics.setColor(color);
        // 绘制文字
        int charCount = 0;
        int totalColumns = (int) Math.ceil((double) text.length() / columnSize); // 总列数
        int totalRows = Math.min(columnSize, text.length()); // 总行数
        int remainingChars = text.length() % columnSize; // 最后一列剩余字符数

        for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {
            for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
                if (charCount >= text.length()) break;
                char ch = text.charAt(charCount);
                // 计算当前位置
                int cx = x - columnIndex * columnWidth;
                int cy = y + rowIndex * fontHeight + rowIndex * verticalSpacing; // 加入垂直偏移量
                // 计算当前位置
//                int cx = x - columnIndex * columnWidth;
//                int cy = y + rowIndex * fontHeight + rowIndex * verticalSpacing + columnIndex ;

                // 如果是最后一列并且不满 7 个字符，则需要将剩余字符居中
                if (columnIndex == totalColumns - 1 && remainingChars > 0) {
                    int extraVerticalSpace = (columnSize - remainingChars) * (fontHeight + verticalSpacing) / 2;
                    cy += extraVerticalSpace;
                }
                // 绘制文字
                textGraphics.drawString(String.valueOf(ch), cx, cy);
                charCount++;
            }
        }
    }

    /**
     * 横向显示 垂直文字
     *
     * @param textGraphics Graphics 对象
     * @param text         显示的 内容
     * @param number       没列显示汉字数量
     */
    public static void drawString(Graphics textGraphics, String text, int number) {

        // 每列显示的汉字数量
        int columnSize = number;
        // 文字之间的垂直间距
        int verticalSpacing = 50;

        // 获取字体渲染上下文
        FontMetrics fm = textGraphics.getFontMetrics();
        // 获取字体的高度
        int fontHeight = fm.getHeight();

        // 计算每列的宽度
        int columnWidth = fontHeight + verticalSpacing;

        // 设置初始位置
        int x = 0;
        int y = 0;
        switch (number) {
            case 3:
                x = 1250;
                y = 790;
                break;
            case 10:
                x = 1150;
                y = 800;
                break;
            // 可以添加更多的条件分支
            default:
                // 默认情况下的坐标设置
                break;
        }
        if (number == 10) {
            Font fontFour = new Font(text, Font.BOLD, 60);
            textGraphics.setFont(fontFour);
            textGraphics.setColor(Color.WHITE);
        }
        // 绘制文字
        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);
            // 计算当前位置
            int cx = x - (i / columnSize) * columnWidth;
            int cy = y + (i % columnSize) * fontHeight;
            // 绘制文字
            textGraphics.drawString(String.valueOf(ch), cx, cy);
        }
    }

    public static void drawCenteredText(Graphics graphics, String text, int oneY, int twoY) {
        // 设置字体和颜色
        Font font = new Font("宋体", Font.BOLD, 50);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        // 获取字体渲染上下文
        FontMetrics fm = graphics.getFontMetrics(font);
        // 截取第二行文字的部分
        String textTwo = text.substring(text.indexOf("第"));
        // 获取第一行文字的宽度
        String textOne = text.substring(0, text.indexOf("第"));
        // 获取第二行文字的宽度
        int textTwoWidth = fm.stringWidth(textTwo);
        // 计算第一行文字的起始x坐标，使其水平居中
        int oneX = 50;
        // 计算第二行文字的起始x坐标，使其水平居中
        int twoX = (450 - textTwoWidth) / 2;
        // 绘制第一行文字
        graphics.drawString(textOne, oneX, oneY);
        // 绘制第二行文字
        graphics.drawString(textTwo, twoX, twoY);
    }

    /**
     * 在Graphics2D对象上绘制竖排文字
     *
     * @param textGraphics Graphics2D对象
     * @param text         要绘制的文字
     */
    public static void drawMyLectureString(Graphics textGraphics, String text) {

        // 每列显示的汉字数量
        int columnSize = 25;
        // 文字之间的垂直间距
        int verticalSpacing = 1;

        // 获取字体渲染上下文
        FontMetrics fm = textGraphics.getFontMetrics();
        // 获取字体的高度
        int fontHeight = fm.getHeight() - 30;

        // 计算每列的宽度
        int columnWidth = fontHeight + verticalSpacing;

        // 设置初始位置
        int x = 800;
        int y = 190;
        Font fontFour = new Font("宋体", Font.BOLD, 40);
        textGraphics.setFont(fontFour);
        textGraphics.setColor(Color.WHITE);

        // 计算总列数
        int totalColumns = (int) Math.ceil((double) text.length() / columnSize); // 总列数

        // 绘制文字
        int charCount = 0;
        for (int rowIndex = 0; rowIndex < columnSize; rowIndex++) {
            for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {
                int charIndex = rowIndex * totalColumns + columnIndex;
                if (charIndex >= text.length()) break;
                char ch = text.charAt(charIndex);
                // 计算当前位置
                int cx = x - columnIndex * columnWidth;
                int cy = y + rowIndex * fontHeight + rowIndex * verticalSpacing; // 加入垂直偏移量
                // 绘制文字
                textGraphics.drawString(String.valueOf(ch), cx, cy);
            }
        }
    }


    /**
     * 字体倾斜
     */
    public static void qinxie(Font fontMonth, Graphics2D graphics) {
        // 创建 AffineTransform 对象
        AffineTransform month = new AffineTransform();
        // 设置水平倾斜
        month.shear(-0.2, 0); // + 向左  -  向右倾斜
        // 应用变换到字体
        fontMonth = fontMonth.deriveFont(month);
        graphics.setFont(fontMonth);
    }

    /**
     * 文字垂直
     */
    public static void drawSecondString(Graphics textGraphics, String text, int number) {

        // 每列显示的汉字数量
        int columnSize = number;
        // 文字之间的垂直间距
        int verticalSpacing = columnSize == 15 ? -500 : 50;
        // 获取字体渲染上下文
        FontMetrics fm = textGraphics.getFontMetrics();
        // 获取字体的高度
        int fontHeight = fm.getHeight();

        // 计算每列的宽度
        int columnWidth = columnSize == 15 ? 60 : fontHeight + verticalSpacing;

        // 设置初始位置
        int x = 0;
        int y = 0;
        switch (number) {
            case 3:
                x = 1100;
                y = 200;
                break;
            case 10:
                x = 1000;
                y = 210;
                break;
            case 15:
                x = 800;
                y = 210;
                break;
            // 可以添加更多的条件分支
            default:
                // 默认情况下的坐标设置
                break;
        }
        if (number == 10) {
            Font fontFour = new Font(text, Font.BOLD, 60);
            textGraphics.setFont(fontFour);
            textGraphics.setColor(Color.WHITE);
        } else if (number == 15) {
            Font fontFour = new Font(text, Font.BOLD, 40);
            textGraphics.setFont(fontFour);
            textGraphics.setColor(Color.WHITE);
        }
        // 绘制文字
        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);
            // 计算当前位置
            int cx = x - (i / columnSize) * columnWidth;
            int cy = y + (i % columnSize) * fontHeight;

            textGraphics.drawString(String.valueOf(ch), cx, cy);
        }
    }

}
