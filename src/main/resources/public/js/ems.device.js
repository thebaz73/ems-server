/**
 * Created by thebaz on 22/09/14.
 */
var paths = [];
var selectors = [];
var deviceData = {
    uri: "",
    data: {},
    page: 0,
    pageSize: 5
};


/**
 * Initial activities
 */
$(document).ready(function () {
    var response = $.getJSON(schemaRequest);
    response.done(function(data) {
        var pathCursor = [];
        var selectorCursor = [];
        traverse(data, pathCursor, paths, selectorCursor, selectors);
        $("#driver").append(main(data));
    });

    updateData();
});

function updateData() {
    var request = "/inventory/device/" + deviceId;
    $.get(request, function(device) {
        $("#devicePanel").removeClass();
        $("#devicePanel").addClass("panel");
        addStatusClass($("#devicePanel"), device.driver.status);
        assignValues(device.driver, selectors, paths);
        //console.log(JSON.stringify(device));
    });
    loadDevicePagedData("/inventory/events/"+deviceId, "/template/deviceLogPage");

    setTimeout(updateData, updateFrequency);
}

function addStatusClass(element, status) {
    if(status === 'OK') {
        element.addClass("panel-success");
    }
    else if(status === 'WARN') {
        element.addClass("panel-warning");
    }
    else if(status === 'ERROR') {
        element.addClass("panel-danger");
    }
    else {
        element.addClass("panel-default");
    }
}

/**
 * Loads device related data
 *
 * @param uri base uri
 * @param template rendering template
 * @param page page number
 * @param pageSize page size
 */
function loadDevicePagedData(uri, template, page, pageSize) {
    if(page) {
        deviceData.page = page;
    }
    if(pageSize) {
        deviceData.pageSize = pageSize;
    }
    var request = uri + "?page=" + deviceData.page + "&pageSize=" + deviceData.pageSize;
    var response = $.getJSON(request);
    response.done(function(data, deviceId) {
        deviceData.uri = uri;
        deviceData.data = data;
        $.get(template, function (template) {
            var t = Handlebars.compile(template);
            $('#log-page').html(t(deviceData));
            $("[data-toggle='popover']").popover();
        });
    });
}

function traverse(obj, pathCursor, paths, selectorCursor, selectors) {
    if(obj.type === 'object') {
        var tmpObj = obj.properties;
        for(var key in tmpObj) {
            if(key !== 'status' && key !== 'type' && key !== 'location') {
                if(tmpObj[key].type !== 'object' &&  tmpObj[key].type !== 'array') {
                    paths.push(pathCursor.join('.') + '.' + key);
                    selectors.push(selectorCursor.join(' ') + ' ' + '*[data-path="'+key+'"]');
                }
                else {
                    pathCursor.push(key);
                    selectorCursor.push('*[data-path="'+key+'"]');
                    traverse(tmpObj[key], pathCursor, paths, selectorCursor, selectors);
                    pathCursor.pop();
                    selectorCursor.pop();
                }
            }
        }
    }
    else if(obj.type === 'array') {
        var tmpArrayObj = obj.items;
        for(var i = 0; i < obj.maxItems;i++) {
            pathCursor.push(i);
            //selectorCursor.push("["+i+"]");
            traverse(tmpArrayObj, pathCursor, paths, selectorCursor, selectors);
            pathCursor.pop();
            //selectorCursor.pop();
        }
    }
}

function getObjectPath(obj, path) {
    var parts = path.split('.');
    while (parts.length && (obj = obj[parts.shift()]));
    return obj;
}

function assignValues(driver, selectors, paths) {
    for (var i = 0; i < paths.length; i++) {
        var path = paths[i];
        var selector = selectors[i];
        var value = getObjectPath(driver, path);
        //console.log(value);
        //console.log($(selector).html());
        $(selector).text(value);
    }
}
