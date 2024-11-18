package com.mayday9.splatoonbot.common.util;

import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatBankaraSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatBankaraSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatRegularSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatRegularSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatSalmonRunDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatSalmonRunDrawWeaponDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatXSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatXSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/25 17:20
 **/
@Component
public class ImageGenerator {

    private static String uploadFilePath;

    @Value("${file.uploadFolder:D://wxBot/}")
    private String uploadFolder;

    @PostConstruct
    public void init() {
        uploadFilePath = uploadFolder;
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        uploadFilePath = "D://wxBot/";
//        List<SplatBankaraSchedulesDrawDTO> dataList = getSplatBankaraSchedulesDrawDTOList();
//        generateBankaraMatchImage(dataList);

        List<SplatRegularSchedulesDrawDTO> dataList = getSplatRegularSchedulesDrawDTOList();
        generateRegularMatchImage(dataList);

//        List<SplatSalmonRunDrawDTO> dataList = getSplatSalmonRunDrawDTOList();
//        generateSalmonRunImage(dataList);

//        List<SplatXSchedulesDrawDTO> dataList = getSplatXSchedulesDrawDTOList();
//        generateXMatchImage(dataList);
    }

    /**
     * 生成真格赛程图
     *
     * @param dataList 数据
     * @return void
     */
    public static File generateBankaraMatchImage(List<SplatBankaraSchedulesDrawDTO> dataList) throws IOException, FontFormatException {
        int width = 500;
        int height = 70 + (dataList.size() * 325);
        int centerPoint = width / 2;

        // 创建一个 BufferedImage 对象，宽度为 width，高度为 height，使用 RGB 颜色模式
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取 Graphics2D 对象，用于绘制图形
        Graphics2D g2d = image.createGraphics();
        //消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置背景颜色为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        ClassLoader classLoader = ImageGenerator.class.getClassLoader();
        String imagesPath = classLoader.getResource("static/images/").getPath();
        String fontPath = classLoader.getResource("static/font/").getPath();
        File originalFile = new File(imagesPath + "splatbg3.jpg");
        BufferedImage bgImage = ImageIO.read(originalFile);
        int scaleHeight = bgImage.getHeight() / (bgImage.getWidth() / 500);
        TexturePaint texturePaint = new TexturePaint(bgImage, new Rectangle(0, 0, 500, scaleHeight));
        Graphics2D graphics9 = image.createGraphics();
        graphics9.setPaint(texturePaint);
        graphics9.fillRect(0, 0, width, height);
        graphics9.dispose();
        // 设置字体样式和大小
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "DFP_GBZY7 .ttf"));
        Font customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "BlitzBold.otf"));
        Font mainFont = customFont.deriveFont(Font.PLAIN, 24);
        Font timeFont = customFont2.deriveFont(Font.BOLD, 20);
        Font matchModelFont = customFont.deriveFont(Font.BOLD, 22);
        Font matchTypeFont = customFont.deriveFont(Font.BOLD, 16);
        Font mapNameFont = customFont.deriveFont(Font.PLAIN, 12);
        g2d.setFont(mainFont);
        // 设置文字颜色为黑色
        g2d.setColor(Color.WHITE);

        // 在图片上绘制文字
        String text = "蛮颓比赛";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int x = (width - textWidth) / 2;
        int y = 40;
        File bankaraImageFile = new File(imagesPath + "bankara.png");
        BufferedImage bankaraImage = ImageIO.read(bankaraImageFile);
        g2d.drawImage(bankaraImage, 160, 12, null);
        g2d.drawString(text, x, y);

        // 填充消息
        int dataX = 10;
        int dataY = 80;
        for (SplatBankaraSchedulesDrawDTO data : dataList) {
            // 描绘时间
            g2d.setFont(timeFont);
            // 设置文字颜色为黑色
            g2d.setColor(Color.WHITE);
            int timeTextWidth = g2d.getFontMetrics().stringWidth(data.getTimeBetween());
            g2d.drawString(data.getTimeBetween(), centerPoint - (timeTextWidth / 2), dataY);
            dataY = dataY + 40;
            // 描绘字
            // 真格模式
            setModeIcon(imagesPath, data.getChallengeMatchRule(), g2d, dataX, dataY - 24);
            g2d.setFont(matchModelFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString(data.getChallengeMatchRule(), dataX + 34, dataY);
            // 【开放】样式
            int openMatchRuleTextWidth = g2d.getFontMetrics().stringWidth(data.getChallengeMatchRule());
            g2d.setFont(matchTypeFont);
            g2d.setColor(new Color(96, 59, 255));
            g2d.fillRect(dataX + openMatchRuleTextWidth + 38, dataY - 24, 46, 34);
            g2d.setColor(Color.WHITE);
            g2d.drawString("挑战", dataX + openMatchRuleTextWidth + 42, dataY);

            // 真格模式
            setModeIcon(imagesPath, data.getOpenMatchRule(), g2d, centerPoint, dataY - 24);
            g2d.setColor(Color.WHITE);
            g2d.setFont(matchModelFont);
            g2d.drawString(data.getOpenMatchRule(), width / 2 + 34, dataY);
            // 【挑战】样式
            int challengeMatchRuleTextWidth = g2d.getFontMetrics().stringWidth(data.getOpenMatchRule());
            g2d.setFont(matchTypeFont);
            g2d.setColor(new Color(237, 236, 61));
            g2d.fillRect(centerPoint + openMatchRuleTextWidth + 38, dataY - 24, 46, 34);
            g2d.setColor(Color.BLACK);
            g2d.drawString("开放", centerPoint + challengeMatchRuleTextWidth + 42, dataY);
            dataY = dataY + 16;
            // 描绘图
            List<SplatBankaraSchedulesDrawVsDetailDTO> openMatchVsDetailList = data.getOpenMatchVsDetailList();
            List<SplatBankaraSchedulesDrawVsDetailDTO> challengeMatchVsDetailList = data.getChallengeMatchVsDetailList();

            int imageDataY = dataY;

            for (SplatBankaraSchedulesDrawVsDetailDTO challengeMatchVsDetail : challengeMatchVsDetailList) {
                File imageFile = new File(uploadFilePath + challengeMatchVsDetail.getImageUrl());
                BufferedImage vsDataImage = ImageIO.read(imageFile);
                BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(vsDataImage, 235, 110);
                g2d.drawImage(vsDataImageUrlScale, dataX, imageDataY, null);
                g2d.setColor(Color.BLACK);
                g2d.setFont(mapNameFont);
                int nameTextWidth = g2d.getFontMetrics().stringWidth(challengeMatchVsDetail.getName());
                g2d.fillRect(dataX, imageDataY + 75, nameTextWidth + 20, 30);
                g2d.setColor(Color.WHITE);
                g2d.drawString(challengeMatchVsDetail.getName(), dataX + 10, imageDataY + 95);
                imageDataY = imageDataY + 110;
            }
            imageDataY = dataY;

            for (SplatBankaraSchedulesDrawVsDetailDTO openMatchVsDetail : openMatchVsDetailList) {
                File imageFile = new File(uploadFilePath + openMatchVsDetail.getImageUrl());
                BufferedImage vsDataImage = ImageIO.read(imageFile);
                BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(vsDataImage, 235, 110);
                g2d.drawImage(vsDataImageUrlScale, centerPoint, imageDataY, null);
                g2d.setColor(Color.BLACK);
                g2d.setFont(mapNameFont);
                int nameTextWidth = g2d.getFontMetrics().stringWidth(openMatchVsDetail.getName());
                g2d.fillRect(centerPoint, imageDataY + 75, nameTextWidth + 20, 30);
                g2d.setColor(Color.WHITE);
                g2d.drawString(openMatchVsDetail.getName(), centerPoint + 10, imageDataY + 95);
                imageDataY = imageDataY + 110;
            }
            dataY = imageDataY + 50;

        }

        // 释放资源
        g2d.dispose();

        // 保存生成的图片
        String tmpFilePath = uploadFilePath + "temp" + "/";
        File tmpFileDir = new File(tmpFilePath);
        if (!tmpFileDir.exists()) {
            tmpFileDir.mkdirs();
        }
        String fileName = "bankara" + System.currentTimeMillis() + ".png";
        File output = new File(tmpFilePath + fileName);
        ImageIO.write(image, "png", output);
        System.out.println("图片已生成！");
        return output;
    }

    public static void setModeIcon(String imagesPath, String matchModel, Graphics2D g2d, int x, int y) throws IOException {
        if ("蛤蜊".equals(matchModel)) {
            drawIcon(imagesPath + "asari.png", g2d, x, y);
        }
        if ("塔楼".equals(matchModel)) {
            drawIcon(imagesPath + "yagura.png", g2d, x, y);
        }
        if ("鱼虎".equals(matchModel)) {
            drawIcon(imagesPath + "hoko.png", g2d, x, y);
        }
        if ("区域".equals(matchModel)) {
            drawIcon(imagesPath + "area.png", g2d, x, y);
        }
        if ("祭典".equals(matchModel)) {
            drawIcon(imagesPath + "6_84_.png", g2d, x, y);
        }
    }

    public static void drawIcon(String filePath, Graphics2D g2d, int x, int y) throws IOException {
        File bankaraImageFile = new File(filePath);
        BufferedImage bankaraImage = ImageIO.read(bankaraImageFile);
        BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(bankaraImage, 30, 30);
        g2d.drawImage(vsDataImageUrlScale, x, y, null);
    }

    public static List<SplatBankaraSchedulesDrawDTO> getSplatBankaraSchedulesDrawDTOList() {
        List<SplatBankaraSchedulesDrawDTO> drawDTOList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            SplatBankaraSchedulesDrawDTO drawDTO1 = new SplatBankaraSchedulesDrawDTO();
            drawDTO1.setTimeBetween("10:00 ~ 12:00");
            drawDTO1.setOpenMatchRule("蛤蜊");
            drawDTO1.setChallengeMatchRule("塔楼");
            List<SplatBankaraSchedulesDrawVsDetailDTO> openMatchVsDetailList = new ArrayList<>();
            SplatBankaraSchedulesDrawVsDetailDTO openVsDetailDTO1 = new SplatBankaraSchedulesDrawVsDetailDTO();
            openVsDetailDTO1.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            openVsDetailDTO1.setName("贝见亭");
            SplatBankaraSchedulesDrawVsDetailDTO openVsDetailDTO2 = new SplatBankaraSchedulesDrawVsDetailDTO();
            openVsDetailDTO2.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            openVsDetailDTO2.setName("旗鱼机场");
            openMatchVsDetailList.add(openVsDetailDTO1);
            openMatchVsDetailList.add(openVsDetailDTO2);
            drawDTO1.setOpenMatchVsDetailList(openMatchVsDetailList);
            List<SplatBankaraSchedulesDrawVsDetailDTO> challengeMatchVsDetailList = new ArrayList<>();
            SplatBankaraSchedulesDrawVsDetailDTO challengeVsDetailDTO1 = new SplatBankaraSchedulesDrawVsDetailDTO();
            challengeVsDetailDTO1.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            challengeVsDetailDTO1.setName("比目鱼住宅区");
            SplatBankaraSchedulesDrawVsDetailDTO challengeVsDetailDTO2 = new SplatBankaraSchedulesDrawVsDetailDTO();
            challengeVsDetailDTO2.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            challengeVsDetailDTO2.setName("醋饭海洋世界");
            challengeMatchVsDetailList.add(challengeVsDetailDTO1);
            challengeMatchVsDetailList.add(challengeVsDetailDTO2);
            drawDTO1.setChallengeMatchVsDetailList(challengeMatchVsDetailList);
            drawDTOList.add(drawDTO1);
        }
        return drawDTOList;
    }

    /**
     * 生成涂地赛程图
     *
     * @param dataList 数据
     * @return void
     */
    public static File generateRegularMatchImage(List<SplatRegularSchedulesDrawDTO> dataList) throws IOException, FontFormatException {
        int width = 500;
        int height = 70 + (dataList.size() * 190);
        int centerPoint = width / 2;

        // 创建一个 BufferedImage 对象，宽度为 width，高度为 height，使用 RGB 颜色模式
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取 Graphics2D 对象，用于绘制图形
        Graphics2D g2d = image.createGraphics();
        //消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置背景颜色为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        ClassLoader classLoader = ImageGenerator.class.getClassLoader();
        String imagesPath = classLoader.getResource("static/images/").getPath();
        String fontPath = classLoader.getResource("static/font/").getPath();
        File originalFile = new File(imagesPath + "splatbg6.jpg");
        BufferedImage bgImage = ImageIO.read(originalFile);
        int scaleHeight = bgImage.getHeight() / (bgImage.getWidth() / 500);
        TexturePaint texturePaint = new TexturePaint(bgImage, new Rectangle(0, 0, 500, scaleHeight));
        Graphics2D graphics9 = image.createGraphics();
        graphics9.setPaint(texturePaint);
        graphics9.fillRect(0, 0, width, height);
        graphics9.dispose();
        // 设置字体样式和大小
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "DFP_GBZY7 .ttf"));
        Font customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "BlitzBold.otf"));
        Font mainFont = customFont.deriveFont(Font.PLAIN, 24);
        Font timeFont = customFont2.deriveFont(Font.BOLD, 20);
        Font mapNameFont = customFont.deriveFont(Font.PLAIN, 12);
        Color fontColor = new Color(46, 46, 46);
        g2d.setFont(mainFont);
        // 设置文字颜色为黑色
        g2d.setColor(fontColor);

        // 在图片上绘制文字
        String text = "一般比赛";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int x = (width - textWidth) / 2;
        int y = 40;
        File bankaraImageFile = new File(imagesPath + "regular1.png");
        BufferedImage bankaraImage = ImageIO.read(bankaraImageFile);
        g2d.drawImage(bankaraImage, 160, 12, null);
        g2d.drawString(text, x, y);

        // 填充消息
        int dataX = 10;
        int dataY = 80;
        for (SplatRegularSchedulesDrawDTO data : dataList) {
            File itemBgFile = new File(imagesPath + "item-bg1.png");
            BufferedImage itemBgImage = ImageIO.read(itemBgFile);
            g2d.drawImage(itemBgImage, 5, dataY - 24, null);

            // 描绘时间
            g2d.setFont(timeFont);
            // 设置文字颜色为黑色
            g2d.setColor(Color.WHITE);
            int timeTextWidth = g2d.getFontMetrics().stringWidth(data.getTimeBetween());
            g2d.drawString(data.getTimeBetween(), centerPoint - (timeTextWidth / 2), dataY + 5);
            dataY = dataY + 20;

            // 描绘图
            List<SplatRegularSchedulesDrawVsDetailDTO> matchVsDetailList = data.getMatchVsDetailList();
            int tempDataX = dataX;
            for (SplatRegularSchedulesDrawVsDetailDTO matchVsDetail : matchVsDetailList) {
                File imageFile = new File(uploadFilePath + matchVsDetail.getImageUrl());
                BufferedImage vsDataImage = ImageIO.read(imageFile);
                BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(vsDataImage, 235, 120);
                g2d.drawImage(vsDataImageUrlScale, tempDataX, dataY, null);
                g2d.setColor(Color.BLACK);
                g2d.setFont(mapNameFont);
                int nameTextWidth = g2d.getFontMetrics().stringWidth(matchVsDetail.getName());
                g2d.fillRect(tempDataX, dataY + 85, nameTextWidth + 20, 30);
                g2d.setColor(Color.WHITE);
                g2d.drawString(matchVsDetail.getName(), tempDataX + 10, dataY + 105);
                tempDataX = centerPoint;
            }
            dataY = dataY + 170;

        }

        // 释放资源
        g2d.dispose();

        // 保存生成的图片
        String tmpFilePath = uploadFilePath + "temp" + "/";
        File tmpFileDir = new File(tmpFilePath);
        if (!tmpFileDir.exists()) {
            tmpFileDir.mkdirs();
        }
        String fileName = "regular" + System.currentTimeMillis() + ".png";
        File output = new File(tmpFilePath + fileName);
        ImageIO.write(image, "png", output);
        System.out.println("图片已生成！");
        return output;
    }


    public static List<SplatRegularSchedulesDrawDTO> getSplatRegularSchedulesDrawDTOList() {
        List<SplatRegularSchedulesDrawDTO> drawDTOList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            SplatRegularSchedulesDrawDTO drawDTO1 = new SplatRegularSchedulesDrawDTO();
            drawDTO1.setTimeBetween("10:00 - 12:00");
            List<SplatRegularSchedulesDrawVsDetailDTO> matchVsDetailList = new ArrayList<>();
            SplatRegularSchedulesDrawVsDetailDTO vsDetailDTO = new SplatRegularSchedulesDrawVsDetailDTO();
            vsDetailDTO.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            vsDetailDTO.setName("贝见亭");
            SplatRegularSchedulesDrawVsDetailDTO vsDetailDTO2 = new SplatRegularSchedulesDrawVsDetailDTO();
            vsDetailDTO2.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            vsDetailDTO2.setName("旗鱼机场");
            matchVsDetailList.add(vsDetailDTO);
            matchVsDetailList.add(vsDetailDTO2);
            drawDTO1.setMatchVsDetailList(matchVsDetailList);
            drawDTOList.add(drawDTO1);
        }
        return drawDTOList;
    }

    /**
     * 生成打工日程图
     *
     * @param dataList 数据
     * @return File
     */
    public static File generateSalmonRunImage(List<SplatSalmonRunDrawDTO> dataList) throws IOException, FontFormatException {
        int width = 554;
        int height = 60 + (dataList.size() * 240);
        int centerPoint = width / 2;

        // 创建一个 BufferedImage 对象，宽度为 width，高度为 height，使用 RGB 颜色模式
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取 Graphics2D 对象，用于绘制图形
        Graphics2D g2d = image.createGraphics();
        //消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置背景颜色为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        ClassLoader classLoader = ImageGenerator.class.getClassLoader();
        String imagesPath = classLoader.getResource("static/images/").getPath();
        String fontPath = classLoader.getResource("static/font/").getPath();
        File originalFile = new File(imagesPath + "salmon_run/splatbg4.jpg");
        BufferedImage bgImage = ImageIO.read(originalFile);
        int scaleHeight = bgImage.getHeight() / (bgImage.getWidth() / width);
        TexturePaint texturePaint = new TexturePaint(bgImage, new Rectangle(0, 0, width, scaleHeight));
        Graphics2D graphics9 = image.createGraphics();
        graphics9.setPaint(texturePaint);
        graphics9.fillRect(0, 0, width, height);
        graphics9.dispose();
        // 设置字体样式和大小
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "DFP_GBZY7 .ttf"));
        Font customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "FOT-RowdyStd-EB.otf"));
        Font mainFont = customFont.deriveFont(Font.PLAIN, 24);
        Font timeFont = customFont2.deriveFont(Font.BOLD, 20);
        Font mapNameFont = customFont.deriveFont(Font.PLAIN, 14);
        Color fontColor = new Color(46, 46, 46);
        Color fontColor2 = new Color(211, 211, 211);
        g2d.setFont(mainFont);
        // 设置文字颜色为黑色
        g2d.setColor(fontColor);

        // 在图片上绘制文字
        String text = "鲑鱼跑";
        int x = 64;
        int y = 40;
        File salmonRunImageFile = new File(imagesPath + "salmon_run/salmon.png");
        BufferedImage salmonRunImage = ImageIO.read(salmonRunImageFile);
        g2d.drawImage(salmonRunImage, 20, 12, null);
        g2d.drawString(text, x, y);

        // 填充消息
        int dataX = 10;
        int dataY = 60;
        for (SplatSalmonRunDrawDTO data : dataList) {

            // 背景
            File itemBgFile = new File(imagesPath + "salmon_run/salmon_run_item_bg.png");
            BufferedImage itemBgImage = ImageIO.read(itemBgFile);
            g2d.drawImage(itemBgImage, 5, dataY, null);


            dataY = dataY + 10;
            // BOSS
            String bossImgPath = getBossImagePath(data.getBossName());
            File bossImageFile = new File(imagesPath + bossImgPath);
            BufferedImage bossImage = ImageIO.read(bossImageFile);
            BufferedImage bossImageScale = PosterUtil.scaleImage(bossImage, 40, 40);
            g2d.drawImage(bossImageScale, 20, dataY, null);

            dataY = dataY + 30;
            // 描绘时间
            g2d.setFont(timeFont);
            // 设置文字颜色为黑色
            g2d.setColor(fontColor2);
            int timeTextWidth = g2d.getFontMetrics().stringWidth(data.getTimeBetween());
            g2d.drawString(data.getTimeBetween(), centerPoint - (timeTextWidth / 2), dataY);
            dataY = dataY + 30;

            // 地图
            File stageImgFile = new File(uploadFilePath + data.getStageImage());
            BufferedImage stageImage = ImageIO.read(stageImgFile);
            BufferedImage stageImageScale = PosterUtil.scaleImage(stageImage, 235, 120);
            g2d.drawImage(stageImageScale, 20, dataY + 20, null);

            g2d.setColor(new Color(235, 79, 3));
            g2d.setFont(mapNameFont);
            int nameTextWidth = g2d.getFontMetrics().stringWidth(data.getStageName());
            g2d.fillRect(20, dataY + 105, nameTextWidth + 20, 30);
            g2d.setColor(Color.WHITE);
            g2d.drawString(data.getStageName(), 30, dataY + 125);

            // 描绘图
            List<SplatSalmonRunDrawWeaponDTO> weaponDTOList = data.getWeaponDTOList();
            int tempDataX = 265;
            for (SplatSalmonRunDrawWeaponDTO weaponDTO : weaponDTOList) {
                File imageFile = new File(uploadFilePath + weaponDTO.getImageUrl());
                BufferedImage vsDataImage = ImageIO.read(imageFile);
                BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(vsDataImage, 60, 60, BufferedImage.TYPE_4BYTE_ABGR);
                g2d.drawImage(vsDataImageUrlScale, tempDataX, dataY + 60, null);
                tempDataX = tempDataX + 70;
            }
            dataY = dataY + 170;

        }

        // 释放资源
        g2d.dispose();

        // 保存生成的图片
        String tmpFilePath = uploadFilePath + "temp" + "/";
        File tmpFileDir = new File(tmpFilePath);
        if (!tmpFileDir.exists()) {
            tmpFileDir.mkdirs();
        }
        String fileName = "salmonrun" + System.currentTimeMillis() + ".png";
        File output = new File(tmpFilePath + fileName);
        ImageIO.write(image, "png", output);
        System.out.println("图片已生成！");
        return output;
    }

    /**
     * 获取boss图片
     *
     * @param bossName boss名称
     * @return String
     */
    private static String getBossImagePath(String bossName) {
        if ("Cohozuna".equals(bossName)) {
            return "salmon_run/king-cohozuna.png";
        } else if ("Horrorboros".equals(bossName)) {
            return "salmon_run/king-horrorboros.png";
        } else if ("Megalodontia".equals(bossName)) {
            return "salmon_run/king-megalodontia.png";
        } else if ("Triumvirate".equals(bossName)) {
            return "salmon_run/king-triumvirate.png";
        } else {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "BOSS名称不正确，请联系管理员！");
        }

    }


    public static List<SplatSalmonRunDrawDTO> getSplatSalmonRunDrawDTOList() {
        List<SplatSalmonRunDrawDTO> drawDTOList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SplatSalmonRunDrawDTO salmonRunDrawDTO = new SplatSalmonRunDrawDTO();
            salmonRunDrawDTO.setTimeBetween("10/2 周三 16:00 - 10/4 8:00");
            salmonRunDrawDTO.setBossName("Cohozuna");
            salmonRunDrawDTO.setStageImage("upload/20241010/5f09625c9031652ca1edcf8028265b03ecc28475ab3d56910960a68430d7948a_1.png");
            salmonRunDrawDTO.setStageName("鲑坝");
            List<SplatSalmonRunDrawWeaponDTO> weaponDTOList = new ArrayList<>();
            SplatSalmonRunDrawWeaponDTO weaponDTO1 = new SplatSalmonRunDrawWeaponDTO();
            weaponDTO1.setImageUrl("upload/20241010/273a31632f184752a59dcfbecc54e256/34fe0401b6f6a0b09839696fc820ece9570a9d56e3a746b65f0604dec91a9920_0.png");
            weaponDTO1.setName("斯普拉射击枪");
            SplatSalmonRunDrawWeaponDTO weaponDTO2 = new SplatSalmonRunDrawWeaponDTO();
            weaponDTO2.setImageUrl("upload/20241010/273a31632f184752a59dcfbecc54e256/34fe0401b6f6a0b09839696fc820ece9570a9d56e3a746b65f0604dec91a9920_0.png");
            weaponDTO2.setName("斯普拉射击枪");
            SplatSalmonRunDrawWeaponDTO weaponDTO3 = new SplatSalmonRunDrawWeaponDTO();
            weaponDTO3.setImageUrl("upload/20241010/273a31632f184752a59dcfbecc54e256/34fe0401b6f6a0b09839696fc820ece9570a9d56e3a746b65f0604dec91a9920_0.png");
            weaponDTO3.setName("斯普拉射击枪");
            SplatSalmonRunDrawWeaponDTO weaponDTO4 = new SplatSalmonRunDrawWeaponDTO();
            weaponDTO4.setImageUrl("upload/20241010/273a31632f184752a59dcfbecc54e256/34fe0401b6f6a0b09839696fc820ece9570a9d56e3a746b65f0604dec91a9920_0.png");
            weaponDTO4.setName("斯普拉射击枪");
            weaponDTOList.add(weaponDTO1);
            weaponDTOList.add(weaponDTO2);
            weaponDTOList.add(weaponDTO3);
            weaponDTOList.add(weaponDTO4);
            salmonRunDrawDTO.setWeaponDTOList(weaponDTOList);
            drawDTOList.add(salmonRunDrawDTO);
        }
        return drawDTOList;
    }


    public static File generateXMatchImage(List<SplatXSchedulesDrawDTO> dataList) throws IOException, FontFormatException {
        int width = 500;
        int height = 70 + (dataList.size() * 200);
        int centerPoint = width / 2;

        // 创建一个 BufferedImage 对象，宽度为 width，高度为 height，使用 RGB 颜色模式
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取 Graphics2D 对象，用于绘制图形
        Graphics2D g2d = image.createGraphics();
        //消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置背景颜色为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        ClassLoader classLoader = ImageGenerator.class.getClassLoader();
        String imagesPath = classLoader.getResource("static/images/").getPath();
        String fontPath = classLoader.getResource("static/font/").getPath();
        File originalFile = new File(imagesPath + "splatbg7.jpg");
        BufferedImage bgImage = ImageIO.read(originalFile);
        int scaleHeight = bgImage.getHeight() / (bgImage.getWidth() / 500);
        TexturePaint texturePaint = new TexturePaint(bgImage, new Rectangle(0, 0, 500, scaleHeight));
        Graphics2D graphics9 = image.createGraphics();
        graphics9.setPaint(texturePaint);
        graphics9.fillRect(0, 0, width, height);
        graphics9.dispose();
        // 设置字体样式和大小
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "DFP_GBZY7 .ttf"));
        Font customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "BlitzBold.otf"));
        Font mainFont = customFont.deriveFont(Font.PLAIN, 24);
        Font matchModelFont = customFont.deriveFont(Font.BOLD, 22);
        Font timeFont = customFont2.deriveFont(Font.BOLD, 20);
        Font mapNameFont = customFont.deriveFont(Font.PLAIN, 12);
        Color fontColor = new Color(46, 46, 46);
        g2d.setFont(mainFont);
        // 设置文字颜色为黑色
        g2d.setColor(fontColor);

        // 在图片上绘制文字
        String text = "X比赛";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int x = (width - textWidth) / 2;
        int y = 40;
        File bankaraImageFile = new File(imagesPath + "x.png");
        BufferedImage bankaraImage = ImageIO.read(bankaraImageFile);
        g2d.drawImage(bankaraImage, 160, 12, null);
        g2d.drawString(text, x, y);

        // 填充消息
        int dataX = 10;
        int dataY = 80;
        for (SplatXSchedulesDrawDTO data : dataList) {
            File itemBgFile = new File(imagesPath + "item-bg1.png");
            BufferedImage itemBgImage = ImageIO.read(itemBgFile);
            g2d.drawImage(itemBgImage, 5, dataY - 24, null);

            // 描绘时间
            g2d.setFont(timeFont);
            // 设置文字颜色为黑色
            g2d.setColor(Color.WHITE);
            int timeTextWidth = g2d.getFontMetrics().stringWidth(data.getTimeBetween());
            g2d.drawString(data.getTimeBetween(), centerPoint - (timeTextWidth / 2), dataY + 5);

            setModeIcon(imagesPath, data.getMatchRule(), g2d, 10, dataY - 20);
            g2d.setFont(matchModelFont);
            g2d.drawString(data.getMatchRule(), 50, dataY + 5);

            dataY = dataY + 20;

            // 描绘图
            List<SplatXSchedulesDrawVsDetailDTO> matchVsDetailList = data.getMatchVsDetailList();
            int tempDataX = dataX;
            for (SplatXSchedulesDrawVsDetailDTO matchVsDetail : matchVsDetailList) {
                File imageFile = new File(uploadFilePath + matchVsDetail.getImageUrl());
                BufferedImage vsDataImage = ImageIO.read(imageFile);
                BufferedImage vsDataImageUrlScale = PosterUtil.scaleImage(vsDataImage, 235, 120);
                g2d.drawImage(vsDataImageUrlScale, tempDataX, dataY, null);
                g2d.setColor(Color.BLACK);
                g2d.setFont(mapNameFont);
                int nameTextWidth = g2d.getFontMetrics().stringWidth(matchVsDetail.getName());
                g2d.fillRect(tempDataX, dataY + 85, nameTextWidth + 20, 30);
                g2d.setColor(Color.WHITE);
                g2d.drawString(matchVsDetail.getName(), tempDataX + 10, dataY + 105);
                tempDataX = centerPoint;
            }
            dataY = dataY + 180;

        }

        // 释放资源
        g2d.dispose();

        // 保存生成的图片
        String tmpFilePath = uploadFilePath + "temp" + "/";
        File tmpFileDir = new File(tmpFilePath);
        if (!tmpFileDir.exists()) {
            tmpFileDir.mkdirs();
        }
        String fileName = "x" + System.currentTimeMillis() + ".png";
        File output = new File(tmpFilePath + fileName);
        ImageIO.write(image, "png", output);
        System.out.println("图片已生成！");
        return output;
    }


    public static List<SplatXSchedulesDrawDTO> getSplatXSchedulesDrawDTOList() {
        List<SplatXSchedulesDrawDTO> drawDTOList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            SplatXSchedulesDrawDTO drawDTO1 = new SplatXSchedulesDrawDTO();
            drawDTO1.setTimeBetween("10:00 - 12:00");
            drawDTO1.setMatchRule("鱼虎");
            List<SplatXSchedulesDrawVsDetailDTO> matchVsDetailList = new ArrayList<>();
            SplatXSchedulesDrawVsDetailDTO vsDetailDTO = new SplatXSchedulesDrawVsDetailDTO();
            vsDetailDTO.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            vsDetailDTO.setName("贝见亭");
            SplatXSchedulesDrawVsDetailDTO vsDetailDTO2 = new SplatXSchedulesDrawVsDetailDTO();
            vsDetailDTO2.setImageUrl("upload/20241011/083745761d9e46948fb39958682230cf/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png");
            vsDetailDTO2.setName("旗鱼机场");
            matchVsDetailList.add(vsDetailDTO);
            matchVsDetailList.add(vsDetailDTO2);
            drawDTO1.setMatchVsDetailList(matchVsDetailList);
            drawDTOList.add(drawDTO1);
        }
        return drawDTOList;
    }

}
