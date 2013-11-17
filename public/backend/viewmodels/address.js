define(['plugins/http', 'durandal/app', 'knockout', 'durandal/system'], function (http, app, ko, system) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.
    var Address = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.name  = ko.observable(data.name);
        self.preName   = ko.observable(data.preName);

        self.editText = ko.computed(function() {
            if(this.id !== undefined && this.id() > 0) {
                return "Bearbeite Adresse " + this.id() + ": ";
            }
            else {
                return "Erstelle Adresse: ";
            }
        }, self);

        return self;
    };

    var self = this;
        addresses = ko.observableArray(),
        selectedAddress = ko.observable(),
        getAddresses = function() {
            return $.getJSON('/api/address/all', { }, function(response) {
                var addresses = $.map(response, function(item) {return new Address(item) });
                system.log(addresses);
                self.addresses(addresses); //(addresses);
            });
        },
        selectAddress = function(adr){
            selectedAddress(adr);
        },
        createAddress = function() {
            var newAddress = new Address({});
            addresses.push(newAddress);
            selectAddress(newAddress);
        },
        deleteAddress = function(adr) {
            $.ajax({
                type:"DELETE",
                url:"/api/address",
                data: ko.toJSON(adr).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);

                    addresses.remove(adr);

                    selectAddress(undefined);
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
                data: ko.toJSON(selectedAddress).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);

                    selectAddress(undefined);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + msg.responseText);
                }
            });
        };

    var isCtrl = false;

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

    return {

        that: this,
        addresses: addresses,
        selectedAddress: selectedAddress,
        getAddresses: getAddresses,
        selectAddress: selectAddress,
        deleteAddress: deleteAddress,
        submitAddress: submitAddress,
        displayName: 'Address',

        activate: function () {
            //the router's activator calls this function and waits for it to complete before proceding
            //if (this.addresses().length > 0) {
            //    return;
            //}



            return getAddresses();
        },
        select: function(item) {
            //the app model allows easy display of modal dialogs by passing a view model
            //views are usually located by convention, but you an specify it as well with viewUrl
            //item.viewUrl = 'views/detail';
            //app.showDialog(item);
        }
    };
});