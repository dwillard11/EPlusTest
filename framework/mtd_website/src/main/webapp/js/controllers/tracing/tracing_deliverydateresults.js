'use strict';

app.controller('DeliveryDateResultsCtrl', [ '$scope', '$timeout', '$http','$state', 'toaster', '$localStorage','$stateParams',
		function($scope, $timeout, $http, $state, toaster, $localStorage, $stateParams) {
			$scope.rowCollectionPage = 	$stateParams.probills;
			$scope.conllectionLength =  $scope.rowCollectionPage.length;
			$scope.beginDate = $stateParams.beginDate;
			$scope.endDate = $stateParams.endDate;
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
			function coverttoexportdataformat(dataCollection) {
				var dataarray = [];
				var keyarray = [ "Probill", "Delivery Date","Delivery Time", "Shipper", "City", "Consignee","City", 
				                 "Pickup Date","Received By","BOL #","PO #","Shipper #", "Weight","Adj. Wgt","Terms"];
				dataarray.push(keyarray);
				angular.forEach(dataCollection,
								function(item, key) {
									var valuearray = [];
									valuearray.push(dataCollection[key].probillNumber+ "");
									valuearray.push(dataCollection[key].formattedDeliveryDate+ "");
									valuearray.push(dataCollection[key].formattedDeliveryTime+ "");
									valuearray.push(dataCollection[key].shipper+ "");
									valuearray.push(dataCollection[key].shipperCity+ "");
									valuearray.push(dataCollection[key].consignee+ "");
									valuearray.push(dataCollection[key].consigneeCity+ "");
									valuearray.push(dataCollection[key].formattedPickupDate+ "");
									valuearray.push(dataCollection[key].receivedBy+ "");
									valuearray.push(dataCollection[key].bolNumber+ "");
									valuearray.push(dataCollection[key].poNumber+ "");
									valuearray.push(dataCollection[key].shipperNumber+ "");
									valuearray.push(dataCollection[key].weight+ "");
									valuearray.push(dataCollection[key].adjWeight+ "");
									valuearray.push(dataCollection[key].terms+ "");
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
				$scope.csvfilename = 'ProBills_by_Delivery_Date_'+$scope.beginDate+'-'+$scope.endDate+'.csv';
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
					pageOrientation: 'landscape',
					content : [ {
						fontSize: 7,
						table : {
							headerRows : 1,
							body : coverttoexportdataformat(datasource)
						}
					} ]
				};

				pdfMake.createPdf(docDefinition1).download('ProBills_by_Delivery_Date_'+$scope.beginDate+'-'+$scope.endDate+'.pdf');
			}
			
			$scope.seeDetails = function(probillNo) {
				toaster.pop('wait', '', 'Loading...');
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
					$localStorage.probills = $scope.rowCollectionPage;
					$localStorage.beginDate = $scope.beginDate;
					$localStorage.endDate = $scope.endDate;
					$localStorage.original = 'app.tracing.deliverydateresults';
					$state.go('app.tracing.resultforprobill', param);
				}, function errorCallback(response) {
					toaster.pop('error', '', 'can not connect the server');
				});
			}
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