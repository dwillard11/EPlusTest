app.controller('eventtemplatesctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q', 'valutils', '$filter', '$stateParams', '$rootScope', '$window', 'commService',
function ($scope, $timeout, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, valutils, $filter, $stateParams, $rootScope, $window, commService) {
    if (!$stateParams.name) {
        $stateParams.tripType = $window.sessionStorage["eventTemplateTripType"];
        $stateParams.name = $window.sessionStorage["eventTemplateName"];
    }
    var params = {
        tripType: $stateParams.tripType || "",
        name: $stateParams.name || ""
    }
    $window.sessionStorage["eventTemplateTripType"] = $stateParams.tripType;
    $window.sessionStorage["eventTemplateName"] = $stateParams.name;

    $scope.tripOption = [];
    $scope.eventCodeOption = [];
    $scope.eventClassOption = [];
    $scope.statusOption = [];
    $scope.yesNoOption = [];
    $scope.currencyOption = [];
    $scope.chargeCodeOption = [];
    $scope.updateTime = null;

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
                                list.push({ id: item.entityId, categorySeq: -1, itemSeq: itemSeq });
                            });
                        } else {
                            targetNode.getParentNode().children.forEach(function (category, categorySql) {
                                category.children.forEach(function (item, itemSeq) {
                                    list.push({ id: item.entityId, categorySeq: categorySql, itemSeq: itemSeq });
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
            url: "maintenance/tripEventTemplate/saveEventTree.do",
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            data: $.param({ 'list': JSON.stringify(param) })
        }).then(function successCallback(response) {
            if (response.data != null && response.data != "" && response.data.result == "success") {
                loadTree();
            }
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    }, loadTree = function (selectId) {
        $http({
            method: 'GET',
            url: "maintenance/tripEventTemplate/getEventTree.do",
            params: params
        }).then(function successCallback(response) {
            if (response.data != null && response.data != "" && response.data.result == "success") {
                var treeData = $.map(response.data.records,
                function (item) {
                    if (item.type == "event") {
                        item.childOuter = false;
                        item.drag = false;
                    } else if (item.type == "category") {
                        item.childOuter = false;
                    } else {
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
                $scope.getData(curTreeNode.entityId);
            } else if (response.data != null && response.data != "" && response.data.result == "error") {
                $rootScope.back();
            }
        },
        function errorCallback() {
            $scope.isLoading = false;
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    };
    loadTree();



    $scope.getEpCodeData = function (type) {
        if (type != "" && type != null) {
            $http({
                method: 'GET',
                url: "maintenance/tripEventTemplate/getEpCodeListByType.do",
                params: {
                    "type": type
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                if (type == "Trip Type") {
                    $scope.tripOption = response.data.records;
                } else if (type == "Event Code") {
                    $scope.eventCodeOption = response.data.records;
                } else if (type == "Event Class") {
                    $scope.eventClassOption = response.data.records;
                } else if (type == "Status") {
                    $scope.statusOption = response.data.records;
                } else if (type == "Currency") {
                    $scope.currencyOption = response.data.records;
                } else if (type == "Yes Or No") {
                    $scope.yesNoOption = response.data.records;
                } else if (type == "Charge Code") {
                    $scope.chargeCodeOption = response.data.records;
                }
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }

    $scope.getEpCodeData("Trip Type");
    $scope.getEpCodeData("Event Code");
    $scope.getEpCodeData("Event Class");
    $scope.getEpCodeData("Status");
    $scope.getEpCodeData("Currency");
    $scope.getEpCodeData("Charge Code");
    $scope.getEpCodeData("Yes Or No");

    $scope.locations = [];
    $scope.contacts = [];
    $scope.curContact = null;

    $scope.loadLocationContact = function (keyword, locationid, contactid, mode, callback) {
        $scope.linkedEntity = {};
        $scope.linkedEntity.selected = "";
        //toaster.pop('wait', '', 'Loading...');
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
                        customerName: response.data.selected_location.customerName|| "",
                        postalCode: response.data.selected_location.postalCode|| "",
                        contactName: response.data.selected_location.contactName
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

    $scope.searchLocationContact = function (keyword, locationid, contactid, callback) {
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
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
    }

    // search LocationContact by type
    $scope.selectLocationContact = function (location, type) {
        if (null != location && undefined != location && '' != location) {
            if ('cost' == type) {
                $scope.costEntity.linkedEntity = {
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
            else {
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
            }
        }
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

    $scope.getData = function (eId) {
        if (eId != "" && eId != null && eId > 0) {
            $http({
                method: 'GET',
                url: "maintenance/tripEventTemplate/retrieveEvent.do",
                params: {
                    "eid": eId
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                $scope.id = eId;
                $scope.type = response.data.records.type;
                $scope.status = response.data.records.status;
                $scope.eventClass = response.data.records.eventClass;
                $scope.name = response.data.records.name;
                $scope.category = response.data.records.category;
                $scope.sequence = response.data.records.sequence;
                $scope.categorySequence = response.data.records.categorySequence;
                $scope.code = response.data.records.code;
                $scope.item = response.data.records.item;
                $scope.linkedEntity = response.data.records.linkedEntity;
                $scope.description = response.data.records.description;
                $scope.cost = response.data.records.cost;
                $scope.status = response.data.records.status;
                $scope.currentCompany = response.data.records.currentCompany;
                $scope.updatedBy = response.data.records.updatedBy;
                $scope.currentCustomer = response.data.records.currentCustomer;
                $scope.customerNotify = response.data.records.customerNotify;
                $scope.duplicateEmail = response.data.records.duplicateEmailForAllEvent == "Y";
                $scope.updateTime = $filter('date')(response.data.records.updateTime, 'yyyy-MM-dd HH:mm:ss');

                $scope.loadLocationContact("", response.data.records.linkedEntity, response.data.records.linkedEntityContact, "edit");

                if (response.data.records.templateCost) $scope.CostList = response.data.records.templateCost;

                if (response.data.records.templateNotify) $scope.AddressList = response.data.records.templateNotify;
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }

    $scope.submitted = false;
    $scope.submit = function (isValid) {
        var method = "";
        if (!isValid) {
            $scope.submitted = true;
        } else {
            method = "maintenance/tripEventTemplate/saveEventTemplate.do";
            var costList = "";
            if ($scope.CostList) {
                costList = JSON.stringify($.map($scope.CostList,
                function (item) {
                    item.estDate = $filter('date')(item.estDate, 'yyyy-MM-dd') || item.estDate;
                    return item;
                }));
            }
            var addressList = "";
            if ($scope.AddressList) {
                addressList = JSON.stringify($scope.AddressList);
            }
            $http({
                method: 'POST',
                url: method,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                data: $.param({
                    "id": $scope.id,
                    "name": $scope.name || "",
                    "type": $scope.type || "",
                    "eventClass": $scope.eventClass || "",
                    "category": $scope.category || "",
                    "sequence": $scope.sequence || "",
                    "categorySequence": $scope.categorySequence || "",
                    "code": $scope.code || "",
                    "item": $scope.item || "",
                    "description": $scope.description || "",
                    "linkedEntity": $scope.linkedEntity.locationId || "",
                    "linkedEntityContact": $scope.linkedEntity.contactId || "",
                    "status": $scope.status,
                    "customerNotify": $scope.customerNotify,
                    "duplicateEmailForAllEvent": $scope.duplicateEmail ? "Y" : "N",
                    "templateCost": costList,
                    "templateNotify": addressList
                })
            }).then(function successCallback(response) {
                if (response.data.result != 'success' || !response.data.records) {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                toaster.pop('success', '', "Save successfully");
                loadTree(response.data.records + 100000000);
                //$state.go("app.system_maintenance.event_templates");
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }

    $scope.addEvent = function () {
        var treeObj = $.fn.zTree.getZTreeObj("dataTree");
        var nodes = treeObj.getSelectedNodes();
        if (null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
            $scope.category = "";
            $scope.name = "";
            $scope.type = "";
        } else {
            $scope.type = nodes[0].TripType;
            $scope.category = nodes[0].Category;
            $scope.name = nodes[0].EventName;
        }
        $scope.id = 0;
        $scope.status = "";
        $scope.code = "";
        $scope.eventClass = "T";
        $scope.sequence = "";
        $scope.categorySequence = "";
        $scope.item = "";
        $scope.linkedEntity = {};
        $scope.description = "";
        $scope.cost = 0;
        $scope.status = "Active";
        $scope.currentCompany = "";
        $scope.updatedBy = "";
        $scope.currentCustomer = "";
        $scope.customerNotify = "1";
        $scope.updateTime = "";
        $scope.CostList = [];
        $scope.AddressList = [];
        $scope.loadLocationContact("", "", "", "add");
    }

    $scope.remove = function () {
        var treeObj = $.fn.zTree.getZTreeObj("dataTree");
        var nodes = treeObj.getSelectedNodes();
        if (null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
            return;
        }
        if (nodes[0].type != "item") {
            toaster.pop('warning', '', "Please select an event template!");
            return;
        }
        if (confirm('Are you sure you want to delete this record?')) {
            $http({
                method: 'GET',
                url: 'maintenance/tripEventTemplate/removeEventTemplate.do',
                params: {
                    "id": nodes[0].entityId
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                toaster.pop('success', '', "Delete successfully");
                loadTree();
                $state.go("app.system_maintenance.event_templates");
            },
            function errorCallback(response) {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }

    //Event Cost Items
    $scope.CostList = [];
    $scope.costEntity = {};
    var costIndex = -1,
    costModal = $("#costModal");
    costModal.modal({
        "backdrop": "static",
        keyboard: true,
        show: false
    }).on('hidden.bs.modal',
    function (e) { });
    $scope.addCost = function () {
        costIndex = -1;
        $scope.costEntity = {linkedEntity: $scope.linkedEntity};
        costModal.modal("show");
    }
    $scope.editCost = function (index, item) {
        var linkedEntity = item.linkedEntity;
        var linkedEntityContact = item.linkedEntityContact;

        $scope.costEntity = {
            id: item.id,
            templateId: item.templateId,
            estCost: item.estCost,
            estDate: $filter('date')(item.estDate, 'yyyy-MM-dd') || item.estDate,
            estCurrency: item.estCurrency,
            chargeCode: item.chargeCode,
            chargeDesc: item.chargeDesc,
            description: item.description,
            visible: item.visible,
            linkedEntity: linkedEntity,
            linkedEntityContact: linkedEntityContact,
            charge: {
                id: item.chargeCode,
                name: item.chargeDesc
            }
        };

        $scope.searchLocationContact("", linkedEntity, linkedEntityContact, function (model) {
            if (model) {
                $scope.costEntity.linkedEntity = {
                    selected: model || "",
                    id: model.keyId || "",
                    locationId: model.id|| "",
                    contactId: model.contactId|| "",
                    code: model.code|| "",
                    busPhone1: model.busPhone1|| "",
                    address1: model.address1|| "",
                    city: model.city|| "",
                    country: model.country|| "",
                    stateProvince: model.stateProvince|| "",
                    notes: model.notes|| "",
                    genericInfo: model.genericInfo|| "",
                    customerName: model.customerName|| "",
                    postalCode: location.postalCode|| "",
                    contactName: model.contactName|| ""
                };
            }
            else { $scope.costEntity.linkedEntity = {} }
        });
        costIndex = index;
        costModal.modal("show");
    }

    $scope.removeCost = function (index) {
        if (window.confirm("Are you sure you want to delete this record?")) {
            $scope.CostList.splice(index, 1);
        }
    }

    $scope.submitCost = function (isValid) {
        if (isValid && $scope.costEntity) {
            var linkedEntity = $scope.costEntity.linkedEntity.locationId;
            var linkedEntityContact = $scope.costEntity.linkedEntity.contactId;
            if ($scope.costEntity.charge) {
                $scope.costEntity.chargeCode = $scope.costEntity.charge.id;
                $scope.costEntity.chargeDesc = $scope.costEntity.charge.name;
            }
            $scope.costEntity.linkedEntity = linkedEntity;
            $scope.costEntity.linkedEntityContact = linkedEntityContact;
            if (!$scope.CostList) $scope.CostList = [];
            if (costIndex == -1) {
                $scope.CostList.push($scope.costEntity);
            } else {
                $scope.CostList[costIndex] = $scope.costEntity;
            }
            costModal.modal("hide");
        }
    }

    //Email Address
    $scope.AddressList = [];
    $scope.addressEntity = {};
    $scope.isNewAddress = false;
    var addressIndex = -1, isImport = false,
    addressModal = $("#addressModal"),
    setRequired = function (isAdd) {
        $("#txtAddressName,#txtAddressEmail,#txtAddressMsg").removeAttr("required");
        $(".tab-pane").hide();
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
    function (e) { });
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
            templateId: item.templateId,
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
    $scope.removeAddress = function (index) {
        if (window.confirm("Are you sure you want to delete this record?")) {
            $scope.AddressList.splice(index, 1);
        }
    }

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
            if (!$scope.AddressList) $scope.AddressList = [];
            if (addressIndex == -1) {
                if (isImport) {
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
                                if (!$scope.existInAddressList($scope.AddressList, entity)) {
                                    $scope.AddressList.push(entity);
                                }
                            }
                        }
                    });
                } else {
                    $scope.addressEntity.mailMsg = "";
                    $scope.AddressList.push($scope.addressEntity);
                }
            } else {
                $scope.AddressList[addressIndex] = $scope.addressEntity;
            }
            addressModal.modal("hide");
        }
    }

    $scope.go = function (router, param) {
        $state.go(router);
    }

    $scope.btnCancel = function () {
        $state.go("app.system_maintenance.event_template_list");
    }
}])
