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
        app.setRoot('viewmodels/shell'); //, 'entrance'
    });
});