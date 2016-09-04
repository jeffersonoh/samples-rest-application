package br.com.sample.spring.authenticator.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.stereotype.Component;

/**
 * Implementação em memória do repositório de tokens
 * gerenciados.
 * <p>
 * Baseado no {@link InMemoryTokenStore} mas estende as 
 * funcionalidades definidas em {@link ManagedTokenStore}.
 * 
 * @author hugo.junior
 */
@Component
public class InMemoryManagedTokenStore extends InMemoryTokenStore implements ManagedTokenStore {
	
	private static final Logger LOG = LoggerFactory.getLogger(InMemoryManagedTokenStore.class);
	
	Map<String, OAuth2AccessToken> tokensMap = new HashMap<>();
	Map<String, Collection<OAuth2AccessToken>> tokensPerUser = new HashMap<>();

	@Override
	public int countTokens() {
		return super.getAccessTokenCount();
	}
	
	@Override
	public int countTokensByClientId(String clientId) {
		return super.findTokensByClientId(clientId).size();
	}

	@Override
	public int countTokensByClientIdAndUserName(String clientId, String userName) {
		return super.findTokensByClientIdAndUserName(clientId, userName).size();
	}

	@Override
	public int countTokensByUserName(String userName) {
		return findTokensByUserName(userName).size();
	}

	@Override
	public Collection<OAuth2AccessToken> findAllTokens() {
		return tokensMap.values();
	}
	
	@Override
	public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
		if (tokensPerUser.containsKey(userName)) {
			return tokensPerUser.get(userName);
		}
		return Collections.emptySet();
	}
	
	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		LOG.debug("Storing token '{}' for '{}'.", token, authentication.getName());
		String tokenValue = token.getValue();
		tokensMap.put(tokenValue, token);
		
		// If token is for an user (not client credentials)
		if (authentication.getUserAuthentication() != null) {
			String username = authentication.getUserAuthentication().getName();
			Collection<OAuth2AccessToken> userTokens = tokensPerUser.get(username);
			if (userTokens == null) {
				userTokens = new ArrayList<>();
				tokensPerUser.put(username, userTokens);
			}
			userTokens.add(token);
		}
		
		super.storeAccessToken(token, authentication);
	}

	@Override
	public void removeAccessToken(String tokenValue) {
		OAuth2Authentication authentication = super.readAuthentication(tokenValue);
		LOG.debug("Removing token '{}' of user '{}'", tokenValue, authentication != null ? authentication.getName() : "");
		
		OAuth2AccessToken token = tokensMap.remove(tokenValue);
		if (authentication != null && authentication.getUserAuthentication() != null) {
			String username = authentication.getUserAuthentication().getName();
			tokensPerUser.get(username).remove(token);
		}
		
		super.removeAccessToken(tokenValue);
	}
	
	@Override
	public void clear() {
		LOG.debug("Clearing all tokens...");
		tokensMap.clear();
		tokensPerUser.clear();
		super.clear();
	}
	
}
