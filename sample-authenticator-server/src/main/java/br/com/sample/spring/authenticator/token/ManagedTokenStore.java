package br.com.sample.spring.authenticator.token;

import java.util.Collection;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Repositório de armazenamento de tokens gerenciados.
 * <p>
 * Baseado no {@link TokenStore} mas estende funcionalidades
 * para contabilizar tokens por usuário.
 * 
 * @author hugo.junior
 */
public interface ManagedTokenStore extends TokenStore {
	
	int countTokens();
	
	int countTokensByUserName(String userName);
	
	int countTokensByClientId(String clientId);
	
	int countTokensByClientIdAndUserName(String clientId, String userName);
	
	Collection<OAuth2AccessToken> findAllTokens();
	
	Collection<OAuth2AccessToken> findTokensByUserName(String userName);

}
