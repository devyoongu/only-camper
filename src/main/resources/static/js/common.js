// $(document).ready(function(){
/**
 * 파트너 여부 체크
 * */
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


if (document.getElementById('exampleModal')) {
    const exampleModal = document.getElementById('exampleModal');
    exampleModal.addEventListener('show.bs.modal', event => {
        const button = event.relatedTarget;
        const itemName = button.getAttribute('data-itemName');
        const totalAmount = button.getAttribute('data-totalAmount');
        const orderToken = button.getAttribute('data-orderToken');

        // const modalTitle = exampleModal.querySelector('.modal-title');
        const itemNameInput = exampleModal.querySelector('.modal-body .itemName');
        const totalAmountInput = exampleModal.querySelector('.modal-body .totalAmount');
        const orderTokenInput = exampleModal.querySelector('.modal-body .orderToken');

        // modalTitle.textContent = `${itemName}` + ' 결제';
        itemNameInput.value = itemName;
        totalAmountInput.value = totalAmount;
        orderTokenInput.value = orderToken;
    })
}

if (document.getElementById('btn-pay')) {
    const btnPay = document.getElementById('btn-pay');
    btnPay.addEventListener('click', event => {
        var paramMap = {
            orderToken: $("#orderToken").val(),
            payMethod: $("#payMethod").val(),
            amount :$("#totalAmount").val(),
            orderDescription: $("#orderDescription").val(),
        };

        $.ajax({
            url: "/api/v1/orders/payment-order",
            type: "POST",
            // contentType: "application/json",
            data: paramMap,
        })
        .done(function (fragment) {
            console.log(fragment);
            const toastLiveExample = document.getElementById('liveToast');

            if (fragment.result.toUpperCase() != "SUCCESS") {
                $('#toast-body').text(fragment.message);
                $("#liveToast").addClass("text-bg-danger");
            } else {
                $('#toast-body').text("결제 완료하였습니다.");
                $("#liveToast").addClass("text-bg-primary");
                setTimeout(function(){
                    location.reload();
                },3000);
            }

            const toast = new bootstrap.Toast(toastLiveExample);
            toast.show();

        });
    })
}

// });