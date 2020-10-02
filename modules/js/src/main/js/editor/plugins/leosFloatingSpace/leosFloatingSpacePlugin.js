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
/**
 * @fileOverview Handles the floating space toolbar of the inline editor. This is a customized plugin similar to ckeditor floatinspace plugin. The customization is done to adjust the 
 * position of floating toolbar. The 'changeMode' method updates the position of the toolbar based on the scroll position of the editor window.
 */

; // jshint ignore:line
define(function floatingSpacePluginModule(require) {
    "use strict";

    // load module dependencies
    var CKEDITOR = require("promise!ckEditor");
    var LOG = require("logger");
    var pluginTools = require("plugins/pluginTools");

    var pluginName = "leosFloatingSpace";

    var win = CKEDITOR.document.getWindow(), pixelate = CKEDITOR.tools.cssLength;

    var pluginDefinition = {
        init: function(editor) {
            // Add listener with lower priority than that in themedui creator.
            // Thereby floatingspace will be created only if themedui wasn't used.
            editor.on('loaded', function() {
                attach(this);
            }, null, null, 20);
        }
    };

    function scrollOffset(side) {
        var pageOffset = side == 'left' ? 'pageXOffset' : 'pageYOffset',
         docScrollOffset = side == 'left' ? 'scrollLeft' : 'scrollTop';

        return (pageOffset in win.$) ? win.$[pageOffset] : CKEDITOR.document.$.documentElement[docScrollOffset];
    }

    function attach(editor) {
        var config = editor.config,

        // Get the HTML for the predefined spaces.
        topHtml = editor.fire('uiSpace', {
            space: 'top',
            html: ''
        }).html,

        // Re-positioning of the space.
        layout = (function() {
            // Mode indicates the vertical aligning mode.
            var mode, editable, spaceRect, editorRect, viewRect, spaceHeight, pageScrollX,

            // Allow minor adjustments of the float space from custom configs.
            dockedOffsetX = config.floatSpaceDockedOffsetX || 0, dockedOffsetY = config.floatSpaceDockedOffsetY || 0, pinnedOffsetX = config.floatSpacePinnedOffsetX || 0, pinnedOffsetY = config.floatSpacePinnedOffsetY || 0;

            // Update the float space position.
            function updatePos(pos, prop, val) {
                floatSpace.setStyle(prop, pixelate(val));
                floatSpace.setStyle('position', pos);
            }

            // Change the current mode and update float space position accordingly.
            function changeMode(newMode) {
                var holder = document.getElementsByClassName('leos-editing-pane')[0] || document.body;
                var editorOffset = holder.getBoundingClientRect().top;

                //Adjust the position due to change in parent element (LEOS-1990)
                switch (newMode) {
                    case 'top':
                        if ((editorRect.top - spaceHeight) > editorOffset) {
                            updatePos('fixed', 'top', editorRect.top - spaceHeight - dockedOffsetY);
                        } else {
                            updatePos('fixed', 'top', editorOffset);
                        }
                        break;
                    case 'pin':
                        updatePos('fixed', 'top', editorOffset);
                        break;
                    case 'bottom':
                        var bottomPosition = editorRect.bottom >= editorOffset ? editorOffset : editorRect.top;
                        updatePos('fixed', 'top', bottomPosition);
                        break;
                }
                mode = newMode;
            }

            return function(evt) {
                // #10112 Do not fail on editable-less editor.
                if (!(editable = editor.editable()))
                    return;

                var show = (evt && evt.name == 'focus');

                // Show up the space on focus gain.
                if (show) {
                    floatSpace.show();
                }

                editor.fire('floatingSpaceLayout', {
                    show: show
                });

                // Reset the horizontal position for below measurement.
                floatSpace.removeStyle('left');
                floatSpace.removeStyle('right');

                // Compute the screen position from the TextRectangle object would
                // be very simple, even though the "width"/"height" property is not
                // available for all, it's safe to figure that out from the rest.

                // http://help.dottoro.com/ljgupwlp.php
                spaceRect = floatSpace.getClientRect();
                editorRect = editable.getClientRect();
                viewRect = win.getViewPaneSize();
                spaceHeight = spaceRect.height;
                pageScrollX = scrollOffset('left');

                // We initialize it as pin mode.
                if (!mode) {
                    mode = 'pin';
                    changeMode('pin');
                    // Call for a refresh to the actual layout.
                    layout(evt);
                    return;
                }

                // +------------------------ Viewport -+ \
                // | | |-> floatSpaceDockedOffsetY
                // | ................................. | /
                // | |
                // | +------ Space -+ |
                // | | | |
                // | +--------------+ |
                // | +------------------ Editor -+ |
                // | | | |
                //
                if (spaceHeight + dockedOffsetY <= editorRect.top)
                    changeMode('top');

                // +- - - - - - - - - Editor -+
                // | |
                // +------------------------ Viewport -+ \
                // | | | | |-> floatSpacePinnedOffsetY
                // | ................................. | /
                // | +------ Space -+ | |
                // | | | | |
                // | +--------------+ | |
                // | | | |
                // | +---------------------------+ |
                // +-----------------------------------+
                //
                else if (spaceHeight + dockedOffsetY > viewRect.height - editorRect.bottom)
                    changeMode('pin');

                // +- - - - - - - - - Editor -+
                // | |
                // +------------------------ Viewport -+ \
                // | | | | |-> floatSpacePinnedOffsetY
                // | ................................. | /
                // | | | |
                // | | | |
                // | +---------------------------+ |
                // | +------ Space -+ |
                // | | | |
                // | +--------------+ |
                //
                else
                    changeMode('bottom');

                var mid = viewRect.width / 2, alignSide, offset;

                if (config.floatSpacePreferRight) {
                    alignSide = 'right';
                } else if (editorRect.left > 0 && editorRect.right < viewRect.width && editorRect.width > spaceRect.width) {
                    alignSide = config.contentsLangDirection == 'rtl' ? 'right' : 'left';
                } else {
                    alignSide = mid - editorRect.left > editorRect.right - mid ? 'left' : 'right';
                }

                // (#9769) If viewport width is less than space width,
                // make sure space never cross the left boundary of the viewport.
                // In other words: top-left corner of the space is always visible.
                if (spaceRect.width > viewRect.width) {
                    alignSide = 'left';
                    offset = 0;
                } else {
                    if (alignSide == 'left') {
                        // If the space rect fits into viewport, align it
                        // to the left edge of editor:
                        //
                        // +------------------------ Viewport -+
                        // | |
                        // | +------------- Space -+ |
                        // | | | |
                        // | +---------------------+ |
                        // | +------------------ Editor -+ |
                        // | | | |
                        //
                        if (editorRect.left > 0)
                            offset = editorRect.left;

                        // If the left part of the editor is cut off by the left
                        // edge of the viewport, stick the space to the viewport:
                        //
                        // +------------------------ Viewport -+
                        // | |
                        // +---------------- Space -+ |
                        // | | |
                        // +------------------------+ |
                        // +----|------------- Editor -+ |
                        // | | | |
                        //
                        else
                            offset = 0;
                    } else {
                        // If the space rect fits into viewport, align it
                        // to the right edge of editor:
                        //
                        // +------------------------ Viewport -+
                        // | |
                        // | +------------- Space -+ |
                        // | | | |
                        // | +---------------------+ |
                        // | +------------------ Editor -+ |
                        // | | | |
                        //
                        if (editorRect.right < viewRect.width)
                            offset = viewRect.width - editorRect.right;

                        // If the right part of the editor is cut off by the right
                        // edge of the viewport, stick the space to the viewport:
                        //
                        // +------------------------ Viewport -+
                        // | |
                        // | +------------- Space -+
                        // | | |
                        // | +---------------------+
                        // | +-----------------|- Editor -+
                        // | | | |
                        //
                        else
                            offset = 0;
                    }

                    // (#9769) Finally, stick the space to the opposite side of
                    // the viewport when it's cut off horizontally on the left/right
                    // side like below.
                    //
                    // This trick reveals cut off space in some edge cases and
                    // hence it improves accessibility.
                    //
                    // +------------------------ Viewport -+
                    // | |
                    // | +--------------------|-- Space -+
                    // | | | |
                    // | +--------------------|----------+
                    // | +------- Editor -+ |
                    // | | | |
                    //
                    // becomes:
                    //
                    // +------------------------ Viewport -+
                    // | |
                    // | +----------------------- Space -+
                    // | | |
                    // | +-------------------------------+
                    // | +------- Editor -+ |
                    // | | | |
                    //
                    if (offset + spaceRect.width > viewRect.width) {
                        alignSide = alignSide == 'left' ? 'right' : 'left';
                        offset = 0;
                    }
                }

                // Pin mode is fixed, so don't include scroll-x.
                // (#9903) For mode is "top" or "bottom", add opposite scroll-x for right-aligned space.
                var scroll = mode == 'pin' ? 0 : alignSide == 'left' ? pageScrollX : -pageScrollX;

                floatSpace.setStyle(alignSide, pixelate((mode == 'pin' ? pinnedOffsetX : dockedOffsetX) + offset + scroll));
            };
        })();

        if (topHtml) {
            var floatSpaceTpl = new CKEDITOR.template(
                '<div' +
                ' id="cke_{name}"' +
                ' class="cke {id} cke_reset_all cke_chrome cke_editor_{name} cke_float cke_{langDir} ' + CKEDITOR.env.cssClass + '"' +
                ' dir="{langDir}"' +
                ' title="' + ( CKEDITOR.env.gecko ? ' ' : '' ) + '"' +
                ' lang="{langCode}"' +
                ' role="application"' +
                ' style="{style}"' +
                ( editor.title ? ' aria-labelledby="cke_{name}_arialbl"' : ' ' ) +
                '>' +
                ( editor.title ? '<span id="cke_{name}_arialbl" class="cke_voice_label">{voiceLabel}</span>' : ' ' ) +
                '<div class="cke_inner">' +
                '<div id="{topId}" class="cke_top" role="presentation">{content}</div>' +
                '</div>' +
                '</div>' );

             //Appending the toolbar to editable pane instead of to the 'Body' to avoid extra spaces (LEOS - 1990)
             var holder = document.getElementsByClassName('leos-editing-pane')[0] || document.body;

             var floatSpace = CKEDITOR.dom.element.get(holder).append(CKEDITOR.dom.element.createFromHtml(floatSpaceTpl.output({
                        content: topHtml,
                        id: editor.id,
                        langDir: editor.lang.dir,
                        langCode: editor.langCode,
                        name: editor.name,
                        style: 'display:none;z-index:' + (config.baseFloatZIndex - 1),
                        topId: editor.ui.spaceId('top'),
                        voiceLabel: editor.title
                    })));

            // Use event buffers to reduce CPU load when tons of events are fired.
            var changeBuffer = CKEDITOR.tools.eventsBuffer(500, layout),
                uiBuffer = CKEDITOR.tools.eventsBuffer(100, layout);

            // There's no need for the floatSpace to be selectable.
            floatSpace.unselectable();

            // Prevent clicking on non-buttons area of the space from blurring editor.
            floatSpace.on('mousedown', function(evt) {
                evt = evt.data;
                if (!evt.getTarget().hasAscendant('a', 1))
                    evt.preventDefault();
            });

            // LEOS-2764 set 'ckevent' type on all events coming from the toolbox to avoid mixing with other events like annotation(hypothesis) events.
            var _setEventType = function(event) {
                event.hostEventType = 'ckEvent';
            }
            for (var key in floatSpace.$){
                if(key.search('on') === 0) {
                    floatSpace.$.addEventListener(key.slice(2), _setEventType)
                }
            }
            // ------------------------------------------------------------------------------------------------------------------------------------

            editor.on('focus', function(evt) {
                layout(evt);
                //on focus set the editor toolbar to edit mode.
                editor.setReadOnly(false);
                editor.on('change', changeBuffer.input);
                win.on('scroll', uiBuffer.input);
                win.on('resize', uiBuffer.input);
            });
            
            //re-position editor toolbar in case of scroll or resize
            editor.on('reposition', uiBuffer.input);

            editor.on('blur', function() {
                //Keeping the toolbar visible all time.
                //floatSpace.hide();
                editor.removeListener('change', changeBuffer.input);
                win.removeListener('scroll', uiBuffer.input);
                win.removeListener('resize', uiBuffer.input);
                //set the editor toolbar to read-only mode when focus is lost.
                editor.setReadOnly();
            });

            editor.on('destroy', function() {
                win.removeListener('scroll', uiBuffer.input);
                win.removeListener('resize', uiBuffer.input);
                floatSpace.clearCustomData();
                floatSpace.remove();
            });

            // Handle initial focus.
            if (editor.focusManager.hasFocus)
                floatSpace.show();

            // Register this UI space to the focus manager.
            editor.focusManager.add(floatSpace, 1);
        }
    }

    pluginTools.addPlugin(pluginName, pluginDefinition);

    // return plugin module
    var pluginModule = {
        name: pluginName
    };

    return pluginModule;
});

