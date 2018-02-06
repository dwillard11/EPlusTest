app.controller('tsaairportctrl', ['$scope', '$window', '$timeout', '$http', '$state', 'toaster', '$localStorage',
    function ($scope, $window, $timeout, $http, $state, toaster, $localStorage) {
        $scope.isLoading = false;
        $scope.advsearchfilter = {
            tsaAirport: "",
            quickName: "",
            address: ""
        };

        if ($window.sessionStorage["tsaAirportAirportName"]) {
            $scope.advsearchfilter.tsaAirport = $window.sessionStorage["tsaAirportAirportName"];
        }
        if ($window.sessionStorage["tsaAirportQuickName"]) {
            $scope.advsearchfilter.quickName = $window.sessionStorage["tsaAirportQuickName"];
        }
        if ($window.sessionStorage["tsaAirportAddress"]) {
            $scope.advsearchfilter.address = $window.sessionStorage["tsaAirportAddress"];
        }

        $scope.itemsByPage = 10;
        $scope.advsearch = function () {

            $scope.getData();
        }

        $scope.getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "report/reportManager/getTSAAuthorizedAgentByAirportReporting.do",
                params: $scope.advsearchfilter
            }).then(
                function successCallback(response) {
                    $scope.isLoading = false;
                    if (response.data.result != 'success' || !response.data.records) {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.rowCollectionPage = response.data.records;
                    $scope.conllectionLength = $scope.rowCollectionPage.length;
                    $window.sessionStorage["tsaAirportAirportName"] = $scope.advsearchfilter.tsaAirport||"";
                    $window.sessionStorage["tsaAirportQuickName"] = $scope.advsearchfilter.quickName||"";
                    $window.sessionStorage["tsaAirportAddress"] = $scope.advsearchfilter.address||"";
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        //$scope.getData();

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["TSA Airport", "Name", "Address 1", "Bus Phone", "Email", "TSA Contact"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(dataCollection[key].tsaAirport || "");
                        valuearray.push(dataCollection[key].customerName || "");
                        valuearray.push(dataCollection[key].address1 || "");
						valuearray.push(dataCollection[key].busPhone1 || "");
						valuearray.push(dataCollection[key].email || "");
						valuearray.push(dataCollection[key].tsaContact || "");
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
            $scope.csvfilename = 'TSA Authorized Agent by Airport Reporting.csv';
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

            pdfMake.createPdf(docDefinition1).download('TSA Authorized Agent by Airport Reporting.pdf');
        }
        $scope.view = function (entityId,customerId) {
            toaster.pop('wait', '', 'Loading...');
            var param = {
                entityId: entityId,
                selectId: customerId + 1000000,
                status: "from_edit"
            };
            $state.go('app.system_maintenance.entity_edit', param);
        };

		(function(){
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