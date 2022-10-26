package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.IpaMobileprovision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface IpaMobileprovisionRepository extends JpaRepository<IpaMobileprovision, UUID> {
    @Transactional
    long deleteByAppBinaryId(UUID appBinaryId);
    @Transactional
    List<IpaMobileprovision> findAllByAppBinaryId(UUID id);
}