import { Component, OnInit, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import {Title} from '@angular/platform-browser';
import {Response} from "@angular/http";
import {$WebSocket} from 'angular2-websocket/angular2-websocket';

@Component({
  selector: 'synchro-run',
  viewProviders: [Title],
  templateUrl: 'synchro_running.html',
  styleUrls: ['synchro_running.css']
})
export class SynchroRun implements OnInit, OnDestroy {
  ws : $WebSocket = null;

  ongletIndex : number;
  syncFilesinfo : any;

  constructor(title : Title) {
    title.setTitle("SyncFiles - Running synchronization");
  }

  public ngOnInit() {
    this.ws = new $WebSocket("ws://localhost:9900/websocket/syncfilesInfo");
    this.ws.send("Hello");
    this.ws.getDataStream().subscribe(
      res => {
        let msg : any = JSON.parse(res.data);
        this.syncFilesinfo = msg;
        if (this.syncFilesinfo!=null) {
          this.ongletIndex = this.syncFilesinfo.length-1;
        }
      },
      function(e) {
         console.log('Error: ' + e.message);
        },
      function() {
         console.log('Completed');
      }
    );
  }

  public ngOnDestroy() {
    this.ws.close(true);
  }

  public getTitle(sync:any) :string {
    let datePipe = new DatePipe("fr");
    let res = sync.id + ' - ' + sync.title + ' (' + datePipe.transform(sync.lastStateDate, "dd/MM/yyyy HH:mm:ss") + ')';
    // console.log("getTitle : " + res);
    return res;
  }

  onTabActivate(e) {
    console.log('TabClick');
      this.ongletIndex = e.index;
    console.log('TabClick : ' + this.ongletIndex);
  }
}
