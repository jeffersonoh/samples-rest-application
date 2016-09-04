package br.com.sample.spring.authenticator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Configuração da autenticação de usuários do OAuth Server.
 * 
 * @author hugo.junior
 */
@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class AuthenticationConfiguration extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfiguration.class);
	
	/**
	 * Configuração externalizada.
	 */
	@Autowired 
	private OAuthServerProperties properties;
	
	/**
	 * Libera as páginas e recursos estáticos.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
				.antMatchers("/", "/index*", "/css/**", "/js/**", "/img/**");
	}
	
	/**
	 * Configura a autenticação para usar Form Login.
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		LOG.debug("Configurando autenticação por Form Login.");
		
		http
			.formLogin()
		.and()
			.authorizeRequests()
				.antMatchers("/user", "/parse")
					.permitAll()
				.anyRequest()
					.authenticated()
		.and()
			.csrf()
				.disable();
		;
		
		if (properties.getMaxSessionsPerUser() > 0) {
			LOG.info("Max sessions per user: {}", properties.getMaxSessionsPerUser());
			http.sessionManagement().maximumSessions(properties.getMaxSessionsPerUser());
		}
	}
	
	/**
	 * Configura um {@link AuthenticationManager} global para ser usado tanto pela
	 * autenticação de usuários no OAuth Server quanto pela Autorização de Tokens.
	 * <p>
	 * A implementação usada é autenticação de usuários em memória, apenas para
	 * prova de conceito. Em uma aplicação real deve ser usado um Banco de Dados ou
	 * outra forma.
	 * <p>
	 * A autorização funciona parcialmente ao configurar o AuthenticationManager sobrescrevendo 
	 * os métodos {@link WebSecurityConfigurerAdapter#configure(AuthenticationManagerBuilder auth)} 
	 * e {@link WebSecurityConfigurerAdapter#authenticationManagerBean()} da classe externa, 
	 * devido a ordem que o Spring Boot instancia as configurações.
	 * <p>
	 * As chamadas de {@code refresh_token} não irão funcionar porque o {@link UserDetailsService} 
	 * não vai estar definido no momento que ocorre a configuração os endpoints oauth.
	 * 
	 * @author hugo.junior
	 */
	@Configuration
	protected static class AuthenticationManagerConfiguration extends GlobalAuthenticationConfigurerAdapter {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			LOG.debug("Configurando AuthenticationManager com autenticação em memória.");
			auth
				.inMemoryAuthentication()
					.withUser("teste").password("teste").authorities("USER", "TEST")
				.and()
					.withUser("admin").password("admin").authorities("USER", "ADMIN")
				.and()
					.withUser("api").password("api").authorities("API")
				.and()
					.withUser("username").password("password").authorities("USER")
			;
		}
	}

	/**-/ Configuração anterior na classe externa
	
	/**
	 * Configura o AuthenticationManager para utilizar autenticação em memória.
	 *-/
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOG.debug("Configurando AuthenticationManager com autenticação em memória.");
		auth
			.inMemoryAuthentication()
				.withUser("teste").password("teste").roles("TESTER")
			.and()
				.withUser("admin").password("admin").roles("ADMIN")
			.and()
				.withUser("api").password("api").roles("API")
			.and()
				.withUser("username").password("password").roles()
		;
	}
	
	/**
	 * Expõe o {@link AuthenticationManager} como um bean, para ser usado em
	 * outras configurações (Authorization).
	 * <p>
	 * {@inheritDoc}
	 *-/
	@Override
    @Bean(name="customAuthenticationManager")
	public AuthenticationManager authenticationManagerBean() throws Exception {
		LOG.debug("Configuring Authentication Manager Bean name: {}", "customAuthenticationManager");
		return super.authenticationManagerBean();
	}
	/**/
	
}