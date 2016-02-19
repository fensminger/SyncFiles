'use strict';

angular.module('synfilessbApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
