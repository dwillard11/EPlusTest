app.service("fileService", function ($http, $q) {
    this.uploadFile = function (file, tripid, fileType) {
        var defer = $q.defer();
        var uploadUrl = "uploaddocument";
        var fd = new FormData();
        fd.append('file', file);
        fd.append('tripid', tripid);
        fd.append('filetype', fileType);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).success(function (response) {
            defer.resolve(response);
        }).error(function (response) {
            defer.reject(response);
        });
        return defer.promise;
    };
    this.deleteFile = function (id) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeFile.do',
            params: {
                id: id
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
})