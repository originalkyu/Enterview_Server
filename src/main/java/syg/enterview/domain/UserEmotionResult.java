package syg.enterview.domain;

import lombok.Data;

@Data
public class UserEmotionResult {
    private String totalTIme;
    private EmotionData2 avrEmotion; // 찬규 로직
    private String highScore; // 찬규 룆ㄱ
    private String lowestScore; // 찬규 로직
    private String goodTime; // 찬규 로직
    private String badTime; // 찬규 로직
    private String avrScore; // 찬규 로직
    private String comment;
}

