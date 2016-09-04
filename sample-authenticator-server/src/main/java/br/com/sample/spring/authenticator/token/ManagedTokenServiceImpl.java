package br.com.sample.spring.authenticator.token;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * Serviço de gerenciamento de tokens com controle de limite.
 * <p>
 * Baseado no {@link DefaultTokenServices} mas que utiliza
 * um {@link ManagedTokenStore} para contabilizar os tokens 
 * armazenados. 
 *  
 * @author hugo.junior
 */
@Component
public class ManagedTokenServiceImpl extends DefaultTokenServices implements ManagedTokenService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ManagedTokenServiceImpl.class);
	
	@Autowired
	private ManagedTokenStore managedTokenStore;
	
	@Autowired
	private ManagedTokenProperties properties;
	
	public ManagedTokenServiceImpl() {
		setSupportRefreshToken(true);
		setReuseRefreshToken(true);
	}
	
	@Override
	public void setTokenStore(TokenStore tokenStore) {
		if (!(tokenStore instanceof ManagedTokenStore)) {
			throw new IllegalArgumentException("Only ManagedTokenStore is allowed on ManagedTokenService.");
		}
		this.setTokenStore((ManagedTokenStore) tokenStore);
	}
	
	@Autowired
	public void setTokenStore(ManagedTokenStore managedTokenStore) {
		this.managedTokenStore = managedTokenStore;
		super.setTokenStore(managedTokenStore);
	}
	
	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException 
	{
		String clientId = authentication.getOAuth2Request().getClientId();
		LOG.debug("Creating access token for user '{}' on client '{}'...", authentication.getName(), clientId);
		
		if (properties.getMaxTokens() > 0 && managedTokenStore.countTokens() >= properties.getMaxTokens()) {
			LOG.warn("Limit of tokens exceeded. Current: {} | Max: {}", managedTokenStore.countTokens(), properties.getMaxTokens());
			throw new ManagedTokenException("Cannot create access tokens. Limit of tokens exceeded.");
		}
		
		if (properties.getMaxTokensPerClient() > 0 && managedTokenStore.countTokensByClientId(clientId) >= properties.getMaxTokensPerClient()) {
			LOG.warn("Limit of tokens for client '{}' exceeded. Current: {} | Max: {}", clientId, managedTokenStore.countTokensByClientId(clientId), properties.getMaxTokensPerClient());
			throw new ManagedTokenException("Cannot create access tokens. Limit of tokens for client exceeded.");
		}
		
		if (authentication.getUserAuthentication() != null) {
			String userName = authentication.getUserAuthentication().getName();
		
			if (properties.getMaxTokensPerUser() > 0 && managedTokenStore.countTokensByUserName(userName) >= properties.getMaxTokensPerUser()) {
				LOG.warn("Limit of tokens for user '{}' exceeded. Current: {} | Max: {}", userName, managedTokenStore.countTokensByUserName(userName), properties.getMaxTokensPerUser());
				throw new ManagedTokenException("Cannot create access tokens. Limit of tokens for user exceeded.");
			}
			
			if (properties.getMaxTokensPerClientPerUser() > 0 && managedTokenStore.countTokensByClientIdAndUserName(clientId, userName) >= properties.getMaxTokensPerClientPerUser()) {
				LOG.warn("Limit of tokens for user '{}' on client '{}' exceeded. Current: {} | Max: {}", userName, clientId, managedTokenStore.countTokensByClientIdAndUserName(clientId, userName), properties.getMaxTokensPerClientPerUser());
				throw new ManagedTokenException("Cannot create access tokens. Limit of tokens for user on client exceeded.");
			}
		}
		
		return super.createAccessToken(authentication);
	}
	
	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AccessToken token = super.getAccessToken(authentication);
		if (token != null) {
			renewTokenExpiration(token, authentication);
		}
		return token;
	}
	
	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		OAuth2AccessToken token = super.readAccessToken(accessToken);
		if (token != null) {
			OAuth2Authentication authentication = super.loadAuthentication(accessToken);
			renewTokenExpiration(token, authentication);
		}
		return token;
	}
	
	private void renewTokenExpiration(OAuth2AccessToken token, OAuth2Authentication authentication) {
		if (!properties.isRenewTokenExpiration()) {
			return;
		}
		
		if (!(token instanceof DefaultOAuth2AccessToken)) {
			return;
		}
		
		LOG.debug("Renewing token expiration for '{}'", token);
		DefaultOAuth2AccessToken modifiableAccessToken = (DefaultOAuth2AccessToken) token;
		int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
		if (validitySeconds > 0) {
			modifiableAccessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
			managedTokenStore.storeAccessToken(modifiableAccessToken, authentication);
		}
	}
	
}
