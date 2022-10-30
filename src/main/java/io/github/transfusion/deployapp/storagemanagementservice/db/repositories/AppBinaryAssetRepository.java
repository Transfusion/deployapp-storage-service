package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppBinaryAssetRepository extends JpaRepository<AppBinaryAsset, UUID> {
    @Transactional
    long deleteByAppBinaryIdAndType(UUID appBinaryId, String type);

    List<AppBinaryAsset> findByAppBinaryId(UUID id);

    List<AppBinaryAsset> findByAppBinaryIdAndType(UUID id, String type);
}