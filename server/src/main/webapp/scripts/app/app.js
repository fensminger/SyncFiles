'use strict';

angular.module('synfilessbApp', ['LocalStorageModule', 'tmh.dynamicLocale',
    'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'infinite-scroll', 'ngMaterial', 'ngMessages'])
    .run(function ($rootScope, $location, $window, $http, $state, $translate, Auth, Principal, Language, ENV, VERSION) {
        $rootScope.ENV = ENV;
        $rootScope.VERSION = VERSION;
        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            if (Principal.isIdentityResolved()) {
                Auth.authorize();
            }

            // Update the language
            Language.getCurrent().then(function (language) {
                $translate.use(language);
            });
        });

        $rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
            var titleKey = 'global.title';

            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;

            // Set the page title key to the one configured in state or use default one
            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }
            $translate(titleKey).then(function (title) {
                // Change window title with translated one
                $window.document.title = title;
            });
        });

        $rootScope.back = function() {
            // If previous state is 'activate' or do not exist go to 'home'
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };
    })

    .factory('authInterceptor', function ($rootScope, $q, $location, localStorageService) {
        return {
            // Add authorization token to headers
            request: function (config) {
                config.headers = config.headers || {};
                var token = localStorageService.get('token');

                if (token && token.expires_at && token.expires_at > new Date().getTime()) {
                    config.headers.Authorization = 'Bearer ' + token.access_token;
                }

                return config;
            }
        };
    })

    .factory('authExpiredInterceptor', function ($rootScope, $q, $injector, localStorageService) {
        return {
            responseError: function (response) {
                // token has expired
                if (response.status === 401 && (response.data.error == 'invalid_token' || response.data.error == 'Unauthorized')) {
                    localStorageService.remove('token');
                    var Principal = $injector.get('Principal');
                    if (Principal.isAuthenticated()) {
                        var Auth = $injector.get('Auth');
                        Auth.authorize(true);
                    }
                }
                return $q.reject(response);
            }
        };
    })
    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            views: {
                'navbar@': {
                    templateUrl: 'scripts/components/navbar/navbar.html',
                    controller: 'NavbarController'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('language');
                    return $translate.refresh();
                }]
            }
        });

        $httpProvider.interceptors.push('authInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');

        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json'
        });

        $translateProvider.preferredLanguage('en');
        $translateProvider.useCookieStorage();

        tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
    })
    .controller('AppCtrl', function($scope, $timeout, $mdSidenav, $log, $rootScope, $cookies, $http) {
        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
        };

        $scope.toggleLeft = function() {
            $mdSidenav('left').toggle()
                .then(function(){
                    $log.debug("toggle left is done");
                });
        };
        $scope.openLeft = function() {
            $mdSidenav('left').open()
                .then(function(){
                    $log.debug("open left is done");
                });
        };
        $scope.toggleRight = function() {
            $mdSidenav('right').toggle()
                .then(function(){
                    $log.debug("toggle RIGHT is done");
                });
        };
        $scope.$on('$stateChangeSuccess', function(){
            //Here your view content is fully loaded !!
            $mdSidenav('left').close()
                .then(function(){
                    $log.debug("close LEFT is done");
                });
        });

        $rootScope.synchroEvents = [];
        $http.get("/rest/syncfiles/loadCurrentEvents").success(function(data) {
            $rootScope.synchroEvents = data;
        });

        $rootScope.socketEvent = new SockJS('/synchro/tracker');
        var stompClient = null;
        stompClient = Stomp.over($rootScope.socketEvent);
        var headers = {};
        headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
        stompClient.connect(headers, function(frame) {
            stompClient.subscribe('/topic/synchro', function(activity) {
                showActivity(JSON.parse(activity.body));
            });
        });

        function showActivity(activity) {
            $rootScope.synchroEvents.push(activity);
            $scope.$apply();
        };
    })
    .controller('LeftCtrl', function($scope, $timeout, $mdSidenav, $log) {
        $scope.close = function() {
            $mdSidenav('left').close()
                .then(function(){
                    $log.debug("close LEFT is done");
                });
        };

    })
;
