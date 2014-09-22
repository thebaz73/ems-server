var editMode;
var map;
/**
 * Available widgets
 */
var widgets = {
    map: {
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
        },
        refresh: function(widget) {
            var response = $.getJSON(widget.data);
            response.done(widget.callback);
        },
        refreshMap: function () {
            if(map) {
                map.refresh();
            }
        }
    },
    inventory: {
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
        },
        refresh: function(widget) {
            var request = widget.data + "?page=" + widget.page + "&pageSize=" + widget.pageSize;
            var response = $.getJSON(request);
            response.done(function(data) {
                $.get("/template/inventoryPage", function (template) {
                    var t = Handlebars.compile(template);
                    $('#inventory-page').html(t(data));
                });
            });
        }
    },
    log: {
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
        },
        refresh: function(widget) {
            var request = widget.data + "?page=" + widget.page + "&pageSize=" + widget.pageSize;
            var response = $.getJSON(request);
            response.done(function(data) {
                $.get("/template/logPage", function (template) {
                    var t = Handlebars.compile(template);
                    $('#log-page').html(t(data));
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
        classNames: ["col-md-12","col-md-6","col-md-6","col-md-12"]
    },
    {
        classNames: ["col-md-12","col-md-4","col-md-8","col-md-12"]
    },
    {
        classNames: ["col-md-12","col-md-8","col-md-4","col-md-12"]
    },
    {
        classNames: ["col-md-12","col-md-5","col-md-7","col-md-12"]
    },
    {
        classNames: ["col-md-12","col-md-7","col-md-5","col-md-12"]
    }
];
/**
 * Dashboard configuration objects
 */
var dashboardConfig;
var defaultConfig = {
    layout: 1,
    columns: [{
        widgets: []
    },{
        widgets: ["map"]
    },{
        widgets: ["inventory","log"]
    },{
        widgets: []
    }]
};

/**
 * Initial activities
 */
$(document).ready(function () {
    if(typeof(Storage) === "undefined") {
        console.log("Sorry! No Web Storage support..");
        $("#dashboard-commands .glyphicon-floppy-save").toggle(false);
    }
    dashboardConfig = JSON.parse(localStorage.getItem("dashboardConfig"));
    if(!dashboardConfig) {
        dashboardConfig = defaultConfig;
    }
    layoutPage(false);

    updateWidgetData();

    $('#dashboard-commands .glyphicon-edit').click(function(event){
        event.preventDefault();
        editMode = !editMode;
        applyEditMode(editMode, true);
    });
    $('#dashboard-commands .glyphicon-cog').click(function(event) {
        event.preventDefault();
        $('#settingsModal').modal();
    });
    $('#dashboard-commands .glyphicon-floppy-save').click(function(event) {
        event.preventDefault();
        localStorage.setItem("dashboardConfig", JSON.stringify(dashboardConfig));
        editMode = false;
        applyEditMode(editMode, true);
        $('#saveModal').modal();
    });
    $("#closeSettings").click(function(event) {
        var layout = $( "#settingsModal input:checked" ).val();
        changeLayout(layout);
        layoutPage(true);
    });
});

function updateWidgetData() {
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            if(editMode) {
                break;
            }
            else {
                var widget = widgets[column.widgets[w]];
                widget.refresh(widget);
            }
        }
    }

    setTimeout(updateWidgetData, updateFrequency);
}

/**
 * Function that renders layout
 *
 * @param force f
 */
function layoutPage(force) {
    var classNames = layouts[dashboardConfig.layout].classNames;
    for (var i = 0; i < classNames.length; i++) {
        var wrapper = $("#wrapper" + i);
        if(wrapper.length == 0) {
            wrapper = $('<div/>', {
                id: 'wrapper'+i
            });
            wrapper.appendTo("#dashboard");
            wrapper.data("wrapper", i);
        }
        else {
            wrapper.removeClass();
        }
        wrapper.addClass("wrapper");
        wrapper.addClass(classNames[i]);
    }
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            var widget = $("#"+column.widgets[w]+"-widget");
            if(widget.length == 0) {
                widget = widgets[column.widgets[w]];
                widget.loadFunction(widget, $('#wrapper'+col));
            }
            else {
                widget.detach();
                widget.appendTo('#wrapper'+col);
            }
            if(column.widgets[w] === "map") {
                widgets["map"].refreshMap();
            }
        }
    }
    editMode = false;
    applyEditMode(editMode, force);
}

/**
 * Applies edit mode
 *
 * @param mode edit mode
 * @param force force droppable and draggable destroy
 */
function applyEditMode(mode, force) {
    $("#dashboard-commands .glyphicon-floppy-save").toggle(mode);
    $("#dashboard-commands .glyphicon-cog").toggle(mode);
    $("*[data-action='panel-buttons']").toggle(mode);

    if(mode) {
        $(".wrapper" ).addClass( "editable-board" );
        //$(".widget").css("max-width", "100px");
        $(".widget").draggable({
            revert: "invalid", // when not dropped, the item will revert back to its initial position
            cursor: "move"
        });
        $(".wrapper").droppable({
            accept: ".widget",
            activeClass: "activeWrapper",
            hoverClass: "hoverWrapper",
            drop: function( event, ui ) {
                updateConfig( ui.draggable, $(this) );
            }
        });
    }
    else {
        $(".wrapper" ).removeClass( "editable-board" );
        if(force) {
            $(".widget").draggable( "destroy" );
            $(".wrapper").droppable( "destroy" );
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
    // save moves;
    var widgetName = draggable.data("widget");
    var wrapperId = droppable.data("wrapper");
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var obj = dashboardConfig.columns[col].widgets;
        var index = obj.indexOf(widgetName);
        if(index > -1) {
            dashboardConfig.columns[col].widgets = obj.splice(index, 1);
        }
    }
    dashboardConfig.columns[wrapperId].widgets.push(widgetName);
    if(widgetName === "map") {
        widgets["map"].refreshMap();
    }
}