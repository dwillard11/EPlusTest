'use strict';

/* Controllers */

// Form controller
app.controller('departmenteditctrl', ['$scope', '$http', 'toaster', '$stateParams', '$state', '$window',
    function ($scope, $http, toaster, $stateParams, $state, $window) {
    if (!$stateParams.departmentId) {
        $stateParams.departmentId = $window.sessionStorage["editDepartmentId"];
    }
    var departmentId = $stateParams.departmentId;
    $window.sessionStorage["editDepartmentId"] = departmentId||"";
    if (departmentId!=null && departmentId > 0) {
        $scope.pagestatus = "Edit";
    } else {
        $scope.pagestatus = "Add";
    }
    $scope.$watch('country', function () {
        $scope.getProvinceByCountry($scope.country);
    });
    $scope.getProvinceByCountry = function (country) {
        $http({
            method: 'GET',
            url: "maintenance/departmentManager/getProvinceByCountry.do",
            params: {
                "country": country
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.provinceOption = response.data.records;
        }, function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        })
    }
    $scope.getData = function (departmentId) {
        $http({
            method: 'GET',
            url: "maintenance/departmentManager/retrieveDepartment.do",
            params: {
                "id": departmentId
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.departmentId = departmentId;
                $scope.name = response.data.records.name;
                $scope.shortName = response.data.records.shortName;
                $scope.status = response.data.records.status;
                $scope.city = response.data.records.city;
                $scope.address1 = response.data.records.address1;
                $scope.address2 = response.data.records.address2;
                $scope.postalCode = response.data.records.postalCode;
                $scope.province = response.data.records.province;
                $scope.country = response.data.records.country;
                $scope.phoneMobile = response.data.records.phoneMobile;
                $scope.phoneOffice = response.data.records.phoneOffice;
                $scope.phoneFax = response.data.records.phoneFax;
                $scope.phone800 = response.data.records.phone800;
                $scope.notes = response.data.records.notes;
                $scope.invoiceOfficeSeq = response.data.records.invoiceOfficeSeq;
                $scope.invoiceOfficeName = response.data.records.invoiceOfficeName;
                $scope.invoiceOfficeLine1 = response.data.records.invoiceOfficeLine1;
                $scope.invoiceOfficeLine2 = response.data.records.invoiceOfficeLine2;
                $scope.invoiceOfficeLine3 = response.data.records.invoiceOfficeLine3;
                $scope.defaultCurrency = response.data.records.defaultCurrency;
                $scope.defaultTimezone = response.data.records.defaultTimezone;
                $scope.defaultEmail = response.data.records.defaultEmail;
                $scope.mailHost = response.data.records.mailHost;
                $scope.mailUserName = response.data.records.mailUserName;
                $scope.mailPassword = response.data.records.mailPassword;
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }
    $scope.getData(departmentId);
    if (null != departmentId && "" != departmentId && undefined != departmentId) {
       // $scope.getData(departmentId);
    }else{
        $scope.country = "";
        $scope.province = "";
        $scope.defaultCurrency ="";
        $scope.status = "";
    }
    $scope.getCountry = function () {
        $http({
            method: 'GET',
            url: "maintenance/departmentManager/getCountry.do",
            params: {

            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.countryOption = response.data.records;

            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }

    $scope.getEpCodeData = function (type) {
        if (type != "" && type != null) {
            $http({
                method: 'GET',
                url: "maintenance/departmentManager/getEpCodeListByType.do",
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
                    } else if (type == "Timezone") {
                        $scope.timezoneOption = response.data.records;
                    }

                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
    }

    $scope.getEpCodeData("Status");
    $scope.getEpCodeData("Currency");
    $scope.getEpCodeData("Timezone");
    $scope.getCountry();

    $scope.submitted = false;
    $scope.submit = function (isValid) {
        var method = "";
        if (!isValid){
            $scope.submitted = true;
        }else {
            if (null != departmentId && "" != departmentId && undefined != departmentId) {
                method = "maintenance/departmentManager/updateDepartment.do";
            } else {
                method = "maintenance/departmentManager/addDepartment.do";
            }
            $http({
                method: 'GET',
                url: method,
                params: {
                    "id": departmentId,
                    "name": $scope.name || "",
                    "shortName": $scope.shortName || "",
                    "status": $scope.status || "",
                    "city": $scope.city || "",
                    "address1": $scope.address1 || "",
                    "address2": $scope.address2 || "",
                    "postalCode": $scope.postalCode || "",
                    "province": $scope.province || "",
                    "country": $scope.country || "",
                    "phoneMobile": $scope.phoneMobile || "",
                    "phoneOffice": $scope.phoneOffice || "",
                    "phoneFax": $scope.phoneFax || "",
                    "phone800": $scope.phone800 || "",
                    "notes": $scope.notes || "",
                    "invoiceOfficeSeq": $scope.invoiceOfficeSeq,
                    "invoiceOfficeName": $scope.invoiceOfficeName || "",
                    "invoiceOfficeLine1": $scope.invoiceOfficeLine1 || "",
                    "invoiceOfficeLine2": $scope.invoiceOfficeLine2 || "",
                    "invoiceOfficeLine3": $scope.invoiceOfficeLine3 || "",
                    "defaultCurrency": $scope.defaultCurrency || "",
                    "defaultTimezone": $scope.defaultTimezone || "",
                    "defaultEmail": $scope.defaultEmail || "",
                    "mailHost": $scope.mailHost || "",
                    "mailUserName": $scope.mailUserName || "",
                    "mailPassword": $scope.mailPassword || ""
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully!");
                    $state.go("app.system_maintenance.department_list");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
    }
}]);
