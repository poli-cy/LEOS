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
define(function aknClausePluginModule(require) {
    "use strict";

    // load module dependencies
    var pluginTools = require("plugins/pluginTools");

    var CKEDITOR = require("promise!ckEditor");

    var pluginName = "aknClause";

    var pluginDefinition = {
        init : function init(editor) {
        }
    };

    pluginTools.addPlugin(pluginName, pluginDefinition);

    var transformationConfig = {
            akn : "clause",
            html : "div[data-akn-name=clause]",
            attr : [ {
                akn : "xml:id",
                html : "id"
            }, {
                akn : "leos:origin",
                html : "data-origin"
            }, {
                akn : "leos:deletable",
                html : "leos:deletable",
            }, {
                akn : "leos:editable",
                html : "leos:editable",
            }, {
                akn : "leos:optionlist",
                html : "leos:optionlist"
            }, {
                akn : "leos:selectedoption",
                html : "leos:selectedoption"
            }, {
                html : "data-akn-name=clause"
            }],
            sub: {
                akn: "content",
                html: "div",
                attr : [ {
                    akn : "xml:id",
                    html : "data-akn-content-id"
                }, {
                    akn : "leos:origin",
                    html : "data-content-origin"
                }],
                sub: {
                    akn: "mp",
                    html: "div/p",
                    attr : [ {
                        akn : "xml:id",
                        html : "data-akn-mp-id"
                    }, {
                        akn : "leos:origin",
                        html : "data-mp-origin"
                    }],
                    sub: {
                        akn: "text",
                        html: "div/p/text"
                    }
                }
            }
        };


    pluginTools.addTransformationConfigForPlugin(transformationConfig, pluginName);

    // return plugin module
    var pluginModule = {
        name : pluginName,
        transformationConfig: transformationConfig,
    };

    return pluginModule;
});