'use strict';

angular.module('jmserverApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


