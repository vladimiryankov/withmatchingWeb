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
	
	$scope.uid = SessionUser.getId();
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
	
	$scope.uid = SessionUser.getId();
	
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

withmatchingControllers.controller('TestQuestionCtrl', ['$scope', '$state', 'SessionUser', 'AjaxCallService', '$stateParams',  function ($scope, $state, SessionUser, AjaxCallService, $stateParams) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	
	AjaxCallService.call('loadTestAndAllQuestions',{uid: SessionUser.getId(), test: {id: parseInt($stateParams.testId)}}, 
			function onSuccess(data) {
				$scope.test = data.test;
				$scope.questions = data.questions;
			},
			function onError(data) {
				$scope.$emit('alertMessage', {type: 'danger', message: data});
			}
	);
	
	$scope.addTestQuestion = function (question) {
		AjaxCallService.call('addTestQuestion',{uid: SessionUser.getId(), test: {id: $scope.test.id}, question: question}, 
				function onSuccess(data) {
					$scope.test.questions.push(data.question);
					$scope.$emit('alertMessage', {type: 'success', message: "Question '"+question.body+"' successfully added to '"+$scope.test.name+"'!"});
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
	
	$scope.deleteTestQuestion = function (question, index) {
		AjaxCallService.call('removeTestQuestion',{uid: SessionUser.getId(), test: {id: $scope.test.id}, question: question}, 
				function onSuccess(data) {
					$scope.test.questions.splice(index,1);
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
}]);

withmatchingControllers.controller('TestPlayCtrl', ['$scope', '$state', 'SessionUser', 'AjaxCallService', '$stateParams',  function ($scope, $state, SessionUser, AjaxCallService, $stateParams) {
	if (!SessionUser.isLoggedIn()) $state.go('login');
	
	AjaxCallService.call('loadTestAndAllQuestions',{uid: SessionUser.getId(), test: {id: parseInt($stateParams.testId)}}, 
			function onSuccess(data) {
				$scope.test = data.test;
				
				var answers = [];
				var questions = [];
				for (var i = 0; i < data.test.questions.length; i++) {
					var q = data.test.questions[i];
					questions.push({body: q.body, answer: "", id: q.id});
					answers.push(q.answer);
				}
				
				for (var i = 0; i < questions.length; i++) {
					questions[i].answers = answers;
				}
				$scope.questions = questions;
			},
			function onError(data) {
				$scope.$emit('alertMessage', {type: 'danger', message: data});
			}
	);
	
	$scope.submitQuestions = function () {
		$scope.test.questions = $scope.questions;
		AjaxCallService.call('checkTest',{uid: SessionUser.getId(), test: $scope.test}, 
				function onSuccess(data) {
					//console.log(data);
					
					if (data.wrongQuestions.length == 0) $scope.$emit('alertMessage', {type: 'success', message: "Well done!"});
					else $scope.$emit('alertMessage', {type: 'warning', message: ($scope.questions.length-data.wrongQuestions.length)+" out of "+$scope.questions.length+" right answers!"});
					
					for (var i = 0; i < $scope.questions.length; i++) {
						var q = $scope.questions[i];
						q.result = "list-group-item-success";
						
						for(var j = 0; j < data.wrongQuestions.length; j++) {
							var wq = data.wrongQuestions[j];
							if (wq.id == q.id) {
								q.result = "list-group-item-danger";
								break;
							}
						}
						
					}
					
				},
				function onError(data) {
					$scope.$emit('alertMessage', {type: 'danger', message: data});
				}
		);
	}
	
}]);

