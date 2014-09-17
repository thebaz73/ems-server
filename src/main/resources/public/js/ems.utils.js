/**
 * Created by thebaz on 30/08/14.
 */

/**
 * Vars
 */
var map;

function doOperation(uri, elementId, operation) {
    $.getJSON(uri, function(data) {
        if(data.result) {
            $(elementId + ' .modal-body').html(operation + ' succeeded.');
            $(elementId).modal();
        }
        else {
            $(elementId + ' .modal-body').html(operation + ' failed.');
            $(elementId).modal();
        }
    });
}

function loadPagedData(uri, page, pageSize, callback) {
    var request = uri + "?page=" + page + "&pageSize=" + pageSize;
    var response = $.getJSON(request);
    response.done(callback);
}

/**
 * Inventory Utils
 *
 * @param data data
 */
function fillInventory(data) {
    $.get("/template/inventoryPage", function (template) {
        var t = Handlebars.compile(template);
        $('#inventory-page').html(t(data));
    });
}

/**
 * Event log utils
 *
 * @param data data
 */
function fillEventLogs(data) {
    $.get("/template/logPage", function (template) {
        var t = Handlebars.compile(template);
        $('#log-page').html(t(data));
    });
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
    var request = uri + "?page=" + page + "&pageSize=" + pageSize;
    var response = $.getJSON(request);
    response.done(function(data, deviceId) {
        var json = new Object();
        json.uri = uri;
        json.data = data;
        $.get(template, function (template) {
            var t = Handlebars.compile(template);
            $('#log-page').html(t(json));
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
        var tmpObj = obj.items;
        for(var i = 0; i < obj.maxItems;i++) {
            pathCursor.push(i);
            //selectorCursor.push("["+i+"]");
            traverse(tmpObj, pathCursor, paths, selectorCursor, selectors);
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
//
// Handlebars HELPERS
//
Handlebars.registerHelper('equal', function(lvalue, rvalue, options) {
    if (arguments.length < 3)
        throw new Error("Handlebars Helper equal needs 2 parameters");
    if( lvalue!=rvalue ) {
        return options.inverse(this);
    } else {
        return options.fn(this);
    }
});

Handlebars.registerHelper('notEqual', function(lvalue, rvalue, options) {
    if (arguments.length < 3)
        throw new Error("Handlebars Helper notEqual needs 2 parameters");
    if( lvalue==rvalue ) {
        return options.inverse(this);
    } else {
        return options.fn(this);
    }
});

Handlebars.registerHelper('validDriverProperty', function(lvalue, options) {
    if (arguments.length < 2)
        throw new Error("Handlebars Helper validDriverProperty needs 1 parameter");
    if( lvalue=='status' || lvalue=='type' || lvalue=='location' ) {
        return options.inverse(this);
    } else {
        return options.fn(this);
    }
});

//  format an ISO date using Moment.js
//  http://momentjs.com/
//  moment syntax example: moment(Date("2011-07-18T15:50:52")).format("MMMM YYYY")
//  usage: {{dateFormat creation_date format="MMMM YYYY"}}
Handlebars.registerHelper('dateFormat', function(context, block) {
    var d = new Date(context);
    if (window.moment) {
        var f = block.hash.format || "YYYY-MM-DDThh:mm:ss";
        return d.toISOString();//moment(d).format(f);
    }else{
        return d.toISOString();   //  moment plugin not available. return data as is.
    }
});

Handlebars.registerHelper('pagination', function(uri, currentPage, totalPage, size, options) {
    var startPage, endPage, context;

    if (arguments.length === 4) {
        options = size;
        size = 5;
    }

    currentPage++;

    startPage = currentPage - Math.floor(size / 2);
    endPage = currentPage + Math.floor(size / 2);

    if (startPage <= 0) {
        endPage -= (startPage - 1);
        startPage = 1;
    }

    if (endPage > totalPage) {
        endPage = totalPage;
        if (endPage - size + 1 > 0) {
            startPage = endPage - size + 1;
        } else {
            startPage = 1;
        }
    }

    context = {
        uri: uri,
        startFromFirstPage: false,
        previousPage:currentPage-2,
        nextPage:currentPage,
        pages: [],
        endAtLastPage: false
    };
    if (startPage === 1) {
        context.startFromFirstPage = true;
    }
    for (var i = startPage; i <= endPage; i++) {
        context.pages.push({
            uri: uri,
            page: i,
            pageIndex: i-1,
            isCurrent: i === currentPage
        });
    }
    if (endPage === totalPage) {
        context.endAtLastPage = true;
    }

    return options.fn(context);
});
