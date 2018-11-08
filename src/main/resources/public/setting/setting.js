'use strict';

var snapshotSettingModule = angular.module('myApp.setting', ['ngCookies', 'ngSanitize', 'ui.select']);

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
	
	$rootScope.setOrderBy = function(orderBy) {
		$rootScope.setting = snapshotSettingService.setOrderBy(orderBy);
	};
	
//	
//	$rootScope.setAlertSound = function(alertSound) {
//		snapshotSettingService.setAlertSound(alertSound);
//		$rootScope.setting.alertSound = alertSound;
//	};
//	
//	$rootScope.selectedOption = $rootScope.setting.alertSound;
}]);

snapshotSettingModule.factory('snapshotSettingService', ['$log', '$cookies', '$http', '$rootScope', function($log, $cookies, $http, $rootScope) {
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
		
		if(angular.isUndefined(setting.orderBy) || !angular.isArray(setting.orderBy)) {
			setting.orderBy = [$rootScope.orderByOption[0],
			                   $rootScope.orderByOption[2]];
		}
		
//		if(setting.alertSound == undefined) {
//			setting.alertSound = 'beep.mp3';
//		}
		
		return setting;
	};
	
	var loadSettingFromCookies = function() {
		var setting = {};
		
		angular.forEach($cookies.getAll(), function(c, i) {
			try {
				if(isNaN(i) && i.toLowerCase() == 'setting') {
					setting = JSON.parse(c);
				}
			}
			catch(error) {

			}
		});
				
		return setting;
	};
	
	var setCookiesForSetting = function(setting) {
		//$cookies['setting'] = JSON.stringify(setting);
		var exp = new Date();
		exp.setDate(exp.getDate() + 365);
		$cookies.put('setting', JSON.stringify(setting), {expires: exp});
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
	
	var setOrderBy = function(orderBy) {
		var setting = loadSettingFromCookies();
		
		setting.orderBy = orderBy;
		
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
			setMaxPrice : setMaxPrice,
			setOrderBy : setOrderBy
			/*setAlertSound : setAlertSound*/}; 
}]);

snapshotSettingModule.filter('orderByOptionFilter', ['$log', '$rootScope', function($log, $rootScope) {
	return function(allOptions) {
		var filteredOptions = [];
		var selectedOptions = $rootScope.setting.orderBy;
		
		for(var i = 0; i < allOptions.length; i++) {
			var selected = false;
			for(var j = 0; j < selectedOptions.length; j++) {
				if(selectedOptions[j]['name'].split(':')[0].toLowerCase() == allOptions[i]['name'].split(':')[0].toLowerCase()) {
					selected = true;
					break;
				}
			}
			
			if(!selected) {
				filteredOptions.push(allOptions[i]);
			}
		}
		
		return filteredOptions;
	};
}]);






