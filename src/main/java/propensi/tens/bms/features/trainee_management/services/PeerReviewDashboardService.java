// PeerReviewDashboardService.java
package propensi.tens.bms.features.trainee_management.services;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.trainee_management.dto.response.*;
import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;
import propensi.tens.bms.features.trainee_management.models.PeerReviewContent;
import propensi.tens.bms.features.trainee_management.models.PeerReviewSubmission;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewAssignmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewContentRepository;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewSubmissionRepository;

@Service
public class PeerReviewDashboardService {

    @Autowired
    private PeerReviewContentRepository contentRepository;
    
    @Autowired
    private PeerReviewSubmissionRepository submissionRepository;
    
    @Autowired
    private PeerReviewAssignmentRepository assignmentRepository;
    
    @Autowired
    private ProbationBaristaDb probationBaristaDb;
    
    @Autowired
    private OutletDb outletDb;
    
    // Mendapatkan ringkasan dashboard

    public DashboardSummaryDTO getDashboardSummary(String timeRange) {
        Date[] dateRange = getDateRangeFromTimeRange(timeRange);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        System.out.println("Filtering assignments with endDateFill between: " + startDate + " and " + endDate);
        
        // PERBAIKAN: Filter assignment berdasarkan endDateFill terlebih dahulu
        List<PeerReviewAssignment> filteredAssignments = assignmentRepository.findByEndDateFillBetween(startDate, endDate);
        
        // Kemudian ambil submission yang terkait dengan assignment yang sudah difilter
        List<PeerReviewSubmission> submissions = new ArrayList<>();
        for (PeerReviewAssignment assignment : filteredAssignments) {
            PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
            if (submission != null) {
                submissions.add(submission);
            }
        }
        
        System.out.println("Found " + filteredAssignments.size() + " assignments and " + submissions.size() + " submissions");
        
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        // Hitung total barista probation (tetap sama)
        List<ProbationBarista> probationBaristas = probationBaristaDb.findAll();
        summary.setTotalBaristas(probationBaristas.size());
        
        // Hitung rata-rata skor dari submission yang sudah difilter
        double averageScore = 0.0;
        if (!submissions.isEmpty()) {
            averageScore = submissions.stream()
                    .mapToDouble(this::calculateAverageScore)
                    .average()
                    .orElse(0.0);
        }
        summary.setAverageScore(averageScore);
        
        // Hitung tren skor (dibandingkan dengan periode sebelumnya)
        Date[] previousDateRange = getPreviousDateRange(startDate, endDate);
        List<PeerReviewAssignment> previousAssignments = assignmentRepository.findByEndDateFillBetween(
                previousDateRange[0], previousDateRange[1]);
        
        List<PeerReviewSubmission> previousSubmissions = new ArrayList<>();
        for (PeerReviewAssignment assignment : previousAssignments) {
            PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
            if (submission != null) {
                previousSubmissions.add(submission);
            }
        }
        
        double previousAverageScore = 0.0;
        if (!previousSubmissions.isEmpty()) {
            previousAverageScore = previousSubmissions.stream()
                    .mapToDouble(this::calculateAverageScore)
                    .average()
                    .orElse(0.0);
        }
        
        summary.setScoreTrend(averageScore - previousAverageScore);
        
        // Hitung tingkat kelulusan
        Map<String, List<PeerReviewSubmission>> submissionsByReviewee = groupSubmissionsByReviewee(submissions);
        
        int passCount = 0;
        for (Map.Entry<String, List<PeerReviewSubmission>> entry : submissionsByReviewee.entrySet()) {
            double baristaAvgScore = entry.getValue().stream()
                    .mapToDouble(this::calculateAverageScore)
                    .average()
                    .orElse(0.0);
            
            if (baristaAvgScore >= 3.5) {
                passCount++;
            }
        }
        
        double passRate = submissionsByReviewee.size() > 0 ? 
                (double) passCount / submissionsByReviewee.size() * 100 : 0;
        summary.setPassRate(passRate);
        
        // Hitung jumlah review yang selesai dan total dari assignment yang difilter
        int completedReviews = submissions.size();
        int totalReviews = filteredAssignments.size();         
        summary.setCompletedReviews(completedReviews);
        summary.setTotalReviews(totalReviews);
        
        return summary;
    }
    // public DashboardSummaryDTO getDashboardSummary(String timeRange) {
    //     Date[] dateRange = getDateRangeFromTimeRange(timeRange);
    //     Date startDate = dateRange[0];
    //     Date endDate = dateRange[1];
        
    //     DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
    //     // Hitung total barista probation
    //     List<ProbationBarista> probationBaristas = probationBaristaDb.findAll();
    //     summary.setTotalBaristas(probationBaristas.size());
        
