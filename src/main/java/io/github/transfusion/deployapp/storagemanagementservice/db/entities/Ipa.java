package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.transfusion.deployapp.storagemanagementservice.db.converters.JsonStringListConverter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ipa")
@PrimaryKeyJoinColumn(name = "id")
public class Ipa extends AppBinary {
//    @Id
//    @Column(name = "id", nullable = false)
//    private UUID id;

//    @MapsId
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "id", nullable = false)
//    private AppBinary appBinary;

    @Column(name = "min_sdk_version", nullable = false, length = 10)
    private String minSdkVersion;

    @Column(name = "iphone", nullable = false)
    private Boolean iphone = false;

    @Column(name = "ipad", nullable = false)
    private Boolean ipad = false;

    @Column(name = "universal", nullable = false)
    private Boolean universal = false;

    @Column(name = "device_type", length = 10)
    private String deviceType;

    @Column(name = "display_name")
    @Type(type = "org.hibernate.type.TextType")
    private String displayName;

    @Column(name = "release_type", length = 15)
    private String releaseType;

    @Column(name = "build_type", length = 15)
    private String buildType;

    @Column(name = "expired_date")
    private Instant expiredDate;

    @Column(name = "team_name")
    @Type(type = "org.hibernate.type.TextType")
    private String teamName;
//    @Column(name = "name")
//    @Type(type = "org.hibernate.type.TextType")
//    private String name;

    @Column(name = "archs", columnDefinition = "json not null")
//    @Convert(converter = JsonStringListConverter.class)
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> archs;

    @Column(name = "devices", columnDefinition = "json")
//    @Convert(converter = JsonStringListConverter.class)
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private List<String> devices;

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public List<String> getArchs() {
        return archs;
    }

    public void setArchs(List<String> archs) {
        this.archs = archs;
    }

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }

//    public AppBinary getAppBinary() {
//        return appBinary;
//    }
//
//    public void setAppBinary(AppBinary appBinary) {
//        this.appBinary = appBinary;
//    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public Boolean getIphone() {
        return iphone;
    }

    public void setIphone(Boolean iphone) {
        this.iphone = iphone;
    }

    public Boolean getIpad() {
        return ipad;
    }

    public void setIpad(Boolean ipad) {
        this.ipad = ipad;
    }

    public Boolean getUniversal() {
        return universal;
    }

    public void setUniversal(Boolean universal) {
        this.universal = universal;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }

   /* public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/


    @Column(name = "plist_json", columnDefinition = "json not null")
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    private JsonNode plistJson;

    public void setPlistJson(JsonNode plistJson) {
        this.plistJson = plistJson;
    }

    public JsonNode getPlistJson() {
        return plistJson;
    }

}