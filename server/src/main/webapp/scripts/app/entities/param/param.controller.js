'use strict';

angular.module('synfilessbApp')
    .controller('ParamController', function ($scope, Param, $rootScope, $cookies, $http, $mdBottomSheet, $state) {
        $rootScope.topTitle = "Synchronisations";

        $scope.selectedParam = {
            key : null
        }

        $scope.params = [];
        $scope.loadAll = function() {
            Param.query(function(result) {
               $scope.params = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Param.update($scope.param,
                function () {
                    $scope.loadAll();
                    //$('#saveParamModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Param.get({id: id}, function(result) {
                $scope.param = result;
                //$('#saveParamModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Param.get({id: id}, function(result) {
                $scope.param = result;
                //$('#deleteParamConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Param.delete({id: id},
                function () {
                    $scope.loadAll();
                    //$('#deleteParamConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.param = {name: null, cronExp: null, masterDir: null, slaveDir: null, includeDir: null, id: null};
        };


        $scope.showBottomSheet = function($event) {
            $mdBottomSheet.show({
                templateUrl: 'param-bottom-menu.html',
                controller: 'ParamController',
                targetEvent: $event
            }).then(function(action) {
                if (action=="new") {
                    $http.get("/rest/syncfiles/newParam/").success(function(data) {
                        $scope.loadAll();
                    });
                } else if (action=='copy') {
                    if ($scope.selectedParam.key!=null) {
                        $http.get("/rest/syncfiles/copyParam/"+$scope.selectedParam.key).success(function(data) {
                            $scope.loadAll();
                        });
                    }
                } else if (action=='delete') {
                    if ($scope.selectedParam.key!=null) {
                        $http.get("/rest/syncfiles/deleteParam/"+$scope.selectedParam.key).success(function(data) {
                            $scope.loadAll();
                        });
                    }
                }
            });
        };

        $scope.clickAction = function(action) {
            $mdBottomSheet.hide(action);
        };
    });
