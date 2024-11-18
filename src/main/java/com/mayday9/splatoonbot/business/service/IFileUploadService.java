package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.vo.UploadFileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IFileUploadService {

    List<UploadFileVO> uploadFile(MultipartFile[] multipartFiles, String pathKey) throws Exception;

    UploadFileVO uploadFile(MultipartFile multipartFiles, String pathKey) throws Exception;

    UploadFileVO uploadFile(String urlStr, String pathKey) throws Exception;

    public UploadFileVO uploadSplatoonFile(String urlStr) throws Exception;

    void downloadFile(String filePath, HttpServletResponse response);

//    void getImage(String filePath, HttpServletResponse response, HttpServletRequest request) throws Exception;

    List<UploadFileVO> uploadFile(MultipartFile[] files, String segmentation, Double height, Double widthStart, Double widthEnd) throws Exception;

    void compressFile(String compressPath, String picPath, Double compressWidth);

    void cutPic(String saveCutFilePath, String compressPath, Double widthStart, Double heightStart);
}
