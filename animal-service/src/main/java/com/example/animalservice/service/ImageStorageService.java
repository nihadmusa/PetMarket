package com.example.animalservice.service;

import com.example.animalservice.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Map<String, String> EXT_TO_CONTENT_TYPE = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp"
    );
    private static final Map<String, byte[]> EXT_MAGIC = Map.of(
            "jpg", new byte[] {(byte) 0xFF, (byte) 0xD8},
            "jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8},
            "png", new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47},
            "webp", new byte[] {0x52, 0x49, 0x46, 0x46}
    );

    @Value("${app.upload-dir}")
    private String uploadDir;

    public List<String> saveAll(List<MultipartFile> files) {
        return files.stream().map(this::save).toList();
    }

    public String save(MultipartFile file) {
        validateFile(file);
        String ext = extractExtension(file.getOriginalFilename());
        String name = UUID.randomUUID() + "." + ext;
        Path target = resolve(name);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(file.getInputStream(), target);
            return name;
        } catch (IOException e) {
            throw new InvalidImageException("Sekil saxlanila bilmedi");
        }
    }

    public Resource load(String name) {
        try {
            Path path = resolve(name);
            if (!Files.exists(path)) {
                throw new InvalidImageException("Sekil tapilmadi");
            }
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new InvalidImageException("Sekil tapilmadi");
        }
    }

    public void delete(String name) {
        try {
            Files.deleteIfExists(resolve(name));
        } catch (IOException ignored) {
        }
    }

    public String contentType(String name) {
        String ext = extractExtension(name);
        return EXT_TO_CONTENT_TYPE.getOrDefault(ext, "application/octet-stream");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Sekil fayli bos ola bilmez");
        }
        String ext = extractExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new InvalidImageException("Yalniz jpg, png, webp sekiller yuklenile biler");
        }
        verifyMagic(file, ext);
    }

    private void verifyMagic(MultipartFile file, String ext) {
        try {
            byte[] bytes = file.getBytes();
            byte[] magic = EXT_MAGIC.get(ext);
            if (magic == null || bytes.length < magic.length) {
                throw new InvalidImageException("Sekil fayli pozulmusdur");
            }
            for (int i = 0; i < magic.length; i++) {
                if (bytes[i] != magic[i]) {
                    throw new InvalidImageException("Sekil fayli pozulmusdur");
                }
            }
            if ("webp".equals(ext)) {
                if (bytes.length < 12
                        || bytes[8] != 'W' || bytes[9] != 'E'
                        || bytes[10] != 'B' || bytes[11] != 'P') {
                    throw new InvalidImageException("Sekil fayli pozulmusdur");
                }
            }
        } catch (IOException e) {
            throw new InvalidImageException("Sekil fayli oxunamadi");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private Path resolve(String name) {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        return base.resolve(name).normalize();
    }
}