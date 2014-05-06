'use strict';

/* Controllers */

var withmatchingControllers = angular.module('withmatchingControllers',['ui.bootstrap']);

withmatchingControllers.controller('LoginCtrl', ['$scope', '$state','AjaxCallService', 'SessionUser', function ($scope, $state, AjaxCallService, SessionUser) {
	$scope.login = function (email, password) {
		AjaxCallService.call('login',{email: email, password: password}, 
				function onSuccess(data) {
					SessionUser.init(data.user);
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
					SessionUser.init(data.user);
					$scope.$emit('onUserStateChange', {});
					$state.go('home');
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);

withmatchingControllers.controller('GlobalCtrl', ['$scope', 'SessionUser', 'AjaxCallService', '$state', function ($scope, SessionUser, AjaxCallService, $state) {
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

withmatchingControllers.controller('HomeCtrl', ['$scope', '$state', 'SessionUser', function ($scope, $state, SessionUser) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	$scope.user = {};
	$scope.user.name = SessionUser.getName();
}]);

withmatchingControllers.controller('QuestionCtrl', ['$scope', '$state', 'SessionUser', 'AjaxCallService', function ($scope, $state, SessionUser, AjaxCallService) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	

	AjaxCallService.call('loadAllQuestions',{uid: SessionUser.getId()}, 
			function onSuccess(data) {
				$scope.questions = data.allQuestions;
			},
			function onError(data) {
				$scope.$emit('alertMessage', {type: 'danger', message: data});
			}
	);
	
	$scope.saveQuestion = function () {
		AjaxCallService.call('addQuestion',{uid: SessionUser.getId(), question: {body: $scope.qBody, answer: $scope.qAnswer}}, 
				function onSuccess(data) {
					$scope.questions.push(data.question);
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
	
	$scope.deleteQuestion = function (question, index) {
		AjaxCallService.call('deleteQuestion',{uid: SessionUser.getId(), question: question}, 
				function onSuccess(data) {
					$scope.questions.splice(index,1);
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);

withmatchingControllers.controller('TestCtrl', ['$scope', '$state', 'SessionUser', 'AjaxCallService', function ($scope, $state, SessionUser, AjaxCallService) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	

	AjaxCallService.call('loadAllTests',{uid: SessionUser.getId()}, 
			function onSuccess(data) {
				$scope.tests = data.allTests;
			},
			function onError(data) {
				$scope.$emit('alertMessage', {type: 'danger', message: data});
			}
	);
	
	$scope.saveTest = function () {
		AjaxCallService.call('addTest',{uid: SessionUser.getId(), test: {name: $scope.tName}}, 
				function onSuccess(data) {
					$scope.tests.push(data.test);
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
	
	$scope.deleteTest = function (test, index) {
		AjaxCallService.call('deleteTest',{uid: SessionUser.getId(), test: test}, 
				function onSuccess(data) {
					$scope.tests.splice(index,1);
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);
