package propensi.tens.bms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import jakarta.transaction.Transactional;
import propensi.tens.bms.features.account_management.models.Admin;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.CLevel;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.AdminDb;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.CLevelDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.account_management.services.AccountService;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;
import propensi.tens.bms.features.trainee_management.enums.QuestionType;
import propensi.tens.bms.features.trainee_management.models.Assessment;
import propensi.tens.bms.features.trainee_management.models.AssessmentQuestion;
import propensi.tens.bms.features.trainee_management.models.PeerReviewContent;
import propensi.tens.bms.features.trainee_management.models.QuestionOption;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewContentRepository;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class BmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmsApplication.class, args);
    }

    @Autowired
    private AccountService userService;

    @Bean
    @Transactional
    CommandLineRunner run(
        AdminDb adminDb,
        OutletDb outletDb,
        BaristaDb baristaDb,
        CLevelDb cLevelDb,
        HeadBarDb headBarDb,
        ProbationBaristaDb probationBaristaDb,
        AssessmentRepository assessmentRepo,
        PeerReviewContentRepository peerReviewContentRepo
    ) {
        return args -> {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(userService.hashPassword("admin"));
            admin.setFullName("Administrator");
            admin.setGender(true);
            admin.setPhoneNumber("08123456789");
            admin.setStatus("Active");
            admin.setAddress("Jl. admin");
            admin.setIsVerified(true);
            adminDb.save(admin);

            Outlet outlet1 = new Outlet();
            outlet1.setName("Tens Coffee Margonda");
            outlet1.setLocation("Jl. Margonda Raya No.519, Pondok Cina, Beji, Depok, Jawa Barat 16424");

            Outlet outlet2 = new Outlet();
            outlet2.setName("Tens Coffee Kantin Vokasi UI");
            outlet2.setLocation("Kantin Vokasi UI, Kukusan, Beji, Depok, Jawa Barat 16425");

            Outlet outlet3 = new Outlet();
            outlet3.setName("Tens Coffee UIN Ciputat");
            outlet3.setLocation("Jl. Tarumanegara, Pisangan, Ciputat Tim., Tangerang Selatan, Banten 15419");

            Outlet outlet4 = new Outlet();
            outlet4.setName("Tens Coffee Pamulang");
            outlet4.setLocation("Jl. Pajajaran No.8, Pamulang Bar., Pamulang, Tangerang Selatan, Banten 15417");

            Outlet outlet5 = new Outlet();
            outlet5.setName("Tens Coffee UPN Veteran Jakarta");
            outlet5.setLocation("Jl. Pangkalan Jati 1 No.1e, Cinere, Depok, Jawa Barat 16513");

            Outlet outlet6 = new Outlet();
            outlet6.setName("Tens Coffee Balairung");
            outlet6.setLocation("Jl. Raya Balairung No.1, Pondok Cina, Beji, Depok, Jawa Barat 16424");

            outletDb.save(outlet1);
            outletDb.save(outlet2);
            outletDb.save(outlet3);
            outletDb.save(outlet4);
            outletDb.save(outlet5);
            outletDb.save(outlet6);
            

            baristaDb.save(createBarista("skinny.pete", "Skinny Pete", true, true, "08111111111", outlet1));
            baristaDb.save(createBarista("brandon.mayhew", "Brandon Mayhew", false, false, "08222222222", outlet2));
            baristaDb.save(createBarista("christian.ortega", "Christian Ortega", true, false, "08333333333", outlet3));

            baristaDb.save(createBarista("akinny.pete", "Akinny Pete", true, true, "08111111111", outlet4));
            baristaDb.save(createBarista("crandon.mayhew", "Crandon Mayhew", false, false, "08222222222", outlet5));
            baristaDb.save(createBarista("dhristian.ortega", "Dhristian Ortega", true, false, "08333333333", outlet6));

            baristaDb.save(createBarista("bkinny.pete", "Skinny Pete", true, true, "08111111111", outlet4));
            baristaDb.save(createBarista("drandon.mayhew", "Brandon Mayhew", false, false, "08222222222", outlet5));
            baristaDb.save(createBarista("ehristian.ortega", "Christian Ortega", true, false, "08333333333", outlet6));

            baristaDb.save(createBarista("ckinny.pete", "Ckinny Pete", true, true, "08111111111", outlet4));
            baristaDb.save(createBarista("erandon.mayhew", "Erandon Mayhew", false, false, "08222222222", outlet5));
            baristaDb.save(createBarista("fhristian.ortega", "Fhristian Ortega", true, false, "08333333333", outlet6));

            baristaDb.save(createBarista("dkinny.pete", "Dkinny Pete", true, true, "08111111111", outlet2));
            baristaDb.save(createBarista("frandon.mayhew", "Frandon Mayhew", false, false, "08222222222", outlet3));
            baristaDb.save(createBarista("ghristian.ortega", "Ghristian Ortega", true, false, "08333333333", outlet3));

            cLevelDb.save(createCLevel("gustavo.fring", "Gustavo Fring", true, "08444444444", "CLEVEL"));
            cLevelDb.save(createCLevel("mike.ehrmantraut", "Mike Ehrmantraut", true, "08666666666", "HR"));

            HeadBar headBar1 = createHeadBar("novi.irianti", "Novi Irianti", true, "08777777777", outlet2);
            headBar1 = headBarDb.save(headBar1);
            outlet2.setHeadbar(headBar1);
            outletDb.save(outlet2);

            HeadBar headBar2 = createHeadBar("dewi.astuti", "Dewi Astuti", false, "08888888888", outlet5);
            headBar2 = headBarDb.save(headBar2);
            outlet5.setHeadbar(headBar2);
            outletDb.save(outlet5);

            HeadBar headBar3 = createHeadBar("annisa.zaskia", "Annisa Kayla Zaskia", true, "08999999999", outlet1);
            headBar3 = headBarDb.save(headBar3);
            outlet1.setHeadbar(headBar3);
            outletDb.save(outlet1);

            HeadBar headBar4 = createHeadBar("farhan.satrio", "Farhan Hadi Satrio", true, "08999999999", outlet3);
            headBar4 = headBarDb.save(headBar4);
            outlet3.setHeadbar(headBar4);
            outletDb.save(outlet3);

            HeadBar headBar5 = createHeadBar("faraz.taqwa", "Faraz Taqwa", true, "08999999999", outlet4);
            headBar5 = headBarDb.save(headBar5);
            outlet4.setHeadbar(headBar5);
            outletDb.save(outlet4);

            HeadBar headBar6 = createHeadBar("alexandre.alexandre", "Alexandre", true, "08999999999", outlet6);
            headBar6 = headBarDb.save(headBar6);
            outlet6.setHeadbar(headBar6);
            outletDb.save(outlet6);

            probationBaristaDb.save(createProbationBarista("jesse.pinkman", "Jesse Pinkman", true, "08101010101", outlet1));
            probationBaristaDb.save(createProbationBarista("todd.alquist", "Todd Alquist", false, "08202020202", outlet1));
            probationBaristaDb.save(createProbationBarista("andrea.cantillo", "Andrea Cantillo", true, "08303030303", outlet1));

            Assessment baristaA = new Assessment();
            baristaA.setTemplate(AssessmentTemplate.BARISTA);
            baristaA.setDeadline(LocalDate.now().plusDays(7));
            String[] mcQs = {
                "Apa nama espresso yang diseduh dengan tekanan tinggi?",
                "Manakah bahan utama dalam pembuatan latte?",
                "Berapa suhu ideal untuk menyajikan cappuccino?",
                "Apa rasio umum espresso dan susu dalam latte?",
                "Manakah alat yang digunakan untuk menghasilkan buih susu?"
            };
            String[][] mcOpts = {
                {"Latte", "Cappuccino", "Espresso", "Americano", "Mocha"},
                {"Susu", "Air", "Cokelat", "Gula", "Teh"},
                {"60°C", "70°C", "80°C", "85°C", "90°C"},
                {"1:1", "1:2", "1:3", "2:1", "3:1"},
                {"Portafilter", "Tamper", "Steam wand", "Grinder", "Shot glass"}
            };
            int[] correctIdx = {2, 0, 1, 2, 2};
            for (int i = 0; i < 5; i++) {
                AssessmentQuestion q = new AssessmentQuestion();
                q.setCode("BA-Q" + (i + 1));
                q.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                q.setTemplate(AssessmentTemplate.BARISTA);
                q.setQuestionText(mcQs[i]);
                q.setAssessment(baristaA);
                for (int j = 0; j < 5; j++) {
                    QuestionOption o = new QuestionOption();
                    o.setOptionText(mcOpts[i][j]);
                    o.setIsCorrect(j == correctIdx[i]);
                    o.setQuestion(q);
                    q.getOptions().add(o);
                }
                baristaA.getQuestions().add(q);
            }
            AssessmentQuestion essayB = new AssessmentQuestion();
            essayB.setCode("BA-Q6");
            essayB.setQuestionType(QuestionType.ESSAY);
            essayB.setTemplate(AssessmentTemplate.BARISTA);
            essayB.setQuestionText("Jelaskan langkah-langkah dasar dalam pembuatan espresso mulai dari penggilingan biji hingga menuang espresso.");
            essayB.setAssessment(baristaA);
            baristaA.getQuestions().add(essayB);
            baristaA.getAssignedUsers().addAll(baristaDb.findAll());
            assessmentRepo.save(baristaA);

            Assessment headbarA = new Assessment();
            headbarA.setTemplate(AssessmentTemplate.HEADBAR);
            headbarA.setDeadline(LocalDate.now().plusDays(7));
            String[] mcQs2 = {
                "Apa tujuan utama penjadwalan shift yang terstruktur?",
                "Berapa minimal waktu pemberitahuan perubahan jadwal shift untuk barista?",
                "Manakah yang bukan tanggung jawab Head Bar?",
                "Apa yang dimaksud dengan 'overtime log'?",
                "Sistem notifikasi mana yang harus diaktifkan untuk pengingat peer review?"
            };
            String[][] mcOpts2 = {
                {"Memastikan layanan konsisten", "Mengurangi jumlah karyawan", "Menambah jam lembur", "Meningkatkan biaya operasional", "Membatasi cuti"},
                {"1 jam", "6 jam", "12 jam", "24 jam", "48 jam"},
                {"Membuat shift schedule", "Menyetujui cuti", "Menilai assessment barista", "Mengelola overtime", "Menugaskan peer review"},
                {"Jadwal shift tambahan", "Catatan jam kerja lembur", "Daftar cuti", "Penilaian kinerja", "Materi pelatihan"},
                {"Email", "SMS", "Aplikasi mobile", "Dashboard", "Semua di atas"}
            };
            int[] correctIdx2 = {0, 3, 2, 1, 0};
            for (int i = 0; i < 5; i++) {
                AssessmentQuestion q = new AssessmentQuestion();
                q.setCode("HB-Q" + (i + 1));
                q.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                q.setTemplate(AssessmentTemplate.HEADBAR);
                q.setQuestionText(mcQs2[i]);
                q.setAssessment(headbarA);
                for (int j = 0; j < 5; j++) {
                    QuestionOption o = new QuestionOption();
                    o.setOptionText(mcOpts2[i][j]);
                    o.setIsCorrect(j == correctIdx2[i]);
                    o.setQuestion(q);
                    q.getOptions().add(o);
                }
                headbarA.getQuestions().add(q);
            }
            AssessmentQuestion essayH = new AssessmentQuestion();
            essayH.setCode("HB-Q6");
            essayH.setQuestionType(QuestionType.ESSAY);
            essayH.setTemplate(AssessmentTemplate.HEADBAR);
            essayH.setQuestionText("Jelaskan bagaimana Anda akan meninjau dan menyetujui permohonan cuti barista yang mendadak.");
            essayH.setAssessment(headbarA);
            headbarA.getQuestions().add(essayH);
            headbarA.getAssignedUsers().addAll(headBarDb.findAll());
            assessmentRepo.save(headbarA);

            if (peerReviewContentRepo.count() == 0) {
                peerReviewContentRepo.save(new PeerReviewContent(null, 1, "Kemampuan berkomunikasi dengan rekan kerja dan pelanggan"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 2, "Kecepatan dan ketepatan dalam menyajikan minuman"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 3, "Kedisiplinan dalam mematuhi jadwal kerja dan waktu istirahat"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 4, "Kualitas dalam pembuatan minuman sesuai standar"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 5, "Inisiatif dan proaktif dalam menyelesaikan tugas"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 6, "Kemampuan bekerja sama dalam tim"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 7, "Kebersihan dan kerapian area kerja"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 8, "Pemahaman menu dan standar pelayanan"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 9, "Kemampuan menangani keluhan pelanggan dengan baik"));
                peerReviewContentRepo.save(new PeerReviewContent(null, 10, "Adaptasi terhadap prosedur dan budaya outlet"));
            }
        };
    }

    private Barista createBarista(String username, String fullName, boolean gender, boolean isIntern, String phone, Outlet outlet) {
        Barista barista = new Barista();
        barista.setUsername(username);
        barista.setPassword(userService.hashPassword("barista123"));
        barista.setFullName(fullName);
        barista.setGender(gender);
        barista.setPhoneNumber(phone);
        barista.setStatus("Active");
        barista.setIsVerified(true);
        barista.setOutlet(outlet);
        barista.setIsIntern(isIntern);
        return barista;
    }

    private CLevel createCLevel(String username, String fullName, boolean gender, String phone, String cLevelType) {
        CLevel clevel = new CLevel();
        clevel.setUsername(username);
        clevel.setPassword(userService.hashPassword("clevel123"));
        clevel.setFullName(fullName);
        clevel.setGender(gender);
        clevel.setPhoneNumber(phone);
        clevel.setStatus("Active");
        clevel.setIsVerified(true);
        clevel.setCLevelType(cLevelType);
        return clevel;
    }

    private HeadBar createHeadBar(String username, String fullName, boolean gender, String phone, Outlet outlet) {
        HeadBar headBar = new HeadBar();
        headBar.setUsername(username);
        headBar.setPassword(userService.hashPassword("headbar123"));
        headBar.setFullName(fullName);
        headBar.setGender(gender);
        headBar.setPhoneNumber(phone);
        headBar.setStatus("Active");
        headBar.setIsVerified(true);
        headBar.setOutlet(outlet);
        return headBar;
    }

    private ProbationBarista createProbationBarista(String username, String fullName, boolean gender, String phone, Outlet outlet) {
        ProbationBarista probationBarista = new ProbationBarista();
        probationBarista.setUsername(username);
        probationBarista.setPassword(userService.hashPassword("probation123"));
        probationBarista.setFullName(fullName);
        probationBarista.setGender(gender);
        probationBarista.setPhoneNumber(phone);
        probationBarista.setStatus("Active");
        probationBarista.setIsVerified(true);
        probationBarista.setProbationEndDate(addDays(new Date(), 30));
        probationBarista.setOutlet(outlet);
        return probationBarista;
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}
