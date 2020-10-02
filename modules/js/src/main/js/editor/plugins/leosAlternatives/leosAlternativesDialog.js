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
define(function leosAlternativesDialog(require) {
    "use strict";

    var dialogDefinition = {
        dialogName: "leosAlternativesDialog"
    };
    
    dialogDefinition.initializeDialog = function initializeDialog(editor) {
        var msg = editor.lang.leosAlternatives.warningMsg;
        
        var dialogDefinition = {
            title: editor.lang.leosAlternatives.warningTitle,
            minWidth: 400,
            minHeight: 50,
            contents: [{
                id: 'tab1',
                elements: [{
                    id: "confirm",
                    type: 'hbox',
                    widths: ['100%'],
                    height: 50,
                    children: [{
                        type: 'html',
                        html: msg
                    }]
                }]
            }],
            onOk: function (event) {
                editor.fire("confirmNewAlternative");
                event.sender.hide();
            }
        };
        return dialogDefinition;
    };
    
    return dialogDefinition;
});
