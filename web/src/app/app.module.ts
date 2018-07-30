import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {UserComponent} from './user/user.component';
import {HttpModule} from '@angular/http';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AgmCoreModule } from '@agm/core';
import { MapComponent } from './map/map.component';
import { ActivityListComponent } from './activity-list/activity-list.component';
import {ActivityService} from './services/activity.service';
import {appRoutes, AppRoutingModule} from '../routes';
import {RouterModule} from '@angular/router';
import {MapService} from './services/map.service';
import { MyDatePickerModule } from 'mydatepicker';
import { UiMapComponent } from './ui-map/ui-map.component';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {UiMapService} from './services/ui-map.service';
import {SignInModalComponent} from './popup/signin.component';
import {PopupModule} from 'ng2-opd-popup';

@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    MapComponent,
    ActivityListComponent,
    UiMapComponent,
    SignInModalComponent
  ],
  imports: [
    BrowserModule,
    HttpModule,
    CommonModule,
    MyDatePickerModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    PopupModule.forRoot()
  ],

  providers: [ActivityService, MapService, UiMapService],
  bootstrap: [AppComponent]
})
export class AppModule { }
