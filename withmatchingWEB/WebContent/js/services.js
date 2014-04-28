'use strict';

/* Services */

var spreasyServices = angular.module('withmatchingServices',[]);

spreasyServices.factory('AjaxCallService', ['$http', '$rootScope', function ($http, $rootScope) {
	return {
		call: function (method, data, onSuccess, onError) {
			$rootScope.ajaxLoading = true;
			var payload = {
					method: method,
					data: data
			};
			
			$http({
				method: 'POST', 
				url: '/local',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				data: "json="+encodeURIComponent(JSON.stringify(payload))}).
			success(function onAjaxSuccess(resp, status, headers, config) {
				$rootScope.ajaxLoading = false;
				if (resp.status != "200:OK") {
					onError(resp.error.message);
					console.log(resp.error);
				} else {
					onSuccess(resp.result);
				}
			}).
			error(function onAjaxError(resp, status, headers, config) {
				$rootScope.ajaxLoading = false;
				//TODO: Maybe change this to popup message
				alert(resp+"\n"+status);
			});
		}
	};
	
}]);

spreasyServices.factory('SessionUser', function () {
	var user = {
			name: null,
			id: 0,
			loggedIn: false
	};
	
	return {
		init: function (usr) {
			user.name = usr.name;
			user.id = usr.id;
			user.loggedIn = true;
		},
		
		setName: function (name) {
			user.name = name;
		},
		
		getName: function () {
			return user.name;
		},
	
		setId: function (id) {
			user.id = id;
		},
		
		getId: function () {
			return this.id;
		},
		
		setLoggedIn: function (li) {
			user.loggedIn = (li) ? true: false;
		},
		
		isLoggedIn: function () {
			return user.loggedIn;
		},
		
		destroy: function () {
			user = {
					name: null,
					id: 0,
					loggedIn: false
			};
		}
	};
});