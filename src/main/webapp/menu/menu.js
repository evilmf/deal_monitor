'use strict';

var menuModule = angular.module('myApp.menu', []);

menuModule.controller('menuCtrl', ['snapshotService', 'menuService', '$log', '$rootScope', function(snapshotService, menuService, $log, $rootScope) {
	$rootScope.nextSnapshot = function() {
		$rootScope.currentSnapshotId = menuService.getNextSnapshotId($rootScope.currentSnapshotId, $rootScope.latestSnapshotId);
	};
	
	$rootScope.preSnapshot = function() {
		$rootScope.currentSnapshotId = menuService.getPreSnapshotId($rootScope.currentSnapshotId);
	};
	
	$rootScope.lastSnapshot = function() {
		$rootScope.currentSnapshotId = $rootScope.latestSnapshotId;
	};
	
	$rootScope.getSnapshotById = function(id) {
		if(id > 0) {
			$rootScope.currentSnapshotId = id;
		}
	};
}]);

menuModule.factory('menuService', ['$log', function($log){
	var getNextSnapshotId = function(c, l) {
		c = parseInt(c);
		var s = c;
		if (c < l) {
			s = c + 1;
		}
		
		return s;
	};
	
	var getPreSnapshotId = function(c) {
		c = parseInt(c);
		var s = c;
		if (c > 1) {
			s = c - 1;
		}
		
		return s;
	};
	
	return {getNextSnapshotId : getNextSnapshotId,
			getPreSnapshotId : getPreSnapshotId};
}]);