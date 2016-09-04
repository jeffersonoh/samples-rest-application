package br.com.samle.spring.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ponto de entrada da aplicação Backend com Spring Boot, 
 * protegida pelo OAuth Server. Este é Resource Server, não 
 * utiliza SSO e faz relay do token para o OAuth Server.
 * <p>
 * As aplicações que consomem serviços desses servidor deve
 * informar o token no cabeçalho das requisições. Caso o token 
 * seja inválido ou não informado, a resposta
 * será um <b>401 Unauthorized</b>.
 * <p>
 * Exemplo do token no cabeçalho da requisição:
 * <pre>
 * Authorization: Bearer [TOKEN]
 * </pre> 
 * 
 * @author hugo.junior
 */
@SpringBootApplication
@RestController
@EnableOAuth2Resource 
public class BackendOAuthApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(BackendOAuthApplication.class, args);
    }    

    @RequestMapping("/resource")
    public Map<String,Object> home() {
      Map<String,Object> model = new HashMap<String,Object>();
      model.put("id", UUID.randomUUID().toString());
      model.put("content", "Hello World! (from java)");
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication != null ? authentication.getName() : "[not authenticated]";
      model.put("user", username);
      return model;
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
      return user;
    }

}
