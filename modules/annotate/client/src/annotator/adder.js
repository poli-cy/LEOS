'use strict';

var classnames = require('classnames');

var template = require('./adder.html');

var ANNOTATE_BTN_CLASS = 'js-annotate-btn';
var ANNOTATE_BTN_SELECTOR = '.js-annotate-btn';

var HIGHLIGHT_BTN_SELECTOR = '.js-highlight-btn';

/**
 * @typedef Target
 * @prop {number} left - Offset from left edge of viewport.
 * @prop {number} top - Offset from top edge of viewport.
 * @prop {number} arrowDirection - Direction of the adder's arrow.
 */

/**
 * Show the adder above the selection with an arrow pointing down at the
 * selected text.
 */
var ARROW_POINTING_DOWN = 1;

/**
 * Show the adder above the selection with an arrow pointing up at the
 * selected text.
 */
var ARROW_POINTING_UP = 2;

function toPx(pixels) {
  return pixels.toString() + 'px';
}

var ARROW_HEIGHT = 10;

// The preferred gap between the end of the text selection and the adder's
// arrow position.
var ARROW_H_MARGIN = 20;

function attachShadow(element) {
  if (element.attachShadow) {
    // Shadow DOM v1 (Chrome v53, Safari 10)
    return element.attachShadow({mode: 'open'});
  } else if (element.createShadowRoot) {
    // Shadow DOM v0 (Chrome ~35-52)
    return element.createShadowRoot();
  } else {
    return null;
  }
}

/**
 * Return the closest ancestor of `el` which has been positioned.
 *
 * If no ancestor has been positioned, returns the root element.
 *
 * @param {Element} el
 * @return {Element}
 */
function nearestPositionedAncestor(el) {
  var parentEl = el.parentElement;
  while (parentEl.parentElement) {
    if (getComputedStyle(parentEl).position !== 'static') {
      break;
    }
    parentEl = parentEl.parentElement;
  }
  return parentEl;
}

/**
 * LEOS Change added this function
 * Checks if an element is scrollable.
 * See https://gist.github.com/jeffturcotte/1100144.
 */
function isScrollable(el) {
  return (el.scrollWidth > el.clientWidth) || (el.scrollHeight > el.clientHeight);
}

/**
 * Create the DOM structure for the Adder.
 *
 * Returns the root DOM node for the adder, which may be in a shadow tree.
 */
function createAdderDOM(container) {
  var element;

  // If the browser supports Shadow DOM, use it to isolate the adder
  // from the page's CSS
  //
  // See https://developers.google.com/web/fundamentals/primers/shadowdom/
  var shadowRoot = attachShadow(container);
  if (shadowRoot) {
    shadowRoot.innerHTML = template;
    element = shadowRoot.querySelector('.js-adder');

    // Load stylesheets required by adder into shadow DOM element
    var adderStyles = Array.from(document.styleSheets).map(function (sheet) {
      return sheet.href;
    }).filter(function (url) {
      return (url || '').match(/(icomoon|annotator)\.css/);
    });

    // Stylesheet <link> elements are inert inside shadow roots [1]. Until
    // Shadow DOM implementations support external stylesheets [2], grab the
    // relevant CSS files from the current page and `@import` them.
    //
    // [1] http://stackoverflow.com/questions/27746590
    // [2] https://github.com/w3c/webcomponents/issues/530
    //
    // This will unfortunately break if the page blocks inline stylesheets via
    // CSP, but that appears to be rare and if this happens, the user will still
    // get a usable adder, albeit one that uses browser default styles for the
    // toolbar.
    var styleEl = document.createElement('style');
    styleEl.textContent = adderStyles.map(function (url) {
      return '@import "' + url + '";';
    }).join('\n');
    shadowRoot.appendChild(styleEl);
  } else {
    container.innerHTML = template;
    element = container.querySelector('.js-adder');
  }
  return element;
}

/**
 * Annotation 'adder' toolbar which appears next to the selection
 * and provides controls for the user to create new annotations.
 */
class Adder {
  /**
   * Construct the toolbar and populate the UI.
   *
   * The adder is initially hidden.
   *
   * @param {Element} container - The DOM element into which the adder will be created
   * @param {Object} options - Options object specifying `onAnnotate` and `onHighlight`
   *        event handlers.
   */
  constructor(container, options) {
    this.element = createAdderDOM(container);
    this._container = container;

    // Set initial style
    Object.assign(container.style, {
      display: 'block',

      // take position out of layout flow initially
      position: 'absolute',
      top: 0,

      // Assign a high Z-index so that the adder shows above any content on the
      // page
      zIndex: 999,
    });

    // The adder is hidden using the `visibility` property rather than `display`
    // so that we can compute its size in order to position it before display.
    this.element.style.visibility = 'hidden';

    this._view = this.element.ownerDocument.defaultView;
    this._enterTimeout = null;

    var handleCommand = (event) => {
      event.preventDefault();
      event.stopPropagation();

      var isAnnotateCommand = event.target.classList.contains(ANNOTATE_BTN_CLASS);

      if (isAnnotateCommand) {
        options.onAnnotate();
      } else {
        options.onHighlight();
      }

      this.hide();
    };

    this.element.querySelector(ANNOTATE_BTN_SELECTOR)
      .addEventListener('click', handleCommand);
    this.element.querySelector(HIGHLIGHT_BTN_SELECTOR)
      .addEventListener('click', handleCommand);

    this._width = () => this.element.getBoundingClientRect().width;
    this._height = () => this.element.getBoundingClientRect().height;
  }

