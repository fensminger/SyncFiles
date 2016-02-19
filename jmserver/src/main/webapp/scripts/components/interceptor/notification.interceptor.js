 'use strict';

angular.module('jmserverApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-jmserverApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-jmserverApp-params')});
                }
                return response;
            }
        };
    });