    //     // Ambil semua submission dalam range waktu
    //     List<PeerReviewSubmission> submissions = submissionRepository.findByReviewedAtBetween(startDate, endDate);
        
    //     // Hitung rata-rata skor
    //     double averageScore = 0.0;
    //     if (!submissions.isEmpty()) {
    //         averageScore = submissions.stream()
    //                 .mapToDouble(submission -> calculateAverageScore((PeerReviewSubmission) submission))
    //                 .average()
    //                 .orElse(0.0);
    //     }
    //     summary.setAverageScore(averageScore);
        
    //     // Hitung tren skor (dibandingkan dengan periode sebelumnya)
    //     Date[] previousDateRange = getPreviousDateRange(startDate, endDate);
    //     List<PeerReviewSubmission> previousSubmissions = submissionRepository.findByReviewedAtBetween(
    //             previousDateRange[0], previousDateRange[1]);
        
    //     double previousAverageScore = 0.0;
    //     if (!previousSubmissions.isEmpty()) {
    //         previousAverageScore = previousSubmissions.stream()
    //                 .mapToDouble(submission -> calculateAverageScore((PeerReviewSubmission) submission))
    //                 .average()
    //                 .orElse(0.0);
    //     }
        
    //     summary.setScoreTrend(averageScore - previousAverageScore);
        
    //     // Hitung tingkat kelulusan
    //     Map<String, List<PeerReviewSubmission>> submissionsByReviewee = groupSubmissionsByReviewee(submissions);
        
    //     int passCount = 0;
    //     for (Map.Entry<String, List<PeerReviewSubmission>> entry : submissionsByReviewee.entrySet()) {
    //         double baristaAvgScore = entry.getValue().stream()
    //                 .mapToDouble(submission -> calculateAverageScore((PeerReviewSubmission) submission))
    //                 .average()
    //                 .orElse(0.0);
            
    //         if (baristaAvgScore >= 3.5) {
    //             passCount++;
    //         }
    //     }
        
    //     double passRate = submissionsByReviewee.size() > 0 ? 
    //             (double) passCount / submissionsByReviewee.size() * 100 : 0;
    //     summary.setPassRate(passRate);
        
    //     // Hitung jumlah review yang selesai dan total
    //     int completedReviews = submissions.size();
    //     int totalReviews = assignmentRepository.findAll().size();         
    //     summary.setCompletedReviews(completedReviews);
    //     summary.setTotalReviews(totalReviews);
        
    //     return summary;
    // }
    
    // Mendapatkan performa outlet
    public List<OutletSummaryDTO> getOutletPerformance(String timeRange, String month) {
        Date[] dateRange;
        
        if (month != null && !month.isEmpty() && timeRange.matches("\\d{4}")) {
            // Filter berdasarkan bulan spesifik dalam tahun
            dateRange = getDateRangeFromYearAndMonth(timeRange, month);
        } else {
            // Filter berdasarkan timeRange saja
            dateRange = getDateRangeFromTimeRange(timeRange);
        }
        
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        System.out.println("Filtering outlet performance from " + startDate + " to " + endDate);
        
        // Filter assignment berdasarkan endDateFill
        List<PeerReviewAssignment> filteredAssignments = assignmentRepository.findByEndDateFillBetween(startDate, endDate);
        
        List<Outlet> outlets = outletDb.findAll();
        List<OutletSummaryDTO> outletSummaries = new ArrayList<>();
        
        for (Outlet outlet : outlets) {
            if (outlet == null || outlet.getName() == null || outlet.getName().isEmpty()) continue;
            
            OutletSummaryDTO summary = new OutletSummaryDTO();
            summary.setName(outlet.getName());
            
            // Hitung jumlah barista di outlet
            List<ProbationBarista> outletBaristas = probationBaristaDb.findByOutlet_OutletId(outlet.getOutletId());
            summary.setBaristaCount(outletBaristas.size());
            
            // Ambil assignment yang difilter untuk barista di outlet ini
            List<PeerReviewSubmission> outletSubmissions = new ArrayList<>();
            for (PeerReviewAssignment assignment : filteredAssignments) {
                if (outletBaristas.contains(assignment.getReviewee())) {
                    PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
                    if (submission != null) {
                        outletSubmissions.add(submission);
                    }
                }
            }
            
            // Hitung rata-rata skor
            double averageScore = 0.0;
            if (!outletSubmissions.isEmpty()) {
                averageScore = outletSubmissions.stream()
                        .mapToDouble(this::calculateAverageScore)
                        .average()
                        .orElse(0.0);
            }
            summary.setAverageScore(averageScore);
            
            // Hitung tingkat kelulusan
            Map<String, List<PeerReviewSubmission>> submissionsByReviewee = groupSubmissionsByReviewee(outletSubmissions);
            
            int passCount = 0;
            for (Map.Entry<String, List<PeerReviewSubmission>> entry : submissionsByReviewee.entrySet()) {
                double baristaAvgScore = entry.getValue().stream()
                        .mapToDouble(this::calculateAverageScore)
                        .average()
                        .orElse(0.0);
                
                if (baristaAvgScore >= 3.5) {
                    passCount++;
                }
            }
            
            double passRate = submissionsByReviewee.size() > 0 ? 
                    (double) passCount / submissionsByReviewee.size() * 100 : 0;
            summary.setPassRate(passRate);
            
            // Hitung tingkat penyelesaian review berdasarkan assignment yang difilter
            int totalExpectedReviews = 0;
            for (PeerReviewAssignment assignment : filteredAssignments) {
                if (outletBaristas.contains(assignment.getReviewee())) {
                    totalExpectedReviews++;
                }
            }
            
            double completionRate = totalExpectedReviews > 0 ? 
                    (double) outletSubmissions.size() / totalExpectedReviews * 100 : 0;
            summary.setReviewCompletionRate(completionRate);
            
            outletSummaries.add(summary);
        }
        
        return outletSummaries;
    }

