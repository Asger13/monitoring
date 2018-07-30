import {Http} from '@angular/http';
import {Injectable} from '@angular/core';


@Injectable()
export class UsersService {

  constructor(private http: Http) {}

  getUsers() {
    return this.http.get('https://randomuser.me/api/?inc=gender,name,picture,location&results=8&nat=gb');
  }
}
