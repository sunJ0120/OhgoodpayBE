package com.ohgoodteam.ohgoodpay.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public class FileSystemMultipartFile implements MultipartFile {
    private final String name;                 // form field name (e.g., "file")
    private final String originalFilename;     // original file name
    private final String contentType;          // mime type
    private final byte[] content;

    public FileSystemMultipartFile(Path path) throws IOException {
        this.name = "file";
        this.originalFilename = path.getFileName().toString();
        String detected = Files.probeContentType(path);
        this.contentType = detected != null ? detected : "application/octet-stream";
        this.content = Files.readAllBytes(path);
    }

    @Override public String getName() { return name; }
    @Override public String getOriginalFilename() { return originalFilename; }
    @Override public String getContentType() { return contentType; }
    @Override public boolean isEmpty() { return content.length == 0; }
    @Override public long getSize() { return content.length; }
    @Override public byte[] getBytes() { return content; }
    @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }

    @Override
    public void transferTo(File dest) throws IOException {
        try (OutputStream os = new FileOutputStream(dest)) {
            os.write(content);
        }
    }
}