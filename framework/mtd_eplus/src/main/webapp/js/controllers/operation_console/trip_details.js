app.controller('tripdetailsctrl', ['$scope', 'deptService','$loading', '$window', 'commService', '$rootScope', 'tripService', 'currencyService', 'contactService', 'freightService', 'codeService', 'valutils', '$timeout', '$stateParams', '$filter', '$location', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', '$log',
    function ($scope, deptService,$loading, $window, commService, $rootScope, tripService, currencyService, contactService, freightService, codeService, valutils, $timeout, $stateParams, $filter, $location, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $log) {
        // get parameters from other page
        $scope.tripid = $stateParams.tripid;
        $scope.triptype = $stateParams.triptype; // quote or trip
        $scope.dept = $stateParams.dept;
        if ($stateParams.tripmode == "event") {
            $scope.tripmode = "edit";
            $scope.activityEvent = true;
        } else {
            $scope.tripmode = $stateParams.tripmode; // add or edit or readonly
        }

        if (!$stateParams.tripid) {
            $scope.tripid = $window.sessionStorage["editTripid"];
            $scope.triptype = $window.sessionStorage["editTripType"];
            $scope.tripmode = $window.sessionStorage["editTripMode"];
            $scope.dept = $window.sessionStorage["editDeptId"];
        }
        $window.sessionStorage["editTripid"] = $scope.tripid;
        $window.sessionStorage["editTripType"] = $scope.triptype;
        $window.sessionStorage["editTripMode"] = $scope.tripmode;
        $window.sessionStorage["editDeptId"] = $scope.dept;

        // item number of one page in grid
        $scope.itemsByPage = 10;
        $scope.activeSaveButton = true;
        $scope.activityNote = false;
        $scope.activityCommCenter = false;
        $scope.activityEvent = false;
        $scope.showEditEventButton = true;
        $scope.showGrid = true;
        $scope.showTree = !$scope.showGrid;
        $scope.panelStyle = "col-md-7";
        var loadClient = false;
        var loadBroker = false;

        $scope.showDetail = function () {
            $scope.showEditEventButton = true;
            $scope.panelStyle = "col-md-7";

        }
        $scope.hideDetail = function () {
            $scope.showEditEventButton = false;
            $scope.panelStyle = "";
        }

        if ($scope.tripmode == 'read' || $scope.tripmode == "readnote") {
            $scope.activeSaveButton = false;
        }
        if ($scope.tripmode == "readnote") {
            $scope.activityNote = true;
        }
        if ($scope.tripmode == "focusnote") {
            $scope.focusNotes = true;
        }
        if ($scope.tripmode == "commcenter") {
            $scope.activityCommCenter = true;
        }

        // init drop down list
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
        codeService.getEpCodeData("Trip Type").then(function (res) {
            $scope.tripOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.tripOption = res.records;
            $scope.quoteTypeOption = res.records;
        });
        codeService.getEpCodeData("System Of Measure").then(function (res) {
            $scope.measureOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.measureOption = res.records;

        });
        $scope.changeTripStatus = function () {
            if ($scope.trip.status.toUpperCase() == "DEL" ||
                // $scope.trip.status.toUpperCase() == "OH" ||
                $scope.trip.status.toUpperCase() == "ARC" ||
                $scope.trip.status.toUpperCase() == "CLO"){
                $scope.trip.criticalTime = null;
            }
        };
        $scope.changeMeasure = function (tripid, measure) {
            tripService.updateTripMeasureSystem(tripid, measure).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.showFreights(tripid);
            });

        };
        $scope.getTotalChargeableWeight = function(freightList,unit) {
            if (valutils.isEmptyOrUndefined(freightList) || freightList.length ==0)
                return 0;
            var totalPieces = 0;
            var freightSummary = freightList.length;
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
        $scope.getTotalUsdCost = function(costList) {
            if (valutils.isEmptyOrUndefined(costList) || costList.length ==0)
                return 0;
            var totalUsdCost = 0.0;
            angular.forEach(costList, function (record) {
                var actUsedCost = record.actUsedCost?record.actUsedCost:0.0;
                totalUsdCost += (actUsedCost);
            });

            return totalUsdCost.toFixed(2);
        };
        $scope.getTotalQuoteCost = function() {
            var totalQuoteCost = 0.00;
            if ($scope.trip != undefined) {
                if ($scope.trip.quoteTotal)
                    totalQuoteCost = $scope.trip.quoteTotal.toFixed(2);

                if ($scope.trip.quoteCurrency)
                    totalQuoteCost = totalQuoteCost + " " + $scope.trip.quoteCurrency;
            }
            return totalQuoteCost;
        };
        codeService.getEpCodeData("Trip Status").then(function (res) {
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.tripStatusOption = res.records;
            if ($scope.triptype == "trip" && $scope.tripmode != "fromQuote") {
                // filter  Quote/Quote Cancelled/Will Call
                $scope.filteredStatus = [];
                angular.forEach($scope.tripStatusOption, function (cb, index) {
                    if (cb.id != 'QUO' && cb.id != 'QC') {
                        $scope.filteredStatus.push(cb);
                    }
                });

                $scope.tripStatusOption = $scope.filteredStatus;
            }
            if ($scope.tripmode == "fromQuote") {
                $scope.trip.status = 'QUO';
            }
        });
        codeService.getEpCodeData("Status").then(function (res) {
            $scope.statusOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.statusOption = res.records;
        });
        codeService.getEpCodeData("Event Class").then(function (res) {
            $scope.eventClassOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.eventClassOption = res.records;
        });
        codeService.getEpCodeData("Event Code").then(function (res) {
            $scope.eventCodeOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.eventCodeOption = res.records;
        });
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
        codeService.getEpCodeData("Yes Or No").then(function (res) {
            $scope.yesNoOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.yesNoOption = res.records;
        });
        deptService.getCurrentUserDepts().then(function (res) {
            $scope.departmentOptions = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.departmentOptions = res.records;
        });



        // Trip Editor tab
        $scope.copyToClient = function(type) {
            if (type == 'shipper') {
                $scope.client = {
                    selected: $scope.shipper || "",
                    id: $scope.shipper.id || "",
                    code: $scope.shipper.code || "",
                    busPhone1: $scope.shipper.busPhone1 || "",
                    address1: $scope.shipper.address1 || "",
                    city: $scope.shipper.city || "",
                    country: $scope.shipper.country || "",
                    stateProvince: $scope.shipper.stateProvince || "",
                    notes: $scope.shipper.notes || "",
                    billingCompany: $scope.shipper.billingCompany || "",
                    genericInfo: $scope.shipper.genericInfo || "",
                    customerName: $scope.shipper.customerName || "",
                    postalCode: $scope.shipper.postalCode || ""
                };
            }
            if (type == 'consignee') {
                $scope.client = {
                    selected: $scope.consignee || "",
                    id: $scope.consignee.id || "",
                    code: $scope.consignee.code || "",
                    busPhone1: $scope.consignee.busPhone1 || "",
                    address1: $scope.consignee.address1 || "",
                    city: $scope.consignee.city || "",
                    country: $scope.consignee.country || "",
                    stateProvince: $scope.consignee.stateProvince || "",
                    notes: $scope.consignee.notes || "",
                    billingCompany: $scope.consignee.billingCompany || "",
                    genericInfo: $scope.consignee.genericInfo || "",
                    customerName: $scope.consignee.customerName || "",
                    postalCode: $scope.consignee.postalCode || ""
                }
            }
        };
        $scope.loadTrip = function (tripid, tripmode, triptype) {
            $loading.start('sample-1');
            tripService.loadTrip(tripid, tripmode, triptype, 0).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                if (res.readonly) {
                    $scope.activeSaveButton = false;
                    toaster.pop('error', '', res.readMsg);
                }
                $scope.trip = res.records;
                $scope.tripid = $scope.trip.id;
                $scope.trip.departmentId = $scope.trip.departmentId+"";
                $rootScope.title = "Trip Details "+($scope.trip.tripRefNo || "");
                $scope.authorizationNo = $scope.trip.authorizationNo;
                $scope.authorizedBy = $scope.trip.authorizedBy;
                $scope.quoteRefNo = $scope.trip.quoteRefNo;
                $scope.systemOfMeasure = $scope.trip.systemOfMeasure || "M";
                $scope.consignees = res.consignees;
                $scope.shippers = res.shippers;
                $scope.clients = res.clients;
                $scope.brokers = res.brokers;
                $scope.locations = res.clients;
                if ($scope.trip.systemOfMeasure == "I") {
                    $scope.measureTag = "LBS";
                } else {
                    $scope.measureTag = "KG";
                }
                if (!valutils.isEmptyOrUndefined(res.selected_client)) {
                    $scope.client = {
                        selected: res.selected_client || "",
                        id: res.selected_client.id || "",
                        code: res.selected_client.code || "",
                        busPhone1: res.selected_client.busPhone1 || "",
                        address1: res.selected_client.address1 || "",
                        city: res.selected_client.city || "",
                        country: res.selected_client.country || "",
                        stateProvince: res.selected_client.stateProvince || "",
                        notes: res.selected_client.notes || "",
                        billingCompany: res.selected_client.billingCompany || "",
                        genericInfo: res.selected_client.genericInfo || "",
                        customerName: res.selected_client.customerName || "",
                        postalCode: res.selected_client.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_consignee)) {
                    $scope.consignee = {
                        selected: res.selected_consignee || "",
                        id: res.selected_consignee.id || "",
                        code: res.selected_consignee.code || "",
                        busPhone1: res.selected_consignee.busPhone1 || "",
                        address1: res.selected_consignee.address1 || "",
                        city: res.selected_consignee.city || "",
                        country: res.selected_consignee.country || "",
                        stateProvince: res.selected_consignee.stateProvince || "",
                        notes: res.selected_consignee.notes || "",
                        genericInfo: res.selected_consignee.genericInfo || "",
                        customerName: res.selected_consignee.customerName || "",
                        postalCode: res.selected_consignee.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_consignee2)) {
                    $scope.consignee2 = {
                        selected: res.selected_consignee2 || "",
                        id: res.selected_consignee2.id || "",
                        code: res.selected_consignee2.code || "",
                        busPhone1: res.selected_consignee2.busPhone1 || "",
                        address1: res.selected_consignee2.address1 || "",
                        city: res.selected_consignee2.city || "",
                        country: res.selected_consignee2.country || "",
                        stateProvince: res.selected_consignee2.stateProvince || "",
                        notes: res.selected_consignee2.notes || "",
                        genericInfo: res.selected_consignee2.genericInfo || "",
                        customerName: res.selected_consignee2.customerName || "",
                        postalCode: res.selected_consignee2.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_consignee3)) {
                    $scope.consignee3 = {
                        selected: res.selected_consignee3 || "",
                        id: res.selected_consignee3.id || "",
                        code: res.selected_consignee3.code || "",
                        busPhone1: res.selected_consignee3.busPhone1 || "",
                        address1: res.selected_consignee3.address1 || "",
                        city: res.selected_consignee3.city || "",
                        country: res.selected_consignee3.country || "",
                        stateProvince: res.selected_consignee3.stateProvince || "",
                        notes: res.selected_consignee3.notes || "",
                        genericInfo: res.selected_consignee3.genericInfo || "",
                        customerName: res.selected_consignee3.customerName || "",
                        postalCode: res.selected_consignee3.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_shipper)) {
                    $scope.shipper = {
                        selected: res.selected_shipper || "",
                        id: res.selected_shipper.id || "",
                        code: res.selected_shipper.code || "",
                        busPhone1: res.selected_shipper.busPhone1 || "",
                        address1: res.selected_shipper.address1 || "",
                        city: res.selected_shipper.city || "",
                        country: res.selected_shipper.country || "",
                        stateProvince: res.selected_shipper.stateProvince || "",
                        notes: res.selected_shipper.notes || "",
                        genericInfo: res.selected_shipper.genericInfo || "",
                        customerName: res.selected_shipper.customerName || "",
                        postalCode: res.selected_shipper.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_shipper2)) {
                    $scope.shipper2 = {
                        selected: res.selected_shipper2 || "",
                        id: res.selected_shipper2.id || "",
                        code: res.selected_shipper2.code || "",
                        busPhone1: res.selected_shipper2.busPhone1 || "",
                        address1: res.selected_shipper2.address1 || "",
                        city: res.selected_shipper2.city || "",
                        country: res.selected_shipper2.country || "",
                        stateProvince: res.selected_shipper2.stateProvince || "",
                        notes: res.selected_shipper2.notes || "",
                        genericInfo: res.selected_shipper2.genericInfo || "",
                        customerName: res.selected_shipper2.customerName || "",
                        postalCode: res.selected_shipper2.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_shipper3)) {
                    $scope.shipper3 = {
                        selected: res.selected_shipper3 || "",
                        id: res.selected_shipper3.id || "",
                        code: res.selected_shipper3.code || "",
                        busPhone1: res.selected_shipper3.busPhone1 || "",
                        address1: res.selected_shipper3.address1 || "",
                        city: res.selected_shipper3.city || "",
                        country: res.selected_shipper3.country || "",
                        stateProvince: res.selected_shipper3.stateProvince || "",
                        notes: res.selected_shipper3.notes || "",
                        genericInfo: res.selected_shipper3.genericInfo || "",
                        customerName: res.selected_shipper3.customerName || "",
                        postalCode: res.selected_shipper3.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_broker)) {
                    $scope.broker = {
                        selected: res.selected_broker || "",
                        id: res.selected_broker.id || "",
                        code: res.selected_broker.code || "",
                        busPhone1: res.selected_broker.busPhone1 || "",
                        address1: res.selected_broker.address1 || "",
                        city: res.selected_broker.city || "",
                        country: res.selected_broker.country || "",
                        stateProvince: res.selected_broker.stateProvince || "",
                        notes: res.selected_broker.notes || "",
                        genericInfo: res.selected_broker.genericInfo || "",
                        customerName: res.selected_broker.customerName || "",
                        postalCode: res.selected_broker.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_broker2)) {
                    $scope.broker2 = {
                        selected: res.selected_broker2 || "",
                        id: res.selected_broker2.id || "",
                        code: res.selected_broker2.code || "",
                        busPhone1: res.selected_broker2.busPhone1 || "",
                        address1: res.selected_broker2.address1 || "",
                        city: res.selected_broker2.city || "",
                        country: res.selected_broker2.country || "",
                        stateProvince: res.selected_broker2.stateProvince || "",
                        notes: res.selected_broker2.notes || "",
                        genericInfo: res.selected_broker2.genericInfo || "",
                        customerName: res.selected_broker2.customerName || "",
                        postalCode: res.selected_broker2.postalCode || ""
                    };
                }
                if (!valutils.isEmptyOrUndefined(res.selected_billed_client)) {
                    $scope.billedClient = {
                        selected: res.selected_billed_client || "",
                        id: res.selected_billed_client.id || "",
                        code: res.selected_billed_client.code || "",
                        busPhone1: res.selected_billed_client.busPhone1 || "",
                        address1: res.selected_billed_client.address1 || "",
                        city: res.selected_billed_client.city || "",
                        country: res.selected_billed_client.country || "",
                        stateProvince: res.selected_billed_client.stateProvince || "",
                        notes: res.selected_billed_client.notes || "",
                        genericInfo: res.selected_billed_client.genericInfo || "",
                        customerName: res.selected_billed_client.customerName || "",
                        postalCode: res.selected_billed_client.postalCode || ""
                    };
                }
                $scope.trip.criticalTime = $filter('date')($scope.trip.criticalTime, "yyyy-MM-dd HH:mm");
                $scope.trip.pickupDate = $scope.trip.pickupDate;
                $scope.trip.deliveryDate = $scope.trip.deliveryDate;
                $scope.trip.readyTime = $filter('date')($scope.trip.readyTime, "yyyy-MM-dd HH:mm");
                if ($scope.tripmode == "add") {
                    $scope.client.selected.genericInfo = "";
                    $scope.consignee.selected.genericInfo = "";
                    $scope.consignee2.selected.genericInfo = "";
                    $scope.consignee3.selected.genericInfo = "";
                    $scope.shipper2.selected.genericInfo = "";
                    $scope.shipper3.selected.genericInfo = "";
                    $scope.shipper.selected.genericInfo = "";
                    $scope.broker.selected.genericInfo = "";
                    $scope.broker2.selected.genericInfo = "";
                    $scope.billedClient.selected.genericInfo = "";
                }
                else {
                    $scope.freightList = $scope.trip.freights;
                    var data = freightService.calculateSummary($scope.freightList);
                    $scope.freightSummary = data.freightSummary;
                    $scope.totalPieces = data.totalPieces;
                    $scope.totalWeight = data.totalWeight;
                    $scope.costList = $scope.trip.costs;
                }
                if ($scope.tripmode == "edit" || $scope.tripmode == "fromQuote" || $scope.tripmode == "read") {
                    $scope.loadTripEvents(tripid,0,true);
                }
                if ($scope.tripmode == "readnote") {
                    $scope.showNotes(tripid);
                }
                if ($scope.tripmode == "commcenter") {
                    $scope.showTripEmails(tripid);
                }
                if (!valutils.isEmptyOrUndefined($state.scopePrevious)) {
                    if ($state.scopePrevious.name == "app.operation_console.bol_details") {
                        $scope.activityDoc = true;
                        $scope.activityEvent = false;
                        $scope.showDocs($scope.tripid);
                    }
                    else if ($state.scopePrevious.name == "app.operation_console.pickup_details") {
                        $scope.activityDoc = true;
                        $scope.showDocs($scope.tripid);
                    }
                    else if ($state.scopePrevious.name == "app.operation_console.quote_builder") {
                        $scope.activityDoc = true;
                        $scope.showDocs($scope.tripid);
                    } else if ($state.scopePrevious.name == "app.operation_console.invoice_details") {
                       
                        $scope.activityInvoice = true;
                        $loading.start('sample-8');
                        $scope.showInvoices($scope.tripid);
                        $loading.finish('sample-8');
                    } else {
                        if ($scope.tripmode == "edit" && $scope.triptype == "trip" && res.showEvents) {
                            $scope.activityEvent = true;
                        }
                    }
                }
                $loading.finish('sample-1');
            });
        }
        $scope.loadTrip($scope.tripid, $scope.tripmode, $scope.triptype);
        $scope.showFreights = function (tripid) {
            $scope.isLoading = true;
            tripService.loadFreights(tripid).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.freightList = res.records;
                var data = freightService.calculateSummary($scope.freightList);
                $scope.freightSummary = data.freightSummary;
                $scope.totalPieces = data.totalPieces;
                $scope.totalWeight = data.totalWeight;
                $scope.isLoading = false;
            });
        }
        $scope.handleFreight = function (tripid, record, mode, measureCode) {
            tripid = $scope.tripid;
            var uniqueKey = "";
            if (mode == "edit") {
                // edit page should be a unique key
                uniqueKey = record.$$hashKey;
            }
            if (mode == "add") {
                record = "";
            }
            var modalInstance = $modal.open({
                templateUrl: 'tripFreight.html',
                controller: 'tripfreightctrl',
                size: 'lg',
                resolve: {
                    mode: function () {
                        return mode;
                    },
                    tripid: function () {
                        return tripid;
                    },
                    record: function () {
                        return record;
                    },
                    uniqueKey: function () {
                        return uniqueKey;
                    },
                    measureCode: function () {
                        return measureCode;
                    }
                }
            });

            modalInstance.result.then(function (result) {
                if (mode == "add") {
                    result[0].tripId = tripid;
                    tripService.createFreight(result[0]).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showFreights($scope.tripid);
                        }
                    });
                }
                if (mode == "edit") {
                    for (var i = 0; i < $scope.freightList.length; i++) {
                        if ($scope.freightList[i].$$hashKey == result[0].uniqueKey) {
                            result[0].id = $scope.freightList[i].id;
                            result[0].tripId = tripid;
                            tripService.updateFreight(result[0]).then(function (res) {
                                if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                                    $scope.showFreights($scope.tripid);
                                }
                            });
                            break;
                        }
                    }
                }

                // $scope.showFreights(tripid);
                // $scope.changeMeasure($scope.tripid,$scope.trip.systemOfMeasure);
                $scope.isLoading = false;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        $scope.removeFreight = function (freight) {
            if (confirm('Are you sure you want to delete this record?')) {
                $scope.isLoading = true;
                var index = $scope.freightList.indexOf(freight);
                if (index !== -1) {
                    tripService.removeFreight(freight.id).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showFreights($scope.tripid);
                        }
                    });
                }

                $scope.isLoading = false;
                return;
            }
            return;

        }
        // cost tab
        $scope.showCosts = function (tripid) {
            $scope.loadingCosts = true;
            $scope.startLoading('sample-3');
            tripService.loadCosts(tripid).then(function (res) {

                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.costList = res.records;
                $scope.loadingCosts = false;
                $scope.finishLoading('sample-3');
            });
        }
        $scope.handleCost = function (tripid, record, mode) {
            var uniqueKey = "";
            if (mode == "edit") {
                // edit page should be a unique key
                uniqueKey = record.$$hashKey;
            }
            if (mode == "add") {
                record = "";
            }
            var modalInstance = $modal.open({
                templateUrl: 'tripCost.html',
                controller: 'tripcostctrl',
                size: 'lg',
                resolve: {
                    mode: function () {
                        return mode;
                    },
                    tripid: function () {
                        return tripid;
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
                if (valutils.isEmptyOrUndefined($scope.costList)) {
                    $scope.costList = result;
                    result[0].tripId = tripid;
                    tripService.createTripCost(result[0]).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showCosts($scope.tripid);
                        }
                    });
                } else {
                    if (mode == "add") {
                        $scope.costList = $scope.costList.concat(result);
                        result[0].tripId = tripid;
                        tripService.createTripCost(result[0]).then(function (res) {
                            if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                                $scope.showCosts($scope.tripid);
                            }
                        });
                    }
                    if (mode == "edit") {
                        for (var i = 0; i < $scope.costList.length; i++) {
                            if ($scope.costList[i].$$hashKey == result[0].uniqueKey) {
                                result[0].id = $scope.costList[i].id;
                                result[0].tripId = tripid;
                                tripService.updateTripCost(result[0]).then(function (res) {
                                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                                        $scope.showCosts($scope.tripid);
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
                $scope.loadingCosts = false;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }
        $scope.removeCost = function (cost) {
            if (confirm('Are you sure you want to delete this record?')) {
                $scope.loadingCosts = true;
                var index = $scope.costList.indexOf(cost);
                if (index !== -1) {
                    tripService.removeTripCost(cost.id).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showCosts($scope.tripid);
                        }
                    });
                }
                $scope.loadingCosts = false;
                return;
            }
            return;

        };
        // Doc tab
        $scope.showDocs = function (tripid) {
            $scope.activityDoc = true;
            $scope.loadingDocs = true;
            $scope.startLoading('sample-6');
            tripService.loadDocsByType(tripid, 'NoEmail').then(function (res) {

                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.doclist = res.records;
                $scope.loadingDocs = false;
                $scope.finishLoading('sample-6');
            });

        }

        $scope.showBOL = function (tripid, mode, type) {
            $scope.activityDoc = true;
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.bol_details", param);
        }
        $scope.showPickup = function (tripid, mode, type) {
            $scope.activityDoc = true;
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.pickup_details", param);
        }
        $scope.btnQuotePDF = function (tripid) {
            $scope.activityDoc = true;
            var param = {
                quoteid: tripid
            };
            $state.go("app.operation_console.quote_builder", param);
        };
        $scope.printTripPDF = function (tripid) {
            toaster.pop('wait', '', 'start to print trip pdf');
            var protocol = $location.protocol();
            var host = $location.host();
            var port = $location.port();
            var url = protocol + "://" + host + ":" + port;
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/printTripPDF.do",
                params: {
                    pdfURL: url,
                    id: tripid
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $window.open(response.data.records, '_blank');
                    toaster.pop('success', '', "save successfully!");
                    $scope.showDocs(tripid);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });

        }
        $scope.uploadDocs = function (tripid) {
            var uniqueKey = "";
            var record = "";
            var modalInstance = $modal.open({
                templateUrl: 'uploaddoc.html',
                controller: 'uploadctrl',
                size: 'lg',
                resolve: {
                    tripid: function () {
                        return tripid;
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
                if (result) {
                    $scope.showDocs($scope.tripid);
                }
                $scope.loadingdocs = false;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.deleteDocument = function (id, type) {
            if (confirm('Are you sure you want to delete this record?')) {
                tripService.deleteDocument(id, type).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.showDocs($scope.tripid);
                });
            }
            return;

        };
        // note tab
        $scope.showNotes = function (tripid) {
            $scope.loadingNotes = true;
            $scope.startLoading('sample-7');
            tripService.loadNotes(tripid).then(function (res) {

                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.noteList = res.records;
                $scope.loadingNotes = false;
                $scope.noteslength = $scope.noteList.length;
                $scope.finishLoading('sample-7');
            });
        };
        $scope.handleNote = function (tripid, record, mode) {
            var uniqueKey = "";
            if (mode == "edit") {
                // edit page should be a unique key
                uniqueKey = record.$$hashKey;
            }
            if (mode == "add") {
                record = "";
            }
            var modalInstance = $modal.open({
                templateUrl: 'noteContent.html',
                controller: 'NoteInsCtrl',
                size: 'lg',
                resolve: {
                    mode: function () {
                        return mode;
                    },
                    tripid: function () {
                        return tripid;
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
                if (result) {
                    $scope.showNotes(tripid);
                }
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        $scope.removeNote = function (tripid, id) {
            $scope.isLoading = true;
            if(id){
                $http({
                    method: 'GET',
                    url: "operationconsole/operationconsole/deleteNote.do",
                    params: {
                        "id": id
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        $scope.showNotes(tripid);
                        $scope.isLoading = false;
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                        $scope.isLoading = false;
                    });
            }
        };

        // Communication center tab
        $scope.showTripEmails = function (tripId) {
            $scope.loadingEmails = true;
            $scope.startLoading('sample-5');
            commService.getEmailsByTrip(tripId).then(function (res) {

                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.tripEmails = res.records;
                $.each($scope.tripEmails,function (index,item) {
                    item.mailTo = item.mailTo.replace(/,/g,", ");
                    item.mailTo = item.mailTo.replace(/;/g,", ");
                })
                $scope.loadingEmails = false;
                $scope.emailLength = $scope.tripEmails.length;
                $scope.finishLoading('sample-5');
            });
        };
        $scope.searchEmails = function (tripid, dateFrom, dateTo, searchContent, label, includeDelete) {
            $scope.loadingEmails = true;
            commService.searchEmails(tripid, dateFrom, dateTo, searchContent, label, includeDelete).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.tripEmails = res.records;
                $.each($scope.tripEmails,function (index,item) {
                    item.mailTo = item.mailTo.replace(/,/g,", ");
                    item.mailTo = item.mailTo.replace(/;/g,", ");
                })
                $scope.loadingEmails = false;
                $scope.emailLength = $scope.tripEmails.length;
            });
        };
        $scope.markEmail = function (id, tag) {
            commService.markEmail(id, tag).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.showTripEmails($scope.tripid);
            })
        };
        $scope.showCopyPanel = function (doc) {
            var modalInstance = $modal.open({
                templateUrl: 'copyDocPanel.html',
                controller: 'copyDocCtrl',
                size: 'lg',
                resolve: {
                    doc: function () {
                        return doc;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                $scope.showTripEmails($scope.tripid);
            }, function () {
                $scope.showTripEmails($scope.tripid);
            });
        };
        codeService.getEpCodeData("Email Label").then(function (res) {
            $scope.searchLabelOptions = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.searchLabelOptions = res.records;
            $scope.searchLabelOptions.splice(0,0,{id:"",name:""});
        });
        $scope.markLabel = function(id, label) {
            commService.markLabel(id, label).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error','',res.msg);
                    return;
                }
                $scope.showTripEmails($scope.tripid);
            })
        };
        $scope.updateLink = function (emailId, tripId, tag) {
            commService.updateLink(emailId, tripId, tag).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.showTripEmails($scope.tripid);
            });
        };
        $scope.removeEmail = function (emailId) {
            if (confirm('Are you sure you want to delete this record?')) {
                commService.removeEmail(emailId).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.showTripEmails($scope.tripid);
                });
            }
        };
        $scope.undeleteEmail = function (emailId) {
            if (confirm('Are you sure you want to undelete this record?')) {
                commService.unremoveEmail(emailId).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.showTripEmails($scope.tripid);
                });
            }
        };
        $scope.sendEmail = function (mail, tag) {
            if (tag == 'compose') {
                mail = {
                    tripId: $scope.tripid || "",
                    subject: "[" + $scope.trip.tripRefNo + "]",
                    content: commService.buildSignare(),
                    mailFrom: "",
                    mailTo: "",
                    mailCc: "",
                    mailBcc: "",
                    departmentId: $scope.dept
                }
            } else {
                if (tag == 'edit') {
                    mail.subject = mail.subject.replace(/\[.*?\]/g, "[" + $scope.trip.tripRefNo + "]");
                    if (mail.subject.indexOf($scope.trip.tripRefNo) == -1) {
                        mail.subject += " [" + $scope.trip.tripRefNo + "]";
                    }
                } else {
                    $scope.markEmail(mail.id, 'read');
                    if (tag == 'reply' || tag == 'replyAll') {
                        mail.subject = mail.subject.replace(/\[.*?\]/g, "[" + $scope.trip.tripRefNo + "]");
                        if (mail.subject.indexOf($scope.trip.tripRefNo) == -1) {
                            mail.subject += " [" + $scope.trip.tripRefNo + "]";
                        }
                    }
                }
            }

            var modalInstance = $modal.open({
                templateUrl: 'editTripEmail.html',
                controller: 'editTripEmailCtrl',
                size: 'pg',
                resolve: {
                    mail: function () {
                        return mail;
                    },
                    tag: function () {
                        return tag;
                    },
                    referenceNum: function () {
                        return "[" + $scope.trip.tripRefNo + "]";
                    }
                }
            });
            modalInstance.result.then(function (result) {
                $scope.showTripEmails($scope.tripid);
            }, function () {
            });
        };
        $scope.viewEmail = function (emailId, canReply) {
            $scope.markEmail(emailId, 'read');
            var modalInstance = $modal.open({
                templateUrl: 'viewEmail.html',
                controller: 'viewEmailCtrl',
                size: 'pg',
                resolve: {
                    emailId: function () {
                        return emailId;
                    },
                    canReply: function () {
                        return canReply;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                $scope.showTripEmails($scope.tripid);

            }, function () {
                $scope.showTripEmails($scope.tripid);
            });
        };
        $scope.selectStyle = function (item) {
            var style ={};
            if(item){
                if(typeof(item) == "object"){
                    style= {
                        "background-color": item.backColor,
                        "color": item.fontColor
                    }
                }else if(typeof(item) == "string" && item.length > 0){
                    if($scope.searchLabelOptions){
                        $.each($scope.searchLabelOptions,function (i,o) {
                            if(o.id == item){
                                style  = {
                                    "background-color": o.backColor,
                                    "color": o.fontColor
                                }
                                return false;
                            }
                        })
                    }
                }
            }
            return style;
        };
        // invoice tab
        $scope.showInvoices = function (tripid) {
            $scope.loadingInvoice = true;
            $loading.start('sample-8');
            tripService.loadInvoices(tripid).then(function (res) {

                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.invoiceList = res.records;
                $scope.invoicelength = $scope.invoiceList.length;
                $scope.loadingInvoice = false;
                $loading.finish('sample-8');
            });
        };
        if (!valutils.isEmptyOrUndefined($state.scopePrevious)) {
            if ($state.scopePrevious.name == "app.operation_console.invoice_details") {
                $scope.showInvoices($scope.tripid);
                $scope.activityInvoice = true;
            } else {
                $scope.activityInvoice = false;
            }
        }
        $scope.showInvoice = function (tripid, invoiceid, mode, type) {
            var param = {
                tripid: tripid,
                invoiceid: invoiceid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.invoice_details", param);
        };
        // event tab->details
        $scope.eventTree = "";
        $scope.initEvent = function () {
            $scope.event = {
                id: 0,
                status: "",
                type: "",
                sequence: "",
                item: "",
                description: "",
                currentCompany: "",
                updatedBy: "",
                currentCustomer: "",
                updateTime: "",

                code: "",
                estimatedDate: "",
                actualDate: "",
                eventCosts: "",
                AddressList: "",
                customerNotify: "1"
            }
        }
        $scope.initEvent();
        $scope.nodesOnClick = function (event, treeId, treeNode, clickFlag) {
            if (treeNode != null) {
                $scope.getTripEventDetails(treeNode.entityId, true);
            }
        };
        $scope.setting = {
            view: {
                showLine: true,
                selectedMulti: false,
                dblClickExpand: false
            },
            data: {
                key: {
                    title: "t",
                    name: "name"
                },
                simpleData: {
                    enable: true,
                    idKey: "id",
                    pIdKey: "pId",
                }
            },
            callback: {
                onClick: $scope.nodesOnClick
            }
        };
        var treeModal = $("#treeModal"), originalTreeObj = null;
        treeModal.modal({"backdrop": "static", keyboard: true, show: false}).on('hidden.bs.modal', function (e) {
        });
        $scope.loadTripEvents = function (tripid, selectId, hideDetail) {
            $scope.isLoadingTripEvent = true;
            $scope.startLoading('sample-2');
            if(hideDetail)
                $scope.hideDetail();

            tripService.loadTripEvents($scope.tripid).then(function (res) {
                $scope.isLoadingTripEvent = false;
                $scope.finishLoading('sample-2');
                $scope.eventList = res;
                if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {

                    $scope.eventTree = res.export;
                    if ($scope.eventTree) {
                        angular.forEach(
                            $scope.eventTree,
                            function (item, key) {
                                $scope.eventTree[key].hasChecked = false;
                            });
                    }
                    $scope.isLoadingTripEvent = false;
                    var treeData = $.map(res.records,
                        function (item) {
                            if (item.type == "event") {
                                item.childOuter = false;
                                item.drag = false;
                            } else if (item.type == "category") {
                                item.childOuter = false;
                            } else {
                                if (item.IsComplete == "Y")
                                    item.iconSkin = "Check";
                                if (!selectId)
                                    selectId = item.id;
                            }
                            return item;
                        });
                    $.fn.zTree.init($("#dataTree"), settingTree, treeData);
                    var treeObj = $.fn.zTree.getZTreeObj("dataTree");
                    var curTreeNode = treeObj.getNodes()[0];
                    if (selectId) {
                        curTreeNode = treeObj.getNodeByParam("id", selectId);
                    }
                    treeObj.selectNode(curTreeNode);
                    if (curTreeNode != null) {
                        $scope.getTripEventDetails(curTreeNode.entityId);
                    }
                }
            });
        }

        $scope.switchTo = function (tag) {
            $scope.loadTripEvents($scope.tripid);
            if (tag == 'tree') {
                $scope.showTree = true;
                $scope.showGrid = false;
            }
            if (tag == 'grid') {
                $scope.showTree = false;
                $scope.showGrid = true;
            }
        }
        $scope.showDuplicateButton = true;
        $scope.getTripEventDetails = function (eventId, showDetail) {
            $scope.showDuplicateButton = true;
            if(showDetail)
                $scope.showDetail();
            $scope.initEvent();
            if (!valutils.isEmptyOrUndefined(eventId)) {
                tripService.loadTripEventById(eventId).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }

                    $scope.event.id = eventId;
                    $scope.event.eventCosts = res.costs;

                    $scope.loadLocationContact("", res.records.linkedEntity, res.records.linkedEntityContact, "edit", "event");
                    $scope.event.AddressList = res.notifies;
                    $scope.loadingEventCosts = false;
                    $scope.event.type = res.records.type;
                    $scope.event.status = res.records.status;
                    $scope.event.name = res.records.name;
                    $scope.event.category = res.records.category;
                    $scope.event.sequence = res.records.sequence;
                    $scope.event.categorySequence = res.records.categorySequence;
                    $scope.event.item = res.records.item;
                    $scope.event.description = res.records.description;
                    $scope.event.podDate = $filter('date')(res.records.actualDate, "yyyy-MM-dd HH:mm");
                    $scope.event.podName = res.records.podName;
                    $scope.event.eventClass = res.records.eventClass;
                    $scope.event.customerNotify = res.records.customerNotify;
                    $scope.event.actualDate = $filter('date')(res.records.actualDate, "yyyy-MM-dd HH:mm");
                    $scope.event.estimatedDate = $filter('date')(res.records.estimatedDate, "yyyy-MM-dd HH:mm");
                    $scope.event.currentCompany = res.records.currentCompany;
                    $scope.event.updatedBy = res.records.updatedBy;
                    $scope.event.currentCustomer = res.records.currentCustomer;
                    $scope.event.code = res.records.code;
                    $scope.event.updateTime = $filter('date')(res.records.updateTime, 'yyyy-MM-dd HH:mm:ss');
                });
            }
        }

        $scope.loadEventTemplateByQuoteType = function (quoteType) {
            if (valutils.isEmptyOrUndefined(quoteType)) {
                toaster.pop('error', '', 'Please specify Trip Type!');
                return;
            }
            tripService.loadEventTemplateByQuoteType(quoteType).then(function (res) {
                if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                    $.fn.zTree.init($("#originalTree"), {
                        view: {selectedMulti: false},
                        check: {enable: true},
                        data: {simpleData: {enable: true}},
                        callback: {
                            onCheck: function (event, treeId, treeNode, clickFlag) {
                            }
                        }
                    }, res.records);
                    originalTreeObj = $.fn.zTree.getZTreeObj("originalTree");
                    treeModal.modal("show");
                }
            })
        }
        $scope.resetTripEventTree = function () {
            if (!originalTreeObj) return;
            var checkNodes = originalTreeObj.getCheckedNodes(true);
            var entityIds = $.map(checkNodes, function (item) {
                var entityId = null;
                if (item.type == "item") {
                    entityId = item.entityId;
                }
                return entityId;
            });
            var checkCount = entityIds.length;
            if (checkCount == 0) {
                toaster.pop('warning', '', "Please select at least one record.");
                return;
            }
            if (confirm('All Event will be replaced by the selected template, are you sure to continue?')) {
                tripService.resetTripEventTree($scope.tripid, entityIds.join(",")).then(function (res) {
                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        //$scope.showDetail();
                        //if (res.export.length == 0) {
                        //    // no events
                        //    $scope.showDetail();
                        //    return;
                        //}
                        $scope.eventTree = res.export;
                        var treeData = $.map(res.records,
                            function (item) {
                                if (item.type == "event") {
                                    item.childOuter = false;
                                    item.drag = false;
                                } else if (item.type == "category") {
                                    item.childOuter = false;
                                } else {
                                }
                                return item;
                            });
                        $.fn.zTree.init($("#dataTree"), settingTree, treeData);
                        zTree_Menu = $.fn.zTree.getZTreeObj("dataTree");
                        curMenu = zTree_Menu.getNodes()[0];
                        zTree_Menu.selectNode(curMenu);
                        var a = $("#" + zTree_Menu.getNodes()[0].tId + "_a");
                        a.addClass("cur");
                        $scope.getTripEventDetails(curMenu.entityId);
                    }
                    treeModal.modal("hide");
                    return;
                })
            } else {
                treeModal.modal("hide");
                return;
            }
        };
        $scope.addTripEvent = function () {
            $scope.showDetail();
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();

            if (valutils.isEmptyOrUndefined(nodes) || valutils.isEmptyOrUndefined(nodes[0]) || valutils.isEmptyOrUndefined(nodes[0].entityId)) {
                $scope.event.category = "New Category_" + $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
                $scope.event.name = "New Event_" + $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
                $scope.event.type = $scope.trip.type;
                $scope.event.item = "New Item_" + $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
            } else {
                $scope.event.type = nodes[0].TripType;
                $scope.event.category = nodes[0].Category;
                $scope.event.name = nodes[0].EventName;
                $scope.event.item = "";
            }
            $scope.showDuplicateButton = false;
            $scope.event.id = 0;
            $scope.event.status = "";

            $scope.event.description = "";
            $scope.event.currentCompany = "";
            $scope.event.updatedBy = "";
            $scope.event.currentCustomer = "";
            $scope.event.updateTime = "";
            $scope.event.eventClass = "";
            $scope.event.code = "";
            $scope.event.podDate = $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
            $scope.event.podName = "";
            $scope.event.estimatedDate = $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
            $scope.event.actualDate = $filter('date')(new Date(), "yyyy-MM-dd HH:mm");
            $scope.event.eventCosts = "";
            $scope.event.AddressList = "";
            $scope.event.customerNotify = "1";
            $scope.loadLocationContact("", '', '', "add", "event");
        };

        $scope.removeTripEvent = function () {
            if ($scope.showGrid) {
                if (confirm('Are you sure you want to delete this record?')) {
                    var checkedIds = "";
                    /*
                    if ($scope.eventTree) {
                        angular.forEach(
                            $scope.eventTree,
                            function (item, key) {
                                if ($scope.eventTree[key].hasChecked) {
                                    checkedIds = checkedIds + "," + item.id;
                                }
                            });
                    }*/
                     $("#eventList .hasChecked:checked").each(function (item) {
                     checkedIds = checkedIds + "," + $(this).attr("data-id");
                     });
                    if (valutils.isEmptyOrUndefined(checkedIds)) {
                        toaster.pop('error', '', 'Please choose 1 record for this operation');
                        return;
                    }
                    tripService.removeTripEventByIds(checkedIds).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result != 'success') {
                            toaster.pop('error', '', res.msg);
                            return;
                        }

                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.loadTripEvents($scope.tripid);
                        }
                    })

                    return;
                }
                return;
            }
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (valutils.isEmptyOrUndefined(nodes[0].entityId)) {
                return;
            }
            if (nodes[0].type != "item") {
                toaster.pop('warning', '', "Please select the Event Template!");
                return;
            }
            if (confirm('Are you sure you want to delete this record?')) {
                tripService.removeTripEventById(nodes[0].entityId).then(function (res) {
                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        $scope.loadTripEvents($scope.tripid);
                    }
                })
            }
        };

        $scope.removeInvoice = function (id) {
            if (window.confirm("Are you sure you want to delete this record?")) {
                tripService.removeInvoice(id).then(function (res) {
                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        $scope.showInvoices($scope.tripid);
                    }
                })
            }
        };

        var checkAllRecords = false;
        $scope.selectAllEvents = function () {
            checkAllRecords = !checkAllRecords;
            $("#eventList .hasChecked").prop("checked",checkAllRecords);
        };


        $scope.unselectAllEvents = function () {
            checkAllRecords = false;
            $("#checkAllRecords").prop("checked",false);
            $("#eventList .hasChecked").prop("checked",false);
        };

        $scope.markComplete = function (markedComplete) {
            if ($scope.showGrid) {
                var checkedIds = "";
                /*
                if ($scope.eventTree) {
                    angular.forEach(
                        $scope.eventTree,
                        function (item, key) {
                            if ($scope.eventTree[key].hasChecked && $scope.eventTree[key] != 1) {
                                checkedIds = checkedIds + "," + item.id;
                            }
                        });
                }*/
                $("#eventList .hasChecked:checked").each(function (item) {
                    checkedIds = checkedIds + "," + $(this).attr("data-id");
                });
                if (valutils.isEmptyOrUndefined(checkedIds)) {
                    toaster.pop('error', '', 'Please choose at least one record for this operation');
                    return;
                }
                tripService.markComplete(checkedIds, markedComplete).then(function (res) {
                    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        $scope.loadTripEvents($scope.tripid);
                    }
                })

                return;
            }

            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (valutils.isEmptyOrUndefined(nodes[0].entityId)) {
                return;
            }
            if (nodes[0].type != "item") {
                toaster.pop('warning', '', "Please select the Event Template!");
                return;
            }

            tripService.markComplete(nodes[0].entityId, markedComplete).then(function (res) {
                if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                    $scope.loadTripEvents($scope.tripid);
                }
            })

        };
        var curDragNodes = [], settingTree = {
            data: {
                key: {
                    title: "t",
                    name: "name"
                },
                simpleData: {
                    enable: true,
                    idKey: "id",
                    pIdKey: "pId",
                }
            },
            view: {
                showLine: true,
                selectedMulti: false,
                dblClickExpand: false
            },
            edit: {
                drag: {
                    prev: function (treeId, nodes, targetNode) {
                        for (var i = 0,
                                 l = curDragNodes.length; i < l; i++) {
                            var curPNode = curDragNodes[i].getParentNode();
                            if (curPNode && curPNode !== targetNode.getParentNode() && curPNode.childOuter === false) {
                                return false;
                            }
                        }
                        return true;
                    },
                    inner: function (treeId, nodes, targetNode) {
                        for (var i = 0,
                                 l = curDragNodes.length; i < l; i++) {
                            if (curDragNodes[i].parentTId && curDragNodes[i].getParentNode() !== targetNode && curDragNodes[i].getParentNode().childOuter === false) {
                                return false;
                            }
                        }
                        return true;
                    },
                    next: function (treeId, nodes, targetNode) {
                        for (var i = 0,
                                 l = curDragNodes.length; i < l; i++) {
                            var curPNode = curDragNodes[i].getParentNode();
                            if (curPNode && curPNode !== targetNode.getParentNode() && curPNode.childOuter === false) {
                                return false;
                            }
                        }
                        return true;
                    }
                },
                enable: true,
                showRemoveBtn: false,
                showRenameBtn: false
            },
            callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    if (treeNode != null) {
                        $scope.getTripEventDetails(treeNode.entityId, true);
                    }
                },
                beforeDrag: function (treeId, treeNodes) {
                    for (var i = 0,
                             l = treeNodes.length; i < l; i++) {
                        if (treeNodes[i].drag === false) {
                            curDragNodes = null;
                            return false;
                        }
                    }
                    curDragNodes = treeNodes;
                    return true;
                },
                onDrop: function (event, treeId, treeNodes, targetNode, isMove) {
                    if (targetNode && isMove && treeNodes && treeNodes.length > 0) {
                        var list = [];
                        if (targetNode.getParentNode() != null) {
                            if (targetNode.type == "item") {
                                targetNode.getParentNode().children.forEach(function (item, itemSeq) {
                                    list.push({id: item.entityId, categorySeq: -1, itemSeq: itemSeq});
                                });
                            } else {
                                targetNode.getParentNode().children.forEach(function (category, categorySql) {
                                    category.children.forEach(function (item, itemSeq) {
                                        list.push({id: item.entityId, categorySeq: categorySql, itemSeq: itemSeq});
                                    })
                                });
                            }
                        }
                        updateTree(list);
                    }
                }
            }
        }, updateTree = function (param) {
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/saveEventTree.do",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({'list': JSON.stringify(param)})
            }).then(function successCallback(response) {
                    if (!valutils.isEmptyOrUndefined(response.data) && response.data.result == "success") {
                        $scope.loadTripEvents($scope.tripid);
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        // event tab->add route
        $scope.addRoute = function () {
            var modalInstance = $modal.open({
                templateUrl: 'addRoute.html',
                controller: 'addRouteCtrl',
                size: 'lg'
            });
            modalInstance.result.then(function (result) {
                if (null == $scope.event.description)
                    $scope.event.description = "" + "\n" + result;
                else
                    $scope.event.description = $scope.event.description + "\n" + result;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }
        // event tab->Email Address
        $scope.AddressList = [];
        $scope.addressEntity = {};
        $scope.isNewAddress = false;
        var addressIndex = -1, isImport = false,
            addressModal = $("#addressModal"),
            setRequired = function (isAdd) {
                $("#txtAddressName,#txtAddressEmail,#txtAddressMsg").removeAttr("required");
                $(".subtab").hide();
                if (isAdd) {
                    $("#addAddress").show();
                    $("#txtAddressName,#txtAddressEmail").attr("required", "required");
                } else {
                    $("#importAddress").show();
                    $("#txtAddressMsg").attr("required", "required");
                }
            };
        addressModal.modal({
            "backdrop": "static",
            keyboard: true,
            show: false
        }).on('hidden.bs.modal',
            function (e) {
            });
        $('#tablist a').click(function (e) {
            e.preventDefault()
            $(this).tab("show");
            isImport = $(this).attr('aria-controls') == "importAddress";
            setRequired(!isImport);
        });
        $scope.searchContact = function (keyword, id, callback) {
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/loadContacts.do",
                params: {
                    "contact": id,
                    "keyword": keyword
                }
            }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.contacts = response.data.contacts;
                    if (typeof (callback) == "function")
                        callback(response.data.selected_contact);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                })
        }
        $scope.addAddress = function () {
            addressIndex = -1;
            $scope.isNewAddress = true;
            $scope.addressEntity = {};
            $(".tab-content").removeClass("no-border");
            $("#tablist a:first").tab("show")
            setRequired(true);
            addressModal.modal("show");
        }
        $scope.editAddress = function (index, item) {
            $scope.isNewAddress = false;
            setRequired(true);
            $(".tab-content").addClass("no-border");
            $scope.addressEntity = {
                id: item.id,
                eventId: item.eventId,
                name: item.name,
                email: item.email,
                mailMsg: ""
            };
            addressIndex = index;
            addressModal.modal("show");
        }
        $scope.addressContact = function () {
            if ($scope.addressEntity && $scope.addressEntity.Contact) {
                $scope.addressEntity.name = ($scope.addressEntity.Contact.firstName || "") + " " + ($scope.addressEntity.Contact.lastName || "");
                $scope.addressEntity.email = $scope.addressEntity.Contact.email || "";
            }
        }
        $scope.removeAddress = function (index, id) {
            if (window.confirm("Are you sure you want to delete this record?")) {
                $scope.event.AddressList.splice(index, 1);
                if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                    tripService.removeEventNotify(id);
                }
            }
        };
        $scope.existInAddressList = function (list, entity) {
            if (null == list || undefined == list || list.length == 0) return false;
            if (null == entity || undefined == entity || valutils.isEmptyOrUndefined(entity.email)) return true;
            var flag = false;
            angular.forEach(list, function (item) {
                if (item.email == entity.email) {
                    flag = true;
                    return;
                }
            });
            return flag;
        };
        $scope.submitAddress = function (isValid) {
            if (isValid && $scope.addressEntity) {
                if (!$scope.event.AddressList) $scope.event.AddressList = [];
                if (addressIndex == -1) {
                    // add
                    if (isImport) {
                        // add -> import
                        if (!$scope.addressEntity.mailMsg
                            || typeof ($scope.addressEntity.mailMsg) != "string"
                            || $scope.addressEntity.mailMsg.length < 3) return;
                        $scope.addressEntity.mailMsg = commService.removeCommaFromEmailAddress($scope.addressEntity.mailMsg);
                        var emailArray = $scope.addressEntity.mailMsg.split(";");
                        if ($scope.addressEntity.mailMsg.contains(","))
                            emailArray = $scope.addressEntity.mailMsg.split(",");
                        emailArray.forEach(function (item, index) {
                            if (typeof (item) == "string" && item.length > 3) {
                                var name = "";
                                var email = "";
                                if (item.indexOf('"') != -1) {
                                    var array = item.split("\"");
                                    if (array.length == 3) {
                                        name = array[1].toString();
                                        email = array[2].toString();
                                    }
                                } else {
                                    var array = item.split("<");
                                    if (array.length == 2) {
                                        name = array[0].toString();
                                        email = array[1].toString();
                                    } else if (array.length == 1) {
                                        name = "";
                                        email = array[0];
                                    }
                                }
                                if (email!=""){
                                    email = email.replace(/(^\s*)/g, "");
                                    email = email.replace(/(\s*$)/g, "");
                                    email = email.replace(/(\>*)/g, "");
                                    email = email.replace(/(\<*)/g, "");
                                    if (name!=""){
                                        name = name.replace(/(^\s*)/g, "");
                                        name = name.replace(/(\s*$)/g, "");
                                    } else {
                                        name = email;
                                    }
                                    var entity = {
                                        "name": name,
                                        "email": email
                                    };
                                    if (!$scope.existInAddressList($scope.event.AddressList, entity)) {
                                        $scope.event.AddressList.push(entity);
                                        if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                                            entity.eventId = $scope.event.id;
                                            tripService.createEventNotify(entity).then(function (res) {
                                            })
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        // add -> search
                        $scope.addressEntity.mailMsg = "";
                        if (!$scope.existInAddressList($scope.event.AddressList, $scope.addressEntity)) {
                            $scope.event.AddressList.push($scope.addressEntity);
                            if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                                $scope.addressEntity.eventId = $scope.event.id;
                                tripService.createEventNotify($scope.addressEntity).then(function (res) {
                                })
                            }
                        } else {
                            toaster.pop('error', '', 'Exist record!');
                            return;
                        }
                    }
                } else {
                    // edit record
                    if (!$scope.existInAddressList($scope.event.AddressList, $scope.addressEntity)) {
                        $scope.event.AddressList[addressIndex] = $scope.addressEntity;
                        if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                            $scope.addressEntity.eventId = $scope.event.id;
                            tripService.createEventNotify($scope.addressEntity).then(function (res) {
                            })
                        }
                    }
                }
                addressModal.modal("hide");
            }
        };

        $scope.duplicateEmail = function (addresslist, eventId, tripId) {
            if ($scope.showGrid) {
                var count = 0;
                if ($scope.eventTree) {
                    angular.forEach(
                        $scope.eventTree,
                        function (item, key) {
                            if ($scope.eventTree[key].hasChecked) {
                                count++;
                            }
                        });
                }
                if (count > 1) {
                    toaster.pop('error', '', '"Can only select 1 record for this operation"');
                    return;
                }
            }
            tripService.replicateEmailAddressForAllEvents(addresslist, eventId, tripId).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.loadTripEvents(tripId, eventId);
                toaster.pop('success', '', "Successfully Duplicate Email Address To All Other Events");
            })
        };

        //Event Cost Items
        $scope.event.eventCosts = [];
        $scope.costEntity = {};
        var costIndex = -1,
            costModal = $("#costModal");
        costModal.modal({
            "backdrop": "static",
            keyboard: true,
            show: false
        }).on('hidden.bs.modal',
            function (e) {
            });
        $scope.addEventCost = function () {
            if ($scope.showGrid) {
                var count = 0;
                if ($scope.eventTree) {
                    angular.forEach(
                        $scope.eventTree,
                        function (item, key) {
                            if ($scope.eventTree[key].hasChecked) {
                                count++;
                            }
                        });
                }
                if (count > 1) {
                    toaster.pop('error', '', '"Can only select 1 record for this operation"');
                    return;
                }
            }

            costIndex = -1;
            var linkedEntity = "";
            var linkedEntityContact = "";
            if ($scope.linkedEntity) {
                linkedEntity = $scope.linkedEntity.locationId|| "";
                linkedEntityContact = $scope.linkedEntity.contactId|| "";
            }
            $scope.costEntity = {
                visible: "1",
                costLocation: {
                    //id: $scope.linkedEntity.id || "",
                    locationId: linkedEntity,
                    contactId: linkedEntityContact || ""
                },
                estDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
                actDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
                estCurrency: $localStorage.user.defaultCurrency,
                actCurrency: $localStorage.user.defaultCurrency
            };
            $scope.searchLocationContact("", linkedEntity, linkedEntityContact, function (model) {
                if (model) {
                    $scope.costEntity.costLocation = {
                        selected: model || "",
                        id: model.keyId || "",
                        locationId: model.id|| "",
                        contactId: model.contactId|| "",
                        code: model.code || "",
                        busPhone1: model.busPhone1 || "",
                        address1: model.address1 || "",
                        city: model.city || "",
                        stateProvince: model.stateProvince || "",
                        country: model.country || "",
                        notes: model.notes || "",
                        genericInfo: model.genericInfo || "",
                        customerName: model.customerName || "",
                        postalCode: model.postalCode || "",
                        contactName: model.contactName|| ""
                    };
                }
                else {
                    $scope.costEntity.costLocation = {}
                }
            });
            costModal.modal("show");
        }
        $scope.editEventCost = function (index, item) {
            $scope.costEntity = {
                id: item.id,
                eventId: item.eventId,
                estCost: item.estCost,
                estDate: $filter('date')(item.estDate, 'yyyy-MM-dd') || item.estDate,
                actCost: item.actCost,
                actDate: $filter('date')(item.actDate, 'yyyy-MM-dd') || item.estDate,
                estCurrency: item.estCurrency,
                actCurrency: item.actCurrency,
                chargeCode: item.chargeCode,
                chargeDesc: item.chargeDesc,
                description: item.description,
                visible: item.visible,
                costLocation: {
                    //id: item.contact
                    locationId: item.linkedEntity || "",
                    contactId: item.linkedEntityContact || ""
                },
                charge: {
                    id: item.chargeCode,
                    name: item.chargeDesc
                }
            };
            $scope.searchLocationContact("", item.linkedEntity, item.linkedEntityContact, function (model) {
                if (model) {
                    $scope.costEntity.costLocation = {
                        selected: model || "",
                        id: model.keyId || "",
                        locationId: model.id|| "",
                        contactId: model.contactId|| "",
                        code: model.code || "",
                        busPhone1: model.busPhone1 || "",
                        address1: model.address1 || "",
                        city: model.city || "",
                        stateProvince: model.stateProvince || "",
                        country: model.country || "",
                        notes: model.notes || "",
                        genericInfo: model.genericInfo || "",
                        customerName: model.customerName || "",
                        postalCode: model.postalCode || "",
                        contactName: model.contactName|| ""
                    };
                }
                else {
                    $scope.costEntity.costLocation = {}
                }
            });
            if (!valutils.isEmptyOrUndefined($scope.costEntity.actCurrency) && !valutils.isEmptyOrUndefined($scope.costEntity.actCost)) {
                currencyService.getUSDRate($scope.costEntity.actCurrency).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.costEntity.actUsedRate = res.records;
                    $scope.costEntity.actUsedCost = $scope.costEntity.actUsedRate * $scope.costEntity.actCost;
                });
            }

            costIndex = index;
            costModal.modal("show");
        }
        $scope.getUSDRate = function (currency) {
            currencyService.getUSDRate(currency).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.costEntity.actUsedRate = res.records;
                $scope.costEntity.actUsedCost = $scope.costEntity.actUsedRate * $scope.costEntity.actCost;
            });
        };
        $scope.removeEventCost = function (index, id) {
            if (window.confirm("Are you sure you want to delete this record?")) {
                $scope.event.eventCosts.splice(index, 1);
                if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                    tripService.removeTripCost(id);
                }
            }
        };
        $scope.submitEventCost = function (isValid) {
            if (isValid && $scope.costEntity) {
                if ($scope.costEntity.charge) {
                    $scope.costEntity.chargeCode = $scope.costEntity.charge.id;
                    $scope.costEntity.chargeDesc = $scope.costEntity.charge.name;

                } else {
                    toaster.pop('error', '', 'Charge code required!');
                    return;
                }
                if (!valutils.isEmptyOrUndefined($scope.costEntity.costLocation) && !valutils.isEmptyOrUndefined($scope.costEntity.costLocation.locationId)) {
                    $scope.costEntity.linkedEntity = $scope.costEntity.costLocation.locationId;
                }
                if (!valutils.isEmptyOrUndefined($scope.costEntity.costLocation) && !valutils.isEmptyOrUndefined($scope.costEntity.costLocation.contactId)) {
                    $scope.costEntity.linkedEntityContact = $scope.costEntity.costLocation.contactId;
                }
                if (!$scope.event.eventCosts) $scope.event.eventCosts = [];
                if (costIndex == -1) {
                    $scope.event.eventCosts.push($scope.costEntity);
                } else {
                    $scope.event.eventCosts[costIndex] = $scope.costEntity;
                }
                if (!valutils.isEmptyOrUndefined($scope.event.id) && $scope.event.id != 0) {
                    if (costIndex == -1) {
                        $scope.costEntity.eventId = $scope.event.id;
                        $scope.costEntity.tripId = $scope.tripid;

                        tripService.createTripCost($scope.costEntity).then(function (res) {
                            addressModal.modal("hide");
                            $scope.getTripEventDetails($scope.event.id, true);
                        })
                    } else {
                        $scope.costEntity.tripId = $scope.tripid;
                        tripService.updateTripCost($scope.costEntity).then(function (res) {
                            addressModal.modal("hide");
                            $scope.getTripEventDetails($scope.event.id, true);
                        })
                    }
                }
                costModal.modal("hide");
            }
        };
        $scope.submitted = false;
        $scope.submitTripEvents = function (isValid, tag) {
            var method = "";

            if (!isValid) {
                $scope.submitted = true;
            } else {
                if (!valutils.isEmptyOrUndefined($scope.event.id)) {
                    method = "operationconsole/operationconsole/updateEvent.do";
                } else {
                    method = "operationconsole/operationconsole/createEvent.do";
                }
                var notifies = "";
                if (!valutils.isEmptyOrUndefined($scope.event.AddressList)) {
                    notifies = JSON.stringify($scope.event.AddressList);
                }
                var costs = "";
                if ($scope.event.eventCosts) {
                    costs = JSON.stringify($.map($scope.event.eventCosts,
                        function (item) {
                            item.estDate = $filter('date')(item.estDate, 'yyyy-MM-dd');
                            item.actDate = $filter('date')(item.actDate, 'yyyy-MM-dd');
                            return item;
                        }));
                }
                var linkedEntity = "";
                var linkedEntityContact = "";
                if ($scope.linkedEntity) {
                    linkedEntity = $scope.linkedEntity.locationId;
                    linkedEntityContact = $scope.linkedEntity.contactId;
                }

                $http({
                    method: 'POST',
                    url: method,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param({
                        'tripID': $scope.tripid,
                        'id': $scope.event.id,
                        'name': $scope.event.name,
                        'type': $scope.event.type,
                        'category': $scope.event.category,
                        'sequence': $scope.event.sequence,
                        'categorySequence': $scope.event.categorySequence,
                        'item': $scope.event.item,
                        'description': $scope.event.description,
                        'eventClass': $scope.event.eventClass,
                        'estimatedDate': $scope.event.estimatedDate,
                        'actualDate': $scope.event.actualDate,
                        'podDate': $scope.event.actualDate,
                        'podName': $scope.event.podName,
                        'linkEntity': linkedEntity,
                        'linkEntityContact': linkedEntityContact,
                        'customerNotify': $scope.event.customerNotify,
                        'notifies': notifies,
                        'costs': costs,
                        'code': $scope.event.code
                    })
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (tag=='keep') {
                            // $scope.saveTrip(tag);
                            toaster.pop('success', '', "Save successfully");
                        }
                        $scope.loadTripEvents($scope.tripid, response.data.records.id || 0);
                        if (tag == 'exit') {
                            $scope.saveTrip(tag);
                            // $rootScope.back();
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        };

        //Event Subject
        var subjectModal = $("#subjectModal");
        subjectModal.modal({
            "backdrop": "static",
            keyboard: true,
            show: false
        }).on('hidden.bs.modal',
            function (e) {
            });
        $scope.showTripEventSubject = function () {
            $http({
                method: 'GET',
                url: 'operationconsole/operationconsole/getTripEventSubject.do',
                params: {
                    tripId: $scope.tripid
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.trip.subject = response.data.eventSubject;
                subjectModal.modal("show");
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
        };
        $scope.submitted = false;
        $scope.submitEventSubject = function (isValid) {
            if (!isValid) {
                $scope.submitted = true;
            } else {
                $http({
                    method: 'POST',
                    url: 'operationconsole/operationconsole/sendEventNotification.do',
                    params: {
                        tripId: $scope.tripid,
                        eventSubject: $scope.trip.subject
                    }
                }).then(function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Event Notifications sent out!");
                        subjectModal.modal("hide");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    })
            }
        };

        // save trip
        $scope.saveTrip = function (tag) {
            if (valutils.isEmptyOrUndefined($scope.trip.type)) {
                toaster.pop('error', '', 'Trip Type is empty!');
                return;
            }

            if (valutils.isEmptyOrUndefined($scope.trip.status)) {
                toaster.pop('error', '', 'Trip Status is empty!');
                return;
            }

            if ($scope.trip.status != 'QUO' && $scope.trip.status != 'QC' && $scope.trip.status != 'WC') {
                if (valutils.isEmptyOrUndefined($scope.trip.authorizedBy)) {
                    toaster.pop('error', '', 'Authorized By is empty!');
                    return;
                }
            }
            var trip = $scope.trip;
            var triptype = $scope.triptype;
            var tripmode = $scope.tripmode;
            var statusOptions = $scope.tripStatusOption;
            if ($scope.trip.status == 'DEL' || $scope.trip.status == 'ARC' || $scope.trip.status == 'CLO' || tag == 'keep') {
                if (valutils.isEmptyOrUndefined($scope.trip.type)) {
                    toaster.pop('error', '', 'Trip Type is empty!');
                    return;
                }

                if (valutils.isEmptyOrUndefined($scope.trip.status)) {
                    toaster.pop('error', '', 'Trip Status is empty!');
                    return;
                }

                if ($scope.trip.status != 'QUO' && $scope.trip.status != 'QC' && $scope.trip.status != 'WC') {
                    if (valutils.isEmptyOrUndefined($scope.trip.authorizedBy)) {
                        toaster.pop('error', '', 'Authorized By is empty!');
                        return;
                    }
                }

                var brokerId = "";
                if (!valutils.isEmptyOrUndefined($scope.broker)) {
                    brokerId = $scope.broker.id
                }

                $http({
                    method: 'POST',
                    url: "operationconsole/operationconsole/saveTrip.do",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param( {
                        tripID: $scope.tripid,
                        tripType: $scope.trip.type,
                        tripStatus: $scope.trip.status,
                        departmentId: $scope.trip.departmentId,
                        clientID: $scope.client.id || "",
                        consigneeID: $scope.consignee.id || "",
                        consigneeID2: $scope.consignee2.id || "",
                        consigneeID3: $scope.consignee3.id || "",
                        shipperID: $scope.shipper.id || "",
                        shipperID2: $scope.shipper2.id || "",
                        shipperID3: $scope.shipper3.id || "",
                        brokerID: brokerId,
                        brokerID2: $scope.broker2.id || "",
                        billedClientID: $scope.billedClient.id,
                        dropConsigneeName: $scope.trip.dropCosigneeName,
                        dropShipperName: $scope.trip.dropShipperName,
                        authorizedBy: $scope.trip.authorizedBy,
                        authorizationNo: $scope.trip.authorizationNo,
                        criticalTime: $scope.trip.criticalTime,
                        pickupDate: $filter('date')($scope.trip.pickupDate, 'yyyy-MM-dd'),
                        deliveryDate: $filter('date')($scope.trip.deliveryDate, 'yyyy-MM-dd'),
                        readyTime: $scope.trip.readyTime,
                        note: $scope.trip.note,
                        totalPieces: $scope.totalPieces,
                        totalWeight: $scope.totalWeight,
                        systemOfMeasure: $scope.trip.systemOfMeasure,
                        dept: $scope.dept
                    })
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (tag == 'exit') {
                            $rootScope.back();
                        } else {
                            toaster.pop('success', '', "Save successfully");
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
            else {
                var modalInstance = $modal.open({
                    templateUrl: 'beforeSaveTrip.html',
                    controller: 'confirmSaveCtrl',
                    size: 'lg',
                    resolve: {
                        record: function () {
                            return trip;
                        },
                        triptype: function () {
                            return triptype;
                        },
                        tripmode: function () {
                            return tripmode;
                        },
                        statusOptions: function () {
                            return statusOptions;
                        },
                        tag: function () {
                            return tag;
                        }
                    }
                });
                modalInstance.result.then(function (result) {
                    $scope.trip.status = result.status;
                    $scope.trip.note = result.note;
                    $scope.trip.criticalTime = result.criticalTime;
                    if (valutils.isEmptyOrUndefined($scope.trip.type)) {
                        toaster.pop('error', '', 'Trip Type is empty!');
                        return;
                    }

                    if (valutils.isEmptyOrUndefined($scope.trip.status)) {
                        toaster.pop('error', '', 'Trip Status is empty!');
                        return;
                    }

                    if ($scope.trip.status != 'QUO' && $scope.trip.status != 'QC' && $scope.trip.status != 'WC') {
                        if (valutils.isEmptyOrUndefined($scope.trip.authorizedBy)) {
                            toaster.pop('error', '', 'Authorized By is empty!');
                            return;
                        }
                    }

                    var brokerId = "";
                    if (!valutils.isEmptyOrUndefined($scope.broker)) {
                        brokerId = $scope.broker.id
                    }

                    $http({
                        method: 'POST',
                        url: "operationconsole/operationconsole/saveTrip.do",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param( {
                            tripID: $scope.tripid,
                            tripType: $scope.trip.type,
                            tripStatus: $scope.trip.status,
                            departmentId: $scope.trip.departmentId,
                            clientID: $scope.client.id || "",
                            consigneeID: $scope.consignee.id || "",
                            consigneeID2: $scope.consignee2.id || "",
                            consigneeID3: $scope.consignee3.id || "",
                            shipperID: $scope.shipper.id || "",
                            shipperID2: $scope.shipper2.id || "",
                            shipperID3: $scope.shipper3.id || "",
                            brokerID: brokerId,
                            brokerID2: $scope.broker2.id || "",
                            billedClientID: $scope.billedClient.id,
                            dropConsigneeName: $scope.trip.dropCosigneeName,
                            dropShipperName: $scope.trip.dropShipperName,
                            authorizedBy: $scope.trip.authorizedBy,
                            authorizationNo: $scope.trip.authorizationNo,
                            criticalTime: $scope.trip.criticalTime,
                            pickupDate: $scope.trip.pickupDate == "" ? "" : $scope.trip.pickupDate,
                            deliveryDate: $scope.trip.deliveryDate == "" ? "" : $filter('date')($scope.trip.deliveryDate, 'yyyy-MM-dd'),
                            readyTime: $scope.trip.readyTime,
                            note: $scope.trip.note,
                            totalPieces: $scope.totalPieces,
                            totalWeight: $scope.totalWeight,
                            systemOfMeasure: $scope.trip.systemOfMeasure,
                            dept: $scope.dept
                        })
                    }).then(
                        function successCallback(response) {
                            if (response.data.result != 'success') {
                                toaster.pop('error', '', response.data.msg);
                                return;
                            }
                            if (tag == 'exit') {
                                $rootScope.back();
                            } else {
                                toaster.pop('success', '', "Save successfully");
                            }
                        },
                        function errorCallback() {
                            toaster.pop('error', '', "Server error, please contact system administrator");
                        });


                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                    return;
                });
            }

        };
        $scope.cancel = function () {
            tripService.releaseTripLock().then(function (res) {
                $rootScope.back();
            });
        };
        // event tab->contact
        $scope.loadLocation = function (keyword, contactid, mode, callback) {
            $scope.linkedEntity = "";
            $scope.linkedEntity.selected = "";
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
                        $scope.linkedEntity = {
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
                        $scope.linkedEntity.selected.genericInfo = "";
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                })
        }

        $scope.loadLocationContact = function (keyword, locationid, contactid, mode, type, callback) {
            if (type=="event"){
                $scope.linkedEntity = "";
                $scope.linkedEntity.selected = "";
            } else {
                $scope.costEntity.costLocation = "";
                $scope.costEntity.costLocation.selected = "";
            }

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
                        if (type=="event") {
                            $scope.linkedEntity = {
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
                        } else {
                            $scope.costEntity.costLocation = {
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
                    }
                    if (mode == 'add') {
                        if (type=="event") {
                            $scope.linkedEntity.selected.genericInfo = "";
                        } else {
                            $scope.costEntity.costLocation.selected.genericInfo = "";
                        }
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                })
        }

        $scope.searchLocation = function (keyword, id, callback) {

            if ((!keyword || keyword.length == 0) && !loadClient && !(typeof(callback) == "function")) {
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
        }

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
        }

        $scope.selectLocation = function (location, type) {
            if (!valutils.isEmptyOrUndefined(location)) {
                if ('consignee' == type) {
                    $scope.trip.dropCosigneeName = "";
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
                        customerName: location.customerName,
                        postalCode: location.postalCode
                    };

                } else if ('consignee2' == type) {
                    $scope.consignee2 = {
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

                } else if ('consignee3' == type) {
                    $scope.consignee3 = {
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

                } else if ('client' == type) {
                    $scope.client = {
                        selected: location,
                        id: location.id,
                        code: location.code,
                        busPhone1: location.busPhone1,
                        address1: location.address1,
                        city: location.city,
                        country: location.country,
                        stateProvince: location.stateProvince,
                        notes: location.notes,
                        billingCompany: location.billingCompany,
                        genericInfo: location.genericInfo,
                        customerName: location.customerName,
                        postalCode: location.postalCode
                    };

                    if (!valutils.isEmptyOrUndefined($scope.client.billingCompany)) {
                        $http({
                            method: 'POST',
                            url: "operationconsole/operationconsole/loadLocation.do",
                            params: {
                                "locationId": $scope.client.billingCompany
                            }
                        }).then(
                            function successCallback(response) {
                                if (response.data.result != 'success') {
                                    toaster.pop('error', '', response.data.msg);
                                    return;
                                }

                                $scope.billedClient = {
                                    selected: response.data.selected_location,
                                    id: response.data.selected_location.id,
                                    code: response.data.selected_location.code,
                                    busPhone1: response.data.selected_location.busPhone1,
                                    address1: response.data.selected_location.address1,
                                    city: response.data.selected_location.city,
                                    country: response.data.selected_location.country,
                                    stateProvince: response.data.selected_location.stateProvince,
                                    notes: response.data.selected_location.notes,
                                    genericInfo: response.data.selected_location.genericInfo,
                                    customerName: response.data.selected_location.customerName,
                                    postalCode: response.data.selected_location.postalCode
                                };
                            },
                            function errorCallback() {
                                toaster.pop('error', '', "Server error, please contact system administrator");
                            });
                    }
                    else {
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
                            billingCompany: location.billingCompany,
                            genericInfo: location.genericInfo,
                            customerName: location.customerName,
                            postalCode: location.postalCode
                        };
                    }
                } else if ('shipper' == type) {
                    $scope.trip.dropShipperName = "";
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
                        customerName: location.customerName,
                        postalCode: location.postalCode
                    };

                } else if ('shipper2' == type) {
                    $scope.shipper2 = {
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

                } else if ('shipper3' == type) {
                    $scope.shipper3 = {
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
                        customerName: location.customerName,
                        postalCode: location.postalCode
                    };
                } else if ('broker2' == type) {
                    $scope.broker2 = {
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
                } else if ('billedClient' == type) {
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
                        genericInfo: location.genericInfo,
                        customerName: location.customerName,
                        postalCode: location.postalCode
                    };
                } else if ('event' == type) {
                    $scope.linkedEntity = {
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
                } else if ('cost' == type) {
                    $scope.costEntity.costLocation = {
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

        $scope.selectLocationContact = function (location, type) {
            if (!valutils.isEmptyOrUndefined(location)) {
                if ('event' == type) {
                    $scope.linkedEntity = {
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
                } else if ('cost' == type) {
                    $scope.costEntity.costLocation = {
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

        // save as pdf/csv
        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];
            if (type == 'invoice') {
                var keyarray = ["Invoice No", "Service Type", "Invoice To", "Shipper", "Currency", "Total", "Date"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].refNum));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].serviceType));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].billedClient));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].shipper));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].billingCurrency));
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].totalAmount || 0), 2) + "");
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].invoiceDate), "yyyy-MM-dd"));
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'freight') {
                var keyarray = ["Desc goods", "Act Dim", "Act Weight", "Bag Tag", "Act Value", "USD Value", "USD Rate"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].item));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].actualDimension));
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].actualWeight), 2) + "");
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].bagtag));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].estimatedCost)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].actualCurrency) + " " + $filter('number')(valutils.trimToEmpty(dataCollection[key].actualCost||0), 2));
                            }
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].usdCost || 0), 2) + "");
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].usdRate || 0), 2) + "");
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'docs') {
                var keyarray = ["File Type", "File Name", "File Size", "Created Date", "Created By"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].fileType));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].originalFileName));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].filesize)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].filesize) + " KB");
                            }
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].updateTime), 'yyyy-MM-dd'));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].updatedBy));
                            dataarray.push(valuearray);

                        });
                return dataarray;
            }
            if (type == 'notes') {
                var keyarray = ["Date", "Note", "User Name"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].createTime), 'yyyy-MM-dd HH:mm:ss'));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].content));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].createdBy));
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'costs') {
                var keyarray = ["Charge Code", "Est. Cost", "Act Cost",
                    "USD Cost", "USD Rate", "Est. Date", "Act Date", "Event", "Entity"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].chargeCode) + " " + valutils.trimToEmpty(dataCollection[key].chargeDesc) + " " + valutils.trimToEmpty(dataCollection[key].description));
                            if (valutils.isEmptyOrUndefined(dataCollection[key].estCost)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].estCurrency) + " " + $filter('number')(dataCollection[key].estCost, 2));
                            }
                            if (valutils.isEmptyOrUndefined(dataCollection[key].actCost)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(dataCollection[key].actCurrency) + " " + $filter('number')(dataCollection[key].actCost, 2));
                            }
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].actUsedCost || 0), 2) + "");
                            valuearray.push($filter('number')(valutils.trimToEmpty(dataCollection[key].actUsedRate || 0), 2) + "");
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].estDate), 'yyyy-MM-dd'));
                            valuearray.push($filter('date')(valutils.trimToEmpty(dataCollection[key].actDate), 'yyyy-MM-dd'));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].eventItem));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].linkedEntityName));
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
            if (type == 'event') {
                var keyarray = ["Leg", "Class", "Item", "Est/Act Date", "Status", "Entity", "Contact", "Notify", "Notes", "Total Costs"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (item, key) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].category));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].eventClassDesc));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].item));
                            var estActDate = "";
                            if (!valutils.isEmptyOrUndefined(dataCollection[key].estimatedDate)) {
                                estActDate = $filter('date')(dataCollection[key].estimatedDate, 'yyyy-MM-dd HH:mm:ss');
                            }
                            if (!valutils.isEmptyOrUndefined(dataCollection[key].actualDate)) {
                                if (estActDate != "") {
                                    estActDate = estActDate + " / "
                                }
                                estActDate = estActDate + $filter('date')(dataCollection[key].actualDate, 'yyyy-MM-dd HH:mm:ss');
                            }
                            valuearray.push(estActDate);
                            if (valutils.isEmptyOrUndefined(dataCollection[key].markedComplete) == "1"){
                                valuearray.push("");
                            } else {
                                valuearray.push("Y");
                            }
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].linkedEntityName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].contactName));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].jointNotify));
                            valuearray.push(valutils.trimToEmpty(dataCollection[key].description));
                            valuearray.push("USD " + $filter('number')(valutils.trimToEmpty(dataCollection[key].totalCosts || 0), 2) + "");
                            dataarray.push(valuearray);
                        });
                return dataarray;
            }
        }

        $scope.exportcsv = function (collection, type) {
            if (type == "invoice") {
                $scope.csvfilename = 'Invoice_List.csv';
            }
            if (type == "freight") {
                $scope.csvfilename = 'Freight_List.csv';
            }
            if (type == 'docs') {
                $scope.csvfilename = 'Generated_Docs.csv';
            }
            if (type == 'notes') {
                $scope.csvfilename = 'Notes.csv';
            }
            if (type == 'costs') {
                $scope.csvfilename = 'Costs.csv';
            }
            if (type == 'event') {
                $scope.csvfilename = 'trip_event.csv';
            }
            return coverttoexportdataformat(collection, type);

        }
        $scope.exportpdf = function (collection, type) {
            if (type == "invoice") {
                $scope.csvfilename = 'Invoice_List.pdf';
            }
            if (type == "freight") {
                $scope.csvfilename = 'Freight_List.pdf';
            }
            if (type == 'docs') {
                $scope.csvfilename = 'Generated_Docs.pdf';
            }
            if (type == 'notes') {
                $scope.csvfilename = 'Notes.pdf';
            }
            if (type == 'costs') {
                $scope.csvfilename = 'Costs.pdf';
            }
            if (type == 'event') {
                $scope.csvfilename = 'trip_event.pdf';
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
        // download file
        $scope.downloadFile = function (id) {
            window.location.href = "downloaddocument?id=" + id;
        };
        // route to another page
        $scope.go = function (router) {
            $state.go(router);
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

        $scope.startLoading = function (name) {
            $loading.start(name);
        };

        $scope.finishLoading = function (name) {
            $loading.finish(name);
        };
    }]);

app.directive('searchEventModel', function () {
    return {
        require: '^stTable',
        scope: {
            searchEventModel: '='
        },
        link: function (scope, ele, attr, ctrl) {
            var table = ctrl;
            scope.$parent.advsearch2 = function () {
                scope.$watch('searchEventModel', function (val) {
                    table.search(val);
                });
            }
        }
    };
});