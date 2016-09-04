angular
	.module('services', [])
	.controller('services', function($scope, $http, BACKEND_URL) {
  		
		// Usado na marcação da barra de navegação
		$scope.section = "services";
		
  		var sendData = "data=" + encodeURIComponent("message to send.");
  		var sendConfig = {headers: {'Content-Type': 'application/x-www-form-urlencoded'}};
  		
  		var extractJson = function(json) {
			var msg = "{";
			for (p in json) {
				msg += p + ":" + json[p] + ","; 
			}
			msg += "}";
			return msg;
  		}
  		
  		var extractError = function(response) {
  			var msg = "Unknown error";
  			if (response.status == 0) {
  				msg = "Service unavailable - Offline/CORS";
  			}
  			else {
  				msg = response.status + " - " + response.statusText;
  			}
  			return msg;
  		}
  		
  		
  		$scope.service = {};
  		
  		$http.get(BACKEND_URL + '/service/get/getId').then(
  			// success
  			function(response){
  				$scope.service.get = "Success: " + response.data;
  			},
  			// error
  			function(response) {
  				$scope.service.get = "Error: " + extractError(response);
  			}
  		);
  		
  		$http.post(BACKEND_URL + '/service/post/postId', sendData, sendConfig).then(
  			// success
  			function(response){
  				$scope.service.post = "Success: " + response.data;
  			},
  			// error
  			function(response) {
  				$scope.service.post = "Error: " + extractError(response);
  			}
  		);
  		
  		$http.put(BACKEND_URL + '/service/put/putId', sendData, sendConfig).then(
  			// success
  			function(response){
  				$scope.service.put = "Success: " + response.data;
  			},
  			// error
  			function(response) {
  				$scope.service.put = "Error: " + extractError(response);
  			}
  		);
  		
  		$http.delete(BACKEND_URL + '/service/delete/deleteId').then(
  			// success
  			function(response){
  				$scope.service.delete = "Success: " + response.data;
  			},
  			// error
  			function(response) {
  				$scope.service.delete = "Error: " + extractError(response);
  			}
  		);
  		
  		$http.patch(BACKEND_URL + '/service/patch/patchId', sendData, sendConfig).then(
  			// success
  			function(response){
  				$scope.service.patch = "Success: " + response.data;
  			},
  			// error
  			function(response) {
  				$scope.service.patch = "Error: " + extractError(response);
  			}
  		);
  		
  		$http.head(BACKEND_URL + '/service/head').then(
  	  			// success
  	  			function(response){
  	  				$scope.service.head = "Success: " + extractJson(response.headers());
  	  			},
  	  			// error
  	  			function(response) {
  	  				$scope.service.head = "Error: " + extractError(response);
  	  			}
  	  		);
  	  		
  		$http.jsonp(BACKEND_URL + '/service/jsonp?callback=JSON_CALLBACK').then(
  			// success
  			function(response){
  				$scope.service.jsonp = "Success: " + extractJson(response.data);
  			},
  			// error
  			function(response) {
  				$scope.service.jsonp = "Error: " + extractError(response); 
  			}
  		);
  		
  		
  	})

;