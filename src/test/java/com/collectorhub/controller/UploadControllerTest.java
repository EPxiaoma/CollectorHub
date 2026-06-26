package com.collectorhub.controller;

import com.collectorhub.utils.SystemConstants;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UploadControllerTest {

    @Test
    void uploadIconStoresAvatarUnderIconsDirectory() throws Exception {
        Path iconsDir = Paths.get(SystemConstants.IMAGE_UPLOAD_DIR, "icons");
        Set<Path> before = listFiles(iconsDir);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new UploadController()).build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        try {
            MvcResult result = mockMvc.perform(multipart("/upload/icons").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(matchesPattern("/icons/[0-9a-f-]+\\.png")))
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            String uploaded = response.replaceAll(".*\"data\":\"([^\"]+)\".*", "$1");
            Path uploadedPath = Paths.get(SystemConstants.IMAGE_UPLOAD_DIR, uploaded.substring(1));
            org.junit.jupiter.api.Assertions.assertTrue(Files.exists(uploadedPath));
        } finally {
            Set<Path> after = listFiles(iconsDir);
            after.removeAll(before);
            for (Path path : after) {
                Files.deleteIfExists(path);
            }
        }
    }

    private Set<Path> listFiles(Path dir) throws Exception {
        if (!Files.exists(dir)) {
            return new java.util.HashSet<>();
        }
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.collect(Collectors.toSet());
        }
    }
}
