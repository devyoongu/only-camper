// $(document).ready(function(){
function checkIsPartner(itemToken) {
    $.ajax({
        url: "/item/ispartner/" + itemToken,
        type: "GET",
    })
    .done(function (fragment) {
        console.log(fragment);
        if (!fragment) {
            const toastLiveExample = document.getElementById('liveToast');
            const toast = new bootstrap.Toast(toastLiveExample);
            toast.show();
        } else {
            window.location.href = "/item/edit/" + itemToken;
        }
    });
}

function onOptionGroupChange() {
    var paramMap = {
        itemToken: $("#itemToken").val(),
        optionGroupOrdering: $("#optionGroupOrdering").val(),
    };

    $.ajax({
        url: "/order/option",
        type: "POST",
        data: paramMap,
    })
    .done(function (fragment) {

        $('#optionSelect').replaceWith(fragment);

    });
}

function onCalcuChange() {
    var paramMap = {
        itemToken: $("#itemToken").val(),
        optionGroupOrdering: $("#optionGroupOrdering").val(),
        optionOrdering: $("#optionOrdering").val(),
        orderCount: $("#orderCount").val(),
    };
    var btnSubmit = document.getElementById("btnSubmit");

    $.ajax({
        url: "/order/calcu",
        type: "POST",
        data: paramMap,
    })
    .done(function (fragment) {
        $('#totalAmount').text(fragment.totalAmount);
        $('#itemPrice').attr('value', fragment.totalAmount);
    });
}


// });