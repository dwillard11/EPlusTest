'use strict';


app.controller('SigninFormController', ['$scope', '$http', '$state', 'toaster', '$localStorage', function ($scope, $http, $state, toaster, $localStorage) {
    $scope.user = {userId: "", password: ""};
    $scope.authError = null;


    $scope.getLang = function () {
        var langOpt = {
            "en-CA": "english",
            "fr-CA": "french",
            "zh-CN": "chinese",
            "de-DE": "german",
            "es-ES": "spanish"
        };
        var l_lang;
        if (navigator.userLanguage) // Explorer
            l_lang = navigator.userLanguage;
        else if (navigator.language) // FF
            l_lang = navigator.language;
        else
            l_lang = "en-CA";
        if (langOpt[l_lang]) return langOpt[l_lang];
        return "english"
    };
    $scope.login = function () {
        $scope.authError = null;
        $http({
            method: 'GET',
            url: "login.do",

            params: {
                userId: $scope.user.userId,
                password: $scope.user.password,
                lang: $scope.getLang(),
                foobar: new Date().getTime()
            }
        }).then(
            function successCallback(response) {
                if (response.data.result != 'success') {
                    $scope.authError = response.data.msg;
                    return;
                }
                $localStorage.user = response.data.sessionuser;
                if (response.data.sessionuser && response.data.sessionuser.roles&& response.data.sessionuser.roles.length > 0) {
                    var isOperator = false;
                    var isAdmin = false;
                    angular.forEach(response.data.sessionuser.roles,function(role){
                        if (role.name.toLowerCase() == "operator") {
                            isOperator = true;
                        }
                        if (role.name.toLowerCase()== "ep admin") {
                            isAdmin = true;
                        }
                    })
                }
                if (isOperator && !isAdmin){
                    window.location.href = "index.html?login";
                } else {
                    window.location.href = "index.html";
                }

                //$state.go('app.operation_console.operation_center');
            },
            function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            });
    }
}
]);