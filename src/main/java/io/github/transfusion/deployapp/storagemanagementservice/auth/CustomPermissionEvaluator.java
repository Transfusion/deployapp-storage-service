package io.github.transfusion.deployapp.storagemanagementservice.auth;

import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private AppBinaryRepository appBinaryRepository;

    private boolean checkAppBinaryRead(Authentication authentication, UUID id) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            // TODO: anonymous uploads
            return false;
        }
        // TODO: organizations
        UUID userId = ((CustomUserPrincipal) authentication.getPrincipal()).getId();

        Optional<AppBinary> _appBinary = appBinaryRepository.findById(id);
        if (_appBinary.isEmpty()) throw new ResourceNotFoundException("AppBinary", "id", id);
        AppBinary appBinary = _appBinary.get();
        return appBinary.getUserId().equals(userId);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (permission.equals("APPBINARY_READ")) {
            return checkAppBinaryRead(authentication, (UUID) targetDomainObject);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
