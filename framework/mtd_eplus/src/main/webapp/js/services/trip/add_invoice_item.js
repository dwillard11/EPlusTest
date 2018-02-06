app.controller('invoiceitemctrl', ['$scope','codeService', '$localStorage', 'toaster', '$http', '$modalInstance', 'mode', 'invoiceId', 'record', 'uniqueKey','valutils',
    function ($scope,codeService, $localStorage, toaster, $http, $modalInstance, mode, invoiceId, record, uniqueKey,valutils) {
        function checkVal(val) {
            if (val != null && '' != val && undefined != val) {
                return true;
            }
            return false;
        }
        $scope.chargeCodeOption = [];
        $scope.taxCodeOption = [];
        $scope.invoiceDescCodeOption = [];
        $scope.getEpCodeData = function (type) {
            if (checkVal(type)) {
                $http({
                    method: 'GET',
                    url: "operationconsole/operationconsole/getEpCodeListByType.do",
                    params: {
                        type: type,
                        footer: new Date().getTime()
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (type == "Charge Code") {
                            $scope.chargeCodeOption = response.data.records;
                            return;
                        }
                        if (type == "Tax Code") {
                            $scope.taxCodeOption = response.data.records;
                            return;
                        }
                        if (type == "Invoice Items") {
                            $scope.invoiceDescCodeOption = response.data.records;
                            return;
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }
        $scope.getEpCodeData("Charge Code");
        $scope.getEpCodeData("Invoice Items");
        $scope.getEpCodeData("Tax Code");
        if (mode == "add") {
            $scope.i = {
                invoiceId: invoiceId,
                chargeCode: "",
                item: "",
                amount: "",
                sequence: record.sequence,
                taxCode: "NOTAX",
                taxCodeValue: 0,
                taxAmount: "",
                total: ""
            }
        }
        if (mode == "edit") {
            $scope.uniqueKey = uniqueKey;
            $scope.i = {
                invoiceId: invoiceId,
                chargeCode: record.chargeCode,
                item: record.item,
                amount: record.amount,
                sequence: record.sequence,
                taxCode: record.taxCode,
                taxCodeValue: record.taxCodeValue,
                taxAmount: record.taxAmount,
                total: record.total
            }

        }

        $scope.findChargeDesc = function (chargeCode) {
            angular.forEach($scope.invoiceDescCodeOption, function (data) {
                if (data.id == chargeCode) {
                    $scope.i.item = data.name;
                }
            });
        };

        $scope.findTaxCodeValue = function (taxCode, taxCodeOptions) {
            angular.forEach(taxCodeOptions, function (data) {
                if (data.id == taxCode) {
                    $scope.i.taxCodeValue = data.name;
                }
            });
        };

        $scope.initializing = true;
        /*
        $scope.$watch('i.chargeCode', function () {
            if($scope.initializing) {
                $scope.initializing = false;
                return;
            } else {
                codeService.getEpCodeById($scope.i.chargeCode).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.i.item = res.records.remarks || "";
                });
            }

        });
        */
        $scope.findTaxCodeValue($scope.i.taxCode, $scope.taxCodeOption);
        $scope.submitted = false;
        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                if (valutils.isEmptyOrUndefined(document.getElementById("chargeDesc").value)) {
                    toaster.pop('error','','Charge Desc required!');
                    return;
                }
                var items = [];
                items.push({
                    "invoiceId": invoiceId,
                    "chargeCode": $scope.i.chargeCode,
                    "item": $scope.i.item,
                    "amount": $scope.i.amount,
                    "sequence": $scope.i.sequence,
                    "taxCode": $scope.i.taxCode,
                    "taxCodeValue": $scope.i.taxCodeValue,
                    "taxAmount": (Math.round($scope.i.taxCodeValue * $scope.i.amount * 100)/100).toFixed(2),
                    "total": (Math.round(($scope.i.amount * 100 + $scope.i.taxCodeValue * $scope.i.amount * 100))/100).toFixed(2),
                    "uniqueKey": $scope.uniqueKey
                });
                $modalInstance.close(items);
            }
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };


    }]);
