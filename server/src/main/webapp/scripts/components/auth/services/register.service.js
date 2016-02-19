'use strict';

angular.module('synfilessbApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


