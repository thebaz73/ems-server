/**
 * Created by thebaz on 30/08/14.
 */

/**
 * Vars
 */
var map;

/**
 * Map utils
 *
 * @param data data
 */
function fillMap(data) {
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
    $.get("/template/inventory", function (template) {
        var t = Handlebars.compile(template);
        $('#inventory-placeholder').html(t(data));
    });
}

/**
 * Event log utils
 *
 * @param data data
 */
function fillEventLogs(data) {
//    var json = {
//        events: data
//    };
    $.get("/template/log", function (template) {
        var t = Handlebars.compile(template);
        $('#log-placeholder').html(t(data));
    });
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
        throw new Error("Handlebars Helper equal needs 2 parameters");
    if( lvalue==rvalue ) {
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

Handlebars.registerHelper('pagination', function(currentPage, totalPage, size, options) {
    var startPage, endPage, context;

    if (arguments.length === 3) {
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
