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
define(function aknAlineaMandatePluginModule(require) {
    "use strict";

    // load module dependencies
    var pluginTools = require("plugins/pluginTools");
    var leosHierarchicalElementTransformerStamp = require("plugins/leosHierarchicalElementTransformer/hierarchicalElementTransformer");
    var leosKeyHandler = require("plugins/leosKeyHandler/leosKeyHandler");

    var pluginName = "aknAlineaMandate";
    var ENTER_KEY = 13;
    var SHIFT_ENTER = CKEDITOR.SHIFT + ENTER_KEY;

    var pluginDefinition = {
        init: function init(editor) {
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
            editor.on("toDataFormat", _transformAlinea, null, null, 15);
        }
    };

    function _onEnterKey(context) {
        context.event.cancel();
    }

    function _onShiftEnterKey(context) {
        context.event.cancel();
    }

    function _transformAlinea(event) {
    	if (event.data.dataValue.includes("<alinea>")) {
    		event.data.dataValue = (event.data.dataValue + '?').replace("<alinea>", "").replace("</alinea>?", "");
    	}
    }

    pluginTools.addPlugin(pluginName, pluginDefinition);

    var leosHierarchicalElementTransformer = leosHierarchicalElementTransformerStamp({
        firstLevelConfig: {
            akn: 'alinea',
            html: 'ol[data-akn-name=aknAlineaMandate]',
            attr: [{
                html: "data-akn-name=aknAlineaMandate"
            }]
        },
        rootElementsForFrom: ["alinea"],
        contentWrapperForFrom: "alinea",
        rootElementsForTo: ["ol", "li"]
    });

    var transformationConfig = leosHierarchicalElementTransformer.getTransformationConfig();

    // return plugin module
    var pluginModule = {
        name: pluginName,
        transformationConfig: transformationConfig
    };

    pluginTools.addTransformationConfigForPlugin(transformationConfig, pluginName);

    return pluginModule;
});