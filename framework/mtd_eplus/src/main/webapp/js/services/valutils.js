app.service("valutils", function () {
    this.isEmptyOrUndefined = function (val) {
        if (val == null || '' == val || undefined == val || "" == val)
            return true;
        return false;
    }
    this.trimToEmpty = function (val) {
        if (this.isEmptyOrUndefined(val))
            return "";
        return val + "";
    }
})