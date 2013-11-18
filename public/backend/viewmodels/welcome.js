define(function() {
    var ctor = function () {
        this.displayName = 'Welcome to the Blastshop Management Interface!';
        this.description = 'The Blastshop Management is a cross-device, cross-platform shop management client written in JavaScript and designed to make the administration of the shop easy to maintain.';
    };

    //Note: This module exports a function. That means that you, the developer, can create multiple instances.
    //This pattern is also recognized by Durandal so that it can create instances on demand.
    //If you wish to create a singleton, you should export an object instead of a function.
    //See the "flickr" module for an example of object export.

    return ctor;
});