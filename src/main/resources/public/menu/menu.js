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
				if(res != undefined && res.length != 0) {
					$rootScope.searchResult = res;
					$rootScope.showSearchResult = true;
				}
				else {
					$rootScope.searchResult = undefined;
					$rootScope.showSearchResult = false;
				}
			});
		}
		else {
			$rootScope.searchResult = undefined;
			$rootScope.showSearchResult = false;
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

menuModule.factory('menuService', ['$http', '$log', '$filter', 'durationFilter', function($http, $log, $filter, durationFilter){
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
		var url = '/service/search';
		return $http.post(url, {'searchKeyword': keyword});
	}; 
	
	var getProductSnapshots = function(productId) {
		var url = '/service/productSnapshots/' + productId;
		return $http.get(url);
	}; 
	
	var getSearchContent = function(searchResult) {
		var content = {};
		content['searchResult'] = searchResult;
		content['parsedImageUrl'] = function() { if(angular.equals(this.brandName, 'abercrombie & fitch') || angular.equals(this.brandName, 'hollister')) { return this.imageUrl + '?$product-anf-v1$&$category-anf-v1$&wid=94&hei=94';} else { return this.imageUrl; } };
		
		var tpl = '<div style="height: 19px; width: 100%; background-color: white;"></div>{{#searchResult}}<div list-product-snapshot productId="{{productId}}" class="search-result-cell pull-left">'
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
			+ '</div><{{/searchResult}}<div style="clear: both;"></div>';
		
		return searchResult == undefined ? '' : Mustache.render(tpl, content);
	};
	
	var getProductSnapshotContent = function(productSnapshots) {
		productSnapshots['activeDateFormatted'] = function() { return $filter('date')(this.activeDate, 'yyyy-MM-dd HH:mm:ss'); };
		productSnapshots['inactiveDateFormatted'] = function() { return $filter('date')(this.inactiveDate, 'yyyy-MM-dd HH:mm:ss'); };
		productSnapshots['durationFormatted'] = function() { return durationFilter(this.duration); };
		
		var tpl = '<table class="table table-hover product-snapshot-table">'
			+ '<thead><tr>'
			+ '<th>Snapshot</th>'
			+ '<th>Price</th>'
			+ '<th>Active Time</th>'
			+ '<th>Inactive Time</th>'
			+ '<th>Duration</th>'
			+ '</tr></thead><tbody>'
			+ '{{#productSnapshotList}}<tr ng-click="setSnapshotId({{snapshotId}})" style="cursor: pointer;">'
			+ '<th scope="row">{{snapshotId}}</th>'
			+ '<td>${{price}}</td>'
			+ '<td>{{activeDateFormatted}}</td>'
			+ '<td>{{inactiveDateFormatted}}</td>'
			+ '<td>{{durationFormatted}}</td>'
			+ '</tr>{{/productSnapshotList}}'
			+ '</tbody></table>';
		
		return productSnapshots == undefined ? undefined : Mustache.render(tpl, productSnapshots);
	};
	
	return {getNextSnapshotId : getNextSnapshotId,
			getPreSnapshotId : getPreSnapshotId,
			getSearchResult : getSearchResult,
			getSearchContent : getSearchContent,
			getProductSnapshots : getProductSnapshots,
			getProductSnapshotContent : getProductSnapshotContent};
}]);

menuModule.directive('searchDetail', ['$rootScope', '$log', '$compile', 'menuService',
                                      function($rootScope, $log, $compile, menuService) {
	return {
		restrict: 'E',
		link: function(scope, element, attrs) {
			$rootScope.$watch('searchResult', function(o, n) {
				var content = menuService.getSearchContent($rootScope.searchResult);
				element.html(content);
				$compile(element.contents())(scope);
			}, true);
		}
	};
}]);

menuModule.directive('listProductSnapshot', ['$rootScope', '$log', '$compile', '$uibModal', 'menuService',
                                     function($rootScope, $log, $compile, $uibModal, menuService) {
	var link = function(scope, element, attr) {
		element.on('click', function(event) {		
			menuService.getProductSnapshots(attr.productid).success(function(res) {
				var content = menuService.getProductSnapshotContent(res);
				
				if(content != undefined) { 
					$uibModal.open({
						template : content,
						windowClass : 'product-snapshot-list-modal',
						backdrop : true,
						//backdropClass : 'filter-backdrop-fade',
						controller: 'productSnapshotModalCtrl'
					});
				}
			});
			
		});
	};
	
	return {restrict: 'A', link : link};
}]);

menuModule.filter('duration', ['$log', function($log){
	return function(duration) {
		var durationString = '';
		
		duration = Math.round(duration);
		
		var day = Math.floor(duration / (60 * 60 * 24));
		duration = duration % (60 * 60 * 24);
		var hour = Math.floor(duration / (60 * 60));
		duration = duration % (60 * 60);
		var min = Math.floor(duration / 60);
		var sec = Math.floor(duration % 60);
		
		if(day > 0) {
			durationString = durationString + day + (day == 1 ? ' day ' : ' days '); 
		} 
		
		if(hour > 0) {
			durationString = durationString + hour + (hour == 1 ? ' hour ' : ' hours ');
		}
		
		if(min > 0) {
			durationString = durationString + min + (min == 1 ? ' min ' : ' mins ');
		} 
		
		if(sec > 0) {
			durationString = durationString + sec + (sec == 1 ? ' sec' : ' secs');
		}
		
		return durationString;
	};
}]);

menuModule.controller('productSnapshotModalCtrl', ['$log', '$rootScope', function($log, $rootScope) {	
	$rootScope.setSnapshotId = function(id) {
		$rootScope.currentSnapshotId = id;
		//$rootScope.showSearchResult = false;
	};
}]);

menuModule.controller('dropdownCtrl', ['$log', '$rootScope', function($log, $rootScope) {
	$rootScope.hideSearchResult = function() {
		$rootScope.showSearchResult = false;
		//$log.log('Hide search result.');
	};
}]);















