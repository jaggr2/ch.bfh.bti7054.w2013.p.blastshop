﻿define(['plugins/http', 'durandal/app', 'knockout', 'plugins/router'], function (http, app, ko, router) {
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
        createAddress = function() {

            currentAddress(new Address({}));
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

                    currentAddress(new Address(data));

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
    return {

        that: this,
        currentAddress: currentAddress,
        loadAddress: loadAddress,
        createAddress: createAddress,
        deleteAddress: deleteAddress,
        submitAddress: submitAddress,
        displayName: ko.computed(function() {
            if(currentAddress.id !== undefined && currentAddress.id() > 0) {
                return "Bearbeite Adresse " + currentAddress.id() + ": ";
            }
            else {
                return "Erstelle Adresse: ";
            }
        }),

    activate: function (addressId) {

        //the router's activator calls this function and waits for it to complete before proceding
        if(addressId !== undefined && addressId == 'create') {
            createAddress();
        }
        else if (addressId !== undefined && addressId > 0) {
            loadAddress(addressId);
        }
        else {
            alert("Invalid parameter!");
        }

    }
};
});