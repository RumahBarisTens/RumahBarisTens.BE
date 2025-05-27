package propensi.tens.bms.features.trainee_management.services;

import java.util.List;

import propensi.tens.bms.features.trainee_management.dto.request.TrainingMaterialRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.TrainingMaterialResponseDTO;

public interface TrainingMaterialService {
    TrainingMaterialResponseDTO createTrainingMaterial(TrainingMaterialRequestDTO request);
    List<TrainingMaterialResponseDTO> getAllTrainingMaterials();
    TrainingMaterialResponseDTO getTrainingMaterialDetail(Long id);
    List<TrainingMaterialResponseDTO> getAssignedMaterialsForProbationBarista();
    TrainingMaterialResponseDTO updateTrainingMaterial(Long id, TrainingMaterialRequestDTO request);
    void deleteTrainingMaterial(Long id);
}

