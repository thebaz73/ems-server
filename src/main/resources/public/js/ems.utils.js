/**
 * Created by thebaz on 30/08/14.
 */
var map;

function fillInventory (data) {
    var markers_data = [];
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            $('#inventory-placeholder').append('<tr valign="middle"><td>' +
                item.name +
                '</td><td>' +
                item.specification.type +
                '</td></tr>');

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
