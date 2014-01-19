define(['plugins/router', 'durandal/app', 'lib/datamodel'], function (router, app, dataModel) {
    return {
        router: router,
        search: function() {
            //It's really easy to show a message box.
            //You can add custom options too. Also, it returns a promise for the user's response.
            app.showMessage('Search not yet implemented...');
        },
        activate: function () {
            router.map([
                { route: '',        title:'Dashboard',    moduleId: 'viewmodels/dashboard', nav: true },
                { route: 'address',                     moduleId: 'viewmodels/address', nav: true },
                { route: 'address/:id',                 moduleId: 'viewmodels/addressdetail',      nav: false },
                { route: 'address/create',              moduleId: 'viewmodels/addressdetail',      nav: false },
                { route: 'article',                     moduleId: 'viewmodels/article', nav: true },
                { route: 'article/:id',                 moduleId: 'viewmodels/articledetail',      nav: false },
                { route: 'article/create',              moduleId: 'viewmodels/articledetail',      nav: false }
            ]).buildNavigationModel();

            dataModel.loadGlobalDatastore();

            return router.activate();
        }
    };
});