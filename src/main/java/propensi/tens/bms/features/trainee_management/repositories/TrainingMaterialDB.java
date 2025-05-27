package propensi.tens.bms.features.trainee_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import propensi.tens.bms.features.trainee_management.models.TrainingMaterial;

public interface TrainingMaterialDB extends JpaRepository<TrainingMaterial, Long>  {
    
}
