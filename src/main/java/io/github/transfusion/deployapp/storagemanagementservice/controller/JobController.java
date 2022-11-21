package io.github.transfusion.deployapp.storagemanagementservice.controller;

import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryJobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.JobNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tags(@Tag(name = "Background jobs-related controller"))
@RequestMapping("/api/v1/job")
public class JobController {

    Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private AppBinaryJobService appBinaryJobService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'APPBINARYJOB_EDIT')")
    public ResponseEntity<Void> cancelJob(@PathVariable("id") UUID id) {
        try {
            jobScheduler.delete(id);
            appBinaryJobService.deleteJobSilent(id);
        } catch (JobNotFoundException e) {
            appBinaryJobService.deleteJobSilent(id);
            logger.error(String.format("Job with ID %s does not exist", id));
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }
}
