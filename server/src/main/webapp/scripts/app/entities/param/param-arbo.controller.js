'use strict';

angular.module('synfilessbApp')
    .controller('ParamArboController', function ($scope, $stateParams, Param, $rootScope, $mdBottomSheet, $http, $filter, $state) {
        $rootScope.topTitle = "Arborescence";
        $scope.param = {
            includeExcludePatterns : []
        };

        $scope.mergeDir = [];
        $scope.posMergeDir = 0;
        $scope.path='';
        $scope.pathList = [];

        $rootScope.filter = {
            name: null,
            fileToSync : false
        };

        $scope.changePath = function(newPath) {
            $scope.path = newPath;
            $scope.pathList = [];
            if (newPath=="") {
                return;
            }
            var relaPathList = newPath.split("/");
            var curFullPath = "";
            for(var i = 0; i< relaPathList.length; i++) {
                var relaDir = relaPathList[i];
                if (i==0) {
                    relaDir = "ROOT";
                } else {
                    curFullPath = curFullPath + "/" + relaDir;
                }
                $scope.pathList.push({
                    name : relaDir,
                    fullPath : curFullPath
                });
            }
        }

        $scope.load = function() {
            $http.get("/rest/syncfiles/loadFlatMergedFileFromParam/"+$stateParams.id+"?path="+$scope.path).success(function(data) {
                var mergedDir = data;
                var orderBy = $filter('orderBy');
                $scope.mergeDir = orderBy(mergedDir,$scope.sort,false);
            });
        }
        $scope.changePath($stateParams.path);
        $scope.load();

        $scope.sort = function(mergeFileInfo) {
            var isDir = $scope.isDirectory(mergeFileInfo);
            if (isDir) {
                return "d/" + mergeFileInfo.fileName;
            } else {
                return "f/" + mergeFileInfo.fileName;
            }
        }


        $scope.getOrigin = function(mergeFileInfo) {
            if (mergeFileInfo.sourceFileInfo!=null && mergeFileInfo.targetFileInfo!=null) {
                return "source et destination";
            } else if (mergeFileInfo.sourceFileInfo==null) {
                return "destination";
            } else {
                return "source";
            }
        }

        $scope.changeDir = function(dir) {
            $state.go('paramArbo', {id : $stateParams.id, path : $scope.path + '/' + dir})
            //$scope.changePath($scope.path + '/' + dir);
            //$scope.load();
        }

        $scope.showArbo = function(dir) {
            $state.go('paramSimul', {id : $stateParams.id, path : $scope.path + '/' + dir})
        }

        $scope.changeFullDir = function(dir) {
            $scope.changePath(dir);
            $scope.load();
        }

        $scope.isDirectory = function(mergeFileInfo) {
            if (mergeFileInfo.sourceFileInfo!=null) {
                return mergeFileInfo.sourceFileInfo.directory
            } else {
                return mergeFileInfo.targetFileInfo.directory
            }
        }

        $scope.showBottomSheet = function($event) {
            $mdBottomSheet.show({
                templateUrl: 'param-detail-bottom-menu.html',
                controller: 'ParamDetailController',
                targetEvent: $event
            }).then(function(action) {
                if (action=="ok") {
                    $state.go('paramSearch', {
                        id : $stateParams.id
                        , pathNode : $scope.path
                        , pathFilter : $rootScope.filter.name
                        , fileToSync : $rootScope.filter.fileToSync
                    })
                } else if (action=='cancel') {
                }
            });
        };

        $scope.clickAction = function(action) {
            $mdBottomSheet.hide(action);
        };


    });
