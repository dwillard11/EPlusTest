'use strict';
app.controller('roleeditctrl', ['$scope', '$http', 'toaster', '$stateParams', '$state', '$window',
    function ($scope, $http, toaster, $stateParams, $state, $window) {
    if (!$stateParams.status) {
        $stateParams.status = $window.sessionStorage["editRoleStatus"];
        $stateParams.roleId = $window.sessionStorage["editRoleId"];
    }
    var id = $stateParams.roleId || "";
    $window.sessionStorage["editRoleStatus"] = $stateParams.status;
    $window.sessionStorage["editRoleId"] = id;

    var tag = $stateParams.status;
    if ("from_edit" == tag) {
        $scope.pagestatus = "Edit";
    } else {
        $scope.pagestatus = "Add";
    }


    $scope.getRole = function (id) {
        $scope.isLoading = true;
        $scope.rowCollectionPage = [];
        $http({
            method: 'GET',
            url: "maintenance/roleManager/retrieveRole.do",
            params: {
                "id": id
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }

                $scope.id = response.data.records.id;
                $scope.name = response.data.records.name;
                $scope.description = response.data.records.description;
                $scope.systemDefault = response.data.records.systemDefault;
                $scope.status = response.data.records.status;
                $scope.rowCollectionPage = response.data.records.roleAccessRights;
                $scope.isLoading = false;
                $scope.conllectionLength = $scope.rowCollectionPage.length;
            },
            function errorCallback() {
                $scope.isLoading = false;
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    };
    $scope.getEpCodeData = function (type) {
        if (type != "" && type != null) {
            $http({
                method: 'GET',
                url: "maintenance/roleManager/getEpCodeListByType.do",
                params: {
                    "type": type
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    if (type == "Currency") {
                        $scope.currencyOption = response.data.records;
                    } else if (type == "Status") {
                        $scope.statusOption = response.data.records;
                    }

                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
    }



    $scope.getRole(id);
    $scope.getEpCodeData("Status");


    $scope.checkAllRoleAccessRights = false;
    $scope.selectAllRoleAccessRights = function () {
        angular.forEach($scope.displayedCollection,
            function (item, key) {
                $scope.displayedCollection[key].hasChecked = $scope.checkAllRoleAccessRights;
            });
    };



    $scope.submitted = false;
    $scope.submit = function (isValid) {
        var accessRightItems = "";
        if (!isValid){
            $scope.submitted = true;
        }else{
        angular.forEach(
            $scope.displayedCollection,
            function (item, key) {
                if ($scope.displayedCollection[key].hasChecked) {
                    accessRightItems += item.accessRightId + ",";
                }
            });

        if (null != id && "" != id && undefined != id) {
            $http({
                method: 'GET',
                url: "maintenance/roleManager/updateRole.do",
                params: {
                    "id": id,
                    "name": $scope.name || "",
                    "description": $scope.description || "",
                    "status": $scope.status || "",
                    "systemDefault": $scope.systemDefault,
                    "accessRightIds":accessRightItems
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    $state.go("app.system_maintenance.role_list");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });

        } else {
            $http({
                method: 'GET',
                url: "maintenance/roleManager/addRole.do",
                params: {
                    "name": $scope.name || "",
                    "description": $scope.description || "",
                    "status": $scope.status || "",
                    "systemDefault": false,
                    "accessRightIds":accessRightItems
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    $state.go("app.system_maintenance.role_list");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });

        }
}
    }

}]);
