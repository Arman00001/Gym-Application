package com.epam.gymapp.util;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadClient {

    private final RestTemplate restTemplate;

    @Value("${trainer-workload-service.url}")
    private String trainerWorkloadServiceUrl;

    @Value("${trainer-workload-service.user}")
    private String username;

    @Value("${trainer-workload-service.password}")
    private String password;

    public void sendTrainerWorkload(TrainerActionDto request) {
        String url = trainerWorkloadServiceUrl + "/trainer-workloads";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        HttpEntity<TrainerActionDto> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(url, entity, Void.class);    }
}