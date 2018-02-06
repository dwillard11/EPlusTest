'use strict';

/* Controllers */

angular.module('app').controller('AppCtrl', ['$scope', '$http', '$translate', '$localStorage', '$window', '$state',
    function ($scope, $http, $translate, $localStorage, $window, $state) {
        // add 'ie' classes to html
        var isIE = !!navigator.userAgent.match(/MSIE/i);
        if (isIE) {
            angular.element($window.document.body).addClass('ie');
        }
        if (isSmartDevice($window)) {
            angular.element($window.document.body).addClass('smart')
        }
        ;

        // config
        $scope.app = {
            name: 'Angulr',
            version: '2.2.0 2017-11-13',
            // for chart colors
            color: {
                primary: '#7266ba',
                info: '#23b7e5',
                success: '#27c24c',
                warning: '#fad733',
                danger: '#f05050',
                light: '#e8eff0',
                dark: '#3a3f51',
                black: '#1c2b36'
            },
            langShow: {
                "en-CA": "English",
                "fr-CA": "français",
                "zh-CN": "中文（简体）",
                "de-DE": "Deutsche",
                "es-ES": "español"
            },
            langSave: {
                "en-CA": "english",
                "fr-CA": "french",
                "zh-CN": "chinese",
                "de-DE": "german",
                "es-ES": "spanish"
            },
            langKey: {
                 "english":"en-CA",
                 "french":"fr-CA",
                 "chinese":"zh-CN",
                 "german":"de-DE",
                 "spanish":"es-ES"
            },
            settings: {
                themeID: 1,
                navbarHeaderColor: 'bg-black',
                navbarCollapseColor: 'bg-white-only',
                asideColor: 'bg-black',
                headerFixed: true,
                asideFixed: false,
                asideFolded: false,
                asideDock: false,
                container: false
            }
        };

        $.ajax({
            type: "GET",
            url: "relogin.do",
            data: {
                foobar: new Date().getTime()
            },
            success: function (data) {
                    if (data.result == "success") {
                        $localStorage.user = data.sessionuser;
                        if ($localStorage.user) {
                            $scope.selectLang = $localStorage.user.referLanguage || "english";
                        } else {
                            $scope.selectLang = "english";
                        }
                        $scope.selectLangShow = $scope.app.langShow[$scope.app.langKey[$scope.selectLang]] || "English";
                        $translate.use($scope.app.langKey[$scope.selectLang]);
                        $scope.showName = $localStorage.user.fullName;
                        $scope.permission = $localStorage.user.permission;
                        $scope.permittedMenuGroup = $localStorage.user.permittedMenuGroup;
                        $scope.permittedMenu = $localStorage.user.permittedMenu;
                        $scope.$apply();
                    } else {
                        $state.go('access.signin');
                    }
                },
                error: function () {
                    $state.go('access.signin');
                }
            });

        $scope.changeBg = function() {
            $scope.bgWhite = $scope.bgWhite ? "":"background-color:white";
        };
        // save settings to local storage
        if (angular.isDefined($localStorage.settings)) {
            $scope.app.settings = $localStorage.settings;
        } else {
            $localStorage.settings = $scope.app.settings;
        }
        $scope.$watch('app.settings', function () {
            if ($scope.app.settings.asideDock && $scope.app.settings.asideFixed) {
                // aside dock and fixed must set the header fixed.
                $scope.app.settings.headerFixed = true;
            }
            // for box layout, add background image
            $scope.app.settings.container ? angular.element('html').addClass('bg') : angular.element('html').removeClass('bg');
            // save to local storage
            $localStorage.settings = $scope.app.settings;
        }, true);


        $scope.lang = {
            isopen: false
        };
        if ($localStorage.user) {
            $scope.selectLang = $localStorage.user.referLanguage || "english";
        } else {
            $scope.selectLang = "english";
        }
        $scope.selectLangShow = $scope.app.langShow[$scope.app.langKey[$scope.selectLang]] || "English";
        $translate.use($scope.app.langKey[$scope.selectLang]);

        $scope.setLang = function (langKey, $event) {
            $scope.selectLang = $scope.app.langSave[langKey];
            $translate.use(langKey);
            $.ajax({
                type: "GET",
                url: "switchLanguage.do",
                data: {
                    language: $scope.selectLang,
                    foobar: new Date().getTime()
                },
                success: function (data) {
                    if (data.result == "success") {
                        $scope.selectLang = $scope.app.langSave[langKey];
                        $scope.selectLangShow = $scope.app.langShow[$scope.app.langKey[$scope.selectLang]] || "English";
                        $translate.use(langKey);
                        $localStorage.user.referLanguage  = $scope.selectLang;
                    } else {
                        toaster.pop('error', '', 'Can not change language');
                        $scope.$apply();
                    }
                }
             });
        };

        function isSmartDevice($window) {
            // Adapted from http://www.detectmobilebrowsers.com
            var ua = $window['navigator']['userAgent'] || $window['navigator']['vendor'] || $window['opera'];
            // Checks for iOs, Android, Blackberry, Opera Mini, and Windows mobile devices
            return (/iPhone|iPod|iPad|Silk|Android|BlackBerry|Opera Mini|IEMobile/).test(ua);
        }
        var isMenuShow = true;
        $scope.sideBar = function () {
            var arrow = $("#sideBar i");
            var app = $("#app");
            if(isMenuShow){
                app.addClass("hideMenu");
                arrow.removeClass("fa-caret-left");
                arrow.addClass("fa-caret-right");
            }else{
                arrow.removeClass("fa-caret-right");
                arrow.addClass("fa-caret-left");
                app.removeClass("hideMenu");
            }
            isMenuShow = !isMenuShow;
        };
        var isMenuTopShow = true;
        $scope.sideBarTop = function () {
            var arrow = $("#sideBarTop i");
            var app = $("#app");
            if(isMenuTopShow){
                app.addClass("hideMenuTop");
                arrow.removeClass("fa-caret-up");
                arrow.addClass("fa-caret-down");
            }else{
                arrow.removeClass("fa-caret-down");
                arrow.addClass("fa-caret-up");
                app.removeClass("hideMenuTop");
            }
            isMenuTopShow = !isMenuTopShow;
        };
    }]);
app.filter('unique', function () {
    return function (collection, keyname) {
        var output = [],
            keys = [];

        angular.forEach(collection, function (item) {
            var key = item[keyname];
            if (keys.indexOf(key) === -1) {
                keys.push(key);
                output.push(item);
            }
        });

        return output;
    };
});