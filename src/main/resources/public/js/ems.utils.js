/**
 * Created by thebaz on 30/08/14.
 */
var map;

function fillMap(data) {
    var markers_data = [];
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            if (item.location != undefined && item.location.lat != undefined && item.location.lng != undefined) {
                markers_data.push({
                    lat : item.location.lat,
                    lng : item.location.lng,
                    title : item.name
                });
            }
        }
    }

    map.addMarkers(markers_data);
}

function fillInventory(data, element) {
    var json = {
        devices: JSON.parse(data)
    };
    $.get("/handlebars/inventory.template", function (template) {
        var t = Handlebars.compile(template);
        element.append(t(json));
    });
}