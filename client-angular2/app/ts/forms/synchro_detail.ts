import {Component, OnInit} from '@angular/core';
import {
    CORE_DIRECTIVES,
    FORM_DIRECTIVES,
    FormBuilder,
    ControlGroup,
    Validators, Control
} from '@angular/common';
import {Title} from '@angular/platform-browser';
import {AlertComponent} from 'ng2-bootstrap/ng2-bootstrap';
import {SfcInput} from './sfc_input';
import {SynchroFilesService} from './SynchroFilesService';
import {Response} from "@angular/http";
import {ActivatedRoute, ROUTER_DIRECTIVES} from "@angular/router";

@Component({
  selector: 'synchro-detail',
  viewProviders: [Title],
    providers: [SynchroFilesService],
  directives: [AlertComponent, SfcInput, CORE_DIRECTIVES, FORM_DIRECTIVES],
    template : require('./synchro_detail.html')
})
export class SynchroDetail implements OnInit {
    synchroForm : ControlGroup;
    isHttpRequest :boolean;
    id : String;
    version : number;
    model = {
        includeExcludePatterns : []
    };
    modelModified = {
        name : null,
        cronExp : null,
        masterDir : null,
        slaveDir : null,
        includeExcludePatterns : []
    };
    includeExcludePatterns = [];

    constructor(private _synchroFilesService : SynchroFilesService,
                private _fb: FormBuilder, title : Title,
                private route: ActivatedRoute) {
        title.setTitle("SyncFiles - Detail d'une synchronisation");

        console.log("Paramètre" + this.id);
        this.synchroForm = _fb.group({
            'name': ['', Validators.required],
            'cronExp' : [''],
            'masterDir': ['', Validators.required],
            'slaveDir': ['', Validators.required],
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
                    console.log("Chargement de : " + JSON.stringify(this.model));
                },
                (e : any) => {
                    this.isHttpRequest = false;
                    console.log("Error : " + e);
                }
            );
        }
    }

    createSynchro(value : any) {
        console.log('Synchro à sauvegarder : ' + JSON.stringify(this.model));
        this.isHttpRequest = true;
        value.version = this.version;
        this.modelModified.name = this.synchroForm.find('name').value;
        this.modelModified.cronExp = this.synchroForm.find('cronExp').value;
        this.modelModified.masterDir = this.synchroForm.find('masterDir').value;
        this.modelModified.slaveDir = this.synchroForm.find('slaveDir').value;
        this._synchroFilesService.saveDetail(this.modelModified).subscribe(
            (r : any) => {
                this.isHttpRequest = false;
                this.model = r;
                this.modelModified = JSON.parse(JSON.stringify(this.model));
                console.log("OK" + JSON.stringify(r));
                this.alerts.push({msg: 'Sauvegarde effectu\u00E9e avec succ\u00E8s', type: 'info', closable: true});
            },
            (e : any) => {
                this.isHttpRequest = false;
                console.log("Error : " + JSON.stringify(e));
                this.alerts.push({msg: 'Impossible de sauvegarder...', type: 'error', closable: true});
            }
        );
    }

    public alerts:Array<Object> = [
    ];

    public closeAlert(i:number):void {
        this.alerts.splice(i, 1);
    }

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

    public chgPattern(i : number, event : any) {
        this.modelModified.includeExcludePatterns[i] = event.target.value;
        console.log("Model : " + JSON.stringify(this.modelModified));
    }
}
