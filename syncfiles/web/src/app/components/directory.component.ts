import {Component, Input, Output, ElementRef, EventEmitter, OnInit} from '@angular/core';
import {SynchroFilesService} from "app/forms/SynchroFilesService";
import {SelectItem, MenuItem, Message} from 'primeng/primeng';

@Component({
    selector: 'syncfiles-dir',
    templateUrl : './directory.component.html'
})
export class SyncFilesDirComponent implements OnInit {
    @Input("dir") dir : String;
    @Output("onChange") changeEvent = new EventEmitter();

    private localPath : string = "";
    private selectedLocalDir : string = null;
    private localDirs : SelectItem[];

  private pathMenuItems : MenuItem[] = [];

  private pathHome: MenuItem;

  constructor(private elementRef: ElementRef, private synchroFilesService : SynchroFilesService) {
  }

  ngOnInit(): void {
    this.pathHome = {
      label: "", command : e => {
        this.localPath = "";
        this.pathMenuItems = [];
        this.loadDirs(this.localPath);
      }, url : null
    };
    this.loadDirs(this.localPath);
  }

  private loadDirs(pLocalPath : string) {
    this.synchroFilesService.loadDir(pLocalPath==""?null:pLocalPath).subscribe(
      (r: any) => {
        console.log("Load dir : " + JSON.stringify(r));
        this.localDirs = this.convertToSelectedItem(r);
      },
      (e: any) => {
        console.log("Error : " + JSON.stringify(e));
      });
  }

  private convertToSelectedItem(r: any) : SelectItem[] {
    let res : SelectItem[] = [];
    for(let dir of r) {
      res.push({label:dir.name, value:dir.name});
    }
    return res;
  }

  private selectDir(event : any) : void {
    console.log('Select event : ' + event.value);
    if (this.localPath=="") {
      this.localPath = event.value;
    } else {
      if (!this.localPath.endsWith("\\")) {
        this.localPath = this.localPath + "/";
      }
      this.localPath = this.localPath + event.value;
    }
    console.log('localPath : ' + this.localPath);
    this.loadDirs(this.localPath);
    this.pathMenuItems.push({label: event.value, command : e => {
      this.localPath = e.item.url;
      let index = this.pathMenuItems.indexOf(e.item);
      this.pathMenuItems = this.pathMenuItems.slice(0, index+1);
      this.loadDirs(this.localPath);
    }, url : this.localPath});
  }
}
