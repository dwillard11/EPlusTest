app.controller('departmentlistctrl', ['$scope', '$timeout', '$http', '$state', 'valutils', 'toaster',
    function ($scope, $timeout, $http, $state, valutils, toaster) {
        $scope.isLoading = true;
        $scope.advsearchfilter = null;
        $scope.itemsByPage = 10;
        $scope.advsearch = function () {
            $scope.getData($scope.advsearchfilter);
        }

        $scope.getData = function (searchdata) {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "maintenance/departmentManager/retrieveDepartments.do",
                params: {
                    "advanceSearch": searchdata
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
        var searchdata = $scope.advsearchfilter || null;
        $scope.getData(searchdata);

        $scope.item_ids = [];
        $scope.check = false;
        $scope.selectAllItems = function () {
            angular.forEach($scope.displayedCollection,
                function (item, key) {
                    $scope.displayedCollection[key].selected = $scope.check;
                });

        };

        $scope.removeDepartments = function (id) {
            $scope.isLoading = true;
            var removeItems = "";
            if(id){
                removeItems = id;
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
                }
            }
            $http({
                method: 'GET',
                url: "maintenance/departmentManager/removeDepartments.do",
                params: {
                    "departmentIds": removeItems
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.getData($scope.advsearchfilter);
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                    $scope.isLoading = false;
                });
        };
        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = [
                "Name", "Address",
                "City", "Country", "Office Phone",
                "Status", "Invoice Sequence"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].name) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].address1) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].city) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].country) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].phoneOffice) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].status) + "");
                        valuearray.push(dataCollection[key].invoiceOfficeSeq + "");
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
            $scope.csvfilename = 'division_list.csv';
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

            pdfMake.createPdf(docDefinition1).download('division_list.pdf');
        }
        $scope.editDepartments = function (departmentId) {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                departmentId: departmentId
            };
            $state.go('app.system_maintenance.department_edit', param);

        };
        $scope.addDepartment = function () {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                departmentId: ""
            };
            $state.go('app.system_maintenance.department_edit', param);

        };

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