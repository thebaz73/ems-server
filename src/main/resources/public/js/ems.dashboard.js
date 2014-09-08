var editMode;
/**
 * Available widgets
 */
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
                element.find("#map-widget div[data-action='panel-buttons']").toggle();

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
                        element.find("#inventory-widget div[data-action='panel-buttons']").toggle();
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
                    element.find("#log-widget div[data-action='panel-buttons']").toggle();
                });
            });
        }
    }
};
/**
 * Available layouts
 */
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
/**
 * Dashboard configuration object
 * @type {{layout: number, columns: {widgets: string[]}[]}}
 */
var dashboardConfig = {
    layout: 2,
    columns: [{
        widgets: ["map"]
    },{
        widgets: ["inventory","log"]
    }]
};

/**
 * Initial activities
 */
$(document).ready(function () {
    editMode = false;
    $("#dashboard-commands .glyphicon-th").toggle();
    $("#dashboard-commands .glyphicon-cog").toggle();

    layoutPage();

    $('#dashboard-commands .glyphicon-edit').click(function(event){
        editMode = !editMode;
        event.preventDefault();
        $("#dashboard-commands .glyphicon-th").toggle();
        $("#dashboard-commands .glyphicon-cog").toggle();
        $("*[data-action='panel-buttons']").toggle();

        if(editMode) {
            $(".wrapper" ).addClass( "editable-board" );
            //$(".widget").css("max-width", "100px");
            $(".widget").draggable({
                revert: "invalid", // when not dropped, the item will revert back to its initial position
                containment: "document",
                snap: true,
                cursor: "move"
            });
            $(".wrapper").droppable({
                accept: ".widget",
                drop: function( event, ui ) {
                    updateConfig( ui.draggable, $(this) );
                }
            });
        }
        else {
            $(".wrapper" ).removeClass( "editable-board" );
            $(".widget").draggable( "destroy" );
            $(".wrapper").droppable( "destroy" );
        }
    });
    $('#dashboard-commands .glyphicon-cog').click(function(event) {
        $('#settingsModal').modal();
    });
    $('#dashboard-commands .glyphicon-th').click(function(event) {
        $('#addModal').modal();
    });
});

/**
 * Function that renders layout
 */
function layoutPage() {
    var classNames = layouts[dashboardConfig.layout].classNames;
    for (var i = 0; i < classNames.length; i++) {
        var wrapper = $("#wrapper" + i);
        if(wrapper.length == 0) {
            wrapper = $('<div/>', {
                id: 'wrapper'+i
            })
        }
        wrapper.addClass(classNames[i]);
        wrapper.addClass("wrapper");
        wrapper.appendTo("#dashboard");
    }
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            var widget = widgets[column.widgets[w]];
            widget.loadFunction(widget, $('#wrapper'+col));
        }
    }
}

/**
 * Function that changes layout and dashboard configuration
 *
 * @param newLayout layout number shall be 0,1,2
 */
function changeLayout(newLayout) {
    var configuration = {
        layout:newLayout,
        columns: []
    };

    var allWidgets = [];
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            allWidgets.push(column.widgets[w]);
        }
    }

    configuration.columns.push({widgets:allWidgets});

    var classNames = layouts[newLayout].classNames;
    for (var i = 1; i < classNames.length; i++) {
        configuration.columns.push({widgets:[]});
    }

    dashboardConfig = configuration;
    console.log(configuration);
}

/**
 * Updates dashboard configuration on DnD
 * @param draggable draggable item
 * @param droppable droppable item
 */
function updateConfig(draggable, droppable) {
    draggable.appendTo(droppable);
    draggable.css("position", "");
    draggable.css("top", "");
    draggable.css("left", "");
}