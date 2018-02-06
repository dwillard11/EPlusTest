app.controller('invoicedetailsctrl', ['$scope', '$window','$rootScope','$timeout', '$stateParams', '$filter', '$location', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', '$filter', '$log', function ($scope,$window,$rootScope, $timeout, $stateParams, $filter, $location, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $filter, $log) {

    // do not save to db and just for pdf
    $scope.temp = {
        pickupAt: ""
    }

    // get parameters from other page
    $scope.tripid = $stateParams.tripid;
    $scope.tripmode = $stateParams.tripmode;
    $scope.triptype = $stateParams.triptype;
    $scope.invoiceid = $stateParams.invoiceid;

    if (!$stateParams.tripid) {
        $scope.tripid = $window.sessionStorage["invoiceTripid"];
        $scope.triptype = $window.sessionStorage["invoiceTripType"];
        $scope.tripmode = $window.sessionStorage["invoiceTripMode"];
        $scope.invoiceid = $window.sessionStorage["invoiceId"];
    }
    $window.sessionStorage["invoiceTripid"] = $scope.tripid;
    $window.sessionStorage["invoiceTripType"] = $scope.triptype;
    $window.sessionStorage["invoiceTripMode"] = $scope.tripmode;
    $window.sessionStorage["invoiceId"] = $scope.invoiceid;

    function checkVal(val) {
        if (val != null && '' != val && undefined != val) {
            return true;
        }
        return false;
    }

    $scope.disableFlag = false;
    $scope.getEpCodeData = function (type) {
        if (type != "" && type != null) {
            $http({
                method: 'GET',
                url: "maintenance/quoteTemplate/getEpCodeListByType.do",
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
                    if (type == "Currency") {
                        // $scope.currency = response.data.records[0];
                        $scope.currencyOption = response.data.records;
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
    }
    $scope.getEpCodeData("Currency");
    $scope.calculateSummary = function (currencyCode, collections) {
        var details = JSON.stringify(collections);
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/calculateSummary.do",
            params: {
                currencyCode: currencyCode,
                details: details,
                foobar: new Date().getTime()
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.usdRate = response.data.usdRate;
                $scope.totalInUSD = response.data.totalInUSD;
                $scope.subtotal = response.data.subtotal;
                $scope.taxs = response.data.taxs;
                $scope.totalAmount = response.data.totalAmount;
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });

    }
    $scope.loadInvoice = function (tripid, invoiceid, tripmode, triptype) {
        $scope.invoice = "";
        $scope.billedClient = "";
        $scope.shipper = "";
        $scope.shipper.selected = "";
        $scope.billedClient.selected = "";
        $scope.isLoading = true;
        toaster.pop('wait', '', 'Loading...');
        var url = "operationconsole/operationconsole/loadTrip.do";
        if (tripmode == 'read') {
            url = "operationconsole/operationconsole/retrieveInvoice.do";
            $scope.disableFlag = true;
        }
        if (tripmode == 'edit') {
            url = "operationconsole/operationconsole/retrieveInvoice.do";
        }
        $http({
            method: 'POST',
            url: url,
            params: {
                "tripid": tripid,
                "tripmode": tripmode,
                "triptype": triptype,
                "invoiceid": invoiceid
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.invoice = response.data.records;
            $rootScope.title = "Invoice Details "+($scope.invoice.refNum || "");
            $scope.keepCurrency = response.data.keepCurrency;
            if($scope.invoice.invoiceDate) {
                $scope.invoice.invoiceDate = $filter('date')($scope.invoice.invoiceDate, 'yyyy-MM-dd');
            } else {
                $scope.invoice.invoiceDate = $filter('date')(new Date(), 'yyyy-MM-dd');
            }
            if($scope.invoice.pickupDate) {
                $scope.invoice.pickupDate = $filter('date')($scope.invoice.pickupDate, 'yyyy-MM-dd');
            } else {
                $scope.invoice.pickupDate = $filter('date')(new Date(), 'yyyy-MM-dd');
            }
            if($scope.invoice.deliveryDate) {
                $scope.invoice.deliveryDate = $filter('date')($scope.invoice.deliveryDate, 'yyyy-MM-dd');
            } else {
                $scope.invoice.deliveryDate = $filter('date')(new Date(), 'yyyy-MM-dd');
            }
            $scope.shippers = response.data.shippers;
            $scope.clients = response.data.clients;
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
                    customerName: response.data.selected_shipper.customerName || "",
                    postalCode: response.data.selected_shipper.postalCode || "",
                    genericInfo: response.data.selected_shipper.genericInfo || ""
                };
            }
            if (checkVal(response.data.selected_billed_client)) {
                $scope.billedClient = {
                    selected: response.data.selected_billed_client || "",
                    id: response.data.selected_billed_client.id || "",
                    code: response.data.selected_billed_client.code || "",
                    busPhone1: response.data.selected_billed_client.busPhone1 || "",
                    address1: response.data.selected_billed_client.address1 || "",
                    city: response.data.selected_billed_client.city || "",
                    country: response.data.selected_billed_client.country || "",
                    stateProvince: response.data.selected_billed_client.stateProvince || "",
                    customerName: response.data.selected_billed_client.customerName || "",
                    postalCode: response.data.selected_billed_client.postalCode || "",
                    notes: response.data.selected_billed_client.notes || "",
                    genericInfo: response.data.selected_billed_client.genericInfo || ""
                };
            }
            if (!checkVal($scope.invoice.billingCurrency)) {
                $scope.invoice.billingCurrency = $localStorage.user.defaultCurrency;
            }
            $scope.calculateSummary($scope.invoice.billingCurrency, $scope.invoice.details);
            $scope.isLoading = false;
        }, function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        })
    }
    $scope.loadInvoice($scope.tripid, $scope.invoiceid, $scope.tripmode, $scope.triptype);

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
    };
    $scope.selectLocation = function (location, type) {
        if (null != location && undefined != location && '' != location) {
            if ('billedClient' == type) {
                $scope.billedClient = {
                    selected: location,
                    id: location.id,
                    code: location.code,
                    busPhone1: location.busPhone1,
                    address1: location.address1,
                    city: location.city,
                    country: location.country,
                    stateProvince: location.stateProvince,
                    notes: location.notes,
                    customerName: location.customerName,
                    postalCode: location.postalCode,
                    genericInfo: location.genericInfo
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
                    customerName: location.customerName,
                    postalCode: location.postalCode,
                    genericInfo: location.genericInfo
                };
            }
        }
    };

    $scope.searchLocationContact = function (keyword, id, contactid, callback) {
        if ((!keyword || keyword.length == 0) && !loadClient && !(typeof(callback) == "function")) {
            return;
        }
        loadClient = true;
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

    $scope.getAvaliableSeq = function() {
        if ($scope.invoice && $scope.invoice.details) {
            return $scope.invoice.details.length + 2;
        } else {
            return 0;
        }
    }
    $scope.editDetails = function (invoiceId, record, mode) {
        var uniqueKey = "";
        if (mode == "edit") {
            // edit page should be a unique key
            uniqueKey = record.$$hashKey;
        }
        if (mode == "add") {
            record = {
                sequence:$scope.getAvaliableSeq()
            }
        }
        var modalInstance = $modal.open({
            templateUrl: 'invoiceitem.html',
            controller: 'invoiceitemctrl',
            size: 'lg',
            resolve: {
                mode: function () {
                    return mode;
                },
                invoiceId: function () {
                    return invoiceId;
                },
                record: function () {
                    return record;
                },
                uniqueKey: function () {
                    return uniqueKey;
                }
            }
        });

        modalInstance.result.then(function (result) {
            $scope.isLoading = true;
            if (mode == "add") {
                if (null != $scope.invoice.details && '' != $scope.invoice.details && undefined != $scope.invoice.details) {

                    $scope.invoice.details = $scope.invoice.details.concat(result)
                } else {
                    $scope.invoice.details = result;
                }
                $scope.isLoading = false;
            }
            if (mode == "edit") {
                if (null != $scope.invoice.details && '' != $scope.invoice.details && undefined != $scope.invoice.details) {
                    for (var i = 0; i < $scope.invoice.details.length; i++) {
                        if ($scope.invoice.details[i].$$hashKey == result[0].uniqueKey) {
                            $scope.invoice.details.splice(i, 1);
                            $scope.invoice.details = $scope.invoice.details.concat(result);
                            break;
                        }
                    }


                } else {
                    $scope.invoice.details = result;
                }
            }

            $scope.calculateSummary($scope.invoice.billingCurrency, $scope.invoice.details);
            $scope.isLoading = false;

        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    }
    $scope.removeItem = function (item) {
        if (confirm('Are you sure you want to delete this record?')) {
            $scope.isLoading = true;
            var index = $scope.invoice.details.indexOf(item);
            if (index !== -1) {
                $scope.invoice.details.splice(index, 1);
            }
            $scope.calculateSummary($scope.invoice.billingCurrency, $scope.invoice.details);
            $scope.isLoading = false;
            return;
        }
        return;
    }


    $scope.go = function (router, param) {
        $state.go(router);
    }

    $scope.getInvocePDF = function (invoiceid) {
        var protocol = $location.protocol();
        var host = $location.host();
        var port = $location.port();
        var url = protocol + "://" + host + ":" + port;
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/getInvoicePDF.do",
            params: {
                pdfURL: url,
                id: invoiceid,
                pickupAt: $scope.temp.pickupAt
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
    $scope.cancel = function () {
        $rootScope.back();
    };
    $scope.saveInvoice = function () {
        if (!checkVal($scope.invoice.billingCurrency)) {
            toaster.pop('error', '', 'billing Currency is empty!');
            return;
        }
        if (!checkVal($scope.invoice.details)) {
            toaster.pop('error', '', 'details list is empty!');
            return;
        }
        var details = JSON.stringify($scope.invoice.details);

        if (!checkVal($scope.invoice.invoiceDate)) {
            toaster.pop('error', '', 'invoice date is empty!');
            return;
        }

        if (!checkVal($scope.billedClient.id)) {
            toaster.pop('error', '', 'Invoice To is empty!');
            return;
        }
        if (!checkVal($scope.shipper.id)) {
            toaster.pop('error', '', 'Shipper is empty!');
            return;
        }
        toaster.pop('wait', '', 'Create invoice...');
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/saveInvoice.do",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: $.param({
                mode: $scope.tripmode,
                tripid: $scope.invoice.tripId,
                invoiceid: $scope.invoice.id,
                refNum: $scope.invoice.refNum,
                serviceType: $scope.invoice.serviceType,
                invoiceDate: $filter('date')($scope.invoice.invoiceDate, "yyyy-MM-dd"),
                invoiceFrom: $scope.invoice.invoiceFrom,
                invoiceTo: $scope.invoice.invoiceTo,
                pickupDate: $filter('date')($scope.invoice.pickupDate, "yyyy-MM-dd"),
                deliveryDate: $filter('date')($scope.invoice.deliveryDate, "yyyy-MM-dd"),
                shipperID: $scope.shipper.id,
                billedClientID: $scope.billedClient.id,
                authBy: $scope.invoice.authBy,
                authNo: $scope.invoice.authNo,
                miles: $scope.invoice.miles,
                rate: $scope.invoice.rate,
                totalPieces: $scope.invoice.totalPieces,
                actualWeight: $scope.invoice.actualWeight,
                chargeableWeight: $scope.invoice.chargeableWeight,
                billingCurrency: $scope.invoice.billingCurrency,
                details: details,
                subtotal: $scope.subtotal,
                totalAmount: $scope.totalAmount,
                pickupAt: $scope.temp.pickupAt
            })
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                toaster.pop('success', '', 'create successfully!');
                $scope.getInvocePDF(response.data.records);
                $rootScope.back();

            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }

    $scope.clearNoNum = function (obj, attr) {
        obj[attr] = obj[attr].replace(/[^\d.]/g, "");

        obj[attr] = obj[attr].replace(/^\./g, "");

        obj[attr] = obj[attr].replace(/\.{2,}/g, "");

        obj[attr] = obj[attr].replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
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