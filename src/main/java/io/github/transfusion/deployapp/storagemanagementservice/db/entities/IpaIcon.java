package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ipa_icons")
public class IpaIcon {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private Ipa ipa;

    @Column(name = "name", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String name;

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

/*
    TODO [JPA Buddy] create field to map the 'dimensions' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "dimensions", columnDefinition = "json not null")
    private Object dimensions;
*/
}