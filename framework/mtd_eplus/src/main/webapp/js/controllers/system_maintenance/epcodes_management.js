app.controller('epcodesmanagectrl', ['$scope', '$filter', '$http', 'editableOptions', 'editableThemes', 'valutils', 'toaster',
    function ($scope, $filter, $http, editableOptions, editableThemes, valutils, toaster) {
        editableThemes.bs3.inputClass = 'input-sm';
        editableThemes.bs3.buttonsClass = 'btn-sm';
        editableOptions.theme = 'bs3';
        $scope.availableOptions = [];
        $scope.selectedOption = [];
        $scope.getEpCodeType = function () {
            $http({
                method: 'GET',
                url: "maintenance/epCodeManager/retrieveEpCodeType.do"
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.availableOptions = response.data.records;
                    $scope.selectedOption = response.data.records[0];
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        $scope.getEpCodeType();
        $scope.getData = function (searchType) {
            $scope.epcodes = [];
            $http({
                method: 'GET',
                url: "maintenance/epCodeManager/retrieveBusinessCode.do",
                params: {
                    "searchType": searchType || ""
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.epcodes = response.data.records;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });

        };

        $scope.getData();

        $scope.$watch('selectedOption', function (newValue, oldValue) {
            $scope.getData($scope.selectedOption.id);
        });

        $scope.showCategoryCode = function (epcode) {
          return epcode.categoryCode;
        };

        $scope.getOtherLable = function (data, index) {
            if(!data || data.length == 0) return;
            $http({
                method: 'GET',
                url: "maintenance/epCodeManager/getMultipileLanguageByText.do",
                params: {
                    "textStr": data
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    var result = response.data.records;
                    var row = $scope.epcodes[index];
                    if(result){
                        row.label_french = result.label_french;
                        row.label_chinese = result.label_chinese;
                        row.label_german = result.label_german;
                        row.label_spanish = result.label_spanish;
                    }
                    row.sortingOrder = index + 1;
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        $scope.checkEpCode = function(data, id) {

        };
        $scope.saveEpCode = function(data, id) {
            if (undefined == id) {
                $http({
                    method: 'POST',
                    url: "maintenance/epCodeManager/addBusinessCode.do",
                    params: {
                        "categoryCode":$scope.selectedOption.name,
                        "status": data.status,
                        "sortingOrder": data.sortingOrder,
                        "remarks": data.remarks,
                        "code": data.code,
                        "label": data.label,
                        "label_english": data.label_english,
                        "label_french":data.label_french,
                        "label_chinese":data.label_chinese,
                        "label_german":data.label_german,
                        "label_spanish":data.label_spanish
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        //angular.extend(data, {cd_id: response.data.records});
                        toaster.pop('success', '', "Save successfully");
                        $scope.getData($scope.selectedOption.id);
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });

            } else {
                $http({
                    method: 'POST',
                    url: "maintenance/epCodeManager/updateBusinessCode.do",
                    params: {
                        "id": id,
                        "categoryCode": $scope.selectedOption.name,
                        "status": data.status,
                        "sortingOrder": data.sortingOrder,
                        "remarks": data.remarks,
                        "code": data.code,
                        "label": data.label,
                        "label_english": data.label_english,
                        "label_french":data.label_french,
                        "label_chinese":data.label_chinese,
                        "label_german":data.label_german,
                        "label_spanish":data.label_spanish
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Save successfully");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        };


        $scope.removeEpCode = function (id, index) {
            if(id) {
                $http({
                    method: 'GET',
                    url: "maintenance/epCodeManager/removeBusinessCode.do",
                    params: {
                        "id": id
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        $scope.epcodes.splice(index, 1);
                        toaster.pop('success', '', "Delete successfully");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }else{
                $scope.epcodes.splice(index, 1);
            }
        };


        $scope.addEpCode = function() {
            $scope.inserted = {
                "categoryCode":$scope.selectedOption.name,
                "status": "Active",
                "sortingOrder": '',
                "remarks": "",
                "code": "",
                "label":"",
                "label_english": "",
                "label_french":"",
                "label_chinese":"",
                "label_german":"",
                "label_spanish":""
            };
            $scope.epcodes.push($scope.inserted);
        };

        $scope.statuses = [
            {value: 'Active', text: 'Active'},
            {value: 'Inactive', text: 'Inactive'}
        ];

        $scope.agenda = [
            {value: 1, text: 'male'},
            {value: 2, text: 'female'}
        ];

        $scope.showAgenda = function() {
            var selected = $filter('filter')($scope.agenda, {value: $scope.user.agenda});
            return ($scope.user.agenda && selected.length) ? selected[0].text : 'Not set';
        };

        // editable table
        $scope.users = [
            {id: 1, name: 'awesome user1', status: 2, group: 4, groupName: 'admin'},
            {id: 2, name: 'awesome user2', status: undefined, group: 3, groupName: 'vip'},
            {id: 3, name: 'awesome user3', status: 2, group: null}
        ];

        $scope.groups = [];
        $scope.loadGroups = function() {
            return $scope.groups.length ? null : $http.get('api/groups').success(function(data) {
                    $scope.groups = data;
                });
        };

        $scope.showGroup = function(user) {
            if(user.group && $scope.groups.length) {
                var selected = $filter('filter')($scope.groups, {id: user.group});
                return selected.length ? selected[0].text : 'Not set';
            } else {
                return user.groupName || 'Not set';
            }
        };

        $scope.showStatus = function (epcode) {
            var selected = [];
            if (epcode && epcode.status) {
                selected = $filter('filter')($scope.statuses, {value: epcode.status});
            }
            return selected.length ? selected[0].text : 'Active';
        };


        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];
            if (type == 'codes') {
                var keyarray = ["Key", "English", "French", "Chinese", "German", "Spanish", "Status", "Description", "Sequence"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].code) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].label_english) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].label_french) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].label_chinese) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].label_german) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].label_spanish) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].status) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].remarks) + "");
                            valuearray.push(dataCollection[key].sortingOrder + "");
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }

        }

        $scope.exportcsv = function (collection, type) {
            if (type == "codes") {
                $scope.csvfilename = 'Code_Management.csv';
            }
            return coverttoexportdataformat(collection, type);

        }
        $scope.exportpdf = function (collection, type) {
            if (type == "codes") {
                $scope.csvfilename = 'Code_Management.pdf';
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