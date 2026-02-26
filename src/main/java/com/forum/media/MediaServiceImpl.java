package com.forum.media;

import com.forum.Utils.Utils;
import com.forum.media.dto.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MediaServiceImpl  implements  MediaService{


    @Value("${media.server-path}")
    private String mediaServerPath;

    @Value("${media.base-uri}")
    private String mediaBaseUri;


    @Override
    public MediaResponse uploadSingle(MultipartFile multipartFile, String folderName) {

        String newName = Utils.generateUuid() + Utils.extractExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        Path pathDirectory = Paths.get(mediaServerPath);

        if (!Files.exists(pathDirectory)) {
            try {
                Files.createDirectories(pathDirectory);
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Could not create directory", e
                );
            }
        }

        Path path = pathDirectory.resolve(newName);
        try {
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not copy file", e
            );
        }

        return MediaResponse.builder()
                .name(newName)
                .contentType(multipartFile.getContentType())
                .extension(Utils.extractExtension(multipartFile.getOriginalFilename()))
                .size(multipartFile.getSize())
                .uri(String.format("%s/%s/%s", mediaBaseUri, folderName, newName))
                .build();

//        return MediaResponse.builder()
//                .name(newName)
//                .contentType(multipartFile.getContentType())
//                .extension(Utils.extractExtension(multipartFile.getOriginalFilename()))
//                .size(multipartFile.getSize())
//                .uri(String.format("%s%s/%s", mediaBaseUri, folderName, newName))
//                .build();
    }

    @Override
    public List<MediaResponse> uploadMultiple(List<MultipartFile> files, String folderName) {
        List<MediaResponse> mediaResponses = new ArrayList<>();

        // Process each file
        files.forEach(file -> {
            // Upload the file and get its metadata
            MediaResponse mediaResponse = this.uploadSingle(file, folderName);

            // Add the media response to the list
            mediaResponses.add(mediaResponse);
        });

        return mediaResponses;
    }

}
