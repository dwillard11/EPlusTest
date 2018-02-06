app.service("deptService", function ($http, $q) {
    this.getCurrentUserDepts = function () {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/getCurrentUserDepts.do",
            params: {
                foobar: new Date().getTime()
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };

    this.getDeptInfo = function (deptid) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "maintenance/departmentManager/retrieveDepartment.do",
            params: {
                "id": deptid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
})