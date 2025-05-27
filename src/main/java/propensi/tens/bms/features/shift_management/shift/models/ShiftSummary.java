package propensi.tens.bms.features.shift_management.shift.models;

import lombok.Getter;
import lombok.Setter;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ShiftSummary {
    private long targetDays = 25;  // Default target: 25 days
    private long completedDays;    // Completed work days
    private long overtimeHours;    // Total overtime hours
    private long remainingDays;    // Remaining work days
    private String progress;       // Progress percentage
    private Map<LocalDate, ShiftDetail> calendarData; // Calendar data with shift details

    // Constructor with overtime logs
    public ShiftSummary(long completedDays, List<OvertimeLogResponse> overtimeLogs, Map<LocalDate, ShiftDetail> calendarData) {
        this.completedDays = completedDays;
        this.overtimeHours = calculateOvertimeHours(overtimeLogs);
        this.remainingDays = targetDays - completedDays;
        this.progress = completedDays + " dari " + targetDays + " hari (" + (completedDays * 100 / targetDays) + "%)";
        this.calendarData = calendarData;
    }
    
    // Original constructor - keep for backward compatibility
    public ShiftSummary(long completedDays, long overtimeHours, Map<LocalDate, ShiftDetail> calendarData) {
        this.completedDays = completedDays;
        this.overtimeHours = overtimeHours;
        this.remainingDays = targetDays - completedDays;
        this.progress = completedDays + " dari " + targetDays + " hari (" + (completedDays * 100 / targetDays) + "%)";
        this.calendarData = calendarData;
    }

    // Default constructor
    public ShiftSummary() {
        this.remainingDays = targetDays;
        this.progress = "0 dari " + targetDays + " hari (0%)";
    }
    
    // Helper method to calculate total overtime hours from overtime logs
    private long calculateOvertimeHours(List<OvertimeLogResponse> overtimeLogs) {
        if (overtimeLogs == null || overtimeLogs.isEmpty()) {
            return 0;
        }
        
        long totalMinutes = 0;
        for (OvertimeLogResponse log : overtimeLogs) {
            // Assuming OvertimeLogResponse has a getDuration() method that returns LocalTime
            LocalTime duration = log.getDuration();
            totalMinutes += duration.getHour() * 60 + duration.getMinute();
        }
        
        // Convert total minutes to hours
        return totalMinutes / 60;
    }
}