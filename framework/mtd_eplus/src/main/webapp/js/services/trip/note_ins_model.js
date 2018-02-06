app.controller('NoteInsCtrl', ['$scope', '$localStorage', 'toaster', '$http', '$modalInstance', 'mode', 'tripid', 'record', 'uniqueKey',
    function ($scope, $localStorage, toaster, $http, $modalInstance, mode, tripid, record, uniqueKey) {
    if (mode == "add") {
        $scope.f = {
            tripid: tripid,
            noteDesc: "",
            noteDate: "",
            noteUser: new Date(),
            id:""
        }
    }
    if (mode == "edit") {
        $scope.uniqueKey = uniqueKey;
        var item = record;

        $scope.f = {
            tripid: tripid,
            noteDesc: item.content,
            noteDate: item.updateTime,
            noteUser: item.updatedBy,
            id: item.id
        };
    }

    $scope.submitted = false;
    $scope.ok = function (isValid) {
        if (!isValid) {
            $scope.submitted = true;
            return;
        } else {
            $scope.savenote($scope.f.id, tripid, $scope.f.noteDesc);
        }
    };
    $scope.savenote = function (id, tripid, desc) {
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/saveNote.do",
            params: {
                id: id,
                desc: desc,
                tripid: tripid
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $modalInstance.close(true);
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);
