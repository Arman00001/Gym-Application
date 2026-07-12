package com.epam.gymapp.workload;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainer-workload-service")
public interface WorkloadServiceClient {

    @PostMapping("/trainer-workloads")
    void updateTrainerWorkload(@RequestBody TrainerActionDto request);
}