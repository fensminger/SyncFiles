'use strict';

angular.module('synfilessbApp')
    .controller('ParamSynchroController', function ($scope, $stateParams, Param, $rootScope, $mdBottomSheet, $http, $cookies, $filter) {
        $rootScope.topTitle = "Etat de la synchronisation ";

        if ($rootScope.synchroEvents.length>10000) {
            $rootScope.synchroEvents = [];
        }

    });
