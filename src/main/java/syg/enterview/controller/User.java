package syg.enterview.controller;

import lombok.Data;
import syg.enterview.domain.EnterviewProcess;

@Data
public class User {
    private Long userId;
    private EnterviewProcess enterviewProcess;
    private Long count;
}
