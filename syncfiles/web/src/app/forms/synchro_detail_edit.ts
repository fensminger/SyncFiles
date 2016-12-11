import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators, FormGroup} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {SfcInput} from './sfc_input';
import {SynchroFilesService} from './SynchroFilesService';
import {Response} from "@angular/http";
import {ActivatedRoute} from "@angular/router";
import {Location} from '@angular/common';
import {Message, SelectItem} from 'primeng/primeng';

@Component({
  selector: 'synchro-detail-edit',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : 'synchro_detail_edit.html',
    styleUrls: ['synchro_detail_edit.css']
})
export class SynchroDetailEdit implements OnInit {
    synchroForm : FormGroup;
    isHttpRequest :boolean;
    id : String;
    version : number;
    model = {
        id : null,
        name : null,
        includeDir:false,
        includeExcludePatterns : []
    };
    modelModified = {
        id : null,
        name : null,
        cronExp : null,
        masterDir : null,
        slaveDir : null,
        includeExcludePatterns : []
    };
    includeExcludePatterns = [];

    patternTypes: SelectItem[];


    constructor(private _synchroFilesService : SynchroFilesService,
                private _fb: FormBuilder, title : Title,
                private route: ActivatedRoute,
                private location: Location,) {
        title.setTitle("SyncFiles - Detail d'une synchronisation");

        console.log("Paramètre" + this.id);

        this.patternTypes = [];
        this.patternTypes.push({label:'Exclude pattern regular expression files', value:false});
        this.patternTypes.push({label:'Include pattern regular expression files', value:true});


        // console.log('lodash version:', _.VERSION);

        this.synchroForm = _fb.group({
            'name': ['', Validators.required],
            'cronExp' : [''],
            'masterDir': ['', Validators.required],
            'slaveDir': ['', Validators.required],
            'includeDir': ['']
        });
        route.params.subscribe(params => { this.id = params['id']; });
    }

    ngOnInit() {
        if (this.id!=null) {
            this.isHttpRequest = true;
            this._synchroFilesService.loadOne(this.id).subscribe(
                (r : any) => {
                    this.isHttpRequest = false;
                    this.model = r;
                    if (this.model.includeExcludePatterns) {
                        this.includeExcludePatterns.length = this.model.includeExcludePatterns.length;
                    }
                    this.modelModified = JSON.parse(JSON.stringify(this.model));
                    console.log("Loading : " + JSON.stringify(this.model));
                },
                (e : any) => {
                    this.isHttpRequest = false;
                    console.log("Error : " + e);
                }
            );
        }
    }

    createSynchro(value : any) {
        console.log('Synchro to save : ' + JSON.stringify(this.model));
        this.isHttpRequest = true;
        value.version = this.version;
        this.modelModified.name = this.synchroForm.get('name').value;
        this.modelModified.cronExp = this.synchroForm.get('cronExp').value;
        this.modelModified.masterDir = this.synchroForm.get('masterDir').value;
        this.modelModified.slaveDir = this.synchroForm.get('slaveDir').value;
        this._synchroFilesService.saveDetail(this.modelModified).subscribe(
            (r : any) => {
                this.isHttpRequest = false;
                this.model = r;
                this.modelModified = JSON.parse(JSON.stringify(this.model));
                console.log("OK" + JSON.stringify(r));
                this.alerts.push({severity:'info', summary:'Info', detail:'Saved synchronization detail.'});
            },
            (e : any) => {
                this.isHttpRequest = false;
                console.log("Error : " + JSON.stringify(e));
                this.alerts.push({severity:'error', summary:'Error', detail:'Unable to save detail synchronization...'});
            }
        );
    }

    public alerts:Message[] = [
    ];

    public addPattern() : void {
        if (!this.modelModified.includeExcludePatterns) {
            this.model.includeExcludePatterns = [];
            this.modelModified.includeExcludePatterns = [];
            this.includeExcludePatterns = [];
        }
        this.model.includeExcludePatterns.push("");
        this.modelModified.includeExcludePatterns.push("");
        this.includeExcludePatterns.push("");
        console.log("Model : " + JSON.stringify(this.modelModified));
    }

    public removePattern(index:number) : void {
        this.modelModified.includeExcludePatterns.splice(index, 1);
        this.model.includeExcludePatterns.splice(index, 1);
    }

    public chgPattern(i : number, event : any) {
        this.modelModified.includeExcludePatterns[i] = event.target.value;
        console.log("Model : " + JSON.stringify(this.modelModified));
    }

    public duplicate() {
      this.model.name = "Copy of " + this.model.name;
      this.modelModified.name = this.model.name;
      this.id = null;
      this.model.id = null;
      this.modelModified.id = null;
      this.location.replaceState("detail");
    }
}
