app.controller('ViewMessagesCtrl',['$scope','$timeout','$http','$state','toaster','$localStorage',
        function ($scope, $timeout, $http, $state, toaster,$localStorage) {
			var rowHeight = $localStorage.user.rowHeight || 1;
            $scope.rowstyle = {
                'padding': rowHeight + 'px 15px'
            };
            $scope.changerowstyle = function (rowHeightCode) {

                $http({
                    method: 'GET',
                    url: "updateUserRowHeightCode.do",
                    params: {
                        "rowHeightCode": rowHeightCode,
                        "userId": $localStorage.user.id
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error','',response.data.msg);
                            return;
                        }
                        var rowHeight = response.data.records;
                        $localStorage.user.rowHeight = rowHeight;
                        $localStorage.user.rowHeightCode = rowHeightCode;
                        $scope.rowstyle = {
                            'padding': rowHeight + 'px 15px'
                        };
                    },
                    function errorCallback(response) {
                        toaster.pop('error', '',"server error");
                    });
            }
            $scope.isLoading = true;
            $scope.advsearchfilter = null;
            $scope.itemsByPage = 10;
            $scope.advsearch = function () {
                $scope.getData($scope.advsearchfilter);
            }

            $scope.getData = function (searchdata) {
                $scope.isLoading = true;
                $scope.rowCollectionPage = [];
                var result = [];
                $http({
                    method: 'GET',
                    url: "retrieveMessages.do",
                    params: {
                        "advanceSearch": searchdata
                    }

                }).then(
                    function successCallback(
                        response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error','',response.data.msg);
                            return;
                        }
                        $scope.rowCollectionPage = response.data.records;
                        $scope.isLoading = false;
                        $scope.conllectionLength =  $scope.rowCollectionPage.length;
                    },
                    function errorCallback(response) {
                        $scope.isLoading = false;
                        toaster.pop('error', '',"server error");
                    });
            };
            var searchdata = $scope.advsearchfilter || null;
            $scope.getData(searchdata);

            $scope.item_ids = [];
            $scope.check = false;
            $scope.selectAllItems = function () {
                $scope.check = !$scope.check;
                angular.forEach($scope.displayedCollection,
                    function (item, key) {
                        $scope.displayedCollection[key].selected = $scope.check;
                    });

            };

            $scope.removeMessages = function () {
                $scope.isLoading = true;
                var removeItems = "";
                angular.forEach(
                    $scope.displayedCollection,
                    function (item, key) {
                        if ($scope.displayedCollection[key].selected) {
                            removeItems += item.id + ",";
                        }
                    });
                if (removeItems === "" || removeItems === null || typeof removeItems === "undefined") {
                    toaster.pop('warning', '',"choose an item");
                    $scope.isLoading = false;
                    return;
                }
                var jsonData = angular.toJson(removeItems);
                var objectToSerialize = {
                    'object': jsonData
                };
                $http({
                    method: 'GET',
                    url: "removeMessages.do",
                    params: {
                        "messageIds": removeItems
                    }
                }).then(
                    function successCallback(
                        response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error','',response.data.msg);
                            return;
                        }
                        $scope.getData($scope.advsearchfilter);
                        $scope.isLoading = false;
                    },
                    function errorCallback(response) {
                        toaster.pop('error', '',"server error");
                        $scope.isLoading = false;
                    });
            };
            function coverttoexportdataformat(dataCollection) {
                var dataarray = [];
                var keyarray = ["Activity Date",
                    "Activity Time", "Probill #",
                    "Activity", "Information"];

                dataarray.push(keyarray);
                angular
                    .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(dataCollection[key].formattedActivityDate + "");
                        valuearray.push(dataCollection[key].formattedActivityTime + "");
                        valuearray.push(dataCollection[key].probillNo + "");
                        valuearray.push(dataCollection[key].activity + "");
                        valuearray.push(dataCollection[key].information + "");
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
                $scope.csvfilename = 'MESSAGE_CENTRAL_View_Messages.csv'; 
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

                pdfMake.createPdf(docDefinition1).download('MESSAGE_CENTRAL_View_Messages.pdf');
            }
            $scope.showMessageDetails = function (messageId) {
                toaster.pop('wait', '', 'Loading...');
                var param = {
                    messageId: messageId
                };
                $state.go('app.messagecentral.messagedetails',param);
                   
            };

        }]);