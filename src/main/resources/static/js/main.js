$(document).ready(function () {
    // to store card num in localStorage
    var num = 0;
    // var checkBalanceAndTransactions = document.getElementById("checkBalanceAndTransactions");
    // Forms
    $("#atm-form").submit(function (event) {

        event.preventDefault();

        atm_login();

    });
    $("#withdraw-form").submit(function (event) {

        event.preventDefault();

        withdraw_ajax_submit();

    });
    $("#onl-bank-login-form").submit(function (event) {

        event.preventDefault();

        online_bank_login();

    });

    document.getElementById("withdraw").onclick = function () {
        window.location.href = "withdraw.html";
    }

    document.getElementById("checkBalance").onclick = function () {

        check_bal_ajax();
        // window.location.href = "checkBalance.html";
    }
    // WHY DOESNT WORK ??
    // at the beginning of main js there is var checkBalanceAndTransactions
    // in onlineBankFirstPage.html there is button with checkBalanceAndTransactions id
    // there is main.js in scripts of onlineBankFirstPage.html
    // checkBalanceAndTransactions.onclick = function () {
    //     console.log("CHECK ");
    // }
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

    function online_bank_login() {
        var onlineBankUser = {}
        onlineBankUser["login"] = $("#onlBankLogin").val();
        onlineBankUser["password"] = $("#onlBankPass").val();
        localStorage.setItem("onlineBankLogin",$("#onlBankLogin").val());
        $("#bth-onl-bank-user").prop("disabled", true);
        console.log(JSON.stringify(onlineBankUser))
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/v1/user/login",
            data: JSON.stringify(onlineBankUser),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("SUCCESS : ", data);
                $("#bth-onl-bank-user").prop("disabled", false);
                window.location.href = "onlineBankFirstPage.html";
            },
            error: function (e) {

                console.log("ERROR : ", e);
                $("#bth-onl-bank-user").prop("disabled", false);

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

            },
            error: function (e) {

                console.log("ERROR : ", e);
                $("#bth-withdraw").prop("disabled", false);

            }
        });

    }
    // function check_balance_transactions(){
    //
    //     $.ajax({
    //         type: "GET",
    //         contentType: "application/json",
    //         url: "/api/v1/user?login=" + localStorage.getItem("onlineBankLogin"),
    //         //data: JSON.stringify(sumToWithdraw),
    //         dataType: 'json',
    //         cache: false,
    //         timeout: 600000,
    //         success: function (data) {
    //             console.log("GET ITN BY LOGIN: ", data["itn"])
    //             localStorage.setItem("onlineBankLogin", data["itn"]);
    //         },
    //         error: function (e) {
    //             console.log("ERROR : ", e);
    //         }
    //     });
    //     $.ajax({
    //         type: "GET",
    //         contentType: "application/json",
    //         url: "/api/v1/account/all?itn=" + localStorage.getItem("itn"),
    //         //data: JSON.stringify(sumToWithdraw),
    //         dataType: 'json',
    //         cache: false,
    //         timeout: 600000,
    //         success: function (data) {
    //             // localStorage.setItem("number", data["number"]);
    //             console.log("GET USER INFO BY ITN: ");
    //             console.log(data)
    //         },
    //         error: function (e) {
    //             console.log("ERROR : ", e);
    //         }
    //     });
    // }
});


