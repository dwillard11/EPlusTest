app.controller('TracingByPickupNumberCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$scope.showit = false;
			$scope.searchbypickupNumber = function() {
				$scope.showit = true;
			}
		} ]);
