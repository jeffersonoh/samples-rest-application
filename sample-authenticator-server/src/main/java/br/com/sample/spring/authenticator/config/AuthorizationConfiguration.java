package br.com.sample.spring.authenticator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import br.com.sample.spring.authenticator.token.ManagedTokenService;
import br.com.sample.spring.authenticator.token.ManagedTokenStore;

/**
 * Configuração do serviço de autorização por tokens do OAuth Server.
 * 
 * @author hugo.junior
 */
@Configuration
@EnableAuthorizationServer 
public class AuthorizationConfiguration extends AuthorizationServerConfigurerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationConfiguration.class);

	/**
	 * O Authentication Manager configurado.
	 */
	@Autowired
	private AuthenticationManager authenticationManager;
	
	/**
	 * Serviço personalizado de geração de tokens. 
	 */
	@Autowired
	private ManagedTokenService managedTokenService;
	
	/**
	 * Repositório personalizado de armazenamento de tokens.
	 */
	@Autowired
	private ManagedTokenStore managedTokenStore;
	
	/**
	 * Configura os Endpoints do OAuth Server.
	 * <p>
	 * Configura o {@link AuthenticationManager} do servidor nos 
	 * Endpoints OAuth para habilitar a autenticação do tipo 
	 * <b>Resource Owner Credentials ('password')</b>.
	 * <p>
	 * Também permite adicionar o serviço de geração de tokens 
	 * com controle de limite de tokens, por exemplo, etc. 
	 * <hr>
	 * {@inheritDoc}
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		LOG.debug("Configurando OAuth endpoints.\n---> AuthenticationManager: {}", authenticationManager);
		
		endpoints
			.authenticationManager(authenticationManager)
			.tokenStore(managedTokenStore)
			.tokenServices(managedTokenService)
			.accessTokenConverter(new DefaultAccessTokenConverter())
		;
	}
	
	
	/**
	 * Configura outros aspectos de segurança do serviço de autorização.
	 * <p>
	 * Habilita a verificação de tokens no Endpoint {@code /oauth/check_token} 
	 * para que as aplicações clientes possam autenticar tokens que elas mesmas
	 * recebem, no caso de {@code ResourceServer}s, repassando o token para o 
	 * servidor (<b>token relay</b>).
	 * <p> 
	 * Este Endpoint está configurado para exigir autenticação da aplicação cliente.  
	 * <hr>
	 * {@inheritDoc}
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			//.checkTokenAccess("permitAll()") //não exige autenticação da aplicação cliente
			.checkTokenAccess("isAuthenticated()") //exige autenticação da aplicação cliente
		;
	}
	/**
	 * Configura os clientes que podem autenticar neste OAuth Server.
	 * <p>
	 * Grant Types:
	 * <dl>
	 * 	<dt>"authorization_code"</dt>
	 * 		<dd>Padrão, recebe 'code' para trocar pelo 'access token'</dd>
	 * 	<dt>"implicit"</dt>
	 * 		<dd>Implicito, recebe direto o 'access token' (acesso deve ser mais restrito/public token)</dd>
	 * 	<dt>"password"</dt>
	 * 		<dd>User credentials, client fornece username/password do usuário (trusted clients)</dd>
	 * 	<dt>"client_credentials"</dt>
	 * 		<dd>Client se auto-autentica para receber 'access token' (trusted clients)</dd>
	 * 	<dt>"refresh_token"</dt>
	 * 		<dd>Token exclusivo para renovação de 'access token' expirado</dd>
	 * </dl>
	 * <hr>
	 * {@inheritDoc}
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		LOG.debug("Configurando OAuth clients.");
		clients
		.inMemory()
			.withClient("client-key")
				.secret("client-secret")
				.authorizedGrantTypes(
						"authorization_code", 
						"refresh_token"
				)
				.scopes("openid")
				.autoApprove(true)
				.redirectUris("http://www.client-app.com:8080/auth", "localhost")
		.and()
			.withClient("poc-key")
				.secret("poc-secret")
				.authorizedGrantTypes(
						"authorization_code", 
						"implicit",
						"password",
						"client_credentials", 
						"refresh_token"
				)
				.scopes("openid", "write", "api")
				.autoApprove("openid", "api")
		;
	}
	
}