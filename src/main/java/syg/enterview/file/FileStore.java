package syg.enterview.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import syg.enterview.domain.UploadImage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String imageName) { return fileDir + imageName; }

    public UploadImage storeImage(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String originalImageName = multipartFile.getOriginalFilename();
        String storeImageName = createStoreImageName(originalImageName);
        multipartFile.transferTo(new File(getFullPath(storeImageName)));
        return new UploadImage(originalImageName, storeImageName);
    }

    private String createStoreImageName(String originalImageName) {
        String ext = extractExt(originalImageName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalImageName) {
        int pos = originalImageName.lastIndexOf(".");
        return originalImageName.substring(pos + 1);
    }
}
