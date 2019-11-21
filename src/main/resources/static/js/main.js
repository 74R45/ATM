$(document).ready(function () {
    var num = 0;

    $("#atm-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        atm_login();

    });
    $("#withdraw-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        withdraw_ajax_submit();

    });

    document.getElementById("withdraw").onclick = function () {
        window.location.href = "withdraw.html";
    }

    document.getElementById("checkBalance").onclick = function () {

        check_bal_ajax();
        // window.location.href = "checkBalance.html";
    }
    function check_bal_ajax(){
        console.log(num);
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/api/v1/account/balance?number=" + localStorage.getItem("storageName"),
            // data: JSON.stringify(atmUser),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                localStorage.setItem("amount",data["amount"]);
                localStorage.setItem("creditLimit",data["creditLimit"]);
                // console.log("SUCCESS : ", data);
                window.location.href = "checkBalance.html";
            },
            error: function (e) {
                console.log("ERROR : ", e);
            }
        });
    }
    function atm_login() {

        var atmUser = {}
        atmUser["number"] = $("#number").val();
        atmUser["pin"] = $("#pin").val();
        $("#btn-atm-user").prop("disabled", true);
        console.log(JSON.stringify(atmUser))
        num = $("#number").val();
        localStorage.setItem("storageName",num);
        console.log(num);
        // document.getElementById('numToShowWithWithdraw').innerHTML = num;
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/v1/account/login",
            data: JSON.stringify(atmUser),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("SUCCESS : ", data);
                $("#btn-atm-user").prop("disabled", false);
                window.location.href = "atmFirstPage.html";
            },
            error: function (e) {

                console.log("ERROR : ", e);
                $("#btn-atm-user").prop("disabled", false);

            }
        });

    }


    function withdraw_ajax_submit() {

        var sumToWithdraw = {}
        sumToWithdraw["number"] = localStorage.getItem("storageName");
        sumToWithdraw["amount"] = $("#amount").val();
        $("#bth-withdraw").prop("disabled", true);
        console.log(JSON.stringify(sumToWithdraw))
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/v1/account/withdraw",
            data: JSON.stringify(sumToWithdraw),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {

                // var json = "<h4>Ajax Response</h4><pre>"
                //     + JSON.stringify(data, null, 4) + "</pre>";
                // $('#feedback').html(json);
                //
                // console.log("SUCCESS : ", data);
                // $("#btn-withdraw").prop("disabled", false);
                // window.location.href = "atmFirstPage.html";
            },
            error: function (e) {

                // var json = "<h4>Ajax Response</h4><pre>" + JSON.stringify(data, null, 4) +
                //     + e.responseText + "</pre>";
                // $('#feedback').html(json);

                console.log("ERROR : ", e);
                $("#bth-withdraw").prop("disabled", false);

            }
        });

    }
});


