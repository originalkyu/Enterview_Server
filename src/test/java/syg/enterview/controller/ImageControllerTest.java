package syg.enterview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import syg.enterview.domain.EmotionData;

import java.io.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
class ImageControllerTest {
    /**


     * InputStream input = process.getInputStream(); // 자식 프로세스가 System.out에 출력하는 내용
     * OutputStream output = process.getOutputStream(); // 자식 프로세스에 입력값 전달
     */

    /**
     */


    @Test
    public void ProcessBuilderTest() throws Exception {
//        String [] command = {"ping", "google.com"};
        String path = "/Users/chankyu/seasonalkyu/Repository/enterview/";
        String [] command = {"python", "seasonalkyu/hello1.py", path};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(System.getProperty("user.home")));

        try {
            Process process = processBuilder.start();
            // exec()

            OutputStream stdin = process.getOutputStream(); // 서브 프로세스에 입력
            InputStream stdout = process.getInputStream(); // 서브 프로세스 출력

//            Scanner scan = new Scanner(System.in);
            BufferedReader pythonOutput = new BufferedReader(new InputStreamReader(stdout));
            BufferedWriter pythonInput = new BufferedWriter(new OutputStreamWriter(stdin));

            String line;
            line = pythonOutput.readLine();
            System.out.println(line);

            pythonInput.append("3\n");
            pythonInput.append("3\n");
            pythonInput.close();

            line = pythonOutput.readLine();
            System.out.println(line);
            pythonOutput.close();



            // 시작할 땐 줄 수있음
            // 내가 input() 을 줄 수 있을까?  => ok
            // 이미지 파일 크기? 17KB 딜레이가 거의 없음
            int exitCode = process.waitFor();

            System.out.println ("\nExited with error code : " + exitCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ProcessBuilderTest0() throws Exception {
        System.out.println(System.getProperty("user.home"));
        String [] command = {"ls"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(System.getProperty("user.home")));
        Process process = processBuilder.start();
    }
    @Test
    public void ProcessBuilderTest2() throws Exception {
        // C:\Users\pdpdk\anaconda3\envs\enterview111/python ./real_time_image.py ../imgs_exp/ex17.jpg
//        String [] command = {"ping", "google.com"};
        String path = "C:\\Users\\pdpdk\\enterview\\EnterView_Logic";
        String [] command = {"C:\\Users\\pdpdk\\anaconda3\\envs\\enterview111\\python", "./real_time_image.py"};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(path));

        try {
            Process process = processBuilder.start();
            // exec()

            OutputStream stdin = process.getOutputStream(); // 서브 프로세스에 입력
            InputStream stdout = process.getInputStream(); // 서브 프로세스 출력

//            Scanner scan = new Scanner(System.in);

            String line[] = {
                    "../imgs_exp/ex17.jpg\n",
                    "../imgs_exp/ex17.jpg\n",
                    "../imgs_exp/ex17.jpg\n",
                    "../imgs_exp/ex17.jpg\n",
                    "../imgs_exp/ex17.jpg\n",
            };
            String result = new String();
            BufferedWriter pythonInput = new BufferedWriter(new OutputStreamWriter(stdin));
            BufferedReader pythonOutput = new BufferedReader(new InputStreamReader(stdout));

//            pythonInput.append(line[0]);
//            pythonInput.flush();
//            result = pythonOutput.readLine();
//            System.out.println(result);
//            pythonInput.append(line[0]);
//            pythonInput.flush();
//            result = pythonOutput.readLine();
//            System.out.println(result);

            for(int idx=0; idx<2; idx++) {
                pythonInput.append(line[idx]);
                pythonInput.flush();
                result = pythonOutput.readLine();
                result = result.substring(21,result.length()-2);
                System.out.println(result);


                JSONObject jsonObject = new JSONObject(result);
                double angry = jsonObject.getDouble("angry");
                double disgust = jsonObject.getDouble("disgust");
                double scared = jsonObject.getDouble("scared");
                double happy = jsonObject.getDouble("happy");
                double sad = jsonObject.getDouble("sad");
                double surprised = jsonObject.getDouble("surprised");
                double neutral = jsonObject.getDouble("neutral");
                double score = jsonObject.getDouble("score");

                System.out.println(score);
            }
            pythonInput.append("exit");
            pythonInput.flush();

            pythonInput.close();
            pythonOutput.close();


            int exitCode = process.waitFor();

            System.out.println ("\nExited with error code : " + exitCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void updateImg(String img, OutputStream stdin) throws IOException {
        BufferedWriter pythonInput = new BufferedWriter(new OutputStreamWriter(stdin));
        pythonInput.append(img);
        pythonInput.close();
    }

    private String getResult(String result, InputStream stdout) throws IOException {
        BufferedReader pythonOutput = new BufferedReader(new InputStreamReader(stdout));
        result = pythonOutput.readLine();
        System.out.println(result);
        pythonOutput.close();

        return result;
    }


    @Test
    public void ProcessBuilderTest5() throws Exception {
        Logger log = (Logger)LoggerFactory.getLogger(ImageControllerTest.class);
        String commandPath = "C:/Users/pdpdk/enterview/EnterView_Logic";
        String imagePath = "../imgs_exp/2ffc841d-295b-4d1f-887b-abebb684d5e8.jpg\n";
        String [] command = {"C:/Users/pdpdk/anaconda3/envs/enterview111/python", "./real_time_image.py"};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(commandPath));
//        processBuilder.directory(new File(System.getProperty("user.home")));

        Process process = processBuilder.start();
        // exec()

        OutputStream stdin = process.getOutputStream(); // 서브 프로세스에 입력
        InputStream stdout = process.getInputStream(); // 서브 프로세스 출력

        BufferedWriter pythonInput = new BufferedWriter(new OutputStreamWriter(stdin));
        BufferedReader pythonOutput = new BufferedReader(new InputStreamReader(stdout));

        String resultLine;
        EmotionData emotionData;

        // 입력
        pythonInput.append(imagePath);
        pythonInput.flush();

        log.info("image path: {}", imagePath);

        // 출력

        resultLine = pythonOutput.readLine();
        log.info("resultLine: {}", resultLine);
        System.out.println(resultLine);
//        resultLine = resultLine.substring(21,resultLine.length()-2);
//        resultLine = resultLine.replace("\'", "\"");
//        System.out.println(resultLine);

        // 파싱
//        ObjectMapper mapper = new ObjectMapper();
//        emotionData = mapper.readValue(resultLine, EmotionData.class);

        pythonInput.append("exit");
        pythonInput.flush();

        pythonInput.close();
        pythonOutput.close();


        int exitCode = process.waitFor();

        System.out.println ("\nExited with error code : " + exitCode);

    }

}