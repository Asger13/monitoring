import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import { MapService } from '../services/map.service';

import { ActivatedRoute } from '@angular/router';
import {UiMapService} from '../services/ui-map.service';
import {Client} from '../client/client';
import {Transport} from '../client/transport';
import {IMyDateModel, IMyDpOptions} from 'mydatepicker';
import {Agrofield} from '../client/agrofield';
import {Coord} from '../client/coord';

import {LastCoordsTC} from '../client/lastCoordsTC';

import 'leaflet';
import {environment} from '../../environments/environment';
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {
  map: L.Map = null;
@ViewChild('popup1') popup1;
@ViewChild('popup2') popup2;
@ViewChild('popup3') popup3;
@ViewChild('popup4') popup4;

  constructor(private _mapService: MapService,
              private _route: ActivatedRoute,
              private serv: UiMapService) {
  }
  edited: boolean;
  agrofieldThis: string;
  clientThis: string;
  // activity: any;
  // activityName: string;
  activityComments: string;
  activityDate: Date;
  activityDistance: number;
  gpx: any;
  clientList: Client[];
  transportList: Transport[];
  agrofieldList: Agrofield[];
  lastCoordsList: LastCoordsTC[];
  coordList: Coord[];
  changeTransport = 0;
  changeAgroField = 0;
  timestampCalendar: any;
  newtimeCalendar: any;
  interval: any;
  filterValGet = 0;
  customLayer: any;
  areaField = 0;
  public  myDatePickerOptions: IMyDpOptions = {
    // other options...
    dateFormat: 'dd.mm.yyyy',
  };
  // @ViewChild('map') mapContainer;
  // Initialized to specific date (09.10.2018).
  // public model: any = { date: { year: 2018, month: 10, day: 9 } };
  ngOnInit() {
    const apiToken = environment.MAPBOX_API_KEY;
    this.map = L.map('map').setView([55.75, 37.61670000000004], 13);
// попробовать передавать map в другие функции
    L.tileLayer('https://api.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
      maxZoom: 18,
      id: 'mapbox.satellite',
      accessToken: apiToken,
      attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    /* this.activity = this._mapService.getActivity(
      +this._route.snapshot.params['id']); */

    this.customLayer = this._mapService.getLayer(this.map);

    // this._mapService.plotActivity(+this._route.snapshot.params['id']);
    // this.activityName = this.activity.name;
    // this.activityComments = this.activity.comments;
    // this.activityDistance = this.activity.distance;
    // this.activityDate = this.activity.date;
    // this.gpx = this.activity.gpxData;

    this.loadClient();

  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

  private loadClient() {
    this.serv.getСlients().subscribe(data => {
      this.clientList = data;
      this.edited = true;
    });
    console.log('loadClient ');
  }

  clickButttonTest1() {

    this.popup1.options = {
      cancleBtnClass: 'btn btn-default',
      confirmBtnClass: 'btn btn-default',
      color: '#4180ab',
      showButtons: false,
      header: 'Клиент'};

    this.popup1.show(this.popup1.options);
  }

  clickButttonTest2() {

    this.popup2.options = {
      cancleBtnClass: 'btn btn-default',
      confirmBtnClass: 'btn btn-default',
      color: '#4180ab',
      showButtons: false,
      header: 'ТС'};

    this.popup2.show(this.popup2.options);
  }

  clickButttonTest3() {

    this.popup3.options = {
      cancleBtnClass: 'btn btn-default',
      confirmBtnClass: 'btn btn-default ',
      color: '#4180ab',
      showButtons: false,
      header: 'Поле'};

    this.popup3.show(this.popup3.options);
  }

  clickButttonTest4() {

    this.serv.getAreaField(this.agrofieldList[this.changeAgroField].id).subscribe(data => {
      this.areaField = data;
    });
    console.log('area: ' + this.areaField);


    this.popup4.options = {
      cancleBtnClass: 'btn btn-default',
      confirmBtnClass: 'btn btn-default ',
      color: '#4180ab',
      showButtons: false,
      header: 'Площадь'};

    this.popup4.show(this.popup4.options);
  }

  filterForeCasts(filterVal: any) {
    clearInterval(this.interval);
    console.log(filterVal);
    this.filterValGet = filterVal;
    this.serv.getTransportClient(this.clientList[filterVal].id).subscribe(data => {
      this.transportList = data;
    });
    this.serv.getAgrofieldsClient(this.clientList[filterVal].keyName).subscribe(data => {
      this.agrofieldList = data;
    });
    this.getCords();
  }

  getCords() {
    this.interval = setInterval(() => {
      this.loadComponent();
    }, 3000);
  }

  loadComponent() {
    this.map.removeLayer(this.customLayer);

    this.serv.getCoordsTC(this.clientList[this.filterValGet].id).subscribe(data => {
      this.lastCoordsList = data;
      this.customLayer = this._mapService.getLayerForLastPoints(this.map, this.lastCoordsList);
    });
    console.log('load last coord');
  }

  onDateChanged(event: IMyDateModel) {
    this.timestampCalendar = event.epoc;
    console.log('load date ' + Date.now());
    console.log('event epoc ' + event.epoc);
  }

  changeTransportList(val: any) {
    console.log(val);
    this.changeTransport = val;
  }

  changeAgrotList(val: any) {
    console.log(val);
    this.edited = false;
    this.changeAgroField = val;
    this.agrofieldThis = this.agrofieldList[val].name;
  }

  YourConfirmEvent1() {
   this.popup1.hide();
  }
  YourConfirmEvent2() {
    this.popup2.hide();
  }
  YourConfirmEvent3() {
    this.popup3.hide();
  }
  YourConfirmEvent4() {
    this.popup4.hide();
  }

  loadCoordForTrack() {
    // this.map.remove();
    // this.customLayer.removeLayer(1);
    clearInterval(this.interval);
    this.map.removeLayer(this.customLayer);
    // this._mapService.plotActivityMarkers(8); // вызов сервиса
    // если не инициализирована карта
    // при перерисовке нужна деинииализация

    /* console.log('client' + this.clientList);
    console.log('client' + this.clientList[0]);
    console.log('client' + this.clientList[0].name);
    console.log('changetransport' + this.changeTransport);
    console.log('tansportList' + this.transportList);
    console.log('IMEI' + this.transportList[0].gosnumber);
    console.log('Time' + this.timestampCalendar);
    console.log('AgroField' + this.agrofieldList);
    console.log('AgroField' + this.agrofieldList[0]);
    console.log('AgroField' + this.agrofieldList[0].name); */

    this.newtimeCalendar = this.timestampCalendar + 86300;
    this.serv.getCoordForTrack(this.transportList[this.changeTransport].trackerIMEI, this.timestampCalendar, this.newtimeCalendar).subscribe
    (data => {
      this.coordList = data;
      this.customLayer = this._mapService.getNewLayerTrack(this.map, this.coordList);
    });
    console.log('loadCoords');
  }
}
