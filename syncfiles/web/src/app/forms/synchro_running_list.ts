import {Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {URLSearchParams} from '@angular/http';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';
import {LazyLoadEvent} from 'primeng/primeng';
import {SelectItem} from 'primeng/primeng';

@Pipe({name: 'replace'})
export class ReplacePipe implements PipeTransform {
  transform(value: string, replace: string): string {
    let list : string[] = replace.split(':');
    return value.replace(new RegExp(list[0]) , list[1]);
  }
}

@Component({
  selector: 'synchro-running-list',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : 'synchro_running_list.html',
    styleUrls: ['synchro_running_list.css']
})
export class SynchroRunningList implements OnInit {

    pageNumber : number;
    numberRowPerPages = 50;

    infosList : any;
    id : string;
    originFile : string;

    filterName : string;
    startDate : Date;
    endDate : Date;

    fileInfoAction: SelectItem[];
    selectedInfoAction : string[] = [];
    fileInfoActionMap : Map<string, string> = new Map();

    synchroStateAction: SelectItem[];
    selectedSynchroStateAction : string[] = [];
    syncStateActionMap : Map<string, string> = new Map();

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute,
                private location: Location,
                private synchroFilesService : SynchroFilesService) {
                    this.pageNumber = 1;
        this.fileInfoAction = [];
        this.fileInfoAction.push({label:'Create', value:'CREATE'});
        this.fileInfoAction.push({label:'Delete', value:'DELETE'});
        this.fileInfoAction.push({label:'Nothing', value:'NOTHING'});
        this.fileInfoAction.push({label:'Update', value:'UPDATE'});
        for(let item of this.fileInfoAction) {
            this.selectedInfoAction.push(item.value);
            this.fileInfoActionMap[item.value] = item.label;
        }

        this.synchroStateAction = [];
        this.synchroStateAction.push({label:'Finished', value:'FINISHED'});
        this.synchroStateAction.push({label:'Error', value:'ERROR'});
        this.synchroStateAction.push({label:'Updating', value:'UPDATING'});
        this.synchroStateAction.push({label:'Waiting for update', value:'WAITING_FOR_UPDATE'});
        for(let item of this.synchroStateAction) {
            this.selectedSynchroStateAction.push(item.value);
            this.syncStateActionMap[item.value] = item.label;
        }
    }


  public ngOnInit() {
    this.route.params.subscribe(params => { 
        this.originFile = params['originFile'];
        this.id = params['id']; 
    });
  }

  public ngOnDestroy() {
  }

  private loadInfos() {
    let params: URLSearchParams = new URLSearchParams();
    params.set('filterName', this.filterName);

    let fileInfoActionsStr : string = "";
    let isFirst = true;
    for(let action of this.selectedInfoAction) {
        if (isFirst) {
            isFirst = false;
        } else {
            fileInfoActionsStr +=",";
        }
        fileInfoActionsStr += action;
    }
    if (fileInfoActionsStr!="") {
        params.set('fileInfoActionsStr', fileInfoActionsStr);
    }

    let syncStateActionsStr : string = "";
    isFirst = true;
    for(let action of this.selectedSynchroStateAction) {
        if (isFirst) {
            isFirst = false;
        } else {
            syncStateActionsStr +=",";
        }
        syncStateActionsStr += action;
    }
    if (syncStateActionsStr!="") {
        params.set('syncStateStr', syncStateActionsStr);
    }

    if (this.startDate!=null) {
        params.set('startDate', this.startDate.toISOString());
    }
    if (this.endDate!=null) {
        params.set('endDate', this.endDate.toISOString());
    }
    this.synchroFilesService.viewList(this.id, this.originFile, this.pageNumber, this.numberRowPerPages, params).subscribe(
        (r : any) => {
            this.infosList = r;
            console.log("Data loaded");
            //console.log("Msg chargé : " + JSON.stringify(r));
        },
        (e : any) => {
            console.log("Error : " + e);
        }
    );
  }

  loadLazyInfos(event: LazyLoadEvent) {
        //in a real application, make a remote request to load data using state metadata from event
        //event.first = First row offset
        //event.rows = Number of rows per page
        //event.sortField = Field name to sort with
        //event.sortOrder = Sort order as number, 1 for asc and -1 for dec
        //filters: FilterMetadata object having field as key and filter value, filter matchMode as value
        
        //imitate db connection over a network
        console.log("Event : " + JSON.stringify(event ));
        this.pageNumber = Math.floor(event.first/this.numberRowPerPages); 
        this.loadInfos();
    }

    getName(path:string) : string {
        let pos = path.lastIndexOf('/');
        if (pos<0) {
            return path;
        } else {
            return path.substr(pos+1);
        }
    }

    getPath(path:string) : string {
        let pos = path.lastIndexOf('/');
        if (pos<0) {
            return path;
        } else {
            return path.substr(0, pos);
        }
    }

    public searchChanged(value) {
        this.filterName = value;
        //console.log(value);
        // Make cool HTTP requests
        this.loadInfos();
    }

    public changeFileInfoAction(value) {
        console.log("changeFileInfoAction : " + this.selectedInfoAction);
        this.loadInfos();
    }
    
    public changeSyncStateAction(value) {
        console.log("changeSyncStateAction : " + this.selectedSynchroStateAction);
        this.loadInfos();
    }
    
    public dateChange(value) {
        console.log(value + " : " + this.startDate + " -> " + this.endDate);
        this.loadInfos();
        
    }

    public removeStartDate() {
        this.startDate = null;
        this.loadInfos();
    }
    public removeEndDate() {
        this.endDate = null;
        this.loadInfos();
    }
}
