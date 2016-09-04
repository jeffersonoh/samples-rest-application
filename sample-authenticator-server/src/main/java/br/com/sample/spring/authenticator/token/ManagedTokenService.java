package br.com.sample.spring.authenticator.token;

import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

public interface ManagedTokenService extends AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices {

}