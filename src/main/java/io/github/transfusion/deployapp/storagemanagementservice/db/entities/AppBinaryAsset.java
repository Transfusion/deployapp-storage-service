package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.transfusion.deployapp.storagemanagementservice.services.assets.AppBinaryAssetUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "app_binary_assets")
public class AppBinaryAsset {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "status", length = 15)
    private String status;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "app_binary_id", nullable = false)
    private AppBinary appBinary;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    @Column(name = "asset_value", columnDefinition = "json")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private JsonNode value;

    public AppBinary getAppBinary() {
        return appBinary;
    }

    public void setAppBinary(AppBinary appBinary) {
        this.appBinary = appBinary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public boolean isPrivate() {
        return AppBinaryAssetUtils.isAssetPrivate(getType());
    }
}