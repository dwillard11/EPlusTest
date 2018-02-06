app.controller('rolelistctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage',
    function ($scope, $timeout, $http, $state, toaster, $localStorage) {
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
                url: "maintenance/roleManager/retrieveRoles.do",
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

        $scope.removeRole = function (id) {
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
            var jsonData = angular.toJson(removeItems);
            var objectToSerialize = {
                'object': jsonData
            };
            $http({
                method: 'GET',
                url: "maintenance/roleManager/removeRole.do",
                params: {
                    "roleIds": removeItems
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
            var keyarray = ["Name", "Description", "Status"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].name) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].description) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].status) + "");
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
            $scope.csvfilename = 'Role_Management.csv';
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

            pdfMake.createPdf(docDefinition1).download('Role_Management.pdf');
        }
        $scope.editRole = function (roleId) {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                roleId: roleId,
                status: "from_edit"
            };
            $state.go('app.system_maintenance.role_edit', param);

        };
        $scope.addRole = function () {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                roleId: "",
                status: "from_add"
            };
            $state.go('app.system_maintenance.role_edit', param);

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