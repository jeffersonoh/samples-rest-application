angular
	.module('home', [ 'auth' ])
	.controller('home', function($scope, $http, auth, BACKEND_URL) {
		
		// Usado na marcação da barra de navegação
		$scope.section = "home";
		
		$scope.authenticated = function() {
			return auth.authenticated;
		}
		
  		$http.get(BACKEND_URL + '/resource/').then(function(response){
  			$scope.greeting = response.data;
  		});
  	})
  	.controller('navigation', function($rootScope, $scope, $location, auth, CONFIG) {
  		
		// Pega seção de navegação
		$scope.$on('$routeChangeSuccess', function(event, current) {
			$scope.section = current.scope.section; 
		});

  		$scope.authServer = CONFIG.oauthServer;
  		
		// Escuta pelo evento authUpdate para atualizar o nome do usuário
  		$rootScope.$on('authUpdate', function(event, data) {
			$scope.user = auth.userInfo.name;
		});
		
  		// Verifica se o usuário está logado
		auth.checkUser(function() {
			$rootScope.$emit('authUpdate');
		});
		
		$scope.authenticated = function() {
			return auth.authenticated;
		};
		
		$scope.logout = function() {
			auth.doLogout();
		    $location.path("/");
		};
		
 	})
;