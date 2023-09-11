package com.example.zipstream.controller;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class FileController {

    final String DIR = "/home/shane/Downloads/csv";

    @GetMapping("/zip")
    public void zip(HttpServletResponse response) throws IOException {
        File dir = new File(DIR);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"files.zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, read);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    @GetMapping("excel")
    public void excel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=sample.xlsx");
        response.setHeader("Content-Transfer-Encoding", "binary");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            for (int i = 0; i < 100_000; i++) {
                sheet.createRow(i).createCell(0).setCellValue("hello world" + i);
                if (i % 1000 == 0) {
                    workbook.write(outputStream);
                    outputStream.flush();
                }
            }
            workbook.write(outputStream);
            outputStream.flush();
            workbook.close();
        }
    }

}
