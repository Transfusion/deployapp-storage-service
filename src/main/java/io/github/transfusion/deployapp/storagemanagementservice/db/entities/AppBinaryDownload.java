package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_binary_downloads")
public class AppBinaryDownload {
    @Id
    @Column(name = "app_binary_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "app_binary_id", nullable = false)
    private AppBinary appBinary;

    @Column(name = "\"timestamp\"", nullable = false)
    private Instant timestamp;

    @Column(name = "ip", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String ip;

    @Column(name = "ua", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String ua;

    @Column(name = "os", nullable = false, length = 10)
    private String os;

    @Column(name = "version", nullable = false, length = 10)
    private String version;

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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}