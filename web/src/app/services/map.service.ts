import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { IActivity } from '../shared/activity.model';
import { ActivityService } from '../services/activity.service';
import { SAVED_ACTIVITIES } from '../shared/activities';
import {LineString} from 'geojson';
import {Coord} from '../client/coord';
import {LastCoordsTC} from '../client/lastCoordsTC';

const apiToken = environment.MAPBOX_API_KEY;
declare var omnivore: any;
declare var L: any;

const defaultCoords: number[] = [80, 80];
const defaultZoom: number = 8;

@Injectable()
export class MapService {

  constructor() { }

  getActivity(id: number) {
    return SAVED_ACTIVITIES.slice(0).find(run => run.id == id);
  }

  getLayer(map: any) {

    var myStyle = {
      'color': '#3949AB',
      'weight': 5,
      'opacity': 0.95
    };

    var greenIcon = L.icon({
      iconUrl: 'assets/img/5.png',
      iconSize:     [38, 38], // size of the icon
      // iconAnchor:   [22, 50], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -46] // point from which the popup should open relative to the iconAnchor
    });

    /* var customLayer = L.geoJson(null, {
      style: myStyle
    }); */

    var myLines = [{
      'type': 'LineString',
      'coordinates': [[-100, 40], [-105, 45], [-110, 55]]
    }];

    var customLayer13 = L.geoJson(myLines , {
      style: myStyle
    });
    // работает из за того, что объявили действие .on
  customLayer13
    .on('ready', function() {
      map.fitBounds(customLayer13.getBounds());
    }).addTo(map)

   // var myLayer = L.geoJSON().addTo(map);
   // myLayer.addData(geojsonFeature);
    // var gpxLayer = omnivore.gpx(SAVED_ACTIVITIES.slice(0).find(run => run.id == 1).gpxData, null, customLayer1)
    /* var gpxLayer = omnivore.gpx(testFile, null, customLayer1)
      .on('ready', function() {
        map.fitBounds(gpxLayer.getBounds());
      }).addTo(map).bindPopup('1222222222225'); // тут по сути можно передать координаты или информацию в удобном виде
*/
   // L.marker([43.066748000, -89.305216000], {icon: greenIcon}).addTo(map).bindPopup('I am a green leaf.');

   // L.marker([43.066748000, 89.305216000], {icon: greenIcon}).addTo(map).bindPopup(' I am a green leaf.');
    return customLayer13;
  }

  getNewLayerTrack(map: any, coords: Coord[]) {

    map.setView([coords[0].longtitude, coords[0].lattitude], 13);
     var lines = [];

    console.log(coords);

    for (var i = 0; i < coords.length; i++) {
    var line = [coords[i].lattitude, coords[i].longtitude];
    lines.push(line);
    }
    console.log(lines);

    var myLines = [{
      'type': 'LineString',
      'coordinates': lines
    }];

    var myStyle = {
      'color': '#FF0000',
      'weight': 5,
      'opacity': 0.95
    };

    var customLayer13 = L.geoJson(myLines , {
      style: myStyle
    });
    // работает из за того, что объявили действие .on
    customLayer13
      .on('ready', function() {
        map.fitBounds(customLayer13.getBounds());
      }).addTo(map);

    return customLayer13;
  }

  getLayerForLastPoints(map: any , lastCords: LastCoordsTC[]) {
    // map.setView([lastCords[0].lattitude, lastCords[0].longtitude], 13);

    console.log(lastCords);

    var greenIcon = L.icon({
      iconUrl: 'assets/img/5.png',
      iconSize:     [38, 38], // size of the icon
      // iconAnchor:   [22, 50], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -46] // point from which the popup should open relative to the iconAnchor
    });

    var redIcon = L.icon({
      iconUrl: 'assets/img/6.png',
      iconSize:     [38, 38], // size of the icon
      // iconAnchor:   [22, 50], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -46] // point from which the popup should open relative to the iconAnchor
    });

    var markerGroup = L.layerGroup().addTo(map);

    for (var i = 0; i < lastCords.length; i++) {
      if ( Date.now() - lastCords[i].navigationtime <= 600000 ) {
        var markerOne = L.marker([lastCords[i].longtitude, lastCords[i].lattitude],
          {icon: greenIcon}).addTo(markerGroup).bindPopup(lastCords[i].imei);
        console.log('< 600000 ');
      } else {
        var markerOne = L.marker([lastCords[i].longtitude, lastCords[i].lattitude],
          {icon: redIcon}).addTo(markerGroup).bindPopup(lastCords[i].imei);
        console.log('> 600000');
      }
     console.log(lastCords[i].navigationtime);
     console.log(Date.now());
    }
    return markerGroup;
  }
  plotActivityMarkers() {

    var myMap = L.map('map').setView([55.75, 37.61670000000004], defaultZoom);

    myMap.maxZoom = 100;

    L.tileLayer('https://api.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
      attribution: '',
      id: 'mapbox.dark',
      accessToken: apiToken
    }).addTo(myMap);

    var greenIcon = L.icon({
      iconUrl: 'assets/img/5.png',
      iconSize:     [38, 38], // size of the icon
      // iconAnchor:   [22, 50], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -46] // point from which the popup should open relative to the iconAnchor
    });

    L.marker([55.75, 37.61670000000004], {icon: greenIcon}).addTo(myMap).bindPopup(' I am a green leaf.');

  }

  plotActivity(id: number) {
    var myStyle = {
      'color': '#3949AB',
      'weight': 5,
      'opacity': 0.95
    };

    var map = L.map('map').setView(defaultCoords, defaultZoom);

    map.maxZoom = 100;

    L.tileLayer('https://api.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
      attribution: '',
      id: 'mapbox.satellite',
      accessToken: apiToken
    }).addTo(map);


    var customLayer = L.geoJson(null, {
      style: myStyle
    });

    var greenIcon = L.icon({
      iconUrl: 'assets/img/5.png',
      iconSize:     [38, 38], // size of the icon
      // iconAnchor:   [22, 50], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -46] // point from which the popup should open relative to the iconAnchor
    });



    var gpxLayer = omnivore.gpx(SAVED_ACTIVITIES.slice(0).find(run => run.id == id).gpxData, null, customLayer)
      .on('ready', function() {
        map.fitBounds(gpxLayer.getBounds());
        console.log(map.fitBounds(gpxLayer.getBounds()).latitude);
        console.log((map.fitBounds(gpxLayer.getBounds())));

      }).addTo(map).bindPopup(map.latitude); // тут по сути можно передать координаты или информацию в удобном виде
    gpxLayer
      .on('load', function() {
        map.fitBounds(gpxLayer.getBounds());
      })
    var test = gpxLayer.getBounds();
    console.log('location ' + test.location);
    console.log(test.lng);
    console.log(test.latitude);

    var coordinates = new Array();
   // for (var i = 0; i < 5; i++) {
      var marker = L.marker([51.5, 89.305216000]);
      coordinates.push(marker);
    var marker1 = L.marker([51.5, 88.305216000]);
    coordinates.push(marker1);
    // }
    var group = new L.featureGroup(coordinates);
    gpxLayer
    .on('ready', function() {
      map.fitBounds(group.getBounds());

    }).addTo(map).bindPopup(map.latitude);

    // gpxLayer.feature.geometry.latitude == 43.066748000;
    // gpxLayer.feature.geometry.longitude == -89.305216000;
    // gpxLayer.addBounds()

   L.marker([43.066748000, -89.305216000], {icon: greenIcon}).addTo(map).bindPopup('I am a green leaf.');
 }
  // createMarker(data: GeoJson) {
  //  return 'о вы из Англии!';
 // }
}