/**
 * Along with {@link #floatSpaceDockedOffsetY} it defines the
 * amount of offset (in pixels) between the float space and the editable left/right
 * boundaries when the space element is docked on either side of the editable.
 *
 *  config.floatSpaceDockedOffsetX = 10;
 *
 * @cfg {Number} [floatSpaceDockedOffsetX=0]
 * @member CKEDITOR.config
 */

/**
 * Along with {@link #floatSpaceDockedOffsetX} it defines the
 * amount of offset (in pixels) between the float space and the editable top/bottom
 * boundaries when the space element is docked on either side of the editable.
 *
 *  config.floatSpaceDockedOffsetY = 10;
 *
 * @cfg {Number} [floatSpaceDockedOffsetY=0]
 * @member CKEDITOR.config
 */

/**
 * Along with {@link #floatSpacePinnedOffsetY} it defines the
 * amount of offset (in pixels) between the float space and the viewport boundaries
 * when the space element is pinned.
 *
 *  config.floatSpacePinnedOffsetX = 20;
 *
 * @cfg {Number} [floatSpacePinnedOffsetX=0]
 * @member CKEDITOR.config
 */

/**
 * Along with {@link #floatSpacePinnedOffsetX} it defines the
 * amount of offset (in pixels) between the float space and the viewport boundaries
 * when the space element is pinned.
 *
 *  config.floatSpacePinnedOffsetY = 20;
 *
 * @cfg {Number} [floatSpacePinnedOffsetY=0]
 * @member CKEDITOR.config
 */

/**
 * Indicates that the float space should be aligned to the right side
 * of the editable area rather than to the left (if possible).
 *
 *  config.floatSpacePreferRight = true;
 *
 * @since 4.5
 * @cfg {Boolean} [floatSpacePreferRight=false]
 * @member CKEDITOR.config
 */

/**
 * Fired when the viewport or editor parameters change and the floating space needs to check and
 * eventually update its position and dimensions.
 *
 * @since 4.5
 * @event floatingSpaceLayout
 * @member CKEDITOR.editor
 * @param {CKEDITOR.editor} editor The editor instance.
 * @param data
 * @param {Boolean} data.show True if the float space should show up as a result of this event.
 */
