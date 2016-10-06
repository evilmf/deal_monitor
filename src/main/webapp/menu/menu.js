'use strict';

var menuModule = angular.module('myApp.menu', ['ui.bootstrap']);

menuModule.controller('menuCtrl', ['snapshotService', 'menuService', '$log', '$rootScope', function(snapshotService, menuService, $log, $rootScope) {
	$rootScope.showSidebar = false;
	$rootScope.pageCntStyle = {'padding-left': '10px'};
	$rootScope.searchResult;
	
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
	
	$rootScope.getSearchResult = function(keyword) {
		if(keyword.trim().length >= 3 ) {
			menuService.getSearchResult(keyword).success(function(res) {
				$rootScope.searchResult = res;
			});
		}
		else {
			$rootScope.searchResult = undefined;
		}
	};
	
	$rootScope.spinOpts = {
			  lines: 8 // The number of lines to draw
			, length: 6 // The length of each line
			, width: 3 // The line thickness
			, radius: 7 // The radius of the inner circle
			, scale: 0.70 // Scales overall size of the spinner
			, corners: 1 // Corner roundness (0..1)
			, color: '#000' // #rgb or #rrggbb or array of colors
			, opacity: 0.25 // Opacity of the lines
			, rotate: 3 // The rotation offset
			, direction: 1 // 1: clockwise, -1: counterclockwise
			, speed: 1 // Rounds per second
			, trail: 42 // Afterglow percentage
			, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
			, zIndex: 2e9 // The z-index (defaults to 2000000000)
			//, className: 'spinner' // The CSS class to assign to the spinner
			//, top: '50%' // Top position relative to parent
			//, left: '36%' // Left position relative to parent
			, shadow: false // Whether to render a shadow
			, hwaccel: false // Whether to use hardware acceleration
			, position: 'absolute' // Element positioning
			};
}]);

menuModule.directive('sidebarCtrlBtn', ['$log', '$rootScope', function($log, $rootScope) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {
			if($rootScope.showSidebar) {
				$rootScope.showSidebar = false;
				$rootScope.pageCntStyle = {'padding-left': '10px'};
			}
			else {
				$rootScope.showSidebar = true;
				$rootScope.pageCntStyle = {'padding-left': '50px'};
			}
			
			$rootScope.$apply();
		});
	};
	
	return {link : link};
}]);

menuModule.factory('menuService', ['$http', '$log', function($http, $log){
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
	
	var getSearchResult = function(keyword) {
		var url = '/search';
		return $http.post(url, {'searchKeyword': keyword});
	}; 
	
	var getSearchContent = function(searchResult) {
		var content = {};
		content['searchResult'] = searchResult;
		content['parsedImageUrl'] = function() { if(angular.equals(this.brandName, 'abercrombie & fitch') || angular.equals(this.brandName, 'hollister')) { return this.imageUrl + '?$product-anf-v1$&$category-anf-v1$&wid=94&hei=94';} else { return this.imageUrl; } };
		
		var tpl = '{{#searchResult}}<div class="search-result-cell pull-left">'
			+ '<div class="cell-img-cnt pull-left">'
			+ '<img class="cell-img" src="{{parsedImageUrl}}" />'
			+ '</div>'
			+ '<div class="cell-info pull-left">'
			+ '<div class="search-prod-name"><span>{{productName}}</span></div>'
			+ '<div class="search-prod-brand"><span class="span-block pull-left">{{brandName}}</span><span>{{genderName}}</span></div>'
			+ '<div class="search-prod-category"><span>{{categoryName}}</span></div>'
			+ '<div class="search-prod-price"><span class="span-block pull-left">${{minPrice}} - ${{maxPrice}}</span><span># of Snapshots: {{snapshotCount}}</span></div>'
			+ '</div>'
			+ '<div style="clear: both;"></div>'
			+ '<div class="bottom-border"></div>'				
			+ '</div>{{/searchResult}}';
		
		return searchResult == undefined ? '' : Mustache.render(tpl, content);
	};
	
	return {getNextSnapshotId : getNextSnapshotId,
			getPreSnapshotId : getPreSnapshotId,
			getSearchResult : getSearchResult,
			getSearchContent : getSearchContent};
}]);

menuModule.directive('searchDetail', ['$rootScope', '$log', 'menuService', 
                                      function($rootScope, $log, menuService) {
	return {
		restrict: 'E',
		link: function(scope, element, attrs) {
			$rootScope.$watch('searchResult', function(o, n) {
				var content = menuService.getSearchContent($rootScope.searchResult);
				element.html(content);
			}, true);
		}
	};
	
}]);



















