app.controller('currencyreportctrl', ['$scope', '$window', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'valutils','$filter',
    function ($scope, $window, $timeout, $http, $state, toaster, $localStorage, valutils,$filter) {
        //$scope.isLoading = true;
        $scope.departmentOptions =[];
        $scope.currencyOptions =[];
        $scope.advsearchfilter = {
            clientName: "",
            shipperName: "",
            consigneeName: "",
            tripNum: "",
            pointInTime:"",
            endDate: "",
            startDate: ""
        };
        if ($window.sessionStorage["currencyReportPointInTime"]) {
            $scope.advsearchfilter.pointInTime = $window.sessionStorage["currencyReportPointInTime"];
        }

        if ($window.sessionStorage["currencyReportStartDate"]) {
            $scope.advsearchfilter.startDate = $filter('date')($window.sessionStorage["currencyReportStartDate"], 'yyyy-MM-dd');
        }
        if ($window.sessionStorage["currencyReportEndDate"]) {
            $scope.advsearchfilter.endDate = $filter('date')($window.sessionStorage["currencyReportEndDate"], 'yyyy-MM-dd');
        }
        if ($window.sessionStorage["currencyReportClientName"]) {
            $scope.advsearchfilter.clientName = $window.sessionStorage["currencyReportClientName"];
        }
        if ($window.sessionStorage["currencyReportShipperName"]) {
            $scope.advsearchfilter.shipperName = $window.sessionStorage["currencyReportShipperName"];
        }
        if ($window.sessionStorage["currencyReportConsigneeName"]) {
            $scope.advsearchfilter.consigneeName = $window.sessionStorage["currencyReportConsigneeName"];
        }
        if ($window.sessionStorage["currencyReportTripNum"]) {
            $scope.advsearchfilter.tripNum = $window.sessionStorage["currencyReportTripNum"];
        }

        $scope.$watch('advsearchfilter.pointInTime', function() {
            if ($scope.advsearchfilter.pointInTime != undefined && $scope.advsearchfilter.pointInTime != null && $scope.advsearchfilter.pointInTime != '') {
                changePointInTime($scope.advsearchfilter.pointInTime);
            }
        });

        $scope.itemsByPage = 10;
        $scope.advsearch = function () {
            $scope.getData();
        }
        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            var param = $scope.advsearchfilter;
            param.startDate = param.startDate == "" ? "" : $filter('date')(param.startDate, 'yyyy-MM-dd');
            param.endDate = param.endDate == "" ? "" : $filter('date')(param.endDate, 'yyyy-MM-dd');

            $http({
                method: 'GET',
                url: "report/reportManager/getCurrencyReportByDepartment.do",
                params: param
            }).then(
                function successCallback(response) {
                    $scope.isLoading = false;
                    if (response.data.result != 'success' || !response.data.records ) {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage = response.data.records;
                    $scope.conllectionLength = $scope.rowCollectionPage.length;

                    $window.sessionStorage["currencyReportDepartmentIds"] = $scope.advsearchfilter.departmentIds;
                    $window.sessionStorage["currencyReportPointInTime"] = $scope.advsearchfilter.pointInTime;
                    $window.sessionStorage["currencyReportStartDate"] = $filter('date')($scope.advsearchfilter.startDate, 'yyyy-MM-dd')||"";
                    $window.sessionStorage["currencyReportEndDate"] = $filter('date')($scope.advsearchfilter.endDate, 'yyyy-MM-dd')||"";
                    $window.sessionStorage["currencyReportClientName"] = $scope.advsearchfilter.clientName;
                    $window.sessionStorage["currencyReportShipperName"] = $scope.advsearchfilter.shipperName;
                    $window.sessionStorage["currencyReportConsigneeName"] = $scope.advsearchfilter.consigneeName;
                    $window.sessionStorage["currencyReportTripNum"] = $scope.advsearchfilter.tripNum;
                    $window.sessionStorage["currencyReportSelectedCategory"] = $scope.advsearchfilter.selectedCategory;
                    $window.sessionStorage["currencyReportCurrency"] = $scope.advsearchfilter.currency;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };

        var getOptionList = function(){
            $scope.departmentOptions =[];
            $scope.currencyOptions =[];
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
					var ids = "";
                    if (response.data.records.length > 1){
						response.data.records.forEach(function(item,index){
							if(index == 1 ){
								ids += item.id;
							}else if(index > 1 ){
								ids +=  "," +item.id;
							}
						});
					}
					$scope.departmentOptions[0].id = ids;
                    if ($window.sessionStorage["currencyReportDepartmentIds"]) {
                        $scope.advsearchfilter.departmentIds = $window.sessionStorage["currencyReportDepartmentIds"];
                    }
                    else {
                        $scope.advsearchfilter.departmentIds = $scope.departmentOptions[0].id;
                    }
					//$timeout(function(){$scope.getData();},1000);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
            });
            $http({
                method: 'GET',
                url: "maintenance/departmentManager/getEpCodeListByType.do",
                params: {
                    "type": "Currency"
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
					$scope.currencyOptions = response.data.records;
                    $scope.currencyOptions.splice(0,0,{id:"",name:"ALL"});
                    if ($window.sessionStorage["currencyReportCurrency"]) {
                        $scope.advsearchfilter.currency = $window.sessionStorage["currencyReportCurrency"];
                    }
                    else {
                        if (response.data.records.length > 0)
                            $scope.advsearchfilter.currency = response.data.records[0].id;
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }

        $scope.categories = [
            {id:'ALL',name:'ALL'},
            {id: 'OPEN', name: 'Open Shipments'},
            {id: 'DEL', name: 'Delivered Shipments'},
            {id: 'CLOSE', name: 'Close Shipments'}
        ];
        if ($window.sessionStorage["currencyReportSelectedCategory"]) {
            $scope.advsearchfilter.selectedCategory = $window.sessionStorage["currencyReportSelectedCategory"];
        }
        else {
            $scope.advsearchfilter.selectedCategory = $scope.categories[0].id;
        }

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["Trip #", "Trip Type", "Client", "Pickup Date", "Delivery Date", "Shipper", "Consignee", "PCS", "Wgt", "Charge Wgt", "Units", "Total Cost", "Inv Amount", "Cur.", "Invoice #"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(dataCollection[key].tripRefNo || "");
                        valuearray.push(dataCollection[key].typeDesp || "");
                        valuearray.push(dataCollection[key].clientName || "");
                        valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].pickupDate), "yyyy-MM-dd"));
                        valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].deliveryDate), "yyyy-MM-dd"));
						valuearray.push(dataCollection[key].shipperName || "");
						valuearray.push(dataCollection[key].consigneeName || "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalPieces || 0), 0) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalWeight || 0), 2) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].chargedWeight || 0), 2) + "");
						valuearray.push(dataCollection[key].freightUnits || "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalCost || 0), 2) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].amountInvoiced || 0), 2) + "");
						valuearray.push(dataCollection[key].billCurrency || "");
                        valuearray.push(dataCollection[key].invoiceNo || "");
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
            $scope.csvfilename = 'Currency Reporting.csv';
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

            pdfMake.createPdf(docDefinition1).download('Currency Reporting.pdf');
        }

        $scope.view = function (id,deptId,isEdit) {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                tripid: id,
                tripmode: isEdit? "edit" : "read",
                triptype: "trip",
                dept: deptId
            };
            $state.go("app.operation_console.trip_details", param);
        };
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

		(function(){
		    getOptionList();
			$(".titleToggle").click(function(){
				var $this = $(this);
				var $panelbody = $this.parent().parent().find(".panel-body");
				if($panelbody.is(":hidden"))
				{
					$panelbody.slideDown("fast",function(){
					   $this.removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
					 });
				}else{
					$panelbody.slideUp("fast",function(){
					   $this.removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
					});
				}
			});
		 })();

    }]);