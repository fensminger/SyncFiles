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

    constructor(private _synchroFilesService : SynchroFilesService,
                private _fb: FormBuilder, title : Title) {
        title.setTitle("SyncFiles - Synchronisation restore");
    }

    ngOnInit() {
    }

    public changeLocalDir(localPath : any) {
      console.log("Local dir change : " + localPath);
    }
}
