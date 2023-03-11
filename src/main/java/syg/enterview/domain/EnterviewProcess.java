package syg.enterview.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
public class EnterviewProcess {
    private ProcessBuilder processBuilder;
    private Process process;
    private OutputStream stdin; // 서브 프로세스에 입력
    private InputStream stdout;  // 서브 프로세스 출력
    private BufferedWriter pythonInput;
    private BufferedReader pythonOutput;

    public EnterviewProcess(ProcessBuilder processBuilder, Process process, OutputStream stdin, InputStream stdout, BufferedWriter pythonInput, BufferedReader pythonOutput) {
        this.processBuilder = processBuilder;
        this.process = process;
        this.stdin = stdin;
        this.stdout = stdout;
        this.pythonInput = pythonInput;
        this.pythonOutput = pythonOutput;
    }
}
