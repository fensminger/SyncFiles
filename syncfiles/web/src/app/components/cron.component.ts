import {Component, Input, Output, ElementRef, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {Observable} from 'rxjs/Rx';
import {SelectItem, Message} from 'primeng/primeng';
import {SynchroFilesService} from '../forms/SynchroFilesService';

@Component({
    selector: 'syncfiles-cron',
    templateUrl : './cron.component.html'
})
export class SyncFilesCronComponent extends OnChanges {
    @Input() placeholder: string;
    @Output() value = new EventEmitter();

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

  private cronCalc : any = {};
  private msgs: Message[] = [];

  constructor(private elementRef: ElementRef, private synchroFilesService : SynchroFilesService) {
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
    this.scheduleTypes.push({label:'Minutes', value:'MINUTES'});
    this.scheduleTypes.push({label:'Hourly', value:'HOURLY'});
    this.scheduleTypes.push({label:'Daily', value:'DAILY'});
    this.scheduleTypes.push({label:'Weekly', value:'WEEKLY'});
    this.scheduleTypes.push({label:'Monthly', value:'MONTHLY'});
    this.scheduleTypes.push({label:'Yearly', value:'YEARLY'});
    this.scheduleTypes.push({label:'Custom cron expression', value:'CUSTOM_CRON_EXPRESSION'});

  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log("ngOnChanges");
  }


  public calcSchedule() {
    this.cronCalc.nextCron = [];
    this.cronCalc.description=null;
    this.cronCalc.msgError=null;
    this.synchroFilesService.calcSchedule(this.schedule).subscribe(
      (r : any) => {
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
        console.log("Error : " + JSON.stringify(e));
      });
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
