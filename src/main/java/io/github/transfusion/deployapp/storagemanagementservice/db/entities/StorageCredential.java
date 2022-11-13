package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "storage_credentials")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class StorageCredential {
    @Id
    @Column(name = "id", nullable = false)
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
//    https://stackoverflow.com/questions/46821002/spring-crudrepository-save-with-specific-id
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "organization_id")
//    private Organization organization;

    @Column(name = "created_on", nullable = false)
    private Instant createdOn;

    @Column(name = "checked_on", nullable = false)
    private Instant checkedOn;

    @Column(name = "last_used")
    private Instant lastUsed;

    @Column(name = "status")
    private String status;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "organization_id")
    private UUID organizationId;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getCheckedOn() {
        return checkedOn;
    }

    public void setCheckedOn(Instant checkedOn) {
        this.checkedOn = checkedOn;
    }

    public Instant getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Instant lastUsed) {
        this.lastUsed = lastUsed;
    }

//    public Organization getOrganization() {
//        return organization;
//    }
//
//    public void setOrganization(Organization organization) {
//        this.organization = organization;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // use the visitor pattern
    @JsonIgnore
    public abstract IUploader resolveUploader(IUploaderResolver resolver);

    @JsonIgnore
    public abstract IDownloader resolveDownloader(IDownloaderResolver resolver);

    @JsonIgnore
    public abstract IURLGetter resolveURLGetter(IURLGetterResolver resolver);

    @JsonIgnore
    public abstract IDeleter resolveDeleter(IDeleterResolver resolver);
}