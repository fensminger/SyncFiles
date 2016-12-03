import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';

@Component({
  selector: 'synchro-running-tree',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : 'synchro_running_tree.html',
    styleUrls: ['synchro_running_tree.css']
})
export class SynchroRunningTree implements OnInit {

    tabName : string;
    syncFilesinfo : any;
    id : string;

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute,
                private location: Location,
                private synchroFilesService : SynchroFilesService) {

    }


  public ngOnInit() {
    this.route.params.subscribe(params => { 
        this.tabName = params['tabName'];
        this.id = params['id']; 
        this.synchroRunningService.loadOne(this.id).subscribe(
            (r : any) => {
                console.log("Msg chargé : " + r);
            },
            (e : any) => {
                console.log("Error : " + e);
            }
        );

    });
  }

  public ngOnDestroy() {
  }

}
