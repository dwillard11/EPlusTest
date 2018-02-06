app.controller('confirmSaveCtrl', ['$scope', '$filter','valutils', 'codeService', 'toaster', '$modalInstance', '$http', 'record', 'triptype', 'tripmode', 'statusOptions','tag',
    function ($scope,$filter, valutils, codeService, toaster, $modalInstance, $http, record, triptype, tripmode, statusOptions,tag) {
        $scope.tripStatusOption = statusOptions;
        $scope.trip = {
            status: record.status,
            criticalTime: record.criticalTime, //2017-12-12 don’t default “Critical Time” to date.now() $filter('date')(new Date(), "yyyy-MM-dd HH:mm"),
            note: record.note
        };
        $scope.tag = tag;
        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                record.status = $scope.trip.status;
                record.criticalTime = $scope.trip.criticalTime;
                record.note = $scope.trip.note;
                $modalInstance.close(record);
            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);