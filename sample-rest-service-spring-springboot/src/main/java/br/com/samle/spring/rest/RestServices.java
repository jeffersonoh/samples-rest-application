package br.com.samle.spring.rest;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serviços protegidos.
 * 
 * @author hugo.junior
 */
@RestController
@RequestMapping(value = "/service", produces = MediaType.TEXT_PLAIN_VALUE)
public class RestServices {
	
	public static final String HEADER_EXPOSED = "X-Service-Response";
	
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public String get(@PathVariable String id) {
      return "get: " + id;
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.POST)
    public String post(@PathVariable String id, @RequestParam Map<String, String> data) {
      return "post: " + id + " -> " + data;
    }

    @RequestMapping(value = "/put/{id}", method = RequestMethod.PUT)
    public String put(@PathVariable String id, @RequestBody String data) {
      return "put: " + id + " -> " + data;
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String id) {
      return "delete: " + id;
    }
    
    @RequestMapping(value = "/patch/{id}", method = RequestMethod.PATCH)
    public String patch(@PathVariable String id, @RequestBody String data) {
      return "patch: " + id + " -> " + data;
    }
    
    @RequestMapping(value = "/head", method = RequestMethod.HEAD)
    public String head(HttpServletResponse response) {
    	response.addHeader(HEADER_EXPOSED, "Special Value!");
    	return "head";
    }
    
    @RequestMapping(value = "/jsonp", produces = "application/javascript")
    public String jsonp(@RequestParam String callback) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication != null ? authentication.getName() : "[not-authenticated]";
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
    	return callback + "({\"username\": \""+ username +"\", \"roles\": \""+ roles.toString() +"\"});";
    }
}
