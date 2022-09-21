package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "apk")
@PrimaryKeyJoinColumn(name = "id")
public class Apk extends AppBinary {
//    @Id
//    @Column(name = "id", nullable = false)
//    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private AppBinary appBinary;

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

}