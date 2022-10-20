package io.github.transfusion.deployapp.storagemanagementservice.services;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryJob;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AppBinaryJobService {
    @Autowired
    private AppBinaryJobRepository repository;

    public List<AppBinaryJob> getJobs(UUID appBinaryId) {
        return repository.findByAppBinaryId(appBinaryId);
    }

    public void deleteJob(UUID... id) {
        repository.deleteAllById(Arrays.asList(id));
    }

    public void deleteJobSilent(UUID... id) {
        try {
            repository.deleteAllById(Arrays.asList(id));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used internally by {@link AppBinaryService}.
     *
     * @param appBinaryId {@link UUID}
     * @param name        Name of the job
     * @param description Description of the job, may be null.
     * @return the saved {@link AppBinaryJob} entity
     */
    public AppBinaryJob createJob(UUID jobId, UUID appBinaryId, String name, String description) {
        AppBinaryJob job = new AppBinaryJob();
        job.setId(jobId);
        job.setAppBinaryId(appBinaryId);
        job.setName(name);
        job.setDescription(description);
        job.setCreatedDate(Instant.now());
        return repository.save(job);
    }
}
