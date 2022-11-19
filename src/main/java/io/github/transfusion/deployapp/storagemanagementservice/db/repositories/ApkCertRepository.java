package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.ApkCert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ApkCertRepository extends JpaRepository<ApkCert, UUID> {
    @Transactional
    List<ApkCert> findAllByAppBinaryId(UUID id);
}