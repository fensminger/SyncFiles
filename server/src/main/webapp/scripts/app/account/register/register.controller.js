'use strict';

angular.module('synfilessbApp')
    .controller('RegisterController', function ($scope, $translate, $timeout, Auth, $mdToast, $rootScope) {
        $rootScope.topTitle = "S'enregistrer";

        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.registerAccount = {};
        $timeout(function (){angular.element('[ng-model="registerAccount.login"]').focus();});

        $scope.showAthErrorToast = function(templateName) {
            $mdToast.show({
                controller: 'ToastRegErrorCtrl',
                templateUrl: templateName,
                hideDelay: 6000,
                position: 'top right'
            });
        };


        $scope.register = function () {
            if ($scope.registerAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
                $scope.showAthErrorToast('reg-pwdNotMatch.html');
            } else {
                $scope.registerAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.error = null;
                $scope.errorUserExists = null;
                $scope.errorEmailExists = null;

                Auth.createAccount($scope.registerAccount).then(function () {
                    $scope.success = 'OK';
                    $scope.showAthErrorToast('reg-success.html');
                }).catch(function (response) {
                    $scope.success = null;
                    if (response.status === 400 && response.data === 'login already in use') {
                        $scope.errorUserExists = 'ERROR';
                        $scope.showAthErrorToast('reg-userexist.html');
                    } else if (response.status === 400 && response.data === 'e-mail address already in use') {
                        $scope.errorEmailExists = 'ERROR';
                        $scope.showAthErrorToast('reg-loginexist.html');
                    } else {
                        $scope.error = 'ERROR';
                        $scope.showAthErrorToast('reg-error.html');
                    }
                });
            }
        };
    })
    .controller('ToastRegErrorCtrl', function($scope, $mdToast) {
        $scope.closeToast = function() {
            $mdToast.hide();
        };
    });
