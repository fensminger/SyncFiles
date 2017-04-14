import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {FormBuilder, Validators, FormGroup} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {SfcInput} from './sfc_input';
import {RestoreInfo, SynchroFilesService} from './SynchroFilesService';
import {Response} from "@angular/http";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from '@angular/common';
import {Message, SelectItem} from 'primeng/primeng';

@Component({
  selector: 'synchro-detail-restore',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : './synchro_detail_restore.html',
    styleUrls: ['./synchro_detail_restore.css']
})
export class SynchroDetailRestore implements OnInit {

  id : String;

  localPath : string = null;
  targetData : any = null;

  constructor(private _synchroFilesService : SynchroFilesService,
              private _fb: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              title : Title) {
      title.setTitle("SyncFiles - Synchronisation restore");
    route.params.subscribe(params => { this.id = params['id']; });
  }

  ngOnInit() {
  }

  public changeLocalDir(localPath : any) {
    console.log("Restore : Local dir change : " + localPath);
    this.localPath = localPath;
  }

  public changeTargetDir(event : any) {
    this.targetData = event;
    console.log(this.targetData);
  }

  public startRestore() {
    console.log("Start restore from " + this.targetData.relativePathString + " to " + this.localPath);
    let restoreInfo : RestoreInfo = {
      idParamSyncFiles : <string> this.id,
      remoteHubicPath : this.targetData.relativePathString,
      localPath : this.localPath
    };
    this._synchroFilesService.restore(restoreInfo).subscribe(
      (r:any) => {
        this.router.navigate(['/detail', this.id, 'execution']);
      },
      (e : any) => {
        console.log("Error startSynchro : " + JSON.stringify(e));
      }
    );
  }
}
