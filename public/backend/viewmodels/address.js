define(['plugins/http', 'durandal/app', 'knockout', 'durandal/system'], function (http, app, ko, system) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.
    function Address(data) {
        this.id = ko.observable(data.id);
        this.name  = ko.observable(data.name);
        this.preName   = ko.observable(data.preName);


    };

    var that = this;

    submitAddress = function() {
        var newAddress = new Address({});
        newAddress.name = formName();
        newAddress.preName = formPrename();

/*        $.post("/api/address", "'" + test + "'", function(response) {
            alert(response);
        }, "json").fail(function(msg) {
                alert("error: " + msg)
            }); */

        $.ajax({type:"POST",url:"/api/address",

            data: ko.toJSON(newAddress).toString(),

            dataType:"json",

            contentType:"application/json"}).done(function(msg) {
                alert( "success: " + msg );

                formName("");
                formPrename("");

                /* $.getJSON('/api/address/all', { }, function(response) {
                    var addresses = $.map(response, function(item) {return new Address(item) });
                    system.log(addresses);
                    that.addresses(addresses); //(addresses);
                }) */
            }).fail(function(msg) {
                alert("error: " + msg.responseText)
            });


    }

    formName = ko.observable();
    formPrename = ko.observable();

    return {
        displayName: 'Address',
        addresses: ko.observableArray([]),
        activate: function () {
            //the router's activator calls this function and waits for it to complete before proceding
            //if (this.addresses().length > 0) {
            //    return;
            //}

            var that = this;

            return $.getJSON('/api/address/all', { }, function(response) {
                var addresses = $.map(response, function(item) {return new Address(item) });
                system.log(addresses);
                that.addresses(addresses); //(addresses);
            });
        },
        select: function(item) {
            //the app model allows easy display of modal dialogs by passing a view model
            //views are usually located by convention, but you an specify it as well with viewUrl
            //item.viewUrl = 'views/detail';
            //app.showDialog(item);
        }
    };
});