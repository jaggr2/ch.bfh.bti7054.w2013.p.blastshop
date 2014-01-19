/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 23.11.13
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */

define(['plugins/http', 'durandal/app', 'knockout', 'durandal/system', 'plugins/router', 'lib/datamodel'], function (http, app, ko, system, router, dataModel) {
    //Note: This module exports an object.
    //That means that every module that "requires" it will get the same object instance.
    //If you wish to be able to create multiple instances, instead export a function.
    //See the "welcome" module for an example of function export.

    //var dataModel = require();


    var self = this;
    articles = ko.observableArray(),
        selectedArticle = ko.observable(),
        getArticles = function() {
            return $.getJSON('/api/article/all', { }, function(response) {
                var articles = $.map(response, function(item) {return new dataModel.Article(item) });
                //system.log(articles);
                self.articles(articles); //(articles);
            });
        },

        editArticle = function(art) {
            router.navigate('article/' + art.id());
        },

        createArticle = function() {
            router.navigate('article/create');
        },

        deleteArticle = function(art) {
            $.ajax({
                type:"DELETE",
                url:"/api/article",
                data: ko.toJSON(art).toString(),
                dataType:"json",
                contentType:"application/json",
                success: function(data, textStatus, jqXHR) {
                    console.log("ajax success data: " + data);
                    console.log("ajax success textStatus: " + textStatus);
                    console.log("ajax success jqXHR: " + jqXHR);

                    articles.remove(art);

                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log("ajax error xhr: " + jqXHR);
                    console.log("ajax error textStatus: " + textStatus);
                    console.log("ajax error errorThrown: " + errorThrown);

                    alert("error: " + textStatus);
                }
            });
        };


    var isAlt = false;

    return {

        that: this,
        articles: articles,
        getArticles: getArticles,
        createArticle: createArticle,
        editArticle: editArticle,
        deleteArticle: deleteArticle,
        displayName: 'Article',

        activate: function () {

            getArticles();

            $(document).keyup(function (e) {
                if(e.which == 18) isAlt=false;
            }).keydown(function (e) {
                    //alert(e.which); ESC=27
                    if(e.which == 18) isAlt=true;
                    if(e.which == 78 && isAlt == true) {
                        //run code for ALT+N
                        createArticle();
                        //$('#addressName').focus();
                        e.preventDefault();
                    }
                });

        },
        deactivate: function() {
            $(document).off('keydown');
            $(document).off('keyup');
        },
        askDelete: function(item) {

            return app.showMessage('Soll dieser Artikel wirklich gelöscht werden?', 'Wirklich löschen?', ['Yes', 'No']).then(function(dialogResult){
                if(dialogResult == 'Yes') {
                    deleteArticle(item);
                }
            });

            //the app model allows easy display of modal dialogs by passing a view model
            //views are usually located by convention, but you an specify it as well with viewUrl
            /* item.viewUrl = 'views/yesnomessagebox';
             item.messageBoxText = 'Wollen Sie die Adresse wirklich löschen?';
             item.messageBoxTitle = 'Adresse wirklich löschen?'
             app.showDialog(item); */
        }
    };
});