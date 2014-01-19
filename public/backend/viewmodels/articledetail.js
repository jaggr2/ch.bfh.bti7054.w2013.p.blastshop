/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 23.11.13
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */


define(['plugins/http', 'durandal/app', 'knockout', 'plugins/router', 'lib/datamodel', 'bootstrap', 'dropzone'], function (http, app, ko, router, dataModel, bootstrap, Dropzone) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.

    var self = this;
    currentArticle = ko.observable(),

        loadArticle = function(id) {
            $.ajax({
                type:"GET",
                url:"/api/article/" + id,
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    currentArticle(new dataModel.Article(data));

                    console.log("article with id " + data.id + " successfully loaded!");
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error");
                }
            });
        },
        loadNewArticle = function() {
            currentArticle(null);
            currentArticle(new dataModel.Article({}));
        },
        createArticle = function() {
            router.navigate('article/create')
        },
        deleteArticle = function() {
            $.ajax({
                type:"DELETE",
                url:"/api/article",
                data: ko.toJSON(currentArticle).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {

                    currentArticle(null);

                    router.navigate('article', true);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + msg.responseText);
                }
            });
        },
        submitArticle = function() {
            $.ajax({
                type:"POST",
                url:"/api/article",
                data: ko.toJSON(currentArticle).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {

                    currentArticle(new dataModel.Article(data));

                    console.log("article with id " + data.id + " successfully saved!");


                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + textStatus);
                }
            });
        },
        copyArticle = function() {
            if(currentArticle() != null) {
                currentArticle().initCopy();
                router.navigate('article/create', { replace: false, trigger: false })
            }
        };

    var isAlt = false;

    return function() {

        var self = this;
        self.currentArticle = currentArticle;
        self.loadArticle = loadArticle;
        self.createArticle = createArticle;
        self.deleteArticle = deleteArticle;
        self.submitArticle = submitArticle;
        self.loadNewArticle = loadNewArticle;
        self.copyArticle = copyArticle;

        self.closeArticle = function() {
            router.navigate('article');
        }

        self.askDeleteArticle = function() {
            return app.showMessage('Soll dieser Artikel wirklich gelöscht werden?', 'Wirklich löschen?', ['Yes', 'No']).then(function(dialogResult){
                if(dialogResult == 'Yes') {
                    self.deleteArticle();
                }
            });
        }


        self.activate = function(articleID) {
            //the router's activator calls this function and waits for it to complete before proceding

            if(articleID !== undefined && articleID == 'create') {
                self.loadNewArticle();
            }
            else if (articleID !== undefined && articleID > 0) {
                self.loadArticle(articleID);
            }
            else {
                alert("Invalid parameter!");
            }

            $(document).keyup(function (e) {
                if(e.which == 18) isAlt=false;
            }).keydown(function (e) {
                    //alert(e.which);
                    if(e.which == 18) isAlt=true;
                    if(e.which == 78 && isAlt == true) {
                        //run code for ALT+N
                        self.createArticle();
                        e.preventDefault();
                    }
                    if(e.which == 83 && isAlt == true) {
                        //run code for ALT+S
                        self.submitArticle();
                        e.preventDefault();
                    }
                    if(e.which == 67 && isAlt == true) {
                        //run code for ALT+C
                        self.copyArticle();
                        e.preventDefault();
                    }
                    if(e.which == 46 && isAlt == true) {
                        //run code for ALT+DELETE
                        self.askDeleteArticle();
                        e.preventDefault();
                    }
                    if(e.which == 27) self.closeArticle(); // 27=ESC-Key
                });
        };

        self.deactivate = function() {
            $(document).off('keydown');
            $(document).off('keyup');
        }

        self.compositionComplete = function() {
            //var myDropzone = new Dropzone("form#backenddropzone", { url: "/api/file/create/fromform"});

        }
    }
});