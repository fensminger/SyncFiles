import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';

@Component({
  selector: 'synchro-running-infos',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : 'synchro_running_infos.html',
    styleUrls: ['synchro_running_infos.css']
})
export class SynchroRunningInfos implements OnInit {

    index : number;
    syncFilesinfo : any;
    id : string;

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute,
                private location: Location,
                private synchroFilesService : SynchroFilesService) {
        title.setTitle("SyncFiles - Detail");

    }

  public onChange(e) {
    this.index = e.index;
    this.location.replaceState("/running-infos/"+this.id+"/"+this.index);
  }
    

  public ngOnInit() {
    this.route.params.subscribe(params => { 
        this.index = params['index'];
        this.id = params['id']; 
        console.log("index : " + this.index);
        console.log("id : " + this.id);
    });
  }

  public ngOnDestroy() {
  }

}
