import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {FormBuilder, Validators, FormGroup, FormControl} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {SynchroFilesService} from './SynchroFilesService';
import {ActivatedRoute} from "@angular/router";
import {Location} from '@angular/common';
import {Message, SelectItem} from 'primeng/primeng';

@Component({
  selector: 'synchro-detail-edit',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : './synchro_detail_edit.html',
    styleUrls: ['./synchro_detail_edit.css']
})
export class SynchroDetailEdit implements OnInit {

    @Output()
    syncFilesInfo : EventEmitter<any> = new EventEmitter();

    synchroForm : FormGroup;
    isHttpRequest :boolean;
    id : String;
    version : number;
    model = {
        id : null,
        name : null,
        masterDir : null,
        slaveDir : null,
        schedule : null,
        includeDir:false,
        includeExcludePatterns : []
    };

  patternTypes: SelectItem[];
  types: SelectItem[];


    constructor(private _synchroFilesService : SynchroFilesService,
                private _fb: FormBuilder, title : Title,
                private route: ActivatedRoute,
                private location: Location,) {
        title.setTitle("SyncFiles - Detail d'une synchronisation");

        console.log("Paramètre" + this.id);

        this.patternTypes = [];
        this.patternTypes.push({label:'Exclude pattern regular expression files', value:false});
        this.patternTypes.push({label:'Include pattern regular expression files', value:true});

      this.types = [];
      this.types.push({value:'START', label:'Start with'});
      this.types.push({value:'END', label:'End width'});
      this.types.push({value:'CONTAIN', label:'Contains'});
      this.types.push({value:'REGEXP', label:'Reg. Exp. JAVA'});


      // console.log('lodash version:', _.VERSION);

        this.synchroForm = _fb.group({
            'name': ['', Validators.required],
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
                    this.updatePatternControler();
                    console.log("Loading : " + JSON.stringify(this.model));
                },
                (e : any) => {
                    this.isHttpRequest = false;
                    console.log("Error : " + e);
                }
            );
        }
    }

  private updatePatternControler() {
    let i = 0;
    for (let pattern of this.model.includeExcludePatterns) {
      this.synchroForm.addControl("val_" + i, new FormControl());
      this.synchroForm.get("val_" + i).setValue(pattern.value);
      this.synchroForm.addControl("type_" + i, new FormControl());
      this.synchroForm.get("type_" + i).setValue(pattern.type);
      i++;
    }
  }

  createSynchro(value : any) {
    console.log('Synchro to save : ' + JSON.stringify(this.model));
    this.isHttpRequest = true;
        value.version = this.version;
        this.model.name = this.synchroForm.get('name').value;
        this.model.masterDir = this.synchroForm.get('masterDir').value;
        this.model.slaveDir = this.synchroForm.get('slaveDir').value;
        if (!this.model.slaveDir.startsWith("/")) {
          this.model.slaveDir = "/"+this.model.slaveDir.substring(1);
        }
        this._synchroFilesService.saveDetail(this.model).subscribe(
            (r : any) => {
                this.isHttpRequest = false;
                this.model = r;
                this.syncFilesInfo.emit(this.model);
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
        if (!this.model.includeExcludePatterns) {
            this.model.includeExcludePatterns = [];
        }
        this.model.includeExcludePatterns.push({type:'START', value:null});
        this.updatePatternControler();
        console.log("Model : " + JSON.stringify(this.model));
    }

    public removePattern(index:number) : void {
        this.model.includeExcludePatterns.splice(index, 1);
        this.updatePatternControler();
    }

    public chgPattern(i : number, event : any) {
        this.model.includeExcludePatterns[i].value = event.target.value;
        console.log("Model Value : " + JSON.stringify(this.model));
    }

    public chgType(i : number, event : any) {
      this.model.includeExcludePatterns[i].type = event.value;
      console.log("Model type : " + JSON.stringify(this.model));
    }

    public duplicate() {
      this.model.name = "Copy of " + this.model.name;
      this.id = null;
      this.model.id = null;
      this.location.replaceState("detail");
      this.syncFilesInfo.emit(this.model);
    }

    public patternTrackBy(index,item) : any {
      return item;
    }

    public changeSchedule(schedule : any) {
      this.model.schedule = schedule;
    }
}
