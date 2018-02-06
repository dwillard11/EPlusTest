app.controller('termsandconditionstemplatesctrl', ['$scope', '$timeout', '$http', '$state', 'toaster', '$localStorage', 'treeConfig', '$modal', '$q',
    function ($scope, $timeout, $http, $state, toaster, $localStorage, treeConfig, $modal, $q) {
        $scope.remove = function (scope) {
            scope.remove();
        };

        $scope.newSubItem = function (scope) {
            var nodeData = scope.$modelValue;
            nodeData.nodes.push({
                id: nodeData.id * 10 + nodeData.nodes.length,
                title: nodeData.title + '.' + (nodeData.nodes.length + 1),
                nodes: []
            });
        };
        $scope.editSubItem = function (scope) {
            var nodeData = scope.$modelValue;
            var param = {
                nodeData: nodeData
            };
            $state.go('app.system_maintenance.terms_conditions_template_edit', param);

        };
        $scope.visible = function (item) {
            return !($scope.query && $scope.query.length > 0
            && item.title.indexOf($scope.query) == -1);

        };

        $scope.findNodes = function () {

        };

        $scope.data = [
            {
                "id": 01,
                "title": "Hand Carry Service",
                "nodes": [
                    {
                        "id": 011,
                        "title": "DTA",
                        "nodes": []
                    },
                    {
                        "id": 012,
                        "title": "Excess Baggage: Charged at cost + 10%",
                        "nodes": []
                    },
                    {
                        "id": 013,
                        "title": "SELF DECLARATION: If courier required to pay taxes on arrival a service fee will apply + all outlay charges",
                        "nodes": []
                    },
                    {
                        "id": 014,
                        "title": "Iata Baggage Restrictions: Shipper/Consignee providing necessary customs documents",
                        "nodes": []
                    }
                ]
            },
            {
                "id": 02,
                "title": "Expedite Ground",
                "nodes": []
            },
            {
                "id": 03,
                "title": "Air Charter",
                "nodes": []
            },
            {
                "id": 04,
                "title": "Express Air Freight",
                "nodes": []
            }
        ];
    }])
