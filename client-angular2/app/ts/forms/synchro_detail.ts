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
import {RouteParams} from "@angular/router-deprecated";

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
    model = {};

    constructor(private _synchroFilesService : SynchroFilesService,
                private _routeParams : RouteParams,
                private _fb: FormBuilder, title : Title) {
        title.setTitle("SyncFiles - Detail d'une synchronisation");

        this.id = _routeParams.get('id');
        console.log("Paramètre" + this.id);
        this.synchroForm = _fb.group({
            'name': ['', Validators.required],
            'cronExp' : [''],
            'masterDir': ['', Validators.required],
            'slaveDir': ['', Validators.required],
        });
    }

    ngOnInit() {
        if (this.id!=null) {
            this.isHttpRequest = true;
            this._synchroFilesService.loadOne(this.id).subscribe(
                (r : any) => {
                    this.isHttpRequest = false;
                    this.model = r;
                },
                (e : any) => {
                    this.isHttpRequest = false;
                    console.log("Error : " + e);
                }
            );
        }
    }

    createSynchro(value : any) {
        console.log('Synchro à sauvegarder : ' + JSON.stringify(value));
        this.isHttpRequest = true;
        value.version = this.version;
        this._synchroFilesService.saveDetail(value).subscribe(
            (r : any) => {
                this.isHttpRequest = false;
                console.log("OK" + r.text());
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

}
