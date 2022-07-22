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
        const receiverZipcode = button.getAttribute('data-receiverZipcode');

        // const modalTitle = exampleModal.querySelector('.modal-title');
        const itemNameInput = exampleModal.querySelector('.modal-body .itemName');
        const totalAmountInput = exampleModal.querySelector('.modal-body .totalAmount');
        const orderTokenInput = exampleModal.querySelector('.modal-body .orderToken');
        const receiverZipcodeInput = exampleModal.querySelector('.modal-body .receiverZipcode');

        // modalTitle.textContent = `${itemName}` + ' 결제';
        itemNameInput.value = itemName;
        totalAmountInput.value = totalAmount;
        orderTokenInput.value = orderToken;
        receiverZipcodeInput.value = receiverZipcode;
    })
}

if (document.getElementById('btn-pay')) {
    const btnPay = document.getElementById('btn-pay');
    btnPay.addEventListener('click', event => {
        var paramMap = JSON.stringify({
            orderToken: $("#orderToken").val(),
            payMethod: $("#payMethod").val(),
            amount :$("#totalAmount").val(),
            orderDescription: $("#orderDescription").val(),
            isGift: $("#receiverZipcode").val() == "TEMP_VALUE",
        });

        let url = "/api/v1/orders/payment-order";

        if ($("#receiverZipcode").val() == "TEMP_VALUE") {
            url = "/api/v1/gift-orders/payment-order";
        }

        $.ajax({
            url: url,
            type: "POST",
            contentType: "application/json",
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

if (document.getElementById('giftModal')) {
    const exampleModal = document.getElementById('giftModal');
    exampleModal.addEventListener('show.bs.modal', event => {

    })
}

if (document.getElementById('btn-gift')) {
    const exampleModal = document.getElementById('btn-gift');
    exampleModal.addEventListener('click', event => {
        var isGiftInput = document.getElementById("isGift");
        isGiftInput.value = "T"
        document.getElementById('frm').submit();
    })
}



// });