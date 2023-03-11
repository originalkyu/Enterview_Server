package syg.enterview.domain;

import lombok.Data;

@Data
public class Image {
    private Long id;
    private Long userId;
    private String imageName;
    private UploadImage imageFile;
}
