package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ipa_plugin")
public class IpaPlugin {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private Ipa ipa;

    @Column(name = "display_name")
    @Type(type = "org.hibernate.type.TextType")
    private String displayName;

    @Column(name = "bundle_name")
    @Type(type = "org.hibernate.type.TextType")
    private String bundleName;

    @Column(name = "release_version")
    @Type(type = "org.hibernate.type.TextType")
    private String releaseVersion;

    @Column(name = "build_version")
    @Type(type = "org.hibernate.type.TextType")
    private String buildVersion;

    @Column(name = "identifier")
    @Type(type = "org.hibernate.type.TextType")
    private String identifier;

    @Column(name = "bundle_id")
    @Type(type = "org.hibernate.type.TextType")
    private String bundleId;

    @Column(name = "min_sdk_version", length = 10)
    private String minSdkVersion;

    @Column(name = "device_type", length = 10)
    private String deviceType;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "lib", nullable = false)
    private Boolean lib = false;

    @Column(name = "stored", nullable = false)
    private Boolean stored = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Ipa getIpa() {
        return ipa;
    }

    public void setIpa(Ipa ipa) {
        this.ipa = ipa;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLib() {
        return lib;
    }

    public void setLib(Boolean lib) {
        this.lib = lib;
    }

    public Boolean getStored() {
        return stored;
    }

    public void setStored(Boolean stored) {
        this.stored = stored;
    }

}