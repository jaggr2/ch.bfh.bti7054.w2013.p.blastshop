﻿requirejs.config({
    paths: {
        'text': '/assets/lib/require.2.1.8/text',
        'durandal':'/assets/lib/durandal.2.0.1/js',
        'plugins' : '/assets/lib/durandal.2.0.1/js/plugins',
        'transitions' : '/assets/lib/durandal.2.0.1/js/transitions',
        'knockout': '/assets/lib/knockout.2.3.0/knockout-2.3.0',
        'bootstrap': '/assets/lib/bootstrap.3.0.2/js/bootstrap',
        'bootstrapdatepicker': '/assets/lib/bootstrap-datepicker.1.2.0/js/bootstrap-datepicker',
        'jquery': '/assets/lib/jquery.1.9.1/jquery-1.9.1',
        'lib': '/assets/backend/lib'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: 'jQuery'
       },
        'bootstrapdatepicker': {
            deps: ['bootstrap'],
            exports: 'bootstrapdatepicker'
        }

    }
});

define(['durandal/system', 'durandal/app', 'durandal/viewLocator'],  function (system, app, viewLocator) {
    //>>excludeStart("build", true);
    system.debug(true);
    //>>excludeEnd("build");

    app.title = 'Blastshop Management App';

    app.configurePlugins({
        router:true,
        dialog: true,
        widget: true
    });

    app.start().then(function() {
        //Replace 'viewmodels' in the moduleId with 'views' to locate the view.
        //Look for partial views in a 'views' folder in the root.
        viewLocator.useConvention();

        //Show the app by setting the root view model for our application with a transition.
        app.setRoot('viewmodels/shell', 'entrance');
    });
});