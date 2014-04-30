'use strict';

/* Controllers */

var spreasyControllers = angular.module('withmatchingControllers',['ui.bootstrap']);

spreasyControllers.controller('LoginCtrl', ['$scope', '$state','AjaxCallService', 'SessionUser', function ($scope, $state, AjaxCallService, SessionUser) {
	$scope.login = function (email, password) {
		AjaxCallService.call('login',{email: email, password: password}, 
				function onSuccess(data) {
					SessionUser.init(data);
					$scope.$emit('onUserStateChange', {});
					$state.go('home');
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
		
	},
	
	$scope.register = function (reg) {
		AjaxCallService.call('register', {email: reg.email, password: reg.password, name: reg.name},
				function onSuccess(data) {
					SessionUser.init(data);
					$scope.$emit('onUserStateChange', {});
					$state.go('home');
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);

spreasyControllers.controller('GlobalCtrl', ['$scope', 'SessionUser', 'AjaxCallService', '$state', function ($scope, SessionUser, AjaxCallService, $state) {
	$scope.$on('alertMessage', function (event, data) {
		console.log(data);
		$scope.alert = data;
	});
	
	$scope.isLoggedIn = SessionUser.isLoggedIn();
	
	//refresh user info
	$scope.$on('onUserStateChange', function (event, data) {
		$scope.isLoggedIn = SessionUser.isLoggedIn();
		$scope.username = SessionUser.getName();
	});
	
	//alert close
	$scope.closeAlert = function () {
		$scope.alert = null;
	}
	
	$scope.$on('$stateChangeStart', function (event, data) {
		$scope.closeAlert();
	});
	
	//logout
	$scope.logout = function () {
		AjaxCallService.call('logout',{}, 
				function onSuccess(data) {
					SessionUser.destroy();
					$scope.$emit('onUserStateChange', {});
					$state.go('login');
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);

spreasyControllers.controller('HomeCtrl', ['$scope', '$state', 'SessionUser', function ($scope, $state, SessionUser) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	$scope.user = {};
	$scope.user.name = SessionUser.getName();
}]);
