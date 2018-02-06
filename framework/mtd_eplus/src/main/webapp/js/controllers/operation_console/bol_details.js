app.controller('boldetailsctrl', ['$scope','freightService','valutils','$window', '$rootScope','$timeout', '$stateParams', '$filter', '$location', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', '$filter', '$log',
    function ($scope, freightService,valutils,$window,$rootScope,$timeout, $stateParams, $filter, $location, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $filter, $log) {

    // do not save to db and just for pdf
    $scope.temp = {
        bolDate: $filter('date')(new Date(), "yyyy-MM-dd"),
        declaredValue: "",
        refNum: "",
        prepaid: "",
        collect: "",
        routingCarraier: "",
        transferPoint: ""
    };
    var loadClient = false;

    // get parameters from other page
    $scope.tripid = $stateParams.tripid;
    $scope.tripmode = $stateParams.tripmode; // add or edit
    $scope.triptype = $stateParams.triptype; // quote or shipper

    function checkVal(val) {
        if (val != null && '' != val && undefined != val) {
            return true;
        }
        return false;
    }
    $scope.getTotalChargeableWeight = function(freightList,unit) {
        if (valutils.isEmptyOrUndefined(freightList) || freightList.length ==0)
            return 0;

        var totalWeight = 0.0;
        angular.forEach(freightList, function (record) {
            var actualDimension1= record.actualDimension ? freightService.splitDim(record.actualDimension, 0) : '';
            var actualDimension2= record.actualDimension ? freightService.splitDim(record.actualDimension, 1) : '';
            var actualDimension3= record.actualDimension ? freightService.splitDim(record.actualDimension, 2) : '';
            var actPieces = record.actualPieces?record.actualPieces:0.0;
            var thisWeight = freightService.calculateChargeableWt(actPieces, actualDimension1, actualDimension2, actualDimension3, unit);
            totalWeight += (thisWeight);
        });
        return totalWeight;
    };
    $scope.loadTrip = function (tripid, tripmode, triptype) {
        $scope.trip = "";
        $scope.isLoading = true;
        $scope.consignee = "";
        $scope.shipper = "";
        $scope.agent = "";

        $scope.consignee.selected = "";
        $scope.shipper.selected = "";
        $scope.agent.selected = "";
        $scope.refId = "";

        toaster.pop('wait', '', 'Loading...');
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadTrip.do",
            params: {
                "tripid": tripid,
                "tripmode": tripmode,
                "triptype": triptype
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.trip = response.data.records;
            $scope.tripid = $scope.trip.id;
            $rootScope.title = "Bol Details "+($scope.trip.tripRefNo || "");
            $scope.refId = $scope.trip.refId;
            $scope.consignees = response.data.consignees;
            $scope.shippers = response.data.shippers;
            $scope.brokers = response.data.brokers;
            $scope.agents = response.data.agents;
            if (checkVal(response.data.selected_consignee)) {
                $scope.consignee = {
                    selected: response.data.selected_consignee || "",
                    id: response.data.selected_consignee.id || "",
                    code: response.data.selected_consignee.code || "",
                    busPhone1: response.data.selected_consignee.busPhone1 || "",
                    address1: response.data.selected_consignee.address1 || "",
                    city: response.data.selected_consignee.city || "",
                    country: response.data.selected_consignee.country || "",
                    stateProvince: response.data.selected_consignee.stateProvince || "",
                    notes: response.data.selected_consignee.notes || "",
                    genericInfo: response.data.selected_consignee.genericInfo || "",
                    customerName: response.data.selected_consignee.customerName || "",
                    postalCode: response.data.selected_consignee.postalCode || ""
                };
            }
            if (checkVal(response.data.selected_shipper)) {
                $scope.shipper = {
                    selected: response.data.selected_shipper || "",
                    id: response.data.selected_shipper.id || "",
                    code: response.data.selected_shipper.code || "",
                    busPhone1: response.data.selected_shipper.busPhone1 || "",
                    address1: response.data.selected_shipper.address1 || "",
                    city: response.data.selected_shipper.city || "",
                    country: response.data.selected_shipper.country || "",
                    stateProvince: response.data.selected_shipper.stateProvince || "",
                    notes: response.data.selected_shipper.notes || "",
                    genericInfo: response.data.selected_shipper.genericInfo || "",
                    customerName: response.data.selected_shipper.customerName || "",
                    postalCode: response.data.selected_shipper.postalCode || ""
                };
            }
            if (checkVal(response.data.selected_agent)) {
                $scope.agent = {
                    selected: response.data.selected_agent || "",
                    id: response.data.selected_agent.id || "",
                    code: response.data.selected_agent.code || "",
                    busPhone1: response.data.selected_agent.busPhone1 || "",
                    address1: response.data.selected_agent.address1 || "",
                    city: response.data.selected_agent.city || "",
                    country: response.data.selected_agent.country || "",
                    stateProvince: response.data.selected_agent.stateProvince || "",
                    notes: response.data.selected_agent.notes || "",
                    genericInfo: response.data.selected_agent.genericInfo || "",
                    customerName: response.data.selected_agent.customerName || "",
                    postalCode: response.data.selected_agent.postalCode || ""
                };
            }
            $scope.isLoading = false;
            $scope.showdims = false;
            if (checkVal($scope.trip.freights)) {
                if ($scope.trip.freights.length == 1) {
                    $scope.showdims = true;
                }
            }
            $scope.loadingPage = false;

        }, function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        })
    }
    $scope.loadTrip($scope.tripid, $scope.tripmode, $scope.triptype);
    $scope.searchLocation = function (keyword, id, callback) {
        if((!keyword || keyword.length == 0) && !loadClient){
            return;
        }
        loadClient = true;
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocations.do",
            params: {
                "locationId": id,
                "keyword": keyword
            }
        }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.clients = response.data.clients;
                if (typeof(callback) == "function")
                    callback(response.data.selected_location);
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
    };

    $scope.selectLocation = function (location, type) {
        if (null != location && undefined != location && '' != location) {
            if ('consignee' == type) {
                $scope.consignee = {
                    selected: location,
                    id: location.id,
                    code: location.code,
                    busPhone1: location.busPhone1,
                    address1: location.address1,
                    city: location.city,
                    country: location.country,
                    stateProvince: location.stateProvince,
                    notes: location.notes,
                    genericInfo: location.genericInfo,
                    customerName: location.customerName || "",
                    postalCode: location.postalCode || ""
                };
            } else if ('agent' == type) {
                $scope.agent = {
                    selected: location,
                    id: location.id,
                    code: location.code,
                    busPhone1: location.busPhone1,
                    address1: location.address1,
                    city: location.city,
                    country: location.country,
                    stateProvince: location.stateProvince,
                    notes: location.notes,
                    genericInfo: location.genericInfo,
                    customerName: location.customerName || "",
                    postalCode: location.postalCode || ""
                };
            } else if ('shipper' == type) {
                $scope.shipper = {
                    selected: location,
                    id: location.id,
                    code: location.code,
                    busPhone1: location.busPhone1,
                    address1: location.address1,
                    city: location.city,
                    country: location.country,
                    stateProvince: location.stateProvince,
                    notes: location.notes,
                    genericInfo: location.genericInfo,
                    customerName: location.customerName || "",
                    postalCode: location.postalCode || ""
                };
            }
        }
    };

    $scope.go = function (router, param) {
        $state.go(router);
    }
    $scope.getBolPDF = function (url,
                                 tripRefNo,
                                 refId,
                                 tripId,
                                 agentId,
                                 shipperId,
                                 consigneeId,
                                 totalPieces,
                                 totalWeight,
                                 dims,
                                 totalCubicFeet,
                                 poNumber,
                                 refNum,
                                 prepaid,
                                 collect,
                                 routingCarrier,
                                 transferPoint,
                                 bolDate,
                                 declaredValue,
                                 systemOfMeasure) {
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/getBolPDF.do",
            params: {
                "url": url,
                "tripRefNo": tripRefNo,
                "refId": refId,
                "tripId": tripId,
                "thirdPartId": agentId,
                "shipperId": shipperId,
                "consigineeId": consigneeId,
                "totalNoOfPieces": totalPieces,
                "totalWeight": totalWeight,
                "dims": dims,
                "totalCubicFeet": totalCubicFeet,
                "poNumber": poNumber,
                "refNum": refNum,
                "prepaid": prepaid,
                "collect": collect,
                "routingCarrier": routingCarrier,
                "transferPoint": transferPoint,
                "bolDate":bolDate,
                "declaredValue":declaredValue,
                "systemOfMeasure":systemOfMeasure

            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $window.open(response.data.records, '_blank');
                toaster.pop('success', '', "save succuessfully!");
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }
    $scope.saveBol = function (mode) {

        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/saveBol.do",
            params: {
                "tripID": $scope.trip.id,
                "authorizationNo": $scope.trip.authorizationNo
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                var protocol = $location.protocol();
                var host = $location.host();
                var port = $location.port();
                var url = protocol + "://" + host + ":" + port;

                if ($scope.showdims == false) {
                    $scope.trip.dims = "";
                }
                $scope.getBolPDF(url,
                    $scope.trip.tripRefNo,
                    $scope.refId,
                    $scope.trip.id,
                    $scope.agent.id,
                    $scope.shipper.id,
                    $scope.consignee.id,
                    $scope.trip.totalPieces,
                    $scope.trip.totalWeight,
                    $scope.trip.dims,
                    $scope.trip.totalCubicFeet,
                    $scope.trip.authorizationNo,
                    $scope.temp.refNum,
                    $scope.temp.prepaid,
                    $scope.temp.collect,
                    $scope.temp.routingCarraier,
                    $scope.temp.transferPoint,
                    $scope.temp.bolDate,
                    $scope.temp.declaredValue,
                    $scope.trip.systemOfMeasure);
                if (mode == 'save') {
                    $rootScope.back();
                    return;
                }
                $rootScope.back();

            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }

    $scope.preview = function () {
        $scope.savePickup();
    }

    $scope.showEntity = function (info) {
        if(!info || !info.selected) return;
        var param = {
            entityId: info.selected.customerId,
            selectId: info.selected.id + 1000000,
            status: "from_edit"
        };
        var url = $state.href('app.system_maintenance.entity_edit',param);
        $window.open(url,'_blank');
    }

}]);