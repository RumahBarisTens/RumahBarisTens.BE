package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.List;
import java.util.Map;

import propensi.tens.bms.features.trainee_management.enums.QuestionType;

public class QuestionDTO {
    private String id;
    private String question;
    private QuestionType type;
    private Object options;

    public QuestionDTO(String id, String question, QuestionType type, List<Map<String, String>> options) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.options = options;
    }
    public String getId() { return id; }
    public String getQuestion() { return question; }
    public QuestionType getType() { return type; }
    public Object getOptions() { return options; }
}
