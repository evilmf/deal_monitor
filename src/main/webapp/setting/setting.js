'use strict';

var snapshotSettingModule = angular.module('myApp.setting', ['ngCookies']);

snapshotSettingModule.controller('snapshotSettingCtrl', ['$log', '$rootScope', 'snapshotSettingService', function($log, $rootScope, snapshotSettingService) {
	$rootScope.setting = snapshotSettingService.loadSetting();
	
	$rootScope.setMinDiscount = function(minDiscount) {
		$rootScope.setting = snapshotSettingService.setMinDiscount(minDiscount);
	};
	
	$rootScope.setMaxDiscount = function(maxDiscount) {
		$rootScope.setting = snapshotSettingService.setMaxDiscount(maxDiscount);
	};
	
	$rootScope.setMinPrice = function(minPrice) {
		$rootScope.setting = snapshotSettingService.setMinPrice(minPrice);
	};
	
	$rootScope.setMaxPrice = function(maxPrice) {
		$rootScope.setting = snapshotSettingService.setMaxPrice(maxPrice);
	};
//	
//	$rootScope.setAlertSound = function(alertSound) {
//		snapshotSettingService.setAlertSound(alertSound);
//		$rootScope.setting.alertSound = alertSound;
//	};
//	
//	$rootScope.selectedOption = $rootScope.setting.alertSound;
}]);

snapshotSettingModule.factory('snapshotSettingService', ['$log', '$cookies', '$http', function($log, $cookies, $http) {
	var getSetting = function() {
		var setting = loadSettingFromCookies();
		
		if(setting.minDiscount == undefined) {
			setting.minDiscount = 50;
		}
		
		if(setting.maxDiscount == undefined) {
			setting.maxDiscount = 100;
		}
		
		if(setting.alertSound == undefined) {
			setting.alertSound = 'beep.mp3';
		}
		
		if(setting.minPrice == undefined) {
			setting.minPrice = 0;
		}
		
		if(setting.maxPrice == undefined) {
			setting.maxPrice = 9999;
		}
		
//		if(setting.alertSound == undefined) {
//			setting.alertSound = 'beep.mp3';
//		}
		
		return setting;
	};
	
	var loadSettingFromCookies = function() {
		var setting = {};
		
		angular.forEach($cookies, function(c, i) {
			try {
				if(isNaN(i) && i.toLowerCase() == 'setting') {
					setting = JSON.parse(c);
				}
			}
			catch(error) {
				$log.log(error.message);
			}
		});
				
		return setting;
	};
	
	var setCookiesForSetting = function(setting) {
		$cookies['setting'] = JSON.stringify(setting);
	};
	
	var loadSetting = function() {
		var setting = getSetting();
		
		setCookiesForSetting(setting);
		
		return setting;
	};
	
	var setMinDiscount = function(minDiscount) {
		var setting = loadSettingFromCookies();
		
		minDiscount = parseFloat(minDiscount);
		
		if(!isNaN(minDiscount)) {
			setting.minDiscount = minDiscount;
		}
		
		setCookiesForSetting(setting);
		
		return setting;
	};
	
	var setMaxDiscount = function(maxDiscount) {
		var setting = loadSettingFromCookies();
		
		maxDiscount = parseFloat(maxDiscount);
		
		if(!isNaN(maxDiscount)) {
			setting.maxDiscount = maxDiscount;
		}
		
		setCookiesForSetting(setting);
		
		return setting;
	};
	
	var setMinPrice = function(minPrice) {
		var setting = loadSettingFromCookies();
		
		minPrice = parseFloat(minPrice);
		
		if(!isNaN(minPrice)) {
			setting.minPrice = minPrice;
		}
		
		setCookiesForSetting(setting);
			
		return setting;
	};
	
	var setMaxPrice = function(maxPrice) {
		var setting = loadSettingFromCookies();
		
		maxPrice = parseFloat(maxPrice);
		
		if(!isNaN(maxPrice)) {
			setting.maxPrice = maxPrice;
		}
		
		setCookiesForSetting(setting);
		
		return setting;
	};
	
//	var setAlertSound = function(alertSound) {
//		var setting = loadSettingFromCookies();
//		
//		setting.alertSound = alertSound;
//		
//		setCookiesForSetting(setting);
//		
//		return setting;
//	};
	
	return {getSetting : getSetting,
			loadSettingFromCookies : loadSettingFromCookies,
			loadSetting : loadSetting,
			setMinDiscount : setMinDiscount,
			setMaxDiscount : setMaxDiscount,
			setMinPrice : setMinPrice,
			setMaxPrice : setMaxPrice//,
			/*setAlertSound : setAlertSound*/}; 
}]);








