package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "apk")
@PrimaryKeyJoinColumn(name = "id")
public class Apk extends AppBinary {
    public static final String IDENTIFIER = "APK";
//    @Id
//    @Column(name = "id", nullable = false)
//    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private AppBinary appBinary;

    @Column(name = "min_sdk_version", nullable = false, length = 10)
    private String minSdkVersion;

    @Column(name = "min_os_version", nullable = false, length = 10)
    private String minOsVersion;

    @Column(name = "target_sdk_version", nullable = false, length = 10)
    private String targetSdkVersion;

    @Column(name = "wear", nullable = false)
    private Boolean wear = false;

    @Column(name = "tv", nullable = false)
    private Boolean tv = false;

    @Column(name = "automotive", nullable = false)
    private Boolean automotive = false;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;

    @Column(name = "manifest_xml", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String manifestXml;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean getAutomotive() {
        return automotive;
    }

    public void setAutomotive(Boolean automotive) {
        this.automotive = automotive;
    }

    public Boolean getTv() {
        return tv;
    }

    public void setTv(Boolean tv) {
        this.tv = tv;
    }

    public Boolean getWear() {
        return wear;
    }

    public void setWear(Boolean wear) {
        this.wear = wear;
    }

    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public void setTargetSdkVersion(String targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }

    public String getMinOsVersion() {
        return minOsVersion;
    }

    public void setMinOsVersion(String minOsVersion) {
        this.minOsVersion = minOsVersion;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }

    public AppBinary getAppBinary() {
        return appBinary;
    }

    public void setAppBinary(AppBinary appBinary) {
        this.appBinary = appBinary;
    }


    public List<String> getUseFeatures() {
        return useFeatures;
    }

    public void setUseFeatures(List<String> useFeatures) {
        this.useFeatures = useFeatures;
    }

    public List<String> getUsePermissions() {
        return usePermissions;
    }

    public void setUsePermissions(List<String> usePermissions) {
        this.usePermissions = usePermissions;
    }

    public List<String> getDeepLinks() {
        return deepLinks;
    }

    public void setDeepLinks(List<String> deepLinks) {
        this.deepLinks = deepLinks;
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    @Column(name = "use_features", columnDefinition = "json")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> useFeatures;

    @Column(name = "use_permissions", columnDefinition = "json")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> usePermissions;


    @Column(name = "deep_links", columnDefinition = "json")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> deepLinks;

    @Column(name = "schemes", columnDefinition = "json")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> schemes;

    public String getManifestXml() {
        return manifestXml;
    }

    public void setManifestXml(String manifestXml) {
        this.manifestXml = manifestXml;
    }
}