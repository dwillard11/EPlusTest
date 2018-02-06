app.controller('commctrl', ['$scope', '$window', 'commService', 'toaster', '$localStorage', '$modal', 'deptService', '$timeout', 'valutils', 'codeService', '$filter',
    function ($scope, $window, commService, toaster, $localStorage, $modal, deptService, $timeout, valutils, codeService, $filter) {
        $scope.getDepartmentDropDownList = function () {
            $scope.itemsByPage = 20;
            $scope.selectedDeptIds = [];
            $scope.selectedDeptName = [];
            deptService.getCurrentUserDepts().then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }

                $scope.departmentOptions = res.records;
                $scope.selectedDepartmentOption = res.records[0];

                if ($window.sessionStorage["commCenterDepartmentIds"]) {
                    $scope.selectedDeptIds = $window.sessionStorage["commCenterDepartmentIds"].split(",");
                }
                else {
                    angular.forEach($scope.departmentOptions, function (data) {
                        $scope.selectedDeptIds.push(data.id);
                        $scope.selectedDeptName.push(data.name);
                    });
                }
                $scope.dateFrom = "";
                $scope.dateTo = "";
                $scope.includeDelete = false;
                $scope.includeProcessed = false;
                $scope.includeOut = false;
                if ($window.sessionStorage["commCenterDateFrom"]) {
                    $scope.dateFrom = $window.sessionStorage["commCenterDateFrom"];
                }
                if ($window.sessionStorage["commCenterDateTo"]) {
                    $scope.dateTo = $window.sessionStorage["commCenterDateTo"];
                }
                if ($window.sessionStorage["commCenterIncludeDelete"]) {
                    $scope.includeDelete = $window.sessionStorage["commCenterIncludeDelete"] == "true";
                }
                if ($window.sessionStorage["commCenterIncludeProcessed"]) {
                    $scope.includeProcessed = $window.sessionStorage["commCenterIncludeProcessed"] == "true";
                }
                if ($window.sessionStorage["commCenterIncludeOut"]) {
                    $scope.includeOut = $window.sessionStorage["commCenterIncludeOut"] == "true";
                }

                $scope.getAllEmailsByDeptIds();
            })
        };

        $scope.getDepartmentDropDownList();

        var timeoutPromise,isRefresh = true;

        function refreshData() {
            if (timeoutPromise)
                $timeout.cancel(timeoutPromise);
            timeoutPromise = $timeout(function () {
                $scope.getAllEmailsByDeptIds();
            }, 30 * 1000);
        }

        $scope.getAllEmailsByDeptIds = function () {
            refreshData();
            if(!isRefresh)return;
            $scope.isLoading = true;
            $scope.searchEmails();
        };

        $("#refreshBox").on("focus blur", ".refresh", function(){
            isRefresh =  !($(this).is(":focus"));
        });


        var updateSelectDept = function (action, id, name) {
            if (action == 'add' && $scope.selectedDeptIds.indexOf(id) == -1) {
                $scope.selectedDeptIds.push(id);
                $scope.selectedDeptName.push(name);
            }
            if (action == 'remove' && $scope.selectedDeptIds.indexOf(id) != -1) {
                var idx = $scope.selectedDeptIds.indexOf(id);
                $scope.selectedDeptIds.splice(idx, 1);
                $scope.selectedDeptName.splice(idx, 1);
            }
            $scope.getAllEmailsByDeptIds();
        };
        $scope.updateSelectDept = function ($event, id) {
            var checkbox = $event.target;
            var action = (checkbox.checked ? 'add' : 'remove');
            updateSelectDept(action, id, checkbox.name);
        };

        $scope.isSelectedDept = function (id) {
            return $scope.selectedDeptIds.indexOf(id) >= 0;
        };
        $scope.$watch('selectedCategory', function () {
            if ($scope.selectedDepartmentOption != undefined && $scope.selectedDepartmentOption != null && $scope.selectedDepartmentOption != '') {
                $scope.getAllEmailsByDeptIds();
            }
        });
        $scope.updateLink = function (emailId, tripId, tag) {
            commService.updateLink(emailId, tripId, tag).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.getAllEmailsByDeptIds();
            });
        };

        $scope.removeEmail = function (emailId) {
            if (confirm('Are you sure you want to delete this record?')) {
                commService.removeEmail(emailId).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.getAllEmailsByDeptIds();
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
                    $scope.getAllEmailsByDeptIds();
                });
            }
        };

        $scope.searchEmails = function () {
            $scope.isLoading = true;
            var deptIds = $scope.selectedDeptIds.toString();
            commService.getEmailsByConditions($scope.dateFrom,
                $scope.dateTo,
                $scope.searchContent || "",
                deptIds,
                $scope.searchLabel,
                $scope.includeDelete,
                $scope.includeProcessed,
                $scope.includeOut).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.msgList = res.records;
                $.each($scope.msgList,function (index,item) {
                    item.mailTo = item.mailTo.replace(/,/g,", ");
                    item.mailTo = item.mailTo.replace(/;/g,", ");
                })

                $window.sessionStorage["commCenterDepartmentIds"] = deptIds;
                $window.sessionStorage["commCenterDateFrom"] = $filter('date')($scope.dateFrom, 'yyyy-MM-dd');
                $window.sessionStorage["commCenterDateTo"] = $filter('date')($scope.dateTo, 'yyyy-MM-dd');
                $window.sessionStorage["commCenterSearchContent"] = $scope.searchContent || "";
                $window.sessionStorage["commCenterIncludeDelete"] = $scope.includeDelete;
                $window.sessionStorage["commCenterIncludeProcessed"] = $scope.includeProcessed;
                $window.sessionStorage["commCenterIncludeOut"] = $scope.includeOut;

                $scope.isLoading = false;
            });
        };
        $scope.checkAllMsgs = false;
        $scope.selectAllMsgs = function () {
            angular.forEach($scope.displayedMsg,
                function (item, key) {
                    $scope.displayedMsg[key].selected = $scope.checkAllMsgs;
                });
        };
        $scope.removeEmails = function () {
            var deletItems = '';
            angular.forEach(
                $scope.displayedMsg,
                function (item, key) {
                    if ($scope.displayedMsg[key].selected) {
                        deletItems += item.id + ",";
                    }
                });
            if (valutils.isEmptyOrUndefined(deletItems)) {
                toaster.pop('error', '', 'Please select at least one item!');
                return;
            }
            if (confirm('Are you sure you want to delete select records?')) {
                commService.removeEmails(deletItems).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    $scope.getAllEmailsByDeptIds();
                });
            }
        };
        $scope.markEmail = function (id, tag) {
            commService.markEmail(id, tag).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                // toaster.pop('success','','Set successfully');
                $scope.getAllEmailsByDeptIds();
            })
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
                $scope.getAllEmailsByDeptIds();
            })
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
                $scope.getAllEmailsByDeptIds();
            }, function () {
                $scope.getAllEmailsByDeptIds();
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

        $scope.sendEmail = function (mail, tripRefNo, tag) {
            if (tag!='edit') {
                $scope.markEmail(mail.id, 'read');
            }
            if (tag == 'reply' || tag == 'replyAll')
            {
                mail.subject =  mail.subject.replace(/\[.*?\]/g, "[" + tripRefNo + "]");
                if (mail.subject.indexOf(tripRefNo) == -1)
                {
                    mail.subject += " [" + tripRefNo + "]";
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
                        return "[" + tripRefNo + "]";
                    }
                }
            });
            modalInstance.result.then(function (result) {
                $scope.getAllEmailsByDeptIds();
            }, function () {
                $scope.getAllEmailsByDeptIds();
            });
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
                $scope.getAllEmailsByDeptIds();
            }, function () {
                $scope.getAllEmailsByDeptIds();
            });
        };
        $scope.processEmail = function (id, tag) {
            if (confirm('Mark this email as processed?')) {
                commService.processEmail(id, tag).then(function (res) {
                    if (res.result != 'success') {
                        toaster.pop('error', '', res.msg);
                        return;
                    }
                    toaster.pop('success', '', 'Set successfully');
                    $scope.getAllEmailsByDeptIds();
                });
            }
        };

        $scope.$on('$destroy', function () {
            if (timeoutPromise)
                $timeout.cancel(timeoutPromise);
        })
    }
]);
