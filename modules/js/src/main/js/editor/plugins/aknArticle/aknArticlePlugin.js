/*
 * Copyright 2019 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
; // jshint ignore:line
define(function aknArticlePluginModule(require) {
    "use strict";

    // load module dependencies
    var pluginTools = require("plugins/pluginTools");
    var aknArticleNumberWidget = require("plugins/aknArticleWidget/aknArticleNumberWidget");
    var aknArticleHeadingWidget = require("plugins/aknArticleWidget/aknArticleHeadingWidget");
    var leosKeyHandler = require("plugins/leosKeyHandler/leosKeyHandler");

    var pluginName = "aknArticle";

    var ENTER_KEY = 13;
    var SHIFT_ENTER = CKEDITOR.SHIFT + ENTER_KEY;

    var pluginDefinition = {
        requires : "widget,leosWidget",

        init : function init(editor) {
            editor.on("toHtml", removeInitialSnapshot, null, null, 100);

            editor.widgets.add(aknArticleNumberWidget.name, aknArticleNumberWidget.definition);
            editor.addContentsCss(pluginTools.getResourceUrl(pluginName, aknArticleNumberWidget.css));

            editor.widgets.add(aknArticleHeadingWidget.name, aknArticleHeadingWidget.definition);
            editor.addContentsCss(pluginTools.getResourceUrl(pluginName, aknArticleHeadingWidget.css));

            leosKeyHandler.on({
                editor : editor,
                eventType : 'key',
                key : ENTER_KEY,
                action : _onEnterKey
            });

            leosKeyHandler.on({
                editor : editor,
                eventType : 'key',
                key : SHIFT_ENTER,
                action : _onShiftEnterKey
            });
        }
    };

    function _onEnterKey(context) {
        if(context.event.data.domEvent.$.srcElement.tagName.toLocaleLowerCase() === 'h2'){
            context.event.cancel();
        }
    }

    function _onShiftEnterKey(context) {
        if(context.event.data.domEvent.$.srcElement.tagName.toLocaleLowerCase() === 'h2'){
            context.event.cancel();
        }
    }

    pluginTools.addPlugin(pluginName, pluginDefinition);


    /*
     * Removes the initial snapshot which don't have 'article' as top level element 
     */
    function removeInitialSnapshot(event) {
        if (event.editor.undoManager.snapshots.length > 0) {
            if (event.editor.undoManager.snapshots[0].contents.indexOf("article")<0) {
                event.editor.undoManager.snapshots.shift();
            }
        }
    }
    
    var transformationConfig = {
        akn : 'article',
        html : 'article',
        attr : [ {
            akn : "xml:id",
            html : "id"
        }, {
            akn : "leos:origin",
            html : "data-origin"
        }, {
            akn : "leos:editable",
            html : "data-akn-attr-editable"
        }, {
            akn : "leos:deletable",
            html : "data-akn-attr-deletable"
        }, {
            html : "data-akn-name=article"
        } ],
        sub : [ {
            akn : "num",
            html : "article/h1",
            attr : [ {
                html : "class=akn-article-num"
            }, {
                akn : "leos:editable",
                html : "contenteditable=false"
            }, {
                akn : "leos:origin",
                html : "data-num-origin"
            }, {
                akn : "xml:id",
                html : "data-akn-num-id"
            } ],
            sub : {
                akn : "text",
                html : "article/h1/text"
            }
        }, {
            akn : "heading",
            html : "article/h2",
            attr : [ {
                html : "class=akn-article-heading"
            },{ 
                html : "data-akn-name=aknHeading"
            }, {
                akn : "leos:origin",
                html : "data-heading-origin"
            }, {
                akn : "leos:editable",
                html : "contenteditable"
            }, {
                akn : "xml:id",
                html : "data-akn-heading-id"
            } ],
            sub : {
                akn : "text",
                html : "article/h2/text"
            }
        }, {
            akn : "article",
            html : "article/ol",
            attr : [ {
                akn : "xml:id",
                html : "id"
            }, {
                akn : "leos:origin",
                html : "data-origin"
            } ]
        } ]
    };

    // return plugin module
    var pluginModule = {
        name : pluginName
    };

    pluginTools.addTransformationConfigForPlugin(transformationConfig, pluginName);

    return pluginModule;
});