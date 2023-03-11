package syg.enterview.domain;

import lombok.Data;

import java.time.LocalTime;

@Data
public class EmotionData {
    double angry;
    double disgust;
    double scared;
    double happy;
    double sad;
    double surprised;
    double neutral;
    double score;
    Long localTime;
}
