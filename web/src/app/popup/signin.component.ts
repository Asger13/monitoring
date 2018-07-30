import {Component, OnInit} from '@angular/core';

import {Router} from '@angular/router';


@Component({
  template: '<h1>Popup template</h1> <button (click)="onClose()"> Close </button>'
})
export class SignInModalComponent  {


  constructor (private router: Router) {}


  onClose() {
    this.router.navigate([{ outlets: { popup: null }}]);
  }
}
