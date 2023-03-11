package syg.enterview.domain;

import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import syg.enterview.controller.User;

import java.util.*;

@Repository
public class EnterviewRepository {
    // LiknedListMap??
    private final Map<Long, Image> imageStore = new HashMap<>();
    private final Map<Long, User> userStore = new HashMap<>();
    private final Map<Long, ArrayList<EmotionData>> emotionDataStore = new HashMap<>();
    private long sequence = 0L;

    public User userSave(User user) {
        userStore.put(user.getUserId(), user);
        return user;
    }
    public Image imageSave(Image image) {
        image.setId(++sequence);
        imageStore.put(image.getId(), image);
        return image;
    }

    public EmotionData emotionDataSave(Long userId, EmotionData emotionData) {

        boolean isExist = emotionDataStore.containsKey(userId);
        if(isExist) {
            ArrayList<EmotionData> emoList = emotionDataStore.get(userId);
            emoList.add(emotionData);
            emotionDataStore.put(userId, emoList);
        }
        else {
            ArrayList<EmotionData> emoList = new ArrayList<>();
            emoList.add(emotionData);
            emotionDataStore.put(userId, emoList);
        }
        return emotionData;
    }

    public ArrayList emotionDataDelete(Long userId) {
        return emotionDataStore.remove(userId);
    }

    public Image findImageById(Long id) { return imageStore.get(id);}
    public User  findUserById(Long id) { return userStore.get(id);}

    public ArrayList<EmotionData> findAllEmotionDataById(Long id) {
        ArrayList<EmotionData> array = new ArrayList<>();

        array = emotionDataDelete(id);


        return array;
    }
}
