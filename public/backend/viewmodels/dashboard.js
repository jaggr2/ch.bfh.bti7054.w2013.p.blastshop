define(['plugins/http', 'durandal/app', 'knockout', 'durandal/system', 'plugins/router', 'lib/datamodel', 'moment'], function (http, app, ko, system, router, dataModel, moment) {

        var self = this;
        newestAddresses = ko.observableArray();
        newestClientOrders = ko.observableArray();

        getNewestAddresses = function() {
            return $.getJSON('/api/address/newest', { }, function(response) {
                var addresses = $.map(response, function(item) {return new Address(item) });
                self.newestAddresses(addresses);
            });
        };

    getNewestClientOrders = function() {
        return $.getJSON('/api/document/newestClientOrders', { }, function(response) {
            var orders = $.map(response, function(item) {return new Document(item) });
            self.newestClientOrders(orders);
        });
    };

    var isAlt = false;

    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.
    return {

        that: this,
        moment: moment,
        newestAddresses: newestAddresses,
        getNewestAddresses: getNewestAddresses,
        newestClientOrders: newestClientOrders,
        getNewestClientOrders: getNewestClientOrders,
        showAddresses: function() {
            router.navigate('address');
        },
        editAddress: function(adr) {
            router.navigate('address/' + adr.id());
        },
        displayName: 'Dashboard',

        activate: function () {
            //the router's activator calls this function and waits for it to complete before proceding
            /* if (this.addresses().length > 0) {
                return;
            }  */

            getNewestAddresses();
            getNewestClientOrders();

            $(document).keyup(function (e) {
                if(e.which == 18) isAlt=false;
            }).keydown(function (e) {
                    //alert(e.which); ESC=27
                    if(e.which == 18) isAlt=true;
                    /* if(e.which == 78 && isAlt == true) {
                        //run code for CTRL+A -- ie, save!
                        createAddress();
                        //$('#addressName').focus();
                        return false;
                    }  */
                });

        },
        deactivate: function() {
            $(document).off('keydown');
            $(document).off('keyup');
        }
    };
});