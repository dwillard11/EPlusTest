app.controller('lockedtripsctrl', ['$scope','$localStorage', 'deptService', '$timeout', '$http', '$state', 'toaster', 'valutils', '$filter',
    function ($scope, $localStorage,deptService, $timeout, $http, $state, toaster, valutils, $filter) {
        $scope.departmentOptions =[];
        $scope.rowCollectionPage = [];
        (function() {
            deptService.getCurrentUserDepts().then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                angular.forEach(res.records, function (data)
                {
                    $scope.departmentOptions.push({
                        id: data.id, name:data.name, isCheck: true
                    });
                });
                $scope.getList();
            })
        })();

        $scope.getList = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            var departmentIDs = $.map($scope.departmentOptions,
            function(item){
                if(item.isCheck)
                    return item.id;
                return null;
             }).toString();
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/selectLockedTrips.do",
                params: {
                    "departmentIds": departmentIDs
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

        $scope.updateTrips = function (id) {
            if (confirm('Are you sure you want to unlock this record?')) {
                $http({
                    method: 'GET',
                    url: 'operationconsole/operationconsole/releaseTripLockById.do',
                    params: {
                        "tripid": id
                    }
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Unlock successfully");
                     $scope.getList();
                },
                function errorCallback(response) {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            }
        };

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = [
                "Trip #", "Client", "Shipper", "Consignee", "Orig", "Dest", "Locked By", "Lock Time"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].refId));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].clientName));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].shipperName));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].consigneeName));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].orig));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].dest));
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].lockedBy));
                        valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].locktime), "yyyy-MM-dd HH:mm"));
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
            $scope.csvfilename = 'locked_trip_list.csv';
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

            pdfMake.createPdf(docDefinition1).download('locked_trip_list.pdf');
        }
    }
]);
