'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('thinkdvr', ['uiGmapgoogle-maps','ngMaterial']);

app.config(function(uiGmapGoogleMapApiProvider) {
    uiGmapGoogleMapApiProvider.configure({
    key: '',
        v: '3.17',
        libraries: 'weather,geometry,visualization'
    });
});

app.controller("mainMapController", function($scope, uiGmapGoogleMapApi) {
    // Do stuff with your $scope.
    // Note: Some of the directives require at least something to be defined originally!
    // e.g. $scope.markers = []
    var vm = this;

    //$scope.map = { center: { latitude: 0, longitude: 0 }, zoom: 3 };

    vm.map = {
        center:{
            latitude: 51.284197,
            longitude: -0.741352
        },
        zoom: 12
    };

    vm.places = [
                 {
                   idKey: 583187,
                   latitude: 51.284197,
                   longitude: -0.741352,
                   title: "title"
                 }
               ];

    // uiGmapGoogleMapApi is a promise.
    // The "then" callback function provides the google.maps object.
    uiGmapGoogleMapApi.then(function(maps) {
        //$scope.map = { center: { latitude: 45, longitude: -73 }, zoom: 8 };

    });
});