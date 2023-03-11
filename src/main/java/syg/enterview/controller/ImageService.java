package syg.enterview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import syg.enterview.domain.*;

import java.io.*;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final EnterviewRepository enterviewRepository;
    @Value("${file.dir}")
    private String fileDir;


    public EnterviewProcess logicStart() throws IOException {
        String path = "C:/Users/pdpdk/enterview/EnterView_Logic";
        String [] command = {"C:/Users/pdpdk/anaconda3/envs/enterview111/python", "./real_time_image.py"};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(path));
        Process process = processBuilder.start();
        OutputStream stdin = process.getOutputStream(); // 서브 프로세스에 입력
        InputStream stdout = process.getInputStream(); // 서브 프로세스 출력
        BufferedWriter pythonInput = new BufferedWriter(new OutputStreamWriter(stdin));
        BufferedReader pythonOutput = new BufferedReader(new InputStreamReader(stdout));

        return new EnterviewProcess(processBuilder, process, stdin, stdout, pythonInput, pythonOutput);
    }

    public EmotionData imageUpdate(User user, String storeImageName) throws IOException {

        Long count = user.getCount();
        user.setCount(count+1);
        enterviewRepository.userSave(user);
        if(user.getCount() <= 5) {
            return new EmotionData();
        }

        EnterviewProcess process = user.getEnterviewProcess();
        BufferedWriter pythonInput = process.getPythonInput();
        BufferedReader pythonOutput = process.getPythonOutput();


        String resultLine;
        EmotionData emotionData;
        log.info("path: {}", fileDir+storeImageName +"\n");
        // 입력하기
        pythonInput.append(fileDir + storeImageName +"\n");
        pythonInput.flush();

        log.info("input complete");
        // 출력받기
        resultLine = pythonOutput.readLine();
        resultLine = resultLine.substring(21,resultLine.length()-2);
        resultLine = resultLine.replace("\'", "\"");
        log.info("output complete");

        // string을 json으로 변환
        // emotionData 만들기
        ObjectMapper mapper = new ObjectMapper();
        emotionData = mapper.readValue(resultLine, EmotionData.class);
        emotionData.setLocalTime(count);
        enterviewRepository.emotionDataSave(user.getUserId(), emotionData);

        return emotionData;
    }

    public UserEmotionResult makeResult(User userClose) throws IOException, InterruptedException {
        EnterviewProcess enterviewProcess = userClose.getEnterviewProcess();
        BufferedWriter pythonInput = enterviewProcess.getPythonInput();
        BufferedReader pythonOutput = enterviewProcess.getPythonOutput();
        Process process = enterviewProcess.getProcess();

        pythonInput.append("exit");
        pythonInput.flush();

        pythonInput.close();
        pythonOutput.close();
        int exitCode = process.waitFor();
        log.info("userId: {}, exitCode: {}", userClose.getUserId(), exitCode);

        UserEmotionResult userEmotionResult = makeUserEmotionResult(userClose);
        return userEmotionResult;
    }

    private UserEmotionResult makeUserEmotionResult(User userClose) {
        UserEmotionResult userEmotionResult = new UserEmotionResult();
        // userId를 이용해서 EmotionData 조회
        ArrayList<EmotionData> emotionDataList = enterviewRepository.findAllEmotionDataById(userClose.getUserId());
        // highscore 계산
        double max=0.0;
        for(EmotionData emotionData : emotionDataList) {
            if (max < emotionData.getScore()) {
                max = emotionData.getScore();
            }
        }
        userEmotionResult.setHighScore(Double.toString(max));
        // lowscore 계산
        double min=100.0;
        for(EmotionData emotionData : emotionDataList) {
            if (min > emotionData.getScore()) {
                min = emotionData.getScore();
            }
        }
        userEmotionResult.setLowestScore(Double.toString(min));
        // avrScore 계산
        double sum = 0;
        int idx = 0;
        for(EmotionData emotionData : emotionDataList) {
            sum += emotionData.getScore();
            idx++;
        }
        if(idx!=0) {
            sum /= idx;
        }
        else {
            sum = 0;
        }
        userEmotionResult.setAvrScore(Double.toString(sum));
        if(sum > 0.91) userEmotionResult.setComment("잘했어요");
        else userEmotionResult.setComment("못했어요");
        log.info("sum:{}",sum);

        int good = 0;
        int bad = 0;
        int total = 0;
        ArrayList<String> avrEmotion = new ArrayList<>();
        for(EmotionData emotionData : emotionDataList) {
            log.info("good:{}", emotionData.getScore());
            avrEmotion.add(Double.toString(emotionData.getScore()*100));
            if(emotionData.getScore() > 0.92) good++;
            else bad++;
            total++;
        }
        userEmotionResult.setTotalTIme(Integer.toString(total));
        userEmotionResult.setGoodTime(Integer.toString(good));
        userEmotionResult.setBadTime(Integer.toString(bad));

        EmotionData2 avrEmotionData = new EmotionData2();
        double avrAngry=0;
        double avrDisgust=0;
        double avrScared=0;
        double avrHappy=0;
        double avrSad=0;
        double avrSurprised=0;
        double avrNeutral=0;
        for(EmotionData emotionData : emotionDataList) {
            avrAngry += emotionData.getAngry();
            avrDisgust += emotionData.getDisgust();
            avrScared += emotionData.getScared();
            avrHappy += emotionData.getHappy();
            avrSad += emotionData.getSad();
            avrSurprised += emotionData.getSurprised();
            avrNeutral += emotionData.getNeutral();
        }
        avrEmotionData.setAngry(avrAngry/total);
        avrEmotionData.setDisgust(avrDisgust/total);
        avrEmotionData.setScared(avrScared/total);
        avrEmotionData.setHappy(avrHappy/total);
        avrEmotionData.setSad(avrSad/total);
        avrEmotionData.setSurprised(avrSurprised/total);
        avrEmotionData.setNeutral(avrNeutral/total);
        userEmotionResult.setAvrEmotion(avrEmotionData);
        
        return userEmotionResult;
    }
}
