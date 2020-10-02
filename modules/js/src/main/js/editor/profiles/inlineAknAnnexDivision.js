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
define(function aknInlineAnnexDivisionProfileModule(require) {
    "use strict";

    // require profile dependencies, if needed
    // e.g. ckEditor, plugins or utilities
    var transformationConfigManager = require("transformer/transformationConfigManager");
    var pluginTools = require("plugins/pluginTools");
    var $ = require('jquery');
    var plugins = [];
    plugins.push(require("plugins/leosInlineSave/leosInlineSavePlugin"));
    plugins.push(require("plugins/leosInlineCancel/leosInlineCancelPlugin"));
    plugins.push(require("plugins/leosInlineEditor/leosInlineEditorPlugin"));   //required for blur/focus
    plugins.push(require("plugins/leosFloatingSpace/leosFloatingSpacePlugin")); //required for positioning inline editor toolbar
    plugins.push(require("plugins/leosWidget/leosWidgetPlugin"));               //requried for handing widget
    plugins.push(require("plugins/leosDropHandler/leosDropHandlerPlugin"));     //required for Widget drag drop
    plugins.push(require("plugins/leosXmlEntities/leosXmlEntitiesPlugin"));     //required for xml entities <--> html entities
    plugins.push(require("plugins/leosAttrHandler/leosAttrHandlerPlugin"));     //required for id cleanup on enter
    plugins.push(require("plugins/aknUnNumberedBlockList/aknUnNumberedBlockListPlugin"));
    plugins.push(require("plugins/aknNumberedBlockList/aknNumberedBlockListPlugin"));
    plugins.push(require("plugins/aknAuthorialNote/aknAuthorialNotePlugin"));
    plugins.push(require("plugins/leosShowblocks/leosShowblocksPlugin"));
    plugins.push(require("plugins/leosTransformer/leosTransformerPlugin"));     //required for editor transformation
    plugins.push(require("plugins/leosFixNestedPs/leosFixNestedPsPlugin"));
    plugins.push(require("plugins/leosTable/leosTablePlugin"));
    plugins.push(require("plugins/aknHtmlBold/aknHtmlBoldPlugin"));
    plugins.push(require("plugins/aknHtmlItalic/aknHtmlItalicPlugin"));
    plugins.push(require("plugins/aknHtmlImage/aknHtmlImagePlugin"));
    plugins.push(require("plugins/aknHtmlUnderline/aknHtmlUnderlinePlugin"));
    plugins.push(require("plugins/aknHtmlSuperScript/aknHtmlSuperScriptPlugin"));
    plugins.push(require("plugins/aknHtmlSubScript/aknHtmlSubScriptPlugin"));
    plugins.push(require("plugins/aknDivision/aknDivisionPlugin"));
    plugins.push(require("plugins/aknParagraph/aknParagraphPlugin"));
    plugins.push(require("plugins/leosTextCaseChanger/leosTextCaseChangerPlugin"));
    plugins.push(require("plugins/aknHtmlJustify/aknHtmlJustifyPlugin"));
    plugins.push(require("plugins/leosImageResize/leosImageResizePlugin"));
    plugins.push(require("plugins/leosMathematicalFormula/leosMathematicalFormulaPlugin"));
    plugins.push(require("plugins/leosBase64Image/leosBase64ImagePlugin"));
    plugins.push(require("plugins/leosSpecialChar/leosSpecialCharPlugin"));
    plugins.push(require("plugins/leosManualRenumbering/leosManualRenumberingPlugin"));
    plugins.push(require("plugins/leosPreventElementDeletion/leosPreventElementDeletionPlugin"));
    plugins.push(require("plugins/leosSpellChecker/leosSpellCheckerPlugin"));
    plugins.push(require("plugins/leosPreventSelectAll/leosPreventSelectAllPlugin"));

    var pluginNames=[];
    var specificConfig={};
    $.each(plugins, function( index, value ) {
        pluginNames.push(value.name);
        specificConfig= $.extend( specificConfig,  value.specificConfig);
    });
    var transformationConfigResolver = transformationConfigManager.getTransformationConfigResolverForPlugins(pluginNames);
    
    // holds ckEditor external plugins names
    var externalPluginsNames = [];
    pluginTools.addExternalPlugins(externalPluginsNames);
    var extraPlugins = pluginNames.concat(externalPluginsNames).join(",");

    var profileName = "Inline AKN Annex Block Container";

    // create profile configuration
    var profileConfig = {
        // user interface language localisation
        language: "en",
        // custom configuration to load (none if empty)
        customConfig: "",
        // comma-separated list of plugins to be loaded
        plugins: "toolbar,wysiwygarea,elementspath," +
                 "clipboard,undo,basicstyles,enterkey," + "list,indent," +
                 "specialchar,table,tableresize,tabletools,tableselection,button,dialog,dialogui,contextmenu,menubutton,widget,pastetext,mathjax",
        // comma-separated list of toolbar button names that must not be rendered
        removeButtons: "Strike,TextColor,PasteText",
        // comma-separated list of additional plugins to be loaded
        extraPlugins: extraPlugins,
        // disable Advanced Content Filter (allow all content)
        allowedContent: true,
        //show toolbar on startup
        startupFocus: 'end',
        //Use native spellchecker
        disableNativeSpellChecker: false,
        //MathJax plugin configuration - Sets the path to the MathJax library
        mathJaxLib: './webjars/MathJax/2.7.0/MathJax.js?config=default',
        // LEOS-2887 removing tooltip title 
        title: false,
        // toolbar groups arrangement, optimised for a single toolbar row
        toolbarGroups : [ {
            name : "save"
        }, {
            name : "clipboard",
            groups : [ "clipboard", "undo" ]
        }, {
            name : "editing",
            groups : [ "selection", "spellchecker" ]
        }, {
            name : "basicstyles",
            groups : [ "basicstyles", "cleanup" ]
        }, {
            name : "paragraph",
            groups : [ "list","indent" ]
        }, {
            name : "align"
        },'/', {
            name: "ref"      //Toolbar group containing Authorial Note button
        }, {
            name: "insert"
        }, {
            name : "tools"     //Toolbar group containing show blocks
        }, {
            name : "mode"       //Toolbar group containing Source button
        }]
    };
    // adding the specific configs coming from the plugins.
    profileConfig = $.extend( profileConfig,  specificConfig);

    // create profile definition
    var profileDefinition = {
        name: profileName,
        config: profileConfig,
        transformationConfigResolver: transformationConfigResolver
    };

    return profileDefinition;
});
