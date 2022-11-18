package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApkRepository extends JpaRepository<Apk, UUID> {
}