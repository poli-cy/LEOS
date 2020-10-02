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
define(function leosTableTransformer(require) {
    'use strict';

    var CKEDITOR = require("promise!ckEditor");
    var STAMPIT = require('stampit');

    // Adds start and end line anchors to regular expressions
    function _anchor(content) {
        return '^' + content + '$';
    };

    // Parse all the children of a cell (td, th or caption) and map it into the HTML table cell
    function _createContentChildrenAknToHtml(element, rootPath, childMp) {
        var that = this;
        var children = element.children;

        children.forEach(function(childElement) {
            var childElementName = that._getElementName(childElement);

            if (childElementName === 'text' && !childMp) {
                // If there is a text elements, they should be included in an Akn p element
                that.mapToChildProducts(element, {
                    toPath: rootPath,
                    toChild: 'text',
                    toChildTextValue: childElement.value
                });
            } else {                 
                // Not possible to have a 'text' element in an Akn cell
                that.mapToNestedChildProduct(childElement, {
                    toPath: rootPath
                });
            }
        });
    }

    // Checks if an element is a text element or an inline element
    function _isTextOrInlineElement(element) {
        var that = this;
        var elementName = that._getElementName(element);
        return ((elementName === 'text') || CKEDITOR.dtd.$inline.hasOwnProperty(elementName));
    }

    // Parse all the children of a cell (td,th or caption) and map it directly to the Akn cell if these are NOT inline elements
    // All other inline elements should be mapped into Akn p elements
    function _createContentChildrenFromHtmlToAkn(element, rootPath, childMp) {
        var that = this;
        var children = element.children;
        var toChild ='';
        if(childMp) {
            toChild='/mp';
        }
        // If they are text or inline elements, they should be included in an Akn p element, except it chilMp is false
        // This p element should be added only once
        if (childMp && children.some(_isTextOrInlineElement, that)) {
            that.mapToChildProducts(element, {
                toPath : rootPath,
                toChild : 'mp'
            });
        }

        children.forEach(function(childElement) {
            var childElementName = that._getElementName(childElement);

            // If there are text elements, they should be included in an Akn p element, except it chilMp is false
            if (childElementName === 'text') {
                that.mapToChildProducts(element, {
                    toPath: rootPath + toChild,
                    toChild: 'text',
                    toChildTextValue: childElement.value
                });
            // If they are inline elements, they should be included in an Akn p element, except it chilMp is false
            } else if (CKEDITOR.dtd.$inline.hasOwnProperty(childElementName)) {
                that.mapToNestedChildProduct(childElement, {
                    toPath: rootPath + toChild
                });
            // Other elements are put in the akn cell
            } else {
                that.mapToNestedChildProduct(childElement, {
                    toPath: rootPath
                });
            }
        });
    }

    // This function transforms attributes' objects given from the plugin config to correct object formats to feed the transformer
    function _convertAttrs(attrs) {
        var direction = this._.direction;
        var newAttrs = [];
        for (var attr in attrs) {
            if (attrs.hasOwnProperty(attr)) {
                var newAttr = {};
                if (attrs[attr].akn) {
                    if(direction=="from") {
                        newAttr.to = attrs[attr].akn;
                    }else{
                        newAttr.from = attrs[attr].akn;
                    }
                }
                if (attrs[attr].html) {
                    if(direction=="from") {
                        newAttr.from = attrs[attr].html;
                    }else {
                        newAttr.to = attrs[attr].html;
                    }
                }
                newAttr.action = 'passAttributeTransformer';
                newAttrs.push(newAttr);
            }
        }
        return newAttrs;
    }

    // Checks if the transformation configuration is correctly defined from the plugin
    function _checkTranformationConfig(tableTransformationConfig) {
        if ((!tableTransformationConfig) || (!tableTransformationConfig.akn) || (!tableTransformationConfig.html)) {
            return false;
        }
        else if ((!tableTransformationConfig.sub) || (!tableTransformationConfig.sub.akn) || (!tableTransformationConfig.sub.html)) {
            return false;
        }
        else if ((!tableTransformationConfig.sub.sub) 
                || (!tableTransformationConfig.sub.sub.akn) || (!tableTransformationConfig.sub.sub.akn.body) || (!tableTransformationConfig.sub.sub.akn.head)
                || (!tableTransformationConfig.sub.sub.html) || (!tableTransformationConfig.sub.sub.html.body) || (!tableTransformationConfig.sub.sub.html.head)) {
            return false;
        }
        else {
            return true;
        }
    }

    var leosTableTransformerStamp = STAMPIT().enclose(
        // Executed on the instance creation
        function init() {
            if (!_checkTranformationConfig(this.tableTransformationConfig)) {
                throw new Error('Table Tranformation Configuration not correctly defined');
            }

            var aknTableTag = this.tableTransformationConfig.akn;
            var htmlTableTag = this.tableTransformationConfig.html;
            var attrsTableConfig = this.tableTransformationConfig.attr;
            var rowTransformationConfig = this.tableTransformationConfig.sub;
            var cellTransformationConfig = this.tableTransformationConfig.sub.sub;

            var PSR = '\/';

            // Reg exps to match path /table
            var TABLE_MATCH_TO = new RegExp(_anchor(aknTableTag),'i');
            var TABLE_MATCH_FROM = new RegExp(_anchor(htmlTableTag),'i');

            // Reg exps to match path /table/caption
            var CAPTION_MATCH_TO = new RegExp(_anchor([aknTableTag,'caption'].join(PSR)),'i');
            var CAPTION_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'caption'].join(PSR)),'i');
    
            // Reg exps to match path /table/tbody
            var TBODY_MATCH_TO = new RegExp(_anchor([aknTableTag,'tbody'].join(PSR)),'i');

            // Reg exps to match paths /table/tr for AKN and /table/thead/tr or /table/tbody/tr for HTML
            // REMARK: Added tbody in the AKN reg exp because browsers add by default tbody in the DOM
            var TR_MATCH_TO = new RegExp(_anchor([aknTableTag,'(\/tbody)?\/',rowTransformationConfig.akn].join('')),'i');
            var TR_BODY_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'tbody',rowTransformationConfig.html].join(PSR)),'i');
            var TR_HEAD_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'thead',rowTransformationConfig.html].join(PSR)),'i');

            // Reg exps to match paths /table/tr/th for AKN and /table/thead/tr/th or /table/tbody/tr/th for HTML
            // REMARK: Added tbody in the AKN reg exp because browsers add by default tbody in the DOM
            var TH_MATCH_TO = new RegExp(_anchor([aknTableTag,'(\/tbody)?\/',rowTransformationConfig.akn,'\/',cellTransformationConfig.akn.head].join('')),'i');
            var TH_BODY_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'tbody',rowTransformationConfig.html,cellTransformationConfig.html.head].join(PSR)),'i');
            var TH_HEAD_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'thead',rowTransformationConfig.html,cellTransformationConfig.html.head].join(PSR)),'i');

            // Reg exps to match paths /table/tr/td for AKN and /table/tbody/tr/td for HTML
            // REMARK: Added tbody in the AKN reg exp because browsers add by default tbody in the DOM
            var TD_MATCH_TO = new RegExp(_anchor([aknTableTag,'(\/tbody)?\/',rowTransformationConfig.akn,'\/',cellTransformationConfig.akn.body].join('')),'i');
            var TD_MATCH_FROM = new RegExp(_anchor([htmlTableTag,'tbody',rowTransformationConfig.html,cellTransformationConfig.html.body].join(PSR)),'i');

            // Is element part of the heading row. If true, to be placed in the HTML thead tag
            // To be part of the heading rows, a row should contain only 'th' tags
            function _isHeadRow(element, pathToBeMatched) {
                var that = this;
                // Reg exps to match th element or children of this element
                var regExpHeader = new RegExp(_anchor(['.*',rowTransformationConfig.akn,cellTransformationConfig.akn.head + '.*'].join(PSR)),'i');

                if (that._getElementName(element) == rowTransformationConfig.akn) { 
                    if (element.find(cellTransformationConfig.akn.body).length > 0) { //Tries to find 'td' tag in a 'tr' tag, if true: not a heading row
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else if (pathToBeMatched.match(regExpHeader) && (element.parent)) { // Checks to be made on the parent element until we get the 'tr' element
                    return _isHeadRow.call(that, element.parent, pathToBeMatched);
                }
                else {
                    return false;
                }
            }

            var _transformationConfig = {
                akn: aknTableTag,
                html: htmlTableTag,
                attr: attrsTableConfig,
                transformer: {
                    from: {     //From HTML to Akn
                        action: function action(element) {
                            var path = element.transformationContext.elementPath;

                            //From /table to /table
                            if (TABLE_MATCH_FROM.test(path)) {
                                var targetPath = aknTableTag; // table
                                this.mapToProducts(element, {
                                    toPath: aknTableTag,
                                    attrs: _convertAttrs.call(this, attrsTableConfig)
                                });
                            // From /table/caption to /table/caption
                            } else if (CAPTION_MATCH_FROM.test(path)) {
                                var targetPath = aknTableTag + '/caption'; // table/caption
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: [{
                                        from: 'id',
                                        to: 'xml:id',
                                        action: 'passAttributeTransformer'
                                    },{
                                        from: 'data-origin',
                                        to: 'leos:origin',
                                        action: 'passAttributeTransformer'
                                    }]
                                });
                                //Matches all content elements of caption to /table/caption
                                _createContentChildrenFromHtmlToAkn.call(this, element, targetPath, false);
                            // From /table/tbody/tr or /table/thead/tr to /table/tr
                            } else if ((TR_BODY_MATCH_FROM.test(path)) || (TR_HEAD_MATCH_FROM.test(path))) { 
                                var targetPath = aknTableTag + '/' + rowTransformationConfig.akn; // table/tr
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, rowTransformationConfig.attr)
                                });
                            // From /table/tbody/tr/th or /table/thead/tr/th to /table/tr/th
                            } else if ((TH_BODY_MATCH_FROM.test(path)) || (TH_HEAD_MATCH_FROM.test(path))) {
                                var targetPath = aknTableTag + '/' + rowTransformationConfig.akn + '/' + cellTransformationConfig.akn.head; // table/tr/th
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, cellTransformationConfig.attr)
                                });
                                //Matches all content elements of th to td to /table/tr/th or /table/tr/th/mp if these are inline elements
                                _createContentChildrenFromHtmlToAkn.call(this, element, targetPath, true);
                            // From /table/tbody/tr/td to /table/tr/td
                            } else if (TD_MATCH_FROM.test(path)) {
                                var targetPath = aknTableTag + '/' + rowTransformationConfig.akn + '/' + cellTransformationConfig.akn.body; // table/tr/td
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, cellTransformationConfig.attr)
                                });
                                //Matches all content elements of td to /table/tr/td or /table/tr/td/mp if these are inline elements
                                _createContentChildrenFromHtmlToAkn.call(this, element, targetPath, true);
                            }
                        }
                    },
                    to: {          //From Akn to HTML
                        action: function(element) {
                            var path = element.transformationContext.elementPath;
                            var target = 'tbody';
                            if  (_isHeadRow.call(this, element, path)) {
                                target = 'thead';
                            }

                            //From table to /table/thead and /table/tbody
                            if (TABLE_MATCH_TO.test(path)) {
                                var targetPath = htmlTableTag; // table
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, attrsTableConfig)
                                });
                            } else if (TBODY_MATCH_TO.test(path)) {
                                // Create structure of table /table/thead, /table/tbody, table/caption is optional
                                var targetPath = htmlTableTag;
                                this.mapToChildProducts(element, {
                                    toPath : targetPath,
                                    toChild : 'thead',
                                });
                                this.mapToChildProducts(element, {
                                    toPath : targetPath,
                                    toChild : 'tbody',
                                });
                            // From /table/caption to /table/caption
                            } else if (CAPTION_MATCH_TO.test(path)) {
                                var targetPath = htmlTableTag + '/caption';
                                this.mapToChildProducts(element, {
                                    toPath : htmlTableTag,
                                    toChild : 'caption',
                                });
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: [{
                                        from: 'xml:id',
                                        to: 'id',
                                        action: 'passAttributeTransformer'
                                    },{
                                        from: 'leos:origin',
                                        to: 'data-origin',
                                        action: 'passAttributeTransformer'
                                    }]
                                });
                                //Matches all content elements of caption to /table/caption or to /table/caption/mp if these are inline elements
                                _createContentChildrenAknToHtml.call(this, element, htmlTableTag + '/caption', false);
                            // From /table/tr to /table/thead/tr or /table/tbody/tr
                            } else if(TR_MATCH_TO.test(path)) {
                                var targetPath = htmlTableTag + '/' + target + '/' + rowTransformationConfig.html; // table/thead/tr or table/tbody/tr
                                this.mapToProducts(element, {
                                   toPath: targetPath,
                                   attrs: _convertAttrs.call(this, rowTransformationConfig.attr)
                                });
                            // From /table/tr/td to /table/tbody/tr/td
                            } else if (TD_MATCH_TO.test(path)) {
                                var targetPath = htmlTableTag + '/' + target + '/' + rowTransformationConfig.html + '/' + cellTransformationConfig.html.body; // table/tbody/tr/td
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, cellTransformationConfig.attr)
                                });
                                //Matches all content elements of td to /table/tbody/tr/td or to /table/tbody/tr/td/mp if these are inline elements
                                _createContentChildrenAknToHtml.call(this, element, targetPath, true);
                            // From /table/tr/th to /table/thead/th or /table/tbody/th
                            } else if (TH_MATCH_TO.test(path)) {
                                var targetPath = htmlTableTag + '/' + target + '/' + rowTransformationConfig.html + '/' + cellTransformationConfig.html.head; // table/thead/tr/th or table/tbody/tr/th
                                this.mapToProducts(element, {
                                    toPath: targetPath,
                                    attrs: _convertAttrs.call(this, cellTransformationConfig.attr)
                                });
                                //Matches all content elements of th to /table/(tbody|thead)/tr/th or to /table/(tbody|thead)/tr/th/mp if these are inline elements
                                _createContentChildrenAknToHtml.call(this, element, targetPath, true);
                            }
                        }
                    }
                }
            };

            this.getTransformationConfig = function() {
                return _transformationConfig;
            };
        }
    );
    return leosTableTransformerStamp;
});
