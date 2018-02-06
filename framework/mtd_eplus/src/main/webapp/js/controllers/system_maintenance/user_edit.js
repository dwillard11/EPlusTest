'use strict';

app.controller('usereditctrl', ['$scope', '$http', 'toaster', '$stateParams', '$state', '$window',
    function ($scope, $http, toaster, $stateParams, $state, $window) {
    if (!$stateParams.status) {
        $stateParams.status = $window.sessionStorage["editUserStatus"];
        $stateParams.euId = $window.sessionStorage["editUserId"];
    }
    var id = $stateParams.euId || "";
    $window.sessionStorage["editUserStatus"] = $stateParams.status;
    $window.sessionStorage["editUserId"] = id;

    var tag = $stateParams.status;
    if ("from_edit" == tag) {
        $scope.pagestatus = "Edit";
        $scope.isReadonly = true;
    } else {
        $scope.pagestatus = "Add";
        $scope.isReadonly = false;
    }

    $scope.getUser = function (id) {
        $scope.isLoading = true;
        $scope.rowCollectionPage = [];
        $scope.accountCollectionPage = [];
        $http({
            method: 'GET',
            url: "maintenance/userManager/retrieveUserByID.do",
            params: {
                "id": id
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.uid = response.data.records.uid;
                $scope.company = response.data.records.company;
                $scope.customer = response.data.records.customer;
                $scope.defaultcurrency = response.data.records.defaultCurrency;
                $scope.status = response.data.records.status;
                $scope.rowRoles = response.data.roles_table;
                $.each( response.data.departments, function(i,n){
                    $.each( $scope.departmentOptions, function(j,o){
                        if(o.id == n.id)
                        {
                            o.selected = true;
                        }
                    });
                });
                $scope.isLoading = false;
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
                url: "maintenance/userManager/getEpCodeListByType.do",
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
                        $scope.currencyOption.splice(0,0,{name: "", id: ""});
                    } else if (type == "Status") {
                        $scope.statusOption = response.data.records;
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
    }
    $scope.getDepType = function () {
        $scope.departmentOptions = [];
        $http({
            method: 'GET',
            url: "maintenance/departmentManager/retrieveDepartments.do",
            params: {}
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.departmentOptions = response.data.records;
                $scope.selectedDepartmentOption = response.data.records.length > 0 ? response.data.records[0].id : "";
                $scope.getUser(id);
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    };

    $scope.getDepType();
    $scope.getEpCodeData("Currency");
    $scope.getEpCodeData("Status");


    $scope.checkAllRoles = false;
    $scope.selectAllRoles = function () {
        angular.forEach($scope.displayedCollection,
            function (item, key) {
                $scope.displayedCollection[key].selected = $scope.checkAllRoles;
            });
    };
    $scope.checkAllDepartments = false;
    $scope.selectAllDepartment = function () {
        angular.forEach($scope.displayedCollection2,
            function (item, key) {
                $scope.displayedCollection2[key].selected = $scope.checkAllDepartments;
            });
    };


    $scope.submitted = false;
    $scope.submit = function (isValid) {
        var roleItems = "";
        var departmentItems = "";
        if (!isValid) {
            $scope.submitted = true;
        } else {
            angular.forEach(
                $scope.displayedCollection,
                function (item, key) {
                    if ($scope.displayedCollection[key].selected) {
                        roleItems += item.id + ",";
                    }
                });

            angular.forEach(
                $scope.displayedCollection2,
                function (item, key) {
                    if ($scope.displayedCollection2[key].selected) {
                        departmentItems += item.id + ",";
                    }
                });

            if (roleItems.length == 0) {
                toaster.pop('error', '', 'Please select at least one role!');
                return;
            }

            if (departmentItems.length == 0) {
                toaster.pop('error', '', 'Please select at least one division!');
                return;
            }

            if (null != id && "" != id && undefined != id) {
                $http({
                    method: 'GET',
                    url: "maintenance/userManager/updateUser.do",
                    params: {
                        "id": id,
                        "uid": $scope.uid || "",
                        "company": $scope.company || "",
                        "customer": $scope.customer || "",
                        "department": $scope.selectedDepartmentOption || "",
                        "defaultcurrency": $scope.defaultcurrency || "",
                        "status": $scope.status || "",
                        "departmentItems": departmentItems,
                        "roleItems": roleItems
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Save successfully");
                        $state.go("app.system_maintenance.user_list");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });

            } else {
                $http({
                    method: 'GET',
                    url: "maintenance/userManager/addUser.do",
                    params: {
                        "id": id,
                        "uid": $scope.uid || "",
                        "department": $scope.selectedDepartmentOption || "",
                        "defaultcurrency": $scope.defaultcurrency || "",
                        "status": $scope.status || "",
                        "departmentItems": departmentItems,
                        "roleItems": roleItems
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Save successfully");
                        $state.go("app.system_maintenance.user_list");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });

            }
        }
    }

}]);
