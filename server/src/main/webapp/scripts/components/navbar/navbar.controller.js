'use strict';

angular.module('synfilessbApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };

        $scope.viewSynchro = function() {
            $state.go('paramSynchro', {id : 'empty', action : 'view'})
        }
    });
