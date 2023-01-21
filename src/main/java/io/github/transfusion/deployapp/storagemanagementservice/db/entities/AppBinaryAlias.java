package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "app_binary_alias")
public class AppBinaryAlias {
    @Id
    @Size(max = 50)
    @Column(name = "alias", nullable = false, length = 50)
    private String id;

    @NotNull
    @Column(name = "app_binary_id", nullable = false)
    private UUID appBinaryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getAppBinaryId() {
        return appBinaryId;
    }

    public void setAppBinaryId(UUID appBinaryId) {
        this.appBinaryId = appBinaryId;
    }

}