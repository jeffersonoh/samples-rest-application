package br.com.sample.spring.authenticator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Habilita configuração via application.properties.
 *   
 * @author hugo.junior
 */
@Component
@ConfigurationProperties(OAuthServerProperties.PREFIX) 
public class OAuthServerProperties {
	
	public static final String PREFIX = "server-config";
	
	/**
	 * Máximo de sessões ativas por usuário autenticado.
	 */
	private int maxSessionsPerUser = 0;

	public int getMaxSessionsPerUser() {
		return maxSessionsPerUser;
	}

	public void setMaxSessionsPerUser(int maxSessionsPerUser) {
		this.maxSessionsPerUser = maxSessionsPerUser;
	}
}