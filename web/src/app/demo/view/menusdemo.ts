import {Component,OnInit,ViewEncapsulation} from '@angular/core';
import {MenuItem} from 'primeng/primeng';

@Component({
    templateUrl: './menusdemo.html',
    styles: [`
        .ui-steps-item {
            width: 25%
        }
    `],
    encapsulation: ViewEncapsulation.None
})
export class MenusDemo implements OnInit {

    private breadcrumbItems: MenuItem[];

    private tieredItems: MenuItem[];

    private items: MenuItem[];

    private tabMenuItems: MenuItem[];

    private megaMenuItems: MenuItem[];

    private panelMenuItems: MenuItem[];

    private stepsItems: MenuItem[];

    ngOnInit() {
        this.breadcrumbItems = [];
        this.breadcrumbItems.push({label:'Categories'});
        this.breadcrumbItems.push({label:'Sports'});
        this.breadcrumbItems.push({label:'Football'});
        this.breadcrumbItems.push({label:'Countries'});
        this.breadcrumbItems.push({label:'Spain'});
        this.breadcrumbItems.push({label:'F.C. Barcelona'});
        this.breadcrumbItems.push({label:'Squad'});
        this.breadcrumbItems.push({label:'Lionel Messi', url: 'https://en.wikipedia.org/wiki/Lionel_Messi'});

        this.tabMenuItems = [
            {label: 'Stats', icon: 'ui-icon-insert-chart'},
            {label: 'Calendar', icon: 'ui-icon-date-range'},
            {label: 'Documentation', icon: 'ui-icon-book'},
            {label: 'Support', icon: 'ui-icon-help-outline'},
            {label: 'Social', icon: 'ui-icon-public'}
        ];

        this.tieredItems = [
            {
                label: 'File',
                icon: 'ui-icon-folder',
                items: [{
                        label: 'New',
                        icon: 'ui-icon-plus',
                        items: [
                            {label: 'Project'},
                            {label: 'Other'},
                        ]
                    },
                    {label: 'Open'},
                    {label: 'Quit'}
                ]
            },
            {
                label: 'Edit',
                icon: 'ui-icon-edit',
                items: [
                    {label: 'Undo', icon: 'ui-icon-undo'},
                    {label: 'Redo', icon: 'ui-icon-redo'}
                ]
            },
            {
                label: 'Help',
                icon: 'ui-icon-help-outline',
                items: [
                    {
                        label: 'Contents'
                    },
                    {
                        label: 'Search',
                        icon: 'ui-icon-search',
                        items: [
                            {
                                label: 'Text',
                                items: [
                                    {
                                        label: 'Workspace'
                                    }
                                ]
                            },
                            {
                                label: 'File'
                            }
                    ]}
                ]
            },
            {
                label: 'Actions',
                icon: 'ui-icon-settings',
                items: [
                    {
                        label: 'Edit',
                        icon: 'ui-icon-refresh',
                        items: [
                            {label: 'Save', icon: 'ui-icon-save'},
                            {label: 'Update', icon: 'ui-icon-update'},
                        ]
                    },
                    {
                        label: 'Other',
                        icon: 'fa-phone',
                        items: [
                            {label: 'Delete', icon: 'ui-icon-delete'}
                        ]
                    }
                ]
            },
            {
                label: 'Quit', icon: 'ui-icon-power-settings-new'
            }
        ];

        this.items = [{
            label: 'File',
            items: [
                {label: 'New', icon: 'ui-icon-plus'},
                {label: 'Open', icon: 'ui-icon-open-in-browser'}
            ]
        },
        {
            label: 'Edit',
            items: [
                {label: 'Undo', icon: 'ui-icon-undo'},
                {label: 'Redo', icon: 'ui-icon-redo'}
            ]
        }];

        this.megaMenuItems = [
        ];

        this.panelMenuItems = [
            {
                label: 'File',
                icon: 'ui-icon-insert-drive-file',
                items: [{
                        label: 'New',
                        icon: 'ui-icon-add',
                        items: [
                            {label: 'Project'},
                            {label: 'Other'},
                        ]
                    },
                    {label: 'Open'},
                    {label: 'Quit'}
                ]
            },
            {
                label: 'Edit',
                icon: 'ui-icon-edit',
                items: [
                    {label: 'Undo', icon: 'ui-icon-undo'},
                    {label: 'Redo', icon: 'ui-icon-redo'}
                ]
            },
            {
                label: 'Help',
                icon: 'ui-icon-help-outline',
                items: [
                    {
                        label: 'Contents'
                    },
                    {
                        label: 'Search',
                        icon: 'ui-icon-search',
                        items: [
                            {
                                label: 'Text',
                                items: [
                                    {
                                        label: 'Workspace'
                                    }
                                ]
                            },
                            {
                                label: 'File'
                            }
                    ]}
                ]
            },
            {
                label: 'Actions',
                icon: 'ui-icon-settings',
                items: [
                    {
                        label: 'Edit',
                        icon: 'ui-icon-edit',
                        items: [
                            {label: 'Save', icon: 'ui-icon-save'},
                            {label: 'Update', icon: 'ui-icon-update'},
                        ]
                    },
                    {
                        label: 'Other',
                        icon: 'ui-icon-list',
                        items: [
                            {label: 'Delete', icon: 'ui-icon-delete'}
                        ]
                    }
                ]
            }
        ];

        this.stepsItems = [
            {
                label: 'Personal'
            },
            {
                label: 'Seat'
            },
            {
                label: 'Payment'
            },
            {
                label: 'Confirmation'
            }
        ];
    }

}
