'use strict';

var snapshotModule = angular.module('myApp.snapshot', ['ngRoute', 'ngResource']);

snapshotModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/snapshot', {
    templateUrl: 'snapshot/snapshot.html',
    controller: 'snapshotCtrl'
  });
}]);

snapshotModule.controller('snapshotCtrl', ['snapshotService', '$log', '$scope', '$interval', function(snapshotService, $log, $scope, $interval) {
	$scope.latestSnapshotId = null;
	$scope.currentSnapshotId = null;
	$scope.snapshotDetail = null;
	$scope.updateOn = true;
	$scope.alertOn = true;
	
	var inProgress = false;
	
	var getSnapshotById = function(id) {
		inProgress = true;		
		snapshotService.getSnapshotById(id).success(function(res) {
			//$log.log('Setting snapshot!');
			$scope.snapshotDetail = res['snapshotDetail'];
		}).
		error(function(res) {
			$log.log('Error from getSnapshotById');
			$log.log(res);
		}).
		finally(function() {
			inProgress = false;
		});
	};
	
	var initSnapshot = function() {
		if(inProgress) {
			$log.log('Skipping... Getting snapshot in progress.');
			return;
		}
		
		inProgress = true;
		snapshotService.initSnapshot().success(function(res) {
			snapshotService.setCurrentSnapshotId(res['snapshotId']);
			snapshotService.setLatestSnapshotId(res['snapshotId']);
			$scope.snapshotDetail = res['snapshotDetail'];
		}).
		error(function(res) {
			$log.log('Error from initSnapshot');
			$log.log(res);
		}).
		finally(function() {
			inProgress = false;
		});
	};
	
	var getSnapshotNoDetail = function() {
		if(inProgress) {
			$log.log('Skipping... Getting snapshot in progress.');
			return;
		}
		
		inProgress = true;
		snapshotService.getSnapshotNoDetail().success(function(res) {
			if(res['snapshotId'] > $scope.latestSnapshotId) {
				snapshotService.setLatestSnapshotId(res['snapshotId']);
			}
		}).
		finally(function() {
			inProgress = false;
		});
	};
	
	var getLatestSnapshot = function() {
		//$log.log('Checking if there is new snapshot available.');
		if(inProgress) {
			$log.log('Skipping... Getting snapshot in progress.');
			return;
		}
		
		inProgress = true;
		var sid = $scope.latestSnapshotId + 1;
		snapshotService.getSnapshotById(sid).success(function(res) {
			if(res['snapshotDetail'] != null) {
				$log.log('New snapshot available.');
				$scope.snapshotDetail = res['snapshotDetail'];
				$scope.latestSnapshotId = res['snapshotId'];
				$scope.currentSnapshotId = res['snapshotId'];
				snapshotService.setCurrentSnapshotId(res['snapshotId']);
				snapshotService.setLatestSnapshotId(res['snapshotId']);
			}
		}).
		error(function(res) {
			$log.log('Error from getLatestSnapshot');
			$log.log(res);
		}).
		finally(function() {
			inProgress = false;
		});		
	};
	
	var checkSnapshotUpdate = function() {
		$interval(function() {
			if(!$scope.updateOn) {
				//$log.log('Update is off!');
				return;
			}
			
			if ($scope.latestSnapshotId == null) {
				$log.log('Initializing snapshot.');
				initSnapshot();
			}
			else if($scope.currentSnapshotId != $scope.latestSnapshotId) {
				//$log.log('Getting snapshot with no detail.');
				getSnapshotNoDetail();
			}
			else {
				//$log.log('Getting latest snapshot.');
				getLatestSnapshot();
			}
		}, 5000);
	};
	
	initSnapshot();
	
	checkSnapshotUpdate();
	
	$scope.$watch(function() { return snapshotService.getUpdateStatus(); },
			function(n, o) { 
				if(n != null || n != o) {
					//$log.log('updateOn ' + $scope.updateOn + ' ' + n);
					$scope.updateOn = n; 
				}
			}
	);
	
	$scope.$watch(function() { return snapshotService.getCurrentSnapshotId(); },
			function(n, o) { 
				if(n == null) {
					return;
				}
				if(n != $scope.currentSnapshotId) {
					$scope.currentSnapshotId = n; 
					//$log.log("getCurrentSnapshot from watch");
					getSnapshotById(n);
				}
			});
	
	$scope.$watch(function() { return snapshotService.getLatestSnapshotId(); },
			function(n, o) { 
				$scope.latestSnapshotId = n; 
			}
	);
}]);

snapshotModule.factory('snapshotService', ['$http', '$log', function($http, $log){
	var snapshotData = {latestSnapshotId : null,
					currentSnapshotId : null,
					updateStatus : null,
					alsertStatus: null};
	
	var getLatestSnapshotId = function() {
		return snapshotData.latestSnapshotId;
	};
	
	var getCurrentSnapshotId = function() {
		return snapshotData.currentSnapshotId;
	};
	
	var getUpdateStatus = function() {
		return snapshotData.updateStatus;
	};
	
	var getAlertStatus = function() {
		return snapshotData.alertStatus;
	};
	
	var setLatestSnapshotId = function(latestSnapshotId) {
		snapshotData.latestSnapshotId = latestSnapshotId;
	};
	
	var setCurrentSnapshotId = function(currentSnapshotId) {
		snapshotData.currentSnapshotId = currentSnapshotId;
	};
	
	var setUpdateStatus = function(updateStatus) {
		snapshotData.updateStatus = updateStatus;
	};
	
	var setAlertStatus = function(alertStatus) {
		snapshotData.alertStatus = alertStatus;
	};
	
	var getSnapshotById = function(snapshotId) {
		var url = '/af/getSnapshot/' + snapshotId;
		return $http.get(url);
	};
	
	var getSnapshotNoDetail = function() {
		var url = '/af/getSnapshotNoDetail';
		return $http.get(url);
	}; 
	
	var initSnapshot = function() {
		var url = '/af/getSnapshot';
		return $http.get(url);
	};
	
	return {getSnapshotById : getSnapshotById,
			getSnapshotNoDetail : getSnapshotNoDetail,
			initSnapshot : initSnapshot,
			getLatestSnapshotId : getLatestSnapshotId,
			getCurrentSnapshotId : getCurrentSnapshotId,
			getUpdateStatus : getUpdateStatus,
			getAlertStatus : getAlertStatus,
			setLatestSnapshotId : setLatestSnapshotId,
			setCurrentSnapshotId : setCurrentSnapshotId,
			setUpdateStatus : setUpdateStatus,
			setAlertStatus : setAlertStatus};
}]);