app.controller('editTripEmailCtrl', ['$scope', 'valutils', '$filter', 'commService', 'fileService', 'toaster', '$modalInstance', '$modal', '$log', '$http', 'mail', 'tag',
    function ($scope, valutils, $filter, commService, fileService, toaster, $modalInstance, $modal, $log, $http, mail, tag) {
        var thisSubject = mail.subject;
        $scope.ueditorConfig = UEDITOR_CONFIG;
        $scope.tag = tag;
        if(mail.id > 0) {
            commService.viewEmail(mail.id).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                mail = res.records;
                mail.content=commService.formatEmailContent(mail.content);
                loadData();
            });
        }else{
            loadData();
        }

        function loadData() {
            $scope.mail = {
                tripId: mail.tripId || "",
                subject: thisSubject || "",
                content: mail.content || "",
                mailFrom: mail.mailFrom || "",
                mailTo: mail.mailTo || "",
                mailCc: mail.mailCc || "",
                mailBcc: mail.mailBcc || "",
                attachments: mail.attachments || [],
                attachIds: "",
                created: $filter('date')(mail.created, "yyyy-MM-dd HH:mm")|| "",
                updatedBy: mail.updatedBy|| "",
                readStatus: mail.readStatus,
                type: mail.type,
                id: mail.id,
                processedStatus: mail.processedStatus,
                departmentId: mail.departmentId,
                defaultEmail: mail.defaultEmail
            };

            if (!valutils.isEmptyOrUndefined($scope.mail.mailTo)) {
                $scope.mail.mailTo = commService.formatEmailAddress($scope.mail.mailTo);
            }
            if (!valutils.isEmptyOrUndefined($scope.mail.mailCc)) {
                $scope.mail.mailCc = commService.formatEmailAddress($scope.mail.mailCc);
            }
            if (!valutils.isEmptyOrUndefined($scope.mail.mailBcc)) {
                $scope.mail.mailBcc = commService.formatEmailAddress($scope.mail.mailBcc);
            }
            if (tag == 'reply' || tag == 'replyAll') {
                $scope.mail.id = "";
            }

            if (tag == 'reply') {
                $scope.mail.content = commService.buildReplyFooter($scope.mail);
                $scope.mail.mailTo = $scope.mail.mailFrom;
                $scope.mail.mailCc = "";
                $scope.mail.mailBcc = "";
                $scope.mail.subject = "RE: " + $scope.mail.subject;
            }
            if (tag == 'replyAll') {
                $scope.mail.content = commService.buildReplyFooter($scope.mail);
                if (!valutils.isEmptyOrUndefined($scope.mail.mailTo)) {
                    $scope.mail.mailTo = $scope.mail.mailFrom + "," + $scope.mail.mailTo;
                    if (!valutils.isEmptyOrUndefined($scope.mail.defaultEmail)) {
                        $scope.mail.mailTo = $scope.mail.mailTo.replace(',' + $scope.mail.defaultEmail, '');
                        $scope.mail.mailTo = $scope.mail.mailTo.replace($scope.mail.defaultEmail + ',', '');
                        $scope.mail.mailTo = $scope.mail.mailTo.replace($scope.mail.defaultEmail, '');
                        $scope.mail.mailTo = $scope.mail.mailTo.replace(',,', ',');
                    }
                }else {
                    $scope.mail.mailTo = $scope.mail.mailFrom;
                }
                if (!valutils.isEmptyOrUndefined($scope.mail.mailCc)) {
                    $scope.mail.mailCc = $scope.mail.mailCc;
                    if (!valutils.isEmptyOrUndefined($scope.mail.defaultEmail)) {
                        $scope.mail.mailCc = $scope.mail.mailCc.replace(',' + $scope.mail.defaultEmail, '');
                        $scope.mail.mailCc = $scope.mail.mailCc.replace($scope.mail.defaultEmail + ',', '');
                        $scope.mail.mailCc = $scope.mail.mailCc.replace($scope.mail.defaultEmail, '');
                        $scope.mail.mailCc = $scope.mail.mailCc.replace(',,', ',');
                    }
                }
                $scope.mail.subject = "RE: " + $scope.mail.subject;
            }
        }
        $scope.submitted = false;
        $scope.downloadFile = function (id) {
            window.location.href = "downloaddocument?id=" + id;
        };
        $scope.deleteFile = function (id, index) {
            if (window.confirm("Are you sure you want to delete this record?")) {
                //2017-Nov-08: When composing a new email, if we select uploaded document as attachment then remove it from the email it will also delete the document, please do not delete the document.
                //fileService.deleteFile(id).then(function (res) {
                //    if (!valutils.isEmptyOrUndefined(res) && res.result == "success") {
                        $scope.mail.attachments.splice(index, 1);
                 //   }
                //})
            }
        }
        $scope.uploadAttachment = function () {
            var fi;
            if (document.getElementById('fileinput')) {
                fi = document.getElementById('fileinput').files[0];
            } else {
                return;
            }
            if (fi.name.split('.').pop().toLowerCase() == 'bat' || fi.name.split('.').pop().toLowerCase() == 'exe') {
                toaster.pop('error', '', "error! can not upload exe or bat files");
                return;
            }
            if (fi && fi.size > 10 * 1024 * 1024) {
                toaster.pop('error', '', "Upload failed, Maximum allowed size is 10MB");
                return;
            }
            fileService.uploadFile(fi, $scope.mail.tripId, "Email").then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                $scope.mail.attachIds = $scope.mail.attachIds + "," + res.docId;
                var attach = {id: res.docId, originalFileName: fi.name};
                $scope.mail.attachments.push(attach);
            });
        };
        $scope.ok = function (isValid, action) {
            if (!isValid) {
                $scope.submitted = true;
                return;
            } else {
                // $scope.mail.content = $('#NotesWysiwyg').html();
                $scope.mail.attachIds = "";
                if ($scope.mail.attachments) {
                    angular.forEach($scope.mail.attachments, function (item) {
                        $scope.mail.attachIds = $scope.mail.attachIds + "," + item.id;
                    });
                } else {
                    $scope.mail.attachIds = "";
                }
                $scope.sendEmail($scope.mail, $scope.tag, action);
            }
        };
        $scope.sendEmail = function (mail, tag, action) {
            commService.sendEmail(mail, tag, action).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    return;
                }
                if (action=='send')
                    toaster.pop('success', '', "Send successfully");
                else
                    toaster.pop('success', '', "Save successfully");

                $modalInstance.close();
            });
        };

        $scope.showRefDoc = function (tripId, fileType) {
            var docModal = $modal.open({
                templateUrl: 'tripRefDoc.html',
                controller: 'tripRefDocCtrl',
                size: 'lg',
                resolve: {
                    tripId: function () {
                        return tripId;
                    },
                    fileType: function () {
                        return fileType;
                    }
                }
            });
            docModal.result.then(function (result) {
                angular.forEach(
                    result,
                    function (item, key) {
                        $scope.mail.attachIds = $scope.mail.attachIds + "," + item.id;
                        var attach = {id: item.id, originalFileName: item.originalFileName};
                        $scope.mail.attachments.push(attach);
                    });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
                return;
            });
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.addressEntity = {
            mailCc: "",
            mailBcc: "",
            mailTo: ""
        }
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

        $scope.appendEmail = function (tag, mail) {
            if (tag == 'mailcc') {
                if (mail != null && mail.trim() != '') {
                    if ($scope.mail.mailCc != null && $scope.mail.mailCc.trim() != '') {
                        $scope.mail.mailCc = $scope.mail.mailCc + "," + mail;
                    } else {
                        $scope.mail.mailCc = mail;
                    }
                }
            }
            if (tag == 'mailto') {
                if (mail != null && mail.trim() != '') {
                    if ($scope.mail.mailTo != null && $scope.mail.mailTo.trim() != '') {
                        $scope.mail.mailTo = $scope.mail.mailTo + "," + mail;
                    } else {
                        $scope.mail.mailTo = mail;
                    }
                }
            }
            if (tag == 'mailbcc') {
                if (mail != null && mail.trim() != '') {
                    if ($scope.mail.mailBcc != null && $scope.mail.mailBcc.trim() != '') {
                        $scope.mail.mailBcc = $scope.mail.mailBcc + "," + mail;
                    } else {
                        $scope.mail.mailBcc = mail;
                    }
                }
            }
        }

        $scope.formatEmailAddress = function (mail,tag) {
            mail = commService.formatEmailAddress(mail);
            if (tag == 'mailto')
                $scope.mail.mailTo = mail;
            else if (tag == 'mailcc')
                $scope.mail.mailCc = mail;
            else if (tag == 'mailbcc')
                $scope.mail.mailBcc = mail;
        }
    }]);
