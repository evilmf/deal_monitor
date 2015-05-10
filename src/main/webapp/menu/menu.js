'use strict';

var menuModule = angular.module('myApp.menu', []);

menuModule.controller('menuCtrl', ['snapshotService', 'menuService', '$log', '$scope', function(snapshotService, menuService, $log, $scope) {
	$scope.latestSnapshotId = null;
	$scope.currentSnapshotId = null;
	$scope.snapshotDetail = null;
	
	$scope.nextSnapshot = function() {
		snapshotService.setCurrentSnapshotId(menuService.getNextSnapshotId($scope.currentSnapshotId, $scope.latestSnapshotId));
	};
	
	$scope.preSnapshot = function() {
		snapshotService.setCurrentSnapshotId(menuService.getPreSnapshotId($scope.currentSnapshotId));
	};
	
	$scope.lastSnapshot = function() {
		snapshotService.setCurrentSnapshotId($scope.latestSnapshotId);
	};
	
	$scope.$watch(function() { return snapshotService.getCurrentSnapshotId(); }, function(n, o) {
		if(n != o) {
			$scope.currentSnapshotId = n;
		}
	});
	
	$scope.$watch(function() { return snapshotService.getLatestSnapshotId(); }, function(n, o) {
		if(n != o) {
			$scope.latestSnapshotId = n;
		}
	});
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