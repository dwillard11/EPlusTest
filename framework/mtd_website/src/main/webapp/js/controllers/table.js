app.controller('TableCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
    $scope.rowCollectionBasic = [
        { firstName: 'Laurent', lastName: 'Renard', birthDate: new Date('1987-05-21'), balance: 102, email: 'whatever@gmail.com' },
        { firstName: 'Blandine', lastName: 'Faivre', birthDate: new Date('1987-04-25'), balance: -2323.22, email: 'oufblandou@gmail.com' },
        { firstName: 'Francoise', lastName: 'Frere', birthDate: new Date('1955-08-27'), balance: 42343, email: 'raymondef@gmail.com' }
    ];

    $scope.removeRow = function (row) {
        var index = $scope.rowCollectionBasic.indexOf(row);
        if (index !== -1) {
            $scope.rowCollectionBasic.splice(index, 1);
        }
    };

    $scope.predicates = ['firstName', 'lastName', 'birthDate', 'balance', 'email'];
    $scope.selectedPredicate = $scope.predicates[0];

    var firstnames = ['Laurent', 'Blandine', 'Olivier', 'Max'];
    var lastnames = ['Renard', 'Faivre', 'Frere', 'Eponge'];
    var dates = ['1987-05-21', '1987-04-25', '1955-08-27', '1966-06-06'];
    var id = 1;


    var Probills = ['5530219', '5530220'];
    var DeliveryDates = ['2005-10-06'];
    var DeliveryTimes = ['08:01'];
    var Shippers = ['HATHEWAYS TRACADE', 'TAYLOR FORD SALES'];
    var Cities = ['TRACADE SHELA,NB', 'MONCTON,NB'];
    var Consignees = ['LAMBTON WOODWORKS % ESSO'];
    var Cities2 = ['ESSO SARINA, ON'];
    var PickupDates = ['2005/10/05'];
    var ReceivedBys = ['MASTER'];
    var id = 1;

    function getProbills(id) {

        var probill = Probills[Math.floor(Math.random() * 2)];
        var deliverydate = DeliveryDates[Math.floor(Math.random() * 1)];
        var deliverytime = DeliveryTimes[Math.floor(Math.random() * 1)];
        var shipper = Shippers[Math.floor(Math.random() * 2)];
        var city = Cities[Math.floor(Math.random() * 2)];
        var consignee = Consignees[Math.floor(Math.random() * 1)];
        var city2 = Cities2[Math.floor(Math.random() * 1)];
        var pickupdate = PickupDates[Math.floor(Math.random() * 1)];
        var receivedby = ReceivedBys[Math.floor(Math.random() * 1)];
        return {
            id: id,
            probill: probill,
            deliverydate: deliverydate,
            deliverytime: deliverytime,
            shipper: shipper,
            city: city,
            consignee: consignee,
            city2: city2,
            pickupdate: pickupdate,
            receivedby: receivedby
        }
    }

    function generateRandomItem(id) {

        var firstname = firstnames[Math.floor(Math.random() * 3)];
        var lastname = lastnames[Math.floor(Math.random() * 3)];
        var birthdate = dates[Math.floor(Math.random() * 3)];
        var balance = Math.floor(Math.random() * 2000);

        return {
            id: id,
            firstName: firstname,
            lastName: lastname,
            birthDate: new Date(birthdate),
            balance: balance
        }
    }

    $scope.rowCollection = [];

    for (id; id < 5; id++) {
        $scope.rowCollection.push(generateRandomItem(id));
    }

    //copy the references (you could clone ie angular.copy but then have to go through a dirty checking for the matches)
    $scope.displayedCollection = [].concat($scope.rowCollection);

    //add to the real data holder
    $scope.addRandomItem = function addRandomItem() {
        $scope.rowCollection.push(generateRandomItem(id));
        id++;
    };

    //remove to the real data holder
    $scope.removeItem = function (row) {
        var index = $scope.rowCollection.indexOf(row);
        if (index !== -1) {
            $scope.rowCollection.splice(index, 1);
        }
    }

    //  pagination
    $scope.itemsByPage = 5;

    $scope.rowCollectionPage = [];
    for (var j = 0; j < 20; j++) {
        $scope.rowCollectionPage.push(getProbills(j));
    }

    // pip
    var promise = null;
    $scope.isLoading = false;
    $scope.rowCollectionPip = [];
    $scope.getPage = function () {
        $scope.rowCollectionPip = [];
        for (var j = 0; j < 20; j++) {
            $scope.rowCollectionPip.push(generateRandomItem(j));
        }
    }

    $scope.callServer = function getData(tableState) {
        //here you could create a query string from tableState
        //fake ajax call
        $scope.isLoading = true;

        $timeout(function () {
            $scope.getPage();
            $scope.isLoading = false;
        }, 2000);
    };

    $scope.getPage();

}]);