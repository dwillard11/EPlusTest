app.service("codeService", function ($http, $q) {
    this.getEpCodeData = function (type) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/getEpCodeListByType.do",
            params: {
                type: type,
                foobar: new Date().getTime()
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.getEpCodeById = function (id) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/getEpCodeById.do",
            params: {
                id: id,
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