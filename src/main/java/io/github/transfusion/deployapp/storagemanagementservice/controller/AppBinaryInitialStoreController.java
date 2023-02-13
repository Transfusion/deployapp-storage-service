package io.github.transfusion.deployapp.storagemanagementservice.controller;

import io.github.transfusion.deployapp.dto.internal.CancelInitialStoreJobFutureMessage;
import io.github.transfusion.deployapp.dto.response.AppBinaryStoreJobDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryStoreJobMapper;
import io.github.transfusion.deployapp.storagemanagementservice.messaging.FanoutEventsSender;
import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/app")
public class AppBinaryInitialStoreController {

    @Autowired
    private AppBinaryInitialStoreService appBinaryInitialStoreService;

    @Autowired
    private AppBinaryStoreJobMapper appBinaryStoreJobMapper;

    @GetMapping("/initial_storage_jobs")
    public ResponseEntity<List<AppBinaryStoreJobDTO>> getInitialStorageJobs(@RequestParam(required = false) String organizationId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            List<AppBinaryStoreJob> jobs = appBinaryInitialStoreService.findOwnJobsAnonymous();
            jobs.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
            // ignore organizationId, since organizations are a logged-in feature
            return new ResponseEntity<>(jobs.stream().map(appBinaryStoreJobMapper::toDTO).collect(Collectors.toList()), HttpStatus.OK);
        }

        if (organizationId == null) {
            List<AppBinaryStoreJob> jobs = appBinaryInitialStoreService.findOwnJobs();
            jobs.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
            return new ResponseEntity<>(jobs
                    .stream().map(appBinaryStoreJobMapper::toDTO).collect(Collectors.toList()), HttpStatus.OK);
        } else {
            throw new NotImplementedException("Organization support isn't available yet.");
        }
    }

    @Autowired
    private FanoutEventsSender fanoutEventsSender;

    @PostMapping("/initial_storage_jobs/{id}/cancel")
    public ResponseEntity<Void> cancelStorageJob(@PathVariable("id") UUID id) {
        appBinaryInitialStoreService.cancelStoreAppBinary(id); // mark in db first
        fanoutEventsSender.send(String.format("dpl-%s", id), new CancelInitialStoreJobFutureMessage(id));
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/initial_storage_jobs/{id}")
    @Operation(summary = "Dismisses a job in SUCCESSFUL or CANCELLING state.")
    public ResponseEntity<Void> deleteStorageJob(@PathVariable("id") UUID id) {
        appBinaryInitialStoreService.deleteStoreAppBinary(id);
        return ResponseEntity.ok(null);
    }
}
