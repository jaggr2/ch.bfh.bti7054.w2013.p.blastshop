define(['knockout'], function (ko) {
    return {
        Address: function(data) {
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

            self.getURL = ko.computed(function() {
                return "#address/" + self.id();
            }, self);

            return self;
        }
    };
});
