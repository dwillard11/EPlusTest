app.controller('ImageViewCtrl', [ '$scope', '$http', '$stateParams', '$localStorage','$state',
		function($scope, $http, $stateParams, $localStorage, $state) {
			$scope.formattedProbillNumber = $stateParams.probill.formattedProbillNumber;
			var imagefold = $stateParams.probill.probillNumber;
			var path = $stateParams.path;
			$scope.imageURL = "http://mtdirect.dapasoft.com/VirtualViewerJavaHTML5/index.html"+path;
			$scope.backOriginal = function() {
				var param = {
					probill:$stateParams.probill
				}
				$state.go('app.tracing.imagesforprobill', param);
			}
} ]);

app.filter('trustAsResourceUrl', ['$sce', function($sce) {
    return function(val) {
        return $sce.trustAsResourceUrl(val);
    };
}])