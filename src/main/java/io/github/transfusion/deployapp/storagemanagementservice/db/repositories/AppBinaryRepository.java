package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface AppBinaryRepository extends JpaRepository<AppBinary, UUID>,
        JpaSpecificationExecutor<AppBinary> {
//    Page<AppBinary> findByUserId(UUID userId, Specification<AppBinary> specification, Pageable pageable);

    @Transactional
    long deleteByStorageCredential(UUID storageCredentialId);

    @Modifying
    @Transactional
    @Query("UPDATE AppBinary s set s.userId = :userId WHERE s.id in :ids")
    int migrateAnonymousAppBinaries(UUID userId, Collection<UUID> ids);
}