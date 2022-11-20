package io.github.transfusion.deployapp.storagemanagementservice.services;

import io.github.transfusion.deployapp.dto.response.AppBinaryDownloadDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryDownload;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryDownloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AppBinaryDownloadsService {
    @Autowired
    private AppBinaryDownloadRepository appBinaryDownloadRepository;

    @Autowired
    private AppBinaryService appBinaryService;

    public AppBinaryDownload recordDownload(String userAgent, String addr, UUID appBinaryId) {
        AppBinary binary = appBinaryService.ensureBinaryAvailable(appBinaryId);
        if (!binary.getAvailable())
            throw new AccessDeniedException(String.format("AppBinary with id %s is not available", appBinaryId));

        AppBinaryDownload record = new AppBinaryDownload();
        record.setId(UUID.randomUUID());
        record.setAppBinary(binary);
        record.setIp(addr);
        record.setUa(userAgent);

//         TODO: include a UA parsing library..
//        record.setOs("N/A");
//        record.setVersion("N/A");

        Instant ts = Instant.now();
        record.setTs(ts);
        return appBinaryDownloadRepository.save(record);
    }

    public Page<AppBinaryDownload> getDownloadsPaginated(UUID appBinaryId, Pageable pageable) {
        return appBinaryDownloadRepository.findByAppBinaryId(appBinaryId, pageable);
    }
}
