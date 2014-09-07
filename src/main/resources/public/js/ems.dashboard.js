var editMode;
var widgets = {
    "map": {
        template: "/template/map",
        data: "/inventory/devices",
        callback: function(data) {
            var markers_data = [];
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    if (item.driver.location != undefined && item.driver.location.latitude != undefined && item.driver.location.longitude != undefined) {
                        var icon = (item.driver.status == 'OK') ? '/imgs/status_ok.png' : (item.driver.status == 'ERROR') ? '/imgs/status_error.png' : (item.driver.status == 'WARN') ? '/imgs/status_warn.png' : '/imgs/status_unknown.png';
                        markers_data.push({
                            lat : item.driver.location.latitude,
                            lng : item.driver.location.longitude,
                            title : item.name,
                            icon : {
                                size : new google.maps.Size(16, 16),
                                url : icon
                            },
                            itemUrl: '/inventory/show/'+item.id,
                            click: function(e) {
                                window.location = this.itemUrl;
                            }
                        });
                    }
                }
            }

            map.addMarkers(markers_data);
        },
        loadFunction: function(widget, element) {
            $.get(widget.template, function (template) {
                element.html(template);
                element.find("div[data-action='panel-buttons']").toggle();

                $("#map").ready(function () {
                    map = new GMaps({
                        div: '#map',
                        mapType: "satellite",
                        zoom: 5,
                        lat: latitude,
                        lng: longitude
                    });
                    var response = $.getJSON(widget.data);
                    response.done(widget.callback);
                });
            });
        }
    },
    "inventory": {
        template: "/template/inventory",
        data: "/inventory/devices",
        page: 0,
        pageSize: 5,
        loadFunction: function(widget, element) {
                var request = widget.data + "?page=" + widget.page + "&pageSize=" + widget.pageSize;
                var response = $.getJSON(request);
                response.done(function(data) {
                    $.get(widget.template, function (template) {
                        var t = Handlebars.compile(template);
                        element.append(t(data));
                        element.find("div[data-action='panel-buttons']").toggle();
                    });
                });
        }
    },
    "log": {
        template: "/template/log",
        data: "/inventory/events",
        page: 0,
        pageSize: 5,
        loadFunction: function(widget, element) {
            var request = widget.data + "?page=" + widget.page + "&pageSize=" + widget.pageSize;
            var response = $.getJSON(request);
            response.done(function(data) {
                $.get(widget.template, function (template) {
                    var t = Handlebars.compile(template);
                    element.append(t(data));
                    element.find("div[data-action='panel-buttons']").toggle();
                });
            });
        }
    }
};
var layouts = [
    {
        classNames: ["col-md-12","col-md-6","col-md-6"]
    },
    {
        classNames: ["col-md-6","col-md-6"]
    },
    {
        classNames: ["col-md-4","col-md-8"]
    }
];
var dashboardConfig = {
    layout: 2,
    columns: [{
        widgets: ["map"]
    },{
        widgets: ["inventory","log"]
    }]
};

$(document).ready(function () {
    editMode = false;
    $("#dashboard-commands .glyphicon-th").toggle();
    $("#dashboard-commands .glyphicon-cog").toggle();

    var classNames = layouts[dashboardConfig.layout].classNames;
    for (var i = 0; i < classNames.length; i++) {
        $('<div/>', {
            id: 'wrapper'+i,
            class: classNames[i]
        }).appendTo("#dashboard");
    }
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            var widget = widgets[column.widgets[w]];
            widget.loadFunction(widget, $('#wrapper'+col));
        }
    }


    $('#dashboard-commands .glyphicon-edit').click(function(event){
        editMode = !editMode;
        event.preventDefault();
        $("#dashboard-commands .glyphicon-th").toggle();
        $("#dashboard-commands .glyphicon-cog").toggle();
        $("*[data-action='panel-buttons']").toggle();

        if(editMode) {
            $("#dashboard > div" ).addClass( "editable-board" );
        }
        else {
            $("#dashboard > div" ).removeClass( "editable-board" );
        }
    });
    $('#dashboard-commands .glyphicon-cog').click(function(event) {
        $('#settingsModal').modal();
    });
    $('#dashboard-commands .glyphicon-th').click(function(event) {
        $('#addModal').modal();
    });
});