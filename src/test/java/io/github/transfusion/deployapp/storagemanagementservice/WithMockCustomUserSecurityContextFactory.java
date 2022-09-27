package io.github.transfusion.deployapp.storagemanagementservice;

import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.*;
import java.util.stream.Collectors;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> grantedAuthorities = Arrays.stream(customUser.authorities()).map(a -> (GrantedAuthority) () -> a).collect(Collectors.toList());
        CustomUserPrincipal principal =
                new CustomUserPrincipal(UUID.fromString(customUser.id()),
                        customUser.username(), customUser.email(),
                        customUser.password(), customUser.name(),
                        customUser.accountVerified(),
                        grantedAuthorities);

        Authentication auth =
                new OAuth2AuthenticationToken(principal, principal.getAuthorities(),
                        customUser.oAuth2registrationId());

        context.setAuthentication(auth);
        return context;
    }
}