'use strict';

var snapshotModule = angular.module('myApp.snapshot', ['ngRoute', 'ngResource']);

snapshotModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/snapshot', {
    templateUrl: 'snapshot/snapshot.html',
    controller: 'snapshotCtrl'
  });
}]);

snapshotModule.controller('snapshotCtrl', ['snapshotService', '$log', '$rootScope', '$interval', function(snapshotService, $log, $rootScope, $interval) {
	$rootScope.latestSnapshotId = null;
	$rootScope.currentSnapshotId = null;
	$rootScope.snapshotDetail = null;
	$rootScope.updateOn = true;
	$rootScope.alertOn = true;
	
	var inProgress = false;
	
	var getSnapshotById = function(id) {
		inProgress = true;
		snapshotService.getSnapshotById(id).success(function(res) {
			$log.log('getSnapshotById - setting $scope.snapshotDetail');
			$rootScope.snapshotDetail = res['snapshotDetail'];
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
			return;
		}
		
		inProgress = true;
		snapshotService.initSnapshot().success(function(res) {
			$log.log('initSnapshot - setting $scope.snapshotDetail');
			$rootScope.currentSnapshotId = res['snapshotId'];
			$rootScope.latestSnapshotId = res['snapshotId'];
			$rootScope.snapshotDetail = res['snapshotDetail'];
		}).
		error(function(res) {
			$log.log(res);
		}).
		finally(function() {
			inProgress = false;
		});
	};
	
	var getSnapshotNoDetail = function() {
		if(inProgress) {
			return;
		}
		
		inProgress = true;
		snapshotService.getSnapshotNoDetail().success(function(res) {
			if(res['snapshotId'] > $rootScope.latestSnapshotId) {
				$rootScope.latestSnapshotId = res['snapshotId'];
			}
		}).
		finally(function() {
			inProgress = false;
		});
	};
	
	var getLatestSnapshot = function() {
		if(inProgress) {
			$log.log('Skipping... Getting snapshot in progress.');
			return;
		}
		
		inProgress = true;
		var sid = $rootScope.latestSnapshotId + 1;
		snapshotService.getSnapshotById(sid).success(function(res) {
			if(res['snapshotDetail'] != null) {
				$log.log('snapshotService.getLatestSnapshot');
				$log.log('New snapshot available.');
				$log.log('initSnapshot - setting $scope.snapshotDetail');
				$rootScope.snapshotDetail = res['snapshotDetail'];
				$rootScope.latestSnapshotId = res['snapshotId'];
				$rootScope.currentSnapshotId = res['snapshotId'];
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
			if(!$rootScope.play) {
				return;
			}
			
			if ($rootScope.latestSnapshotId == null) {
				initSnapshot();
			}
			else if($rootScope.currentSnapshotId != $rootScope.latestSnapshotId) {
				getSnapshotNoDetail();
			}
			else {
				getLatestSnapshot();
			}
		}, 5000, 0, false);
	};
	
	initSnapshot();
	
	checkSnapshotUpdate();
	
	$rootScope.$watch(function() { 
			return $rootScope.currentSnapshotId;
		}, 
		function(n, o) {
			if(n != o) {
				getSnapshotById(n);
			}
		}, true);
}]);

snapshotModule.factory('snapshotService', ['$http', '$log', 'orderFilter', 'categorizeFilter', 
                                           function($http, $log, orderFilter, categorizeFilter){
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
	
	var getSnapshotContent = function(products) {
		$log.log(products);
		var categorizedProducts = categorizeFilter(products);
		var filteredProducts = {};
		filteredProducts['new'] = [];
		filteredProducts['existing'] = [];
				
		angular.forEach(categorizedProducts, function(product, productId) {
			if(product.isNew) {
				filteredProducts['new'].push(product);
			}
			else {
				filteredProducts['existing'].push(product);
			}
		});
		
		filteredProducts['new'] = orderFilter(filteredProducts['new']);
		filteredProducts['existing'] = orderFilter(filteredProducts['existing']);
		
		var tpl = '{{#new}}' 
			+ '<div class="prod-cnt pull-left">'
			+ '<div class="prod-img-cnt"><a target="_blank" href="{{productUrl}}"><img class="prod-img" src="{{images.0}}" /></a></div>'
			+ '<div class="prod-brand"><span>{{brandName}}</span></div>'
			+ '<div class="prod-name"><a href="#">{{productName}}</a></div>'
			+ '<div class="prod-price"><a href="#">${{priceDiscount}} - ${{priceRegular}}</a><div class="prod-cat"><a href="#">{{categoryName}}</a></div></div>'
			//+ '<div class="prod-cat"><a href="#">{{categoryName}}</a></div>'
			+ '</div>'
			+ '{{/new}}'
			+ '<div style="clear: both;"><div>'
			+ '{{#existing}}' 
			+ '<div class="prod-cnt pull-left">'
			+ '<div class="prod-img-cnt"><a target="_blank" href="{{productUrl}}"><img class="prod-img" src="{{images.0}}" /></a></div>'
			+ '<div class="prod-brand"><span>{{brandName}}</span></div>'
			+ '<div class="prod-name"><a href="#">{{productName}}</a></div>'
			+ '<div class="prod-price"><a href="#">${{priceDiscount}} - ${{priceRegular}}</a><div class="prod-cat"><a href="#">{{categoryName}}</a></div></div>'
			//+ '<div class="prod-cat"><a href="#">{{categoryName}}</a></div>'
			+ '</div>'
			+ '{{/existing}}';
		
		var rendered = Mustache.render(tpl, filteredProducts);
		
		return rendered;
	};
	
	return {getSnapshotById : getSnapshotById,
			getSnapshotNoDetail : getSnapshotNoDetail,
			initSnapshot : initSnapshot,
			getSnapshotContent : getSnapshotContent};
}]);

snapshotModule.filter('categorize', ['$log', 'snapshotFilterService', function($log, snapshotFilterService){
	return function(products, filteredCategory) {
		$log.log('categorize' + ' ' + (new Date));
		var filteredProducts = {};
		
		if(filteredCategory == undefined) {
			filteredCategory = snapshotFilterService.loadCategoryFromCookies();
		}
		
		if(!snapshotFilterService.hasFilteredCategory(filteredCategory)) {
			$log.log('Set default category');
			filteredCategory = snapshotFilterService.setDefaultCategory(products);
		}
		
		angular.forEach(products, function(product, productId) {
			try {
				if(filteredCategory[product.brandId][product.genderId][product.categoryId]) {
					filteredProducts[productId] = product;
				}
			}
			catch (error) {
				filteredProducts[productId] = product;
			}
		});
		
		return filteredProducts;
	};
}]);

snapshotModule.filter('order', ['$log', function($log) {
	return function(products) {
		$log.log('order');
		var orderedProducts = [];
		
		angular.forEach(products, function(product, productId) {
			orderedProducts.push(product);
		});
		
		if(orderedProducts.length > 1) {
			orderedProducts.sort(function(a, b) {
				return a.priceDiscount - b.priceDiscount;
			});
		}
		
		return orderedProducts;
	};
}]);

snapshotModule.directive('snapshotDetail', ['$rootScope', '$log', 'orderFilter', 'categorizeFilter', 'snapshotService', '$compile',
                                            function($rootScope, $log, orderFilter, categorizeFilter, snapshotService, $compile) {
	return {
		restrict: 'E',
		link: function(scope, element, attrs) {
	        $rootScope.$watch('snapshotDetail', function(o, n) {
	        	$log.log('snapshotDetail changed');
	        	var content = snapshotService.getSnapshotContent($rootScope.snapshotDetail);
	        	element.html(content);
	        }, true);
	        
	        $rootScope.$watch('filteredCategory', function(o, n) {
	        	$log.log('filteredCategory changed');
	        	var content = snapshotService.getSnapshotContent($rootScope.snapshotDetail);
	        	element.html(content);
	        }, true);
	    }
	};
}]);

/*
 * Object {productId: 8139, 
 * images: Array[1], 
 * priceRegular: 20, 
 * priceDiscount: 14, 
 * productUrl: "http://www.abercrombie.com/shop/us/mens-graphic-tees-clearance/do-right-graphic-tee-6197574?ofp=true"…}
 * brandId: 1
 * brandName: "abercrombie & fitch"
 * categoryId: 6
 * categoryName: "graphic tees"
 * discount: 0.3
 * genderId: 2
 * genderName: "mens"
 * images: Array[1]
 * isNew: false
 * priceDiscount: 14
 * priceRegular: 20
 * productCreateDate: 1453161600000
 * productDataId: "6197574"
 * productId: 8139
 * productName: "Do Right Graphic Tee"
 * productUrl: "http://www.abercrombie.com/shop/us/mens-graphic-tees-clearance/do-right-graphic-tee-6197574?ofp=true"
 * snapshotCreateDate: 1453161600000
 * snapshotDetailId: 42662
 * snapshotId: 2243__proto__: Object
 */
