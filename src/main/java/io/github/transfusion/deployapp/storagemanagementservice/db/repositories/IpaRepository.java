package io.github.transfusion.deployapp.storagemanagementservice.db.repositories;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IpaRepository extends JpaRepository<Ipa, UUID> {
}