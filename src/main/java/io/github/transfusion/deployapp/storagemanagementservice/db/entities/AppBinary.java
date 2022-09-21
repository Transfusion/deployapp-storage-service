package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_binary")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AppBinary {

    // do the generation ourselves
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "version", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String version;

    @Column(name = "build", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String build;

    @Column(name = "upload_date", nullable = false)
    private Instant uploadDate;

    @Column(name = "name", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "last_install_date")
    private Instant lastInstallDate;

    @Column(name = "identifier", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String identifier;

    @Column(name = "assets_on_front_page", nullable = false)
    private Boolean assetsOnFrontPage = false;

    @Column(name = "size_bytes", nullable = false)
    private BigDecimal sizeBytes;

    @Column(name = "file_name", nullable = false, length = 50)
    private String fileName;

    @Column(name = "storage_credential")
    private UUID storageCredential;

    public UUID getStorageCredential() {
        return storageCredential;
    }

    public void setStorageCredential(UUID storageCredential) {
        this.storageCredential = storageCredential;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastInstallDate() {
        return lastInstallDate;
    }

    public void setLastInstallDate(Instant lastInstallDate) {
        this.lastInstallDate = lastInstallDate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getAssetsOnFrontPage() {
        return assetsOnFrontPage;
    }

    public void setAssetsOnFrontPage(Boolean assetsOnFrontPage) {
        this.assetsOnFrontPage = assetsOnFrontPage;
    }

    public BigDecimal getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(BigDecimal sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

}