'use strict';

app.controller('NewSubscriptionCtrl', [ '$scope', '$timeout', '$http','$state', 'toaster', '$localStorage',
		function($scope, $timeout, $http, $state, toaster, $localStorage) {
			$scope.disabledbutton = false;
			$scope.buttonText = "Submit";
			$scope.createMessageSubscription = function() {
				$scope.disabledbutton = true;
				$scope.buttonText = "Loading...";
				var probillNo = $scope.probillNo;
				var expiryDate = document.getElementById("expiryDate").value;
				var noExipry = $scope.noExipry||false;
				var rescheduledStatus = $scope.rescheduledStatus||false;
				var deliveryStatus = $scope.deliveryStatus||false;
				var emailChecked = $scope.emailChecked||false;
				var emailValue = $scope.emailValue;
				var messageCentralChecked = $scope.messageCentralChecked||false;
				$http({
					method : 'GET',
					url : "createMessageSubscription.do",
					params : {
						"noExpiry": noExipry,
						"expiryDate":expiryDate,
						"rescheduledStatus":rescheduledStatus,
						"deliveryStatus":deliveryStatus,
						"emailChecked":emailChecked,
						"emailValue":emailValue,
						"messageCentralChecked":messageCentralChecked,
						"probillNo":probillNo
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
					$state.go('app.messagecentral.listeditsubscription');
				}, function errorCallback(response) {
					toaster.pop('error', '', "server error");
					$scope.disabledbutton = false;
					$scope.buttonText = "Submit";
				});
			}
		} ]);

app.directive("limitTo", [function() {
    return {
        restrict: "A",
        link: function(scope, elem, attrs) {
            var limit = parseInt(attrs.limitTo);
            angular.element(elem).on("keypress", function(e) {
                if (this.value.length == limit) e.preventDefault();
            });
        }
    }
}]);
