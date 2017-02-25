///<reference path="../../../node_modules/@angular/core/src/metadata/lifecycle_hooks.d.ts"/>
import {Component, Input, Output, ElementRef, EventEmitter, OnChanges, SimpleChanges, ChangeDetectorRef, OnInit, DoCheck} from '@angular/core';
import {Observable} from 'rxjs/Rx';
import {SelectItem, Message} from 'primeng/primeng';
import {SynchroFilesService} from '../forms/SynchroFilesService';

@Component({
    selector: 'syncfiles-cron',
    templateUrl : './cron.component.html'
})
export class SyncFilesCronComponent extends OnChanges implements OnInit, DoCheck {
    @Input("sched") _value : any;
    @Output("onChange") changeEvent = new EventEmitter();

    private keyDownEventEmitter = new EventEmitter();

  weekNumberInMonth: SelectItem[];
  days: SelectItem[];
  months: SelectItem[];
  scheduleTypes: SelectItem[];

  public schedule : any = {
    type : 'WEEKLY',
    minutes : {},
    hourly : {},
    daily : {
      type : 'WEEK_DAY'
    },
    weekly : {
      days: []
    },
    monthly : {
      type : 'NUMBER_DAY',
      numberOfWeek : 1,
      dayOfWeek : 'MON'
    },
    yearly : {
      type : 'EVERY',
      month : 1,
      numberInMonth : 1,
      dayOfWeek : 'MON',
      numberOfMonth : '1'
    },
    cronExp : ''
  };

  private testTime : any;

  private cronCalc : any = {};
  private msgs: Message[] = [];
  private isFirstLoad: boolean =true;

  constructor(private elementRef: ElementRef, private synchroFilesService : SynchroFilesService, private changeDetectorRef : ChangeDetectorRef) {
    super();
    this.weekNumberInMonth = [];
    this.weekNumberInMonth.push({label:'First', value:1});
    this.weekNumberInMonth.push({label:'Second', value:2});
    this.weekNumberInMonth.push({label:'Third', value:3});
    this.weekNumberInMonth.push({label:'Fourth', value:4});

    this.days = [];
    this.days.push({label:'Monday', value:'MON'});
    this.days.push({label:'Tuesday', value:'TUE'});
    this.days.push({label:'Wednesday', value:'WED'});
    this.days.push({label:'Thursday', value:'THU'});
    this.days.push({label:'Friday', value:'FRI'});
    this.days.push({label:'Saturday', value:'SAT'});
    this.days.push({label:'Sunday', value:'SUN'});

    this.months = [];
    this.months.push({label:'January', value:1});
    this.months.push({label:'February', value:2});
    this.months.push({label:'March', value:3});
    this.months.push({label:'April', value:4});
    this.months.push({label:'May', value:5});
    this.months.push({label:'June', value:6});
    this.months.push({label:'July', value:7});
    this.months.push({label:'August', value:8});
    this.months.push({label:'September', value:9});
    this.months.push({label:'October', value:10});
    this.months.push({label:'November', value:11});
    this.months.push({label:'December', value:12});

    this.scheduleTypes = [];
    this.scheduleTypes.push({label:'Manual execution', value:'MANUAL'});
    this.scheduleTypes.push({label:'Minutes', value:'MINUTES'});
    this.scheduleTypes.push({label:'Hourly', value:'HOURLY'});
    this.scheduleTypes.push({label:'Daily', value:'DAILY'});
    this.scheduleTypes.push({label:'Weekly', value:'WEEKLY'});
    this.scheduleTypes.push({label:'Monthly', value:'MONTHLY'});
    this.scheduleTypes.push({label:'Yearly', value:'YEARLY'});
    this.scheduleTypes.push({label:'Custom cron expression', value:'CUSTOM_CRON_EXPRESSION'});

    // const eventStream = Observable.fromEvent(elementRef.nativeElement, 'keyup')
    //   .map(() => "ok")
    //   .debounceTime(300)
    //   .distinctUntilChanged();
    //
    // eventStream.subscribe(input => {
    //   console.log("keyUp global....");
    // });

    this.keyDownEventEmitter
      .debounceTime(400)
      .subscribe(v => {
        this.calcSchedule();
      });

  }

  ngDoCheck(): void {
    if (this.value !=null && this.isFirstLoad) {
      console.log("DoCheck : " + this.value);
      this.isFirstLoad = false;
      this.schedule = this.value;
      // if (this.schedule.daily.time!=null) {
      //   this.schedule.daily.time = new Date(this.schedule.daily.time);
      // }
      // if (this.schedule.weekly.time!=null) {
      //   this.schedule.weekly.time = new Date(this.schedule.weekly.time);
      // }
      // if (this.schedule.monthly.time!=null) {
      //   this.schedule.monthly.time = new Date(this.schedule.monthly.time);
      // }
      // if (this.schedule.yearly.time!=null) {
      //   this.schedule.yearly.time = new Date(this.schedule.yearly.time);
      // }
      this.calcSchedule();
    }
  }


  ngOnInit(): void {
  }

  get value() : any {
    return this._value;
  }

  set value(val :any) {
    this._value = val;
    this.schedule = val;
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log("ngOnChanges");
  }


  public calcSchedule() {
    this.changeEvent.emit(this.schedule);
//    this.cronCalc.nextCron = [];
//     this.cronCalc.description=null;
//     this.cronCalc.msgError=null;
    // this.changeDetectorRef.detach();
    this.synchroFilesService.calcSchedule(this.schedule).subscribe(
      (r : any) => {
        // this.changeDetectorRef.reattach();
        // this.changeDetectorRef.detectChanges();
        this.cronCalc = r;
        this.schedule = this.cronCalc.schedule;
        this.msgs = [];
        if (this.cronCalc.msgError!=null) {
          this.msgs.push({severity:'error', summary:'Cron expression error', detail:this.cronCalc.msgError});
        }
        if (this.cronCalc.description!=null) {
          this.msgs.push({severity:'info', summary:'Cron expression description '+ this.cronCalc.schedule.cronExp + ' : ', detail:this.cronCalc.description});
        }
      },
      (e : any) => {
        // this.changeDetectorRef.reattach();
        console.log("Error : " + JSON.stringify(e));
      });
  }

  public eventKey(event:any) :any {
    this.keyDownEventEmitter.emit("keyUp");
  }

  public calcScheduleDay(event:any) :any {
    this.schedule.daily.type = event;
    this.calcSchedule();
  }
  public calcScheduleMonthly(event:any) :any {
    this.schedule.monthly.type = event;
    this.calcSchedule();
  }
  public calcScheduleYearly(event:any) :any {
    this.schedule.yearly.type = event;
    this.calcSchedule();
  }
}
