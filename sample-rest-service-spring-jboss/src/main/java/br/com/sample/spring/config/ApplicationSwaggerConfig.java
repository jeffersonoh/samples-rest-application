package br.com.sample.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationSwaggerConfig {
	@Bean
	public springfox.documentation.spring.web.plugins.Docket docket() {
		Docket docket = new Docket(springfox.documentation.spi.DocumentationType.SWAGGER_2);
		
//		Customizando exception
		
//		.useDefaultResponseMessages(false)                                   
//		.globalResponseMessage(RequestMethod.GET,                     
//		newArrayList(new ResponseMessageBuilder()   
//		.code(500)
//		.message("500 message")
//		.responseModel(new ModelRef("Error"))
//		.build(),
//		 new ResponseMessageBuilder() 
//		.code(403)
//		.message("Forbidden!")
//		.build()));
		
		return docket.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Client sample-springmvc jboss  API", "Description for API.", "Version API 1.0", "Termos de uso", new Contact("sample-springmvc jboss", "www.sample-springmvc jboss.com.br", "contato@e-mail.com"), "API License", "API License URL");
	}
}
