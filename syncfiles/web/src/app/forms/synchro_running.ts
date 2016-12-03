import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import {Title} from '@angular/platform-browser';
import {Response} from "@angular/http";
import {SynchroRunningService} from "./synchro_running.service";

@Component({
  selector: 'synchro-run',
  viewProviders: [Title],
  templateUrl: 'synchro_running.html',
  styleUrls: ['synchro_running.css']
})
export class SynchroRun implements OnInit, OnDestroy {

  ongletIndex : number;
  syncFilesinfo : any;

  constructor(title : Title, private synchroRunningService : SynchroRunningService
    , private router: Router) {
    title.setTitle("SyncFiles - Running synchronization");
  }

  public ngOnInit() {
    this.initSyncFilesInfo(this.synchroRunningService.syncFilesinfo);
    this.synchroRunningService.syncFilesinfoObservable.subscribe(
        s => {
          this.initSyncFilesInfo(s);
        }
    );
    
  }

  public initSyncFilesInfo(s : any) {
      this.syncFilesinfo = s;
      if (this.syncFilesinfo!=null) {
        this.ongletIndex = this.syncFilesinfo.length-1;
      }
  }

  public ngOnDestroy() {
  }

  public getTitle(sync:any) :string {
    let datePipe = new DatePipe("fr");
    let res = sync.id + ' - ' + sync.title + ' (' + datePipe.transform(sync.lastStateDate, "dd/MM/yyyy HH:mm:ss") + ')';
    return res;
  }

  onTabActivate(e) {
    console.log('TabClick');
      this.ongletIndex = e.index;
  }

}
