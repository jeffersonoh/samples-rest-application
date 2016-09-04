package br.com.sample.spring.authenticator;

import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.sample.spring.authenticator.token.InMemoryManagedTokenStore;

/**
 * Ponto de entrada da aplicação OAuth Server com Spring Boot.
 * <p>
 * Serviço de autorização e distribuição de token de acesso,
 * seguindo o modelo do Spring Cloud Security.
 * <p>
 * Realiza autenticação de usuários através de um {@code AuthenticationManager}
 * configurado para usuários em memória e autenticação via form.
 * <p>
 * Disponibiliza {@code Endpoints OAuth} para autorização de aplicações clientes 
 * pelo usuário autenticado, e para distribuição e validação de tokens.
 * 
 * @author hugo.junior
 */
@SpringBootApplication
@RestController
public class OAuthServerApplication extends WebMvcConfigurerAdapter {
	
	@Autowired
	private InMemoryManagedTokenStore tokenStore;

	public static void main(String[] args) {
		SpringApplication.run(OAuthServerApplication.class, args);
	}
	
	/**
	 * Retorna o usuário autenticado no Servidor.
	 * <p>
	 * Processado pelo filtro de segurança da configuração padrão de autenticação.  
	 */
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}
	
	/**
	 * Consome o retorno da autenticação oauth.
	 */
	@RequestMapping("/parse")
	public ResponseEntity<String> explain(@RequestParam Map<String, String> params) {
		StringBuilder str = new StringBuilder()
			.append("<html><head>")
			.append("<link href='css/bootstrap.css' rel='stylesheet' />")
			.append("<link href='css/server.css' rel='stylesheet' />")
			.append("<script src='js/server.js'></script>")
			.append("</head><body>")
			.append("<h2>Tratamento de Retorno</h2>")
			.append("<h3>Parâmetros Recebidos</h3>")
			.append("<table id='table'><tr><th>Parâmetro</th><th>Valor</th></tr>")
		;
		for (Entry<String, String> param : params.entrySet()) {
			str
				.append("<tr><td>").append(param.getKey()).append("</td>")
				.append("<td>").append(param.getValue()).append("</td></tr>")
			;
		}
		str
			.append("</table>")
			.append("<script type='text/javascript'>appendHash('table');</script>")
		;
		
		if (params.containsKey("code")) {
			str
				.append("<h3>Segunda Chamada: trocar Code por Token</h3>")
				.append("<div class='command-box'>")
				.append("<label>POST: (curl)</label>")
				.append("<code>\n")
				.append("curl <b>poc-key:poc-secret</b>@localhost:7710/oauth-server/oauth/token ")
				.append("-d grant_type=<b>authorization_code</b> ")
				.append("-d redirect_uri=<b>/oauth-server/parse</b> ")
				.append("-d code=<b>").append(params.get("code")).append("</b>")
				.append("\n</code></div>")
			;
		}
		
		str
			.append("<hr><a class='btn btn-primary' href='/oauth-server/'>Voltar</a>")
			.append("</body></html>")
		;
		
		return ResponseEntity.ok(str.toString());
	}
	
	/**
	 * Limpa os tokens.
	 * <p>
	 * Apenas para testes, não usar em produção.
	 */
	@RequestMapping("/clear")
	public String clear() {
		tokenStore.clear();
		return "OK";
	}
	
	

}
