'use strict';

angular.module('synfilessbApp')
    .controller('ParamSimulController', function ($scope, $stateParams, Param, $rootScope, $mdBottomSheet, $http, $filter) {
        $rootScope.topTitle = "Chargement, veuillez patienter...";
        $scope.param = {
            includeExcludePatterns : []
        };

        $scope.scroll = {
            busy : true
        };

        //$scope.mergedPageDir = [];
        $scope.posMergeDir = 0;

        $scope.data = {
            nbOfElements : null
        };

        $rootScope.filter = {
            dir : 'asset',
            file: null,
            fileToSync : null
        };

        var dirFileListList;

        $scope.filtre = function(dirFilter, fileFilter, toSyncOnly) {
            $scope.mergeDir = [];

            if (dirFileListList==null || dirFileListList.length == 0) {
                return;
            }

            var nbFileFiltered = 0;

            for(var i = 0; i<dirFileListList.length; i++) {
                var curDirFileList = dirFileListList[i];
                if (dirFilter==null || curDirFileList.dir.toLowerCase().indexOf(dirFilter.toLowerCase())>-1) {
                    var toAdd = false;
                    var elt = {
                        dir : curDirFileList.dir,
                        mergedFileInfoJsonList : []
                    };
                    var mergedFileInfoJsonList = curDirFileList.mergedFileInfoJsonList;
                    if (fileFilter==null && !toSyncOnly) {
                        toAdd = true;
                        elt.mergedFileInfoJsonList = mergedFileInfoJsonList;
                        nbFileFiltered = nbFileFiltered + mergedFileInfoJsonList.length;
                    } else {
                        for(var j = 0; j < mergedFileInfoJsonList.length; j++) {
                            var mergedFileInfoJson = mergedFileInfoJsonList[j];
                            if (!toSyncOnly || (toSyncOnly && mergedFileInfoJson.synchronize)) {
                                if (fileFilter==null || (fileFilter!=null && mergedFileInfoJson.fileName.toLowerCase().indexOf(fileFilter.toLowerCase())>-1)) {
                                    toAdd = true;
                                    elt.mergedFileInfoJsonList.push(mergedFileInfoJson);
                                    nbFileFiltered = nbFileFiltered + 1;
                                }
                            }
                        }
                    }
                    if (toAdd) {
                        $scope.mergeDir.push(elt);
                    }
                }
            }
            $rootScope.topTitle = "Simulation. Nombre de fichiers filtrés: " + nbFileFiltered;

        }

        $http.get("/rest/syncfiles/loadMergedFileViewFromParam/"+$stateParams.id+"?path="+$stateParams.path).success(function(data) {
            dirFileListList = data.dirFileListList;
            $scope.filtre(null, null, false);
            $scope.data.nbOfElements = data.nbOfElements;
            $rootScope.topTitle = "Simulation. Nombre de fichiers : " + $scope.data.nbOfElements;
            if (data.nbOfElements>10000) {
                $rootScope.topTitle = $rootScope.topTitle + "arbo. incomplète...";
            }

            //angular.forEach(data.dirFileMap, function(value, key) {
            //    this.push({
            //        dir : key,
            //        fileList : value
            //    });
            //}, mergedDir);
            //var orderBy = $filter('orderBy');
            //$scope.mergeDir = orderBy(mergedDir,'dir',false);

            //if ($scope.mergeDir.length>0) {
            //    for(var i =0 ; i< 50 && $scope.mergeDir.length > i; i++ ) {
            //        $scope.mergedPageDir.push($scope.mergeDir[$scope.posMergeDir]);
            //        $scope.posMergeDir = $scope.posMergeDir + 1;
            //    }
            //}
            //$scope.scroll.busy = false;
        });

        $scope.getOrigin = function(mergeFileInfo) {
            if (mergeFileInfo.sourceFileInfo!=null && mergeFileInfo.targetFileInfo!=null) {
                return "source et destination";
            } else if (mergeFileInfo.sourceFileInfo==null) {
                return "destination";
            } else {
                return "source";
            }
        }

        //$scope.nextPage = function() {
        //    if (!$scope.scroll.busy) {
        //        $scope.scroll.busy = true;
        //
        //        var start = $scope.posMergeDir;
        //        var end = $scope.posMergeDir + 50;
        //        if (end>$scope.mergeDir.length) {
        //            end = $scope.mergeDir.length;
        //        }
        //        for(var i = start; i<end;i++) {
        //            $scope.mergedPageDir.push($scope.mergeDir[i]);
        //        }
        //        $scope.posMergeDir = end;
        //
        //        $scope.scroll.busy = false;
        //
        //    }
        //    return $scope.mergedPageDir;
        //}


        $scope.showBottomSheet = function($event) {
            $mdBottomSheet.show({
                templateUrl: 'param-detail-bottom-menu.html',
                controller: 'ParamDetailController',
                targetEvent: $event
            }).then(function(action) {
                if (action=="cancel") {
                } else {
                    $rootScope.topTitle = "Chargement, veuillez patienter...";
                    $scope.filtre(action.dir, action.file, action.fileToSync);
                }
            });
        };

        $scope.clickAction = function(action) {
            $mdBottomSheet.hide(action);
        };


    });
