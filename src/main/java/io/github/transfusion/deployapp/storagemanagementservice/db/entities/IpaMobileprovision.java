package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ipa_mobileprovision")
public class IpaMobileprovision {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private Ipa ipa;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "app_name")
    @Type(type = "org.hibernate.type.TextType")
    private String appName;

    @Column(name = "type")
    @Type(type = "org.hibernate.type.TextType")
    private String type;

    @Column(name = "platform")
    @Type(type = "org.hibernate.type.TextType")
    private String platform;

    @Column(name = "team_name")
    @Type(type = "org.hibernate.type.TextType")
    private String teamName;

    @Column(name = "profile_name")
    @Type(type = "org.hibernate.type.TextType")
    private String profileName;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "expired_date")
    private Instant expiredDate;
    @Column(name = "adhoc", nullable = false)
    private Boolean adhoc = false;

    @Column(name = "development", nullable = false)
    private Boolean development = false;
    @Column(name = "enterprise", nullable = false)
    private Boolean enterprise = false;

    @Column(name = "appstore", nullable = false)
    private Boolean appstore = false;
    @Column(name = "inhouse", nullable = false)
    private Boolean inhouse = false;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Boolean getDevelopment() {
        return development;
    }

    public void setDevelopment(Boolean development) {
        this.development = development;
    }

    public Boolean getAppstore() {
        return appstore;
    }

    public void setAppstore(Boolean appstore) {
        this.appstore = appstore;
    }

    public Boolean getAdhoc() {
        return adhoc;
    }

    public void setAdhoc(Boolean adhoc) {
        this.adhoc = adhoc;
    }

    public Boolean getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Boolean enterprise) {
        this.enterprise = enterprise;
    }

    public Boolean getInhouse() {
        return inhouse;
    }

    public void setInhouse(Boolean inhouse) {
        this.inhouse = inhouse;
    }

/*
    TODO [JPA Buddy] create field to map the 'platforms' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "platforms", columnDefinition = "json")
    private Object platforms;
*/
/*
    TODO [JPA Buddy] create field to map the 'devices' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "devices", columnDefinition = "json")
    private Object devices;
*/
/*
    TODO [JPA Buddy] create field to map the 'team_identifier' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "team_identifier", columnDefinition = "json")
    private Object teamIdentifier;
*/
/*
    TODO [JPA Buddy] create field to map the 'enabled_capabilities' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "enabled_capabilities", columnDefinition = "json")
    private Object enabledCapabilities;
*/
}