app.controller('ListEditSubscriptionCtrl', ['$scope','$timeout','$http','$state','toaster','$localStorage',
						function($scope, $timeout, $http, $state, toaster, $localStorage) {
							$scope.getData = function() {
								$scope.isLoading = true;
								$scope.itemsByPage = 10;
								$http({
									method : 'GET',
									url : "retrieveMessageSubscriptions.do"
								}).then(function successCallback(response) {
													if (response.data.result != 'success') {
														toaster.pop('error','',response.data.msg);
														return;
													}
													$scope.rowCollectionPage = response.data.records;
													$scope.isLoading = false;
													$scope.conllectionLength =  $scope.rowCollectionPage.length;
												},
												function errorCallback(response) {
													$scope.isLoading = false;
													toaster.pop('error', '',"server error");
												});
							};
							var rowHeight = $localStorage.user.rowHeight || 1;
							$scope.rowstyle = {
								'padding' : rowHeight + 'px 15px'
							};
							$scope.changerowstyle = function(rowHeightCode) {
								$http({
									method : 'GET',
									url : "updateUserRowHeightCode.do",
									params : {
										"rowHeightCode" : rowHeightCode,
										"userId" : $localStorage.user.id
									}
								}).then(function successCallback(response) {
													if (response.data.result != 'success') {
														toaster.pop('error','',response.data.msg);
														return;
													}
													var rowHeight = response.data.records;
													$localStorage.user.rowHeight = rowHeight;
													$localStorage.user.rowHeightCode = rowHeightCode;
													$scope.rowstyle = {
														'padding' : rowHeight + 'px 15px'
													};
												},
												function errorCallback(response) {
													toaster.pop('error', '', "server error");
												});
							};

							$scope.getData();
							function coverttoexportdataformat(dataCollection) {
								var dataarray = [];
								var keyarray = [ "Requested Date", "Requested Time", "Expiry Date", "Notification For", "Activity","Message Central", "E-mail" ];
								dataarray.push(keyarray);
								angular.forEach(dataCollection,
												function(item, key) {
													var valuearray = [];
													valuearray.push(dataCollection[key].formattedRequestDate+ "");
													valuearray.push(dataCollection[key].formattedRequestTime+ "");
													valuearray.push(dataCollection[key].formattedExpiryDate+ "");
													valuearray.push(dataCollection[key].probillNo+ "");
													valuearray.push(dataCollection[key].activityDescription+ "");
													valuearray.push(dataCollection[key].messageCentral+ "");
													valuearray.push(dataCollection[key].email+ "");
													dataarray.push(valuearray);
												});
								return dataarray;
							}

							$scope.exportcsv = function(exportOption) {
								var datasource = [];
								if (exportOption == 'currentPage') {
									datasource = $scope.displayedCollection;
								} else {
									datasource = $scope.rowCollectionPage;
								}
								$scope.csvfilename = 'MESSAGE_CENTRAL_Active_Subscription.csv';
								return coverttoexportdataformat(datasource);
							}

							$scope.exportpdf = function(exportOption) {
								var datasource = [];
								if (exportOption == 'currentPage') {
									datasource = $scope.displayedCollection;
								} else {
									datasource = $scope.rowCollectionPage;
								}
								var docDefinition1 = {
									pageOrientation : 'landscape',
									content : [ {
										table : {
											headerRows : 1,
											body : coverttoexportdataformat(datasource)
										}
									} ]
								};

								pdfMake.createPdf(docDefinition1).download('MESSAGE_CENTRAL_Active_Subscription.pdf');
							}

							$scope.item_ids = [];
							$scope.check = false;
							$scope.selectAllItems = function() {
								$scope.check = !$scope.check;

								angular.forEach($scope.displayedCollection,
												function(item, key) {
													$scope.displayedCollection[key].selected = $scope.check;
												});
							};

							$scope.editMessageSubscription = function(
									messageSubscription) {
								var param = {
									messageSubscription : messageSubscription
								};
								$state.go('app.messagecentral.updatesubscription',param);
							};
							$scope.removeMessageSubscriptions = function() {
								$scope.isLoading = true;
								var removeItems = "";
								angular.forEach($scope.displayedCollection,
												function(item, key) {
													if ($scope.displayedCollection[key].selected) {
														removeItems += item.id + ",";
													}
												});
								if (removeItems === "" || removeItems === null || typeof removeItems === "undefined") {
									toaster.pop('warning', '',"choose an item");
									$scope.isLoading = false;
									return;
								}
								var jsonData = angular.toJson(removeItems);
								var objectToSerialize = {
									'object' : jsonData
								};
								$http({
									method : 'GET',
									url : "removeMessageSubscriptions.do",
									params : {
										"messageSubscriptionIds" : removeItems
									}
								}).then(function successCallback(response) {
													if (response.data.result != 'success') {
														toaster.pop('error','',response.data.msg);
														return;
													}
													$scope.getData();
													$scope.isLoading = false;
												},
												function errorCallback(response) {
													toaster.pop('error', '',"server error");
													$scope.isLoading = false;
												});
							};

						} ]);
app.directive('searchWatchModel', function() {
	return {
		require : '^stTable',
		scope : {
			searchWatchModel : '='
		},
		link : function(scope, ele, attr, ctrl) {
			var table = ctrl;
			scope.$parent.advsearch = function() {
				scope.$watch('searchWatchModel', function(val) {
					ctrl.search(val);
				});
			}

		}
	};
});
