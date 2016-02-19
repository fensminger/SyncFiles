'use strict';

angular.module('synfilessbApp')
    .directive('resize', function ($window) {
    return function (scope, element) {
        var w = angular.element($window);
        var marge = element.attr("resize");
        if (marge=="") {
            marge = 20;
        }
        var changeHeight = function() {
            element.css('height', (w.height() - element.offset().top - marge) + 'px' );
        };
        w.bind('resize', function () {
            changeHeight();   // when window size gets changed
        });
        changeHeight(); // when page loads
    }
});