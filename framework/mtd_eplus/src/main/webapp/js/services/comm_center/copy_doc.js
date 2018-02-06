app.controller('copyDocCtrl', ['$scope', 'valutils', 'commService', 'fileService', 'toaster', '$modalInstance', '$modal', '$log', '$http', 'codeService', 'doc',
    function ($scope, valutils, commService, fileService, toaster, $modalInstance, $modal, $log, $http, codeService, doc) {
        $scope.f = {
            newDocName:doc.originalFileName,
            newDocType:doc.type
        }
        $scope.submitted = false;
        $scope.doc = doc;
        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                $scope.copyAttach($scope.doc.id, $scope.f.newDocName, $scope.f.newDocType);
            }
        };
        $scope.copyAttach = function (docId, newName, newType) {
            commService.copyAttach(docId, newName, newType).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                toaster.pop('success', '', 'Copy successfully');
                $modalInstance.close($scope.doc);
            });
        }


        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        codeService.getEpCodeData("Document Type").then(function (res) {
            $scope.fileTypeOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.fileTypeOption = res.records;
        });


    }]);
