'use strict';

app.controller('TracingDeliveryDateCtrl', [ '$scope', '$timeout', '$http','$state', 'toaster', '$localStorage',
		function($scope, $timeout, $http, $state, toaster, $localStorage) {
			$scope.disabledbutton = false;
			$scope.buttonText = "Show";
			$scope.accountType = "C"
			$scope.retrieveProbillsByDeliveryDate = function() {
				$scope.disabledbutton = true;
				$scope.buttonText = "Loading...";
				var beginDate = document.getElementById("beginDate").value;
				var endDate = document.getElementById("endDate").value;
				var accountType = $scope.accountType;
				$http({
					method : 'GET',
					url : "retrieveProbillsByDeliveryDate.do",
					params : {
						"beginDate": beginDate,
						"endDate":endDate,
						"accountType":accountType
					}
				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						toaster.pop('error', '', response.data.msg);
						$scope.disabledbutton = false;
						$scope.buttonText = "Show";
						return;
					}
					$scope.disabledbutton = false;
					$scope.buttonText = "Show";
					var params = {
						probills:response.data.records,
						beginDate:response.data.beginDate,
						endDate:response.data.endDate
					}
					$state.go('app.tracing.deliverydateresults',params);
				}, function errorCallback(response) {
					toaster.pop('error', '', "server error");
					$scope.disabledbutton = false;
					$scope.buttonText = "Show";
				});
			}
		} ]);
