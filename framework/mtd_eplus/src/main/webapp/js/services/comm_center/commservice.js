app.service("commService", function ($http, $q, $filter, $localStorage) {
    this.getAllEmails = function () {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/retrieveAllUnlinkedEmails.do"
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.getAllEmailsByDeptIds = function (deptIds) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/retrieveAllUnlinkedEmailsByDepts.do",
            params: {
                departmentIds: deptIds
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.updateLink = function (emailId, tripId, tag) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/updateEmailLink.do",
            params: {
                emailId: emailId,
                tripRefNumber: tripId,
                tag: tag
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.viewEmail = function (emailId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/retrieveEmail.do",
            params: {
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.removeEmail = function (emailId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/removeEmail.do",
            params: {
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.unremoveEmail = function (emailId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/unremoveEmail.do",
            params: {
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.removeEmails = function (emailIds) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/removeEmails.do",
            params: {
                emailIds: emailIds
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.markEmail = function (emailId, tag) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/markEmail.do",
            params: {
                tag: tag,
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.markLabel = function (emailId, label) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/markLabel.do",
            params: {
                label: label,
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.processEmail = function (emailId, tag) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/processEmail.do",
            params: {
                tag: tag,
                emailId: emailId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.buildReplyFooter = function (email) {
        var fullname = $localStorage.user.fullName || "";
        var signature = $localStorage.user.signature || "";
        var footer =
            "<br/><br/><br/>"
            + "------------------"
            + "<br/>"
            + fullname
            + "<br/><br/><br/>"
            + signature
            + "<br/><br/><br/>"
            + "--------Original--------" + "<br/>"
            + "<b>From:</b>" + email.mailFrom + "<br/>"
            + "<b>Send time:</b>" + $filter('date')(email.created, 'yyyy-MM-dd HH:mm') + "<br/>"
            + "<b>To:</b>" + email.mailTo + "<br/>"
            + "<b>Subject:</b>" + email.subject + "<br/><br/><br/>"
            + email.content;
        return footer;
    }
    this.buildSignare = function () {
        var fullname = $localStorage.user.fullName || "";
        var signature = $localStorage.user.signature || "";
        var footer =
            "<br/><br/><br/>"
            + "------------------"
            + "<br/>"
            + fullname
            + "<br/><br/><br/>"
            + signature;
        return footer;
    }
    this.getEmailsByTrip = function (tripId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/retrieveEmailsByTrip.do",
            params: {
                tripId: tripId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.searchEmails = function (tripId, dateFrom, dateTo, searchKey, searchLabel, includeDelete) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/searchEmails.do",
            params: {
                tripId: tripId,
                dateFrom: dateFrom,
                dateTo: dateTo,
                searchKey: searchKey,
                searchLabel: searchLabel,
                includeDelete: includeDelete ? "Y": ""
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.getEmailsByConditions = function (dateFrom, dateTo, searchKey, deptIds, searchLabel, includeDelete, includeProcessed, includeOut) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/retrieveAllUnlinkedEmailsByConditions.do",
            params: {
                searchLabel: searchLabel,
                dateFrom: dateFrom,
                dateTo: dateTo,
                searchKey: searchKey,
                includeDelete: includeDelete ? "Y": "",
                includeProcessed: includeProcessed ? "Y": "",
                includeOut: includeOut ? "Y" : "",
                departmentIds: deptIds
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.copyAttach = function (docId, newName, newType) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/copyAttach.do",
            params: {
                docId: docId,
                newName: newName,
                newType: newType
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.sendEmail = function (email, tag, action) {
        var emailId = email.id || "";
        var tripId = email.tripId || "";
        var subject = email.subject || "";
        var content = email.content || "";
        var mailFrom = email.mailFrom || "";
        var mailTo = email.mailTo || "";
        var mailCc = email.mailCc || "";
        var mailBcc = email.mailBcc || "";
        var attachIds = email.attachIds || "";
        var processedStatus = email.processedStatus || "";
        var departmentId = email.departmentId || "";
        var defer = $q.defer();
        $("#sendMailLoading").show();
        $http({
            method: 'POST',
            url: "operationconsole/commcenter/sendEmail.do",
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            data: $.param({
                tripId: tripId,
                subject: subject,
                content: content,
                mailFrom: mailFrom,
                mailTo: mailTo,
                mailCc: mailCc,
                mailBcc: mailBcc,
                attachIds: attachIds,
                processedStatus: processedStatus,
                departmentId: departmentId,
                emailId: emailId,
                tag: tag,
                action: action
            })
        }).success(function (data) {
            $("#sendMailLoading").hide();
            defer.resolve(data);
        }).error(function (data) {
            $("#sendMailLoading").hide();
            defer.reject(data);
        });
        return defer.promise;
    };
    this.removeCommaFromEmailAddress = function (emailStr) {
        var execObj = (emailStr).match(new RegExp('\"([^\"]*)\"','gi'));
        if(execObj && execObj.length > 0){
            $.each(execObj,function (i,item) {
                emailStr = emailStr.replace(item, item.replace(/,/gi, ""));
            });
        };
        return emailStr;
    }

    this.formatEmailAddress = function (emailStr) {
        var returnStr = [];
        emailStr = this.removeCommaFromEmailAddress(emailStr);
        var emailArray = emailStr.split(";");
        if (emailStr.contains(","))
            emailArray = emailStr.split(",");

        emailArray.forEach(function (item, index) {
            if (typeof (item) == "string" && item.length > 3) {
                var email = "";
                if (item.indexOf('"') != -1) {
                    var array = item.split("\"");
                    if (array.length == 3) {
                        email = array[2].toString().replace(/(^\s*)/g, "");
                    }
                } else {
                    var array = item.split("<");
                    if (array.length == 2) {
                        email = array[1].toString().replace(/(^\s*)/g, "");
                    } else if (array.length == 1) {
                        email = array[0].toString().replace(/(^\s*)/g, "");
                    }
                }
                if (email!=""){
                    email = email.replace(/(\s*$)/g, "");
                    email = email.replace(/(\>*)/g, "");
                    email = email.replace(/(\<*)/g, "");
                    returnStr.push(email);
                }
            }
        });
        return returnStr.join(",");
    };
    this.formatEmailContent = function (emailContent) {
        var returnStr = "";
        if(!emailContent || emailContent.length == 0) returnStr;

        var emailContent = emailContent.replace(/<!--\[if\s+gte\s+mso\s+9\]>(?:(?!<!\[endif\]-->)[\s\S])*<!\[endif\]-->/gi, "");
        emailContent = emailContent.replace(/<!\[if !supportLists\]\>/gi, "");
        emailContent = emailContent.replace(/<!\[endif\]\>/gi, "");
        emailContent = emailContent.replace(/&middot;/gi, "&#8226;");

        var execObj = /<body[^>]*>([\s\S]*)<\/body>/gi.exec(emailContent);
        if(execObj == null || execObj == undefined || execObj.length < 1){
            execObj = /<html[^>]*>([\s\S]*)<\/html>/gi.exec(emailContent);
        }
        if(execObj && execObj.length > 1){
            returnStr = "<div>"+execObj[1]+"</div>";
            returnStr = returnStr.replace(/<br>/gi, "")
            returnStr = returnStr.replace(/<br\/>/gi, "")
        }
        if(returnStr.length > 0){
            execObj = (emailContent).match(new RegExp('<style.*?>(.*?)<\/style>','gi'));
            if(execObj && execObj.length > 0){
                $.each(execObj,function (i,item) {
                    var style = item;
                    style = style.replace(/<br>/gi, "")
                    style = style.replace(/<br\/>/gi, "")
                    returnStr = style + returnStr;
                });
            }
        }else{
            emailContent = $.trim(emailContent);
            if(emailContent.substring(0,1) != "<")
                returnStr = "<div>"+ emailContent +"</div>";
            else
                returnStr = emailContent;
        }

        return returnStr;
    };
})