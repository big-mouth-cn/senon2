(function () {
    var WorkersPage = {
        init: function () {
            WorkersPage.loadData();
            WorkersPage.initButtonEvent();
            WorkersPage.initMessage();

        },
        loadData: function () {
            $.post(BASE_PATH + "/workers/list", function (res) {
                $.each(res, function (i, v) {
                    var row = "<tr>";
                    row += '<td>' + v.host + '</td>'
                    row += '<td>' + v.port + '</td>';
                    row += '<td>' + v.registerTime + '</td>';
                    row += '</tr>';
                    $(row).prependTo($("#worker-list-tbody"));
                });
            });
        },
        initButtonEvent: function () {
            $(".save-btn").click(function () {
                $("#add-form input[name='name']").val($("#name-add-input").val());
                $("#add-form input[name='url']").val($("#url-add-input").val());
                $("#add-form").submit();
            });
        },
        initMessage: function () {
            if ($("#status").val() == 'false') {
                Noty.error($("#message").val());
            } else if ($("#status").val() == 'true') {
                Noty.info("操作成功!");
            }
        }
    };

    WorkersPage.init();
}());
