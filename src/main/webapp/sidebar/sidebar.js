'use strict';

var sidebarModule = angular.module('myApp.sidebar', ['ui.bootstrap', 'ngCookies']);

sidebarModule.controller('sidebarCtrl', ['$log', function($log) {
	
}]);

sidebarModule.controller('filterModalCtrl', ['$log', 'snapshotService', function($log, snapshotService) {
	
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

sidebarModule.directive('sidebarAlertBtn', ['$log', '$rootScope', function($log, $rootScope) {
	var link = function(scope, element, attr) {
		var on = 'on';
		var off = 'off';
		
		element.on('click', function(event) {
			if(element.attr('sound') == on) {
				element.attr('sound', off);
				element.removeClass('glyphicon-volume-up');
				element.addClass('glyphicon-volume-off');
				$rootScope.alertOn = false;
				$log.log('[alert] ' + element.attr('sound'));
			}
			else {
				element.attr('sound', on);
				element.removeClass('glyphicon-volume-off');
				element.addClass('glyphicon-volume-up');
				$rootScope.alertOn = true;
				$log.log('[alert] ' + element.attr('sound'));
			}
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('sidebarPlayBtn', ['$log', '$rootScope', function($log, $rootScope) {
	var link = function(scope, element, attr) {
		var on = 'on';
		var off = 'off';
		
		element.on('click', function(event) {
			if(element.attr('play') == on) {
				element.attr('play', off);
				element.removeClass('glyphicon-pause');
				element.addClass('glyphicon-play');
				$rootScope.updateOn = false;
			}
			else {
				element.attr('play', on);
				element.removeClass('glyphicon-play');
				element.addClass('glyphicon-pause');
				$rootScope.updateOn = true;
			}
		});
	};
	
	return {link : link};
}]);

sidebarModule.directive('audioAlert', ['$rootScope', '$log', function($rootScope, $log) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
	        $rootScope.$watch('latestSnapshotId', function(o, n) {
	        	element[0].play();
	        }, true);
	    }
	};
}]);