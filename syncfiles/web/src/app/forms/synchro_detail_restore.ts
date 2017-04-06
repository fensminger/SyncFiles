import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {FormBuilder, Validators, FormGroup} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {SfcInput} from './sfc_input';
import {SynchroFilesService} from './SynchroFilesService';
import {Response} from "@angular/http";
import {ActivatedRoute} from "@angular/router";
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
  }
}
