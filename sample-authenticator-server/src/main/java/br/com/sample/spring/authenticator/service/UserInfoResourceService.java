package br.com.sample.spring.authenticator.service;

import java.security.Principal;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Configuração do serviço {@code /oauth/user}.
 * <p>
 * Este serviço é semelhante ao {@code /user}, mas diferente deste, 
 * que retorna o usuário autenticado na sessão do servidor, 
 * este serviço é tratado como um Resource OAuth e é autenticado 
 * por um <b>access token</b>.
 * <p>
 * Este serviço é necessario caso as aplições que irão validar os tokens
 * neste servidor utilizem  {@code UserInfoUri} ao invés do {@code TokenInfoUri}.
 * <p>
 * Contudo, o endpoint de UserInfo não exige confiança entra a aplicação
 * cliente e o servidor, e isso pode ser considerado uma falha de segurança,
 * de forma que qualquer um que tiver posse de um token válido, poderá 
 * obter todos os detalhes do usuário associado.
 * <p>
 * Jà o endpoint TokenInfo pode exigir (configurável), e é mais seguro.
 * 
 * @author hugo.junior
 */
@RestController
public class UserInfoResourceService {
	
	/**
	 * Retorna o usuário autenticado por um Access Token.
	 * <p>
	 * Este serviço requer a configuração de um filtro separado
	 * de segurança {@link OAuthResourceConfiguration}.
	 */
	@RequestMapping("/oauth/user")
	public Principal oauthUser(Principal user) {
		return user;
	}
	
	/**
	 * Configuração de segurança do serviço, utilizando o protocolo 
	 * OAuth do próprio servidor.
	 * 
	 * @author hugo.junior
	 */
	@Configuration
	@EnableWebSecurity
	@EnableResourceServer
	public static class OAuthResourceConfiguration extends ResourceServerConfigurerAdapter {
	
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/oauth/user")
				.authorizeRequests()
					.anyRequest()
						.authenticated()
			;
		}
	}
}