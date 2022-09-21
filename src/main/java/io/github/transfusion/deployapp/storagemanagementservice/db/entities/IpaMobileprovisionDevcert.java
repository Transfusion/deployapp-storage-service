package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ipa_mobileprovision_devcert")
public class IpaMobileprovisionDevcert {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private IpaMobileprovision ipaMobileprovision;

    @Column(name = "name", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "expired_date", nullable = false)
    private Instant expiredDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public IpaMobileprovision getIpaMobileprovision() {
        return ipaMobileprovision;
    }

    public void setIpaMobileprovision(IpaMobileprovision ipaMobileprovision) {
        this.ipaMobileprovision = ipaMobileprovision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}