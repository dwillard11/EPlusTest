app.controller('Departsummaryctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage', '$filter', 'valutils',
    function ($scope, $timeout, $http, $state, toaster, $localStorage, $filter, valutils) {
        $scope.itemsByPage = 10;
        $scope.searchParam = {
            departmentId: "0",
            //startDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            //endDate: $filter('date')(new Date(), 'yyyy-MM-dd')
        }
        $scope.getDepartmentDropDownList = function () {
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
                    if (response.data.records.length > 0)
                        $scope.searchParam.departmentId = response.data.records[0].id;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        loadData = function () {
            var params = {
                departmentId: $scope.searchParam.departmentId || 0,
                startDate: $scope.searchParam.startDate == "" ? "" : $filter('date')($scope.searchParam.startDate, 'yyyy-MM-dd'),
                endDate: $scope.searchParam.endDate == "" ? "" : $filter('date')($scope.searchParam.endDate, 'yyyy-MM-dd')
            };
            $scope.getShipment(params);
            $scope.getQuote(params);
            $scope.getCriticalShipment(params);
            $scope.getMessage(params);
            $scope.getStaffOnline(params);
            refreshData();
        };

        $scope.ShipmentList = [];
        $scope.getShipment = function (params) {
            $scope.isLoading = true;
            $scope.ShipmentList = [];
            $http({
                method: 'GET',
                url: 'dashboard/departSummary/retrieveShipmentForDashboard.do',
                params: params
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    if (!response.data.records) return;
                    $scope.ShipmentList = [];
                    var groupId = "";
                    $.each(response.data.records, function (index,item)
                    {
                        var shipment ={
                            name: item.groupDesc,
                            qty: item.countShipments || 0,
                            value: $filter('number')(valutils.trimToEmpty(item.totalShipments || 0), 2) + " "+item.currency,
                            type: item.groupId == 1 ? "OPEN" : (item.groupId == 2 ? "DEL" : "CLOSE")
                        }
                        if(groupId == item.groupId){shipment.name = "";}
                        if(item.countShipments != 0 || item.totalShipments != 0){
                            $scope.ShipmentList.push(shipment);
                        }
                        groupId = item.groupId;
                    })
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                }
            );
        }
        $scope.QuoteList = [];
        $scope.getQuote = function (params) {
            $scope.isLoading = true;
            $scope.QuoteList = [];
            $http({
                method: 'GET',
                url: 'dashboard/departSummary/retrieveQuoteForDashboard.do',
                params: params
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    if (!response.data.records) return;
                    var item = response.data.records;
                    $scope.QuoteList = [];
                    $scope.QuoteList.push({
                        name: "Open Quotes",
                        qty: item.countOpenQuotes || 0,
                        value: $filter('number')(valutils.trimToEmpty(item.totalOpenQuotes || 0), 2) + "",
                        type: "OPEN"
                    });
                    $scope.QuoteList.push({
                        name: "Cancelled Quotes",
                        qty: item.countCancelledQuotes || 0,
                        value: $filter('number')(valutils.trimToEmpty(item.totalCancelledQuotes || 0), 2) + "",
                        type: "CANCEL"
                    });
                    $scope.QuoteList.push({
                        name: "Approved Quotes",
                        qty: item.countApprovedQuotes || 0,
                        value: $filter('number')(valutils.trimToEmpty(item.totalApprovedQuotes || 0), 2) + "",
                        type: "APPROVED"
                    });
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                }
            );
        }
        $scope.CriticalShipment = "";
        $scope.getCriticalShipment = function (params) {
            $scope.isLoading = true;
            $scope.CriticalShipment = "";
            $http({
                method: 'GET',
                url: 'dashboard/departSummary/retrieveCriticalShipment.do',
                params: params
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    if (!response.data.records) return;
                    var item = response.data.records;
                    $scope.CriticalShipment = item == "G" ? "fa-Green" : (item == "R" ? "fa-Red" : (item == "Y" ? "fa-Orange" : ""))
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                }
            );
        }
        $scope.MessageList = [];
        $scope.getMessage = function (params) {
            $scope.isLoading = true;
            $scope.MessageList = [];
            $http({
                method: 'GET',
                url: 'dashboard/departSummary/retrieveMessageQueue.do',
                params: params
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    if (!response.data.records) return;
                    var item = response.data.records;
                    $scope.MessageList = [];
                    $scope.MessageList.push({
                        name: "Inbox",
                        qty: item.inboxMsg || 0
                    });
                    $scope.MessageList.push({
                        name: "Processed",
                        qty: item.proceededMsg || 0
                    });
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                }
            );
        }

        $scope.onlineList = [];
        $scope.getStaffOnline = function (params) {
            $scope.isLoading = true;
            $scope.onlineList = [];
            $http({
                method: 'GET',
                url: 'dashboard/departSummary/retrieveNumberOfOnlineUsers.do',
                params: params
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.onlineList = response.data.records;
                    $scope.isLoading = false;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                }
            );
        };


        $scope.getDepartmentDropDownList();
        $scope.$watch('searchParam', function () {
            loadData();
        },true);

        $scope.$watch('selectedPointInTime', function () {
            if ($scope.selectedPointInTime != undefined && $scope.selectedPointInTime != null) {
                $scope.changePointInTime($scope.selectedPointInTime);
            }
        });

        $scope.changePointInTime = function (pointInTime) {
            var startDate = "", endDate = "";
            if (pointInTime == 0) {

            } else if (pointInTime == 1) {//Today
                var myDate = new Date();
                startDate = getFormatDate(myDate);
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
            $("#txtStartDate").val(startDate);
            $("#txtEndDate").val(endDate);
            $scope.searchParam = {
                departmentId: $scope.searchParam.departmentId,
                startDate: startDate,
                endDate: endDate
            }
        }

        function getPriorMonthFirstDay(year, month) {
            if (month == 0) {
                month = 11;
                year--;
                return new Date(year, month, 1);
            }
            month--;
            return new Date(year, month, 1);
        }

        function getMonthDays(year, month) {
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

        function getFormatDate(date) {
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

        $scope.showDetails = function (tripStatus, tripType) {
            var param = {
                departmentId: $scope.searchParam.departmentId || 0,
                tripStatus: tripStatus,
                tripType: tripType,
                startDate: $scope.searchParam.startDate == "" ? "" : $filter('date')($scope.searchParam.startDate, 'yyyy-MM-dd'),
                endDate: $scope.searchParam.endDate == "" ? "" : $filter('date')($scope.searchParam.endDate, 'yyyy-MM-dd'),
                searchKey: "",
                pointInTime: $scope.selectedPointInTime || "",
                triggerMenu: false,
                chargeCode: "",
                searchAWB: ""
            };
            if (tripType == "quote"){
                $state.go('app.operation_console.quote_list', param);
            }
            else {
                $state.go('app.operation_console.trip_list', param);
            }
        }

        var timeoutPromise;
        function refreshData(){
            if(timeoutPromise)
                $timeout.cancel(timeoutPromise);
            timeoutPromise = $timeout(function(){
                loadData();
            },60*1000);
        }
        $scope.$on('$destroy',function(){
              if(timeoutPromise)
                $timeout.cancel(timeoutPromise);
         });

        $scope.getData = function () {
            loadData();
        }
    }]);
