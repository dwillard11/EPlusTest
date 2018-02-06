app.controller('quotedetailsctrl', ['$scope', 'deptService', '$loading', 'commService', 'codeService', '$window', '$rootScope', 'valutils', 'tripService', '$timeout', '$stateParams', '$filter', '$location', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', '$filter', '$log',
    function ($scope, deptService, $loading, commService, codeService, $window, $rootScope, valutils, tripService, $timeout, $stateParams, $filter, $location, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $filter, $log) {

        // get parameters from other page
        $scope.tripid = $stateParams.tripid;
        $scope.tripmode = $stateParams.tripmode; // add or edit
        $scope.triptype = $stateParams.triptype; // quote or shipper
        $scope.dept = $stateParams.dept;

        if (!$stateParams.tripid) {
            $scope.tripid = $window.sessionStorage["editQuoteTripid"];
            $scope.triptype = $window.sessionStorage["editQuoteTripType"];
            $scope.tripmode = $window.sessionStorage["editQuoteTripMode"];
            $scope.dept = $window.sessionStorage["editQuoteDeptId"];
        }
        $window.sessionStorage["editQuoteTripid"] = $scope.tripid;
        $window.sessionStorage["editQuoteTripType"] = $scope.triptype;
        $window.sessionStorage["editQuoteTripMode"] = $scope.tripmode;
        $window.sessionStorage["editQuoteDeptId"] = $scope.dept;

        $scope.loadingPage = true;
        $scope.activeSaveButton = true;
        $scope.activityCommCenter = false;
        var loadClient = false;
        deptService.getCurrentUserDepts().then(function (res) {
            $scope.departmentOptions = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.departmentOptions = res.records;
        });
        // load Quote or Shipper
        $scope.loadTrip = function (tripid, tripmode, triptype) {
            $loading.start('sample-1');
            $scope.expireDate = $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss');
            $scope.isLoading = true;
            $scope.rowFreightsPage = [];
            $scope.loadingPage = true;
            $scope.quote = "";
            $scope.consignees = [];
            $scope.consignee = {};
            $scope.client = {};
            $scope.clients = [];
            $scope.shippers = [];
            $scope.shipper = {};
            $scope.billedClient = {};
            $scope.authorizedBy = "";
            $scope.authorizationNo = "";
            $scope.version = "";
            $scope.refId = "";
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/loadTrip.do",
                params: {
                    "tripid": tripid,
                    "tripmode": tripmode,
                    "triptype": triptype,
                    "deptid": 0
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                if (response.data.readonly) {
                    $scope.activeSaveButton = false;
                    toaster.pop('error', '', response.data.readMsg);
                }
                $scope.quote = response.data.records;
                $scope.tripid = $scope.quote.id;
                $scope.quote.departmentId = $scope.quote.departmentId + "";
                $rootScope.title = "Quote Details " + ($scope.quote.quoteRefNo || "");
                $scope.authorizedBy = $scope.quote.authorizedBy;
                $scope.authorizationNo = $scope.quote.authorizationNo;
                $scope.expireDate = $filter('date')($scope.quote.expireDate, "yyyy-MM-dd HH:mm") || $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss');

                $scope.freightMeasure = $scope.quote.systemOfMeasure;
                $scope.consignees = response.data.consignees;
                $scope.version = $scope.quote.version;
                $scope.refId = $scope.quote.refId;
                $scope.quoteRefNo = $scope.quote.quoteRefNo;
                $scope.consignee = {
                    selected: response.data.selected_consignee,
                    id: response.data.selected_consignee.id,
                    code: response.data.selected_consignee.code,
                    busPhone1: response.data.selected_consignee.busPhone1,
                    address1: response.data.selected_consignee.address1,
                    city: response.data.selected_consignee.city,
                    country: response.data.selected_consignee.country,
                    stateProvince: response.data.selected_consignee.stateProvince,
                    notes: response.data.selected_consignee.notes,
                    genericInfo: response.data.selected_consignee.genericInfo,
                    customerName: response.data.selected_consignee.customerName || "",
                    postalCode: response.data.selected_consignee.postalCode || ""
                };
                $scope.clients = response.data.clients;
                $scope.client = {
                    selected: response.data.selected_client,
                    id: response.data.selected_client.id,
                    code: response.data.selected_client.code,
                    busPhone1: response.data.selected_client.busPhone1,
                    address1: response.data.selected_client.address1,
                    city: response.data.selected_client.city,
                    country: response.data.selected_client.country,
                    stateProvince: response.data.selected_client.stateProvince,
                    notes: response.data.selected_client.notes,
                    genericInfo: response.data.selected_client.genericInfo,
                    customerName: response.data.selected_client.customerName || "",
                    postalCode: response.data.selected_client.postalCode || ""
                };
                $scope.shippers = response.data.shippers;
                $scope.shipper = {
                    selected: response.data.selected_shipper,
                    id: response.data.selected_shipper.id,
                    code: response.data.selected_shipper.code,
                    busPhone1: response.data.selected_shipper.busPhone1,
                    address1: response.data.selected_shipper.address1,
                    city: response.data.selected_shipper.city,
                    country: response.data.selected_shipper.country,
                    stateProvince: response.data.selected_shipper.stateProvince,
                    notes: response.data.selected_shipper.notes,
                    genericInfo: response.data.selected_shipper.genericInfo,
                    customerName: response.data.selected_shipper.customerName || "",
                    postalCode: response.data.selected_shipper.postalCode || ""
                };

                if (tripmode == "edit") {
                    $scope.quoteTypeCode = $scope.quote.type;
                    $scope.quoteStatusCode = $scope.quote.status;
                    $scope.rowFreightsPage = $scope.quote.freights;
                    $scope.conllectionLength = $scope.rowFreightsPage.length;
                    $scope.isLoading = false;
                }
                if (tripmode == "commcenter") {
                    $scope.activityCommCenter = true;
                    $scope.quoteTypeCode = $scope.quote.type;
                    $scope.quoteStatusCode = $scope.quote.status;
                    $scope.rowFreightsPage = $scope.quote.freights;
                    $scope.conllectionLength = $scope.rowFreightsPage.length;
                    $scope.isLoading = false;
                    $scope.showTripEmails($scope.tripid);
                }
                if (tripmode == "add") {
                    $scope.quote.status = "QUO";
                    $scope.isReadOnly = true;
                    $scope.client.selected.genericInfo = "";
                    $scope.consignee.selected.genericInfo = "";
                    $scope.shipper.selected.genericInfo = "";
                }
                $scope.isLoading = false;
                $scope.loadingPage = false;
                if (!valutils.isEmptyOrUndefined($state.scopePrevious)) {
                    if ($state.scopePrevious.name == "app.operation_console.bol_details") {
                        $scope.activityDoc = true;
                        $scope.showDocs($scope.tripid);
                    }
                    else if ($state.scopePrevious.name == "app.operation_console.pickup_details") {
                        $scope.activityDoc = true;
                        $scope.showDocs($scope.tripid);
                    }
                    else if ($state.scopePrevious.name == "app.operation_console.quote_builder") {
                        $scope.activityDoc = true;
                        $scope.showDocs($scope.tripid);
                    }
                }
                $loading.finish('sample-1');
            }, function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
                $loading.finish('sample-1');
            })
        };
        $scope.copyToClient = function (type) {
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
        $scope.loadTrip($scope.tripid, $scope.tripmode, $scope.triptype);

        codeService.getEpCodeData("System Of Measure").then(function (res) {
            $scope.measureOption = [];
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            $scope.measureOption = res.records;
            if ($scope.freightMeasure)
                $scope.freightMeasure = $scope.measureOption[0].id;
        });

        // search location by type
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
        $scope.selectLocation = function (location, type) {
            if (null != location && undefined != location && '' != location) {
                if ('consignee' == type) {
                    $scope.quote.dropCosigneeName = "";
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

                }
                if ('client' == type) {
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
                        genericInfo: location.genericInfo,
                        customerName: location.customerName,
                        billingCompany: location.billingCompany,
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
                }
                if ('shipper' == type) {
                    $scope.quote.dropShipperName = "";
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

                }
            }
        };
        $scope.go = function (router, param) {
            $state.go(router);
        }

        // load epcode
        $scope.getEpCodeData = function (type) {
            if (type != "" && type != null) {
                $http({
                    method: 'GET',
                    url: "operationconsole/operationconsole/getEpCodeListByType.do",
                    params: {
                        "type": type
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (type == "Trip Type") {
                            $scope.tripOption = response.data.records;
                            $scope.quoteTypeOption = response.data.records;
                        } else if (type == "Status") {
                            $scope.statusOption = response.data.records;
                        } else if (type == "Trip Status") {
                            $scope.quoteStatusOption = response.data.records;
                            $scope.filteredStatus = [];
                            angular.forEach($scope.quoteStatusOption, function (cb, index) {
                                if (cb.id === 'QUO' || cb.id === 'QC' || cb.id === 'QA') {
                                    $scope.filteredStatus.push(cb);
                                }
                            });
                            $scope.quoteStatusOption = $scope.filteredStatus;

                        }

                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }
        $scope.getEpCodeData("Trip Type");
        $scope.getEpCodeData("Trip Status");
        $scope.getEpCodeData("Status");
        $scope.showFreights = function (tripid) {
            $scope.isLoading = true;
            tripService.loadFreights(tripid).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.rowFreightsPage = res.records;
                $scope.conllectionLength = $scope.rowFreightsPage.length;
                $scope.isLoading = false;
            });
        }
        // handle freight
        // record->row of freight list, if mode is add, record = ""
        // mode-> edit or add
        $scope.handlefreight = function (tripid, record, mode, freightMeasure) {
            var uniqueKey = "";
            if (mode == "edit") {
                // edit page should be a unique key
                uniqueKey = record.$$hashKey;
            }
            if (mode == "add") {
                record = "";
            }
            var modalInstance = $modal.open({
                templateUrl: 'myModalContent.html',
                controller: 'ModalInsCtrl',
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
                        return freightMeasure;
                    }
                }
            });

            modalInstance.result.then(function (result) {
                $scope.isLoading = true;
                if (mode == "add") {
                    result[0].tripId = tripid;
                    tripService.createFreight(result[0]).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showFreights($scope.tripid);
                        }
                    });
                }
                if (mode == "edit") {
                    for (var i = 0; i < $scope.rowFreightsPage.length; i++) {
                        if ($scope.rowFreightsPage[i].$$hashKey == result[0].uniqueKey) {
                            result[0].id = $scope.rowFreightsPage[i].id;
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
                $scope.showFreights(tripid);

                $scope.isLoading = false;

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

        $scope.removeFreight = function (freight) {
            if (confirm('Are you sure you want to delete this record?')) {
                $scope.isLoading = true;
                var index = $scope.rowFreightsPage.indexOf(freight);
                if (index !== -1) {
                    tripService.removeFreight(freight.id).then(function (res) {
                        if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                            $scope.showFreights($scope.tripid);
                        }
                    });
                }
                return;
            }
            return;

        }
        $scope.changeMeasure = function (tripid, measure) {
            if (measure == "I") {
                $scope.measureTag = "LBS";
            } else {
                $scope.measureTag = "KG";
            }
            tripService.updateTripMeasureSystem(tripid, measure).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.showFreights(tripid);
            });

        }
        $scope.loaddocs = function (tripid) {
            $scope.doclist = "";
            $scope.loadingdocs = true;
            $loading.start('sample-4');
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/loadDocs.do",
                params: {
                    "tripid": tripid,
                    "type": "NoEmail"
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.doclist = response.data.records;
                    $scope.loadingdocs = false;
                    $loading.finish('sample-4');
                },
                function errorCallback() {
                    $scope.loadingdocs = false;
                    $loading.finish('sample-4');
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };
        $scope.editdocs = function () {
            $scope.loaddocs($scope.tripid);
        }
        $scope.tripOption = [];
        $scope.statusOption = [];
        $scope.t = {
            item: "",
            description: ""
        }
        $scope.nodesOnClick = function (event, treeId, treeNode, clickFlag) {
            if (treeNode != null) {
                $scope.getData(treeNode.entityId);
            }
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
                        $scope.getData(treeNode.entityId);
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
                url: "operationconsole/operationconsole/saveConditionTree.do",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({'list': JSON.stringify(param)})
            }).then(function successCallback(response) {
                    if (!valutils.isEmptyOrUndefined(response.data) && response.data.result == "success") {
                        $scope.loadTree($scope.tripid, "load");
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        $scope.initTree = "";
        $scope.loadTree = function (tripId, mode, entityIds) {
            var method = '';
            if (mode == 'load') {
                method = 'operationconsole/operationconsole/retrieveQuoteTreeByTripID.do';
            }
            if (mode == 'reset') {
                method = 'operationconsole/operationconsole/resetQuoteTreeByTripID.do';
            }
            if (mode == 'import') {
                method = 'operationconsole/operationconsole/importTemplateByTripID.do';
                $scope.isFirstImportTree = false;
            }
            $http({
                method: 'POST',
                url: method,
                params: {
                    tripId: tripId,
                    entityIds: entityIds
                }
            }).then(
                function successCallback(response) {
                    if (response.data != null && response.data != "" && response.data.result == "success") {
                        var treeData = $.map(response.data.records,
                            function (item) {
                                if (item.type == "event") {
                                    item.childOuter = false;
                                    item.drag = false;
                                } else if (item.type == "category") {
                                    item.childOuter = false;
                                } else {
                                    if (item.IsComplete == "Y")
                                        item.iconSkin = "Check";
                                }
                                return item;
                            });
                        $.fn.zTree.init($("#dataTree"), settingTree, treeData);
                        zTree_Menu = $.fn.zTree.getZTreeObj("dataTree");
                        if (response.data.records && response.data.records.length > 0) {
                            var curMenu = zTree_Menu.getNodes()[0];
                            zTree_Menu.selectNode(curMenu);
                            $scope.getData(curMenu.entityId);
                        } else {
                            $scope.getData(0);
                        }
                        $scope.initTree = response.data.records;
                    }
                },
                function errorCallback() {
                    $scope.isLoading = false;
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        };

        $scope.isFirstImportTree = true;


        $scope.loadTreeMode = function (tripid, mode) {
            $loading.start('sample-2');
            if ($scope.tripmode == 'edit') {
                $scope.isFirstImportTree = false;
                if (mode == 'reset') {
                    if (confirm('All Terms & Conditions will be replaced by the tempate, are you sure to continue?')) {
                        $scope.loadTree(tripid, mode);
                        $loading.finish('sample-2');
                        return;
                    } else {
                        $loading.finish('sample-2');
                        return;
                    }
                }
                $scope.loadTree(tripid, mode);
                $loading.finish('sample-2');
            } else if ($scope.tripmode == 'add') {
                if (mode == 'load') {
                    $loading.finish('sample-2');
                    return;
                } else if (mode == 'reset') {
                    if ($scope.isFirstImportTree) {
                        $scope.loadTree(tripid, mode);
                        $scope.isFirstImportTree = false;
                        $loading.finish('sample-2');
                        return;
                    }
                    if (confirm('All Terms & Conditions will be replaced by the tempate, are you sure to continue?')) {
                        $scope.loadTree(tripid, mode);
                        $loading.finish('sample-2');
                        return;
                    } else {
                        $loading.finish('sample-2');
                        return;
                    }
                }
                $loading.finish('sample-2');
            }
        }
        $scope.getData = function (eId) {
            if (eId != "" && eId != null && eId > 0) {
                $http({
                    method: 'POST',
                    url: "operationconsole/operationconsole/retrieveConditionById.do",
                    params: {
                        "eid": eId
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        $scope.id = eId;
                        $scope.t.type = response.data.records.type;
                        $scope.t.name = response.data.records.name;
                        $scope.t.category = response.data.records.category;
                        $scope.t.sequence = response.data.records.sequence;
                        $scope.t.item = response.data.records.item;
                        $scope.t.description = response.data.records.description;
                        $scope.currentCompany = response.data.records.currentCompany;
                        $scope.updatedBy = response.data.records.updatedBy;
                        $scope.currentCustomer = response.data.records.currentCustomer;
                        $scope.updateTime = $filter('date')(response.data.records.updateTime, 'yyyy-MM-dd HH:mm:ss');
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }

        $scope.submitted = false;

        $scope.submitConditions = function (isValid, tag) {

            var method = "";
            if (!isValid) {
                $scope.submitted = true;
            } else {
                if (null != $scope.id && "" != $scope.id && undefined != $scope.id) {
                    method = "operationconsole/operationconsole/updateCondition.do";
                } else {
                    method = "operationconsole/operationconsole/addCondition.do";
                }
                $http({
                    method: 'POST',
                    url: method,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param( {
                        "tripID": $scope.tripid,
                        "id": $scope.id,
                        "name": $scope.t.name,
                        "type": $scope.t.type,
                        "category": $scope.t.category,
                        "sequence": $scope.t.sequence,
                        "item": $scope.t.item,
                        "description": $scope.t.description
                    })
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        $scope.loadTree($scope.tripid, 'load');
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
        }

        $scope.addCondition = function () {
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (undefined == nodes || null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
                $scope.t.category = "";
                $scope.t.name = "";
                $scope.t.type = "";
            } else {
                $scope.t.type = nodes[0].TripType;
                $scope.t.category = nodes[0].Category;
                $scope.t.name = nodes[0].EventName;
            }
            $scope.id = 0;
            $scope.t.sequence = "";
            $scope.t.item = "";
            $scope.t.description = "";
            $scope.currentCompany = "";
            $scope.updatedBy = "";
            $scope.currentCustomer = "";
            $scope.updateTime = "";
        }

        $scope.removeCondition = function () {
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
                return;
            }
            if (nodes[0].type != "item") {
                toaster.pop('warning', '', "Please select the Quote Template!");
                return;
            }
            if (confirm('Are you sure you want to delete this record?')) {
                $http({
                    method: 'GET',
                    url: 'operationconsole/operationconsole/removeCondition.do',
                    params: {
                        "id": nodes[0].entityId
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        $scope.loadTree($scope.tripid, 'load');
                        toaster.pop('success', '', "Delete successfully");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }

        function checkVal(val) {
            if (val != null && '' != val && undefined != val) {
                return true;
            }
            return false;
        }


        $scope.showBOL = function (tripid, mode, type) {
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.bol_details", param);
        }
        $scope.showPickup = function (tripid, mode, type) {
            var param = {
                tripid: tripid,
                tripmode: mode,
                triptype: type
            };
            $state.go("app.operation_console.pickup_details", param);
        }
        $scope.btnQuotePDF = function (tripid) {
            var param = {
                quoteid: tripid
            };
            $state.go("app.operation_console.quote_builder", param);
        };

        $scope.downloadFile = function (id) {
            window.location.href = "downloaddocument?id=" + id;
        };
        $scope.getPDF = function (tripid, quoteNum, version, url) {
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/getPDF.do",
                params: {
                    "version": version,
                    "quoteNum": quoteNum,
                    "tripId": tripid,
                    "url": url
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully!");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            // window.location.href = "downloaddocument?tripid=" + $scope.tripid + "&filename=" + filename;
        }
        $scope.handleQuote = function (tag) {

            if (!checkVal($scope.quote.type)) {
                toaster.pop('error', '', 'Trip Type is empty!');
                return;
            }

            if ($scope.quote.status == 'QA') {
                if (!checkVal($scope.quote.authorizedBy)) {
                    toaster.pop('error', '', 'Authorized By is empty!');
                    return;
                }
            }


            var method = "operationconsole/operationconsole/createQuote.do"
            if ($scope.tripmode == "edit") {
                method = "operationconsole/operationconsole/updateQuote.do"
            }

            $http({
                method: 'POST',
                url: method,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param( {
                    tripID: $scope.tripid,
                    tripType: $scope.quote.type,
                    tripStatus: $scope.quote.status,
                    departmentId: $scope.quote.departmentId,
                    clientID: $scope.client.id || "",
                    consigneeID: $scope.consignee.id || "",
                    authorizedBy: $scope.quote.authorizedBy,
                    shipperID: $scope.shipper.id || "",
                    billedClientID: $scope.billedClient.id || "",
                    authorizationNo: $scope.quote.authorizationNo,
                    dept: $scope.dept,
                    dropShipperName: $scope.quote.dropShipperName,
                    dropCosigneeName: $scope.quote.dropCosigneeName,
                    expireDate: document.getElementById("expireDate").value
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
                        toaster.pop('success', '', 'Save successfully!');
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        $scope.cancel = function () {
            tripService.releaseTripLock().then(function (res) {
                $rootScope.back();
            });
        }
        // Tree Model
        var treeModal = $("#treeModal"), originalTreeObj = null;
        treeModal.modal({"backdrop": "static", keyboard: true, show: false}).on('hidden.bs.modal', function (e) {
        });

        $scope.ShowTree = function () {
            $http({
                method: 'GET',
                url: "maintenance/quoteTemplate/getQuoteTree.do",
                params: {
                    "tripType": $scope.quote.type
                }

            }).then(
                function successCallback(response) {
                    if (response.data != null && response.data != "" && response.data.result == "success") {
                        $.fn.zTree.init($("#originalTree"), {
                            view: {selectedMulti: false},
                            check: {enable: true},
                            data: {simpleData: {enable: true}},
                            callback: {
                                onCheck: function (event, treeId, treeNode, clickFlag) {
                                }
                            }
                        }, response.data.records);
                        originalTreeObj = $.fn.zTree.getZTreeObj("originalTree");
                        treeModal.modal("show");
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }
        $scope.ImportTree = function () {
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
            if (confirm('All Terms & Conditions will be replaced by the selected template, are you sure to continue?')) {
                $scope.loadTree($scope.tripid, "import", entityIds.join(","));
                treeModal.modal("hide");
                return;
            } else {
                treeModal.modal("hide");
                return;
            }
        };

        function coverttoexportdataformat(dataCollection, type) {
            var dataarray = [];
            if (type == 'freight') {
                var keyarray = ["Desc Goods", "Pieces", "Est Dim", "Est Weight", "Est Value"];
                dataarray.push(keyarray);
                angular
                    .forEach(
                        dataCollection,
                        function (data) {
                            var valuearray = [];
                            valuearray.push(valutils.trimToEmpty(data.item));
                            valuearray.push($filter('number')(valutils.trimToEmpty(data.estimatedPieces || 0), 0) + "");
                            valuearray.push(valutils.trimToEmpty(data.estimatedDimension));
                            valuearray.push($filter('number')(valutils.trimToEmpty(data.estimatedWeight || 0), 2) + "");
                            if (valutils.isEmptyOrUndefined(data.estimatedCost)) {
                                valuearray.push("");
                            } else {
                                valuearray.push(valutils.trimToEmpty(data.estimatedCurrency) + " " + $filter('number')(valutils.trimToEmpty(data.estimatedCost||0), 2));
                            }
                            dataarray.push(valuearray);
                        });

            }
            return dataarray;
        }

        $scope.exportcsv = function (collection, type) {
            if (type == 'freight') {
                $scope.csvfilename = 'Freight_List.csv';
            }

            return coverttoexportdataformat(collection, type);

        }
        $scope.exportpdf = function (collection, type) {
            if (type == 'freight') {
                $scope.csvfilename = 'Freight_List.pdf';
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

        $scope.showDocs = function (tripid) {
            $scope.loadingDocs = true;
            tripService.loadDocsByType(tripid, 'NoEmail').then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.doclist = res.records;
                $scope.loadingDocs = false;
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
        }

        // Communication center tab
        $scope.showTripEmails = function (tripId) {
            $scope.loadingEmails = true;
            $loading.start('sample-3');
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
                $loading.finish('sample-3');
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
        $scope.markLabel = function (id, label) {
            commService.markLabel(id, label).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
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
        $scope.sendEmail = function (mail, tag) {
            if (tag == 'compose') {
                mail = {
                    tripId: $scope.tripid || "",
                    subject: "[" + $scope.quote.quoteRefNo + "]",
                    content: commService.buildSignare(),
                    mailFrom: "",
                    mailTo: "",
                    mailCc: "",
                    mailBcc: "",
                    departmentId: $scope.dept
                }
            } else {
                if (tag == 'edit') {
                    mail.subject = mail.subject.replace(/\[.*?\]/g, "[" + $scope.quote.quoteRefNo + "]");
                    if (mail.subject.indexOf($scope.quote.quoteRefNo) == -1) {
                        mail.subject += " [" + $scope.quote.quoteRefNo + "]";
                    }
                } else {
                    $scope.markEmail(mail.id, 'read');
                    if (tag == 'reply' || tag == 'replyAll') {
                        mail.subject = mail.subject.replace(/\[.*?\]/g, "[" + $scope.quote.quoteRefNo + "]");
                        if (mail.subject.indexOf($scope.quote.quoteRefNo) == -1) {
                            mail.subject += " [" + $scope.quote.quoteRefNo + "]";
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
app.controller('ModalInsCtrl', ['$scope', 'freightService', 'valutils', '$localStorage', 'toaster', '$http', '$modalInstance', 'mode', 'tripid', 'record', 'uniqueKey', 'measureCode',
    function ($scope, freightService, valutils, $localStorage, toaster, $http, $modalInstance, mode, tripid, record, uniqueKey, measureCode) {
        if (mode == "edit") {
            $scope.uniqueKey = uniqueKey;
        }
        var sizeUnit = '';
        var weightUnit = '';
        if (measureCode == 'M') {
            sizeUnit = 'CM';
            weightUnit = 'KG';
        } else {
            sizeUnit = 'Inches';
            weightUnit = 'LBS';
        }


        if (mode == "add") {
            $scope.f = {
                tripid: tripid,
                item: "",
                description: "",
                estimatedUOM: weightUnit,
                estimatedCurrency: $localStorage.user.defaultCurrency,
                lengthUOM: sizeUnit, // not save to db / CM/Inches
                estimatedChargeWt: ""
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
                estimatedUOM: weightUnit,
                estimatedCurrency: record.estimatedCurrency,
                estimatedWeight: record.estimatedWeight,
                estimatedCost: record.estimatedCost,
                estimatedPieces: record.estimatedPieces,
                lengthUOM: sizeUnit, // not save to db / CM/Inches
                estimatedChargeWt: record.estimatedChargeWt
            }
        }

        $scope.getChargeWt = function () {
            var pieces = $scope.f.estimatedPieces ? $scope.f.estimatedPieces : 0.0;
            var thisWeight = freightService.calculateChargeableWt(pieces, $scope.f.estimatedDimension1, $scope.f.estimatedDimension2, $scope.f.estimatedDimension3, $scope.f.lengthUOM);
            return thisWeight;
        }
        $scope.submitted = false;

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
                    "estimatedChargeWt": $scope.getChargeWt(),
                    "uniqueKey": $scope.uniqueKey
                })

                if ($scope.getChargeWt() >=1000000)
                {
                    toaster.pop('error', '', "Chargeable Weight exceeded maximum allowed value!");
                }
                else {
                    $modalInstance.close(freightData);
                }
            }

        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.getEpCodeData = function (type) {

            if (type != "" && type != null) {
                if (type == "Dim Unit") {
                    // hard cord for dim unit
                    // $scope.f.estimatedUOM = "Inches";
                    $scope.dimUnitOption = [
                        {
                            id: "CM",
                            name: "CM"
                        },
                        {
                            id: "Inches",
                            name: "Inches"
                        }
                    ];
                    return;
                }
                $http({
                    method: 'GET',
                    url: "maintenance/quoteTemplate/getEpCodeListByType.do",
                    params: {
                        "type": type
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (type == "Trip Type") {
                            $scope.tripOption = response.data.records;
                        } else if (type == "Status") {
                            $scope.statusOption = response.data.records;
                        } else if (type == "Currency") {
                            $scope.currencyOption = response.data.records;
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }
        $scope.getEpCodeData("Currency");
        $scope.getEpCodeData("Dim Unit");

        $scope.changeWeightUnit = function (type, mode) {
            if (mode == 'est') {
                if (isNaN($scope.f.estimatedWeight)) {
                    $scope.f.estimatedWeight = 0.00;
                    return;
                }
                if (type == "LBS") {
                    $scope.f.estimatedWtUOM = "LBS";
                    $scope.f.estimatedWeight = parseFloat(($scope.f.estimatedWeight * 2.21).toFixed(2)); // .toFixed(2);
                    return;
                }
                if (type == "KG") {
                    $scope.f.estimatedWtUOM = "KG";
                    $scope.f.estimatedWeight = parseFloat(($scope.f.estimatedWeight / 2.21).toFixed(2));// .toFixed(2);
                    return;
                }
            }

        }
        $scope.changedValue = function (type) {
            if (isNaN($scope.f.estimatedDimension1)) {
                $scope.f.estimatedDimension1 = 0;
                return;
            }
            if (isNaN($scope.f.estimatedDimension2)) {
                $scope.f.estimatedDimension2 = 0;
                return;
            }
            if (isNaN($scope.f.estimatedDimension3)) {
                $scope.f.estimatedDimension3 = 0;
                return;
            }
            if (type == "CM") {
                $scope.f.estimatedDimension1 = parseInt($scope.f.estimatedDimension1 * 2.5);
                $scope.f.estimatedDimension2 = parseInt($scope.f.estimatedDimension2 * 2.5);
                $scope.f.estimatedDimension3 = parseInt($scope.f.estimatedDimension3 * 2.5);
            } else {
                $scope.f.estimatedDimension1 = parseInt($scope.f.estimatedDimension1 * 0.4);
                $scope.f.estimatedDimension2 = parseInt($scope.f.estimatedDimension2 * 0.4);
                $scope.f.estimatedDimension3 = parseInt($scope.f.estimatedDimension3 * 0.4);
            }
        }

    }]);
app.controller('uploadctrl', ['$scope', 'valutils', 'codeService', '$localStorage', 'toaster', '$http', '$modalInstance', 'tripid', 'record', 'uniqueKey', function ($scope, valutils, codeService, $localStorage, toaster, $http, $modalInstance, tripid, record, uniqueKey) {
    $scope.d = {
        fileType: "",
        tripid: tripid
    }
    codeService.getEpCodeData("Document Type").then(function (res) {
        $scope.fileTypeOption = [];
        if (res.result != 'success') {
            toaster.pop('error', '', res.msg);
            return;
        }
        $scope.fileTypeOption = res.records;
    });
    $scope.submitted = false;
    $scope.showalert = false;
    $scope.uploadFileToUrl = function (file, tripid, fileType, uploadUrl) {
        var fd = new FormData();
        fd.append('file', file);
        fd.append('tripid', tripid);
        fd.append('filetype', fileType);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).success(function () {
            $modalInstance.close(true);
        }).error(function () {
        });
    }
    $scope.uploadFile = function (file, tripid, fileType) {
        var uploadUrl = "uploaddocument";
        $scope.uploadFileToUrl(file, tripid, fileType, uploadUrl);
    };
    $scope.ok = function (isValid) {
        if (!isValid) {
            $scope.submitted = true;
            return;
        } else {

            var fi = document.getElementById('fileinput').files[0];
            if (fi.name.split('.').pop().toLowerCase() == 'bat' || fi.name.split('.').pop().toLowerCase() == 'exe') {
                $scope.showalert = true;
                return;
            }
            $scope.showalert = false;
            $scope.uploadFile(fi, tripid, $scope.d.fileType);

        }
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);