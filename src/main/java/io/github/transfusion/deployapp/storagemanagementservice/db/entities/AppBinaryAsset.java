package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "app_binary_assets")
public class AppBinaryAsset {
    @Id
    @Column(name = "app_binary_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "app_binary_id", nullable = false)
    private AppBinary appBinary;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

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

/*
    TODO [JPA Buddy] create field to map the 'value' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "value", columnDefinition = "json not null")
    private Object value;
*/
}