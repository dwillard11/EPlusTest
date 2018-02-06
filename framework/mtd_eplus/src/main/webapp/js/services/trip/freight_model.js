app.controller('tripfreightctrl', ['$scope', 'valutils', 'freightService', 'codeService', 'currencyService', '$localStorage', 'toaster', '$http', '$modalInstance', 'mode', 'tripid', 'record', 'uniqueKey', 'measureCode',
    function ($scope, valutils, freightService, codeService, currencyService, $localStorage, toaster, $http, $modalInstance, mode, tripid, record, uniqueKey, measureCode) {
        codeService.getEpCodeData("Currency").then(function (res) {
            $scope.currencyOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.currencyOption = res.records;
        });
        var sizeUnit = "";
        var weightUnit = "";
        if (measureCode == "M") {
            sizeUnit = "CM";
            weightUnit = "KG";
        } else {
            sizeUnit = "Inches";
            weightUnit = "LBS";
        }

        $scope.submitted = false;
        if (mode == "edit") {
            $scope.uniqueKey = uniqueKey;
        }
        if (mode == "add") {
            $scope.f = {
                tripid: tripid,
                item: "",
                description: "",
                estimatedUOM: weightUnit,
                estimatedCurrency: $localStorage.user.defaultCurrency,
                actualUOM: weightUnit,
                actualCurrency: $localStorage.user.defaultCurrency,
                estimatedChargeWt: "",
                actualChargeWt: "",
                bagtag: "",
                usdRate: "",
                lengthUOM: sizeUnit // not save to db / CM/Inches

            }
        }
        if (mode == "edit") {
            $scope.f = {
                tripid: tripid,
                item: record.item || "",
                description: record.description || "",
                estimatedDimension1: record.estimatedDimension ? freightService.splitDim(record.estimatedDimension, 0) : '',
                estimatedDimension2: record.estimatedDimension ? freightService.splitDim(record.estimatedDimension, 1) : '',
                estimatedDimension3: record.estimatedDimension ? freightService.splitDim(record.estimatedDimension, 2) : '',
                estimatedDimension: record.estimatedDimension,
                lengthUOM: sizeUnit,
                estimatedCurrency: record.estimatedCurrency,
                estimatedUOM: weightUnit,
                estimatedWeight: record.estimatedWeight,
                estimatedCost: record.estimatedCost,
                estimatedPieces: record.estimatedPieces,
                actualPieces: record.actualPieces,
                actualDimension1: record.actualDimension ? freightService.splitDim(record.actualDimension, 0) : '',
                actualDimension2: record.actualDimension ? freightService.splitDim(record.actualDimension, 1) : '',
                actualDimension3: record.actualDimension ? freightService.splitDim(record.actualDimension, 2) : '',
                actualDimension: record.actualDimension,
                actualUOM: weightUnit,
                actualWeight: record.actualWeight,
                actualCost: record.actualCost,
                actualCurrency: record.actualCurrency,
                usdCost: record.usdCost,
                usdRate: record.usdRate,
                bagtag: record.bagtag,
                actualChargeWt: record.actualChargeWt,
                estimatedChargeWt: record.estimatedChargeWt
            }
        }

        if (valutils.isEmptyOrUndefined($scope.f.actualCurrency))
            $scope.f.actualCurrency = $localStorage.user.defaultCurrency;

        $scope.getUSDRate = function (currency) {
            currencyService.getUSDRate(currency).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.f.usdRate = res.records;
                $scope.f.usdCost = $scope.f.usdRate * $scope.f.actualCost;
            });
        }
        $scope.getUSDRate($scope.f.actualCurrency);
        $scope.calChargeWt = function(tag) {
            if (tag == "act") {
                var actPieces = $scope.f.actualPieces?$scope.f.actualPieces:0.0;
                var thisWeight = freightService.calculateChargeableWt(actPieces, $scope.f.actualDimension1, $scope.f.actualDimension2, $scope.f.actualDimension3, $scope.f.lengthUOM);
                return thisWeight;
            }
            if (tag == "est") {
                var estPieces = $scope.f.estimatedPieces?$scope.f.estimatedPieces:0.0;
                var thisWeight = freightService.calculateChargeableWt(estPieces, $scope.f.estimatedDimension1, $scope.f.estimatedDimension2, $scope.f.estimatedDimension3, $scope.f.lengthUOM);
                return thisWeight;
            }
        }
        $scope.ok = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {

                var freightData = [];
                freightData.push({
                    "id": record.id || "",
                    "tripid": tripid,
                    "item": $scope.f.item || "",
                    "description": $scope.f.description || "",
                    "estimatedDimension": freightService.buildDim($scope.f.estimatedDimension1, $scope.f.estimatedDimension2, $scope.f.estimatedDimension3, $scope.f.lengthUOM),
                    "estimatedWeight": $scope.f.estimatedWeight || "",
                    "estimatedCost": $scope.f.estimatedCost || "",
                    "estimatedPieces": $scope.f.estimatedPieces || "",
                    "estimatedUOM": weightUnit,
                    "estimatedCurrency": $scope.f.estimatedCurrency || "",
                    "uniqueKey": $scope.uniqueKey || "",
                    "actualDimension": freightService.buildDim($scope.f.actualDimension1, $scope.f.actualDimension2, $scope.f.actualDimension3, $scope.f.lengthUOM),
                    "actualWeight": $scope.f.actualWeight || "",
                    "actualCost": $scope.f.actualCost || "",
                    "actualPieces": $scope.f.actualPieces || "",
                    "actualUOM": weightUnit,
                    "actualCurrency": $scope.f.actualCurrency || "",
                    "bagtag": $scope.f.bagtag || "",
                    "usdCost": $scope.f.usdCost || "",
                    "usdRate": $scope.f.usdRate || "",
                    "actualChargeWt":  $scope.calChargeWt('act'),
                    "estimatedChargeWt":  $scope.calChargeWt('est')
                });

                if ($scope.calChargeWt('est') >=1000000)
                {
                    toaster.pop('error', '', "Estimated Chargeable Weight exceeded maximum allowed value!");
                }else if ($scope.calChargeWt('act') >=1000000)
                {
                    toaster.pop('error', '', "Actual Chargeable Weight exceeded maximum allowed value!");
                }else{
                        $modalInstance.close(freightData);
                }
            }

        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);