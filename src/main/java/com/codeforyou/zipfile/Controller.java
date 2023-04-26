package com.codeforyou.zipfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;

@RestController
public class Controller {

    @GetMapping("/zip")
    public List<Map<String, Object>> zipFiles(HttpServletResponse response)
            throws IOException {

        List<String> filenames = new ArrayList<String>();

        List<Map<String,Object>> listofmap = new ArrayList<>();

        Map<String,Object> maps = new HashMap<>();
        maps.put("one", "two.txt");
        maps.put("two","two.txt");
        listofmap.add(maps);

        for (Map map : listofmap) {
            filenames.addAll(map.values());
        }
        byte[] buf = new byte[2048];

        // Create the ZIP file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);

        // Compress the files
        for (int i = 0; i < filenames.size(); i++) {

            try (FileInputStream fileInputStream = new FileInputStream(filenames.get(i).toString())) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                // Add ZIP entry to output stream.
                File file = new File(filenames.get(i).toString());
                String entryname = file.getName();
                zipOutputStream.putNextEntry(zipParameters);

                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                    zipOutputStream.write(buf, 0, bytesRead);
                }

                zipOutputStream.closeEntry();
                bufferedInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        zipOutputStream.flush();
        byteArrayOutputStream.flush();
        zipOutputStream.close();
        byteArrayOutputStream.close();
        ServletOutputStream servletOutputStream = response.getOutputStream();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"MyZip.ZIP\"");
        servletOutputStream.write(byteArrayOutputStream.toByteArray());
        return null;
    }
    
}
