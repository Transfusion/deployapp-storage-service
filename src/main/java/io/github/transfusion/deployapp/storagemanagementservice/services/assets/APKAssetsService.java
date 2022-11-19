package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.ApkCert;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.ApkCertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class APKAssetsService {
    Logger logger = LoggerFactory.getLogger(APKAssetsService.class);

    @Autowired
    private ApkCertRepository apkCertRepository;

    public List<ApkCert> getApkCerts(UUID appBinaryId) {
        return apkCertRepository.findAllByAppBinaryId(appBinaryId);
    }
}
