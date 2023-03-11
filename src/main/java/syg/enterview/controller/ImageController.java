package syg.enterview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import syg.enterview.domain.*;
import syg.enterview.file.FileStore;


import java.io.*;
import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final EnterviewRepository enterviewRepository;
    private final FileStore fileStore;
    private final ImageService imageService;

    @ResponseBody
    @PostMapping("/image")
    public String logicStart(@RequestBody User user) throws IOException {

        Long userId = user.getUserId();

        EnterviewProcess enterviewProcess = imageService.logicStart();
        user.setEnterviewProcess(enterviewProcess);

        user.setCount(0L);
        enterviewRepository.userSave(user);

        return "Success";
    }

    @ResponseBody
    @PostMapping("image/new")
    public EmotionData saveImage(@ModelAttribute ImageForm form) throws IOException {
        log.info("info log");
        UploadImage attachImage = fileStore.storeImage(form.getImageFile());
//        log.info("path: {}", System.getProperty("user.home"));
        // 데이터베이스 저장
        // 이미지 저장
        Image image = new Image();
        image.setUserId(form.getUserId());
        image.setImageName(form.getImageName());
        image.setImageFile(attachImage);
        enterviewRepository.imageSave(image);


        // 이미지 처리
        User user = enterviewRepository.findUserById(form.getUserId());
        log.info("user: {}", user);
        log.info("image: {}", image);
        EmotionData emotionData;

        emotionData = imageService.imageUpdate(user, image.getImageFile().getStoreImageName());
        return emotionData;
//        return image.getImageFile().getStoreImageName();
    }

    @ResponseBody
    @PostMapping("image/end")
    public UserEmotionResult endLogic(@RequestBody User user) throws IOException, InterruptedException {
        User userClose = enterviewRepository.findUserById(user.getUserId());
        UserEmotionResult userEmotionResult = imageService.makeResult(userClose);
        return userEmotionResult;
    }

    @ResponseBody
    @GetMapping("/imageview/{imageName}")
    public Resource getImage(@PathVariable String imageName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(imageName));
    }

    @GetMapping("/image/{imageName}/no")
    public ResponseEntity<Resource> downloadImage(@PathVariable String imageName) throws MalformedURLException {
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(imageName));
        String contentDisposition = "attachment; filename=\"" + imageName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }



}
