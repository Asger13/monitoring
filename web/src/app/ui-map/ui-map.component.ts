import { Component, OnInit } from '@angular/core';
import {UiMapService} from '../services/ui-map.service';
import {Client} from '../client/client';


@Component({
  selector: 'app-ui-map',
  templateUrl: './ui-map.component.html',
  styleUrls: ['./ui-map.component.scss']
})
export class UiMapComponent implements OnInit {


  clientList: Object;

  constructor(private serv: UiMapService) {

  }

  ngOnInit() {
    this.loadClient();
  }
  private loadClient() {
    this.serv.getСlients().subscribe(data => {
      this.clientList = data;
    });
    console.log(this.clientList);
  }
}

