package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
}
