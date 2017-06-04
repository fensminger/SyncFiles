import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';

@Component({
  selector: 'synchro-detail',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : './synchro_detail.html',
    styleUrls: ['./synchro_detail.css']
})
export class SynchroDetail implements OnInit {

    tabName : string;
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
    console.log('change tab');
    let oldTabName = this.tabName;
    if (e.index==0) {
        this.tabName = "general";
    } else if (e.index==1) {
        this.tabName = "execution";
    }  else {
      this.tabName = "restore";
    }
    this.location.replaceState(this.location.path(false).replace(oldTabName, this.tabName));
  }


    public initSyncFilesInfo(s :any) {
        if (s==null || s===undefined) {
            return;
        }
        for(let sync of s) {
            if (this.id==sync.paramSyncFilesId) {
                this.syncFilesinfo = sync;
            }
        }
    }

  public ngOnInit() {
    this.route.params.subscribe(params => {
        this.tabName = params['tabName'];
        this.id = params['id'];
        this.synchroRunningService.syncFilesinfoObservable.subscribe(
          s => {
            this.initSyncFilesInfo(s);
          }
        );
        this.synchroRunningService.loadOne(this.id).subscribe(
            (r : any) => {
                console.log("Msg chargé : " + r);
                this.syncFilesinfo = r;
            },
            (e : any) => {
                console.log("Error : " + e);
            }
        );

    });
  }

  public ngOnDestroy() {
  }

  startSynchro() :void {
      if (this.syncFilesinfo!=null) {
        this.syncFilesinfo.running = true;
      }
    this.synchroFilesService.synchronize(this.id).subscribe(
      (r:any) => {
      },
        (e : any) => {
          console.log("Error startSynchro : " + JSON.stringify(e));
        }
    );
  }

  startSimulation() :void {
    if (this.syncFilesinfo!=null) {
      this.syncFilesinfo.running = true;
    }
    this.synchroFilesService.simulation(this.id).subscribe(
      (r:any) => {
      },
      (e : any) => {
        console.log("Error startSimulation : " + JSON.stringify(e));
      }
    );
  }

  synchronizeAfterSimulation() :void {
    if (this.syncFilesinfo!=null) {
      this.syncFilesinfo.running = true;
    }
    this.synchroFilesService.synchronizeAfterSimulation(this.id).subscribe(
      (r:any) => {
      },
      (e : any) => {
        console.log("Error synchronizeAfterSimulation : " + JSON.stringify(e));
      }
    );
  }

  changeSyncFileInfo(s : any) {
      console.log("param changed.");
      if (this.id != s.id) {
        this.id = s.id;
        this.syncFilesinfo = null;
      }
  }
}
