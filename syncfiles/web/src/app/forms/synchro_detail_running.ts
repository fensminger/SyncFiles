import { Component, Input } from '@angular/core';
import { DatePipe } from '@angular/common';
import {Title} from '@angular/platform-browser';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';

@Component({
  selector: 'synchro-detail-run',
  viewProviders: [Title],
  templateUrl: 'synchro_detail_running.html',
  styleUrls: ['synchro_detail_running.css']
})
export class SynchroDetailRun {
  ongletIndex : number;

  @Input('syncFilesinfo')
  syncFilesinfo : any;

  constructor(title : Title) {
    title.setTitle("SyncFiles - Running synchronization");
  }

  public getTitle(sync:any) :string {
    let datePipe = new DatePipe("fr");
    let res = sync.id + ' - ' + sync.title + ' (' + datePipe.transform(sync.lastStateDate, "dd/MM/yyyy HH:mm:ss") + ')';
    // console.log("getTitle : " + res);
    return res;
  }

}
