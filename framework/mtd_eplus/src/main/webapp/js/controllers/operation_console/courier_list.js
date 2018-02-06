/**
 * Created by admin on 4/15/2017.
 */
app.controller('courierlistctrl', ['$scope', '$window', '$timeout', '$http', '$state', 'toaster', '$stateParams', 'valutils', '$filter',
    function ($scope, $window, $timeout, $http, $state, toaster, $stateParams, valutils, $filter) {
        $scope.isLoading = true;
        $scope.advsearchfilter = $stateParams ? $stateParams : {companyId: "",locationId: "",status: "", country: "", airport: "", city: ""};
        $scope.companyOption = [];
        $scope.locationOption = [];
        $scope.onlineStatusList = [];
        $scope.countryOption = [];
        $scope.itemsByPage = 10;

        if ($window.sessionStorage["courierListStatus"]) {
            $scope.advsearchfilter.status = $window.sessionStorage["courierListStatus"];
        }
        if ($window.sessionStorage["courierListAirport"]) {
            $scope.advsearchfilter.airport = $window.sessionStorage["courierListAirport"];
        }
        if ($window.sessionStorage["courierListCity"]) {
            $scope.advsearchfilter.city = $window.sessionStorage["courierListCity"];
        }
        if ($window.sessionStorage["courierListCountry"]) {
            $scope.advsearchfilter.country = $window.sessionStorage["courierListCountry"];
        }

        $scope.getCompany = function () {
            $http({
                method: 'GET',
                url: "customer/customerManager/retrieveCustomerByType.do",
                params: {
                    advanceSearch: 'COURIER'
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.companyOption = response.data.records;
                    $scope.companyOption.splice(0, 0, {id:0, name:"All"});

                    if ($window.sessionStorage["courierListCompanyId"]) {
                        $scope.advsearchfilter.companyId = parseInt($window.sessionStorage["courierListCompanyId"]);
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        $scope.$watch('advsearchfilter.companyId', function () {
            $scope.getLocationByCompany($scope.advsearchfilter.companyId);
        });
        $scope.getLocationByCompany = function (companyId) {
            $http({
                method: 'GET',
                url: "customer/customerManager/retrieveCustomerLocationByEntityId.do",
                params: {
                    "entityId": companyId
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.locationOption = response.data.records;
                $scope.locationOption.splice(0, 0, {id:0, code:"All"});
                if ($window.sessionStorage["courierListLocationId"]) {
                    var value = parseInt($window.sessionStorage["courierListLocationId"]);
                    if(value >0 )
                    {
                        $.each($scope.locationOption,function (i,item) {
                            if(item.id ==  value)
                            {
                                $scope.advsearchfilter.locationId = value;
                            }
                        });
                    }
                }
            }, function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
        }

        var getEpCodeData = function(type) {
            if (type != "" && type != null) {
                $http({
                    method: 'GET',
                    url: "maintenance/userManager/getEpCodeListByType.do",
                    params: {
                        "type": type
                    }
                }).then(function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if(type == "Online Status"){
                            $scope.onlineStatusList = response.data.records;
                            $scope.onlineStatusList.splice(0, 0, {id:"", name:"All"});
                            $scope.advsearchfilter.status = "";
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        };

        $scope.getCountry = function () {
            $http({
                method: 'GET',
                url: "maintenance/departmentManager/getEntityContactCountry.do",
                params: {

                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.countryOption = response.data.records;
                    if($scope.advsearchfilter.country && $scope.advsearchfilter.country.length > 0)
                    {
                        $.each($scope.countryOption,function (i,item) {
                            item.isChecked = ($scope.advsearchfilter.country.indexOf(item.id) != -1);
                        });
                        $("#chkAll").prop("checked",($scope.advsearchfilter.country.indexOf(",0,") != -1));
                    };
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        getEpCodeData("Online Status");
        $scope.getCountry();
        $scope.getCompany();

        $scope.selectAllCountry = function ()
        {
            if($("#chkAll").prop("checked")){
                $("#CountryList input").prop("checked",true);
            }else{
                $("#CountryList input").prop("checked",false);
            }
            $scope.advsearch();
        }

        $scope.advsearch = function () {
            var chkCountrys =",";
           $("#CountryList input:checked").each(function(i){
                chkCountrys += $(this).val()+",";
            });
            $scope.advsearchfilter.country = chkCountrys;
            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            var param =  $scope.advsearchfilter;
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/retrieveCourierList.do",
                params: param
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage = response.data.records;
                    $scope.isLoading = false;
                    $scope.conllectionLength = $scope.rowCollectionPage.length;

                    $window.sessionStorage["courierListCompanyId"] = $scope.advsearchfilter.companyId||"";
                    if($scope.advsearchfilter.locationId.toString().length > 0)
                        $window.sessionStorage["courierListLocationId"] = $scope.advsearchfilter.locationId||"";
                    $window.sessionStorage["courierListStatus"] = $scope.advsearchfilter.status||"";
                    $window.sessionStorage["courierListAirport"] = $scope.advsearchfilter.airport||"";
                    $window.sessionStorage["courierListCity"] = $scope.advsearchfilter.city||"";
                    $window.sessionStorage["courierListCountry"] = $scope.advsearchfilter.country||"";
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        $scope.getData();

        $scope.editEntity = function (item) {
            var param = {
                entityId: item.entityId,
                selectId: item.id,
                status: "from_edit_courier"
            };
            $state.go('app.system_maintenance.entity_edit', param);
        }

        $scope.removeEntity = function (id) {
            $scope.isLoading = true;
            var removeItems = id;
            return;
            $http({
                method: 'GET',
                url: "customer/customerManager/deleteCustomerContact.do",
                params: {
                    "ids": removeItems
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.getData();
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                    $scope.isLoading = false;
                });
        };

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["Company", "Online Status", "Courier Country", "Courier City", "Courier Air Port", "Airline Info", "Contact Name", "Visa"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].company));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].onlineStatusDesc));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].onlineCountry));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].courierCity));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].courierAirport));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].airLineInfo));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].contactName));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].visaCountry));
                        dataarray.push(valuearray);
                    });
            return dataarray;

        }

        $scope.exportcsv = function (exportOption) {
            var datasource = [];
            if (exportOption == 'currentPage') {
                datasource = $scope.displayedCollection;
            } else {
                datasource = $scope.rowCollectionPage;
            }
            $scope.csvfilename = 'courier_list.csv';
            return coverttoexportdataformat(datasource);

        }

        $scope.exportpdf = function (exportOption) {
            var datasource = [];
            if (exportOption == 'currentPage') {
                datasource = $scope.displayedCollection;
            } else {
                datasource = $scope.rowCollectionPage;
            }
            var docDefinition1 = {
                pageOrientation: 'landscape',
                content: [{
                    table: {
                        headerRows: 1,
                        body: coverttoexportdataformat(datasource)
                    }
                }]
            };

            pdfMake.createPdf(docDefinition1).download('courier_list.pdf');
        }

    }]);
app.directive('ngConfirmClick', [
    function () {
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click', function () {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }])
