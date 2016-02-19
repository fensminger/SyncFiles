'use strict';

angular.module('synfilessbApp')
    .controller('MainController', function ($scope, Principal, $rootScope) {
        $rootScope.topTitle = "Home";
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
