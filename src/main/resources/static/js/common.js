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

/**
 * 판매 종료로 변경
 * */
function changeEndOfSaleItem(itemToken) {
    var paramMap = JSON.stringify({
        itemToken: itemToken
    });

    $.ajax({
        url: "/api/v1/items/change-end-of-sales",
        type: "POST",
        contentType: "application/json",
        data: paramMap,
    })
    .done(function (fragment) {
        console.log(fragment);
        location.reload();
    });
}

/**
 * 판매 상태로 변경
 * */
function changeOnSaleItem(itemToken) {
    var paramMap = JSON.stringify({
        itemToken: itemToken
    });

    $.ajax({
        url: "/api/v1/items/change-on-sales",
        type: "POST",
        contentType: "application/json",
        data: paramMap,
    })
    .done(function (fragment) {
        console.log(fragment);
        location.reload();
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

//주문결제 modal
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

//주문 or 선물 결제
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
                },2000);
            }

            const toast = new bootstrap.Toast(toastLiveExample);
            toast.show();

        });
    })
}

//선물하기 Modal
if (document.getElementById('giftModal')) {
    const exampleModal = document.getElementById('giftModal');
    exampleModal.addEventListener('show.bs.modal', event => {
    })
}

//선물하기 등록
if (document.getElementById('btn-gift')) {
    const exampleModal = document.getElementById('btn-gift');
    exampleModal.addEventListener('click', event => {
        var paramMap = JSON.stringify({
            itemToken : $("#itemToken").val(),
            buyerUserId : $("#buyerUserId").val(),
            optionGroupOrdering : $("#optionGroupOrdering option:selected").val(),
            optionOrdering : $("#optionOrdering option:selected").val(),
            orderCount: $("#orderCount").val(),
            payMethod : $("#payMethod option:selected").val(),
            pushType:$('input:radio[name="pushType"]:checked').val(),
            giftReceiverUserId : $("#giftReceiverUserId option:selected").val(),
            giftReceiverName : $("#giftReceiverUserId option:selected").text(),
            giftReceiverPhone : $("#giftReceiverPhone").val(),
            giftMessage : $("#giftMessage").val(),
        });

        $.ajax({
           url: "/api/v1/gift-orders/gift-init/"+$("#itemToken").val()+"/"+$("#orderCount").val(),
           type: "POST",
           contentType: "application/json",
           data: paramMap,
       })
       .done(function (fragment) {
           console.log(fragment);
           const toastLiveExample = document.getElementById('liveToast');

           if (fragment.result.toUpperCase() != "SUCCESS") {
               $('#toast-body').text(fragment.errorCode);
               $("#liveToast").addClass("text-bg-danger");
           } else {
               $('#toast-body').text("선물하기 완료하였습니다.");
               $("#liveToast").removeClass("text-bg-danger");
               $("#liveToast").addClass("text-bg-primary");
               setTimeout(function(){
                   window.location.href = "/order/list";
               },2000);
           }

           const toast = new bootstrap.Toast(toastLiveExample);
           toast.show();
       })

    })
}

//선물수락
function onClickAccept(giftToken) {
    var giftTokenInput = document.getElementById("giftToken");
    giftTokenInput.value = giftToken;
    document.getElementById('frm').submit();
}


if (document.getElementById('profile_status')) {
    let url = "/profile";

    $.ajax({
        url: url,
        type: "GET",
        contentType: "application/json",
    })
    .done(function (fragment) {
        $('#profile_status').text(fragment);

    });
}

// });