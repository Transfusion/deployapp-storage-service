package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppBinaryAssetRepository extends JpaRepository<AppBinaryAsset, UUID> {
    long deleteByAppBinaryIdAndType(UUID appBinaryId, String type);

    List<AppBinaryAsset> findByAppBinaryId(UUID id);
}