  /** Hide the adder */
  hide() {
    clearTimeout(this._enterTimeout);
    this.element.className = classnames({'annotator-adder': true});
    this.element.style.visibility = 'hidden';
  }

  /**
   * Return the best position to show the adder in order to target the
   * selected text in `targetRect`.
   *
   * @param {Rect} targetRect - The rect of text to target, in viewport
   *        coordinates.
   * @param {boolean} isSelectionBackwards - True if the selection was made
   *        backwards, such that the focus point is mosty likely at the top-left
   *        edge of `targetRect`.
   * @return {Target}
   */
  target(targetRect, isSelectionBackwards) {
    // Set the initial arrow direction based on whether the selection was made
    // forwards/upwards or downwards/backwards.
    var arrowDirection;
    if (isSelectionBackwards) {
      arrowDirection = ARROW_POINTING_DOWN;
    } else {
      arrowDirection = ARROW_POINTING_UP;
    }
    var top;
    var left;

    // Position the adder such that the arrow it is above or below the selection
    // and close to the end.
    var hMargin = Math.min(ARROW_H_MARGIN, targetRect.width);
    if (isSelectionBackwards) {
      left = targetRect.left - this._width() / 2 + hMargin;
    } else {
      left = targetRect.left + targetRect.width - this._width() / 2 - hMargin;
    }

    // Flip arrow direction if adder would appear above the top or below the
    // bottom of the viewport.
    if (targetRect.top - this._height() < 0 &&
        arrowDirection === ARROW_POINTING_DOWN) {
      arrowDirection = ARROW_POINTING_UP;
    } else if (targetRect.top + this._height() > this._view.innerHeight) {
      arrowDirection = ARROW_POINTING_DOWN;
    }

    if (arrowDirection === ARROW_POINTING_UP) {
      top = targetRect.top + targetRect.height + ARROW_HEIGHT;
    } else {
      top = targetRect.top - this._height() - ARROW_HEIGHT;
    }

    // Constrain the adder to the viewport.
    left = Math.max(left, 0);
    left = Math.min(left, this._view.innerWidth - this._width());

    top = Math.max(top, 0);
    top = Math.min(top, this._view.innerHeight - this._height());

    return {top, left, arrowDirection};
  }

  /**
   * Show the adder at the given position and with the arrow pointing in
   * `arrowDirection`.
   *
   * @param {number} left - Horizontal offset from left edge of viewport.
   * @param {number} top - Vertical offset from top edge of viewport.
   */
  showAt(left, top, arrowDirection) {
    this.element.className = classnames({
      'annotator-adder': true,
      'annotator-adder--arrow-down': arrowDirection === ARROW_POINTING_DOWN,
      'annotator-adder--arrow-up': arrowDirection === ARROW_POINTING_UP,
    });

    // Some sites make big assumptions about interactive
    // elements on the page. Some want to hide interactive elements
    // after use. So we need to make sure the button stays displayed
    // the way it was originally displayed - without the inline styles
    // See: https://github.com/hypothesis/client/issues/137
    this.element.querySelector(ANNOTATE_BTN_SELECTOR).style.display = '';
    var highlightButton = this.element.querySelector(HIGHLIGHT_BTN_SELECTOR); // LEOS 3725 Change - Remove highlight option for ISC
    if(highlightButton) {
      highlightButton.style.display = '';
    }

    // Translate the (left, top) viewport coordinates into positions relative to
    // the adder's nearest positioned ancestor (NPA).
    //
    // Typically the adder is a child of the `<body>` and the NPA is the root
    // `<html>` element. However page styling may make the `<body>` positioned.
    // See https://github.com/hypothesis/client/issues/487.
    var positionedAncestor = nearestPositionedAncestor(this._container);
    var parentRect = positionedAncestor.getBoundingClientRect();

    //LEOS Change: added scroll position for inline containers
    //Done to place correctly the menu "Annotate Highlight" while selecting some text in a scrollable container
    //Needed this update as by default the document's body is taken as container
    var parentTop = parentRect.top;
    var parentLeft = parentRect.left;
    if (isScrollable(positionedAncestor)) {
      parentTop = parentTop - positionedAncestor.scrollTop;
      parentLeft = parentLeft - positionedAncestor.scrollLeft;
    }

    Object.assign(this._container.style, {
      top: toPx(top - parentTop),
      left: toPx(left - parentLeft),
    });
    this.element.style.visibility = 'visible';

    clearTimeout(this._enterTimeout);
    this._enterTimeout = setTimeout(() => {
      this.element.className += ' is-active';
    }, 1);
  }
}

module.exports = {
  ARROW_POINTING_DOWN: ARROW_POINTING_DOWN,
  ARROW_POINTING_UP: ARROW_POINTING_UP,

  Adder: Adder,
};
