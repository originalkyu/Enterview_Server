package syg.enterview.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageForm {
//    private Long imageId;
    private Long userId;
    private String imageName;
    private MultipartFile imageFile;
}

