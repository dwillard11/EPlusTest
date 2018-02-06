'use strict';

/* Controllers */

angular.module('app').controller('AppCtrl',['$scope','$translate','$localStorage','$window','$state','$http','toaster',
						function($scope, $translate, $localStorage, $window,$state,$http,toaster) {
							// add 'ie' classes to html
							var isIE = !!navigator.userAgent.match(/MSIE/i);
							if (isIE) {
								angular.element($window.document.body).addClass('ie');
							}
							if (isSmartDevice($window)) {
								angular.element($window.document.body).addClass('smart')
							}
							;
							
							// config
							$scope.app = {
								name : 'Manitoulin',
								version : '2.2.0',
								// for chart colors
								color : {
									primary : '#7266ba',
									info : '#23b7e5',
									success : '#27c24c',
									warning : '#fad733',
									danger : '#f05050',
									light : '#e8eff0',
									dark : '#3a3f51',
									black : '#1c2b36'
								},
								settings : {
									themeID : 1,
									navbarHeaderColor : 'bg-danger dker bg-gd',
									navbarCollapseColor : 'bg-danger dker bg-gd',
									asideColor : 'bg-dark',

									headerFixed : true,
									asideFixed : false,
									asideFolded : false,
									asideDock : false,
									container : false
								}
							}
							// $localStorage.LocalMessage = '10';
							// save settings to local storage
							if (angular.isDefined($localStorage.settings)) {
								$scope.app.settings = $localStorage.settings;
							} else {
								$localStorage.settings = $scope.app.settings;
							}
							$scope.$watch('app.settings', function() {
								if ($scope.app.settings.asideDock && $scope.app.settings.asideFixed) {
									// aside dock and fixed must set the header
									// fixed.
									$scope.app.settings.headerFixed = true;
								}
								// for box layout, add background image
								$scope.app.settings.container ? angular.element('html').addClass('bg'): angular.element('html').removeClass('bg');
								// save to local storage
								$localStorage.settings = $scope.app.settings;
							}, true);

							$scope.Account = ""
							$scope.AccountDescription = "";

							// angular translate
							$scope.lang = {
								isopen : false
							};
							$scope.langs = {
								en : 'English',
								fr_FR : 'French'
							};
							$scope.selectLang = $scope.langs[$translate.proposedLanguage()] || "English";

							$scope.setLang = function(langKey, $event) {
								
								$.ajax({
											type : "GET",
											url : "switchLanguage.do",
											data : {
												language : langKey
											},
											success : function(data) {
												if (data.result == "success") {
													$scope.selectLang = $scope.langs[langKey];
													$translate.use(langKey);
													//$scope.lang.isopen = !$scope.lang.isopen
													//$scope.$apply();
													//window.location.href = "index.html";
												} else {
													toaster.pop('error', '', 'Can not change language');
													$scope.$apply();
												}
											}
										});
							};

							$.ajax({
										type : "GET",
										url : "getUserInfo.do",
										success : function(data) {
											if (data.result == "success") {
												// set user groups
												var userGroups = data.groups;
												var selectedGroup = data.selectedGroup;
												var topMenus = data.topMenus;
												$scope.userGroups = userGroups;
												$scope.selectedGroup = selectedGroup;
												$scope.topMenus = topMenus;
												
												
												deliverMenus($scope.topMenus);
												

												// set userName
												$scope.userName = data.username;

												// set language
												// $scope.selectLang =
												// data.referLanguage;

												// you can get user info by
												// $localStorage.user in every
												// js file
												$localStorage.user = data.sessionuser;
												// add by Grey begin
												// advice: use angularjs
												// $state.go can pass params
												// from page login to page
												// dashboard)
												$scope.Account = data.sessionuser.account;
												$scope.AccountDescription = data.sessionuser.accountDescription;
												// add by Grey end
												$scope.$apply();
											}
										}
									});

							/*
							 * $scope.topMenus = { isopen: false };
							 * $scope.firstLevelSideMenus = { isopen: false };
							 * $scope.secondLevelSideMenus = { isopen: false };
							 */
							var deliverMenus = function(topMenus) {
								$scope.topFiveMenus = [];
								$scope.moreMenus = [];
								var menuIndex = 0;
								angular.forEach(topMenus,
												function(item, key) {
													if (menuIndex >= 5) {
														menuIndex++;
														$scope.moreMenus.push(topMenus[key]);
													} else {
														menuIndex++;
														$scope.topFiveMenus.push(topMenus[key]);
													}
												});
								if (menuIndex<5) {
									$scope.hiddenMore = "hidden";
								}
							}
							$scope.searchProbill = function() {
								var probillNo = document.getElementById("probillNoforSearch").value;
								toaster.pop('wait','','Loading...');
								$http({
										method : 'GET',
										url : "retrieveProbillByNumber.do",
										params : {
											"probillNo" : probillNo
									}

								}).then(function successCallback(response) {
									if (response.data.result != 'success') {
										toaster.pop('error', '', response.data.msg);
										return;
									}
									var probill = response.data.records;
									var param = {
										probill : probill
									};
									$localStorage.original = 'app.dashboard-v1';
									$state.go('app.tracing.resultforprobill', param);
								}, function errorCallback(response) {
									toaster.pop('error', '', 'can not connect the server');
								});

							
							}
							$scope.getTopMenus = function(groupObj, $event) {
								$scope.selectedGroup = groupObj;
								$.ajax({
									type : "GET",
									url : "getTopMenus.do",
									data : {
										groupName : groupObj.name,
									},
									success : function(data) {
										if (data.result == "success") {
											$scope.topMenus = data.topMenus;
											deliverMenus($scope.topMenus);
											$scope.$apply();
											
										} else {
											toaster.pop('error', '', 'Can not get top menus');
											$scope.$apply();
										}
									}
								});
							};

							$scope.getSideMenus = function(menuObj, menuLevel) {
								$('.app-aside').toggleClass('off-screen');
								$('.navbar-collapse').toggleClass('show');
								$.ajax({
											type : "GET",
											url : "getSideMenus.do",
											data : {
												parentMenuId : menuObj.id,
												sortNum : menuObj.sortNum,
												level : menuLevel
											},
											success : function(data) {
												if (data.result == "success") {
													$scope.firstLevelSideMenus = data.firstLevelSideMenus;
													$scope.secondLevelSideMenus = data.secondLevelSideMenus;
													$scope.$apply();
												} else {
													toaster.pop('error', '', 'Can not get side menus');
													$scope.$apply();
												}
											}
										});
							};

							function isSmartDevice($window) {
								// Adapted from
								// http://www.detectmobilebrowsers.com
								var ua = $window['navigator']['userAgent']
										|| $window['navigator']['vendor']
										|| $window['opera'];
								// Checks for iOs, Android, Blackberry, Opera
								// Mini, and Windows mobile devices
								return (/iPhone|iPod|iPad|Silk|Android|BlackBerry|Opera Mini|IEMobile/).test(ua);
							}

						} ]);

