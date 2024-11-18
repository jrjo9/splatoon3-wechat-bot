package com.mayday9.splatoonbot.business.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.mayday9.splatoonbot.business.service.IFileUploadService;
import com.mayday9.splatoonbot.business.vo.UploadFileVO;
import com.mayday9.splatoonbot.common.util.OkHttpUtil;
import com.mayday9.splatoonbot.common.util.core.Func;
import com.mayday9.splatoonbot.common.util.core.StopWatch;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Lianjiannan
 * @since 2024/9/27 15:55
 **/
@Service("commonFileUploadServiceImpl")
@Slf4j
public class FileUploadServiceImpl implements IFileUploadService {

    @Value("${file.uploadFolder:D://wxBot/}")
    private String saveFilePath;

    @Value("${file.uploadCompressFolder:#{null}}")
    private String saveCompressFilePath;

    @Value("${file.uploadCutFolder:#{null}}")
    private String saveCutFilePath;

    private static final String UPLOAD_FOLDER = "upload";

    private static DateTimeFormatter ymdDtf = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public List<UploadFileVO> uploadFile(MultipartFile[] multipartFiles, String pathKey) throws Exception {

        if (Func.isBlank(pathKey)) {
            pathKey = UPLOAD_FOLDER;
        }

        List<UploadFileVO> uploadFileVOList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            valid(multipartFile);

            uploadFileVOList.add(actionUploadFile(multipartFile, pathKey, null, null, null));

        }
        return uploadFileVOList;

    }

    @Override
    public UploadFileVO uploadFile(MultipartFile multipartFile, String pathKey) throws Exception {
        if (Func.isBlank(pathKey)) {
            pathKey = UPLOAD_FOLDER;
        }

        valid(multipartFile);

        return actionUploadFile(multipartFile, pathKey, null, null, null);

    }

    public UploadFileVO uploadFile(String urlStr, String pathKey) throws Exception {
        String todayFormat = ymdDtf.format(LocalDateTime.now());
        String path = pathKey + "/" + todayFormat + "/" + UUID.randomUUID().toString().replace("-", "") + "/";
        File fileDir = new File(saveFilePath + path);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        File file = HttpUtil.downloadFileFromUrl(urlStr, saveFilePath + path + fileName);

        UploadFileVO uploadFileVO = new UploadFileVO();
        uploadFileVO.setFileName(fileName);
        uploadFileVO.setFileSize(file.length());
        uploadFileVO.setFileTitle(fileName);
        uploadFileVO.setFileType(MediaType.IMAGE_PNG_VALUE);
        uploadFileVO.setFilePath(path + fileName);
        BufferedImage bufferedImage = ImageIO.read(file); // 通过MultipartFile得到InputStream，从而得到BufferedImage
        if (null == bufferedImage) {
            // 证明上传的文件不是图片，获取图片流失败，不进行下面的操作
            uploadFileVO.setWidth(0);
            uploadFileVO.setHeight(0);
        } else {
            uploadFileVO.setWidth(bufferedImage.getWidth());
            uploadFileVO.setHeight(bufferedImage.getHeight());
        }
        uploadFileVO.setSaveFilePath(saveFilePath);
//        uploadFileVO.setParams(ParamsUtils.addParam("compressWidth", compressWidth).addParam("wi dthStart", widthStart).addParam("heightStart", heightStart).param());
        uploadFileVO.setSaveCutFilePath(saveCutFilePath);
        uploadFileVO.setSaveCompressFilePath(saveCompressFilePath);
        return uploadFileVO;
    }


    public UploadFileVO uploadSplatoonFile(String urlStr) throws Exception {
        String todayFormat = ymdDtf.format(LocalDateTime.now());
        String path = "upload/" + todayFormat + "/" + UUID.randomUUID().toString().replace("-", "") + "/";
        File fileDir = new File(saveFilePath + path);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        Response response = OkHttpUtil.builder()
            .url(urlStr)
            .addHeader(":authority", "splatoon3.ink")
            .addHeader(":method", "GET")
            .addHeader(":path", urlStr.replace("https://splatoon3.ink", ""))
            .addHeader(":scheme", "https")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;g=0.9,image/avif,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;v=b3;q=0.7")
            .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
            .addHeader("Accept-Language", "zh-CN,zh;g=0.9")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Pragma", "no-cache")
            .addHeader("Priority", "u=0,i")
            .addHeader("Sec-Ch-Ua", "\"Chromium\":v=\"130\",\"Google Chrome\":v=\"130\",\"Not?A Brand\";v=\"99\"")
            .addHeader("Sec-Ch-Ua-Mobile", "?0")
            .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
            .addHeader("Sec-Fetch-Dest", "document")
            .addHeader("Sec-Fetch-Mode", "navigate")
            .addHeader("Sec-Fetch-Site", "none")
            .addHeader("Sec-Fetch-User", "?1")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
            .get()
            .syncWithResp();
        if (!response.isSuccessful()) {
            throw new HttpException("Server response error with status code: [{}]", response.code());
        }

        File file = new File(saveFilePath + path + fileName);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    throw new HttpException("下载文件失败！");
                }
            } catch (IOException e) {
                throw new HttpException("下载文件失败！");
            }
        }

        //读入写出
        long fileSize = Objects.requireNonNull(response.body()).contentLength();
        try (InputStream inputStream = response.body().byteStream();
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new HttpException("下载文件失败！");
        }


        UploadFileVO uploadFileVO = new UploadFileVO();
        uploadFileVO.setFileName(fileName);
        uploadFileVO.setFileSize(file.length());
        uploadFileVO.setFileTitle(fileName);
        uploadFileVO.setFileType(MediaType.IMAGE_PNG_VALUE);
        uploadFileVO.setFilePath(path + fileName);
        BufferedImage bufferedImage = ImageIO.read(file); // 通过MultipartFile得到InputStream，从而得到BufferedImage
        if (null == bufferedImage) {
            // 证明上传的文件不是图片，获取图片流失败，不进行下面的操作
            uploadFileVO.setWidth(0);
            uploadFileVO.setHeight(0);
        } else {
            uploadFileVO.setWidth(bufferedImage.getWidth());
            uploadFileVO.setHeight(bufferedImage.getHeight());
        }
        uploadFileVO.setSaveFilePath(saveFilePath);
        uploadFileVO.setSaveCutFilePath(saveCutFilePath);
        uploadFileVO.setSaveCompressFilePath(saveCompressFilePath);
        return uploadFileVO;
    }


    @Override
    public List<UploadFileVO> uploadFile(MultipartFile[] multipartFiles, String pathKey, Double height, Double widthStart, Double widthEnd) throws Exception {
        if (Func.isBlank(pathKey)) {
            pathKey = UPLOAD_FOLDER;
        }

        List<UploadFileVO> uploadFileVOList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            valid(multipartFile);

            StopWatch watch = new StopWatch();

            uploadFileVOList.add(actionUploadFile(multipartFile, pathKey, height, widthStart, widthEnd));

            System.out.println(multipartFile.getOriginalFilename() + "====图片上传总耗时=======>" + watch.elapsedTime() + "(ms)" + "<===========");
        }
        return uploadFileVOList;
    }

    private UploadFileVO actionUploadFile(MultipartFile multipartFile, String pathKey, Double compressWidth, Double widthStart, Double heightStart) throws Exception {

        String todayFormat = ymdDtf.format(LocalDateTime.now());

        String path = pathKey + "/" + todayFormat + "/" + UUID.randomUUID().toString().replace("-", "") + "/";
        File fileDir = new File(saveFilePath + path);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        String fileName = path + multipartFile.getOriginalFilename();
        File dest = new File(saveFilePath + fileName);

        UploadFileVO uploadFileVO = new UploadFileVO();
        uploadFileVO.setFileName(multipartFile.getOriginalFilename());
        uploadFileVO.setFileSize(multipartFile.getSize());
        uploadFileVO.setFileTitle(multipartFile.getName());
        uploadFileVO.setFileType(multipartFile.getContentType());
        uploadFileVO.setFilePath(fileName);
        imageProperty(multipartFile, uploadFileVO);
        uploadFileVO.setSaveFilePath(saveFilePath);
//        uploadFileVO.setParams(ParamsUtils.addParam("compressWidth", compressWidth).addParam("wi dthStart", widthStart).addParam("heightStart", heightStart).param());
        uploadFileVO.setSaveCutFilePath(saveCutFilePath);
        uploadFileVO.setSaveCompressFilePath(saveCompressFilePath);
        multipartFile.transferTo(dest);
        return uploadFileVO;
    }

    public File convert(MultipartFile file, File dest) {
        try {
            dest.createNewFile();
            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(file.getBytes());
            fos.close();
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void imageProperty(MultipartFile multipartFile, UploadFileVO uploadFileVO) throws Exception {
        try {
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream()); // 通过MultipartFile得到InputStream，从而得到BufferedImage
            if (null == bufferedImage) {
                // 证明上传的文件不是图片，获取图片流失败，不进行下面的操作
                uploadFileVO.setWidth(0);
                uploadFileVO.setHeight(0);
                return;
            }
            uploadFileVO.setWidth(bufferedImage.getWidth());
            uploadFileVO.setHeight(bufferedImage.getHeight());
        } catch (Exception e) {
            log.warn("上传图片获取流异常:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void compressFile(String compressPath, String picPath, Double compressWidth) {
        if (Func.isNotBlank(compressPath)) {
            try {
                File compressFile = new File(compressPath);
                if (!compressFile.getParentFile().exists()) {
                    compressFile.getParentFile().mkdirs();
                }
                File compressDest = new File(compressPath);
                BufferedImage image = ImageIO.read(new File(picPath));
                int width = image.getWidth();
                //图片压缩
                ImgUtil.scale(image, FileUtil.file(compressDest), (float) (compressWidth / width));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
            }
        }
    }

    @Override
    public void cutPic(String saveCutFilePath, String compressPath, Double widthStart, Double heightStart) {
        if (Func.isNotBlank(saveCutFilePath)) {
            try {
                File cutFileDir = new File(saveCutFilePath);
                if (!cutFileDir.getParentFile().exists()) {
                    cutFileDir.getParentFile().mkdirs();
                }
                File cutDest = new File(saveCutFilePath);

                BufferedImage image = ImageIO.read(FileUtil.file(compressPath));
                int height = image.getHeight();
                int width = image.getWidth();
                int startY = height / 100 * widthStart.intValue();
                int endY = height / 100 * (heightStart.intValue() - widthStart.intValue());
                //剪切
                ImgUtil.cut(image, FileUtil.file(cutDest), new Rectangle(0, startY, width, endY));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
            }
        }
    }

    @Override
    public void downloadFile(String path, HttpServletResponse response) {

        //创建输入流
        FileInputStream inputStream = null;
        BufferedInputStream buffInputStream = null;

        //创建输出流
        ServletOutputStream outputStream = null;
        BufferedOutputStream buffOutputStream = null;
        try {
            //获取要下载的文件
//            File file = new File(saveFilePath + "/" + path);
            File file = new File(path);

            //设置响应的内容类型为二进制流，即文件类型
            response.setContentType("application/octet-stream");

            //设置文件名
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

            //创建输入流
            inputStream = new FileInputStream(file);
            buffInputStream = new BufferedInputStream(inputStream);

            //创建输出流
            outputStream = response.getOutputStream();
            buffOutputStream = new BufferedOutputStream(outputStream);

            //循环读取数据并写入到响应输出流中
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = buffInputStream.read(buffer)) != -1) {
                buffOutputStream.write(buffer, 0, len);
            }

            //关闭流
            buffOutputStream.flush();
            buffOutputStream.close();
            outputStream.flush();
            outputStream.close();
            buffInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (buffOutputStream != null) {
                    buffOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void getImage(String filePath, HttpServletResponse response, HttpServletRequest request) throws Exception {
//        response.setContentType(ContentType.IMAGE_JPEG.toString());
//        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
//        // 最终的图片
//        BufferedImage finalImage = null;
//
//        InputStream cacheFileInputStream = null;
//        try {
//
//            BufferedImage image = null;
//            // 文件的类型 pdf 1，图片2
//            int pdfType = 1;
//            int imageType = 2;
//            // pdf 类型,将对应页码转为图片,默认 pdf全部在本地
//            image = readImageByStorageType(filePath);
//            // 得到的图片不为 null，进行水印处理返回图片
//            if (null != image) {
//                // 压缩
//                finalImage = image;
//                image.flush();
//                Thumbnails.of(finalImage).
//                    scale(1f).
//                    outputFormat("jpg")
//                    .toOutputStream(outputStream);
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            if (null != finalImage) {
//                finalImage.flush();
//            }
//            outputStream.flush();
//            outputStream.close();
//            if (cacheFileInputStream != null) {
//                cacheFileInputStream.close();
//            }
//        }
//    }


    /**
     * 根据图片的存储类型读取图片
     *
     * @param fileSrc 文件地址
     * @return 返回图片对象
     */
    private BufferedImage readImageByStorageType(String fileSrc) throws IOException {
        BufferedImage image = null;
        InputStream in = null;
        ByteArrayInputStream byteArrayInputStream = null;
        FileInputStream fileInputStream = null;
        int local = 1;
        int ftp = 2;
        int smb = 3;
        try {
            fileInputStream = new FileInputStream(fileSrc);
            in = new BufferedInputStream(fileInputStream);
            if (in != null) {
                image = ImageIO.read(in);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

        } finally {
            if (null != in) {
                in.close();
            }
            if (null != fileInputStream) {
                fileInputStream.close();
            }
        }
        return image;

    }

    private void valid(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "上传失败，请选择文件！");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "未找到原文件文件名！");
        }
        if (!originalFilename.contains(".")) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "原文件名未识别到后缀！");
        }
    }
}
