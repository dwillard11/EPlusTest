app.controller('MessageCentralCtrl', [ '$scope', '$http', '$timeout', '$q',
		function($scope, $http, $timeout, $q) {
			$scope.isLoading = false;
			var totalPageNumber = 0;
			var advanceSearch = "";
			var probillNo = "";
			var activity = "";
			var information = "";
			var activityDate = "";
			var activityTime = "";
			
			$scope.callServer = function callServer(tableState) {
				
				advanceSearch = $scope.advanceSearch;
				probillNo = $scope.probillNo;
				activity = $scope.activity;
				information = $scope.information;
				activityDate = $scope.activityDate;
				activityTime = $scope.activityTime;
				$scope.isLoading = true;
				var pagination = tableState.pagination;
				var sortstatus = tableState.sort;
				var sortcondition = sortstatus.predicate || "";
				var sortreverse = sortstatus.reverse || false;
				
              
				var start = pagination.start || 0;
				var number = pagination.number || 10;

				getPage(start, number,sortcondition,sortreverse).then(function(result) {
					$scope.displayed = result.data;
					tableState.pagination.numberOfPages = result.numberOfPages;
					$scope.isLoading = result.fakeLoading;
				});

			};
			function getPage(start, number,sortcondition,sortreverse) {
				var deferred = $q.defer();
				var result = [];
				var totalPageNum = 0;
				$http({
					method : 'GET',
					url : "retrieveMessages.do",
					params : {
						"currentPage" : (start / number) + 1,
						"pageSize" : 10,
						"information" : information,
						"advanceSearch" : advanceSearch,
						"activity" : activity,
						"probillNo" : probillNo,
						"activityDate" : activityDate,
						"activityTime" : activityTime,
						"sortcondition" : sortcondition,
						"sortreverse" : sortreverse
					}
				}).then(function successCallback(response) {
					result = response.data.records;
					totalPageNum = response.data.totalRecord;
				}, function errorCallback(response) {
				});
				$timeout(function() {
					deferred.resolve({
						data : result,
						numberOfPages : totalPageNum,
						fakeLoading : false
					});
				}, 15000);
				return deferred.promise;
			}
			;
		} ]);
