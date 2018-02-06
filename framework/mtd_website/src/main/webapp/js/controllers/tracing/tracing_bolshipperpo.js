app.controller('TracingByBolShipperPoCtrl', [ '$scope', 'toaster','$http', '$state', '$localStorage',
		function($scope, toaster,$http, $state,$localStorage) {
			$scope.disabledbutton = false;
			$scope.buttonText = "Submit";	
			$scope.accountType = "C";
			$scope.searchbybolshipperpo = function() {
				$scope.disabledbutton = true;
				$scope.buttonText = "Loading...";
				var numberValue = $scope.numberValue;
				var numberType = $scope.numberType;
				var accountType = $scope.accountType;
				$http({
					method : 'GET',
					url : "retrieveProbillByByBolShipperPo.do",
					params : {
						"numberValue" : numberValue,
						"numberType" : numberType,
						"accountType":accountType
					}

				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						toaster.pop('error', '', response.data.msg);
						$scope.disabledbutton = false;
						$scope.buttonText = "Submit";
						return;
					}
					var probill = response.data.records;
					var param = {
						probill : probill
					};
					$scope.disabledbutton = false;
					$scope.buttonText = "Submit";
					$localStorage.original = 'app.tracing.bolshipperpo';
					$state.go('app.tracing.resultforprobill', param);

				}, function errorCallback(response) {
					$scope.disabledbutton = false;
					$scope.buttonText = "Submit";
					toaster.pop('error', '', 'can not connect the server');
					
				});

			}
		} ]);