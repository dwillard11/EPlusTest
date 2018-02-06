app.controller('MessageDetailsCtrl', [ '$scope', '$http', '$stateParams','$localStorage','toaster','$state','$location',
		function($scope, $http, $stateParams,$localStorage,toaster,$state,$location) {
		 var messageId = "";
		 $scope.isShowBack = true;
		 if ($localStorage.isShare = 'Y') {
				$scope.isShowBack=false;
		 }
	     if ($location.search().isShare == 'Y') {
	    	 $localStorage.isShare = 'Y';
	    	 $scope.isShowBack = false;
	    	 messageId = $location.search().id;
	    	 getMessage(messageId);
	    	 buildShareURL(messageId);
	     } else {
	    	 $localStorage.isShare = 'N';
	    	 $scope.isShowBack=true;
	    	 messageId = $stateParams.messageId;
	    	 getMessage(messageId);
	    	 buildShareURL(messageId);
	     }
	     function getMessage(messageId) {
	    	 $scope.isShowDetails = false;
		     
		     $http({
			        method: 'GET',
			        url: "retrieveMessage.do",
			        params: {
			            "messageId": messageId
			        }
			    }).then(
			        function successCallback(response) {
			            if (response.data.result != 'success') {
			                return;
			            }
			            $scope.message = response.data.records;
			            $scope.isShowDetails = true;
			        },
			        function errorCallback(response) {
			        }
			  );
		  }
	      function buildShareURL(messageId) {
				$http({
					method : 'GET',
					url : "generateShareMessageDetailsURL.do",
					params : {
						"messageId" : messageId
					}
				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						return;
					}
					$scope.shareurl = response.data.records;
				}, function errorCallback(response) {
					
				});
			}
			$scope.seeDetails = function(probillNo) {
				toaster.pop('wait', '', 'Loading...');
				$http({
					method : 'GET',
					url : "retrieveProbillByNumber.do",
					params : {
						"probillNo" : probillNo
					}
				}).then(function successCallback(response) {
					if (response.data.result != 'success') {
						toaster.pop('error', '', response.data.msg);
						return;
					}
					var probill = response.data.records;
					var param = {
						probill : probill,
						
					};
					$localStorage.original = 'app.messagecentral.messagedetails';
					$localStorage.messageId = messageId;
					$state.go('app.tracing.resultforprobill', param);
				}, function errorCallback(response) {
					toaster.pop('error', '', 'can not connect the server');
				});
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