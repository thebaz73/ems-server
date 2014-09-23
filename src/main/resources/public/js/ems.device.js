/**
 * Created by thebaz on 22/09/14.
 */
var paths = [];
var selectors = [];
var deviceEventsData = {
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

/**
 * Recurrent function to update all data
 */
function updateData() {
    var request = "/inventory/device/" + deviceId;
    var $devicePanel = $("#devicePanel");
    $.get(request, function(device) {
        $devicePanel.removeClass();
        $devicePanel.addClass("panel");
        addStatusClass($devicePanel, device.driver.status);
        assignValues(device.driver, selectors, paths);
    });
    doLoad("/inventory/events/"+deviceId, "/template/deviceLogPage");

    setTimeout(updateData, updateFrequency);
}

/**
 * Sets a proper class according to device status
 *
 * @param element panel element
 * @param status device status
 */
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
    deviceEventsData.page = page;
    deviceEventsData.pageSize = pageSize;
    doLoad(uri, template);
}

/**
 * Loads device related data
 *
 * @param uri base uri
 * @param template rendering template
 */
function doLoad(uri, template) {
    var request = uri + "?page=" + deviceEventsData.page + "&pageSize=" + deviceEventsData.pageSize;
    var response = $.getJSON(request);
    response.done(function(data) {
        deviceEventsData.uri = uri;
        deviceEventsData.data = data;
        $.get(template, function (template) {
            var t = Handlebars.compile(template);
            $('#log-page').html(t(deviceEventsData));
            $("[data-toggle='popover']").popover();
        });
    });

}

/**
 * Traverses a JSON object representing a driver schema
 * in order to extract a path of elementary properties
 * and a relative DOM selector
 *
 * @param obj driver schema
 * @param pathCursor path cursor
 * @param paths array of paths
 * @param selectorCursor selector cursor
 * @param selectors array of selectors
 */
function traverse(obj, pathCursor, paths, selectorCursor, selectors) {
    if(obj.type === 'object') {
        var tmpObj = obj.properties;
        for(var key in tmpObj) {
            if(key !== 'status' && key !== 'type' && key !== 'location') {
                if(tmpObj[key].type !== 'object' &&  tmpObj[key].type !== 'array') {
                    if(pathCursor.length > 0) {
                        paths.push(pathCursor.join('.') + '.' + key);
                        selectors.push(selectorCursor.join(' ') + ' ' + '*[data-path="'+key+'"]');
                    }
                    else {
                        paths.push(key);
                        selectors.push('*[data-path="'+key+'"]');
                    }
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

/**
 * Follows path along the object to find
 * the elementary property given by the path
 *
 * @param obj driver object
 * @param path property path
 * @returns {*} property object
 */
function getObjectPath(obj, path) {
    var parts = path.split('.');
    while (parts.length && (obj = obj[parts.shift()]));
    return obj;
}

/**
 * Assigns all object property values to DOM selectors
 *
 * @param driver driver object
 * @param selectors DOM selector array
 * @param paths array of path
 */
function assignValues(driver, selectors, paths) {
    for (var i = 0; i < paths.length; i++) {
        var path = paths[i];
        var selector = selectors[i];
        var value = getObjectPath(driver, path);
        //console.log(path + ": " + value);
        //console.log(selector + ": " + $(selector).html());
        $(selector).text(value);
    }
}
