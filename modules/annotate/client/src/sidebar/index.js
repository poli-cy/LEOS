'use strict';

var addAnalytics = require('./ga');
var disableOpenerForExternalLinks = require('./util/disable-opener-for-external-links');
var apiUrls = require('./get-api-url');
var serviceConfig = require('./service-config');
var crossOriginRPC = require('./cross-origin-rpc.js');
require('../shared/polyfills');

var raven;

// Read settings rendered into sidebar app HTML by service/extension.
var settings = require('../shared/settings').jsonConfigsFrom(document);

if (settings.raven) {
  // Initialize Raven. This is required at the top of this file
  // so that it happens early in the app's startup flow
  raven = require('./raven');
  raven.init(settings.raven);
}

var hostPageConfig = require('./host-config');
Object.assign(settings, hostPageConfig(window));

settings.apiUrl = apiUrls.getApiUrl(settings);
settings.websocketUrl = apiUrls.getWSApiUrl(settings);

var isLeosDocument = settings.docType && (settings.docType == 'LeosDocument'); // LEOS Change

// Disable Angular features that are not compatible with CSP.
//
// See https://docs.angularjs.org/api/ng/directive/ngCsp
//
// The `ng-csp` attribute must be set on some HTML element in the document
// _before_ Angular is require'd for the first time.
document.body.setAttribute('ng-csp', '');

// Prevent tab-jacking.
disableOpenerForExternalLinks(document.body);

var angular = require('angular');

// autofill-event relies on the existence of window.angular so
// it must be require'd after angular is first require'd
require('autofill-event');

// Setup Angular integration for Raven
if (settings.raven) {
  raven.angularModule(angular);
} else {
  angular.module('ngRaven', []);
}

if(settings.googleAnalytics){
  addAnalytics(settings.googleAnalytics);
}

// Fetch external state that the app needs before it can run. This includes the
// user's profile and list of groups.
var resolve = {
  // @ngInject
  state: function (groups, session) {
    return session.load().then(function() {                 // LEOS Change load session before loading groups
      return groups.load();
    });
  }
};

var isSidebar = !(window.location.pathname.startsWith('/stream') ||
                  window.location.pathname.startsWith('/a/'));

// @ngInject
function configureLocation($locationProvider) {
  // Use HTML5 history
  return $locationProvider.html5Mode(true);
}

// @ngInject
function configureRoutes($routeProvider) {
  // The `vm.{auth,search}` properties used in these templates come from the
  // `<hypothesis-app>` component which hosts the router's container element.
  $routeProvider.when('/a/:id',
    {
      template: '<annotation-viewer-content search="vm.search"></annotation-viewer-content>',
      reloadOnSearch: false,
      resolve: resolve,
    });
  $routeProvider.when('/stream',
    {
      template: '<stream-content search="vm.search"></stream-content>',
      reloadOnSearch: false,
      resolve: resolve,
    });
  $routeProvider.otherwise({
    template: '<sidebar-content search="vm.search" auth="vm.auth"></sidebar-content>',
    reloadOnSearch: false,
    resolve: resolve,
  });
}

// @ngInject
function configureToastr(toastrConfig) {
  angular.extend(toastrConfig, {
    preventOpenDuplicates: true,
  });
}

// @ngInject
function configureCompile($compileProvider) {
  // Make component bindings available in controller constructor. When
  // pre-assigned bindings is off, as it is by default in Angular >= 1.6.0,
  // bindings are only available during and after the controller's `$onInit`
  // method.
  //
  // This migration helper is being removed in Angular 1.7.0. To see which
  // components need updating, look for uses of `preAssignBindingsEnabled` in
  // tests.
  $compileProvider.preAssignBindingsEnabled(true);
}

// @ngInject
function setupHttp($http, streamer) {
  $http.defaults.headers.common['X-Client-Id'] = streamer.clientId;
}

function processAppOpts() {
  if (settings.liveReloadServer) {
    require('./live-reload-client').connect(settings.liveReloadServer);
  }
}

