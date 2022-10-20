package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppBinaryJobRepository extends JpaRepository<AppBinaryJob, UUID> {
    public List<AppBinaryJob> findByAppBinaryId(UUID id);
}