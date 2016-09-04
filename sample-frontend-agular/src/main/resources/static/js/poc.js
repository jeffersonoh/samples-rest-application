angular
	.module('poc', [ 'ngRoute' , 'auth' , 'home' , 'services' ])
	.constant("CONTEXT_PATH", "/oauth-frontend-angular")
	.constant("BACKEND_URL", "http://localhost:7712/oauth-backend-rest")
	.config(function($routeProvider, $httpProvider) {
		
		// Libera CORS com cookies
	    $httpProvider.defaults.withCredentials = true;

		// Adiciona interceptor para token do OAuth
		$httpProvider.interceptors.push('oauthInterceptor');
		
		$routeProvider
			.when('/', {
				templateUrl : 'js/home/home.html',
				controller : 'home'
			})
			.when('/login:token?', {
				templateUrl : 'js/auth/login.html',
				controller : 'login'
			})
			.when('/services', {
				templateUrl : 'js/services/services.html',
				controller : 'services'
			})
			.otherwise('/')
		;
	})
;
