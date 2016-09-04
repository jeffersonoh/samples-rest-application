angular
	.module('auth', [])
	.constant('CONFIG', {
		oauthServer:  'http://localhost:7710/oauth-server',
		clientKey:    'poc-key',
		clientSecret: 'poc-secret', //INSEGURO: Expõe senha da aplicação (usado no Resource Owner credentials)
		clientScope:  'openid',     //Use 'openid' ou 'api' para auto-approve 
	})
	
	.factory('oauthInterceptor', function() {
		return {
			'request': function(config) {

				// Inicializa headers
				config.headers = config.headers || {};
				
			    // Carrega token
				var accessToken = localStorage.getItem('auth.accessToken');
				
				// Adiciona authorização caso já não esteja definido (ad-hoc)
				if (accessToken && !config.headers['Authorization']) {
					config.headers['Authorization'] = 'Bearer ' + accessToken;
				}
				
				return config;
			}
		};
	})
	
	.factory('auth', function($http, BACKEND_URL, CONFIG) {
		
		var auth = {
			
			authenticated: false,
			userInfo: {},
			
			checkUser: function(callback, result) {
				result = result || {};
	  		    $http.get(BACKEND_URL + '/user').then(
		  		    // success
		  		    function(response) {
						if (response.data.name) {
							auth.authenticated = true;
							auth.userInfo = response.data;
						} else {
							result.error = true;
							result.message = response.data;
							auth.authenticated = false;
							auth.userInfo = {};
						}
						callback && callback(result);
		  		    },
		  		    // error
		  		    function(response) {
		  		    	auth.authenticated = false;
		  		    	auth.userInfo = {};
		  		    	result.error = true;
  		  				result.message = "Error [" + response.status + ']: ' + response.statusText;
  		  				if (response.status == 0) {
  		  					result.message = 'Serviço indisponível.';
  		  				}
		  		    	callback && callback(result);
		  		    }
		  		);
	  		},
	  		
	  		// Autenticação por token passado por paramêmtro (implicit)
	  		implicitLogin: function(routeParams, callback) {
	  			var result = {};
  				var split = routeParams.split('&');
  				var param = {}; 
  				for (p in split) {
  					pair = split[p].split('=');
  					if (pair[1]) {
  						param[pair[0]] = pair[1];
  					}
  				}
  				if (param.error) {
  					result.error = true;
  					result.message = param.error_description;
  					callback && callback(result);
  					return;
  				}
  				if (!param.access_token) {
  					result.error = true;
  					result.message = "Token parameter 'access_token' not found: " + routeParams;
  					callback && callback(result);
  					return;
  				}
  				
				localStorage.setItem('auth.accessToken', param.access_token);
  				auth.checkUser(callback, result);
	  		},
	  		
	  		// Resource Owner Credentails
	  		directLogin: function(credentials, callback) {
	  			var result = {};
	  			if (!credentials || !credentials.username) {
	  				result.error = true;
	  				result.message = "Usuario ou senha não informados."
	  				callback && callback(result);
	  				return;
	  			}
	  			
	  			var url = CONFIG.oauthServer + '/oauth/token';
	  			var data = 'grant_type=password' +
	  				'&username=' + encodeURIComponent(credentials.username) +
	  				'&password=' + encodeURIComponent(credentials.password)
	  			;
	  			
	  			// Basic Scheme (Form Scheme os parametros seriam concatenados em 'data') 
	  			var headers = {
	  				'Content-Type': ' application/x-www-form-urlencoded',
	  				'Authorization': 'Basic ' + btoa(CONFIG.clientKey + ':' + CONFIG.clientSecret),
	  			};
	  			
	  	  		$http.post(url, data, {'headers': headers}).then(
  	    			// success
  	    			function(response){
  	    				localStorage.setItem('auth.accessToken', response.data.access_token);
  		  				auth.checkUser(callback, result);
  	    			},
  	    			// error
  	    			function(response) {
  		  				result.error = true;
  		  				result.message = "Error [" + response.status + ']: ' + response.statusText
  		  				if (response.status == 0) {
  		  					result.message = 'Serviço indisponível.';
  		  				}
  		  				if (response.data && response.data.error_description) {
  		  					result.message = "Error: " + response.data.error_description;
  		  				}
  		  				callback && callback(result);
  	    			}
  	    		);
	  		},
	  		
	  		doLogout: function() {
				auth.authenticated = false;
				auth.userInfo = {};
				localStorage.removeItem('auth.accessToken');
	  		},
	  		
		};
		
		return auth;
 	})
 	
	.controller('login', function($rootScope, $scope, $location, $route, $window, auth, CONFIG) {
		
		// Usado na marcação da barra de navegação
		$scope.section = "login";
		
		// Trata Token recebido na URL
		var routeParams = $route.current.params.token;
		if (routeParams) {
	        $scope.ssoError = false;
			$scope.ssoMsg = "Validating token...";
			auth.implicitLogin(routeParams, function(result){
				var ok;
				if (result.error) {
			        $scope.ssoError = true;
			        $scope.ssoMsg = result.message;
			        ok = false;
				} else {
			        $location.path("/");
			        ok = true;
				}
				// Sinaliza a quem interessar
				$rootScope.$emit('authUpdate', ok);
			});
		}
		
		$scope.ssoLogin = function(withScope) {
			
			var redirect = $window.location.href;
			if (~$window.location.hash.indexOf('&')) {
				redirect 
					= $window.location.protocol + "//"
					+ $window.location.host 
					+ $window.location.pathname 
					+ $window.location.hash.substring(0, $window.location.hash.indexOf('&'))
				;
			}
			
			var url = CONFIG.oauthServer + '/oauth/authorize?response_type=token'
				+ '&client_id=' + CONFIG.clientKey
				+ '&redirect_uri=' + encodeURIComponent(redirect)
			;
			
			if (withScope && CONFIG.clientScope) {
				url += "&scope=" + CONFIG.clientScope;
			}
			
			$window.location.href = url; 
		};
		
		$scope.directLogin = function() {
			$scope.formError = false;
			$scope.formMsg = "Validating credentials...";
			auth.directLogin($scope.credentials, function(result) {
				var ok;
				if (result.error) {
					$location.path("/login");
					$scope.formError = true;
					$scope.formMsg = result.message;
					ok = false;
				} else {
			        $location.path("/");
			        ok = true;
				}
				// Sinaliza a quem interessar
				$rootScope.$emit('authUpdate', ok);
			});
		};
		
 	})
;