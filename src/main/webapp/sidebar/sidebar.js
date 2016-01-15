'use strict';

var sidebarModule = angular.module('myApp.sidebar', ['ui.bootstrap']);

sidebarModule.controller('sidebarCtrl', ['$log', function($log) {
	
}]);

sidebarModule.directive('sidebarFilterBtn', ['$log', 'snapshotService', '$modal', function($log, snapshotService, $modal) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {
			$modal.open({
				templateUrl : 'filter/snapshotFilter.html',
				windowClass : 'filter-modal-cnt',
				backdrop : true,
				backdropClass : 'filter-backdrop-fade'
			});
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarAlertBtn', ['$log', 'snapshotService', function($log, snapshotService) {
	var link = function(scope, element, attr) {
		var on = 'on';
		var off = 'off';
		
		element.on('click', function(event) {
			if(element.attr('sounc') == on) {
				element.attr('sound', off);
				element.removeClass('glyphicon-volume-up');
				element.addClass('glyphicon-volume-off');
				snapshotService.setAlertStatus(false);
				$log.log('[alert] ' + element.attr('sound'));
			}
			else {
				element.attr('sound', on);
				element.removeClass('glyphicon-volume-off');
				element.addClass('glyphicon-volume-up');
				snapshotService.setAlertStatus(true);
				$log.log('[alert] ' + element.attr('sound'));
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
			if(element.attr('play') == on) {
				element.attr('play', off);
				element.removeClass('glyphicon-pause');
				element.addClass('glyphicon-play');
				snapshotService.setUpdateStatus(false);
			}
			else {
				element.attr('play', on);
				element.removeClass('glyphicon-play');
				element.addClass('glyphicon-pause');
				snapshotService.setUpdateStatus(true);
			}
		});
	};
	
	return {link : link};
}]);