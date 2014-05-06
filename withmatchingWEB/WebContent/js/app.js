'use strict';

/* App Module */

var withmatching = angular.module('withmatching', [
  'ui.router',

  'withmatchingControllers',
  'withmatchingServices'
]);

withmatching.config(['$stateProvider','$urlRouterProvider',
  function($stateProvider, $urlRouterProvider){
	
	$urlRouterProvider.otherwise("/login");
	
	$stateProvider.
      state('login', {
    	url: '/login',
        templateUrl: 'partials/login.html',
        controller: 'LoginCtrl'
      }).
      state('home', {
    	url: '/home',
        templateUrl: 'partials/home.html',
        controller: 'HomeCtrl'
      }).
      state('question', {
      	url: '/question',
          templateUrl: 'partials/question.html',
          controller: 'QuestionCtrl'
        }).
    state('test', {
      	url: '/test',
          templateUrl: 'partials/test.html',
          controller: 'TestCtrl'
        });
  }]);
