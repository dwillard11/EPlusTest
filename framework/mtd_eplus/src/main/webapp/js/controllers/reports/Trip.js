app.controller('tripreportctrl', ['$scope', '$window', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'valutils','$filter',
    function ($scope, $window, $timeout, $http, $state, toaster, $localStorage, valutils,$filter) {
        //$scope.isLoading = true;
        $scope.departmentOptions =[];
        $scope.advsearchfilter = {
            //pickupDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            //deliveryDate: $filter('date')(new Date(), 'yyyy-MM-dd')
            shipperName: "",
            consigneeName: "",
            podName: "",
            tripNum: "",
            deliveryDate: "",
            pickupDate: ""
        };

        if ($window.sessionStorage["tripReportPickupDate"]) {
            $scope.advsearchfilter.pickupDate = $window.sessionStorage["tripReportPickupDate"];
        }
        if ($window.sessionStorage["tripReportDeliveryDate"]) {
            $scope.advsearchfilter.deliveryDate = $window.sessionStorage["tripReportDeliveryDate"];
        }
        if ($window.sessionStorage["tripReportShipperName"]) {
            $scope.advsearchfilter.shipperName = $window.sessionStorage["tripReportShipperName"];
        }
        if ($window.sessionStorage["tripReportConsigneeName"]) {
            $scope.advsearchfilter.consigneeName = $window.sessionStorage["tripReportConsigneeName"];
        }
        if ($window.sessionStorage["tripReportPodName"]) {
            $scope.advsearchfilter.podName = $window.sessionStorage["tripReportPodName"];
        }
        if ($window.sessionStorage["tripReportTripNum"]) {
            $scope.advsearchfilter.tripNum = $window.sessionStorage["tripReportTripNum"];
        }

        $scope.itemsByPage = 10;
        $scope.advsearch = function () {
            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            var param = $scope.advsearchfilter;
            param.pickupDate = param.pickupDate == "" ? "" : $filter('date')(param.pickupDate, 'yyyy-MM-dd');
            param.deliveryDate = param.endDate == "" ? "" : $filter('date')(param.deliveryDate, 'yyyy-MM-dd');

            $http({
                method: 'GET',
                url: "report/reportManager/getTripReport.do",
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

                    $window.sessionStorage["tripReportDepartmentIds"] = $scope.advsearchfilter.departmentIds;
                    $window.sessionStorage["tripReportPickupDate"] = $filter('date')($scope.advsearchfilter.pickupDate, 'yyyy-MM-dd');
                    $window.sessionStorage["tripReportDeliveryDate"] = $filter('date')($scope.advsearchfilter.deliveryDate, 'yyyy-MM-dd');
                    $window.sessionStorage["tripReportShipperName"] = $scope.advsearchfilter.shipperName;
                    $window.sessionStorage["tripReportShipperName"] = $scope.advsearchfilter.shipperName;
                    $window.sessionStorage["tripReportConsigneeName"] = $scope.advsearchfilter.consigneeName;
                    $window.sessionStorage["tripReportPodName"] = $scope.advsearchfilter.podName;
                    $window.sessionStorage["tripReportTripNum"] = $scope.advsearchfilter.tripNum;
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };

        var getOptionList = function(){
            $scope.departmentOptions =[];
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
                    if ($window.sessionStorage["tripReportDepartmentIds"]) {
                        $scope.advsearchfilter.departmentIds = $window.sessionStorage["tripReportDepartmentIds"];
                    }
                    else {
                        $scope.advsearchfilter.departmentIds = $scope.departmentOptions[0].id;
                    }
                    //$timeout(function(){$scope.getData();},1000);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["Trip #", "Pickup Date", "Delivery Date", "Shipper", "Consignee", "Pod Name"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(dataCollection[key].tripRefNo || "");
                        valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].pickupDate), "yyyy-MM-dd"));
                        valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].deliveryDate), "yyyy-MM-dd"));
						valuearray.push(dataCollection[key].shipperName || "");
						valuearray.push(dataCollection[key].consigneeName || "");
						valuearray.push(dataCollection[key].podName || "");
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
            $scope.csvfilename = 'Trip Reporting.csv';
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

            pdfMake.createPdf(docDefinition1).download('Trip Reporting.pdf');
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