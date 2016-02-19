'use strict';

angular.module('synfilessbApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth, $mdToast, $animate) {
        $scope.user = {};
        $scope.errors = {};

        $rootScope.topTitle = "Authentification";
        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function () {
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $scope.authenticationError = false;
                if ($rootScope.previousStateName === 'register') {
                    $state.go('home');
                } else {
                    $state.go('param');
//                    $rootScope.back();
                }
            }).catch(function () {
                $scope.authenticationError = true;
                $scope.showAthErrorToast();
                // $mdToast.show($mdToast.simple().content('Hello!'));
                //alert('ici');
            });
        };

        $scope.showAthErrorToast = function() {
            $mdToast.show({
                controller: 'ToastAuthErrorCtrl',
                templateUrl: 'auth-error.html',
                hideDelay: 6000,
                position: 'top right'
            });
        };
    })
    .controller('ToastAuthErrorCtrl', function($scope, $mdToast) {
        $scope.closeToast = function() {
            $mdToast.hide();
        };
    });
