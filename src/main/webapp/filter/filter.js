'use strict';

var snapshotFilterModule = angular.module('myApp.filter', ['ngCookies']);

snapshotFilterModule.controller('snapshotFilterCtrl', 
		['$log', '$rootScope', 'snapshotFilterService', 
		 function($log, $rootScope, snapshotFilterService) {
	var categories = snapshotFilterService.loadCategory($rootScope.snapshotDetail);
	
	snapshotFilterService.getClassification().then(function(result) {
		var classificationMapping = result['data'];
		$rootScope.categories = snapshotFilterService.mapCategories(categories, classificationMapping);
	});
	
	$rootScope.processCategoryFilter = function(option) {
		$rootScope.filteredCategory = snapshotFilterService.processCategoryFilter(option);
		
		$rootScope.categories[option.brandId]['gender'][option.genderId]['category'][option.categoryId]['selected'] = option.selected;
	};
	
	$rootScope.$watch('categories', function() {
		$rootScope.filterOptions = snapshotFilterService.buildOptions($rootScope.categories);
    	
    	$rootScope.selectedOption = $rootScope.filterOptions[0];
    }, true);
}]);

snapshotFilterModule.factory('snapshotFilterService', ['$log', '$cookies', '$http', function($log, $cookies, $http) {
	var getCategories = function(products) {
		var categoryFilter = loadCategoryFromCookies();
		
		angular.forEach(products, function(product) {
			if(categoryFilter[product['brandId']] == undefined) {
				categoryFilter[product['brandId']] = {};
			}
			
			if(categoryFilter[product['brandId']][product['genderId']] == undefined) {
				categoryFilter[product['brandId']][product['genderId']] = {};
			}
			
			if(categoryFilter[product['brandId']][product['genderId']][product['categoryId']] == undefined) {
				categoryFilter[product['brandId']][product['genderId']][product['categoryId']] = false;
			}
		});
			
		return categoryFilter;
	};
	
	var setCookiesForCategory = function(categories) {
		angular.forEach(categories, function(c, i) {
			//$cookies[i] = JSON.stringify(c);
			var exp = new Date();
			exp.setDate(exp.getDate() + 365);
			$cookies.put(i, JSON.stringify(c), {expires: exp});
		});
	};
	
	var loadCategory = function(products) {
		var category = getCategories(products);
		
		setCookiesForCategory(category);
		
		return category;
	};
	
	var setDefaultCategory = function(products) {
		var categoryFilter = loadCategoryFromCookies();
		var defaultCategory = /outerwear|jacket/i;
		
		angular.forEach(products, function(product) {
			if(categoryFilter[product['brandId']] == undefined) {
				categoryFilter[product['brandId']] = {};
			}
			
			if(categoryFilter[product['brandId']][product['genderId']] == undefined) {
				categoryFilter[product['brandId']][product['genderId']] = {};
			}
			
			categoryFilter[product['brandId']][product['genderId']][product['categoryId']] = 
				product['categoryName'].search(defaultCategory) != -1 ? true : false;
		});
		
		setCookiesForCategory(categoryFilter);
			
		return categoryFilter;
	};
	
	var loadCategoryFromCookies = function() {
		var brands = {};
		
		angular.forEach($cookies.getAll(), function(c, i) {
			try {
				if(brands == null) {
					brands = {};
				}
				
				if(!isNaN(i)) {
				
					var categories = JSON.parse(c);
					var brand = i;
					
					brands[brand] = categories;
				}
			}
			catch(error) {

			}
		});
		
		return brands;
	};
	
	var getClassification = function() {
		var url = '/getClassification';
		var promise = $http.get(url).then(function(result) {
			return result;
		},
		function(reason) {
			
		});
		
		return promise;
	};
	
	var mapCategories = function(allCategories, mapping) {
		var categoriesMap = {};
		if(allCategories != undefined && mapping != undefined) {
			angular.forEach(allCategories, function(genders, brandId){
				if(mapping['brands'][brandId] != undefined) {
					categoriesMap[brandId] = {};
					categoriesMap[brandId]['name'] = mapping['brands'][brandId];
					categoriesMap[brandId]['gender'] = {};
					categoriesMap[brandId]['selected'] = false;
					angular.forEach(genders, function(categories, genderId) {
						if(mapping['genders'][genderId] != undefined) {
							categoriesMap[brandId]['gender'][genderId] = {};
							categoriesMap[brandId]['gender'][genderId]['name'] = mapping['genders'][genderId];
							categoriesMap[brandId]['gender'][genderId]['category'] = {};
							categoriesMap[brandId]['gender'][genderId]['selected'] = false;
							angular.forEach(categories, function(isSelected, categoryId) {
								if(mapping['categories'][categoryId] != undefined) {
									categoriesMap[brandId]['gender'][genderId]['category'][categoryId] = {};
									categoriesMap[brandId]['gender'][genderId]['category'][categoryId]['name'] = mapping['categories'][categoryId];
									categoriesMap[brandId]['gender'][genderId]['category'][categoryId]['selected'] = isSelected;
								}
							});
						}
					}); 
				}
			});
		}
		
		return categoriesMap;
	};
	
	var buildOptions = function(categories) {
		var options = [];
		
		angular.forEach(categories, function(brand, brandId) {
			var brandName = brand['name'];
			angular.forEach(brand['gender'], function(gender, genderId) {
				var genderName = gender['name'];
				var option = {};
				option['name'] = brandName + ' - ' + genderName;
				option['value'] = {};
				option['value']['brandId'] = brandId;
				option['value']['genderId'] = genderId;
				option['value']['category'] = gender['category'];
				
				options.push(option);
			});
		});
		
		return options;
	};
	
	var processCategoryFilter = function(option) {
		var cookieCategories = loadCategoryFromCookies();
		cookieCategories[option.brandId][option.genderId][option.categoryId] = option.selected;
		
		setCookiesForCategory(cookieCategories);
		
		return cookieCategories;
	};
	
	var hasFilteredCategory = function(filteredCategory) {
		var hasFilteredCategory = false;
		
		if(filteredCategory != undefined) {
			angular.forEach(filteredCategory, function(brand, brandId) {
				if(!isNaN(brandId)) {
					hasFilteredCategory = true;
				}
			});
		}
		
		return hasFilteredCategory;
	};
	
	return {getCategories : getCategories,
			getClassification : getClassification,
			loadCategory : loadCategory,
			mapCategories : mapCategories,
			buildOptions : buildOptions,
			processCategoryFilter : processCategoryFilter,
			loadCategoryFromCookies : loadCategoryFromCookies,
			setDefaultCategory : setDefaultCategory,
			hasFilteredCategory : hasFilteredCategory};
}]);
