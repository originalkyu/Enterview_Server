package syg.enterview.domain;

import lombok.Data;

@Data
public class UploadImage {
    private String uploadImageName; // 전달받은 파일 명
    private String storeImageName; // 서버 내부 파일명

    public UploadImage(String uploadImageName, String storeImageName) {
        this.uploadImageName = uploadImageName;
        this.storeImageName = storeImageName;
    }
}
