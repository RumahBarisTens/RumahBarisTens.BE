package propensi.tens.bms.features.trainee_management.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.trainee_management.dto.request.TrainingMaterialRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.TrainingMaterialResponseDTO;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;
import propensi.tens.bms.features.trainee_management.models.TrainingMaterial;
import propensi.tens.bms.features.trainee_management.repositories.TrainingMaterialDB;

@Service
@Transactional
public class TrainingMaterialServiceImpl implements TrainingMaterialService {

    @Autowired
    private TrainingMaterialDB trainingMaterialDb;

    @Autowired private NotificationService notificationService;

    @Override
    public TrainingMaterialResponseDTO createTrainingMaterial(TrainingMaterialRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Judul materi wajib diisi.");
        }
        if (request.getLink() == null || request.getLink().trim().isEmpty()) {
            throw new RuntimeException("Link materi wajib diisi.");
        }
        if (request.getAssignedRoles() == null || request.getAssignedRoles().isEmpty()) {
            throw new RuntimeException("Setidaknya assign materi ke satu role.");
        }

        TrainingMaterial trainingMaterial = new TrainingMaterial();
        trainingMaterial.setTitle(request.getTitle());
        trainingMaterial.setLink(request.getLink());
        trainingMaterial.setDescription(request.getDescription());
        trainingMaterial.setCreatedAt(new Date());

        trainingMaterial.setAssignedRoles(new HashSet<>(request.getAssignedRoles()));

        TrainingMaterial saved = trainingMaterialDb.save(trainingMaterial);
        notificationService.notifyTrainingMaterialAssigned(saved.getId(), saved.getAssignedRoles());
        return toResponse(saved);
    }

    @Override
    public List<TrainingMaterialResponseDTO> getAllTrainingMaterials() {
        List<TrainingMaterial> list = trainingMaterialDb.findAll();
        List<TrainingMaterialResponseDTO> responseList = new ArrayList<>();
        for(TrainingMaterial tm : list){
            responseList.add(toResponse(tm));
        }
        return responseList;
    }

    @Override
    public TrainingMaterialResponseDTO getTrainingMaterialDetail(Long id) {
        Optional<TrainingMaterial> opt = trainingMaterialDb.findById(id);
        if(opt.isPresent()){
            return toResponse(opt.get());
        } else {
            throw new RuntimeException("Materi pelatihan tidak ditemukan.");
        }
    }

    @Override
    public List<TrainingMaterialResponseDTO> getAssignedMaterialsForProbationBarista() {
        return trainingMaterialDb.findAll()
            .stream()
            .filter(tm -> tm.getAssignedRoles().contains(AssignedRole.PROBATION_BARISTA))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TrainingMaterialResponseDTO updateTrainingMaterial(Long id, TrainingMaterialRequestDTO request) {
        TrainingMaterial tm = trainingMaterialDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Materi pelatihan tidak ditemukan."));
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Judul materi wajib diisi.");
        }
        if (request.getLink() == null || request.getLink().trim().isEmpty()) {
            throw new RuntimeException("Link materi wajib diisi.");
        }
        if (request.getAssignedRoles() == null || request.getAssignedRoles().isEmpty()) {
            throw new RuntimeException("Setidaknya assign materi ke satu role.");
        }
        tm.setTitle(request.getTitle());
        tm.setLink(request.getLink());
        tm.setDescription(request.getDescription());
        tm.setAssignedRoles(new java.util.HashSet<>(request.getAssignedRoles()));
        TrainingMaterial updated = trainingMaterialDb.save(tm);
        return toResponse(updated);
    }

    @Override
    public void deleteTrainingMaterial(Long id) {
        TrainingMaterial tm = trainingMaterialDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Materi pelatihan tidak ditemukan."));
        trainingMaterialDb.delete(tm);
    }

    private TrainingMaterialResponseDTO toResponse(TrainingMaterial entity) {
        TrainingMaterialResponseDTO response = new TrainingMaterialResponseDTO();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setLink(entity.getLink());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setAssignedRoles(new ArrayList<>(entity.getAssignedRoles()));

        return response;
    }

}

