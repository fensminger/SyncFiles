'use strict';

angular.module('synfilessbApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('param', {
                parent: 'entity',
                url: '/param',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'synfilessbApp.param.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/param/params.html',
                        controller: 'ParamController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('param');
                        return $translate.refresh();
                    }]
                }
            })
            .state('paramDetail', {
                parent: 'entity',
                url: '/param/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'synfilessbApp.param.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/param/param-detail.html',
                        controller: 'ParamDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('param');
                        return $translate.refresh();
                    }]
                }
            })
        .state('paramSimul', {
            parent: 'entity',
            url: '/param/simul/:id/:path',
            data: {
                roles: ['ROLE_USER'],
                pageTitle: 'synfilessbApp.param.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/param/param-simul.html',
                    controller: 'ParamSimulController'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('param');
                    return $translate.refresh();
                }]
            }
        })
            .state('paramSynchro', {
                parent: 'entity',
                url: '/param/synchro/:id/:action',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'synfilessbApp.param.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/param/param-synchro.html',
                        controller: 'ParamSynchroController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('param');
                        return $translate.refresh();
                    }]
                }
            })
            .state('paramSearch', {
                parent: 'entity',
                url: '/param/search/:id/:pathNode/:pathFilter/:fileToSync',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'synfilessbApp.param.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/param/param-search.html',
                        controller: 'ParamSearchController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('param');
                        return $translate.refresh();
                    }]
                }
            })
            .state('paramArbo', {
                parent: 'entity',
                url: '/param/arbo/:id/:path',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'synfilessbApp.param.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/param/param-arbo.html',
                        controller: 'ParamArboController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('param');
                        return $translate.refresh();
                    }]
                }
            });
    });
