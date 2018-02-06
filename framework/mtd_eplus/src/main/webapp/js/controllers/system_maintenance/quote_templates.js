app.controller('quotetemplatesctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q','$filter','$stateParams','$rootScope', '$window',
    function ($scope, $timeout, $http, $state, toaster, $localStorage, treeConfig, $modal, $q, $filter,$stateParams,$rootScope, $window) {
        if (!$stateParams.name) {
            $stateParams.tripType = $window.sessionStorage["quoteTemplateTripType"];
            $stateParams.name = $window.sessionStorage["quoteTemplateName"];
        }
        var params ={
            tripType :  $stateParams.tripType || "",
            name : $stateParams.name || ""
        }
        $window.sessionStorage["quoteTemplateTripType"] = $stateParams.tripType;
        $window.sessionStorage["quoteTemplateName"] = $stateParams.name;

        $scope.tripOption = [];
        $scope.statusOption = [];

         var curDragNodes = [],settingTree = {
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
                prev: function(treeId, nodes, targetNode) {
                    for (var i = 0,
                    l = curDragNodes.length; i < l; i++) {
                        var curPNode = curDragNodes[i].getParentNode();
                        if (curPNode && curPNode !== targetNode.getParentNode() && curPNode.childOuter === false) {
                            return false;
                        }
                    }
                    return true;
                },
                inner: function(treeId, nodes, targetNode) {
                    for (var i = 0,
                    l = curDragNodes.length; i < l; i++) {
                        if (curDragNodes[i].parentTId && curDragNodes[i].getParentNode() !== targetNode && curDragNodes[i].getParentNode().childOuter === false) {
                            return false;
                        }
                    }
                    return true;
                },
                next: function(treeId, nodes, targetNode) {
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
            onClick: function(event, treeId, treeNode, clickFlag) {
                if (treeNode != null) {
                    $scope.getData(treeNode.entityId);
                }
            },
            beforeDrag: function(treeId, treeNodes) {
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
            onDrop: function(event, treeId, treeNodes, targetNode, isMove) {
                if (targetNode && isMove && treeNodes && treeNodes.length > 0) {
                    var list =[];
                    if(targetNode.getParentNode() != null)
                    {
                        if(targetNode.type == "item"){
                            targetNode.getParentNode().children.forEach(function(item,itemSeq){
                                list.push({ id: item.entityId, categorySeq: -1 , itemSeq: itemSeq});
                            });
                        }else{
                            targetNode.getParentNode().children.forEach(function(category,categorySql)
                            {
                                category.children.forEach(function(item, itemSeq){
                                    list.push({id: item.entityId, categorySeq: categorySql, itemSeq: itemSeq});
                                })
                            });
                        }
                    }
                    updateTree(list);
                }
            }
        }
    },updateTree = function(param){
        $http({
            method: 'POST',
            url: "maintenance/quoteTemplate/saveQuoteTree.do",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: $.param({ 'list':JSON.stringify(param)})
        }).then(function successCallback(response) {
            if (response.data != null && response.data != "" && response.data.result == "success") {
                loadTree();
            }
        },
        function errorCallback() {
            toaster.pop('error', '', "Server error, please contact system administrator");
        });
    },loadTree = function(selectId) {
        $http({
            method: 'GET',
            url: "maintenance/quoteTemplate/getQuoteTree.do",
            params: params
        }).then(function successCallback(response) {
            if (response.data != null && response.data != "" && response.data.result == "success") {
                var treeData = $.map(response.data.records,
                function(item) {
                    if (item.type == "event") {
                        item.childOuter = false;
                        item.drag = false;
                    } else if (item.type == "category") {
                        item.childOuter = false;
                    } else {
                        if(!selectId)
                            selectId = item.id;
                    }
                    return item;
                });
                $.fn.zTree.init($("#dataTree"), settingTree, treeData);
                var treeObj = $.fn.zTree.getZTreeObj("dataTree");
                var curTreeNode = treeObj.getNodes()[0];
                if(selectId)
                {
                    curTreeNode = treeObj.getNodeByParam("id", selectId);
                }
                treeObj.selectNode(curTreeNode);
                $scope.getData(curTreeNode.entityId);



            }else if (response.data != null && response.data != "" && response.data.result == "error"){
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
            if(type!="" && type!=null){
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
                        if(type=="Trip Type"){
                            $scope.tripOption = response.data.records;
                        }else if(type=="Status"){
                            $scope.statusOption = response.data.records;
                        }

                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }

        $scope.getEpCodeData("Trip Type");
        $scope.getEpCodeData("Status");

        $scope.getData = function (eId) {
            if(eId!="" && eId!=null && eId>0){
                $http({
                    method: 'GET',
                    url: "maintenance/quoteTemplate/retrieveQuoteTemplate.do",
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
                        $scope.type = response.data.records.type;
                        $scope.status = response.data.records.status;
                        $scope.name = response.data.records.name;
                        $scope.category = response.data.records.category;
                        $scope.sequence = response.data.records.sequence;
                        $scope.item = response.data.records.item;
                        $scope.description = response.data.records.description;
                        $scope.status = response.data.records.status;
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
        $scope.submit = function (isValid) {
            var method = "";
            if (!isValid){
                $scope.submitted = true;
            }else {
                if (null != $scope.id && "" != $scope.id && undefined != $scope.id) {
                    method = "maintenance/quoteTemplate/updateQuoteTemplate.do";
                } else {
                    method = "maintenance/quoteTemplate/addQuoteTemplate.do";
                }
                $http({
                    method: 'POST',
                    url: method,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    data: $.param( {
                        "id": $scope.id,
                        "name": $scope.name || "",
                        "type": $scope.type || "",
                        "category": $scope.category || "",
                        "sequence": $scope.sequence || "",
                        "item": $scope.item || "",
                        "description": $scope.description || "",
                        "status": $scope.status
                    })
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success' || !response.data.records) {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Save successfully");
                        loadTree(response.data.records + 100000000);
                        //$state.go("app.system_maintenance.quote_templates");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }

        $scope.addQuote = function () {
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
                $scope.category = "";
                $scope.name = "";
                $scope.type = "";
            }else{
                $scope.type = nodes[0].TripType;
                $scope.category = nodes[0].Category;
                $scope.name = nodes[0].EventName;
            }
            $scope.id = 0;
            $scope.status = "";
            $scope.sequence = "";
            $scope.item = "";
            $scope.description = "";
            $scope.status = "Active";
            $scope.currentCompany = "";
            $scope.updatedBy = "";
            $scope.currentCustomer = "";
            $scope.updateTime = "";
        }

        $scope.remove = function () {
            var treeObj = $.fn.zTree.getZTreeObj("dataTree");
            var nodes = treeObj.getSelectedNodes();
            if (null == nodes[0].entityId || "" == nodes[0].entityId || undefined == nodes[0].entityId) {
                return;
            }
            if(nodes[0].type !="item"){
                toaster.pop('warning', '', "Please select the Quote Template!");
                return;
            }
            if (confirm('Are you sure you want to delete this record?')) {
                $http({
                    method: 'GET',
                    url: 'maintenance/quoteTemplate/removeQuoteTemplate.do',
                    params: {
                        "id": nodes[0].entityId
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        toaster.pop('success', '', "Delete successfully");
                        loadTree();
                        $state.go("app.system_maintenance.quote_templates");
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }

        $scope.btnCancel = function ()
        {
            $state.go("app.system_maintenance.quote_template_list");
        }
    }])
