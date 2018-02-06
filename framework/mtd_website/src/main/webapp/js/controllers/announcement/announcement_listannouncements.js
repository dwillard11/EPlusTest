'use strict';


app.controller('announcementsCtrl', ['$scope', '$http',function($scope,$http) {
	$scope.announcements = [];
	retrieveAnnouncements();
	function retrieveAnnouncements() {
        $http({
            method: 'GET',
            url: "retrieveAnnouncements.do"
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    return;
                }
                $scope.announcements = response.data.records;
            },
            function errorCallback(response) {
            });
    };
}]);
