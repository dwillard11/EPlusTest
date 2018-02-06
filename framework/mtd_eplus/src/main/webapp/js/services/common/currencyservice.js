app.service("currencyService", function ($http, $q) {
    this.getUSDRate = function (currencyCode) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/retrieveUSDRate.do",
            params: {
                currencyCode: currencyCode,
                foobar: new Date().getTime()
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
})