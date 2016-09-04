package br.com.sample.spring.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationDetailServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username.equalsIgnoreCase("teste")) {
			
			UserDetailsImpl user = new UserDetailsImpl();
			user.setUserName(username);
			user.setPassword("1234");
			user.addAuthority("ROLE_USER");
			
			return user;
		}
		
		throw new UsernameNotFoundException("Usuario não encontrado");
	}

}
