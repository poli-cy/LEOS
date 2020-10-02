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
define(function leosCrossReferenceWidgetModule(require) {
    "use strict";

    var leosCrossReferenceWidgetDefinition = {
        name: "leosCrossReferenceWidget",
        config: {
            inline: true,
            requires: "leosWidgetPlugin",
            allowedContent: "mref[id,data-akn-name],ref[id,data-akn-name,href]",
            template: '<mref id="" data-akn-name="mref"></mref>',
            dialog: "leosCrossReferenceDialog",

            upcast: function upcast(element, data) {
                return (element.attributes["data-akn-name"] === "mref");
            },
        }
    };

    return leosCrossReferenceWidgetDefinition;
});