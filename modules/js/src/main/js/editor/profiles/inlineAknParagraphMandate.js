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
define(function aknParagraphMandateProfileModule(require) {
     "use strict";

    // require profile dependencies, if needed
    // e.g. ckEditor, plugins or utilities
    var transformationConfigManager = require("transformer/transformationConfigManager");
    var pluginTools = require("plugins/pluginTools");
    var $ = require('jquery');

    var plugins = [];
    plugins.push(require("plugins/leosInlineSave/leosInlineSavePlugin"));
    plugins.push(require("plugins/leosInlineCancel/leosInlineCancelPlugin"));
    plugins.push(require("plugins/leosInlineEditor/leosInlineEditorPlugin"));
    plugins.push(require("plugins/leosTable/leosTablePlugin"));
    plugins.push(require("plugins/aknHtmlAnchor/aknHtmlAnchorPlugin"));
    plugins.push(require("plugins/aknHtmlBold/aknHtmlBoldPlugin"));
    plugins.push(require("plugins/aknHtmlItalic/aknHtmlItalicPlugin"));
    plugins.push(require("plugins/aknHtmlUnderline/aknHtmlUnderlinePlugin"));
    plugins.push(require("plugins/aknNumberedParagraphMandate/aknNumberedParagraphMandatePlugin"));
    plugins.push(require("plugins/leosShowblocks/leosShowblocksPlugin"));
    plugins.push(require("plugins/aknAuthorialNote/aknAuthorialNotePlugin"));
    plugins.push(require("plugins/leosTransformer/leosTransformerPlugin"));
    plugins.push(require("plugins/leosFixNestedPs/leosFixNestedPsPlugin"));
    plugins.push(require("plugins/leosMathematicalFormula/leosMathematicalFormulaPlugin"));
    plugins.push(require("plugins/aknHtmlSuperScript/aknHtmlSuperScriptPlugin"));
    plugins.push(require("plugins/aknHtmlSubScript/aknHtmlSubScriptPlugin"));
    plugins.push(require("plugins/leosWidget/leosWidgetPlugin"));
    plugins.push(require("plugins/leosAttrHandler/leosAttrHandlerPlugin"));
    plugins.push(require("plugins/leosPaste/leosPastePlugin"));
    plugins.push(require("plugins/leosCrossReference/leosCrossReferencePlugin"));
    plugins.push(require("plugins/leosHierarchicalElementShiftEnterHandler/leosHierarchicalElementShiftEnterHandler"));
    plugins.push(require("plugins/leosFloatingSpace/leosFloatingSpacePlugin"));
    plugins.push(require("plugins/leosMessageBus/leosMessageBusPlugin"));
    plugins.push(require("plugins/leosDropHandler/leosDropHandlerPlugin"));
    plugins.push(require("plugins/leosXmlEntities/leosXmlEntitiesPlugin"));
    plugins.push(require("plugins/leosTextCaseChanger/leosTextCaseChangerPlugin"));
    plugins.push(require("plugins/leosSpecialChar/leosSpecialCharPlugin"));
    plugins.push(require("plugins/leosPreventElementDeletion/leosPreventElementDeletionPlugin"));
    plugins.push(require("plugins/leosElementSplitHandler/leosElementSplitHandlerPlugin"));
    plugins.push(require("plugins/leosElementMergeHandler/leosElementMergeHandlerPlugin"));
    plugins.push(require("plugins/leosSpellChecker/leosSpellCheckerPlugin"));
    plugins.push(require("plugins/leosPreventSelectAll/leosPreventSelectAllPlugin"));

    var pluginNames=[];
    var specificConfig={
        addPreventElementDeletionWidgetToFirstChild : true
    };
    $.each(plugins, function( index, value ) {
        pluginNames.push(value.name);
        specificConfig= $.extend( specificConfig,  value.specificConfig);
    });
    // holds ckEditor external plugins names
    var externalPluginsNames = [];
    pluginTools.addExternalPlugins(externalPluginsNames);
    var extraPlugins = pluginNames.concat(externalPluginsNames).join(",");
    var transformationConfigResolver = transformationConfigManager.getTransformationConfigResolverForPlugins(pluginNames);
    var leosPasteFilter = pluginTools.createFilterList(transformationConfigResolver);

    var profileName = "AKN Paragraph Mandate";

    // create profile configuration
    var profileConfig = {
        // user interface language localization
        language: "en",
        // custom configuration to load (none if empty)
        customConfig: "",
        // comma-separated list of plugins to be loaded
        plugins: "toolbar,wysiwygarea,elementspath,clipboard,undo,pastefromword,enterkey,button,dialog,dialogui,"
        + "widget,lineutils,basicstyles," + "indent,"
        + "fakeobjects,specialchar,table,tableresize,tabletools,tableselection,contextmenu,menubutton,mathjax,pastetext",
        // comma-separated list of plugins that must not be loaded
        removePlugins: "",
        // comma-separated list of additional plugins to be loaded
        extraPlugins: extraPlugins,
        // disable Advanced Content Filter (allow all content)
        allowedContent: true,
        //only allow elements configured in transformer while paste
        pasteFilter:leosPasteFilter,
        defaultPasteElement:'text',
        // force Paste as plain text
        forcePasteAsPlainText: false,
        //Use native spellchecker
        disableNativeSpellChecker: false,
        // toolbar groups arrangement, optimized for a single toolbar row
        toolbar : [
        	{ name: 'save', items: [ 'leosInlineSave' , 'leosInlineSaveClose', 'leosInlineCancel' ] },
        	{ name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', '-', 'Undo', 'Redo' ] },
        	{ name: 'editing', items: [ 'Scayt' ] },
        	{ name: 'basicstyles', items: [ 'Bold', 'Italic', 'Subscript', 'Superscript', 'TransformTextSwitcher' ] },
        	{ name: 'ref', items: [ 'authorialNoteWidget', 'LeosCrossReference' ] },
        	{ name: 'insert', items: [ 'Mathjax', 'SpecialChar' ] },
        	{ name: 'tools', items: [ 'LeosShowBlocks' ] },
        	'/',
        	{ name: 'splitmerge', items: [ 'leosHierarchicalElementShiftEnterHandler', 'leosElementMerge', 'Table' ] },
        	{ name : "mode" , items: [ 'Sourcedialog' ] }
    	],
        //show toolbar on startup
        startupFocus: 'end',
        // comma-separated list of toolbar button names that must not be rendered
        removeButtons: "Underline,Strike,Anchor,TextColor,PasteFromWord,PasteText",
        // semicolon-separated list of dialog elements that must not be rendered
        // element is a string concatenation of dialog name + colon + tab name
        removeDialogTabs: "",
        // height of the editing area
        height: 515,
        //MathJax plugin configuration - Sets the path to the MathJax library
        mathJaxLib: './webjars/MathJax/2.7.0/MathJax.js?config=default',
        // LEOS-2887 removing tooltip title
        title: false
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
