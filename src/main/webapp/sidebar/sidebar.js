'use strict';

var sidebarModule = angular.module('myApp.sidebar', ['ui.bootstrap', 'ngCookies']);

sidebarModule.controller('sidebarCtrl', ['$log', '$rootScope', 'sidebarService', '$cookies', function($log, $rootScope, sidebarService, $cookies) {
	var setting = sidebarService.loadSetting();
	
	$rootScope.alert = setting['alert'];
	$rootScope.play = setting['play'];
	$rootScope.audio = setting['audio'];
}]);

sidebarModule.controller('filterModalCtrl', ['$log', 'snapshotService', function($log, snapshotService) {
	
}]);

sidebarModule.factory('sidebarService', ['$log', '$cookies', function($log, $cookies) {
	var loadSettingFromCookies = function() {
		var setting = {};
		
		if($cookies.get('setting') != undefined) {
			setting = angular.fromJson($cookies.get('setting'));
		}
		
		return setting;
	};
	
	var setSetting = function(setting) {
		var exp = new Date();
		exp.setDate(exp.getDate() + 365);
		$cookies.put('setting', angular.toJson(setting), {expires: exp});
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

sidebarModule.directive('sidebarFilterBtn', ['$log', '$uibModal', function($log, $uibModal) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {
			$uibModal.open({
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

sidebarModule.directive('sidebarSettingBtn', ['$log', '$uibModal', function($log, $uibModal) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {
			$uibModal.open({
				templateUrl : 'setting/snapshotSetting.html',
				windowClass : 'setting-modal-cnt',
				backdrop : true,
				backdropClass : 'filter-backdrop-fade',
				controller: 'snapshotSettingCtrl'
			});
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarAlertBtn', ['$log', '$rootScope', 'sidebarService', function($log, $rootScope, sidebarService) {
	var link = function(scope, element, attr) {
		var e = element.find('a').find('span');
		
		element.ready(function() {
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