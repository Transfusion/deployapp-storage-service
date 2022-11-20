package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppBinaryDownloadRepository extends JpaRepository<AppBinaryDownload, UUID> {
    Page<AppBinaryDownload> findByAppBinaryId(UUID appBinaryId, Pageable page);
}