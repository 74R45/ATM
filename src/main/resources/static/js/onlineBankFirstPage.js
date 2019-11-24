$(document).ready(function () {
    var checkBalanceAndTransactions = document.getElementById("checkBalanceAndTransactions");
    checkBalanceAndTransactions.onclick = function () {
        console.log("CHECK ");
        check_balance_transactions();
    }

    function check_balance_transactions() {

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/api/v1/user?login=" + localStorage.getItem("onlineBankLogin"),
            //data: JSON.stringify(sumToWithdraw),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("GET ITN BY LOGIN: ", data["itn"])
                localStorage.setItem("itn", data["itn"]);
                console.log("typeof itn");
                console.log(typeof data["itn"]);
            },
            error: function (e) {
                console.log("ERROR : ", e);
            }
        });
        // console.log("get itn after ajax: ", localStorage.getItem("itn"));
        // + localStorage.getItem("itn")
        var dataArr = {}
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/api/v1/account/all?itn=" + localStorage.getItem("itn"),
            //data: JSON.stringify(sumToWithdraw),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                // localStorage.setItem("number", data["number"]);
                console.log("GET USER INFO BY ITN: ");
                console.log(data["0"]["number"]);
                console.log(data["0"]["isCredit"]);


                dataArr["number"] = data["0"]["number"];

                // console.log(localStorage.getItem("data", data);)
            },
            error: function (e) {
                console.log("ERROR : ", e);
            }
        });
        localStorage.setItem("data", dataArr["number"]);
        console.log("GET ITEM ", localStorage.getItem("data"));
        //window.location.href = "checkBalanceAndTransactions.html";
    }
});