module.exports = angular.module('h', [
  // Angular addons which export the Angular module name
  // via module.exports
  require('angular-route'),
  require('angular-sanitize'),
  require('angular-toastr'),

  // Angular addons which do not export the Angular module
  // name via module.exports
  ['angulartics', require('angulartics')][0],
  ['angulartics.google.analytics', require('angulartics/src/angulartics-ga')][0],
  ['ngTagsInput', require('ng-tags-input')][0],
  ['ui.bootstrap', require('./vendor/ui-bootstrap-custom-tpls-0.13.4')][0],

  // Local addons
  'ngRaven',

  //Angular multiple select LEOS Change
  ['multipleSelect', require('angular-multiple-select/build/multiple-select.min.js')][0]
])

  // The root component for the application
  .component('hypothesisApp', require('./components/hypothesis-app'))

  // UI components
  .component('annotation', isLeosDocument ? require('../../leos/sidebar/components/leos-annotation') : require('./components/annotation')) //LEOS Change
  .component('annotationHeader', require('./components/annotation-header'))
  .component('annotationActionButton', require('./components/annotation-action-button'))
  .component('annotationShareDialog', require('./components/annotation-share-dialog'))
  .component('annotationThread', require('./components/annotation-thread'))
  .component('annotationViewerContent', require('./components/annotation-viewer-content'))
  .component('dropdownMenuBtn', require('./components/dropdown-menu-btn'))
  .component('excerpt', require('./components/excerpt'))
  .component('groupList', isLeosDocument ? require('../../leos/sidebar/components/leos-group-list') : require('./components/group-list')) // LEOS Change
  .component('helpLink', require('./components/help-link'))
  .component('helpPanel', require('./components/help-panel'))
  .component('loggedoutMessage', require('./components/loggedout-message'))
  .component('loginControl', require('./components/login-control'))
  .component('markdown', isLeosDocument ? require('../../leos/sidebar/components/leos-markdown') : require('./components/markdown')) // LEOS Change
  .component('moderationBanner', require('./components/moderation-banner'))
  .component('newNoteBtn', require('./components/new-note-btn'))
  .component('publishAnnotationBtn', require('./components/publish-annotation-btn'))
  .component('searchInput', require('./components/search-input'))
  .component('searchStatusBar', require('./components/search-status-bar'))
  .component('selectionTabs', require('./components/selection-tabs'))
  .component('sidebarContent', require('./components/sidebar-content'))
  .component('sidebarTutorial', require('./components/sidebar-tutorial'))
  .component('shareDialog', require('./components/share-dialog'))
  .component('sortDropdown', require('./components/sort-dropdown'))
  .component('streamContent', require('./components/stream-content'))
  .component('svgIcon', require('./components/svg-icon'))
  .component('tagEditor', require('./components/tag-editor'))
  .component('threadList', require('./components/thread-list'))
  .component('timestamp', require('./components/timestamp'))
  .component('leosFilterButton', require('../../leos/sidebar/components/leos-filter-button')) // LEOS Change
  .component('leosFilterPane', require('../../leos/sidebar/components/leos-filter-pane')) // LEOS Change
  .component('topBar', require('./components/top-bar'))
  .component('leosSuggestionButtons', require('../../leos/sidebar/components/leos-suggestion-buttons')) // LEOS Change
  .component('leosAnnotationHeader', require('../../leos/sidebar/components/leos-annotation-header')) // LEOS Change
  .component('leosPublishAnnotationBtn', require('../../leos/sidebar/components/leos-publish-annotation-btn')) // LEOS Change

  .directive('hAutofocus', require('./directive/h-autofocus'))
  .directive('hBranding', require('./directive/h-branding'))
  .directive('hOnTouch', require('./directive/h-on-touch'))
  .directive('hTooltip', require('./directive/h-tooltip'))
  .directive('spinner', require('./directive/spinner'))
  .directive('windowScroll', require('./directive/window-scroll'))

  .service('analytics', require('./services/analytics'))
  .service('annotationMapper', require('./services/annotation-mapper'))
  .service('api', isLeosDocument ? require('../../leos/sidebar/factories/leos-api') : require('./services/api'))
  .service('apiRoutes', require('./services/api-routes'))
  .service('auth', require('./services/oauth-auth'))
  .service('bridge', require('../shared/bridge'))
  .service('drafts', require('./services/drafts'))
  .service('features', require('./services/features'))
  .service('flash', require('./services/flash'))
  .service('frameSync', isLeosDocument ? require('../../leos/sidebar/leos-frame-sync').default : require('./services/frame-sync').default)
  .service('groups', require('./services/groups'))
  .service('localStorage', require('./services/local-storage'))
  .service('permissions', require('./services/permissions'))
  .service('queryParser', require('./services/query-parser'))
  .service('rootThread', require('./services/root-thread'))
  .service('searchFilter', require('./services/search-filter'))
  .service('serviceUrl', require('./services/service-url'))
  .service('session', require('./services/session'))
  .service('streamer', require('./services/streamer'))
  .service('streamFilter', require('./services/stream-filter'))
  .service('tags', require('./services/tags'))
  .service('unicode', require('./services/unicode'))
  .service('viewFilter', require('./services/view-filter'))

  // Redux store
  .service('store', require('./store'))

  // Utilities
  .value('Discovery', require('../shared/discovery'))
  .value('ExcerptOverflowMonitor', require('./util/excerpt-overflow-monitor'))
  .value('OAuthClient', require('./util/oauth-client'))
  .value('VirtualThreadList', require('./virtual-thread-list'))
  .value('isSidebar', isSidebar)
  .value('random', require('./util/random'))
  .value('raven', require('./raven'))
  .value('serviceConfig', serviceConfig)
  .value('settings', settings)
  .value('time', require('./util/time'))
  .value('urlEncodeFilter', require('./filter/url').encode)

  .config(configureCompile)
  .config(configureLocation)
  .config(configureRoutes)
  .config(configureToastr)
  .config(['$provide', '$injector', isLeosDocument ? require('../../leos/sidebar/decorators/leos-oauth-auth') : () => void 0]) //LEOS Change
  .config(['$provide', '$injector', isLeosDocument ? require('../../leos/sidebar/decorators/leos-permissions') : () => void 0]) //LEOS Change
  .run(setupHttp)
  .run(crossOriginRPC.server.start);

processAppOpts();

// Work around a check in Angular's $sniffer service that causes it to
// incorrectly determine that Firefox extensions are Chrome Packaged Apps which
// do not support the HTML 5 History API. This results Angular redirecting the
// browser on startup and thus the app fails to load.
// See https://github.com/angular/angular.js/blob/a03b75c6a812fcc2f616fc05c0f1710e03fca8e9/src/ng/sniffer.js#L30
if (window.chrome && !window.chrome.app) {
  window.chrome.app = {
    dummyAddedByHypothesisClient: true,
  };
}

var appEl = document.querySelector('hypothesis-app');
angular.bootstrap(appEl, ['h'], {strictDi: true});
