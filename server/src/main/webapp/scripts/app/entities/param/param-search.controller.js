'use strict';

angular.module('synfilessbApp')
    .controller('ParamSearchController', function ($scope, $stateParams, Param, $rootScope, $mdBottomSheet, $http, $filter, $state) {
        $rootScope.topTitle = "Résultet de la recherche";

        $scope.mergeDir = [];
        $scope.pathNode = $stateParams.pathNode;
        $scope.pathFilter = $stateParams.pathFilter;
        $scope.fileToSync = $stateParams.fileToSync;

        $scope.load = function() {
            $http.get("/rest/syncfiles/loadSearchMergedFileFromParam/"+$stateParams.id+"?pathNode="+$scope.pathNode+"&nameFilter="+$scope.pathFilter+"&fileToSync="+$scope.fileToSync).success(function(data) {
                $scope.mergeDir = data.mergedFileInfoList;
            });
        }
        $scope.load();


        $scope.getOrigin = function(mergeFileInfo) {
            if (mergeFileInfo.sourceFileInfo!=null && mergeFileInfo.targetFileInfo!=null) {
                return "source et destination";
            } else if (mergeFileInfo.sourceFileInfo==null) {
                return "destination";
            } else {
                return "source";
            }
        }

        $scope.goToDir = function(mergeFileInfo) {
            var dir = '/' + mergeFileInfo.fullName;
            if (!$scope.isDirectory(mergeFileInfo)) {
                var posFile = dir.lastIndexOf('/');
                dir = dir.substring(0,posFile);
            }
            $state.go('paramArbo', {id : $stateParams.id, path : dir})
        }

        $scope.isDirectory = function(mergeFileInfo) {
            if (mergeFileInfo.sourceFileInfo!=null) {
                return mergeFileInfo.sourceFileInfo.directory
            } else {
                return mergeFileInfo.targetFileInfo.directory
            }
        }

    });
