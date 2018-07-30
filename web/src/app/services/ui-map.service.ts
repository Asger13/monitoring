import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Client} from '../client/client';
import {Transport} from '../client/transport';
import {Agrofield} from '../client/agrofield';
import {Timestamp} from 'rxjs/internal-compatibility';
import {Coord} from '../client/coord';
import {LastCoordsTC} from '../client/lastCoordsTC';

@Injectable()
export class UiMapService {

  private url = 'http://localhost:8080/rest/myservice';
  constructor(private http: HttpClient) { }


  getСlients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.url + '/client');
  }
  getTransportClient(id: number): Observable<Transport[]> {
    return this.http.get<Transport[]>(this.url + '/transport/client/' + id);
  }

  getAgrofieldsClient(key_name: string): Observable<Agrofield[]> {
    return this.http.get<Agrofield[]>(this.url + '/client/field/' + key_name);
  }

  getCoordForTrack(IMEI: string, date1: any, date2: any): Observable<Coord[]> {
    return this.http.get<Coord[]>(this.url + '/coordinate/' + IMEI + '/' + date1 + '/' + date2);
  }

  getCoordsTC(id: number): Observable<LastCoordsTC[]> {
    return this.http.get<LastCoordsTC[]>(this.url + '/transports/' + id);
  }

  getAreaField(id: number): Observable<number> {
    return this.http.get<number>(this.url + '/agrofield/area/' + id);
  }
}