    private Date[] getDateRangeFromYearAndMonth(String year, String month) {
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month) - 1; // Calendar month is 0-based
        
        Calendar startCal = Calendar.getInstance();
        startCal.set(yearInt, monthInt, 1, 0, 0, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Date startDate = startCal.getTime();
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(yearInt, monthInt, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date endDate = endCal.getTime();
        
        return new Date[] { startDate, endDate };
    }
    // public List<OutletSummaryDTO> getOutletPerformance(String timeRange) {
    //     Date[] dateRange = getDateRangeFromTimeRange(timeRange);
    //     Date startDate = dateRange[0];
    //     Date endDate = dateRange[1];
        
    //     // Ambil semua outlet
    //     List<Outlet> outlets = outletDb.findAll();
    //     List<OutletSummaryDTO> outletSummaries = new ArrayList<>();
        
    //     for (Outlet outlet : outlets) {
    //         if (outlet == null || outlet.getName() == null || outlet.getName().isEmpty()) continue;
            
    //         OutletSummaryDTO summary = new OutletSummaryDTO();
    //         summary.setName(outlet.getName());
            
    //         // Hitung jumlah barista di outlet
    //         List<ProbationBarista> outletBaristas = probationBaristaDb.findByOutlet_OutletId(outlet.getOutletId());
    //         summary.setBaristaCount(outletBaristas.size());
            
    //         // Ambil semua submission untuk barista di outlet ini
    //         List<PeerReviewSubmission> outletSubmissions = new ArrayList<>();
    //         for (ProbationBarista barista : outletBaristas) {
    //             List<PeerReviewAssignment> assignments = assignmentRepository.findByReviewee(barista);
    //             for (PeerReviewAssignment assignment : assignments) {
    //                 PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
    //                 if (submission != null && submission.getReviewedAt().after(startDate) && 
    //                         submission.getReviewedAt().before(endDate)) {
    //                     outletSubmissions.add(submission);
    //                 }
    //             }
    //         }
            
    //         // Hitung rata-rata skor
    //         double averageScore = 0.0;
    //         if (!outletSubmissions.isEmpty()) {
    //             averageScore = outletSubmissions.stream()
    //                     .mapToDouble(submission -> calculateAverageScore((PeerReviewSubmission) submission))
    //                     .average()
    //                     .orElse(0.0);
    //         }
    //         summary.setAverageScore(averageScore);
            
    //         // Hitung tingkat kelulusan
    //         Map<String, List<PeerReviewSubmission>> submissionsByReviewee = groupSubmissionsByReviewee(outletSubmissions);
            
    //         int passCount = 0;
    //         for (Map.Entry<String, List<PeerReviewSubmission>> entry : submissionsByReviewee.entrySet()) {
    //             double baristaAvgScore = entry.getValue().stream()
    //                     .mapToDouble(submission -> calculateAverageScore((PeerReviewSubmission) submission))
    //                     .average()
    //                     .orElse(0.0);
                
    //             if (baristaAvgScore >= 3.5) {
    //                 passCount++;
    //             }
    //         }
            
    //         double passRate = submissionsByReviewee.size() > 0 ? 
    //                 (double) passCount / submissionsByReviewee.size() * 100 : 0;
    //         summary.setPassRate(passRate);
            
    //         // Hitung tingkat penyelesaian review
    //         int totalExpectedReviews = 0;
    //         for (ProbationBarista barista : outletBaristas) {
    //             totalExpectedReviews += assignmentRepository.findByReviewee(barista).size();
    //         }            
    //         double completionRate = totalExpectedReviews > 0 ? 
    //                 (double) outletSubmissions.size() / totalExpectedReviews * 100 : 0;
    //         summary.setReviewCompletionRate(completionRate);
            
    //         outletSummaries.add(summary);
    //     }
        
    //     return outletSummaries;
    // }
    
    // Mendapatkan performa kategori

    public List<QuestionSummaryDTO> getCategoryPerformance(String timeRange) {
        Date[] dateRange = getDateRangeFromTimeRange(timeRange);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        // Filter assignment berdasarkan endDateFill
        List<PeerReviewAssignment> filteredAssignments = assignmentRepository.findByEndDateFillBetween(startDate, endDate);
        
        // Ambil submission dari assignment yang difilter
        List<PeerReviewSubmission> submissions = new ArrayList<>();
        for (PeerReviewAssignment assignment : filteredAssignments) {
            PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
            if (submission != null) {
                submissions.add(submission);
            }
        }
        
        List<PeerReviewContent> questions = contentRepository.findAll();
        List<QuestionSummaryDTO> questionSummaries = new ArrayList<>();
        
        for (PeerReviewContent question : questions) {
            QuestionSummaryDTO summary = new QuestionSummaryDTO();
            summary.setQuestionNumber(question.getQuestionNumber());
            summary.setText(question.getText());
            
            // Hitung rata-rata skor untuk pertanyaan ini
            double averageScore = calculateAverageScoreForQuestion(submissions, question.getQuestionNumber());
            summary.setAverageScore(averageScore);
            
            questionSummaries.add(summary);
        }
        
        return questionSummaries;
    }
    // public List<QuestionSummaryDTO> getCategoryPerformance(String timeRange) {
    //     Date[] dateRange = getDateRangeFromTimeRange(timeRange);
    //     Date startDate = dateRange[0];
    //     Date endDate = dateRange[1];
        
    //     List<PeerReviewContent> questions = contentRepository.findAll();
    //     List<PeerReviewSubmission> submissions = submissionRepository.findByReviewedAtBetween(startDate, endDate);
        
    //     List<QuestionSummaryDTO> questionSummaries = new ArrayList<>();
        
    //     for (PeerReviewContent question : questions) {
    //         QuestionSummaryDTO summary = new QuestionSummaryDTO();
    //         summary.setQuestionNumber(question.getQuestionNumber());
    //         summary.setText(question.getText());
            
    //         // Hitung rata-rata skor untuk pertanyaan ini
    //         double averageScore = calculateAverageScoreForQuestion(submissions, question.getQuestionNumber());
    //         summary.setAverageScore(averageScore);
            
    //         questionSummaries.add(summary);
    //     }
        
    //     return questionSummaries;
    // }
    public List<Integer> getAvailablePeerReviewYears() {
        List<PeerReviewAssignment> assignments = assignmentRepository.findAll();
    
        return assignments.stream()
            .map(a -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(a.getEndDateFill());
                return cal.get(Calendar.YEAR);
            })
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    // Mendapatkan daftar barista dengan filter
    public PageResponseDTO<BaristaReviewSummaryDTO> getBaristas(
            String outlet, String status, String search, int page, int size) {
        
        // Ambil semua barista probation
        List<ProbationBarista> allBaristas;
        
        if ("all".equals(outlet) && "all".equals(status) && (search == null || search.isEmpty())) {
            // Jika tidak ada filter, ambil semua
            allBaristas = probationBaristaDb.findAll();
        } else if (!"all".equals(outlet) && "all".equals(status)) {
            // Filter berdasarkan outlet saja
            allBaristas = probationBaristaDb.findByOutletName(outlet);
        } else if ("all".equals(outlet) && !"all".equals(status)) {
            // Filter berdasarkan status saja
            allBaristas = probationBaristaDb.findByStatusIgnoreCase(status);
        } else if (!"all".equals(outlet) && !"all".equals(status)) {
            // Filter berdasarkan outlet dan status
            allBaristas = probationBaristaDb.findByOutletNameAndStatusIgnoreCase(outlet, status);
        } else if (search != null && !search.isEmpty()) {
            // Filter berdasarkan search
            allBaristas = probationBaristaDb.findByUsernameContainingIgnoreCase(search);
        } else {
            allBaristas = probationBaristaDb.findAll();
        }
        
        List<BaristaReviewSummaryDTO> baristaReviewSummaries = new ArrayList<>();
        
        for (ProbationBarista barista : allBaristas) {
            BaristaReviewSummaryDTO summary = new BaristaReviewSummaryDTO();
            summary.setUsername(barista.getUsername());
            summary.setOutlet(barista.getOutlet() != null ? barista.getOutlet().getName() : "");
            summary.setPosition("Barista");
            
            // Set probation end date
            Date probationEndDate = barista.getProbationEndDate();
            if (probationEndDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                summary.setProbationEndDate(dateFormat.format(probationEndDate));
            } else {
                summary.setProbationEndDate("");
            }
            
            // Ambil semua assignment untuk barista ini
            List<PeerReviewAssignment> assignments = assignmentRepository.findByReviewee(barista);
            
            // Ambil semua submission untuk assignments tersebut
            List<PeerReviewSubmission> submissions = new ArrayList<>();
            for (PeerReviewAssignment assignment : assignments) {
                PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
                if (submission != null) {
                    submissions.add(submission);
                }
            }
            
            summary.setReviewsCompleted(submissions.size());
            summary.setReviewsTotal(assignments.size());

            if (submissions.isEmpty()) {
                summary.setAverageScore(0.0);
                summary.setStatus("Pending");
                summary.setLastReviewDate("");
                summary.setTrend("stable");
                summary.setTrendValue(0.0);
            } else {
                double averageScore = submissions.stream()
                    .mapToDouble(this::calculateAverageScore)
                    .average()
                    .orElse(0.0);
                summary.setAverageScore(averageScore);

                String baristaStatus;
                if (averageScore < 2.0) {
                    baristaStatus = "Tidak Lulus";
                } else if (averageScore < 3.5) {
                    baristaStatus = "Lulus Bersyarat";
                } else {
                    baristaStatus = "Lulus";
                }
                summary.setStatus(baristaStatus);

                // Filter status jika diperlukan
                if (!"all".equals(status) && !status.isEmpty() &&
                    !baristaStatus.toLowerCase().equals(status.toLowerCase())) {
                    continue;
                }

                // Ambil tanggal review terakhir dan hitung tren
                PeerReviewSubmission latestSubmission = submissions.stream()
                    .max(Comparator.comparing(PeerReviewSubmission::getReviewedAt))
                    .orElse(null);

                if (latestSubmission != null) {
                    summary.setLastReviewDate(new SimpleDateFormat("yyyy-MM-dd")
                        .format(latestSubmission.getReviewedAt()));

                    if (submissions.size() > 1) {
                        List<PeerReviewSubmission> sorted = submissions.stream()
                            .sorted(Comparator.comparing(PeerReviewSubmission::getReviewedAt))
                            .collect(Collectors.toList());

                        PeerReviewSubmission previous = sorted.get(sorted.size() - 2);
                        double previousScore = calculateAverageScore(previous);
                        double currentScore = calculateAverageScore(latestSubmission);
                        double trendValue = currentScore - previousScore;

                        summary.setTrendValue(trendValue);
                        if (trendValue > 0) summary.setTrend("up");
                        else if (trendValue < 0) summary.setTrend("down");
                        else summary.setTrend("stable");
                    } else {
                        summary.setTrend("stable");
                        summary.setTrendValue(0.0);
                    }
                }
            
    
                // // Ambil tanggal review terakhir
                // PeerReviewSubmission latestSubmission = submissions.stream()
                //         .max(Comparator.comparing(PeerReviewSubmission::getReviewedAt))
                //         .orElse(null);
                
                // if (latestSubmission != null) {
                //     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //     summary.setLastReviewDate(dateFormat.format(latestSubmission.getReviewedAt()));
                    
                //     // Hitung tren (perbandingan dengan review sebelumnya)
                //     if (submissions.size() > 1) {
                //         List<PeerReviewSubmission> sortedSubmissions = submissions.stream()
                //                 .sorted(Comparator.comparing(PeerReviewSubmission::getReviewedAt))
                //                 .collect(Collectors.toList());
                        
                //         PeerReviewSubmission previousSubmission = sortedSubmissions.get(sortedSubmissions.size() - 2);
                //         double previousScore = calculateAverageScore(previousSubmission);
                //         double currentScore = calculateAverageScore(latestSubmission);
                //         double trendValue = currentScore - previousScore;
                        
                //         summary.setTrendValue(trendValue);
                //         if (trendValue > 0) {
                //             summary.setTrend("up");
                //         } else if (trendValue < 0) {
                //             summary.setTrend("down");
                //         } else {
                //             summary.setTrend("stable");
                //         }
                //     } else {
                //         summary.setTrend("stable");
                //         summary.setTrendValue(0.0);
                //     }
                // }
            }
            
            baristaReviewSummaries.add(summary);
        }
        
        // Paging manual
        int start = page * size;
        int end = Math.min(start + size, baristaReviewSummaries.size());
        
        List<BaristaReviewSummaryDTO> pagedBaristas;
        if (start < baristaReviewSummaries.size()) {
            pagedBaristas = baristaReviewSummaries.subList(start, end);
        } else {
            pagedBaristas = new ArrayList<>();
        }
        
        PageResponseDTO<BaristaReviewSummaryDTO> response = new PageResponseDTO<>();
        response.setContent(pagedBaristas);
        response.setTotalPages((int) Math.ceil((double) baristaReviewSummaries.size() / size));
        response.setTotalElements((long) baristaReviewSummaries.size());
        response.setCurrentPage(page);
        response.setSize(size);
        
        return response;
    }
    
    // Mendapatkan tren skor
    public List<ScoreTrendDTO> getScoreTrend(String timeRange) {
        List<ScoreTrendDTO> trendData = new ArrayList<>();
        
        // Parse tahun dari timeRange
        int year;
        if (timeRange.matches("\\d{4}")) {
            year = Integer.parseInt(timeRange);
        } else {
            year = Calendar.getInstance().get(Calendar.YEAR); // Default tahun sekarang
        }
        
        System.out.println("Getting score trend for year: " + year);
        
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        // Loop untuk setiap bulan dalam tahun yang dipilih
        for (int month = 0; month < 12; month++) {
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, 1, 0, 0, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            Date startOfMonth = startCal.getTime();
            
            Calendar endCal = Calendar.getInstance();
            endCal.set(year, month, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            Date endOfMonth = endCal.getTime();
            
            // Filter assignment berdasarkan endDateFill dalam bulan tersebut
            List<PeerReviewAssignment> monthAssignments = assignmentRepository.findByEndDateFillBetween(startOfMonth, endOfMonth);
            
            // Ambil submission dari assignment yang difilter
            List<PeerReviewSubmission> monthSubmissions = new ArrayList<>();
            for (PeerReviewAssignment assignment : monthAssignments) {
                PeerReviewSubmission submission = submissionRepository.findByAssignment(assignment);
                if (submission != null) {
                    monthSubmissions.add(submission);
                }
            }
            
            double averageScore = 0.0;
            if (!monthSubmissions.isEmpty()) {
                averageScore = monthSubmissions.stream()
                        .mapToDouble(this::calculateAverageScore)
                        .average()
                        .orElse(0.0);
            }
            
            ScoreTrendDTO monthData = new ScoreTrendDTO();
            monthData.setMonth(monthNames[month]);
            monthData.setScore(averageScore);
            
            trendData.add(monthData);
            
            System.out.println("Month " + monthNames[month] + ": " + monthAssignments.size() + " assignments, " + monthSubmissions.size() + " submissions, avg score: " + averageScore);
        }
        
        return trendData;
    }
    
    // // Export data ke Excel
    // public byte[] exportPeerReviewData(String timeRange, String outlet, String status) throws IOException {
    //     // Ambil data yang diperlukan
    //     PageResponseDTO<BaristaReviewSummaryDTO> baristasPage = getBaristas(outlet, status, "", 0, 1000);
    //     List<BaristaReviewSummaryDTO> baristas = baristasPage.getContent();
    //     List<OutletSummaryDTO> outlets = getOutletPerformance(timeRange);
    //     List<QuestionSummaryDTO> categories = getCategoryPerformance(timeRange);
        
    //     // Buat workbook Excel
    //     Workbook workbook = new XSSFWorkbook();
        
    //     // Buat style untuk header
    //     CellStyle headerStyle = workbook.createCellStyle();
    //     headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    //     headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    //     headerStyle.setBorderBottom(BorderStyle.THIN);
    //     headerStyle.setBorderTop(BorderStyle.THIN);
    //     headerStyle.setBorderLeft(BorderStyle.THIN);
    //     headerStyle.setBorderRight(BorderStyle.THIN);
        
    //     Font headerFont = workbook.createFont();
    //     headerFont.setBold(true);
    //     headerStyle.setFont(headerFont);
        
    //     // Buat sheet untuk baristas
    //     Sheet baristaSheet = workbook.createSheet("Baristas");
        
    //     // Buat header untuk barista sheet
    //     Row baristaHeaderRow = baristaSheet.createRow(0);
    //     String[] baristaHeaders = {"Username", "Outlet", "Position", "Average Score", "Status", 
    //             "Reviews Completed", "Last Review Date", "Probation End Date", "Trend"};
        
    //     for (int i = 0; i < baristaHeaders.length; i++) {
    //         Cell cell = baristaHeaderRow.createCell(i);
    //         cell.setCellValue(baristaHeaders[i]);
    //         cell.setCellStyle(headerStyle);
    //         baristaSheet.setColumnWidth(i, 4000);
    //     }
        
    //     // Isi data barista
    //     int baristaRowNum = 1;
    //     for (BaristaReviewSummaryDTO barista : baristas) {
    //         Row row = baristaSheet.createRow(baristaRowNum++);
    //         row.createCell(0).setCellValue(barista.getUsername());
    //         row.createCell(1).setCellValue(barista.getOutlet());
    //         row.createCell(2).setCellValue(barista.getPosition());
    //         row.createCell(3).setCellValue(barista.getAverageScore());
    //         row.createCell(4).setCellValue(barista.getStatus());
    //         row.createCell(5).setCellValue(barista.getReviewsCompleted() + "/" + barista.getReviewsTotal());
    //         row.createCell(6).setCellValue(barista.getLastReviewDate());
    //         row.createCell(7).setCellValue(barista.getProbationEndDate());
            
    //         String trendText = barista.getTrend().equals("up") ? "+" + barista.getTrendValue() :
    //                 barista.getTrend().equals("down") ? barista.getTrendValue().toString() : "0.0";
    //         row.createCell(8).setCellValue(trendText);
    //     }
        
    //     // Buat sheet untuk outlets
    //     Sheet outletSheet = workbook.createSheet("Outlets");
        
    //     // Buat header untuk outlet sheet
    //     Row outletHeaderRow = outletSheet.createRow(0);
    //     String[] outletHeaders = {"Outlet Name", "Average Score", "Barista Count", "Pass Rate (%)", "Review Completion (%)"};
        
    //     for (int i = 0; i < outletHeaders.length; i++) {
    //         Cell cell = outletHeaderRow.createCell(i);
    //         cell.setCellValue(outletHeaders[i]);
    //         cell.setCellStyle(headerStyle);
    //         outletSheet.setColumnWidth(i, 4000);
    //     }
        
    //     // Isi data outlet
    //     int outletRowNum = 1;
    //     for (OutletSummaryDTO outletItem : outlets) {
    //         Row row = outletSheet.createRow(outletRowNum++);
    //         row.createCell(0).setCellValue(outletItem.getName());
    //         row.createCell(1).setCellValue(outletItem.getAverageScore());
    //         row.createCell(2).setCellValue(outletItem.getBaristaCount());
    //         row.createCell(3).setCellValue(outletItem.getPassRate());
    //         row.createCell(4).setCellValue(outletItem.getReviewCompletionRate());
    //     }
        
    //     // Buat sheet untuk categories
    //     Sheet categorySheet = workbook.createSheet("Categories");
        
    //     // Buat header untuk category sheet
    //     Row categoryHeaderRow = categorySheet.createRow(0);
    //     String[] categoryHeaders = {"Question Number", "Question Text", "Average Score"};
        
    //     for (int i = 0; i < categoryHeaders.length; i++) {
    //         Cell cell = categoryHeaderRow.createCell(i);
    //         cell.setCellValue(categoryHeaders[i]);
    //         cell.setCellStyle(headerStyle);
    //         categorySheet.setColumnWidth(i, 6000);
    //     }
        
    //     // Isi data category
    //     int categoryRowNum = 1;
    //     for (QuestionSummaryDTO category : categories) {
    //         Row row = categorySheet.createRow(categoryRowNum++);
    //         row.createCell(0).setCellValue(category.getQuestionNumber());
    //         row.createCell(1).setCellValue(category.getText());
    //         row.createCell(2).setCellValue(category.getAverageScore());
    //     }
        
    //     // Tulis workbook ke ByteArrayOutputStream
    //     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //     workbook.write(outputStream);
    //     workbook.close();
        
    //     return outputStream.toByteArray();
    // }
    
    // Helper method untuk menghitung rata-rata skor dari submission
    private double calculateAverageScore(PeerReviewSubmission submission) {
        double sum = 0.0;
        int count = 0;
        
        if (submission.getQ1() != null) { sum += submission.getQ1(); count++; }
        if (submission.getQ2() != null) { sum += submission.getQ2(); count++; }
        if (submission.getQ3() != null) { sum += submission.getQ3(); count++; }
        if (submission.getQ4() != null) { sum += submission.getQ4(); count++; }
        if (submission.getQ5() != null) { sum += submission.getQ5(); count++; }
        if (submission.getQ6() != null) { sum += submission.getQ6(); count++; }
        if (submission.getQ7() != null) { sum += submission.getQ7(); count++; }
        if (submission.getQ8() != null) { sum += submission.getQ8(); count++; }
        if (submission.getQ9() != null) { sum += submission.getQ9(); count++; }
        if (submission.getQ10() != null) { sum += submission.getQ10(); count++; }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    // Helper method untuk menghitung rata-rata skor untuk pertanyaan tertentu
    private double calculateAverageScoreForQuestion(List<PeerReviewSubmission> submissions, int questionNumber) {
        switch (questionNumber) {
            case 1: return submissions.stream()
                    .filter(s -> s.getQ1() != null)
                    .mapToDouble(PeerReviewSubmission::getQ1)
                    .average()
                    .orElse(0.0);
            case 2: return submissions.stream()
                    .filter(s -> s.getQ2() != null)
                    .mapToDouble(PeerReviewSubmission::getQ2)
                    .average()
                    .orElse(0.0);
            case 3: return submissions.stream()
                    .filter(s -> s.getQ3() != null)
                    .mapToDouble(PeerReviewSubmission::getQ3)
                    .average()
                    .orElse(0.0);
            case 4: return submissions.stream()
                    .filter(s -> s.getQ4() != null)
                    .mapToDouble(PeerReviewSubmission::getQ4)
                    .average()
                    .orElse(0.0);
            case 5: return submissions.stream()
                    .filter(s -> s.getQ5() != null)
                    .mapToDouble(PeerReviewSubmission::getQ5)
                    .average()
                    .orElse(0.0);
            case 6: return submissions.stream()
                    .filter(s -> s.getQ6() != null)
                    .mapToDouble(PeerReviewSubmission::getQ6)
                    .average()
                    .orElse(0.0);
            case 7: return submissions.stream()
                    .filter(s -> s.getQ7() != null)
                    .mapToDouble(PeerReviewSubmission::getQ7)
                    .average()
                    .orElse(0.0);
            case 8: return submissions.stream()
                    .filter(s -> s.getQ8() != null)
                    .mapToDouble(PeerReviewSubmission::getQ8)
                    .average()
                    .orElse(0.0);
            case 9: return submissions.stream()
                    .filter(s -> s.getQ9() != null)
                    .mapToDouble(PeerReviewSubmission::getQ9)
                    .average()
                    .orElse(0.0);
            case 10: return submissions.stream()
                    .filter(s -> s.getQ10() != null)
                    .mapToDouble(PeerReviewSubmission::getQ10)
                    .average()
                    .orElse(0.0);
            default: return 0.0;
        }
    }
    
    // Helper method untuk mendapatkan range tanggal berdasarkan timeRange
    private Date[] getDateRangeFromTimeRange(String timeRange) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        Date startDate;
        if (timeRange.matches("\\d{4}")) {
            int year = Integer.parseInt(timeRange);
            
            // Set start date ke 1 Januari tahun tersebut
            calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startDate = calendar.getTime();
            
            // Set end date ke 31 Desember tahun tersebut
            calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            endDate = calendar.getTime();
            
            return new Date[] { startDate, endDate };
        }

        switch (timeRange) {
            case "this-month":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;
            case "last-month":
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;
            case "last-3-months":
                calendar.add(Calendar.MONTH, -3);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;
            case "last-6-months":
                calendar.add(Calendar.MONTH, -6);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;
            default:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
        }
        
        return new Date[] { startDate, endDate };
    }
    
    // Helper method untuk mendapatkan range tanggal periode sebelumnya
    private Date[] getPreviousDateRange(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        
        long diffInMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        
        Calendar previousStartCal = Calendar.getInstance();
        previousStartCal.setTime(startDate);
        previousStartCal.add(Calendar.MILLISECOND, -(int)diffInMillis);
        
        Calendar previousEndCal = Calendar.getInstance();
        previousEndCal.setTime(startDate);
        previousEndCal.add(Calendar.MILLISECOND, -1);
        
        return new Date[] { previousStartCal.getTime(), previousEndCal.getTime() };
    }
    
    // Helper method untuk mengelompokkan submission berdasarkan reviewee
    private Map<String, List<PeerReviewSubmission>> groupSubmissionsByReviewee(List<PeerReviewSubmission> submissions) {
        Map<String, List<PeerReviewSubmission>> result = new HashMap<>();
        
        for (PeerReviewSubmission submission : submissions) {
            String revieweeUsername = submission.getAssignment().getReviewee().getUsername();
            
            if (!result.containsKey(revieweeUsername)) {
                result.put(revieweeUsername, new ArrayList<>());
            }
            
            result.get(revieweeUsername).add(submission);
        }
        
        return result;
    }
}