app.controller('salereportctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'valutils','$filter',
    function ($scope, $timeout, $http, $state, toaster, $localStorage, valutils,$filter) {
        //$scope.isLoading = true;
        $scope.departmentOptions =[];
        $scope.currencyOptions =[];
        $scope.advsearchfilter = {};
        $scope.itemsByPage = 10;
        $scope.advsearch = function () {
            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "report/reportManager/getSaleReporting.do",
                params: $scope.advsearchfilter
            }).then(
                function successCallback(response) {
                    $scope.isLoading = false;
                    if (response.data.result != 'success' || !response.data.records ) {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage = response.data.records;
                    $scope.conllectionLength = $scope.rowCollectionPage.length;
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
                    $scope.advsearchfilter.departmentIds = $scope.departmentOptions[0].id;
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
					if (response.data.records.length > 0)
                        $scope.advsearchfilter.currency = response.data.records[0].id;
					
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }



        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["Consignee", "Pieces","Weight", "Charge WT", "Units", "Total Cost","Amount Invoiced","Currency"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
						valuearray.push(dataCollection[key].consigneeName || "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalPieces || 0), 2) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalWeight || 0), 2) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].chargedWeight || 0), 2) + "");
						valuearray.push(dataCollection[key].freightUnits || "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalCost || 0), 2) + "");
                        valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].amountInvoiced || 0), 2) + "");
						valuearray.push(dataCollection[key].billCurrency || "");
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
            $scope.csvfilename = 'Sale Reporting.csv';
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

            pdfMake.createPdf(docDefinition1).download('Sale Reporting.pdf');
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