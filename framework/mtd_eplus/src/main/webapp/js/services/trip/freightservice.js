app.service("freightService", function (valutils) {
    this.calculateSummary = function (freightList) {
        if (valutils.isEmptyOrUndefined(freightList))
            return {totalPieces: "", freightSummary: 0, totalWeight: ""};
        var totalPieces = 0;
        var freightSummary = freightList.length;
        var totalWeight = 0;
        angular.forEach(freightList, function (data) {
            if (!valutils.isEmptyOrUndefined(data.actualPieces)) {
                totalPieces += parseInt(data.actualPieces);
            }
            if (!valutils.isEmptyOrUndefined(data.actualChargeWt)) {
                totalWeight += parseFloat(data.actualChargeWt?data.actualChargeWt:0); // .toFixed(2);
            }

        });
        totalWeight = (totalPieces * totalWeight).toFixed(2);
        return {totalPieces: totalPieces, freightSummary: freightSummary, totalWeight: totalWeight};
    }
    
    this.buildDim = function (dim1, dim2, dim3, dimUOM) {

        if (valutils.isEmptyOrUndefined(dim1) && valutils.isEmptyOrUndefined(dim2) && valutils.isEmptyOrUndefined(dim3)) {
            return "";
        }
        if (valutils.isEmptyOrUndefined(dim1)) {
            dim1 = 0;
        }
        if (valutils.isEmptyOrUndefined(dim2)) {
            dim2 = 0;
        }
        if (valutils.isEmptyOrUndefined(dim3)) {
            dim3 = 0;
        }
        return dim1 + "X" + dim2 + "X" + dim3 + " "+dimUOM;
    }

    this.calculateChargeableWt = function(pcs, dim1,dim2, dim3, unit) {
        var volume = 1.00;
        volume = volume * pcs * parseFloat(dim1?dim1:0) * parseFloat(dim2?dim2:0) * parseFloat(dim3?dim3:0);
        if ("Inches" == unit) {
            volume = volume   / 166.00;
        } else {
            volume = volume / 6000.00;
        }
        return Math.round(volume);
    }
    this.splitDim = function (dim, index) {
        // dim = 20X30X30 Inches
        if (valutils.isEmptyOrUndefined(dim))
            return '';
        var array1 = dim.split(' ');
        var array = array1[0].split('X');
        if (isNaN(array[index])) {
            return 0;
        }
        return parseInt(array[index]);
    }
})