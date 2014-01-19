/**
 * Created by roger.jaggi on 07.01.14.
 */
define(['knockout','bootstrapdatepicker', 'typeahead'], function (ko, bootstrapdatepicker, typeahead) {

    /*
    ko.bindingHandlers.tagit = {
        // Tag-It Plugin: https://github.com/aehlke/tag-it
        // Example HTML: <ul data-bind="tagit:{tags:Tags, autocomplete:{source:TagAutocompleter, delay:250, minLength: 2}, options:{preprocessTag: TagProcessor}}"></ul>
        // Binding Handler source: http://stackoverflow.com/questions/12855157/how-to-use-tagit-with-knockout
        init: function (element, valueAccessor, allBindingsAccessor) {
            var bind = function () {
                valueAccessor().tags($(element).tagit("assignedTags"));
            };

            var options = $.extend({
                allowSpaces: false,
                caseSensitive: false,
                showAutocompleteOnFocus: true,
                afterTagAdded: bind,
                afterTagRemoved: bind,
                placeholderText: "",
                preprocessTag: function () { }
            }, valueAccessor().options || {});

            var tags = valueAccessor()["autocomplete"];
            if (tags)
                $.extend(options, {
                    autocomplete: $.extend({ source: tags.source, delay: 0, minLength: 0 },tags)
                });

            $(element).tagit(options);
        },
        update: function (element, valueAccessor) {
            var value = ko.utils.unwrapObservable(valueAccessor());
            var tags = value.tags();
            $(element).tagit("removeAll");
            for (var x = 0; x < tags.length; x++) {
                $(element).tagit("createTag", tags[x]);
            }
        }
    }; */

    /**
     *  Example usage:
     *  function ViewModel(choices, choice) {
     *      this.choices = ko.observableArray(choices);
     *      this.choice = ko.observable(this.choices.find("id", choice));
     *  };
     *
     *  <select data-bind="options: choices, optionsText: 'name', value: choice"></select>
     *
     *  ID: <span data-bind="text: choice().id"></span>
     * @param prop
     * @param data
     * @returns {*}
     */
    ko.observableArray.fn.find = function(prop, data) {
        var valueToMatch = data[prop];
        return ko.utils.arrayFirst(this(), function(item) {
            return item[prop] === valueToMatch;
        });
    };

    ko.bindingHandlers.datepicker = {
        init: function (element, valueAccessor, allBindingsAccessor) {
            //initialize datepicker with some optional options
            var options = allBindingsAccessor().datepickerOptions || {};
            $(element).datepicker(options).on("changeDate", function (ev) {
                var observable = valueAccessor();

                if ( Object.prototype.toString.call(ev.date) === "[object Date]" ) {
                    // it is a date
                    if ( !isNaN( ev.date.getTime() ) ) {  // d.valueOf() could also work
                        observable(ev.date);
                    }
                }
            });
        },
        update: function (element, valueAccessor) {
            var value = ko.utils.unwrapObservable(valueAccessor());

            if ( Object.prototype.toString.call(value) === "[object Date]" ) {
                // it is a date
                if ( !isNaN( value.getTime() ) ) {  // d.valueOf() could also work
                    $(element).datepicker("update", value);
                }
            }
        }
    };

    /* Bootstrap.typeahead
     ko.bindingHandlers.typeahead = {
     init: function (element, valueAccessor, allBindingsAccessor) {
     var options = valueAccessor() || {};
     var allBindings = allBindingsAccessor();
     // update the "value" binding on select
     var modelValue = allBindings.value;
     /* if (modelValue) {
     var handleValueChange = function (item) {
     var valueToWrite = item ? item : $(element).val();
     if (ko.isWriteableObservable(modelValue)) {
     modelValue(valueToWrite);
     }
     return item;
     };
     options.updater = handleValueChange;
     }
     // call bootstrap type ahead
     $(element).typeahead(options).on('typeahead:selected', function (event) {
     //alert(event.result);
     //updateValues(datum);
     modelValue(event.data);
     }).on('typeahead:autocompleted', function (event) {
     //alert(event.result);
     //updateValues(datum);
     modelValue(event.data);
     });
     }
     };
     /*
     // Typeahead handler
     ko.bindingHandlers.TypeAhead = {
     init: function (element, valueAccessor, allBindingsAccessor) {
     var $element = $(element);
     var allBindings = allBindingsAccessor();
     var idBinding = valueAccessor() || {};

     var updateValues = function (val, id) {
     allBindings.value(val);
     idBinding(id);
     $element.originalDisplayValue = val;
     };

     $element.originalDisplayValue =  ko.utils.unwrapObservable(allBindings.value);

     $element.attr("autocomplete", "off")
     .typeahead({
     'remote': allBindings.remote,
     'minLength': allBindings.minLength,
     'items': allBindings.items
     }).on('typeahead:selected', function (el, datum, dataset) {
     updateValues(datum.value, datum.id);
     }).on('typeahead:autocompleted', function (el, datum, dataset) {
     updateValues(datum.value, datum.id);
     }).blur(function () {
     // reset to originalvalue
     allBindings.value($element.originalDisplayValue);
     });
     } ,
     update: function(element, valueAccessor) {
     var elem = $(element);
     var value = valueAccessor();


     //elem.val(value.target());
     }
     };

     // Bootstrap.Typeahead binding: presently requires custom version from gist: https://gist.github.com/1866577.
     // Use like so: data-bind="typeahead: { target: selectedNamespace, source: namespaces }"
     /* ko.bindingHandlers.typeahead = {
     init: function(element, valueAccessor) {
     var binding = this;
     var elem = $(element);
     var value = valueAccessor();

     // Setup Bootstrap Typeahead for this element.
     elem.typeahead(
     {
     source: function() { return ko.utils.unwrapObservable(value.source); },
     onselect: function(val) { value.target(val); }
     });

     // Set the value of the target when the field is blurred.
     elem.blur(function() { value.target(elem.val()); });
     },
     update: function(element, valueAccessor) {
     var elem = $(element);
     var value = valueAccessor();
     elem.val(value.target());
     }
     }; */

    // Typeahead handler
    ko.bindingHandlers.TypeAhead = {
        init: function (element, valueAccessor, allBindingsAccessor) {
            var $element = $(element);
            var allBindings = allBindingsAccessor();
            var displayValue = valueAccessor() || {};

            var updateValues = function (el, datum, dataset) {
                $element.originalDisplayValue = datum.value;
                displayValue(datum.value);

                if(allBindings.idfield !== undefined && datum.id !== undefined) {
                    allBindings.idfield(datum.id);
                }
                if(allBindings.modelObjectUpdater !== undefined && datum.dataobject !== undefined ) {
                    allBindings.modelObjectUpdater(datum.dataobject);
                }
            };

            $element.originalDisplayValue = ko.utils.unwrapObservable(displayValue());

            $element.attr("autocomplete", "off")
                .typeahead({
                    'remote': allBindings.remote,
                    'minLength': allBindings.minLength,
                    'items': allBindings.items
                }).on('typeahead:selected', updateValues).on('typeahead:autocompleted', updateValues).blur(function () {
                    // reset to originalvalue
                    $element.val($element.originalDisplayValue);
                });
        },
        update: function(element, valueAccessor) {
            var $element = $(element);
            var displayValue = valueAccessor();
            $element.val(displayValue());
        }
    };
})
