app.controller('entitylistctrl', ['$scope', '$timeout', '$http', '$state', 'valutils', 'toaster', '$localStorage',
    function ($scope, $timeout, $http, $state, valutils, toaster, $localStorage) {
        $scope.isLoading = true;
        $scope.advsearchfilter = null;
        $scope.availableOptions = [];
        $scope.selectedOption = null;
        $scope.itemsByPage = 10;

        $scope.getEpCodeType = function () {
            $http({
                method: 'GET',
                url: "maintenance/userManager/getEpCodeListByType.do",
                params: {
                    "type": "Entity Type"
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.availableOptions = response.data.records;
                    $scope.availableOptions.splice(0,0,{id:"",name:"ALL"});
                    $scope.selectedOption = response.data.records[0].id;
                    $scope.getData();
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        $scope.getEpCodeType();

        $scope.advsearch = function () {
            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "customer/customerManager/retrieveCustomerByType.do",
                params: {
                    "advanceSearch": ($scope.selectedOption) ? $scope.selectedOption  : "" ,
                    "keyword": $scope.advsearchfilter || ""
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage = response.data.records;
                    $scope.isLoading = false;
                    $scope.conllectionLength = $scope.rowCollectionPage.length;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };


        $scope.item_ids = [];
        $scope.check = false;
        $scope.selectAllItems = function () {
            angular.forEach($scope.displayedCollection,
                function (item, key) {
                    $scope.displayedCollection[key].selected = $scope.check;
                });

        };

        $scope.removeEntity = function (id) {
            $scope.isLoading = true;
            var removeItems = "";
            if(id){
                removeItems= id;
            }else{
            angular.forEach(
                $scope.displayedCollection,
                function (item, key) {
                    if ($scope.displayedCollection[key].selected) {
                        removeItems += item.id + ",";
                    }
                });
            if (removeItems === "" || removeItems === null || typeof removeItems === "undefined") {
                toaster.pop('warning', '', "Please select at least one item!");
                $scope.isLoading = false;
                return;
            }}
            var jsonData = angular.toJson(removeItems);
            var objectToSerialize = {
                'object': jsonData
            };
            $http({
                method: 'GET',
                url: "customer/customerManager/deleteCustomer.do",
                params: {
                    "customerIds": removeItems
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
            var keyarray = ["Type", "Name", "Quick Name"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].typeDesc) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].name) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].quickName) + "");
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
            $scope.csvfilename = 'Entity_Management.csv';
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

            pdfMake.createPdf(docDefinition1).download('Entity_Management.pdf');
        }

        $scope.editEntity = function (eId) {
            var param = {
                entityId: eId,
                status: "from_edit"
            };
            $state.go('app.system_maintenance.entity_edit', param);

        }

        $scope.addEntity = function () {
            var param = {
                euId: null,
                status: "from_add"
            };
            $state.go('app.system_maintenance.entity_edit', param);
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