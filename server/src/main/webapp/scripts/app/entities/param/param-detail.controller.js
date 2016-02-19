'use strict';

angular.module('synfilessbApp')
    .controller('ParamDetailController', function ($scope, $stateParams, Param, $rootScope, $mdBottomSheet, $state, $http) {
        $rootScope.topTitle = "Synchronisation";
        $scope.param = {
            includeExcludePatterns : []
        };
        $scope.includeExcludePatterns = [];

        $scope.load = function (id) {
            Param.get({id: id}, function(result) {
              $scope.param = result;
                $rootScope.topTitle = $scope.param.name;
                $scope.includeExcludePatterns = [];
                angular.forEach($scope.param.includeExcludePatterns, function(value) {
                    $scope.includeExcludePatterns.push({value : value});
                });
            });
        };
        $scope.load($stateParams.id);

        $scope.addDir = function() {
            $scope.includeExcludePatterns.push({value : ""});
        }

        $scope.removeDir = function(index) {
            $scope.includeExcludePatterns.splice(index,1);
        }

        $scope.save = function() {
            var includeExcludePatterns = [];
            angular.forEach($scope.includeExcludePatterns, function(value) {
                includeExcludePatterns.push(value.value);
            });
            var paramToSave = angular.copy($scope.param);
            paramToSave.includeExcludePatterns = includeExcludePatterns;
            Param.update(paramToSave);
        }

        $scope.showBottomSheet = function($event) {
            $mdBottomSheet.show({
                templateUrl: 'param-detail-bottom-menu.html',
                controller: 'ParamDetailController',
                targetEvent: $event
            }).then(function(action) {
                if (action=="save") {
                    $scope.save();
                } else if (action=='cancel') {
                    $scope.load($scope.param.key);
                } else if (action=='simulation') {
                    // /rest/syncfiles/startSynchronizeHubic/{paramName}/{hubicCheck}/{isSimulation}
                    $http.get("/rest/syncfiles/startSynchronizeHubic/"+$scope.param.key+"/true/true").success(function(data) {
                        $state.go('paramSynchro', {id : $scope.param.key, action : 'simul'})
                    });
                } else if (action=='preview') {
                    $state.go('paramArbo', {id : $scope.param.key})
                } else if (action=='sync') {
                    // /rest/syncfiles/doSynchro/{paramName}/{isSimulation}
                    $http.get("/rest/syncfiles/doSynchro/"+$scope.param.key+"/false").success(function(data) {
                        $state.go('paramSynchro', {id : $scope.param.key, action : 'sync'})
                    });
                }
            });
        };

        $scope.clickAction = function(action) {
            $mdBottomSheet.hide(action);
        };

    });
