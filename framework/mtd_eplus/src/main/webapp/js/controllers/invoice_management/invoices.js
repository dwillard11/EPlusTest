app.controller('invoicectrl', ['$scope','deptService', '$timeout', '$http', '$state', 'toaster', '$localStorage', '$filter','tripService','valutils',
    function ($scope,deptService, $timeout, $http, $state, toaster, $localStorage, $filter,tripService,valutils) {
        //$scope.from = $filter('date')(new Date(), 'yyyy-MM-dd');
        //$scope.to = $filter('date')(new Date(), 'yyyy-MM-dd');
        $scope.getDepartmentDropDownList = function () {
            $scope.selectedDeptIds = [];
            $scope.selectedDeptName = [];
            deptService.getCurrentUserDepts().then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.departmentOptions = res.records;
                $scope.selectedDepartmentOption = res.records[0];
                angular.forEach($scope.departmentOptions, function (data) {
                    $scope.selectedDeptIds.push(data.id);
                    $scope.selectedDeptName.push(data.name);
                });
                $scope.getInvoices($scope.selectedDeptIds.toString(), $scope.from, $scope.to, '');
            })
        };

        $scope.getDepartmentDropDownList();
        $scope.getInvoices = function (departmentIds, from, to, keyword) {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "invoiceManagement/invoices/retrieveInvoices.do",
                params: {
                    departmentIds: departmentIds,
                    from: from,
                    to: to,
                    keyword: keyword,
                    foobar: new Date().getTime()
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

        $scope.removeInvoice = function (id) {
            if (window.confirm("Are you sure you want to delete this record?")) {
                tripService.removeInvoice(id).then(function (res) {
                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        $scope.getInvoices($scope.selectedDeptIds.toString(), $scope.from, $scope.to, '');
                    }
                })
            }
        };
        var updateSelectDept = function (action, id, name) {
            if (action == 'add' && $scope.selectedDeptIds.indexOf(id) == -1) {
                $scope.selectedDeptIds.push(id);
                $scope.selectedDeptName.push(name);
            }
            if (action == 'remove' && $scope.selectedDeptIds.indexOf(id) != -1) {
                var idx = $scope.selectedDeptIds.indexOf(id);
                $scope.selectedDeptIds.splice(idx, 1);
                $scope.selectedDeptName.splice(idx, 1);
            }
            $scope.getInvoices($scope.selectedDeptIds.toString(), $scope.from, $scope.to, '');
        };
        $scope.updateSelectDept = function ($event, id) {
            var checkbox = $event.target;
            var action = (checkbox.checked ? 'add' : 'remove');
            updateSelectDept(action, id, checkbox.name);
        };
        $scope.isSelectedDept = function (id) {
            return $scope.selectedDeptIds.indexOf(id) >= 0;
        };
        $scope.$watch('from', function (newValue, oldValue) {
            $scope.getInvoices($scope.selectedDeptIds.toString(), $scope.from, $scope.to, '');
        });
        $scope.$watch('to', function (newValue, oldValue) {
            $scope.getInvoices($scope.selectedDeptIds.toString(), $scope.from, $scope.to, '');
        });
        $scope.showInvoice = function (tripid, invoiceid, mode, type) {
            var param = {
                tripid: tripid,
                invoiceid: invoiceid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.invoice_details", param);
        };
        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];

            if (type == 'invoice') {
                var keyarray = ["Invoice #", "Service Type", "Invoice To", "Shipper", "Currency", "Total", "Date"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].refNum) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].serviceType) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].billedClient) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].shipper) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].billingCurrency) + "");
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalAmount || 0), 2) + "");
                            valuearray.push($filter('date')(dataCollection[key].invoiceDate, "yyyy-MM-dd") + "");
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }

        }

        $scope.exportcsv = function (collection, type) {
            if (type == "invoice") {
                $scope.csvfilename = 'Invoice_List.csv';
            }
            return coverttoexportdataformat(collection, type);

        }
        $scope.exportpdf = function (collection, type) {
            if (type == "invoice") {
                $scope.csvfilename = 'Invoice_List.pdf';
            }
            var docDefinition1 = {
                pageOrientation: 'landscape',
                content: [{
                    table: {
                        headerRows: 1,
                        body: coverttoexportdataformat(collection, type)
                    }
                }]
            };

            pdfMake.createPdf(docDefinition1).download($scope.csvfilename);
        }

        // download file
        $scope.downloadFile = function (id) {
            window.location.href = "downloaddocument?id=" + id;
        };
    }
]);

app.directive('searchInvoiceModel', function () {
    return {
        require: '^stTable',
        scope: {
            searchInvoiceModel: '='
        },
        link: function (scope, ele, attr, ctrl) {
            var table = ctrl;
            scope.$parent.advsearch = function () {
                scope.$watch('searchInvoiceModel', function (val) {
                    table.search(val);
                });
            }
        }
    };
});