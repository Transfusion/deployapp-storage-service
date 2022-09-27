package io.github.transfusion.deployapp.storagemanagementservice;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String id() default "f75d8115-1771-4815-b51c-753844dbe62e";

    String username() default "foo";

    String email() default "foo@bar.com";

    String password() default "foobar";

    String name() default "Foo Bar";

    boolean accountVerified() default true;

    String[] authorities() default {"ROLE_USER"};

    String oAuth2registrationId() default "google";
}