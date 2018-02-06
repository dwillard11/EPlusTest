app.controller('entityeditctrl', ['$scope', '$rootScope', '$http', 'toaster', '$stateParams', '$state', '$timeout', '$filter', '$window',
function ($scope, $rootScope, $http, toaster, $stateParams, $state, $timeout, $filter, $window) {
    $scope.isLoading = false;
    if (!$stateParams.status) {
        $stateParams.status = $window.sessionStorage["entityEditStatus"];
        $stateParams.entityId = $window.sessionStorage["entityEditEntityId"];
        $stateParams.selectId = $window.sessionStorage["entityEditSelectId"];
    }
    var tag = $stateParams.status;
    if ("from_edit" == tag || "from_edit_courier" == tag ) {
        $scope.pagestatus = "Edit";
    } else {
        $scope.pagestatus = "Add";
    }
    var entityId = $stateParams.entityId || "";
    var selectId = $stateParams.selectId || "";
    $window.sessionStorage["entityEditStatus"] = $stateParams.status;
    $window.sessionStorage["entityEditEntityId"] = entityId;
    $window.sessionStorage["entityEditSelectId"] = selectId;

    var isValidEntity = false, validTimer = null;
    var editType = "Entity";
    $scope.showBox = 1;
    $scope.entityOption = [];
    $scope.entityBrokersOption = [];
    $scope.yesNoOption = [];
    $scope.contactTypeList = [];
    $scope.onlineStatusList = [];
    $scope.IsCouriers = false;
    $scope.TreeOption = {
        id: entityId,
        showOnline: false,
        keyword: ""
    }

    // Entity
    $scope.entityTypeList = [];
    $scope.statusOption = [];
    $scope.entity = {};

    var getEpCodeData = function (type) {
        if (type != "" && type != null) {
            $http({
                method: 'GET',
                url: "maintenance/userManager/getEpCodeListByType.do",
                params: {
                    "type": type
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                if (type == "Entity Type") {
                    $scope.entityTypeList = response.data.records;
                } else if (type == "Status") {
                    $scope.statusOption = response.data.records;
                } else if (type == "Yes Or No") {
                    $scope.yesNoOption = response.data.records;
                } else if (type == "Contact Type") {
                    $scope.contactTypeList = response.data.records;
                } else if (type == "Online Status") {
                    $scope.onlineStatusList = response.data.records;
                }
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }
    getEpCodeData("Entity Type");
    getEpCodeData("Status");
    getEpCodeData("Yes Or No");
    getEpCodeData("Contact Type");
    getEpCodeData("Online Status");

    $scope.getEntity = function (id) {
        $http({
            method: 'GET',
            url: "customer/customerManager/retrieveCustomerProfileById.do",
            params: {
                "id": id
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.entity = response.data.records;
            if ($scope.entity && $scope.entity.id) {
                $scope.IsCouriers = $scope.entity.type == "COURIER";
                $scope.TreeOption.id = $scope.entity.id;
                if (response.data.records.updateTime)
                    $scope.entity.updateTime = $filter('date')(response.data.records.updateTime, 'yyyy-MM-dd HH:mm:ss');
                if (selectId > 0) {
                    loadTree(selectId);
                    selectId = 0;
                } else {
                    loadTree();
                }
            }
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    };

    var createCustomerType = "";
    $scope.submitEntity = function (isValid) {
        if (isValid && $scope.entity && editType == "Entity") {
            if ($scope.entity.id) {
                $http({
                    method: 'GET',
                    url: "customer/customerManager/updateCustomer.do",
                    params: $scope.entity
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    $scope.getEntity(entityId);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            } else {
                $http({
                    method: 'GET',
                    url: "customer/customerManager/createCustomer.do",
                    params: $scope.entity
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    if (createCustomerType == "save" && response.data.records) {
                        //$scope.pagestatus = "Edit";
                        //entityId = response.data.records.id;
                        //$scope.getEntity(entityId);
                        var param = {
                            entityId: response.data.records.id,
                            status: "from_edit"
                        };
                        $state.go('app.system_maintenance.entity_edit', param);
                    } else {
                        $state.go("app.system_maintenance.entity_list");
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            }
        }
    }

    $scope.saveEntity = function (mode) {
        if (mode == "save") {
            createCustomerType = "save";
        } else {
            createCustomerType = "exit";
        }
        $timeout(function () {
            $("#btnSubmitEntity").click();
        }, 100);
    }


    $scope.getEntityList = function (type, keyword) {
        $http({
            /*
            method: 'GET',
            url: "customer/customerManager/retrieveCustomerByType.do",
            params: {
                "advanceSearch": type,
                "keyword": keyword || ""
            }
            */
            method: 'POST',
            url: "operationconsole/operationconsole/loadLocations.do",
            params: {
                "keyword": keyword,
                "type": type
            }

        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            if (type == "") {
                $scope.entityOption = response.data.clients;
                $scope.entityOption.splice(0,0,{id:"",name:"&nbsp;"});
            } else if (type == "BROKER") {
                $scope.entityBrokersOption = response.data.brokers;
                $scope.entityBrokersOption.splice(0,0,{id:"",name:"&nbsp;"});
            }
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    }
    $scope.BillingCompany = {}, $scope.USBroker = {}, $scope.CABroker = {}, $scope.AltUSBroker = {}, $scope.AltCABroker = {}, $scope.NATBroker = {};

    $scope.selectCustomer = function (select, type) {
        if (!select || '' == select) return;
        if (type == "Billing") {
            $scope.location.billingCompany = select.id;
        }else if (type == "US") {
            $scope.location.brokerUs = select.id;
        }else if (type == "CA") {
           $scope.location.brokerCa = select.id;
        }else if (type == "AltUS") {
           $scope.location.altBrokerUs = select.id;
        }else if (type == "AltCA") {
           $scope.location.altBrokerCa = select.id;
        }else if (type == "NAT") {
           $scope.location.brokerNat= select.id;
        }
    }

    // Entity Location
    var locationId = null;
    $scope.location = {};
    $scope.countryOption = [];
    $scope.locationProvinceOption = [];
    $scope.getCountry = function () {
        $http({
            method: 'GET',
            url: "customer/customerManager/getCountry.do",
            params: {}
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.countryOption = response.data.records;
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    }
    $scope.getCountry();
    $scope.getProvinceByCountry = function (country, type) {
        if (type == 1) $scope.locationProvinceOption = [];
        else $scope.contactProvinceOption = [];
        if (!country) return;
        $http({
            method: 'GET',
            url: "customer/customerManager/getProvinceByCountry.do",
            params: {
                "country": country
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            if (type == 1) $scope.locationProvinceOption = response.data.records;
            else $scope.contactProvinceOption = response.data.records;
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        })
    }
    $scope.$watch('location.country',
    function () {
        $scope.getProvinceByCountry($scope.location.country, 1);
    });
    $scope.getLocation = function (id) {
        locationId = id;
        $scope.location = {};
        $http({
            method: 'GET',
            url: "customer/customerManager/selectCustomerLocationById.do",
            params: {
                "locationId": id
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.location = response.data.records;
            $scope.BillingCompany = response.data.billingCompany;
            $scope.USBroker = response.data.brokerUs;
            $scope.CABroker = response.data.brokerCa;
            $scope.AltUSBroker = response.data.altBrokerUs;
            $scope.AltCABroker = response.data.altBrokerCa;
            $scope.NATBroker = response.data.brokerNat;

        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    };

    $scope.submitLocation = function (isValid) {
        if (editType != "Location") return;
        if (isValid && $scope.location) {
            if ($scope.location.id) {
                $http({
                    method: 'GET',
                    url: "customer/customerManager/updateCustomerLocation.do",
                    params: $scope.location
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    destroyTree();
                    loadTree(locationId);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            } else {
                $http({
                    method: 'GET',
                    url: "customer/customerManager/createCustomerLocation.do",
                    params: $scope.location
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    destroyTree();
                    loadTree(response.data.records);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            }
        }
    }

    $scope.codeChange = function () {
        if ($scope.location && (!$scope.location.code || $scope.location.code.length == 0)) {
            var code = "";
            if ($scope.location.quickName && $scope.location.quickName.length > 0) {
                code = $scope.location.quickName.substring(0, 5);
            }
            if (code.length > 0 && $scope.location.city && $scope.location.city.length > 0) {
                code += $scope.location.city;
                $scope.location.code = code;
            }
        }
    }

    // Entity Contact
    var contactId = null;
    $scope.contact = {};
    $scope.contactProvinceOption = [];
    $scope.$watch('contact.country',
    function () {
        $scope.getProvinceByCountry($scope.contact.country, 2);
    });
    //$scope.getEpCodeData("Contact Type");
    $scope.getContact = function (id) {
        contactId = id;
        $scope.contact = {};
        $http({
            method: 'GET',
            url: "customer/customerManager/selectCustomerContactById.do",
            params: {
                "contactId": id
            }
        }).then(function successCallback(response) {
            if (response.data.result != 'success') {
                toaster.pop('error', '', response.data.msg);
                return;
            }
            $scope.contact = response.data.records;
            if ($scope.contact) {
                $scope.VisaList = $scope.contact.courierVisa;
            }
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    };
    $scope.submitContact = function (isValid) {
        if (editType != "Contact") return;
        if (isValid && $scope.contact) {
            if ($scope.contact.id) {
                if ($scope.VisaList) {
                    var list = $.map($scope.VisaList, function (item) {
                        item.validFrom = $filter('date')(item.validFrom, 'yyyy-MM-dd') || item.validFrom;
                        item.validTo = $filter('date')(item.validTo, 'yyyy-MM-dd') || item.validTo;
                        return item;
                    });
                    $scope.contact.courierVisa = JSON.stringify(list);
                }
                $http({
                    method: 'GET',
                    url: "customer/customerManager/updateCustomerContact.do",
                    params: $scope.contact
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    destroyTree();
                    loadTree(contactId);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            } else {
                $http({
                    method: 'GET',
                    url: "customer/customerManager/createCustomerContact.do",
                    params: $scope.contact
                }).then(function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    toaster.pop('success', '', "Save successfully");
                    destroyTree();
                    loadTree(response.data.records);
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            }
        }
    }

    // Tree
    var zTreeObj, treeData = [], clickTimer,
    nodeClick = function (event, treeId, treeNode, clickFlag) {
        if (treeNode != null) {
            if (treeNode.nodeType == "M") {
                editType = "Entity";
                //$scope.getEntity(entityId);
                $scope.location = {};
                $scope.contact = {};
                $scope.showBox = 1;
            } else if (treeNode.nodeType == "L") {
                editType = "Location";
                $scope.getLocation(treeNode.id);
                $scope.contact = {};
                $scope.showBox = 2;
            } else if (treeNode.nodeType == "C") {
                editType = "Contact";
                $scope.location = {};
                $scope.getContact(treeNode.id);
                $scope.showBox = 3;
            }
            $scope.$apply();
        }
    },
    setting = {
        view: {
            showLine: true,
            selectedMulti: false,
            dblClickExpand: false
        },
        data: {
            key: {
                title: "tip",
                name: "name"
            },
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "parentId",
            }
        },
        callback: {
            onClick: nodeClick
        }
    },
    loadTree = function (selectId) {
        $http({
            method: 'GET',
            url: "customer/customerManager/loadCustomerTreeData.do",
            params: $scope.TreeOption
        }).then(function successCallback(response) {
            treeData = [];
            if (response.data != null && response.data != "" && response.data.result == "success") {
                //treeData = response.data.records;
                treeData = $.map(response.data.records, function (item) {
                    if (item.nodeType == "M") {
                        item.iconSkin = "EIcon";
                        item.tip = item.name;
                    }
                    else if (item.nodeType == "L") {
                        item.iconSkin = "LIcon";
                        item.tip = item.name;
                    }
                    else if (item.nodeType == "C") {
                        if ($scope.IsCouriers) {
                            item.iconSkin = item.isOnline == 1 ? "GIcon" : "CIcon";
                        } else {
                            item.iconSkin = "None";
                        }
                        item.tip = item.isOnline == 1 ? "Available" : "Not Available";
                    }
                    return item;
                });
                $.fn.zTree.init($("#dataTree"), setting, treeData);
                zTreeObj = $.fn.zTree.getZTreeObj("dataTree");
                zTreeObj.expandAll(true);
                var curTreeNode = zTreeObj.getNodes()[0];
                if (selectId) {
                    curTreeNode = zTreeObj.getNodeByParam("id", selectId);
                }
                zTreeObj.selectNode(curTreeNode);
                $timeout.cancel(clickTimer);
                clickTimer = $timeout(function () { nodeClick(null, curTreeNode.id, curTreeNode); }, 500);
            }
        },
        function errorCallback() {
            $scope.isLoading = false;
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    },
    destroyTree = function () {
        $.fn.zTree.destroy("dataTree");
    }
    $scope.addNode = function () {
        var nodes = zTreeObj.getSelectedNodes();
        if (!nodes || nodes.length == 0) return;
        var treeNode = nodes[0];
        if (treeNode.nodeType == "M") {
            editType = "Location";
            $scope.location = {
                customerId: $scope.entity.id,
                status: "Active"
            };
            $scope.contact = {};
            $scope.showBox = 2;
        } else if (treeNode.nodeType == "L") {
            editType = "Contact";
            $scope.location = {};
            $scope.contact = {
                locationId: treeNode.id
            };
            $scope.showBox = 3;
        } else if (treeNode.nodeType == "C") {
            editType = "Contact";
            $scope.location = {};
            $scope.contact = {
                locationId: treeNode.parentId
            };
            $scope.showBox = 3;
        }
    }
    $scope.removeNode = function () {
        var nodes = zTreeObj.getSelectedNodes();
        if (!nodes || nodes.length == 0) return;
        var treeNode = nodes[0];
        if (treeNode.nodeType == "M") {
            toaster.pop('warning', '', "Please select the Location Node Or Contact Node!");
            return;
        }
        if (confirm('Are you sure you want to delete this record?')) {
            $http({
                method: 'GET',
                url: treeNode.nodeType == "L" ? 'customer/customerManager/deleteCustomerLocation.do' : 'customer/customerManager/deleteCustomerContact.do',
                params: {
                    "ids": treeNode.id
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                toaster.pop('success', '', "Delete successfully");
                destroyTree();
                loadTree($scope.entity.id);
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
        }
    }
    $scope.searchTree = function () {
        loadTree();
    }

    $scope.getEntity(entityId);


    //Visa List
    var visaModal = $("#visaModal"), visaIndex = -1;
    $scope.VisaList = [];
    $scope.Visa = {};
    visaModal.modal({ "backdrop": "static", keyboard: true, show: false }).on('hidden.bs.modal', function (e) {
    });
    $scope.addVisa = function () {
        $scope.Visa = {}
        visaIndex = -1;
        visaModal.modal("show");
    }
    $scope.editVisa = function (index, item) {
        $scope.Visa = {
            id: item.id,
            name: item.name,
            country: item.country,
            passportNo: item.passportNo,
            visaNo: item.visaNo,
            nationality: item.nationality,
            validFrom: $filter('date')(item.validFrom, 'yyyy-MM-dd') || item.validFrom,
            validTo: $filter('date')(item.validTo, 'yyyy-MM-dd') || item.validTo,
            sector: item.sector,
            note: item.note
        };
        visaIndex = index;
        visaModal.modal("show");
    }
    $scope.removeVisa = function (index) {
        $scope.VisaList.splice(index, 1);
    }
    $scope.submitVisa = function (isValid) {
        if (isValid && $scope.Visa) {
            if (!$scope.VisaList)
                $scope.VisaList = [];

            $.each($scope.countryOption,function(index,item){
                if(item.id == $scope.Visa.country){
                    $scope.Visa.countryName  = item.name;
                    return false;
                }
            });

            if (visaIndex == -1) {
                $scope.VisaList.push($scope.Visa);
            }
            else {
                $scope.VisaList[visaIndex] = $scope.Visa;
            }
            visaModal.modal("hide");
        }
    }



    $scope.btnCancel = function () {
        if(tag == "from_edit_courier")
        {
            $state.go("app.operation_console.courier_list");
        }else{
            $state.go("app.system_maintenance.entity_list");
        }
    }

}]);
app.directive('ngConfirmClick', [
    function () {
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click', function () {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        }
 }]);