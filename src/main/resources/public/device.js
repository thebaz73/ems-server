function DeviceController($scope, $http) {
    $http.get('/devices').
        success(function(data) {
            $scope.devices = data;
        });
}