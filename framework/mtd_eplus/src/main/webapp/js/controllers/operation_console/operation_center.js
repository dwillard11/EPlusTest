app.controller('operationcenterctrl', ['$scope', '$window','$localStorage', 'deptService', 'tripService', 'valutils', '$timeout', '$http', '$state', 'toaster', '$filter', '$modal', '$location',
    function ($scope, $window, $localStorage, deptService, tripService, valutils, $timeout, $http, $state, toaster, $filter, $modal, $location) {

        $scope.getDepartmentDropDownList = function () {
            $scope.itemsByPage = 20;
            $scope.selectedDeptIds = [];
            $scope.selectedDeptName = [];
            deptService.getCurrentUserDepts().then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.departmentOptions = res.records;
                $scope.selectedDepartmentOption = res.records[0];

                if ($window.sessionStorage["selectedDeptIds"]) {
                    $scope.selectedDeptIds = $window.sessionStorage["selectedDeptIds"].split(",");
                }
                else {
                    angular.forEach($scope.departmentOptions, function (data) {
                        $scope.selectedDeptIds.push(data.id);
                        $scope.selectedDeptName.push(data.name);
                    });
                }

                if ($window.sessionStorage["selectedCategory"]) {
                    $scope.selectedCategory = $window.sessionStorage["selectedCategory"];
                }
                else {
                    $scope.selectedCategory = "OPEN";
                }

                $scope.getQuotes($scope.selectedDeptIds.toString(), null);
                $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
            })
        };

        $scope.loadList  = function () {
            $scope.getDepartmentDropDownList();
        };

        $scope.categories = [
            {id:'ALL',name:'ALL'},
            {id: 'OPEN', name: 'Open Shipments'},
            {id: 'DEL', name: 'Delivered Shipments'},
            {id: 'CLOSE', name: 'Close Shipments'}
        ];
        $scope.selectedCategory = "OPEN";

        // get quote list by department and tripStatus
        $scope.getQuotes = function (departmentIDs, tripStatus) {
            $scope.isLoading2 = true;
            $scope.rowCollectionPage2 = [];
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/retrieveQuoteListByDepartmentAndStatus.do",
                params: {
                    "departmentIds": departmentIDs,
                    "tripStatus": "OPEN"
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage2 = response.data.records;
                    $scope.isLoading2 = false;
                    $scope.conllectionLength2 = $scope.rowCollectionPage2.length;
                },
                function errorCallback() {
                    $scope.isLoading2 = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        $scope.getTrips = function (departmentIDs, tripCategory) {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/retrieveTrips.do",
                params: {
                    "departmentIds": departmentIDs,
                    "tripCategory": tripCategory
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
                    refreshData();
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        $scope.loadList();
        $scope.recordStatus = function (shipperCountry, consigneeCountry) {
            if (!(shipperCountry == "CA" || shipperCountry == "US") || !(consigneeCountry == "CA" || consigneeCountry == "US" )) {
                return "mark-Record";
            }
        }

        $scope.timeStatus = function (status, time, shipperCountry, consigneeCountry) {
            if (!time || status=='DEL' || status=='CLO' || status=='ARC') return $scope.recordStatus(shipperCountry, consigneeCountry);
            var timeStr = $filter('date')(time, 'yyyyMMddHHmmss');
            var now = new Date();
            var nowStr = $filter('date')(now, 'yyyyMMddHHmmss');
            var oneStr = $filter('date')(now.setHours(now.getHours() + 1), 'yyyyMMddHHmmss');
            if (timeStr < oneStr)
                return "time-Red";
            var twoStr = $filter('date')(now.setHours(now.getHours() + 2), 'yyyyMMddHHmmss');
            if (timeStr < twoStr)
                return "time-Orange";
            if (timeStr >= twoStr)
                return "time-Green";

            /* Revert UTC time
            var nowUTC = new Date(new Date().toUTCString().replace('GMT',''));
            var nowStr = $filter('date')(nowUTC, 'yyyyMMddHHmmss');
            if (timeStr < nowStr)
                return "time-Red";
            var twoStr = $filter('date')(nowUTC.setHours(nowUTC.getHours() + 2), 'yyyyMMddHHmmss');
            if (timeStr < twoStr)
                return "time-Orange";
            if (timeStr > twoStr)
                return "time-Green";
            */
            return $scope.recordStatus(shipperCountry, consigneeCountry);
        }

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
            $scope.getQuotes($scope.selectedDeptIds.toString(), null);
            $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
            $window.sessionStorage["selectedDeptIds"] = $scope.selectedDeptIds.toString();
            $window.sessionStorage["selectedCategory"] = $scope.selectedCategory;
        };
        $scope.updateSelectDept = function ($event, id) {
            var checkbox = $event.target;
            var action = (checkbox.checked ? 'add' : 'remove');
            updateSelectDept(action, id, checkbox.name);
        };

        $scope.isSelectedDept = function (id) {
            return $scope.selectedDeptIds.indexOf(id) >= 0;
        };
        $scope.$watch('selectedCategory', function () {
            if ($scope.selectedDepartmentOption != undefined && $scope.selectedDepartmentOption != null && $scope.selectedDepartmentOption != '') {
                $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
                $window.sessionStorage["selectedDeptIds"] = $scope.selectedDeptIds.toString();
                $window.sessionStorage["selectedCategory"] = $scope.selectedCategory;
            }
        });
        $scope.handleQuote = function (quoteid, mode, type, dept) {
            if (valutils.isEmptyOrUndefined($localStorage.user.defaultCurrency)
                && !valutils.isEmptyOrUndefined(dept)) {
                // fill dept defaultCurrency in user.defaultCurrency
                deptService.getDeptInfo(dept).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.selectedDept = res.records;
                    $localStorage.user.defaultCurrency = $scope.selectedDept.defaultCurrency;
                })
            }
            if (mode == "add") {
                tripService.loadTrip(quoteid, mode, type, dept).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    quoteid = res.records.id;

                    var param = {
                        tripid: quoteid,
                        tripmode: 'edit',
                        triptype: type,
                        dept: dept
                    };
                    $state.go("app.operation_console.quote_details", param);
                })
            } else {
                var param = {
                    tripid: quoteid,
                    tripmode: mode,
                    triptype: type,
                    dept: dept
                };
                $state.go("app.operation_console.quote_details", param);
            }
        };

        $scope.handleTrip = function (tripid, mode, type, dept) {
            if (valutils.isEmptyOrUndefined($localStorage.user.defaultCurrency)
                && !valutils.isEmptyOrUndefined(dept)) {
                // fill dept defaultCurrency in user.defaultCurrency
                deptService.getDeptInfo(dept).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.selectedDept = res.records;
                    $localStorage.user.defaultCurrency = $scope.selectedDept.defaultCurrency;
                })
            }


            if (mode == "add") {
                tripService.loadTrip(tripid, mode, type, dept).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    tripid = res.records.id;

                    var param = {
                        tripid: tripid,
                        tripmode: 'edit',
                        triptype: type,
                        dept: dept
                    };
                    $state.go("app.operation_console.trip_details", param);
                })
            } else {
                var param = {
                    tripid: tripid,
                    tripmode: mode,
                    triptype: type,
                    dept: dept
                };
                $state.go("app.operation_console.trip_details", param);
            }
        };
        $scope.showPickup = function (tripid, mode, type) {
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.pickup_details", param);
        };
        $scope.showBOL = function (tripid, mode, type) {
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.bol_details", param);
        };
        $scope.showInvoice = function (tripid, invoiceid, mode, type) {
            var param = {
                tripid: tripid,
                invoiceid: invoiceid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.invoice_details", param);
        };
        $scope.createPDF = function () {
            var url = "run?__report=mteplus_report.rptdesign&__format=pdf";
            window.location.href = url;
        };

        $scope.getRatesOAG = function () {
            window.open('http://cargoflights.oagcargo.com/');
        }
        $scope.getRatesEgencia = function () {
            window.open('https://www.egencia.com/pub/agent.dll?qscr=logi&&lang=en&lang=en');
        }
        $scope.btnQuotePDF = function (tripid) {
            var param = {
                quoteid: tripid
            };
            $state.go("app.operation_console.quote_builder", param);
        };

        $scope.printTripPDF = function (tripid) {
            toaster.pop('wait', '', 'start to print trip pdf');
            var protocol = $location.protocol();
            var host = $location.host();
            var port = $location.port();
            var url = protocol + "://" + host + ":" + port;
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/printTripPDF.do",
                params: {
                    pdfURL: url,
                    id: tripid
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "save succuessfully!");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];
            if (type == 'trip') {
                var keyarray = ["Trip #", "Shipper", "Consignee", "Status", "Orig", "Dest", "Critical Time", "Next Event", "Next Event Date", "Email"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].tripRefNo));
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].clientName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].shipperName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].consigneeName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].statusDesp));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].orig));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].dest));
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].criticalTime), "yyyy-MM-dd HH:mm"));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].nextEventName));
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].nextEventDate), "yyyy-MM-dd HH:mm"));
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].note));
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].numberOfUnreadEmail));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].numberOfUnreadEmail)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].numberOfUnreadEmail));
                            }
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'quote') {
                var keyarray = ["Quote #", "Client", "Shipper", "Consignee", "Version", "Expires", "Orig", "Dest", "Type", "Email"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].quoteRefNo));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].clientName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].shipperName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].consigneeName));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].version)) {
                                valuearray.push("");
                            } else {
                                valuearray.push("Version " + valutils.trimToEmpty(dataCollection[key].version));
                            }

                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].expireDate), "yyyy-MM-dd HH:mm"));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].orig));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].dest));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].typeDesp));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].numberOfUnreadEmail)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].numberOfUnreadEmail));
                            }
                            dataarray.push(valuearray);

                        });
                return dataarray;
            }

        }

        $scope.exportcsv = function (collection, type) {
            if (type == "trip") {
                $scope.csvfilename = 'trip_list.csv';
            }
            if (type == "quote") {
                $scope.csvfilename = 'quote_list.csv';
            }
            return coverttoexportdataformat(collection, type);

        }
        $scope.exportpdf = function (collection, type) {
            if (type == "trip") {
                $scope.csvfilename = 'trip_list.pdf';
            }
            if (type == "quote") {
                $scope.csvfilename = 'quote_list.pdf';
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

        $scope.addTripTemplate = function (id,type) {
            var modalInstance = $modal.open({
                templateUrl: 'addTripTemplate.html',
                controller: 'addTripTemplateCtrl',
                size: 'lg',
                resolve: {
                    tripid: function () {
                        return id;
                    },
                    triptype: function () {
                        return type;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
            }, function () {

            });
        }
        $scope.showTripTemplates = function (deptId) {
            var modalInstance = $modal.open({
                templateUrl: 'showTripTemplates.html',
                controller: 'showTripTemplatesCtrl',
                size: 'lg',
                resolve: {
                    deptId: function () {
                        return deptId;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                toaster.pop('success', '', 'Import successfully!');
                $scope.handleTrip(result, 'fromTripTemplate', 'trip', deptId)
                // $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
            }, function () {

            });
        }

        $scope.changeTripDivision = function (type, id, departmentId) {
            var modalInstance = $modal.open({
                templateUrl: 'changeTripDivision.html',
                controller: 'changeTripDivisionCtrl',
                size: 'lg',
                resolve: {
                    tripid: function () {
                        return id;
                    },
                    departmentId: function () {
                        return departmentId;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                if (type == "Quote")
                    $scope.getQuotes($scope.selectedDeptIds.toString(), null);
                else
                    $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);

            }, function () {

            });
        }


        var timeoutPromise;
        function refreshData() {
            if (timeoutPromise)
                $timeout.cancel(timeoutPromise);
            timeoutPromise = $timeout(function () {
                if ($scope.selectedDepartmentOption != undefined && $scope.selectedDepartmentOption != null && $scope.selectedDepartmentOption != '')
                {
                    $scope.getTrips($scope.selectedDeptIds.toString(), $scope.selectedCategory);
                    $window.sessionStorage["selectedDeptIds"] = $scope.selectedDeptIds.toString();
                    $window.sessionStorage["selectedCategory"] = $scope.selectedCategory;
                }
            }, 60 * 1000);
        }

        $scope.$on('$destroy', function () {
            if (timeoutPromise)
                $timeout.cancel(timeoutPromise);
        })
    }
]);
app.controller('showTripTemplatesCtrl', ['$scope', 'valutils', 'tripService', 'toaster', '$modalInstance', 'deptId',
    function ($scope, valutils, tripService, toaster, $modalInstance, deptId) {
        tripService.retrieveTripTemplates(deptId).then(function (res) {
            $scope.isLoading = true;
            if (res.result != 'success') {
                $scope.isLoading = false;
                toaster.pop('error', '', res.msg);
                return;
            }

            $scope.templatesList = res.records;
            $scope.collectionLength = $scope.templatesList.length || 0;
            angular.forEach(
                $scope.templatesList,
                function (item, key) {
                    $scope.templatesList[key].hasChecked = false;
                });
            $scope.isLoading = false;
        });

        $scope.newTripFromTemplates = function () {
            var checkedIds = [];
            angular.forEach(
                $scope.templatesList,
                function (item, key) {
                    if ($scope.templatesList[key].hasChecked) {
                        checkedIds.push(item.id);
                    }
                });
            if (checkedIds.length == 1) {
                $modalInstance.close(checkedIds[0]);
            } else {
                toaster.pop('error', '', 'please chose only one template!');
                return;
            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
app.controller('addTripTemplateCtrl', ['$scope', 'valutils', 'tripService', 'toaster', '$modalInstance', '$http', 'tripid','triptype',
    function ($scope, valutils, tripService, toaster, $modalInstance, $http, tripid, triptype) {
        $scope.template = {
            name: "",
            tripid: tripid,
            triptype: triptype
        };
        tripService.retrieveTripEventTemplates(triptype).then(function (res) {
            $scope.eventTemplates = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.eventTemplates = res.records;
        });

        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                tripService.saveTripTemplateName($scope.template.name, tripid, $scope.template.eventTemplate || "").then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $modalInstance.close(tripid);
                });

            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
app.controller('changeTripDivisionCtrl', ['$scope', 'valutils', 'tripService', 'deptService', 'toaster', '$modalInstance', '$http', 'tripid', 'departmentId',
    function ($scope, valutils, tripService, deptService, toaster, $modalInstance, $http, tripid, departmentId) {
        $scope.tripDivision = {
            tripid: tripid,
            departmentId: departmentId.toString()
        };

        deptService.getCurrentUserDepts().then(function (res) {
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.departmentOptions = res.records;
        });

        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                tripService.updateTripDivision(tripid, $scope.tripDivision.departmentId).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $modalInstance.close(tripid);
                });
            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
app.directive('ngConfirmClick', [
    function () {
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click', function (event) {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }])
app.directive('searchQuoteModel', function () {
    return {
        require: '^stTable',
        scope: {
            searchQuoteModel: '='
        },
        link: function (scope, ele, attr, ctrl) {
            var table = ctrl;
            scope.$parent.advsearch = function () {
                scope.$watch('searchQuoteModel', function (val) {
                    table.search(val);
                });
            }
        }
    };
});
app.directive('searchTripModel', function () {
    return {
        require: '^stTable',
        scope: {
            searchTripModel: '='
        },
        link: function (scope, ele, attr, ctrl) {
            var table = ctrl;
            scope.$parent.advsearch2 = function () {
                scope.$watch('searchTripModel', function (val) {
                    table.search(val);
                });
            }
        }
    };
});