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
define(function aknHtmlSubScriptPluginModule(require) {
    "use strict";
    // load module dependencies
    var pluginTools = require("plugins/pluginTools");

    var pluginName = "aknHtmlSubScript";

    var pluginDefinition = {
        init: function init(editor) {}
    };

    pluginTools.addPlugin(pluginName, pluginDefinition);
    var transformationConfig = {
        akn: "sub",
        html: "sub",
        attr: [{
            akn: "xml:id",
            html: "id"
        }, {
            akn : "leos:origin",
            html : "data-origin"
        }],
        sub: {
            akn: "text",
            html: "sub/text"
        }
    };
    pluginTools.addTransformationConfigForPlugin(transformationConfig, pluginName);

    // return plugin module
    var pluginModule = {
        name: pluginName,
        transformationConfig: transformationConfig,
        specificConfig: {
            coreStyles_subscript : { element: 'sub' ,alwaysRemoveElement: true }
        }
    };
    return pluginModule;
});