app.controller('tripRefDocCtrl', ['$scope', 'valutils', 'commService', 'fileService', 'codeService', 'tripService', 'toaster', '$modalInstance', '$log', '$http', '$modal', 'tripId', 'fileType',
    function ($scope, valutils, commService, fileService, codeService, tripService, toaster, $modalInstance, $log, $http, $modal, tripId, fileType) {
        $scope.loadingdocs = true;
        $scope.loadDocsByType = function (tripId, type) {
            tripService.loadDocsByType(tripId, type).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error', '', res.msg);
                    $scope.loadingdocs = false;
                    return;
                }
                $scope.loadingdocs = false;
                $scope.refDocs = [];
                if (res.records) {
                    angular.forEach(
                        res.records,
                        function (item, key) {
                            //if (item.communicationId == null) {
                                $scope.refDocs.push(item);
                            //}
                        });
                }
                if ($scope.refDocs) {
                    angular.forEach(
                        $scope.refDocs,
                        function (item, key) {
                            $scope.refDocs[key].hasChecked = false;
                        });
                }
            });
        }
        $scope.loadDocsByType(tripId, fileType);

        $scope.attachDocs = function () {
            var checked = [];
            angular.forEach(
                $scope.refDocs,
                function (item, key) {
                    if ($scope.refDocs[key].hasChecked) {
                        checked.push(item);
                    }
                });
            if (checked.length == 0) {
                toaster.pop('error', '', 'please chose at least one item!');
                return;
            } else {
                $modalInstance.close(checked);
            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);