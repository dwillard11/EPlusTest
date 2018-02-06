app.controller('quotetemplatelistctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage',
    function ($scope, $timeout, $http, $state, toaster, $localStorage) {
        $scope.isLoading = true;
        $scope.tripTypes = [];
        $scope.selectedTripType = null;
        $scope.itemsByPage = 10;

        $scope.getEpCodeType = function () {
            $http({
                method: 'GET',
                url: "maintenance/userManager/getEpCodeListByType.do",
                params: {
                    "type": "Trip Type"
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.tripTypes = response.data.records;
                    $scope.selectedTripType = response.data.records[0];
                    getData();
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        $scope.getEpCodeType();

        $scope.advsearch = function () {
            getData();
        }

        var getData = function () {
            $scope.isLoading = true;
            $scope.rowCollectionPage = [];
            $http({
                method: 'GET',
                url: "maintenance/quoteTemplate/retrieveQuoteTemplateListByType.do",
                params: {
                    "tripType": ($scope.selectedTripType) ? $scope.selectedTripType.id  : ""
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

        function coverttoexportdataformat(dataCollection) {
            var dataarray = [];
            var keyarray = ["Type", "Name"];
            dataarray.push(keyarray);
            angular
                .forEach(
                    dataCollection,
                    function (item, key) {
                        var valuearray = [];
                        valuearray.push(dvalutils.trimToEmpty(ataCollection[key].typeDesc) + "");
                        valuearray.push(valutils.trimToEmpty(dataCollection[key].name) + "");
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
            $scope.csvfilename = 'Quote_Template_Management.csv';
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
            pdfMake.createPdf(docDefinition1).download('Quote_Template_Management.pdf');
        }


        var templateModal = $("#templateModal");
        templateModal.modal({
            "backdrop": "static",
            keyboard: true,
            show: false
        }).on('hidden.bs.modal',
        function(e) {});
        $scope.addTemplate = function () {
            $scope.templateEntity = {
                type: $scope.selectedTripType.id,
                typeName: $scope.selectedTripType.name,
                name:""
            };
            templateModal.modal("show");
        }
        $scope.submitTemplate = function(isValid) {
            if (isValid && $scope.templateEntity)
            {
                $http({
                    method: 'POST',
                    url: "maintenance/quoteTemplate/saveQuoteTemplateName.do",
                    params: $scope.templateEntity
                }).then(function successCallback(response) {
                        if (response.data.result == 'duplicate') {
                            toaster.pop('error', '', "Quote Template Name CANNOT be duplicated, please choose a new one!");
                            return;
                        }else if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    getData();
                    templateModal.modal("hide");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            }
        }

        $scope.editTemplate = function (type,name) {
            var param = {
                tripType: type,
                name: name
            };
            $state.go('app.system_maintenance.quote_templates', param);
        }

        $scope.removeTemplate = function (type, name) {
            if (confirm('Are you sure you want to delete this record?')) {
                $scope.isLoading = true;

                $http({
                    method: 'GET',
                    url: "maintenance/quoteTemplate/removeQuoteTemplateByCategory.do",
                    params: {
                        "tripType": type,
                        "name": name
                    }
                }).then(function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Delete successfully");
                        getData();
                        $scope.isLoading = false;
                    },
                    function errorCallback(response) {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                        $scope.isLoading = false;
                    });
            };
            return;
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