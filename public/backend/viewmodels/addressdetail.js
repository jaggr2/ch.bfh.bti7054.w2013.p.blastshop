define(['plugins/http', 'durandal/app', 'knockout', 'plugins/router', 'lib/datamodel'], function (http, app, ko, router, dataModel) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.

    var self = this;
    currentAddress = ko.observable(),

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

                    currentAddress(new dataModel.Address(data));
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error");
                }
            });
        },
        createAddress = function() {

            currentAddress(new dataModel.Address({}));
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

                    currentAddress(undefined);

                    router.navigate('address', true);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + msg.responseText);
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

                    currentAddress(new dataModel.Address(data));

                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);


                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + textStatus);
                }
            });
        };



    /* var isCtrl = false;


     $(document).keyup(function (e) {
     if(e.which == 17) isCtrl=false;
     }).keydown(function (e) {
     if(e.which == 17) isCtrl=true;
     if(e.which == 65 && isCtrl == true) {
     //run code for CTRL+A -- ie, save!
     createAddress();
     $('#addressName').focus();
     return false;
     }
     if(e.which == 83 && isCtrl == true) {
     //run code for CTRL+S -- ie, save!
     submitAddress();
     return false;
     }
     });
     */
    return function() {

        var self = this;
        self.currentAddress = currentAddress;
        self.loadAddress = loadAddress;
        self.createAddress = createAddress;
        self.deleteAddress = deleteAddress;
        self.submitAddress = submitAddress;
        self.displayName = ko.computed(function() {
            if(self.currentAddress.id !== undefined && self.currentAddress.id() > 0) {
                return "Bearbeite Adresse " + self.currentAddress.id() + ": ";
            }
            else {
                return "Erstelle Adresse: ";
            }
        });

        self.closeAddress = function() {
            router.navigateBack();
        }

        self.askDeleteAddress = function() {
            return app.showMessage('Soll die Adresse wirklich gelöscht werden?', 'Wirklich löschen?', ['Yes', 'No']).then(function(dialogResult){
                if(dialogResult == 'Yes') {
                    self.deleteAddress();
                }
            });
        }

        self.activate = function(addressId) {
            //the router's activator calls this function and waits for it to complete before proceding
            if(addressId !== undefined && addressId == 'create') {
                self.createAddress();
            }
            else if (addressId !== undefined && addressId > 0) {
                self.loadAddress(addressId);
            }
            else {
                alert("Invalid parameter!");
            }

        }

        /*
        $('#city input').datepicker({
            format: "dd.mm.yyyy",
            weekStart: 1,
            language: "de",
            calendarWeeks: true,
            autoclose: true,
            todayHighlight: true
        });
        */
    }
});