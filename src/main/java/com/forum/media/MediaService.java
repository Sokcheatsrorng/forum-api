package com.forum.media;


import com.forum.media.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {

    MediaResponse uploadSingle(MultipartFile multipartFile, String folderName);

    List<MediaResponse> uploadMultiple(List<MultipartFile> multipartFile, String folderName);

}
