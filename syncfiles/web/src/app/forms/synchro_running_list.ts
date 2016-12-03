import {Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {URLSearchParams} from '@angular/http';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';
import {LazyLoadEvent} from 'primeng/primeng';

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

    tabName : string;
    infosList : any;
    id : string;

    filterName : string;

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute,
                private location: Location,
                private synchroFilesService : SynchroFilesService) {
                    this.pageNumber = 1;
    }


  public ngOnInit() {
    this.route.params.subscribe(params => { 
        this.tabName = params['tabName'];
        this.id = params['id']; 
    });
  }

  public ngOnDestroy() {
  }

  private loadInfos() {
    let params: URLSearchParams = new URLSearchParams();
    params.set('filterName', this.filterName);
    this.synchroFilesService.viewList(this.id, "SOURCE", this.pageNumber, this.numberRowPerPages, params).subscribe(
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
    
}
