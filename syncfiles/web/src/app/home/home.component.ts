import { Component, OnInit } from '@angular/core';
import {BreadCrumbService} from '../bread-crumb.service'

@Component({
  selector: 'app-login',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private breadCrumbService : BreadCrumbService) {}

  ngOnInit() {
    this.breadCrumbService.changePage([{label : "Page principale"}]);
  }

}
