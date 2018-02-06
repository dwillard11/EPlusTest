/**
 * Created by admin on 3/16/2017.
 */
app.controller('quotelistctrl', ['$scope', '$window', '$timeout', '$http', '$state', 'toaster', '$stateParams', '$localStorage', 'deptService', 'valutils','$filter', '$modal',
    function ($scope, $window, $timeout, $http, $state, toaster, $stateParams, $localStorage, deptService, valutils, $filter, $modal) {
        $scope.isLoading = false;
        $scope.advsearchfilter = $stateParams ? $stateParams : {departmentId: "",tripStatus:"",tripType:"quote", searchKey:"",startDate:"",endDate:"",pointInTime:"", searchAWB:"",eventDesc:"",chargeCode:"",chargeDesc:"",advsearchfilter:""};
        //$scope.advsearchfilter.startDate = $filter('date')(new Date(), 'yyyy-MM-dd');
        //$scope.advsearchfilter.endDate = $filter('date')(new Date(), 'yyyy-MM-dd');
        $scope.departmentOptions = [];
        $scope.showOptions = [];
        $scope.chargeCodeOptions = [];
        $scope.itemsByPage = 20;
        $scope.IsQuotes = $scope.advsearchfilter.tripType =="quote";
        $scope.advsearchfilter.searchAWB = "";
        if ($stateParams.triggerMenu) {
            if ($scope.IsQuotes) {
                if ($window.sessionStorage["quoteListPointInTime"]) {
                    $scope.advsearchfilter.pointInTime = $window.sessionStorage["quoteListPointInTime"];
                }
                if ($window.sessionStorage["quoteListStartDate"]) {
                    $scope.advsearchfilter.startDate = $window.sessionStorage["quoteListStartDate"];
                }
                if ($window.sessionStorage["quoteListEndDate"]) {
                    $scope.advsearchfilter.endDate = $window.sessionStorage["quoteListEndDate"];
                }
                if ($window.sessionStorage["quoteListDepartmentId"]) {
                    $scope.advsearchfilter.departmentId = $window.sessionStorage["quoteListDepartmentId"];
                }
            }
            else {
                if ($window.sessionStorage["tripListPointInTime"]) {
                    $scope.advsearchfilter.pointInTime = $window.sessionStorage["tripListPointInTime"];
                }
                if ($window.sessionStorage["tripListStartDate"]) {
                    $scope.advsearchfilter.startDate = $window.sessionStorage["tripListStartDate"];
                }
                if ($window.sessionStorage["tripListEndDate"]) {
                    $scope.advsearchfilter.endDate = $window.sessionStorage["tripListEndDate"];
                }
                if ($window.sessionStorage["tripListDepartmentId"]) {
                    $scope.advsearchfilter.departmentId = $window.sessionStorage["tripListDepartmentId"];
                }
                if ($window.sessionStorage["tripListSearchAWB"]) {
                    $scope.advsearchfilter.searchAWB = $window.sessionStorage["tripListSearchAWB"];
                }
                if ($window.sessionStorage["tripListEventDesc"]) {
                    $scope.advsearchfilter.eventDesc = $window.sessionStorage["tripListEventDesc"];
                }
                if ($window.sessionStorage["tripListChargeCode"]) {
                    $scope.advsearchfilter.chargeCode = $window.sessionStorage["tripListChargeCode"];
                }
                if ($window.sessionStorage["tripListChargeDesc"]) {
                    $scope.advsearchfilter.chargeDesc = $window.sessionStorage["tripListChargeDesc"];
                }
            }
        }

        $scope.AWBList =[];
        (function getDepartmentDropDownList() {
            $http({
                method: 'GET',
                url: "dashboard/departSummary/getDepartmentDropDownListWithAll.do",
                params: {}
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.departmentOptions = response.data.records;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });

            $http({
                method: 'GET',
                url: "maintenance/epCodeManager/retrieveBusinessCode.do",
                params: {searchType:"Event Code"}
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success')
                    {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.AWBList = response.data.records;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }());

        (function getShowDropDownList() {
            if(!$scope.IsQuotes){
                $scope.showOptions = [
                    {id:'ALL',name:'ALL'},
                    {id:"OPEN",name:"Open Shipments"},
                    {id:"CLOSE",name:"Closed/ Invoiced"},
                    {id:"DEL",name:"Delivered/ Not Invoiced"}
                ];

                if ($stateParams.triggerMenu) {
                    if ($window.sessionStorage["tripListTripStatus"]) {
                        $scope.advsearchfilter.tripStatus = $window.sessionStorage["tripListTripStatus"];
                    }
                    else {
                        $scope.advsearchfilter.tripStatus = "OPEN";
                    }
                }
            }else{
                $scope.showOptions = [{id:"OPEN",name:"Open Quotes"},{id:"CANCEL",name:"Cancelled Quotes"},{id:"APPROVED",name:"Approved Quotes"}];
                if ($stateParams.triggerMenu) {
                    if ($window.sessionStorage["quoteListTripStatus"]) {
                        $scope.advsearchfilter.tripStatus = $window.sessionStorage["quoteListTripStatus"];
                    }
                    else {
                        $scope.advsearchfilter.tripStatus = "OPEN";
                    }
                }
            }
        }());

        var getEpCodeData = function (type) {
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
                        if (type == "Charge Code") {
                            $scope.chargeCodeOptions = response.data.records;
                            $scope.chargeCodeOptions.splice(0,0,{id:"",name:"ALL"});
                            if ($stateParams.triggerMenu) {
                                if (!$window.sessionStorage["tripListChargeCode"]) {
                                    if (response.data.records.length > 0)
                                        $scope.advsearchfilter.chargeCode = response.data.records[0].id;
                                }
                            }
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }
        getEpCodeData("Charge Code");

        $scope.$watch('advsearchfilter.pointInTime', function() {
            if ($scope.advsearchfilter.pointInTime != undefined && $scope.advsearchfilter.pointInTime != null && $scope.advsearchfilter.pointInTime != '') {
                changePointInTime($scope.advsearchfilter.pointInTime);
            }
        });

        $scope.advsearch = function () {
            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            var param =  $scope.advsearchfilter;
            param.tripCategory = param.tripStatus;
            param.startDate = param.startDate == "" ? "" : $filter('date')(param.startDate, 'yyyy-MM-dd');
            param.endDate = param.endDate == "" ? "" : $filter('date')(param.endDate, 'yyyy-MM-dd');

            $http({
                method: 'POST',
                url: $scope.IsQuotes ? "operationconsole/operationconsole/retrieveQuoteList.do" : "operationconsole/operationconsole/retrieveTripList.do",
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
                    if ($scope.IsQuotes) {
                        $window.sessionStorage["quoteListPointInTime"] = $scope.advsearchfilter.pointInTime;
                        $window.sessionStorage["quoteListStartDate"] = $filter('date')($scope.advsearchfilter.startDate, 'yyyy-MM-dd');
                        $window.sessionStorage["quoteListEndDate"] = $filter('date')($scope.advsearchfilter.endDate, 'yyyy-MM-dd');
                        $window.sessionStorage["quoteListDepartmentId"] = $scope.advsearchfilter.departmentId;
                        $window.sessionStorage["quoteListTripStatus"] = $scope.advsearchfilter.tripStatus||'';
                    }
                    else {
                        $window.sessionStorage["tripListPointInTime"] = $scope.advsearchfilter.pointInTime;
                        $window.sessionStorage["tripListStartDate"] = $filter('date')($scope.advsearchfilter.startDate, 'yyyy-MM-dd');
                        $window.sessionStorage["tripListEndDate"] = $filter('date')($scope.advsearchfilter.endDate, 'yyyy-MM-dd');
                        $window.sessionStorage["tripListDepartmentId"] = $scope.advsearchfilter.departmentId;
                        $window.sessionStorage["tripListTripStatus"] = $scope.advsearchfilter.tripStatus||'';
                        $window.sessionStorage["tripListChargeCode"] = $scope.advsearchfilter.chargeCode||'';
                        $window.sessionStorage["tripListChargeDesc"] = $scope.advsearchfilter.chargeDesc||'';
                        $window.sessionStorage["tripListSearchAWB"] = $scope.advsearchfilter.searchAWB||'';
                        $window.sessionStorage["tripListEventDesc"] = $scope.advsearchfilter.eventDesc||'';
                    }
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };

        $scope.recordStatus = function (shipperCountry, consigneeCountry) {
            if (!(shipperCountry == "CA" || shipperCountry == "US" ||  consigneeCountry == "CA" || consigneeCountry == "US" )) {
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
            if (timeStr > twoStr)
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

        $scope.showEntity = function (id, mode, dept) {
            if (!valutils.isEmptyOrUndefined(dept) && valutils.isEmptyOrUndefined($localStorage.user.defaultCurrency)) {
                // fill dept defaultCurrency in user.defaultCurrency
                deptService.getDeptInfo(dept).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $localStorage.user.defaultCurrency = res.records.defaultCurrency;
                })
            }
            var param = {
                tripid: id,
                tripmode: mode,
                triptype: $scope.IsQuotes ? "quote" : "trip",
                dept: dept
            };
            if (mode == "fromQuoteToTrip") {
                param.triptype = "trip";
                param.tripmode = "fromQuote";
            }
            if(!$scope.IsQuotes || mode =="fromQuote" || mode == "fromQuoteToTrip")
                $state.go("app.operation_console.trip_details", param);
            else
                $state.go("app.operation_console.quote_details", param);
        }

        $scope.removeEntity = function (id) {
            $scope.isLoading = true;
            var removeItems = id;
            return;
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

        $scope.btnQuotePDF = function (id) {
            var param = {
                quoteid: id
            };
            $state.go("app.operation_console.quote_builder", param);
        };
        $scope.showPickup = function (id, mode) {
            var param = {
                tripid: id,
                tripmode: mode,
                triptype: "pickup"
            };
            $state.go("app.operation_console.pickup_details", param);
        };
        $scope.showBOL = function (id, mode) {
            var param = {
                tripid: id,
                tripmode: mode,
                triptype: "bol"
            };
            $state.go("app.operation_console.bol_details", param);
        };
        $scope.showInvoice = function (id, mode) {
            var param = {
                tripid: id,
                tripmode: mode,
                triptype: "invoice"
            };
            $state.go("app.operation_console.invoice_details", param);
        };

        $scope.changeTripDivision = function (id, departmentId) {
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
                $scope.getData();
            }, function () {

            });
        }

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


        function changePointInTime (pointInTime) {
            var startDate = "";
            var endDate = "";
            if (pointInTime == 0) {

            } else if (pointInTime == 1) {//Today
                var myDate = new Date();
                startDate =  getFormatDate(myDate);
                endDate = getFormatDate(myDate);
            } else if (pointInTime == 2) {//Yesterday
                var myDate = new Date();
                myDate.setDate(myDate.getDate() - 1);
                startDate = getFormatDate(myDate);
                endDate = getFormatDate(myDate);
            } else if (pointInTime == 3) { //This week
                var currentDate = new Date();
                var week = currentDate.getDay();
                var month = currentDate.getDate();
                var millisecond = 1000 * 60 * 60 * 24;
                var minusDay = week != 0 ? week - 1 : 6;
                var monday = new Date(currentDate.getTime() - (minusDay * millisecond));
                var sunday = new Date(monday.getTime() + (6 * millisecond));
                startDate = getFormatDate(monday);
                endDate = getFormatDate(sunday);
            } else if (pointInTime == 4) { //Last week
                var currentDate = new Date();
                var week = currentDate.getDay();
                var month = currentDate.getDate();
                var millisecond = 1000 * 60 * 60 * 24;
                var minusDay = week != 0 ? week - 1 : 6;
                var currentWeekDayOne = new Date(currentDate.getTime() - (millisecond * minusDay));
                var priorWeekLastDay = new Date(currentWeekDayOne.getTime() - millisecond);
                var priorWeekFirstDay = new Date(priorWeekLastDay.getTime() - (millisecond * 6));
                startDate = getFormatDate(priorWeekFirstDay);
                endDate = getFormatDate(priorWeekLastDay);
            } else if (pointInTime == 5) {//This month
                var currentDate = new Date();
                var currentMonth = currentDate.getMonth();
                var currentYear = currentDate.getFullYear();
                var firstDay = new Date(currentYear, currentMonth, 1);
                if (currentMonth == 11) {
                    currentYear++;
                    currentMonth = 0;
                } else {
                    currentMonth++;
                }
                var millisecond = 1000 * 60 * 60 * 24;
                var nextMonthDayOne = new Date(currentYear, currentMonth, 1);
                var lastDay = new Date(nextMonthDayOne.getTime() - millisecond);
                startDate = getFormatDate(firstDay);
                endDate = getFormatDate(lastDay);
            } else if (pointInTime == 6) {//Last month
                var currentDate = new Date();
                var currentMonth = currentDate.getMonth();
                var currentYear = currentDate.getFullYear();
                var priorMonthFirstDay = getPriorMonthFirstDay(currentYear, currentMonth);
                var priorMonthLastDay = new Date(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth(), getMonthDays(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth()));
                startDate = getFormatDate(priorMonthFirstDay);
                endDate = getFormatDate(priorMonthLastDay);
            } else if (pointInTime == 7) {//This year
                var currentDate = new Date();
                var currentYear = currentDate.getFullYear();
                startDate = getFormatDate(new Date(currentYear, 0, 1));
                endDate = getFormatDate(new Date(currentYear, 11, 31));
            } else if (pointInTime == 8) {//This year to date
                var currentDate = new Date();
                var currentYear = currentDate.getFullYear();
                startDate = getFormatDate(new Date(currentYear, 0, 1));
                endDate = getFormatDate(currentDate);
            } else if (pointInTime == 9) {//Last year
                var currentDate = new Date();
                var currentYear = currentDate.getFullYear();
                currentYear--;
                startDate = getFormatDate(new Date(currentYear, 0, 1));
                endDate = getFormatDate(new Date(currentYear, 11, 31));
            } else if (pointInTime == 10) {//Last year to date
                var currentDate = new Date();
                var currentYear = currentDate.getFullYear();
                currentYear--;
                startDate = getFormatDate(new Date(currentYear, 0, 1));
                endDate = getFormatDate(currentDate);
            }
            $scope.advsearchfilter.startDate = startDate;
            $scope.advsearchfilter.endDate = endDate;
            //$("#txtStartDate").val($scope.startDate);
            //$("#txtEndDate").val($scope.endDate);
        }

        function getPriorMonthFirstDay (year, month) {
            if (month == 0) {
                month = 11;
                year--;
                return new Date(year, month, 1);
            }
            month--;
            return new Date(year, month, 1);
        }

        function getMonthDays (year, month) {
            var relativeDate = new Date(year, month, 1);
            var relativeMonth = relativeDate.getMonth();
            var relativeYear = relativeDate.getFullYear();
            if (relativeMonth == 11) {
                relativeYear++;
                relativeMonth = 0;
            } else {
                relativeMonth++;
            }
            var millisecond = 1000 * 60 * 60 * 24;
            var nextMonthDayOne = new Date(relativeYear, relativeMonth, 1);
            return new Date(nextMonthDayOne.getTime() - millisecond).getDate();
        }

        function getFormatDate (date) {
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            var seperator = "-";
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 1 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var formatDate = year + seperator + month + seperator + strDate;
            return formatDate;
        };
        $scope.selectAWB = function(){
            $scope.advsearchfilter.searchAWB = $scope.selectAWBOption;
        }


        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];
            if (type == 'trip') {
                var keyarray;
                var hasChargeFilter = false;
                var hasEventFilter = false;

                if (($scope.advsearchfilter.chargeCode != undefined && $scope.advsearchfilter.chargeCode != null && $scope.advsearchfilter.chargeCode != '') ||
                    ($scope.advsearchfilter.chargeDesc != undefined && $scope.advsearchfilter.chargeDesc != null && $scope.advsearchfilter.chargeDesc != '')) {
                    hasChargeFilter = true;
                }
                if (($scope.advsearchfilter.searchAWB != undefined && $scope.advsearchfilter.searchAWB != null && $scope.advsearchfilter.searchAWB != '') ||
                    ($scope.advsearchfilter.eventDesc != undefined && $scope.advsearchfilter.eventDesc != null && $scope.advsearchfilter.eventDesc != '')) {
                    hasEventFilter =true;
                }
                if (hasChargeFilter && hasEventFilter){
                    keyarray = ["Trip#", "Charge Code", "Charge Details", "Event Code", "Event Desc", "Client", "Shipper", "Consignee", "Status", "Orig", "Dest", "Critical Time", "Type"];
                } else {
                    if (hasChargeFilter){
                        keyarray = ["Trip#", "Charge Code", "Charge Details", "Client", "Shipper", "Consignee", "Status", "Orig", "Dest", "Critical Time", "Type"];
                    } else if (hasEventFilter){
                        keyarray = ["Trip#", "Event Code", "Event Desc", "Client", "Shipper", "Consignee", "Status", "Orig", "Dest", "Critical Time", "Type"];
                    } else {
                        keyarray = ["Trip#", "Client", "Shipper", "Consignee", "Status", "Orig", "Dest", "Critical Time", "Type"];
                    }
                }

                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].tripRefNo));
                            if (hasChargeFilter) {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].chargeCode));
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].chargeDesc));
                            }
                            if (hasEventFilter) {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].eventCode));
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].eventDesc));
                            }
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].clientName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].shipperName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].consigneeName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].statusDesp));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].orig));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].dest));
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].criticalTime), "yyyy-MM-dd HH:mm"));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].typeDesp));
                            /*
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].nextEventName));
                            //valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].nextEventDate), "yyyy-MM-dd HH:mm"));
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].note));
                            //valuearray.push(valutils.trimToEmpty(dataCollection[key].numberOfUnreadEmail));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].numberOfUnreadEmail)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].numberOfUnreadEmail));
                            }
                            */
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'quote') {
                var keyarray = ["Quote #", "Client", "Shipper", "Consignee", "Version", "Expires", "Orig", "Dest", "Type"];
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
                element.bind('click', function () {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }])
