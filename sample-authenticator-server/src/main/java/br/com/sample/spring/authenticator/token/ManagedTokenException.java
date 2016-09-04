package br.com.sample.spring.authenticator.token;

import org.springframework.security.access.AccessDeniedException;

/**
 * Exceções de tokens gerenciados.
 * 
 * @author hugo.junior
 */
public class ManagedTokenException extends AccessDeniedException {
	
	private static final long serialVersionUID = 1L;

	public ManagedTokenException(String msg) {
		super(msg);
	}

	public ManagedTokenException(String msg, Throwable t) {
		super(msg, t);
	}

}
