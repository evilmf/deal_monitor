'use strict';

var snapshotModule = angular.module('myApp.snapshot', ['ngRoute', 'ngResource', 'ngCookies']);

snapshotModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/snapshot', {
    templateUrl: 'snapshot/snapshot.html',
    controller: 'snapshotCtrl'
  });
}]);

snapshotModule.controller('snapshotCtrl', ['snapshotService', '$log', '$rootScope', '$interval', 'usSpinnerService', '$cookies', function(snapshotService, $log, $rootScope, $interval, usSpinnerService, $cookies) {
	$rootScope.latestSnapshotId = null;
	$rootScope.currentSnapshotId = null;
	$rootScope.snapshotDetail = null;
	$rootScope.snapshotTimestamp = null;
	$rootScope.updateOn = true;
	$rootScope.alertOn = true;
	$rootScope.orderByOption = [{'field': 'priceDiscount', 'asc': true, 'name': 'Price: ASC'},
	                            {'field': 'priceDiscount', 'asc': false, 'name': 'Price: DESC'},
	                            {'field': 'discount', 'asc': false, 'name': '% Off: DESC'},
	                            {'field': 'discount', 'asc': true, 'name': '% Off: ASC'}
	                            ];
	
	var inProgress = false;
	
	var getSnapshotById = function(id) {
		inProgress = true;
		usSpinnerService.spin('loading');
		snapshotService.getSnapshotById(id).success(function(res) {
			$rootScope.snapshotDetail = res['snapshotDetail'];
			$rootScope.snapshotTimestamp = res['createDate'];
		}).
		error(function(res) {

		}).
		finally(function() {
			if($rootScope.currentSnapshotId == id) {
				usSpinnerService.stop('loading');
			}
			inProgress = false;
		});
	};
	
	var initSnapshot = function() {
		if(inProgress) {
			return;
		}
		
		inProgress = true;
		usSpinnerService.spin('loading');
		snapshotService.initSnapshot().success(function(res) {
			$rootScope.currentSnapshotId = res['snapshotId'];
			$rootScope.latestSnapshotId = res['snapshotId'];
			$rootScope.snapshotDetail = res['snapshotDetail'];
			$rootScope.snapshotTimestamp = res['createDate'];
		}).
		error(function(res) {

		}).
		finally(function() {
			inProgress = false;
			usSpinnerService.stop('loading');
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
			return;
		}
		
		inProgress = true;
		var sid = $rootScope.latestSnapshotId + 1;
		snapshotService.getSnapshotById(sid).success(function(res) {
			if(res['snapshotDetail'] != null) {
				usSpinnerService.spin('loading');
				$rootScope.snapshotDetail = res['snapshotDetail'];
				$rootScope.snapshotTimestamp = res['createDate'];
				$rootScope.latestSnapshotId = res['snapshotId'];
				$rootScope.currentSnapshotId = res['snapshotId'];
			}
		}).
		error(function(res) {

		}).
		finally(function() {
			inProgress = false;
			usSpinnerService.stop('loading');
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

snapshotModule.factory('snapshotService', ['$http', '$log', 'orderFilter', 'categorizeFilter', 'discountFilter', 'priceFilter', '$cookies',
                                           function($http, $log, orderFilter, categorizeFilter, discountFilter, priceFilter, $cookies){
	var getSnapshotById = function(snapshotId) {
		var url = '/getSnapshot/' + snapshotId;
		return $http.get(url);
	};
	
	var getSnapshotNoDetail = function() {
		var url = '/getSnapshotNoDetail';
		return $http.get(url);
	}; 
	
	var initSnapshot = function() {
		var url = '/getSnapshot';
		return $http.get(url);
	};
	
	var getSnapshotContent = function(products) {
		var categorizedProducts = categorizeFilter(products);
		categorizedProducts = discountFilter(categorizedProducts);
		categorizedProducts = priceFilter(categorizedProducts);
		
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
		filteredProducts['percentOff'] = function() { return Math.round(this.discount * 100); };
		filteredProducts['ebaySearchKeyword'] = function() { return encodeURIComponent(this.productName + ' ' + this.brandName); };
		filteredProducts['parsedImageUrl'] = function() { if(angular.equals(this.brandName, 'abercrombie & fitch') || angular.equals(this.brandName, 'hollister')) { return this.images[0] + '?$product-anf-v1$&$category-anf-v1$&wid=206&hei=206';} else { return this.images[0]; } };
		
		var tpl = '<div style="clear: both;"></div>{{#new}}' 
			+ '<div class="prod-cnt pull-left">'
			+ '<div class="prod-img-cnt">'
			+ '<span class="new">NEW</span>'
			+ '<a target="_blank" href="{{productUrl}}"><img class="prod-img" src="{{parsedImageUrl}}" /></a></div>'
			+ '<div class="prod-brand"><span>{{brandName}}</span></div>'
			+ '<div class="prod-name"><a href="#">{{productName}}</a></div>'
			+ '<div class="prod-price"><a target=_blank href="http://www.ebay.com/sch/i.html?_nkw={{ebaySearchKeyword}}">${{priceDiscount}} - ${{priceRegular}}</a><div class="prod-cat"><a href="#">{{categoryName}}</a></div></div>'
			+ '<div class="prod-cnt-foot"><div class="prod-cnt-foot-left pull-left">{{percentOff}}% OFF</div><div class="prod-cnt-foot-right pull-left">{{genderName}}</div></div>'
			+ '</div>'
			+ '{{/new}}'
			+ '<div style="clear: both;"></div>'
			+ '{{#existing}}' 
			+ '<div class="prod-cnt pull-left">'
			+ '<div class="prod-img-cnt">'
			+ '<a target="_blank" href="{{productUrl}}"><img class="prod-img" src="{{parsedImageUrl}}" /></a></div>'
			+ '<div class="prod-brand"><span>{{brandName}}</span></div>'
			+ '<div class="prod-name"><a href="#">{{productName}}</a></div>'
			+ '<div class="prod-price"><a target=_blank href="http://www.ebay.com/sch/i.html?_nkw={{ebaySearchKeyword}}">${{priceDiscount}} - ${{priceRegular}}</a><div class="prod-cat"><a href="#">{{categoryName}}</a></div></div>'
			+ '<div class="prod-cnt-foot"><div class="prod-cnt-foot-left pull-left">{{percentOff}}% OFF</div><div class="prod-cnt-foot-right pull-left">{{genderName}}</div></div>'
			+ '</div>'
			+ '{{/existing}}'
			+ '<div style="clear: both;"></div>';
		
		var rendered = Mustache.render(tpl, filteredProducts);
		
		return rendered;
	};
	
	return {getSnapshotById : getSnapshotById,
			getSnapshotNoDetail : getSnapshotNoDetail,
			initSnapshot : initSnapshot,
			getSnapshotContent : getSnapshotContent};
}]);

snapshotModule.filter('categorize', ['$log', 'snapshotFilterService', '$cookies', function($log, snapshotFilterService, $cookies){
	return function(products, filteredCategory) {
		var filteredProducts = {};
		
		if(filteredCategory == undefined) {
			filteredCategory = snapshotFilterService.loadCategoryFromCookies();
		}
		
		if(!snapshotFilterService.hasFilteredCategory(filteredCategory)) {
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

snapshotModule.filter('order', ['$log', 'snapshotSettingService', '$cookies', function($log, snapshotSettingService, $cookies) {
	
	var orderBy = function(products, orderBy) {
		products.sort(function(a, b) {
			var result = 0;
			for(var i = 0; i < orderBy.length; i++) {
				result = (a[orderBy[i]['field']] - b[orderBy[i]['field']]) * (orderBy[i]['asc'] ? 1 : -1);
				
				if(result != 0) 
					break;
			}
			
			return result;
		});
	};
	
	return function(products) {
		var orderedProducts = [];
		var setting = snapshotSettingService.loadSetting();
		
		angular.forEach(products, function(product, productId) {
			orderedProducts.push(product);
		});
		
		if(orderedProducts.length > 1) {
			orderBy(orderedProducts, setting.orderBy);
		}
		
		return orderedProducts;
	};
}]);

snapshotModule.filter('discount', ['$log', 'snapshotSettingService', '$cookies', function($log, snapshotSettingService, $cookies) {
	return function(products) {
		var setting = snapshotSettingService.loadSetting();
		
		var filteredProducts = {};
		
		angular.forEach(products, function(product, productId) {
			try {
				if(product.discount >= setting.minDiscount / 100 && product.discount <= setting.maxDiscount / 100) {
					filteredProducts[productId] = product;
				}
			}
			catch (error) {
				$log.log('[Error] Un-expected exception when processing product through discount filter for productId: ' + productId);
			}
		});
		
		return filteredProducts;
	};
}]);

snapshotModule.filter('price', ['$log', 'snapshotSettingService', '$cookies', function($log, snapshotSettingService, $cookies) {
	return function(products) {
		var setting = snapshotSettingService.loadSetting();
		
		var filteredProducts = {};
		
		angular.forEach(products, function(product, productId) {
			try {
				if(product.priceDiscount >= setting.minPrice && product.priceDiscount <= setting.maxPrice) {
					filteredProducts[productId] = product;
				}
			}
			catch (error) {
				$log.log('[Error] Un-expected exception when processing product through price filter for productId: ' + productId);
			}
		});
		
		return filteredProducts;
	};
}]);

snapshotModule.directive('snapshotDetail', ['$rootScope', '$log', 'orderFilter', 'categorizeFilter', 'snapshotService', '$compile', '$cookies',
                                            function($rootScope, $log, orderFilter, categorizeFilter, snapshotService, $compile, $cookies) {
	return {
		restrict: 'E',
		link: function(scope, element, attrs) {
	        $rootScope.$watch('snapshotDetail', function(o, n) {
	        	var content = snapshotService.getSnapshotContent($rootScope.snapshotDetail);
	        	element.html(content);
	        }, true);
	        
	        $rootScope.$watch('filteredCategory', function(o, n) {
	        	var content = snapshotService.getSnapshotContent($rootScope.snapshotDetail);
	        	element.html(content);
	        }, true);
	        
	        $rootScope.$watch('setting', function(o, n) {
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
