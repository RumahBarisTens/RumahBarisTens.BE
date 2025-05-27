package propensi.tens.bms.features.account_management.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.account_management.dto.response.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@Service
public class BaristaService {

    @Autowired
    private BaristaDb baristaDb;

    @Autowired
    private HeadBarDb headBarDb;

    @Autowired
    private ProbationBaristaDb probationBaristaDb;

    // public List<BaristaDropdownResponseDTO> getBaristasForDropdown(Long outletId) {
    //     List<BaristaDropdownResponseDTO> result = new ArrayList<>();

    //     List<Barista> baristas = baristaDb.findByOutlet_OutletId(outletId);
    //     List<ProbationBarista> probations = probationBaristaDb.findByOutlet_OutletId(outletId);
    //     List<HeadBar> headBars = headBarDb.findByOutlet_OutletId(outletId);

    //     baristas.forEach(b -> result.add(mapToDropdownDto(b, "Barista")));
    //     probations.forEach(p -> result.add(mapToDropdownDto(p, "Probation Barista")));
    //     headBars.forEach(h -> result.add(mapToDropdownDto(h, "Head Bar")));

    //     return result;
    // }
    public List<BaristaDropdownResponseDTO> getBaristasForDropdown(Long outletId, String status) {
        List<BaristaDropdownResponseDTO> result = new ArrayList<>();
    
        List<Barista> baristas = baristaDb.findByOutlet_OutletId(outletId);
        List<ProbationBarista> probations = probationBaristaDb.findByOutlet_OutletId(outletId);
        List<HeadBar> headBars = headBarDb.findByOutlet_OutletId(outletId);
    
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            baristas = baristas.stream()
                .filter(b -> status.equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
            
            probations = probations.stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());
            
            headBars = headBars.stream()
                .filter(h -> status.equalsIgnoreCase(h.getStatus()))
                .collect(Collectors.toList());
        }
    
        baristas.forEach(b -> result.add(mapToDropdownDto(b, "Barista")));
        probations.forEach(p -> result.add(mapToDropdownDto(p, "Probation Barista")));
        headBars.forEach(h -> result.add(mapToDropdownDto(h, "Head Bar")));
    
        return result;
    }

    private BaristaDropdownResponseDTO mapToDropdownDto(EndUser user, String role) {
        return new BaristaDropdownResponseDTO(
            user.getId().toString(),
            user.getFullName(),
            role
        );
    }

    public BaristaStatsResponse getStats() {
        List<Barista> baristas = baristaDb.findAll();
        List<ProbationBarista> probations = probationBaristaDb.findAll();
        List<HeadBar> headBars = headBarDb.findAll();
    
        int total = baristas.size() + probations.size() + headBars.size();
    
        AtomicInteger active = new AtomicInteger(0);
        AtomicInteger inactive = new AtomicInteger(0);
        Set<Long> outletSet = new HashSet<>();
    
        Map<String, Integer> outletStats = new HashMap<>();
        Map<String, Integer> roleStats = new HashMap<>();
        Map<String, Integer> statusStats = new HashMap<>();
    
        BiConsumer<EndUser, String> processUser = (user, role) -> {
            String status = user.getStatus() != null ? user.getStatus() : "Tidak Diketahui";
            statusStats.put(status, statusStats.getOrDefault(status, 0) + 1);
    
            if ("Active".equalsIgnoreCase(status)) active.incrementAndGet();
            else inactive.incrementAndGet();
    
            roleStats.put(role, roleStats.getOrDefault(role, 0) + 1);
    
            if (user instanceof Barista b && b.getOutlet() != null) {
                String outletName = b.getOutlet().getName();
                outletStats.put(outletName, outletStats.getOrDefault(outletName, 0) + 1);
                outletSet.add(b.getOutlet().getOutletId());
            } else if (user instanceof ProbationBarista p && p.getOutlet() != null) {
                String outletName = p.getOutlet().getName();
                outletStats.put(outletName, outletStats.getOrDefault(outletName, 0) + 1);
                outletSet.add(p.getOutlet().getOutletId());
            } else if (user instanceof HeadBar h && h.getOutlet() != null) {
                String outletName = h.getOutlet().getName();
                outletStats.put(outletName, outletStats.getOrDefault(outletName, 0) + 1);
                outletSet.add(h.getOutlet().getOutletId());
            }
        };
    
        baristas.forEach(b -> processUser.accept(b, b.getIsIntern() ? "Intern" : "Barista"));
        probations.forEach(p -> processUser.accept(p, "Probation"));
        headBars.forEach(h -> processUser.accept(h, "Head Bar"));
    
        List<SimpleStat> outletStatsList = outletStats.entrySet().stream()
            .map(e -> new SimpleStat(e.getKey(), e.getValue()))
            .toList();
    
        List<SimpleStat> roleStatsList = roleStats.entrySet().stream()
            .map(e -> new SimpleStat(e.getKey(), e.getValue()))
            .toList();
    
        List<SimpleStat> statusStatsList = statusStats.entrySet().stream()
            .map(e -> new SimpleStat(e.getKey(), e.getValue()))
            .toList();
    
        return new BaristaStatsResponse(
            total,
            active.get(),
            inactive.get(),
            outletSet.size(),
            outletStatsList,
            roleStatsList,
            statusStatsList
        );
    }

    public List<BaristaDropdownResponseDTO> getAllBaristas() {
        List<BaristaDropdownResponseDTO> result = new ArrayList<>();
    
        List<Barista> baristas = baristaDb.findAll();
        List<ProbationBarista> probations = probationBaristaDb.findAll();
        List<HeadBar> headBars = headBarDb.findAll();
    
        baristas.forEach(b -> result.add(mapToDropdownDto(b, b.getIsIntern() ? "Intern" : "Barista")));
        probations.forEach(p -> result.add(mapToDropdownDto(p, "Probation")));
        headBars.forEach(h -> result.add(mapToDropdownDto(h, "Head Bar")));
    
        return result;
    }

    public List<BaristaAll> getAllBaristasDetailed() {
    List<BaristaAll> result = new ArrayList<>();

    List<Barista> baristas = baristaDb.findAll();
    List<ProbationBarista> probations = probationBaristaDb.findAll();
    List<HeadBar> headBars = headBarDb.findAll();

    baristas.forEach(b -> {
        result.add(new BaristaAll(
            b.getId().toString(),
            b.getFullName(),
            b.getIsIntern() ? "Intern" : "Barista",
            b.getOutlet() != null ? b.getOutlet().getName() : "-",
            b.getStatus() != null ? b.getStatus() : "Tidak Diketahui"
        ));
    });

    probations.forEach(p -> {
        result.add(new BaristaAll(
            p.getId().toString(),
            p.getFullName(),
            "Probation",
            p.getOutlet() != null ? p.getOutlet().getName() : "-",
            p.getStatus() != null ? p.getStatus() : "Tidak Diketahui"
        ));
    });

    headBars.forEach(h -> {
        result.add(new BaristaAll(
            h.getId().toString(),
            h.getFullName(),
            "Head Bar",
            h.getOutlet() != null ? h.getOutlet().getName() : "-",
            h.getStatus() != null ? h.getStatus() : "Tidak Diketahui"
        ));
    });

    return result;
}
    
    

    
}