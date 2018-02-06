'use strict';

app.controller('UpdateSubscriptionCtrl', [ '$scope', '$timeout', '$http','$state', 'toaster', '$localStorage','$stateParams',
		function($scope, $timeout, $http, $state, toaster, $localStorage,$stateParams) {
			var messageSubscription = $stateParams.messageSubscription;
			if ('All Probills' == messageSubscription.probillNo) {
				$scope.probillNo = null;
			} else {
				$scope.probillNo = messageSubscription.probillNo;
			}
			if(0 == messageSubscription.expiryDateInInteger) {
				$scope.expiryDate = "";
				$scope.noExipry = true;
			} else {
				$scope.expiryDate = messageSubscription.formattedExpiryDate;
				$scope.noExipry = false;
			}
			if (messageSubscription.notification == 'M') {
				$scope.messageCentralChecked = true;
			}
			if (messageSubscription.notification == 'E') {
				$scope.emailChecked = true;
				$scope.emailValue = messageSubscription.email;
			}
			if (messageSubscription.notification == 'B') {
				$scope.emailChecked = true;
				$scope.messageCentralChecked = true;
				$scope.emailValue = messageSubscription.email;
			}
			if ("RSC" == messageSubscription.activityCode.trim()) {
				$scope.rescheduledStatus = true;
			}
			if ("DLVY" == messageSubscription.activityCode.trim()) {
				$scope.deliveryStatus = true;
			}
			$scope.disabledbutton = false;
			$scope.buttonText = "Update";
			$scope.updateSubscription = function() {
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
				var id =  messageSubscription.id
				$http({
					method : 'GET',
					url : "updateSubscription.do",
					params : {
						"id":id,
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
						$scope.buttonText = "Update";
						return;
					}
					
					$scope.disabledbutton = false;
					$scope.buttonText = "Update";
					$state.go('app.messagecentral.listeditsubscription'); 
				}, function errorCallback(response) {
					toaster.pop('error', '', "server error");
					$scope.disabledbutton = false;
					$scope.buttonText = "Update";
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
