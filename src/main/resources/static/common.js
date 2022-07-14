$(document).ready(function(){

    $("#itemToken").on('change',function (){
        console.log(this.value);
    })

    function dataSend() {

        var data=$("#input").val();

        var messageDTO={
            result:data
        };

        $.ajax({
            url: "/dataSend",
            data: messageDTO,
            type:"POST",
        }).done(function (fragment) {
            $("#resultDiv").replaceWith(fragment);
        });
    }



});