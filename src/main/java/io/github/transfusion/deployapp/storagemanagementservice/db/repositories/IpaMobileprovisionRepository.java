package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.IpaMobileprovision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IpaMobileprovisionRepository extends JpaRepository<IpaMobileprovision, UUID> {
    long deleteByAppBinaryId(UUID appBinaryId);
}