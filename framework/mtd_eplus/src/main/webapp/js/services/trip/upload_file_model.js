app.controller('uploadctrl', ['$scope', 'valutils', 'codeService', '$localStorage', 'toaster', '$http', '$modalInstance', 'tripid', 'record', 'uniqueKey', function ($scope, valutils, codeService, $localStorage, toaster, $http, $modalInstance, tripid, record, uniqueKey) {
    $scope.d = {
        fileType: "",
        tripid: tripid
    }
    codeService.getEpCodeData("Document Type").then(function (res) {
        $scope.fileTypeOption = [];
        if (res.result != 'success') {
            toaster.pop('error', '', res.msg);
            return;
        }
        $scope.fileTypeOption = res.records;
    });
    $scope.submitted = false;
    $scope.showalert = false;
    $scope.uploadFileToUrl = function (file, tripid, fileType, uploadUrl) {
        var fd = new FormData();
        fd.append('file', file);
        fd.append('tripid', tripid);
        fd.append('filetype', fileType);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).success(function () {
            $modalInstance.close(true);
        }).error(function () {
        });
    }
    $scope.uploadFile = function (file, tripid, fileType) {
        var uploadUrl = "uploaddocument";
        $scope.uploadFileToUrl(file, tripid, fileType, uploadUrl);
    };
    $scope.ok = function (isValid) {
        if (!isValid) {
            $scope.submitted = true;
            return;
        } else {

            var fi = document.getElementById('fileinput').files[0];
            if (fi.name.split('.').pop().toLowerCase() == 'bat' || fi.name.split('.').pop().toLowerCase() == 'exe') {
                $scope.showalert = true;
                $scope.errormsg = "can not upload exe or bat files。";
                return;
            }
            if (fi && fi.size > 10 * 1024 * 1024) {
                $scope.showalert = true;
                $scope.errormsg = "Upload failed, Maximum allowed size is 10MB";
                return;
            }

            $scope.showalert = false;
            $scope.uploadFile(fi, tripid, $scope.d.fileType);

        }
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);