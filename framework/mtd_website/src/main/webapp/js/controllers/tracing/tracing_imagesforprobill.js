app.controller('ImagesForProbillCtrl', [ '$scope', '$http', '$stateParams', '$localStorage','$state',
		function($scope, $http, $stateParams, $localStorage, $state) {
			
			$scope.formattedProbillNumber = $stateParams.probill.formattedProbillNumber;
			$scope.probillNo = $stateParams.probill.probillNumber;
			$scope.imageGroup = $stateParams.probill.imageGroup;
			
			$scope.backOriginal = function() {
				var param = {
					probill:$stateParams.probill
				}
				$state.go('app.tracing.resultforprobill', param);
			}
			$scope.seeImage = function(imageFileName) {
				var path = "?documentId="+$scope.probillNo+"/"+imageFileName;
				var param = {
					probill:$stateParams.probill,
					path:path
				}
				$state.go('app.tracing.imageview', param);
			}
			$scope.viewAllImages = function(group,probillNo) {
				var path = "?documentId=VirtualDocument:";
				angular.forEach(group,
	                function (item, key) {
						path += probillNo+"/"+group[key].realFilename+",";
	            	}
				);
				// remove the last ,
			    path = path.substring(0, path.length-1);
			    var param = {
						probill:$stateParams.probill,
						path:path
				}
				$state.go('app.tracing.imageview', param);
			}
			
		} ]);