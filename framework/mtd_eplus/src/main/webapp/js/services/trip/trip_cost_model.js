app.controller('tripcostctrl', ['$scope', 'valutils', '$window', 'tripService', 'codeService', 'currencyService', '$state', '$localStorage', '$filter', 'toaster', '$http', '$modalInstance', 'mode', 'tripid', 'record', 'uniqueKey',
    function ($scope, valutils, $window, tripService, codeService, currencyService, $state, $localStorage, $filter, toaster, $http, $modalInstance, mode, tripid, record, uniqueKey) {
    codeService.getEpCodeData("Currency").then(function (res) {
        $scope.currencyOption = [];
        if (res.result != 'success') {
            toaster.pop('error', '', res.msg);
            return;
        }
        $scope.currencyOption = res.records;
    });
    codeService.getEpCodeData("Charge Code").then(function (res) {
        $scope.chargeCodeOption = [];
        if (res.result != 'success') {
            toaster.pop('error', '', res.msg);
            return;
        }
        $scope.chargeCodeOption = res.records;
    });
    tripService.retrieveEventsByTripId(tripid).then(function (res) {
        $scope.eventsOption = [];
        if (res.result != 'success') {
            toaster.pop('error', '', res.msg);
            return;
        }
        $scope.eventsOption = res.records;
    });
    $scope.visibleOption = [
        {
            id: "1",
            name: "Yes"
        },
        {
            id: "0",
            name: "No"
        }
    ];

    $scope.loadLocationContact = function (keyword, locationid, contactid, mode, callback) {
        $scope.contact = "";
        $scope.contact.selected = "";
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocationOrContacts.do",
            params: {
                "locationId": locationid,
                "contactId": contactid,
                "keyword": keyword
            }
        }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.locations = response.data.clients;
                if (typeof (callback) == "function")
                    callback(response.data.selected_location);

                if (response.data.selected_location) {
                    $scope.contact = {
                        selected: response.data.selected_location || "",
                        id: response.data.selected_location.keyId || "",
                        locationId: response.data.selected_location.id || "",
                        contactId: response.data.selected_location.contactId || "",
                        code: response.data.selected_location.code || "",
                        busPhone1: response.data.selected_location.busPhone1 || "",
                        address1: response.data.selected_location.address1 || "",
                        city: response.data.selected_location.city || "",
                        stateProvince: response.data.selected_location.stateProvince || "",
                        country: response.data.selected_location.country || "",
                        notes: response.data.selected_location.notes || "",
                        genericInfo: response.data.selected_location.genericInfo || "",
                        customerName: response.data.selected_location.customerName || "",
                        postalCode: response.data.selected_location.postalCode || "",
                        contactName: response.data.selected_location.contactName || ""
                    };
                }
                if (mode == 'add') {
                    $scope.contact.selected.genericInfo = "";
                }
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
    }

    if (mode == "add") {
        $scope.c = {
            tripid: tripid,
            chargeDesc: "",
            chargeCode: "",
            description: "",
            estCurrency: $localStorage.user.defaultCurrency,
            actCurrency: $localStorage.user.defaultCurrency,
            actCost: "",
            estDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            actDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            eventItem: "",
            eventId: "",
            actUsedCost: "",
            actUsedRate: "",
            visible: "1"
        }
        $scope.loadLocationContact("", "", "", "add");
    }
    if (mode == "edit") {
        $scope.uniqueKey = uniqueKey;
        var item = record;
        $scope.c = {
            tripid: tripid,
            chargeCode: item.chargeCode,
            chargeDesc: item.chargeDesc,
            estCost: item.estCost,
            estCurrency: item.estCurrency,
            actCost: item.actCost,
            estDate: $filter('date')(item.estDate, 'yyyy-MM-dd'),
            actDate: $filter('date')(item.actDate, 'yyyy-MM-dd'),
            actCurrency: item.actCurrency,
            actUsedCost: item.actUsedCost,
            actUsedRate: item.actUsedRate,
            description: item.description,
            eventItem: item.eventItem,
            eventId: item.eventId,
            visible: item.visible
        };
        $scope.loadLocationContact("", record.linkedEntity, record.linkedEntityContact, "edit");
    }
    if (valutils.isEmptyOrUndefined($scope.c.actCurrency)) {
        $scope.c.actCurrency = $localStorage.user.defaultCurrency;
    }
    $scope.getUSDRate = function (currency) {
        currencyService.getUSDRate(currency).then(function (res) {
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.c.actUsedRate = res.records;
            $scope.c.actUsedCost = $scope.c.actUsedRate * $scope.c.actCost;
        });
    }
    $scope.getUSDRate($scope.c.actCurrency);
    $scope.showContact = function (info) {
        if (!info || !info.selected) return;
        var param = {
            entityId: info.selected.customerId,
            selectId: info.selected.contactId + 1000000000,
            status: "from_edit"
        };
        var url = $state.href('app.system_maintenance.entity_edit', param);
        $window.open(url, '_blank');
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

    /*
    $scope.loadLocation = function (keyword, contactid, mode, callback) {
        $scope.contact = "";
        $scope.contact.selected = "";
        toaster.pop('wait', '', 'Loading...');
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocations.do",
            params: {
                "locationId": contactid,
                "keyword": keyword
            }
        }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.locations = response.data.clients;
                if (typeof (callback) == "function")
                    callback(response.data.selected_location);

                if (response.data.selected_location) {
                    $scope.contact = {
                        selected: response.data.selected_location || "",
                        id: response.data.selected_location.id || "",
                        code: response.data.selected_location.code || "",
                        busPhone1: response.data.selected_location.busPhone1 || "",
                        address1: response.data.selected_location.address1 || "",
                        city: response.data.selected_location.city || "",
                        stateProvince: response.data.selected_location.stateProvince || "",
                        country: response.data.selected_location.country || "",
                        notes: response.data.selected_location.notes || "",
                        genericInfo: response.data.selected_location.genericInfo || "",
                        customerName: response.data.selected_location.customerName || "",
                        postalCode: response.data.selected_location.postalCode || ""
                    };
                }
                if (mode == 'add') {
                    $scope.contact.selected.genericInfo = "";
                }
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
    }
    $scope.searchLocation = function (keyword, id, callback) {
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
                $scope.brokers = response.data.brokers;
                if (typeof(callback) == "function")
                    callback(response.data.selected_location);
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
    }
    $scope.selectLocation = function (location, type) {
        if (!valutils.isEmptyOrUndefined(location)) {
            if ('cost' == type) {
                $scope.contact = {
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
                    customerName: location.customerName,
                    postalCode: location.postalCode
                };
            }
        }
    };
    */

    $scope.searchLocationContact = function (keyword, id, contactid, callback) {
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocationOrContacts.do",
            params: {
                "locationId": id,
                "contactId": contactid,
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
    }

    $scope.selectLocationContact = function (location, type) {
        if (!valutils.isEmptyOrUndefined(location)) {
            if ('cost' == type) {
                $scope.contact = {
                    selected: location,
                    id: location.keyId,
                    locationId: location.id,
                    contactId: location.contactId,
                    code: location.code,
                    busPhone1: location.busPhone1,
                    address1: location.address1,
                    city: location.city,
                    country: location.country,
                    stateProvince: location.stateProvince,
                    notes: location.notes,
                    genericInfo: location.genericInfo,
                    customerName: location.customerName,
                    postalCode: location.postalCode,
                    contactName: location.contactName
                };
            }
        }
    };

    $scope.submitted = false;
    $scope.ok = function (isValid) {
        if (!isValid) {
            $scope.submitted = true;
            return;
        } else {
            if (!$scope.c.chargeCode) {
                toaster.pop('error', '', 'Charge code required!');
                return;
            }
            var costData = [];
            if (valutils.isEmptyOrUndefined($scope.c.actCost)) {
                $scope.c.actCurrency = "";
            }
            angular.forEach($scope.chargeCodeOption, function (data) {
                if (data.id == $scope.c.chargeCode) {
                    $scope.c.chargeDesc = data.name;
                }
            });
            var linkedEntity = "";
            var linkedEntityContact = "";
            if ($scope.contact) {
                linkedEntity = $scope.contact.locationId;
                linkedEntityContact = $scope.contact.contactId;
            }
            costData.push({
                "id": record.id || "",
                "tripid": tripid,
                "chargeCode": $scope.c.chargeCode || "",
                "chargeDesc": $scope.c.chargeDesc || "",
                "estCost": $scope.c.estCost || "",
                "estCurrency": $scope.c.estCurrency || "",
                "estDate": $filter('date')($scope.c.estDate, 'yyyy-MM-dd') || "",
                "actDate": $filter('date')($scope.c.actDate, 'yyyy-MM-dd') || "",
                "estDateStr": $filter('date')($scope.c.estDate, 'yyyy-MM-dd') || "",
                "actDateStr": $filter('date')($scope.c.actDate, 'yyyy-MM-dd') || "",
                "actCost": $scope.c.actCost || "",
                "actCurrency": $scope.c.actCurrency || "",
                "actUsedCost": $scope.c.actUsedCost || "",
                "actUsedRate": $scope.c.actUsedRate || "",
                "description": $scope.c.description || "",
                "eventItem": $scope.c.eventItem || "",
                "eventId": $scope.c.eventId || "",
                "visible": $scope.c.visible || "",
                "linkedEntityContact": linkedEntityContact,
                "linkedEntity": linkedEntity,
                "uniqueKey": $scope.uniqueKey
            });
            $modalInstance.close(costData);
        }
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);
