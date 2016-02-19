'use strict';

var sidebarModule = angular.module('myApp.sidebar', ['ui.bootstrap', 'ngCookies']);

sidebarModule.controller('sidebarCtrl', ['$log', '$rootScope', 'sidebarService', function($log, $rootScope, sidebarService) {
	var setting = sidebarService.loadSetting();
	
	$log.log('Initialize sidebar setting');
	$log.log(setting);
	$rootScope.alert = setting['alert'];
	$rootScope.play = setting['play'];
	$rootScope.audio = setting['audio'];
}]);

sidebarModule.controller('filterModalCtrl', ['$log', 'snapshotService', function($log, snapshotService) {
	
}]);

sidebarModule.factory('sidebarService', ['$log', '$cookies', function($log, $cookies) {
	var loadSettingFromCookies = function() {
		var setting = {};
		
		if($cookies['setting'] != undefined) {
			setting = angular.fromJson($cookies['setting']);
		}
		
		return setting;
	};
	
	var setSetting = function(setting) {
		$cookies['setting'] = angular.toJson(setting);
	};
	
	var getSetting = function() {
		var setting = loadSettingFromCookies();
		
		if(setting['alert'] == undefined) {
			setting['alert'] = true;
		}
		
		if(setting['play'] == undefined) {
			setting['play'] = true;
		}
		
		if(setting['audio'] == undefined) {
			setting['audio'] = 'beep.mp3';
		}
		
		return setting;
	};
	
	var loadSetting = function() {
		var setting = getSetting();
		
		setSetting(setting);
		
		return setting;
	};
	
	var setCookiesSettingForAlert = function(alert) {
		var setting = getSetting();
		
		setting['alert'] = alert;
				
		setSetting(setting);
	};
	
	var setCookiesSettingForPlay = function(play) {
		var setting = getSetting();
		
		setting['play'] = play;
		
		setSetting(setting);
	};
	
	return {loadSettingFromCookies : loadSettingFromCookies,
			getSetting : getSetting,
			loadSetting : loadSetting,
			setCookiesSettingForAlert : setCookiesSettingForAlert,
			setCookiesSettingForPlay : setCookiesSettingForPlay};
}]);

sidebarModule.directive('sidebarFilterBtn', ['$log', '$modal', function($log, $modal) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {
			$modal.open({
				templateUrl : 'filter/snapshotFilter.html',
				windowClass : 'filter-modal-cnt',
				backdrop : true,
				backdropClass : 'filter-backdrop-fade',
				controller: 'snapshotFilterCtrl'
			});
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarAlertBtn', ['$log', '$rootScope', 'sidebarService', function($log, $rootScope, sidebarService) {
	var link = function(scope, element, attr) {
		var e = element.find('a').find('span');
		
		element.ready(function() {
			$log.log('sidebarAlertBtn ready');
			if($rootScope.alert == undefined || $rootScope.alert) {
				e.removeClass('glyphicon-volume-off');
				e.addClass('glyphicon-volume-up');
			}
			else {
				e.removeClass('glyphicon-volume-up');
				e.addClass('glyphicon-volume-off');
			}
			
		});
		
		element.on('click', function(event) {
			if($rootScope.alert == undefined || $rootScope.alert) {
				e.removeClass('glyphicon-volume-up');
				e.addClass('glyphicon-volume-off');
				$rootScope.alert = false;
			}
			else {
				e.removeClass('glyphicon-volume-off');
				e.addClass('glyphicon-volume-up');
				$rootScope.alert = true;
			}
			
			sidebarService.setCookiesSettingForAlert($rootScope.alert);
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarPlayBtn', ['$log', '$rootScope', 'sidebarService', function($log, $rootScope, sidebarService) {
	var link = function(scope, element, attr) {
		var e = element.find('a').find('span');
		
		element.ready(function() {
			$log.log('sidebarPlayBtn ready');
			if($rootScope.play == undefined || $rootScope.play) {
				e.removeClass('glyphicon-play');
				e.addClass('glyphicon-pause');
			}
			else {
				e.removeClass('glyphicon-pause');
				e.addClass('glyphicon-play');
			}
			
		});
		
		element.on('click', function(event) {
			if($rootScope.play == undefined || $rootScope.play) {
				e.removeClass('glyphicon-pause');
				e.addClass('glyphicon-play');
				$rootScope.play = false;
			}
			else {
				e.removeClass('glyphicon-play');
				e.addClass('glyphicon-pause');
				$rootScope.play = true;
			}
			
			sidebarService.setCookiesSettingForPlay($rootScope.play);
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('audioAlert', ['$rootScope', '$log', function($rootScope, $log) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
	        $rootScope.$watch('latestSnapshotId', function(o, n) {
	        	if($rootScope.alert == undefined || $rootScope.alert) {
		        	element[0].play();
	        	}
	        }, true);
	    }
	};
}]);