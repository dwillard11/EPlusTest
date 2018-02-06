var dragModal = {
    mouseStartPoint: { "left": 0, "top": 0 },
    mouseEndPoint: { "left": 0, "top": 0 },
    mouseDragDown: false,
    basePoint: { "left": 0, "top": 0 },
    moveTarget: null,
    isMove:false
}
$(document).ready(function () {
    $(document).on("mousedown", ".modal-header", function (e) {
        console.log(22);
        if ($(e.target).hasClass("close"))
            return;
        dragModal.mouseDragDown = true;
        dragModal.moveTarget = $(this).parent().parent().parent();
        dragModal.mouseStartPoint = { "left": e.clientX, "top": e.clientY };
        dragModal.basePoint = dragModal.moveTarget.offset();
    }).on("mouseup", function (e) {
        dragModal.mouseDragDown = false;
        dragModal.moveTarget = undefined;
        dragModal.mouseStartPoint = { "left": 0, "top": 0 };
        dragModal.basePoint = { "left": 0, "top": 0 };
    }).on("mousemove", function (e) {
        if (!dragModal.mouseDragDown || dragModal.moveTarget == undefined) return;
        var mousX = e.clientX;
        var mousY = e.clientY;
        if (mousX < 0) mousX = 0;
        if (mousY < 0) mousY = 25;
        dragModal.mouseEndPoint = { "left": mousX, "top": mousY };
        var width = dragModal.moveTarget.width();
        var height = dragModal.moveTarget.height();
        dragModal.mouseEndPoint.left = dragModal.mouseEndPoint.left - (dragModal.mouseStartPoint.left - dragModal.basePoint.left);
        dragModal.mouseEndPoint.top = dragModal.mouseEndPoint.top - (dragModal.mouseStartPoint.top - dragModal.basePoint.top);
        dragModal.moveTarget.offset(dragModal.mouseEndPoint);
    });
});