define(['knockout','lib/bindingHandlers'], function (ko, bindingHandlers) {

    Region = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.localName = ko.observable(data.localName);
        self.postalCode = ko.observable(data.postalCode);
        self.parentRegion = (data.parentRegion != null ? new Region(data.parentRegion) : null);

        self.getDisplayValue = function() {
            return self.postalCode() + " " + self.localName();
        };

        self.getParentsOnly = function() {
            if(self.parentRegion != null) {
                return self.parentRegion.getParents();
            }
            return "";
        };

        self.getParents = function() {
            if(self.parentRegion != null) {
                return ko.utils.unwrapObservable(self.localName) + ", " + self.parentRegion.getParents();
            }
            else {
                return ko.utils.unwrapObservable(self.localName);
            }
        };
    };

    AddressRelation = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
    };

    Language = function(data) {
        var self = this;
        if(data === undefined) {
            data = {}
        }

        self.code = ko.observable(data.code);
        self.englishName = ko.observable(data.englishName);
        self.localName = ko.observable(data.localName);
        self.rowStatus = ko.observable(data.rowStatus);
    };

    Unit = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.id = ko.observable(data.id);
        self.type = ko.observable(data.type);
        self.rowStatus = ko.observable(data.rowStatus);
        self.rootUnit = ko.observable((data.rootUnit != null ? new Unit(data.rootUnit) : null));
        self.factorToRootUnit = ko.observable(data.factorToRootUnit);

        self.languages = ko.observableArray(ko.utils.arrayMap(data.languages, function(language) {
            return new UnitLanguage(language);
        }));

        self.getCurrentUnitLanguage = function() {
            var allUnitLanguages = self.languages();
            for (var i = 0; i < allUnitLanguages.length; i++) {
                if( currentUserLanguage() != null && allUnitLanguages[i] != null && allUnitLanguages[i].language.code() == currentUserLanguage().code()) {
                    return allUnitLanguages[i];
                }
            }

            // if user language not found, return the first element (or for the future the english one)
            if(allUnitLanguages.length > 0) {
                return allUnitLanguages[0];
            }

            return null;
        };

        self.displayName = ko.computed(function() {
            var currentUnitLanguage = self.getCurrentUnitLanguage();
            if(currentUnitLanguage != null) {
                return currentUnitLanguage.name + " (" + currentUnitLanguage.abbrevation + ")";
            }

            return "no unit name"
        });
    };

    UnitLanguage = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }

        self.id = data.id;
        self.abbrevation = data.abbrevation;
        self.name = data.name;
        self.language = getLanguage(data.language);
    };

    Currency = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.code = ko.observable(data.code);
        self.symbol = ko.observable(data.symbol);
        self.rowStatus = ko.observable(data.rowStatus);
        self.globalExchangeRateToCHF = ko.observable(data.globalExchangeRateToCHF);

        self.languages = ko.observableArray(ko.utils.arrayMap(data.languages, function(language) {
            return new CurrencyLanguage(language);
        }));

        self.getCurrentCurrencyLanguage = function() {
            var allCurrencyLanguages = self.languages();
            for (var i = 0; i < allCurrencyLanguages.length; i++) {
                if( currentUserLanguage() != null && allCurrencyLanguages[i] != null && allCurrencyLanguages[i].language.code() == currentUserLanguage().code()) {
                    return allCurrencyLanguages[i];
                }
            }

            // if user language not found, return the first element (or for the future the english one)
            if(allCurrencyLanguages.length > 0) {
                return allCurrencyLanguages[0];
            }

            return null;
        };

        self.displayName = ko.computed(function() {
            var currentCurrencyLanguage = self.getCurrentCurrencyLanguage();
            if(currentCurrencyLanguage != null) {
                return self.code() + " (" + currentCurrencyLanguage.longName + ")";
            }

            return "no unit name"
        });
    };

    CurrencyLanguage = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }

        self.id = data.id;
        self.shortName = data.shortName;
        self.longName = data.longName;
        self.language = getLanguage(data.language);
    };

    getLanguage = function(languageObject) {
        if(languageObject !== undefined && languageObject != null) {
            var allLanguagesArray = allLanguages();
            for (var i = 0; i < allLanguagesArray.length; i++) {
                if( allLanguagesArray[i].code() == languageObject.code) {
                    return allLanguagesArray[i];
                }
            }
        }
        return null;
    };

    getUnit = function(unitobject) {
        if(unitobject !== undefined && unitobject != null) {
            var allUnitsArray = allUnits();
            for (var i = 0; i < allUnitsArray.length; i++) {
                if( allUnitsArray[i].id() == unitobject.id) {
                    return allUnitsArray[i];
                }
            }
        }
        return null;
    };

    getCurrency = function(currencyobject) {
        if(currencyobject !== undefined && currencyobject != null) {
            var allUnitsArray = allCurrencies();
            for (var i = 0; i < allUnitsArray.length; i++) {
                if( allUnitsArray[i].code() == currencyobject.code) {
                    return allUnitsArray[i];
                }
            }
        }
        return null;
    };

    formatCurrency = function(value) {
        return (value !== undefined && value != null ? value.toFixed(2) : "0.00");
    };

    Address = function(data) {
        var self = this;
        if (data === undefined || data == null) {
            data = {}
        }

        self.id = ko.observable(data.id);

        self.isNew = function() {
            return self.id() == null || self.id() <= 0;
        };

        self.initCopy = function() {
            self.id(null);
        };

        self.createdOn = ko.observable(new Date(data.createdOn));
        self.updatedOn = ko.observable(new Date(data.updatedOn));

        self.name  = ko.observable(data.name);
        self.preName   = ko.observable(data.preName);
        self.addressline1 = ko.observable(data.addressline1);
        self.addressline2 = ko.observable(data.addressline2);
        self.birthdate = ko.observable(new Date(data.birthdate));
        self.gender = ko.observable(data.gender);

        self.isCompanyOrGroup = function() {
            if(self.gender() != null) {
                if(self.gender() == 'COMPANY' || self.gender() == 'GROUP') {
                    return true;
                }
            }
            return false;
        }

        self.getGender = function() {
            if(self.gender() == 'MALE_PERSON') return 'Mr.';
            if(self.gender() == 'FEMALE_PERSON') return 'Mrs.';
            if(self.gender() == 'COMPANY') return 'Company';
            if(self.gender() == 'GROUP') return 'Group';
            return 'Unknown';
        };

        self.city = ko.observable(null);
        self.cityDisplay = ko.observable(null);
        self.cityId = ko.observable(null);

        self.changeCity = function(regiondata) {
            self.city(new Region(regiondata));

            self.cityDisplay(regiondata.postalCode + " " + regiondata.localName);
            self.cityId(regiondata.id);
        };

        if(data.city != null) {
            self.changeCity(data.city);
        }

        self.getCityParents = function() {
            return (self.city() != null ? self.city().getParentsOnly() : "");
        };

        self.contacts = ko.observableArray(ko.utils.arrayMap(data.contacts, function(contact) {
            return { contactType: contact.contactType, value: contact.value };
        }));

        self.addContact = function() {
            self.contacts.push({
                contactType: "",
                value: ""
            });
        };

        self.removeContact = function(contact) {
            self.contacts.remove(contact);
        };

        self.addressRelationDisplay = function(data) {
            var child = this;

            if(data.childAddress != null) {
                child.childAddressId = ko.observable(data.childAddress.id);
                child.childAddressDisplay = ko.observable(data.childAddress.preName + " " + data.childAddress.name);
            }
            else {
                child.childAddressId = ko.observable();
                child.childAddressDisplay = ko.observable();
            }

            if(data.addressFunction != null) {
                child.addressFunctionId = ko.observable(data.addressFunction.id); //new self.addressFunctionDisplay(data.addressFunction);
                child.childFunctionDisplay = ko.observable(data.addressFunction.i18nDescription);
            }
            else {
                child.addressFunctionId = ko.observable(); //new self.addressFunctionDisplay(data.addressFunction);
                child.childFunctionDisplay = ko.observable();
            }
        };

        self.relations = ko.observableArray(ko.utils.arrayMap(data.relations, function(relation) {
            return new self.addressRelationDisplay(relation);
        }));

        self.addRelation = function() {
            self.relations.push(new self.addressRelationDisplay({}));
        };

        self.removeRelation = function(relation) {
            self.relations.remove(relation);
        };

        self.id.formattedTitle = ko.computed(function() {
            if(!self.isNew()) {
                return "Adresse ID " + this.id() + " bearbeiten";
            }
            else {
                return "Neue Adresse erstellen";
            }
        }, self);

        self.testtext = function() {
            if(this.id !== undefined && this.id() > 0) {
                return "Adresse ID " + this.id() + " bearbeiten";
            }
            else {
                return "Neue Adresse erstellen";
            }
        };

        self.getURL = ko.computed(function() {
            return "#address/" + self.id();
        }, self);

        self.getFullName = function() {
            return (self.preName() != null ? self.preName() + " " : "") + self.name();
        };

        self.getFullAddress = function() {
            return (self.addressline1() != null ? self.addressline1() + " " : "") +
                (self.addressline2() != null ? self.addressline2() + ", " : ", ") +
                (self.city() != null ? self.city().getDisplayValue() : "");
        };

        return self;
    };

    ArticleOption = function(data, selected) {
        var self = this;
        if (data === undefined) {
            data = {}
        }

        //self.uuid = generateGuid();

        self.id = ko.observable(data.id);

        self.id.isSelected = ko.computed(function() {
            return selected !== undefined && self === selected();
        }, self);

        self.isNew = function() {
            return self.id() == null || self.id() <= 0;
        };

        self.initCopy = function() {
            self.id(null);
            self.number(null);
        };

        self.initNew = function() {
            if(self.languages.length == 0) {
                var hasGerman = false, hasEnglish = false, hasFrench = false;

                ko.utils.arrayForEach(this.languages(), function(lang) {
                    if(lang.language() != null && lang.language().code() == 'deu') hasGerman = true;
                    if(lang.language() != null && lang.language().code() == 'fra') hasFrench = true;
                    if(lang.language() != null && lang.language().code() == 'eng') hasEnglish = true;
                });

                if(!hasGerman) self.languages.push(new ArticleLanguage({ language: { code: 'deu'}}));
                if(!hasFrench) self.languages.push(new ArticleLanguage({ language: { code: 'fra'}}));
                if(!hasEnglish) self.languages.push(new ArticleLanguage({ language: { code: 'eng'}}));
            }
        };

        self.number   = ko.observable(data.number);

        self.id.displayName = ko.computed(function() {
            return (self.number() != null ? self.number() : 'Option');
        }, self);

        self.unit  = ko.observable(getUnit(data.unit));

        self.sellPrice = ko.observable(data.sellPrice);
        self.sellPriceCurrency = ko.observable(getCurrency(data.sellPriceCurrency));

        self.languages = ko.observableArray(ko.utils.arrayMap(data.languages, function(lang) {
            return new ArticleLanguage(lang);
        }));

        self.addArticleLang = function() {
            self.languages.push(new ArticleLanguage({}));
        };

        self.removeArticleLang = function(lang) {
            self.languages.remove(lang);
        };


        if(self.isNew()) {
            self.initNew();
        }
    };

    ArticleLanguage = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.id = ko.observable(data.id);
        self.description = ko.observable(data.description);
        self.title = ko.observable(data.title);
        self.language = ko.observable(getLanguage(data.language));
    };

    ArticleCollection = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.id = ko.observable(data.id);
        self.parentCollection = ko.observable(data.description);
        self.languages = ko.observableArray(ko.utils.arrayMap(data.languages, function(lang) {
            return new ArticleCollectionLanguage(lang);
        }));

        self.addArticleLang = function() {
            self.languages.push(new ArticleCollectionLanguage({}));
        };

        self.removeArticleLang = function(lang) {
            self.languages.remove(lang);
        };

        self.id.uniqueName = ko.computed(function() {

            var currentLanguage = currentUserLanguage() || getLanguage({ code: "eng" });
            for (var i = 0; i < self.languages().length; i++) {
                if( self.languages()[i].language() == currentLanguage) {
                    return self.languages()[i].name();
                }
            }

            return null;

        }, self);
    };

    ArticleCollectionLanguage = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.id = ko.observable(data.id);
        self.name = ko.observable(data.name);
        self.language = ko.observable(getLanguage(data.language));
    };

    Document = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }
        self.id = ko.observable(data.id);
        self.trunkNumber = ko.observable(data.trunkNumber);
        self.documentNumber = ko.observable(data.documentNumber);
        self.documentType = ko.observable(data.documentType);
        self.clientAddress = ko.observable(new Address(data.clientAddress));
        self.invoiceAddress = ko.observable(new Address(data.invoiceAddress));
        self.deliveryAddress = ko.observable(new Address(data.deliveryAddress));
        self.deliveryCosts = ko.observable(data.deliveryCosts);
        self.invoiceDate = ko.observable(data.invoiceDate);
        self.deliveryDate = ko.observable(data.deliveryDate);

        self.rows = ko.observableArray(ko.utils.arrayMap(data.rows, function(row) {
            return new DocumentRow(row);
        }));

        self.addRow = function() {
            self.rows.push(new DocumentRow({}));
        };

        self.addArticleOptionRow = function(articleOptionID) {

            $.ajax({
                type:"GET",
                url:"/article/option/" + articleOptionID,
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {

                    self.rows.push(new DocumentRow({ articleOption: data, quantity: 1, price: data.sellPrice }));

                    //updateShoppingCart();

                    console.log("articleOption with id " + data.id + " successfully loaded!");
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error");
                }
            });
        };

        self.removeRow = function(row) {
            self.rows.remove(row);
        };

        self.rowCount = ko.computed(function() {
            return self.rows().length;
        });

        self.subTotal = ko.computed(function() {
            var total = 0;
            $.each(self.rows(), function () {
                total += this.rowTotal()
            });
            return total;
        });

        self.grandTotal = ko.computed(function() {
            return self.subTotal() + (self.deliveryCosts() != null ? self.deliveryCosts() : 0);
        });
    };

    DocumentRow = function(data) {
        var self = this;
        if (data === undefined) {
            data = {}
        }

        self.id = ko.observable(data.id);
        self.articleOption = ko.observable((data.articleOption != null ? new ArticleOption(data.articleOption) : null));
        self.text = ko.observable(data.text);
        self.rowOrder = ko.observable(data.rowOrder);
        self.quantity = ko.observable(data.quantity);
        self.price = ko.observable(data.price);

        self.rowTotal = ko.computed(function() {
            return self.price() * parseInt("0" + self.quantity(), 10);
        });
    };

    allLanguages = ko.observableArray();
    allUnits = ko.observableArray();
    allCurrencies = ko.observableArray();
    allCollections = ko.observableArray();
    currentUserLanguage = ko.observable(null);

    return {
        loadGlobalDatastore: function() {
            this.loadLanguages();
            this.loadUnits();
            this.loadCurrencies();
            this.loadCollections();
        },
        loadLanguages: function() {
            if(allLanguages() == null || allLanguages() <= 0) {
                $.ajax({
                    type:"GET",
                    url:"/api/language/all",
                    dataType:"json",
                    contentType:"application/json",
                    success: function(data, textStatus, jqXHR) {

                        allLanguages.removeAll();
                        for (var i = 0; i < data.length; i++) {
                            allLanguages.push(new Language(data[i]));
                        }

                        //var langs = ko.utils.arrayMap(data, function(language) {
                        //    return new Language(language);
                        //});
                        //data);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.log("ajax error xhr: " + jqXHR);
                        console.log("ajax error textStatus: " + textStatus);
                        console.log("ajax error errorThrown: " + errorThrown);

                        alert("error");
                    }
                });
            };
        },
        loadUnits: function() {
            if(allUnits() == null || allUnits() <= 0) {
                $.ajax({
                    type:"GET",
                    url:"/api/unit/all",
                    dataType:"json",
                    contentType:"application/json",
                    success: function(data, textStatus, jqXHR) {

                        allUnits.removeAll();
                        for (var i = 0; i < data.length; i++) {
                            allUnits.push(new Unit(data[i]));
                        }

                        //var langs = ko.utils.arrayMap(data, function(language) {
                        //    return new Language(language);
                        //});
                        //data);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.log("ajax error xhr: " + jqXHR);
                        console.log("ajax error textStatus: " + textStatus);
                        console.log("ajax error errorThrown: " + errorThrown);

                        alert("error");
                    }
                });
            };
        },
        loadCurrencies: function() {
            if(allCurrencies() == null || allCurrencies() <= 0) {
                $.ajax({
                    type:"GET",
                    url:"/api/currency/all",
                    dataType:"json",
                    contentType:"application/json",
                    success: function(data, textStatus, jqXHR) {

                        allCurrencies.removeAll();
                        for (var i = 0; i < data.length; i++) {
                            allCurrencies.push(new Currency(data[i]));
                        }

                        //var langs = ko.utils.arrayMap(data, function(language) {
                        //    return new Language(language);
                        //});
                        //data);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.log("ajax error xhr: " + jqXHR);
                        console.log("ajax error textStatus: " + textStatus);
                        console.log("ajax error errorThrown: " + errorThrown);

                        alert("error");
                    }
                });
            };
        },
        loadCollections: function() {
        if(allCollections() == null || allCollections().length <= 0) {
            $.ajax({
                type:"GET",
                url:"/api/articlecollection/all",
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {

                    allCollections.removeAll();
                    for (var i = 0; i < data.length; i++) {
                        allCollections.push(new ArticleCollection(data[i]));
                    }

                    //var langs = ko.utils.arrayMap(data, function(language) {
                    //    return new Language(language);
                    //});
                    //data);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error");
                }
            });
        }},

        Article: function(data) {

            var self = this;
            self.id = ko.observable(data.id);

            self.isNew = function() {
                return self.id() == null || self.id() <= 0;
            };

            self.initCopy = function() {
                self.id(null);
                ko.utils.arrayForEach(self.options, function(option) {
                    option.initCopy();
                })
            };

            self.initNew = function() {
                self.addArticleOption();
            };

            self.selectedArticleOption = ko.observable();

            self.options = ko.observableArray(ko.utils.arrayMap(data.options, function(option) {
                return new ArticleOption(option, self.selectedArticleOption);
            }));

            self.addArticleOption = function() {
                var newItem = new ArticleOption({}, self.selectedArticleOption);
                self.options.push(newItem);
                self.selectedArticleOption(newItem);
            };

            self.removeArticleOption = function(articleOption) {
                if(self.options().length > 1) {
                    self.options.remove(articleOption);
                    self.selectedArticleOption(self.options()[0]);
                }
            }

            self.articleCollections = ko.observableArray(ko.utils.arrayMap(data.articleCollections, function(collection) {
                return new ArticleCollection(collection);
            }));

            self.parseArticleCollections = function(tagArray) {

            }


            self.copyArticleOption = function(articleOption) {
                var newItem = new ArticleOption(JSON.parse(ko.toJSON(articleOption)), self.selectedArticleOption);
                newItem.initCopy();
                self.options.push(newItem);
                self.selectedArticleOption(newItem);
            }

            self.id.formattedTitle = ko.computed(function() {
                if(!self.isNew()) {
                    return "Artikel ID " + this.id() + " bearbeiten";
                }
                else {
                    return "Neuen Artikel erstellen";
                }
            }, self);

            self.getURL = ko.computed(function() {
                return "#article/" + self.id();
            }, self);

            if(self.isNew()) {
                self.initNew();
            }
            else {
                self.selectedArticleOption(self.options()[0]);
            }
        }
    };
});
