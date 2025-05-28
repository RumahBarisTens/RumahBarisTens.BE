package propensi.tens.bms.features.shift_management.shift.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import propensi.tens.bms.features.account_management.repositories.OutletDb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;

import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.repositories.ShiftScheduleRepository;

@RestController
@RequestMapping("/api/shift")
public class ShiftDashboardController {

    @Autowired
    private OutletDb outletRepository;
    
    @Autowired
    private BaristaDb baristaRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Get total outlets
            long totalOutlets = outletRepository.count();
            response.put("totalOutlets", totalOutlets);
            
            // Get total employees (baristas)
            long totalEmployees = baristaRepository.count();
            response.put("totalEmployees", totalEmployees);
            
            // Get active shifts
            long activeShifts = shiftScheduleRepository.countActiveShifts();
            response.put("activeShifts", activeShifts);
            
            // Get shift types (usually 2: Opening and Closing)
            response.put("shiftTypes", 2);
            
            // Get staff distribution by outlet
            List<Map<String, Object>> staffByOutlet = outletRepository.findAll().stream()
                .map(outlet -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("outlet", outlet.getName());
                    item.put("count", baristaRepository.countByOutletId(outlet.getOutletId()));
                    return item;
                })
                .collect(Collectors.toList());
            response.put("staffByOutlet", staffByOutlet);
            
            // Get shift type distribution
            List<Map<String, Object>> shiftTypeDistribution = new ArrayList<>();
            Map<String, Object> opening = new HashMap<>();
            opening.put("type", "Opening");
            opening.put("count", shiftScheduleRepository.countByShiftType(1));
            
            Map<String, Object> closing = new HashMap<>();
            closing.put("type", "Closing");
            closing.put("count", shiftScheduleRepository.countByShiftType(2));
            
            shiftTypeDistribution.add(opening);
            shiftTypeDistribution.add(closing);
            response.put("shiftTypeDistribution", shiftTypeDistribution);
            
            // Get active shifts by outlet
            List<Map<String, Object>> activeShiftsByOutlet = outletRepository.findAll().stream()
                .map(outlet -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("outlet", outlet.getName());
                    item.put("count", shiftScheduleRepository.countActiveShiftsByOutletId(outlet.getOutletId()));
                    return item;
                })
                .collect(Collectors.toList());
            response.put("activeShiftsByOutlet", activeShiftsByOutlet);
            
            // Get outlet details
            List<Map<String, Object>> outletDetails = outletRepository.findAll().stream()
                .map(outlet -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", outlet.getName());
                    item.put("staffCount", baristaRepository.countByOutletId(outlet.getOutletId()));
                    item.put("activeShifts", shiftScheduleRepository.countActiveShiftsByOutletId(outlet.getOutletId()));
                    return item;
                })
                .collect(Collectors.toList());
            response.put("outletDetails", outletDetails);
            
            // Get ALL schedule data instead of just today and tomorrow
            List<Map<String, Object>> scheduleData = new ArrayList<>();
            
            // Get all shifts from the repository
            List<ShiftSchedule> allShifts = shiftScheduleRepository.findAll();
            
            // Group shifts by date
            Map<LocalDate, List<ShiftSchedule>> shiftsByDate = allShifts.stream()
                .collect(Collectors.groupingBy(ShiftSchedule::getDateShift));
            
            // Convert each date's shifts to the required format
            for (Map.Entry<LocalDate, List<ShiftSchedule>> entry : shiftsByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<ShiftSchedule> shiftsForDate = entry.getValue();
                
                Map<String, Object> dateData = new HashMap<>();
                dateData.put("date", date.toString());
                dateData.put("shifts", getShiftsForDateList(shiftsForDate));
                scheduleData.add(dateData);
            }
            
            // Sort scheduleData by date (ascending)
            scheduleData.sort((a, b) -> {
                String dateA = (String) a.get("date");
                String dateB = (String) b.get("date");
                return dateA.compareTo(dateB);
            });
            
            response.put("scheduleData", scheduleData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch dashboard data: " + e.getMessage()));
        }
    }
    
    // Modified method to accept a list of ShiftSchedule objects instead of a date
    private List<Map<String, Object>> getShiftsForDateList(List<ShiftSchedule> shifts) {
        return shifts.stream().flatMap(shift -> {
            return shift.getListBarista().stream().map(barista -> {
                Map<String, Object> shiftData = new HashMap<>();
                Outlet outlet = outletRepository.findById(shift.getOutletId()).orElse(null);
                
                shiftData.put("outlet", outlet != null ? outlet.getName() : "Unknown");
                shiftData.put("employeeName", barista.getFullName());
                shiftData.put("employeeInitial", barista.getFullName().substring(0, 1));
                
                String shiftType = shift.getShiftType() == 1 ? "Opening" : "Closing";
                String shiftTime = shift.getShiftType() == 1 ? "08:00-17:00" : "15:00-00:00";
                
                shiftData.put("shiftType", shiftType);
                shiftData.put("shiftTime", shiftTime);
                
                // Determine status based on date and current time
                LocalDate today = LocalDate.now();
                LocalDate shiftDate = shift.getDateShift();
                
                if (shiftDate.isEqual(today)) {
                    shiftData.put("status", "active");
                } else if (shiftDate.isAfter(today)) {
                    shiftData.put("status", "upcoming");
                } else {
                    shiftData.put("status", "completed");
                }
                
                return shiftData;
            });
        }).collect(Collectors.toList());
    }
}