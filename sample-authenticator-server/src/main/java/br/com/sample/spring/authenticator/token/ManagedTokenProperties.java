package br.com.sample.spring.authenticator.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Habilita configuração via application.properties.
 * 
 * @author hugo.junior
 */
@Component
@ConfigurationProperties(ManagedTokenProperties.PREFIX)
public class ManagedTokenProperties {
	
	public static final String PREFIX = "managed-token";
	
	/**
	 * Máximo de tokens ativos no servidor.
	 */
	private int maxTokens;
	
	/**
	 * Máximo de tokens ativos por usuário.
	 */
	private int maxTokensPerUser;

	/**
	 * Máximo de tokens ativos por aplicação cliente.
	 */
	private int maxTokensPerClient;
	
	/**
	 * Máximo de tokens ativos por usuário em cada aplicação cliente.
	 */
	private int maxTokensPerClientPerUser;
	
	/**
	 * Define se o access token terá sua expiração renovada sempre
	 * que for utilizado.
	 * <p>
	 * Há uma implicação de segurança nessa configuração, uma vez que
	 * aumenta a janela de ataque caso um agente malicioso capture
	 * o access token do usuário e o utilize sem autorização.
	 */
	private boolean renewTokenExpiration;
	
	public int getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(int maxTokens) {
		this.maxTokens = maxTokens;
	}

	public int getMaxTokensPerUser() {
		return maxTokensPerUser;
	}

	public void setMaxTokensPerUser(int maxTokensPerUser) {
		this.maxTokensPerUser = maxTokensPerUser;
	}

	public int getMaxTokensPerClient() {
		return maxTokensPerClient;
	}

	public void setMaxTokensPerClient(int maxTokensPerClient) {
		this.maxTokensPerClient = maxTokensPerClient;
	}

	public int getMaxTokensPerClientPerUser() {
		return maxTokensPerClientPerUser;
	}

	public void setMaxTokensPerClientPerUser(int maxTokensPerClientPerUser) {
		this.maxTokensPerClientPerUser = maxTokensPerClientPerUser;
	}

	public boolean isRenewTokenExpiration() {
		return renewTokenExpiration;
	}

	public void setRenewTokenExpiration(boolean renewTokenExpiration) {
		this.renewTokenExpiration = renewTokenExpiration;
	}
}
