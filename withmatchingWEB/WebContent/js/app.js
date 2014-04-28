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
      state('households', {
      	url: '/households',
          templateUrl: 'partials/households.html',
          controller: 'HouseholdCtrl'
        });
  }]);
