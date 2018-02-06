app.controller('viewEmailCtrl', ['$scope', '$modal','commService', 'codeService','toaster', '$modalInstance', 'emailId','canReply',
    function ($scope, $modal,commService,codeService, toaster, $modalInstance, emailId,canReply) {
        $scope.canReply = canReply;
        $scope.ueditorConfig = {toolbars: [], readonly: true};

        commService.viewEmail(emailId).then(function (res) {
            if (res.result != 'success') {
                toaster.pop('error', '', res.msg);
                return;
            }
            var mail = res.records;
            mail.content=commService.formatEmailContent(mail.content);
            $scope.mail = {
                tripId: mail.tripId || "",
                subject: mail.subject || "",
                content: mail.content || "",
                mailFrom: mail.mailFrom || "",
                mailTo: mail.mailTo || "",
                mailCc: mail.mailCc || "",
                mailBcc: mail.mailBcc || "",
                attachments: mail.attachments || {},
                attachIds: "",
                created: mail.created || "",
                readStatus: mail.readStatus,
                type: mail.type,
                id: emailId,
                processedStatus: mail.processedStatus,
                departmentId: mail.departmentId,
                label: mail.label
            };
        });
        $scope.markLabel = function(id, label) {
            commService.markLabel(id, label).then(function (res) {
                if (res.result != 'success') {
                    toaster.pop('error','',res.msg);
                    return;
                }
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
        $scope.downloadFile = function (id) {
            window.location.href = "downloaddocument?id=" + id;
        };
        $scope.ok = function () {
            $modalInstance.close();
        };
        $scope.sendEmail = function (mail, tag) {
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
                $scope.ok();
            }, function () {
            });
        };
        $scope.print = function () {
            var frames = $("#ueditorBody").find("iframe");
            if(frames.length > 0)
                frames[0].contentWindow.print();
        };
    }]);