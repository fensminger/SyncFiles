import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";

@Component({
  selector: 'synchro-detail',
  viewProviders: [Title],
    templateUrl : 'synchro_detail.html',
    styleUrls: ['synchro_detail.css']
})
export class SynchroDetail implements OnInit {

    tabName : string;
    syncFilesinfo : any;
    id : string;

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute) {
        title.setTitle("SyncFiles - Detail");

    }

  public onChange(e) {
    console.log('change tab');
    if (e.index==0) {
        this.tabName = "general";
    } else {
        this.tabName = "execution";
    }
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
        this.synchroRunningService.loadOne(this.id).subscribe(
            (r : any) => {
                console.log("Msg chargé : " + r);
                this.syncFilesinfo = r;
                this.synchroRunningService.syncFilesinfoObservable.subscribe(
                    s => {
                        this.initSyncFilesInfo(s);
                    }
                );
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
