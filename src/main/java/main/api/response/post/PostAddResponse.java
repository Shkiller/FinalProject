package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddResponse {
    private boolean result;
    private Map<String, String> errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}