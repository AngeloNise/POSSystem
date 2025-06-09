package com.code.share.codesharing.tools;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;

public class Systemm {
    public String testPost(){
        return "test only";
    }

    public void FileViewerFunction(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String UploadPath,
                                   String getFileName,
                                   FileType type
                                   ) throws IOException {

        ServletContext context = request.getSession().getServletContext();

        String fullPath = UploadPath + getFileName;
        File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);

        String mineType = context.getMimeType(fullPath);
        if(mineType == null){
            switch (type) {
                case Image:
                    mineType = "image/**";
                    break;
                case PDF:
                    mineType = "application/pdf";
                    break;
            }

        }
        response.addHeader("Content-Disposition","inline; filename="+ downloadFile.getName());

        switch (type) {
            case Image:
                response.setContentType("image/**");
                break;
            case PDF:
                response.setContentType("application/pdf");
                break;
        }

        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();

    }

    public enum FileType{
        Image, PDF
    }

    public Object[] FileSaving(String UploadPath, MultipartFile file, String nameFolderPath){

        String subFolderPath = nameFolderPath + "/" + LocalDate.now().toString() + "/";
        String fileName = file.getOriginalFilename();
        String filePath = subFolderPath + fileName;

        File dr = new File(UploadPath + subFolderPath);
        if(!dr.exists()){
            dr.mkdirs();
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(new File(UploadPath + subFolderPath, fileName))){
            InputStream in = file.getInputStream();

            int ch = 0;
            while ((ch = in.read()) != -1){
                fileOutputStream.write(ch);
            }
            fileOutputStream.flush();

        }catch (Exception error){
            //log.info("Error: " + error);
        }

        Object[] returnParams = new Object[1];
        returnParams[0] = filePath;

        return returnParams;
    }

}
