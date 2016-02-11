'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.snapshot',
  'myApp.menu',
  'myApp.sidebar',
  'myApp.filter',
  'myApp.version'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/snapshot'});
}]);