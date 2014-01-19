define(['plugins/http', 'durandal/app', 'knockout', 'plugins/router', 'lib/datamodel'], function (http, app, ko, router, dataModel) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.

    var module = this;
    currentAddress = ko.observable(null),

        loadAddress = function(id) {
            $.ajax({
                type:"GET",
                url:"/api/address/" + id,
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);

                    currentAddress(new Address(data));
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error");
                }
            });
        },
        loadNewAddress = function() {
            currentAddress(null);
            currentAddress(new Address({}));
        },
        createAddress = function() {
            router.navigate('address/create')
        },
        deleteAddress = function() {
            $.ajax({
                type:"DELETE",
                url:"/api/address",
                data: ko.toJSON(currentAddress).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);

                    currentAddress(null);

                    router.navigate('address', true);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + textStatus);
                }
            });
        },
        submitAddress = function() {
            $.ajax({
                type:"POST",
                url:"/api/address",
                data: ko.toJSON(currentAddress).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {

                    if(currentAddress().id() == null) {
                        router.navigate('address/' + data.id, { replace: true, trigger: false })
                    }
                    currentAddress(new Address(data));

                    console.log("address with id " + data.id + " successfully saved!");
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + textStatus);
                }
            });
        },
        copyAddress = function() {
            if(currentAddress() != null) {
                currentAddress().initCopy();
                router.navigate('address/create', { replace: false, trigger: false })
            }
        };



    //Note: This module exports a function. That means that you, the developer, can create multiple instances.
    //This pattern is also recognized by Durandal so that it can create instances on demand.
    //If you wish to create a singleton, you should export an object instead of a function.
    //See the "flickr" module for an example of object export.
    return function() {

        var self = this;
        var isAlt = false;
        self.currentAddress = currentAddress;
        self.loadAddress = loadAddress;
        self.createAddress = createAddress;
        self.deleteAddress = deleteAddress;
        self.submitAddress = submitAddress;
        self.loadNewAddress = loadNewAddress;
        self.copyAddress = copyAddress;

        self.closeAddress = function () {
            router.navigate('address');
        };

        self.askDeleteAddress = function () {
            return app.showMessage('Soll die Adresse wirklich gelöscht werden?', 'Wirklich löschen?', ['Yes', 'No']).then(function (dialogResult) {
                if (dialogResult == 'Yes') {
                    self.deleteAddress();
                }
            });
        };

        self.activate = function(addressId) {
            //the router's activator calls this function and waits for it to complete before proceding
            if(addressId !== undefined && addressId == 'create') {
                self.loadNewAddress();
            }
            else if (addressId !== undefined && addressId > 0) {
                self.loadAddress(addressId);
            }
            else {
                alert("Invalid parameter!");
                router.navigateBack();
            }

            $(document).keyup(function (e) {
                if(e.which == 18) isAlt=false;
            }).keydown(function (e) {
                    //alert(e.which);
                    if(e.which == 18) isAlt=true;
                    if(e.which == 78 && isAlt == true) {
                        //run code for ALT+N
                        self.createAddress();
                        e.preventDefault();
                    }
                    if(e.which == 83 && isAlt == true) {
                        //run code for ALT+S
                        self.submitAddress();
                        e.preventDefault();
                    }
                    if(e.which == 67 && isAlt == true) {
                        //run code for ALT+C
                        self.copyAddress();
                        e.preventDefault();
                    }
                    if(e.which == 46 && isAlt == true) {
                        //run code for ALT+DELETE
                        self.askDeleteAddress();
                        e.preventDefault();
                    }
                    if(e.which == 27) self.closeAddress(); // 27=ESC-Key
                });

        };

        self.deactivate = function() {
            $(document).off('keydown');
            $(document).off('keyup');
        }
    }
});