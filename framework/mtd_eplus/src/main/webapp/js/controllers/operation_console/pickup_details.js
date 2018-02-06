app.controller('pickupdetailsctrl', ['$scope', '$window', '$rootScope', 'valutils', '$timeout', '$stateParams', '$filter', '$location', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', '$filter', '$log', function ($scope, $window, $rootScope, valutils, $timeout, $stateParams, $filter, $location, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $filter, $log) {

    // do not save to db and just for pdf
    $scope.temp = {
        deliveryTo: "",
        pickupAt: "",
        deliveryProtectTime: ""
    }
    var loadClient = false;
    var loadBroker = false;

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

    $scope.item_ids = [];
    $scope.check = false;
    $scope.selectAllItems = function () {
        angular.forEach($scope.displayedCollection,
            function (item, key) {
                $scope.displayedCollection[key].selected = $scope.check;
            });

    };


    $scope.loadTrip = function (tripid, tripmode, triptype) {
        $scope.trip = "";

        $scope.consignee = "";
        $scope.shipper = "";
        $scope.broker = "";
        $scope.agent = "";

        $scope.consignee.selected = "";
        $scope.shipper.selected = "";
        $scope.broker.selected = "";
        $scope.agent.selected = "";

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
            $rootScope.title = "Pickup Details "+($scope.trip.tripRefNo || "");
            if ($scope.trip.pickupDate) {
                $scope.trip.pickupDate = $filter('date')($scope.trip.pickupDate, 'yyyy-MM-dd');
            } else {
                $scope.trip.pickupDate = $filter('date')(new Date(), 'yyyy-MM-dd');
            }
            if ($scope.trip.deliveryDate) {
                $scope.trip.deliveryDate = $filter('date')($scope.trip.deliveryDate, 'yyyy-MM-dd');
            } else {
                $scope.trip.deliveryDate = $filter('date')(new Date(), 'yyyy-MM-dd');
            }
            if ($scope.trip.chargedWeight == null && $scope.trip.freights && $scope.trip.freights.length > 0) {
                angular.forEach($scope.trip.freights, function (data) {
                    if (data.actualChargeWt != null) {
                        $scope.trip.chargedWeight += data.actualChargeWt;
                    }
                });
            }
            $scope.consignees = response.data.consignees;
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
            if (checkVal(response.data.selected_broker)) {
                $scope.broker = {
                    selected: response.data.selected_broker || "",
                    id: response.data.selected_broker.id || "",
                    code: response.data.selected_broker.code || "",
                    busPhone1: response.data.selected_broker.busPhone1 || "",
                    address1: response.data.selected_broker.address1 || "",
                    city: response.data.selected_broker.city || "",
                    country: response.data.selected_broker.country || "",
                    stateProvince: response.data.selected_broker.stateProvince || "",
                    notes: response.data.selected_broker.notes || "",
                    genericInfo: response.data.selected_broker.genericInfo || "",
                    customerName: response.data.selected_broker.customerName || "",
                    postalCode: response.data.selected_broker.postalCode || ""
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
            $scope.quoteTypeCode = $scope.trip.type;
            $scope.tripStatusCode = $scope.trip.status;
            $scope.dims = $scope.trip.dims||"";
            $scope.pickupInstruction = $scope.trip.pickupInstruction||"";
            $scope.loadingPage = false;

        }, function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        })
    }
    $scope.loadTrip($scope.tripid, $scope.tripmode, $scope.triptype);
    $scope.searchLocation = function (keyword, id, callback) {
        if ((!keyword || keyword.length == 0) && !loadClient) {
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
    $scope.searchBroker = function (keyword, id, callback) {
        if ((!keyword || keyword.length == 0) && !loadBroker) {
            return;
        }
        loadBroker = true;
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocations.do",
            params: {
                "locationId": id,
                "keyword": keyword,
                "type": "BROKER"
            }
        }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.brokers = response.data.brokers;
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
            } else if ('broker' == type) {
                $scope.broker = {
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
    };
    $scope.buildLocation = function (city, stateProvince, country) {

        var str = "";

        if (!valutils.isEmptyOrUndefined(city)) {
            str = city;
        }
        if (!valutils.isEmptyOrUndefined(stateProvince)) {
            if (valutils.isEmptyOrUndefined(str)) {
                str = stateProvince;
            } else {
                str = str + "," + stateProvince;
            }

        }
        if (!valutils.isEmptyOrUndefined(country)) {
            if (valutils.isEmptyOrUndefined(str)) {
                str = country;
            } else {
                str = str + "," + country;
            }
        }
        return str;
    };
    $scope.getPDF = function (tripid, quoteNum, tripNumber,
                              agentId, consigneeId, shipperId, brokerId,
                              pickupDate, deliveryDate, pickupAt, deliveryTo,
                              pieces, chargedWeight, wt, dims, pickupInstruction, costids, url) {
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/getPickupPDF.do",
            params: {
                "tripId": tripid,
                "quoteNum": quoteNum,
                "tripNumber": tripNumber,
                "agentId":agentId,
                "consigneeId": consigneeId,
                "shipperId": shipperId,
                "brokerId": brokerId,
                "pickupDate": pickupDate,
                "deliveryDate": deliveryDate,
                "pickupAt": pickupAt,
                "deliveryTo": deliveryTo,
                "pieces": pieces,
                "chargedWeight": chargedWeight,
                "wt": wt,
                "dims": dims,
                "pickupInstruction": pickupInstruction,
                "costids": costids,
                "url": url
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
        // window.location.href = "downloaddocument?tripid=" + $scope.tripid + "&filename=" + filename;
    }
    $scope.savePickup = function (mode) {
        $scope.costids = "";
        angular.forEach(
            $scope.displayedCollection,
            function (item, key) {
                if ($scope.displayedCollection[key].selected) {
                    $scope.costids += item.id + ",";
                }
            });
        var agentId = ""
        if (!valutils.isEmptyOrUndefined($scope.agent)) {
            agentId = $scope.agent.id
        }
        var shipperId = ""
        if (!valutils.isEmptyOrUndefined($scope.shipper)) {
            shipperId = $scope.shipper.id
        }
        var brokerId = ""
        if (!valutils.isEmptyOrUndefined($scope.broker)) {
            brokerId = $scope.broker.id
        }
        var consigneeId = ""
        if (!valutils.isEmptyOrUndefined($scope.consignee)) {
            consigneeId = $scope.consignee.id
        }
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/savePickup.do",
            params: {
                "tripID": $scope.trip.id,
                "agentId": agentId,
                "consigneeId": consigneeId,
                "shipperId": shipperId,
                "brokerId": brokerId,
                "pickupDate": $filter('date')($scope.trip.pickupDate, 'yyyy-MM-dd'),
                "deliveryDate": $filter('date')($scope.trip.deliveryDate, 'yyyy-MM-dd'),
                "chargedWeight": $scope.trip.chargedWeight,
                "podName": $scope.trip.podName||"",
                "pickupInstruction": $scope.trip.pickupInstruction||""
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

                if (checkVal($scope.costids)) {
                    $scope.costids = ($scope.costids.substring($scope.costids.length - 1) == ',') ? $scope.costids.substring(0, $scope.costids.length - 1) : $scope.costids;
                }

                $scope.getPDF($scope.trip.id,
                    $scope.trip.quoteRefNo,
                    $scope.trip.tripRefNo,
                    agentId,
                    consigneeId,
                    shipperId,
                    brokerId,
                    $filter('date')($scope.trip.pickupDate, 'yyyy-MM-dd'),
                    $filter('date')($scope.trip.deliveryDate, 'yyyy-MM-dd'),
                    $scope.temp.pickupAt||"",
                    $scope.temp.deliveryTo||"",
                    $scope.trip.totalPieces,
                    $scope.trip.chargedWeight,
                    $scope.trip.totalWeight,
                    $scope.trip.dims||"",
                    $scope.trip.pickupInstruction||"",
                    $scope.costids,
                    url);
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
        if (!info || !info.selected) return;
        var param = {
            entityId: info.selected.customerId,
            selectId: info.selected.id + 1000000,
            status: "from_edit"
        };
        var url = $state.href('app.system_maintenance.entity_edit', param);
        $window.open(url, '_blank');
    }

}]);