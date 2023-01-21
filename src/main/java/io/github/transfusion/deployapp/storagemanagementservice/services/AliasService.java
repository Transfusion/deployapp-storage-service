package io.github.transfusion.deployapp.storagemanagementservice.services;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAlias;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryAliasRepository;
import io.github.transfusion.deployapp.utilities.Base62;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AliasService {
    @Autowired
    private AppBinaryAliasRepository appBinaryAliasRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public AppBinaryAlias generateAlias(UUID appBinaryId) {
        long id = sequenceGeneratorService.nextId();
        String str = Base62.fromBase10(id);
        AppBinaryAlias alias = new AppBinaryAlias();
        alias.setId(str);
        alias.setAppBinaryId(appBinaryId);
        return appBinaryAliasRepository.save(alias);
    }

    public List<AppBinaryAlias> getAliases(UUID appBinaryId) {
        return appBinaryAliasRepository.findByAppBinaryId(appBinaryId);
    }

    public boolean deleteAppBinaryAlias(UUID appBinaryId, String alias) {
        return appBinaryAliasRepository.deleteByAppBinaryIdAndId(appBinaryId, alias) > 0;
    }

    public Optional<AppBinaryAlias> getAlias(String alias) {
        return appBinaryAliasRepository.findById(alias);
    }
}
