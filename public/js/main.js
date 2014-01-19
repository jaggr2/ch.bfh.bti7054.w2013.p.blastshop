requirejs.config({
    noGlobal: true,
    paths: {
        'text': '/assets/lib/require.2.1.8/text',
        'durandal':'/assets/lib/durandal.2.0.1/js',
        'plugins' : '/assets/lib/durandal.2.0.1/js/plugins',
        'transitions' : '/assets/lib/durandal.2.0.1/js/transitions',
        'knockout': '/assets/lib/knockout.2.3.0/knockout-2.3.0',
        'bootstrap': '/assets/lib/bootstrap.3.0.2/js/bootstrap',
        'bootstrapdatepicker': '/assets/lib/bootstrap-datepicker.1.2.0/js/bootstrap-datepicker',
        'typeahead': '/assets/lib/typeahead.0.9.3/typeahead',
        'jquery': '/assets/lib/jquery.1.9.1/jquery-1.9.1',
        'moment': '/assets/lib/moment.2.5.0/moment-with-langs',
        'lib': '/assets/backend/lib',
        'dropzone': '/assets/lib/dropzone.3.8.2/dropzone-amd-module',
        'tag-it': '/assets/lib/tag-it.2.0.0/js/tag-it'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: 'jQuery'
       },
        'bootstrapdatepicker': {
            deps: ['bootstrap'],
            exports: 'bootstrapdatepicker'
        },
        'typeahead': {
            deps: ['jquery', 'bootstrap'],
            exports: 'typeahead'
        }
    }
});

define(['knockout', 'bootstrap','lib/datamodel'],  function (ko, bootstrap) {

    shoppingCart = ko.observable(null);

    var loadDocument = function() {
        $.ajax({
            type:"GET",
            url:"/shoppingcart/data",
            dataType:"json",
            contentType:"application/json",
            success: function(data, textStatus, jqXHR) {
                console.log("ajax success data: " + data);
                console.log("ajax success textStatus: " + textStatus);
                console.log("ajax success jqXHR: " + jqXHR);

                shoppingCart(new Document(data));

                shoppingCart().rows.subscribe(function() {
                    updateShoppingCart();
                });




            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("ajax error xhr: " + jqXHR);
                console.log("ajax error textStatus: " + textStatus);
                console.log("ajax error errorThrown: " + errorThrown);

                alert("Error on loading shopping cart");
            }
        });
    };

    addArticleToShoppingCart = function(articleId) {
        shoppingCart().addArticleOptionRow(articleId);

        $('#shoppingCartModal').modal('show');
    };

    confirmShoppingCart = function() {
        shoppingCart().documentType({ id: 2}); // id 2 = clientorder
            $.ajax({
                type:"POST",
                url:"/shoppingcart/data",
                data: ko.toJSON(shoppingCart).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("shopping cart successfully ordered!");
                    window.location.href = "/orderconfirmation";
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error on saving the shopping cart: " + textStatus);
                }
            });

    };

    setLanguage = function(code) {
        $.ajax({
            type:"GET",
            url:"/set/language/" + code,
            success: function() {
                console.log("language successfully changed!");
                window.location.reload();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("ajax error xhr: " + jqXHR);
                console.log("ajax error textStatus: " + textStatus);
                console.log("ajax error errorThrown: " + errorThrown);

                alert("Error on changing language: " + textStatus);
            }
        });

    };

    updateShoppingCart = function() {
        $.ajax({
            type:"POST",
            url:"/shoppingcart/data",
            data: ko.toJSON(shoppingCart).toString(),
            dataType:"json",
            contentType:"application/json",
            success: function(data, textStatus, jqXHR) {

                //shoppingCart(new Document(data));

                console.log("shopping cart successfully saved!");


            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("ajax error xhr: " + jqXHR);
                console.log("ajax error textStatus: " + textStatus);
                console.log("ajax error errorThrown: " + errorThrown);

                alert("error on saving the shopping cart: " + textStatus);
            }
        });
    }

    loadDocument();



    ko.applyBindings(this);
});