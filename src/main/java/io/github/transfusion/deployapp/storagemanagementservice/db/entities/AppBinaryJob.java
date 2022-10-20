package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_binary_jobs")
public class AppBinaryJob {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "app_binary_id", nullable = false)
    private UUID appBinaryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAppBinaryId() {
        return appBinaryId;
    }

    public void setAppBinaryId(UUID appBinaryId) {
        this.appBinaryId = appBinaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}