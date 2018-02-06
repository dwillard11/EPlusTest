app.controller('quotebuildertrl', ['$scope', '$rootScope', '$window', '$timeout', '$http', '$state', 'toaster', '$localStorage', '$location', '$stateParams', '$filter','valutils',
    function ($scope, $rootScope, $window, $timeout, $http, $state, toaster, $localStorage, $location, $stateParams, $filter,valutils) {
        // get parameters from other page
        $scope.quoteid = $stateParams.quoteid;
        $scope.Table = {
            check: false,
            rowCollectionPage: [],
            displayedCollection: [],
            selectAllItems: function () {
                angular.forEach($scope.Table.displayedCollection,
                    function (item, key) {
                        $scope.Table.displayedCollection[key].selected = $scope.Table.check;
                    });
            }
        };

        $scope.getEpCodeData = function (type) {
            if (type != "" && type != null) {
                $http({
                    method: 'GET',
                    url: "maintenance/quoteTemplate/getEpCodeListByType.do",
                    params: {
                        type: type,
                        footer: new Date().getTime()
                    }
                }).then(
                    function successCallback(response) {
                        if (response.data.result != 'success') {
                            toaster.pop('error', '', response.data.msg);
                            return;
                        }
                        if (type == "Currency") {
                            // $scope.currency = response.data.records[0];
                            $scope.currencyOption = response.data.records;
                        }
                    },
                    function errorCallback() {
                        toaster.pop('error', '', "Server error, please contact system administrator");
                    });
            }
        }
        $scope.getEpCodeData("Currency");

        $scope.Info = {}
        $scope.TreeList = [];
        (function loadQuote() {
            $scope.Info = {};
            toaster.pop('wait', '', 'Loading...');
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/loadQuotePdfData.do",
                params: {
                    "tripid": $scope.quoteid
                }
            }).then(function successCallback(response) {
                if (response.data.result != 'success') {
                    toaster.pop('error', '', response.data.msg);
                    return;
                }
                if (response.data.records) {
                    $scope.Info = response.data.records;
                    if ($scope.Info.readyTime) {
                        $scope.Info.readyTimeStr = $filter('date')($scope.Info.readyTime, 'yyyy-MM-dd HH:mm');
                    } else {
                        $scope.Info.readyTimeStr = $filter('date')(new Date(), 'yyyy-MM-dd HH:mm');
                    }
                    if ($scope.Info.expireDate) {
                        $scope.Info.expireDateStr = $filter('date')($scope.Info.expireDate, 'yyyy-MM-dd HH:mm');

                    } else {
                        $scope.Info.expireDateStr = $filter('date')(new Date(), 'yyyy-MM-dd HH:mm');
                    }

                    if (!$scope.Info.quoteCurrency) {
                        $scope.Info.quoteCurrency = $localStorage.user.defaultCurrency;
                    }

                    $scope.Table.rowCollectionPage = $scope.Info.freights;
                    angular.forEach($scope.Table.rowCollectionPage,
                        function (item, key) {
                            item.selected = true;
                        });
                }
                $scope.TreeList = [];
                if (response.data.TERM_CONDITIONS && response.data.TERM_CONDITIONS.length > 0) {
                    var cTitle = "";
                    angular.forEach(response.data.TERM_CONDITIONS,
                        function (item, key) {
                            if (item.category != cTitle) {
                                var category = {
                                    Title: item.category,
                                    List: [{Id: item.id, Title: item.item, Desc: item.description}]
                                }
                                cTitle = item.category;
                                $scope.TreeList.push(category);
                            } else {
                                $scope.TreeList[$scope.TreeList.length - 1].List.push(
                                    {Id: item.id, Title: item.item, Desc: item.description}
                                );
                            }
                        });
                }
            }, function errorCallback() {
                toaster.pop('error', '', "Server error, please contact system administrator");
            })
        }());
        $scope.getPDF = function (tripid, quoteNum, version, expireDate, refId, quoteCurrency) {
            var protocol = $location.protocol();
            var host = $location.host();
            var port = $location.port();
            var url = protocol + "://" + host + ":" + port;
            var termsList = [];
            if ($scope.TreeList) {
                angular.forEach($scope.TreeList, function (item, key) {
                    var title = item.Title;
                    for (var j in item.List) {
                        var model = item.List[j];
                        termsList.push({
                            id: model.Id,
                            title:title,
                            desc: model.Desc
                        });
                    }
                });
            }
            var selectedFreights = [];
            angular.forEach(
                $scope.Table.displayedCollection,
                function (item, key) {
                    if ($scope.Table.displayedCollection[key].selected) {
                        selectedFreights.push(item);
                    }
                });
            var cost = $filter('number')(valutils.trimToEmpty($scope.Info.quoteTotal || 0), 2) + " " + $scope.Info.quoteCurrency;

            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/getQuoteBuildPDF.do",
                params: {
                    "tripId": tripid,
                    "quoteNum": quoteNum,
                    "version": version,
                    "expireDate": expireDate,
                    "refId": refId,
                    "quoteCurrency": quoteCurrency,
                    "url": url,

                    "readyTime": $scope.Info.readyTimeStr,
                    "pickupCity": $scope.Info.pickupCity,
                    "destination": $scope.Info.destinationCity,
                    "cost": cost,
                    "duties": $scope.Info.dutiesTax,
                    "schedule":$scope.Info.flightSchedule,
                    "termsList": JSON.stringify(termsList),
                    "typeDesp": $scope.Info.typeDesp,
                    "freights": JSON.stringify(selectedFreights)

                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    //$window.open(response.data.records, '_blank');
                    toaster.pop('success', '', "save succuessfully!");
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
            // window.location.href = "downloaddocument?tripid=" + $scope.tripid + "&filename=" + filename;
        }
        $scope.savePickup = function (mode) {
            if (!mode) mode = "save";
            var freightids = "";
            angular.forEach(
                $scope.Table.displayedCollection,
                function (item, key) {
                    if ($scope.Table.displayedCollection[key].selected) {
                        freightids += "," + item.id;
                    }
                });
            /*
            2017-12-14 #77: need ability to do quick quotes so please take away check for freight
            if (!freightids || freightids.length == 0) {
                toaster.pop('error', '', "Freight items is empty!");
                return;
            }
            */
            var termsList = [];
            if ($scope.TreeList) {
                angular.forEach($scope.TreeList, function (item, key) {
                    for (var j in item.List) {
                        var model = item.List[j];
                        termsList.push({
                            id: model.Id,
                            desc: model.Desc
                        });
                    }
                });
            }
            $scope.Info.termsList = termsList;
            if ($scope.Info.readyTimeStr) {
                $scope.Info.readyTime = $scope.Info.readyTimeStr;
            }
            if ($scope.Info.expireDateStr) {
                $scope.Info.expireDate = $scope.Info.expireDateStr;
            }
            $http({
                method: 'POST',
                url: "operationconsole/operationconsole/saveQuotePdfData.do",
                params: {
                    tripID: $scope.Info.id,
                    readyTime: $scope.Info.readyTimeStr,
                    expireDate: $scope.Info.expireDateStr,
                    pickupCity: $scope.Info.pickupCity,
                    destinationCity: $scope.Info.destinationCity,
                    quoteTotal: $scope.Info.quoteTotal,
                    dutiesTax: $scope.Info.dutiesTax,
                    flightSchedule: $scope.Info.flightSchedule,
                    quoteCurrency: $scope.Info.quoteCurrency,
                    termsList: JSON.stringify(termsList)
                }
            }).then(
                function successCallback(response) {
                    if (response.data.result != 'success') {
                        toaster.pop('error', '', response.data.msg);
                        return;
                    }
                    $scope.Info.version = $scope.Info.version + 1;

                    $scope.getPDF($scope.Info.id,
                        $scope.Info.quoteRefNo,
                        $scope.Info.version,
                        $scope.Info.expireDateStr,
                        $scope.Info.refId,
                        $scope.Info.quoteCurrency);

                    if (mode == 'save') {
                        $rootScope.back();
                        return;
                    }
                },
                function errorCallback() {
                    toaster.pop('error', '', "Server error, please contact system administrator");
                });
        }

        $scope.btnCancel = function () {
            $rootScope.back();
        }

        $scope.preview = function () {
            $scope.savePickup("preview");
        }

        $scope.getRatesOAG = function () {
            window.open('http://cargoflights.oagcargo.com/');
        }
        $scope.getRatesEgencia = function () {
            window.open('https://www.egencia.com/pub/agent.dll?qscr=logi&&lang=en&lang=en');
        }
    }]);