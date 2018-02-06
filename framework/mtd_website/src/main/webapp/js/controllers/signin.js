'use strict';

/* Controllers */
// signin controller
app.controller('SigninFormController', [ '$scope', '$http', '$state',
		function($scope, $http, $state) {
			$scope.user = {};
			$scope.authError = null;
			$scope.login = function() {
				$scope.authError = null;
				// Try to login
				$http({
					method : 'GET',
					url : "login.do",
					params : {
						userId : $scope.user.username,
						password : $scope.user.password
					}
				}).then(function(response) {
					if (response.data.result == "success") {
						$state.go('app.dashboard-v1');
					} else {
						$scope.authError = response.data.msg;
					}
				}, function(x) {
					$scope.authError = 'Server Error';
				});
			};
		} ]);
