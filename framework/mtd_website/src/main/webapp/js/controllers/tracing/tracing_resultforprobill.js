app.controller('ResultForProbillCtrl', [ '$scope', '$http', '$stateParams', '$localStorage','$state','$location',
		function($scope, $http, $stateParams,$localStorage,$state,$location) {
			var probill = null;
			$scope.isShowBack=true;
			if ($localStorage.isShare = 'Y') {
				$scope.isShowBack = false;
			}
			if ($location.search().isShare == 'Y') {
				$localStorage.isShare = 'Y';
				$scope.isShowBack=false;
				$scope.isShowDetails = false;
		   	    var probillNo = $location.search().id;
			   	$http({
					method : 'GET',
					url : "retrieveProbillByNumber.do",
					params : {
						"probillNo" : probillNo
					}
				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						return;
					}
					probill = response.data.records;
					showCollections(probill);
				}, function errorCallback(response) {
					
				});
		    } else {
		    	$localStorage.isShare = 'N';
		    	$scope.isShowBack=true;
		    	$scope.isShowDetails = false;
		    	probill = $stateParams.probill;
		    	showCollections(probill);
		    }
			
			function showCollections(probill) {
				$scope.probill = probill;
				$scope.showPieces = true;
				$scope.showManifest = true;
				$scope.showImages = true;
				$scope.showCustomer = true;
				if (probill  === "" || probill  === null || typeof probill === "undefined") {
					$scope.showPieces = false;
					$scope.showManifest = false;
					$scope.showImages = false;
					$scope.showCustomer = false;
				} else {
					$scope.rowCollectionPage4 = probill.piecesList;
					if ($scope.rowCollectionPage4  === "" || $scope.rowCollectionPage4  === null || typeof $scope.rowCollectionPage4 === "undefined") {
		                 $scope.showPieces = false;
		            }
					$scope.rowCollectionPage2 = probill.manifestList;
					if ($scope.rowCollectionPage2  === "" || $scope.rowCollectionPage2  === null || typeof $scope.rowCollectionPage2 === "undefined") {
		                 $scope.showManifest = false;
		            }
					$scope.rowCollectionPage3 = probill.images;
					if ($scope.rowCollectionPage3  === "" || $scope.rowCollectionPage3  === null || typeof $scope.rowCollectionPage3 === "undefined") {
		                 $scope.showImages = false;
		            }
					$scope.rowCollectionPage = probill.partnerList;
					if ($scope.rowCollectionPage  === "" || $scope.rowCollectionPage  === null || typeof $scope.rowCollectionPage === "undefined") {
		                 $scope.showCustomer = false;
		            }
				}
				$scope.isShowDetails = true;
				buildShareURL(probill);
			}
			function buildShareURL(probill) {
				var probillNo = probill.probillNumber;
				$http({
					method : 'GET',
					url : "generateShareProbillDetailsURL.do",
					params : {
						"probillNo" : probillNo
					}
				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						return;
					}
					$scope.shareurl = response.data.records;
				}, function errorCallback(response) {
					
				});
			}
			$scope.backOriginal = function() {
				if ($localStorage.original == 'app.tracing.pickupdateresults' || $localStorage.original == 'app.tracing.deliverydateresults') {
					var param = {
						probills:$localStorage.probills,
						beginDate:$localStorage.beginDate,
						endDate: $localStorage.endDate
					}
					$state.go($localStorage.original, param);
					$localStorage.probills = null;
					$localStorage.beginDate = null;
					$localStorage.endDate = null;
					$localStorage.original = null;
				} else if ($localStorage.original == 'app.tracing.probill'){
					$state.go($localStorage.original);
					$localStorage.original = null;
				} else if ($localStorage.original == 'app.tracing.bolshipperpo'){
					$state.go($localStorage.original);
					$localStorage.original = null;
				}  else if ($localStorage.original == 'app.messagecentral.messagedetails'){
					 var param = {
	                      messageId: $localStorage.messageId
	                 };
					$state.go($localStorage.original,param);
					$localStorage.original = null;
					$localStorage.message = null;
				}  else if ($localStorage.original = 'app.dashboard-v1') {
					$state.go($localStorage.original);
					$localStorage.original = null;
				}  else {
					history.back();
				}
			}
			$scope.viewImages = function() {
				var param = {
					probill:$scope.probill
				}
				
				$state.go('app.tracing.imagesforprobill', param);
			}
			
			document.getElementById("copyButton").addEventListener("click", function() {
			    copyToClipboard(document.getElementById("copyTarget"));
			});

			function copyToClipboard(elem) {
				  // create hidden text element, if it doesn't already exist
			    var targetId = "_hiddenCopyText_";
			    var isInput = elem.tagName === "INPUT" || elem.tagName === "TEXTAREA";
			    var origSelectionStart, origSelectionEnd;
			    if (isInput) {
			        // can just use the original source element for the selection and copy
			        target = elem;
			        origSelectionStart = elem.selectionStart;
			        origSelectionEnd = elem.selectionEnd;
			    } else {
			        // must use a temporary form element for the selection and copy
			        target = document.getElementById(targetId);
			        if (!target) {
			            var target = document.createElement("textarea");
			            target.style.position = "absolute";
			            target.style.left = "-9999px";
			            target.style.top = "0";
			            target.id = targetId;
			            document.body.appendChild(target);
			        }
			        target.textContent = elem.textContent;
			    }
			    // select the content
			    var currentFocus = document.activeElement;
			    target.focus();
			    target.setSelectionRange(0, target.value.length);
			    
			    // copy the selection
			    var succeed;
			    try {
			    	  succeed = document.execCommand("copy");
			    } catch(e) {
			        succeed = false;
			    }
			    // restore original focus
			    if (currentFocus && typeof currentFocus.focus === "function") {
			        currentFocus.focus();
			    }
			    
			    if (isInput) {
			        // restore prior selection
			        elem.setSelectionRange(origSelectionStart, origSelectionEnd);
			    } else {
			        // clear temporary content
			        target.textContent = "";
			    }
			    return succeed;
			}
			
		} ]);