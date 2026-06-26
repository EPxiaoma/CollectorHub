package com.collectorhub.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.collectorhub.dto.Result;
import com.collectorhub.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Image upload APIs for reviews and user avatars.
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @PostMapping("/reviews")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String fileName = createNewFileName(originalFilename, "reviews", true);
            image.transferTo(resolveImageFile(fileName));
            log.debug("Review image uploaded: {}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Review image upload failed", e);
        }
    }

    @PostMapping("/icons")
    public Result uploadIcon(@RequestParam("file") MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String fileName = createNewFileName(originalFilename, "icons", false);
            image.transferTo(resolveImageFile(fileName));
            log.debug("Avatar uploaded: {}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Avatar upload failed", e);
        }
    }

    @GetMapping("/reviews/delete")
    public Result deleteReviewImg(@RequestParam("name") String filename) {
        File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return Result.fail("Invalid file name");
        }
        FileUtil.del(file);
        return Result.ok();
    }

    private File resolveImageFile(String fileName) {
        return new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.removePrefix(fileName, "/"));
    }

    private String createNewFileName(String originalFilename, String folder, boolean useHashDir) {
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        String name = UUID.randomUUID().toString();
        if (!useHashDir) {
            File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return StrUtil.format("/{}/{}.{}", folder, name, suffix);
        }
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("{}/{}/{}", folder, d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return StrUtil.format("/{}/{}/{}/{}.{}", folder, d1, d2, name, suffix);
    }
}
