package com.tsybulko.dto.response;

import com.tsybulko.dto.IDTO;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/23/2016 14:07
 */
public class ResponseDTO implements IDTO {

    private boolean success;
    private String answer;

    public ResponseDTO(boolean success, String answer) {
        this.success = success;
        this.answer = answer;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
