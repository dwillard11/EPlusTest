app.controller('TracingCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
    var id = 1;
    var pieces = [4, 5];
    var descriptions = ['MARKETING', 'MARKETING'];
    var weights = [80, 30];
    function getDetails(id) {

        var piece = pieces[Math.floor(Math.random() * 2)];
        var description = descriptions[Math.floor(Math.random() * 2)];
        var weight = weights[Math.floor(Math.random() * 2)];

        return {
            id: id,
            piece: piece,
            description: description,
            weight: weight
        }
    }

    //  pagination
    $scope.itemsByPage = 5;

    $scope.rowCollectionPage = [];
    for (var j = 0; j < 5; j++) {
        $scope.rowCollectionPage.push(getDetails(j));
    }




    var probills = ['5530219', '5530220'];
    var pickupdates = ['2005/10/05'];
    var shippers = ['HATHEWAYS TRACADE', 'TAYLOR FORD SALES'];
    var cities = ['TRACADE SHELA,NB', 'MONCTON,NB'];
    var consignees = ['LAMBTON WOODWORKS % ESSO'];
    var cities2 = ['ESSO SARINA, ON'];
    var deliverydates = ['2005-10-06'];
    var deliverytimes = ['08:01'];
    var bolnumbers = ['NS', 'NA', '23730093'];//3
    var ponumbers = ['NS', '603103'];//2
    var shippernumbers = ['370348']; //1 
    var weights = ['500', '1200', '150', '5'];// 4
    var adjwgts = ['0']; // 1
    var receivedbys = ['MASTER'];//1
    var termsarray = ['NOS', 'OCS'];//2
    var id = 1;

    function getProbills(id) {

        var probill = probills[Math.floor(Math.random() * 2)];
        var pickupdate = pickupdates[Math.floor(Math.random() * 1)];
        var shipper = shippers[Math.floor(Math.random() * 2)];
        var city = cities[Math.floor(Math.random() * 2)];
        var consignee = consignees[Math.floor(Math.random() * 1)];
        var city2 = cities2[Math.floor(Math.random() * 1)];
        var deliverydate = deliverydates[Math.floor(Math.random() * 1)];
        var deliverytime = deliverytimes[Math.floor(Math.random() * 1)];
        var bolnumber = bolnumbers[Math.floor(Math.random() * 3)];
        var ponumber = ponumbers[Math.floor(Math.random() * 2)];
        var shippernumber = shippernumbers[Math.floor(Math.random() * 1)];
        var weight = weights[Math.floor(Math.random() * 4)];
        var adjwgt = adjwgts[Math.floor(Math.random() * 1)];
        var receivedby = receivedbys[Math.floor(Math.random() * 1)];
        var terms = termsarray[Math.floor(Math.random() * 1)];

        return {
            id: id,
            probill:probill,
            pickupdate:pickupdate,
            shipper:shipper,
            city:city,
            consignee:consignee,
            city2:city2,
            deliverydate:deliverydate,
            deliverytime:deliverytime,
            bolnumber:bolnumber,
            ponumber:ponumber,
            shippernumber:shippernumber,
            weight:weight,
            adjwgt:adjwgt,
            receivedby:receivedby,
            terms:terms
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

    
    

    //  pagination
    $scope.itemsByPage2 = 5;

    $scope.rowCollectionPage2 = [];
    for (var j = 0; j < 100; j++) {
        $scope.rowCollectionPage2.push(getProbills(j));
    }

    

}]);