app.service("contactService", function ($http, $q) {
    this.loadContact = function (contactid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadContacts.do",
            params: {
                contact: contactid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
})