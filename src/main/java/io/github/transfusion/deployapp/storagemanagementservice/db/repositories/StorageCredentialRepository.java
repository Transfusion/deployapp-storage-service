package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageCredentialRepository extends JpaRepository<StorageCredential, UUID> {
}