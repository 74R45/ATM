$(document).ready(function () {
    // to store card num in localStorage
    var num = 0;

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
    };

    document.getElementById("checkBalance").onclick = function () {
        check_bal_ajax();
    };

    document.getElementById("endWork").onclick = function () {
        window.location.href = "index.html";
    };

    function check_bal_ajax() {
        console.log(num);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/v1/account/balance?number=" + localStorage.getItem("storageName"),
            // data: JSON.stringify(atmUser),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                if (data.hasOwnProperty("error")) {
                    console.log("ERROR : ", data);
                    $("#btn-atm-user").prop("disabled", false);
                } else {
                    localStorage.setItem("amount", data["amount"]);
                    localStorage.setItem("creditLimit", data["creditLimit"]);
                    // console.log("SUCCESS : ", data);
                    window.location.href = "checkBalance.html";
                }
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
        console.log(JSON.stringify(atmUser));
        num = $("#number").val();
        localStorage.setItem("storageName", num);
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
                if (data.hasOwnProperty("error")) {
                    console.log("ERROR : ", data);
                    window.alert("Card with this number doesn't exist.");
                    $("#btn-atm-user").prop("disabled", false);
                } else if (data.hasOwnProperty("attemptsLeft")) {
                    if (data.attemptsLeft === 0)
                        window.alert("3 attempts used. Your card has been blocked.");
                    else
                        window.alert("Incorrect login. Attempts left: " + data.attemptsLeft)
                } else {
                    console.log("SUCCESS : ", data);
                    $("#btn-atm-user").prop("disabled", false);
                    window.location.href = "atmFirstPage.html";
                }
            },
            error: function (e) {
                console.log("ERROR : ", e);
                $("#btn-atm-user").prop("disabled", false);
            }
        });
    }

    function online_bank_login() {
        var onlineBankUser = {};
        onlineBankUser["login"] = $("#onlBankLogin").val();
        onlineBankUser["password"] = $("#onlBankPass").val();
        localStorage.setItem("onlineBankLogin", $("#onlBankLogin").val());
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
                if (data.hasOwnProperty("error")) {
                    console.log("ERROR : ", data);
                    window.alert("Incorrect login.");
                    $("#bth-onl-bank-user").prop("disabled", false);
                } else {
                    console.log("SUCCESS : ", data);
                    $("#bth-onl-bank-user").prop("disabled", false);
                    window.location.href = "onlineBankFirstPage.html";
                }
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
                if (data.hasOwnProperty("error")) {
                    console.log("ERROR : ", data.message);
                    window.alert(data.message);
                } else {
                    localStorage.setItem("withdrawNumber", data["number"]);
                    localStorage.setItem("withdrawAmountLeft", data["amountLeft"]);
                    localStorage.setItem("withdrawAmount", data["amount"]);
                    localStorage.setItem("withdrawTimestamp", data["timestamp"]);

                    window.location.href = "withdrawCheck.html";
                }
            },
            error: function (e) {
                console.log("ERROR : ", e);
            }
        });
    }
});


