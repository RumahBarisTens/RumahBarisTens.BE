package propensi.tens.bms.features.trainee_management.enums;

public enum AssessmentTemplate {
    BARISTA("Barista Assessment"),
    HEADBAR("Headbar Assessment"),
    PROBATIONBARISTA("Probation Barista Assessment"),
    INTERNBARISTA("Intern Barista Assessment");

    private final String displayName;

    AssessmentTemplate(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}