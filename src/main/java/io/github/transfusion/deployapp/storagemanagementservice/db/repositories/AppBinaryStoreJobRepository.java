package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface AppBinaryStoreJobRepository extends JpaRepository<AppBinaryStoreJob, UUID> {
    List<AppBinaryStoreJob> findByAppBinary_UserId(UUID id);

    List<AppBinaryStoreJob> findByAppBinary_UserIdIn(UUID... id);

    @Modifying
    @Transactional
    @Query("UPDATE AppBinaryStoreJob j SET j.status = :newStatus where j.status = :oldStatus")
    int bulkUpdateStatus(@Param("oldStatus") AppBinaryInitialStoreService.InitialStoreStatus oldStatus,
                          @Param("newStatus") AppBinaryInitialStoreService.InitialStoreStatus newStatus);
}