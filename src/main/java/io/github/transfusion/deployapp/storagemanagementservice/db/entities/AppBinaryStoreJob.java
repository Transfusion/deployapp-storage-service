package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_binary_store_jobs")
public class AppBinaryStoreJob {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private AppBinary appBinary;

//    @Size(max = 10)
//    @NotNull
//    @Column(name = "status", nullable = false, length = 10)
//    private String status;

    public boolean isSuccessful() {
        return getStatus().equals(AppBinaryInitialStoreService.InitialStoreStatus.SUCCESSFUL);
    }

    public boolean isProcessing() {
        return getStatus().equals(AppBinaryInitialStoreService.InitialStoreStatus.PROCESSING);
    }

    public boolean isCancelling() {
        return getStatus().equals(AppBinaryInitialStoreService.InitialStoreStatus.CANCELLING);
    }

    public boolean isAborted() {
        return getStatus().equals(AppBinaryInitialStoreService.InitialStoreStatus.ABORTED);
    }

    public AppBinaryInitialStoreService.InitialStoreStatus getStatus() {
        return status;
    }

    public void setStatus(AppBinaryInitialStoreService.InitialStoreStatus status) {
        this.status = status;
    }

    private AppBinaryInitialStoreService.InitialStoreStatus status;


    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AppBinary getAppBinary() {
        return appBinary;
    }

    public void setAppBinary(AppBinary appBinary) {
        this.appBinary = appBinary;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}