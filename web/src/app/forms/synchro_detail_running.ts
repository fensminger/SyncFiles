import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import {Title} from '@angular/platform-browser';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';

@Component({
  selector: 'synchro-detail-run',
  viewProviders: [Title],
  templateUrl: './synchro_detail_running.html',
  styleUrls: ['./synchro_detail_running.css']
})
export class SynchroDetailRun {
  ongletIndex : number;

  @Input('syncFilesinfo')
  syncFilesinfo : any;

  constructor(title : Title
    , private router: Router) {
    title.setTitle("SyncFiles - Running synchronization");
  }

  public getTitle(sync:any) :string {
    let datePipe = new DatePipe("fr");
    let res = sync.id + ' - ' + sync.title + ' (' + datePipe.transform(sync.lastStateDate, "dd/MM/yyyy HH:mm:ss") + ')';
    // console.log("getTitle : " + res);
    return res;
  }

  showRunningInfos(id : string, index : number) :boolean {
    let originFile : string;
    if (index==0) {
      originFile = "SOURCE";
    } else if (index==1) {
      originFile = "TARGET";
    } else {
      originFile = "SYNCHRO";
    }
    console.log("showRunningInfos : " + index + " ->" + originFile);
    this.router.navigate(['/running-infos', id, originFile, 0]);
    return false;
  }

}
