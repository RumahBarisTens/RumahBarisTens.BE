package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.HashMap;
import java.util.Map;

public class OptionMapDTO {
    private String id;
    private String text;
    
    public OptionMapDTO(String id, String text) {
        this.id = id;
        this.text = text;
    }
    
    public String getId() { return id; }
    public String getText() { return text; }
    
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("text", text);
        return map;
    }
}