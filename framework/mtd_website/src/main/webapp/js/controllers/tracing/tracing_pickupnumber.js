app.controller('TracingByPickupNumberCtrl', [ '$scope', 'toaster','$http','$state',
		function($scope,toaster,$http,$state) {
			$scope.disabledbutton = false;
			$scope.buttonText = "Submit";	
			$scope.showit = false;
			$scope.probill=[];
			$scope.rowCollectionPage = [];
			$scope.searchbypickupNumber = function() {
				$scope.disabledbutton = true;
				$scope.buttonText = "Loading...";
				$scope.showit = false;
				var pickupNumber = $scope.pickupNumber;
				
				$http({
					method : 'GET',
					url : "retrieveProbillByPickupNumber.do",
					params : {
						"pickupNumber" : pickupNumber
					}

				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						toaster.pop('error', '', response.data.msg);
						$scope.disabledbutton = false;
						$scope.buttonText = "Submit";
						return;
					}
					$scope.disabledbutton = false;
					$scope.buttonText = "Submit";
					$scope.showit = true;
					$scope.probill  = response.data.records;
					$scope.rowCollectionPage = response.data.records_extra;
				}, function errorCallback(response) {
					toaster.pop('error', '', 'can not connect the server');
					$scope.disabledbutton = false;
					$scope.buttonText = "Submit";
					$scope.showit = false;
				});
			}
		} ]);
