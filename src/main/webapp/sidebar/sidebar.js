'use strict';

var sidebarModule = angular.module('myApp.sidebar', []);

sidebarModule.controller('sidebarCtrl', ['$log', function($log) {
	
}]);

sidebarModule.directive('sidebarAlertBtn', ['$log', 'snapshotService', function($log, snapshotService) {
	//$log.log('From sidebarAlertBtn');
	var link = function(scope, element, attr) {
		var on = 'on';
		var off = 'off';
		
		element.on('click', function(event) {
			//$log.log('From sidebar click ' + attr['alert']);
			if(element.attr('alert') == on) {
				//$log.log('Setting alert off');
				element.attr('alert', off);
				element.removeClass('glyphicon-volume-up');
				element.addClass('glyphicon-volume-off');
				snapshotService.setAlertStatus(false);
				$log.log('[alert] ' + element.attr('alert'));
			}
			else {
				//$log.log('Setting alert on');
				element.attr('alert', on);
				element.removeClass('glyphicon-volume-off');
				element.addClass('glyphicon-volume-up');
				snapshotService.setAlertStatus(true);
				$log.log('[alert] ' + element.attr('alert'));
			}
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarPlayBtn', ['$log', 'snapshotService', function($log, snapshotService) {
	var link = function(scope, element, attr) {
		var on = 'on';
		var off = 'off';
		
		element.on('click', function(event) {
			//$log.log('From sidebarPlayBtn click ' + attr['play']);
			if(element.attr('play') == on) {
				//$log.log('Setting update off');
				element.attr('play', off);
				element.removeClass('glyphicon-pause');
				element.addClass('glyphicon-play');
				snapshotService.setUpdateStatus(false);
				//$log.log('[Update] ' + element.attr('play'));
			}
			else {
				//$log.log('Setting update on');
				element.attr('play', on);
				element.removeClass('glyphicon-play');
				element.addClass('glyphicon-pause');
				snapshotService.setUpdateStatus(true);
				//$log.log('[Update] ' + element.attr('play'));
			}
		});
	};
	
	return {link : link};
}]);