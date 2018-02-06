app.controller('addRouteCtrl', ['$scope', 'valutils', 'toaster', '$modalInstance', '$filter',
    function ($scope, valutils, toaster, $modalInstance, $filter) {
        $scope.flight = {
            airline: "",
            number: "",
            departing: "",
            arriving: "",
            etd: $filter('date')(new Date(), 'yyyy-MM-dd HH:mm'),
            eta: $filter('date')(new Date(), 'yyyy-MM-dd HH:mm'),
            flightData: ""
        };
        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                var flightData = $scope.flight.airline + " " + $scope.flight.number + " " + $scope.flight.departing + " " + $scope.flight.arriving
                    + " " + $filter('date')($scope.flight.etd, 'yyyy-MM-dd HH:mm') + " | " + $filter('date')($scope.flight.eta, 'yyyy-MM-dd HH:mm');
                $modalInstance.close($filter('uppercase')(flightData));
            }
        }
        $scope.buildFlightInfo = function () {
            var ret = "";
            ret += $scope.flight.airline + " ";
            ret += $scope.flight.number + " ";
            ret += $scope.flight.departing + " ";
            ret += $scope.flight.arriving + " ";
            if ($scope.flight.etd) {
                ret += $filter('date')($scope.flight.etd, 'yyyy-MM-dd HH:mm') + " | ";
            }
            ret += $filter('date')($scope.flight.eta, 'yyyy-MM-dd HH:mm') + " ";
            ret =  $filter('uppercase')(ret);
            return ret;
        }

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);