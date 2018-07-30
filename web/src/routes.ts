import {RouterModule, Routes} from '@angular/router';
import {ActivityListComponent} from './app/activity-list/activity-list.component';
import {MapComponent} from './app/map/map.component';
import {SignInModalComponent} from './app/popup/signin.component';
import {NgModule} from '@angular/core';

export const appRoutes: Routes = [
  {path: 'signin', component: SignInModalComponent, outlet: 'popup'},
  {path: 'monitoring', component: MapComponent},
  {path: '', redirectTo: '/monitoring', pathMatch: 'full'}
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
