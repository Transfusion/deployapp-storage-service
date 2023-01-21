package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AppBinaryAliasRepository extends JpaRepository<AppBinaryAlias, String> {
    List<AppBinaryAlias> findByAppBinaryId(UUID id);

    @Transactional
    long deleteByAppBinaryIdAndId(UUID appBinaryId, String id);
}