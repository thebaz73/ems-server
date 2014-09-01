/**
 * Created by thebaz on 30/08/14.
 */
var map;

Handlebars.registerHelper('equal', function(lvalue, rvalue, options) {
    if (arguments.length < 3)
        throw new Error("Handlebars Helper equal needs 2 parameters");
    if( lvalue!=rvalue ) {
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

function fillMap(data) {
    var markers_data = [];
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            if (item.location != undefined && item.location.latitude != undefined && item.location.longitude != undefined) {
                var icon = (item.status == 'ok') ? '/imgs/status_ok.png' : (item.status == 'error') ? '/imgs/status_error.png' : (item.status == 'warn') ? '/imgs/status_warn.png' : '/imgs/status_unknown.png';
                markers_data.push({
                    lat : item.location.latitude,
                    lng : item.location.longitude,
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

function fillInventory(data) {
    var json = {
        devices: data
    };
    $.get("/handlebars/inventory.template", function (template) {
        var t = Handlebars.compile(template);
        $('#inventory-placeholder').append(t(json));
    });
}

function fillEventLogs(data) {
    var json = {
        events: data
    };
    $.get("/handlebars/log.template", function (template) {
        var t = Handlebars.compile(template);
        $('#log-placeholder').append(t(json));
    });
}