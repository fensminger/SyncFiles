import {Component, OnInit, Input} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from "@angular/router";
import {SynchroRunningService} from "./synchro_running.service";
import {Location} from '@angular/common';
import {SynchroFilesService} from './SynchroFilesService';
import {URLSearchParams} from '@angular/http';
import {TreeNode} from 'primeng/primeng';
import {SelectItem} from 'primeng/primeng';
import {SynchroRunningGenInfos} from './synchro_running_list';

@Component({
  selector: 'synchro-running-tree',
  viewProviders: [Title],
    providers: [SynchroFilesService],
    templateUrl : './synchro_running_tree.html',
    styleUrls: ['./synchro_running_tree.css']
})
export class SynchroRunningTree extends SynchroRunningGenInfos implements OnInit {

  @Input()
  id : string;
  @Input()
  originFile : string;

  treeNodeList : TreeNode[] = [];

  selectedNode : TreeNode;

    constructor(private title : Title,
                private synchroRunningService : SynchroRunningService,
                private route: ActivatedRoute,
                private location: Location,
                private synchroFilesService : SynchroFilesService) {
                    super();
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

  public ngOnInit() {
    if (this.id==null) {
      this.route.params.subscribe(params => {
        this.originFile = params['originFile'];
        this.id = params['id'];
        this.loadInfos(null, "/");
      });
    } else {
      this.originFile = "TARGET";
      this.loadInfos(null, "/");
    }
  }

  public ngOnDestroy() {
  }

  private loadInfos(parentNode : TreeNode, parentPath : string) {
    let params: URLSearchParams = new URLSearchParams();
    params.set('parentPath', parentPath);

    this.synchroFilesService.viewTree(this.id, this.originFile, params).subscribe(
        (r : any) => {
            console.log("Data loaded : tree");
            //console.log("Msg chargé : " + JSON.stringify(r));
            if (parentNode==null) {
                this.treeNodeList = r;
            } else {
                parentNode.children = r;
            }
        },
        (e : any) => {
            console.log("Error : " + e);
        }
    );
  }

  private nodeExpand(event) {
      this.loadInfos(event.node, "/" + event.node.data.relativePathString + "/");
  }

}
