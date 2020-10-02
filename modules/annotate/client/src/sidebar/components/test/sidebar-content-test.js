'use strict';

var angular = require('angular');
var proxyquire = require('proxyquire');
var EventEmitter = require('tiny-emitter');

var events = require('../../events');
var noCallThru = require('../../../shared/test/util').noCallThru;

var searchClients;

//LEOS Change: setTimeout of 0.5 sec added on tests because searchMetadata promise timeout

class FakeSearchClient extends EventEmitter {
  constructor(searchFn, opts) {
    super();

    assert.ok(searchFn);
    searchClients.push(this);
    this.cancel = sinon.stub();
    this.incremental = !!opts.incremental;

    this.get = sinon.spy(function (query) {
      assert.ok(query.uri);

      for (var i = 0; i < query.uri.length; i++) {
        var uri = query.uri[i];
        this.emit('results', [{id: uri + '123', group: '__world__'}]);
        this.emit('results', [{id: uri + '456', group: 'private-group'}]);
      }

      this.emit('end');
    });
  }
}

class FakeRootThread extends EventEmitter {
  constructor() {
    super();
    this.thread = sinon.stub().returns({
      totalChildren: 0,
    });
  }
}

describe('sidebar.components.sidebar-content', function () {
  var $rootScope;
  var $scope;
  var store;
  var ctrl;
  var fakeAnalytics;
  var fakeAnnotationMapper;
  var fakeDrafts;
  var fakeFeatures;
  var fakeFrameSync;
  var fakeGroups;
  var fakeRootThread;
  var fakeSettings;
  var fakeApi;
  var fakeStreamer;
  var fakeStreamFilter;
  var sandbox;

  before(function () {
    angular.module('h', [])
      .service('store', require('../../store'))
      .component('sidebarContent', proxyquire('../sidebar-content',
        noCallThru({
          angular: angular,
          '../search-client': FakeSearchClient,
        })
      ))
      .config(($compileProvider) => $compileProvider.preAssignBindingsEnabled(true));
  });

  beforeEach(angular.mock.module('h'));

  beforeEach(angular.mock.module(function ($provide) {
    searchClients = [];
    sandbox = sinon.sandbox.create();

    fakeAnalytics = {
      track: sandbox.stub(),
      events: {},
    };

    fakeAnnotationMapper = {
      loadAnnotations: sandbox.spy(),
      unloadAnnotations: sandbox.spy(),
    };

    fakeBridge = {};

    fakeFrameSync = {
      focusAnnotations: sinon.stub(),
      scrollToAnnotation: sinon.stub(),
    };

    fakeDrafts = {
      unsaved: sandbox.stub().returns([]),
    };

    fakeFeatures = {
      flagEnabled: sandbox.stub().returns(true),
    };

    fakeStreamer = {
      setConfig: sandbox.spy(),
      connect: sandbox.spy(),
      reconnect: sandbox.spy(),
    };

    fakeStreamFilter = {
      resetFilter: sandbox.stub().returnsThis(),
      addClause: sandbox.stub().returnsThis(),
      getFilter: sandbox.stub().returns({}),
    };

    fakeGroups = {
      focused: sinon.stub().returns({id: 'foo'}),
      focus: sinon.stub(),
    };

    fakeRootThread = new FakeRootThread();

    fakeSettings = {};

    fakeApi = {
      search: sinon.stub(),
    };

    $provide.value('analytics', fakeAnalytics);
    $provide.value('annotationMapper', fakeAnnotationMapper);
    $provide.value('api', fakeApi);
    $provide.value('bridge', fakeBridge);
    $provide.value('drafts', fakeDrafts);
    $provide.value('features', fakeFeatures);
    $provide.value('frameSync', fakeFrameSync);
    $provide.value('rootThread', fakeRootThread);
    $provide.value('streamer', fakeStreamer);
    $provide.value('streamFilter', fakeStreamFilter);
    $provide.value('groups', fakeGroups);
    $provide.value('settings', fakeSettings);
  }));

  function setFrames(frames) {
    frames.forEach(function (frame) {
      store.connectFrame(frame);
    });
  }

  beforeEach(angular.mock.inject(function ($componentController, _store_, _$rootScope_) {
    $rootScope = _$rootScope_;
    $scope = $rootScope.$new();
    store = _store_;
    store.updateFrameAnnotationFetchStatus = sinon.stub();
    ctrl = $componentController('sidebarContent', { $scope: $scope }, {
      auth: { status: 'unknown' },
    });
  }));

  afterEach(function () {
    return sandbox.restore();
  });

  describe('#loadAnnotations', function () {
    it('unloads any existing annotations', function () {
      // When new clients connect, all existing annotations should be unloaded
      // before reloading annotations for each currently-connected client
      store.addAnnotations([{id: '123'}]);
      var uri1 = 'http://example.com/page-a';
      var frames = [{uri: uri1}];
      setFrames(frames);
      $scope.$digest();
      fakeAnnotationMapper.unloadAnnotations = sandbox.spy();
      var uri2 = 'http://example.com/page-b';
      frames = frames.concat({uri: uri2});
      setFrames(frames);
      $scope.$digest();
      assert.calledWith(fakeAnnotationMapper.unloadAnnotations,
        store.getState().annotations);
    });

    it('loads all annotations for a frame', function () {
      var uri = 'http://example.com';
      setFrames([{uri: uri}]);
      $scope.$digest();
      setTimeout(function(){
        var loadSpy = fakeAnnotationMapper.loadAnnotations;
        assert.calledWith(loadSpy, [sinon.match({id: uri + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uri + '456'})]);
      }, 500);
    });

    it('loads all annotations for a frame with multiple urls', function () {
      var uri = 'http://example.com/test.pdf';
      var fingerprint = 'urn:x-pdf:fingerprint';
      setFrames([{
        uri: uri,
        metadata: {
          documentFingerprint: 'fingerprint',
          link: [{
            href: fingerprint,
          },{
            href: uri,
          }],
        },
      }]);
      $scope.$digest();
      setTimeout(function(){
        var loadSpy = fakeAnnotationMapper.loadAnnotations;

        assert.calledWith(loadSpy, [sinon.match({id: uri + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: fingerprint + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uri + '456'})]);
        assert.calledWith(loadSpy, [sinon.match({id: fingerprint + '456'})]);
      }, 500);
    });

    it('loads all annotations for all frames', function () {
      var uris = ['http://example.com', 'http://foobar.com'];
      setFrames(uris.map(function (uri) {
        return {uri: uri};
      }));
      $scope.$digest();
      setTimeout(function(){
        var loadSpy = fakeAnnotationMapper.loadAnnotations;
        assert.calledWith(loadSpy, [sinon.match({id: uris[0] + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uris[0] + '456'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uris[1] + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uris[1] + '456'})]);
      }, 500);
    });

    it('updates annotation fetch status for all frames', function () {
      var frameUris = ['http://example.com', 'http://foobar.com'];
      setFrames(frameUris.map(function (frameUri) {
        return {uri: frameUri};
      }));
      $scope.$digest();
      setTimeout(function(){
        var updateSpy = store.updateFrameAnnotationFetchStatus;
        assert.isTrue(updateSpy.calledWith(frameUris[0], true));
        assert.isTrue(updateSpy.calledWith(frameUris[1], true));
      }, 500);
    });

    context('when there is a selection', function () {
      var uri = 'http://example.com';
      var id = uri + '123';

      beforeEach(function () {
        setFrames([{uri: uri}]);
        store.selectAnnotations([id]);
        $scope.$digest();
      });

      it('selectedAnnotationCount is > 0', function () {
        assert.equal(ctrl.selectedAnnotationCount(), 1);
      });

      it('switches to the selected annotation\'s group', function () {
        setTimeout(function() {
          assert.calledWith(fakeGroups.focus, '__world__');
          assert.calledOnce(fakeAnnotationMapper.loadAnnotations);
          assert.calledWith(fakeAnnotationMapper.loadAnnotations, [
            {id: uri + '123', group: '__world__'},
          ]);
        }, 500);
      });

      it('fetches annotations for all groups', function () {
        setTimeout(function() {
          assert.calledWith(searchClients[0].get, {uri: [uri], group: null});
        }, 500);
      });

      it('loads annotations in one batch', function () {
        assert.notOk(searchClients[0].incremental);
      });
    });

    context('when there is no selection', function () {
      var uri = 'http://example.com';

      beforeEach(function () {
        setFrames([{uri: uri}]);
        fakeGroups.focused.returns({ id: 'a-group' });
        $scope.$digest();
      });

      it('selectedAnnotationCount is 0', function () {
        assert.equal(ctrl.selectedAnnotationCount(), 0);
      });

      it('fetches annotations for the current group', function () {
        setTimeout(function() {
          assert.calledWith(searchClients[0].get, {uri: [uri], group: 'a-group'});
        }, 500);
      });

      it('loads annotations in batches', function () {
        assert.ok(searchClients[0].incremental);
      });
    });

    context('when the selected annotation is not available', function () {
      var uri = 'http://example.com';
      var id = uri + 'does-not-exist';

      beforeEach(function () {
        setFrames([{uri: uri}]);
        store.selectAnnotations([id]);
        fakeGroups.focused.returns({ id: 'private-group' });
        $scope.$digest();
      });

      it('loads annotations from the focused group instead', function () {
        setTimeout(function() {
          assert.calledWith(fakeGroups.focus, 'private-group');
          assert.calledWith(fakeAnnotationMapper.loadAnnotations,
            [{group: 'private-group', id: 'http://example.com456'}]);
        }, 500);
      });
    });
  });

  function connectFrameAndPerformInitialFetch() {
    setFrames([{ uri: 'https://a-page.com' }]);
    $scope.$digest();
    fakeAnnotationMapper.loadAnnotations.reset();
  }

  context('when the search URIs of connected frames change', () => {
    beforeEach(connectFrameAndPerformInitialFetch);

    it('reloads annotations', () => {
      setFrames([{ uri: 'https://new-frame.com' }]);

      $scope.$digest();

      setTimeout(function() {
        assert.called(fakeAnnotationMapper.loadAnnotations);
      }, 500);
    });
  });

  context('when the profile changes', () => {
    beforeEach(connectFrameAndPerformInitialFetch);

    it('reloads annotations if the user ID changed', () => {
      var newProfile = Object.assign({}, store.profile(), {
        userid: 'different-user@hypothes.is',
      });

      store.updateSession(newProfile);
      $scope.$digest();

      setTimeout(function() {
        assert.called(fakeAnnotationMapper.loadAnnotations);
      }, 500);
    });

    it('does not reload annotations if the user ID is the same', () => {
      var newProfile = Object.assign({}, store.profile(), {
        user_info: {
          display_name: 'New display name',
        },
      });

      store.updateSession(newProfile);
      $scope.$digest();

      setTimeout(function() {
        assert.notCalled(fakeAnnotationMapper.loadAnnotations);
      }, 500);
    });
  });

  describe('when an annotation is anchored', function () {
    it('focuses and scrolls to the annotation if already selected', function () {
      var uri = 'http://example.com';
      store.selectAnnotations(['123']);
      setFrames([{uri: uri}]);
      var annot = {
        $tag: 'atag',
        id: '123',
      };
      store.addAnnotations([annot]);
      $scope.$digest();
      $rootScope.$broadcast(events.ANNOTATIONS_SYNCED, ['atag']);
      assert.calledWith(fakeFrameSync.focusAnnotations, ['atag']);
      assert.calledWith(fakeFrameSync.scrollToAnnotation, 'atag');
    });
  });

  describe('when the focused group changes', () => {
    var uri = 'http://example.com';

    beforeEach(() => {
      // Setup an initial state with frames connected, a group focused and some
      // annotations loaded.
      store.addAnnotations([{id: '123'}]);
      store.addAnnotations = sinon.stub();
      fakeDrafts.unsaved.returns([{id: uri + '123'}, {id: uri + '456'}]);
      setFrames([{uri: uri}]);
      $scope.$digest();
    });

    function changeGroup() {
      fakeGroups.focused.returns({ id: 'different-group' });
      $scope.$digest();
    }

    it('should load annotations for the new group', () => {
      var loadSpy = fakeAnnotationMapper.loadAnnotations;

      changeGroup();

      assert.calledWith(fakeAnnotationMapper.unloadAnnotations,
        [sinon.match({id: '123'})]);
      $scope.$digest();
      setTimeout(function() {
        assert.calledWith(loadSpy, [sinon.match({id: uri + '123'})]);
        assert.calledWith(loadSpy, [sinon.match({id: uri + '456'})]);
      }, 500);
    });

    it('should clear the selection', () => {
      store.selectAnnotations(['123']);

      changeGroup();

      setTimeout(function() {
        assert.isFalse(store.hasSelectedAnnotations());
      }, 500);
    });
  });

  describe('direct linking messages', function () {

    /**
     * Connect a frame, indicating that the document has finished initial
     * loading.
     *
     * In the case of an HTML document, this usually happens immediately. For
     * PDFs, this happens once the entire PDF has been downloaded and the
     * document's metadata has been read.
     */
    function addFrame() {
      setFrames([{
        uri: 'http://www.example.com',
      }]);
    }

    beforeEach(function () {
      // There is a direct-linked annotation
      fakeSettings.annotations = 'test';
    });

    it('displays a message if the selection is unavailable', function () {
      addFrame();
      store.selectAnnotations(['missing']);
      $scope.$digest();
      setTimeout(function() {
        assert.isTrue(ctrl.selectedAnnotationUnavailable());
      }, 500);
    });

    it('does not show a message if the selection is available', function () {
      addFrame();
      store.addAnnotations([{id: '123'}]);
      store.selectAnnotations(['123']);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.selectedAnnotationUnavailable());
      }, 500);
    });

    it('does not a show a message if there is no selection', function () {
      addFrame();
      store.selectAnnotations([]);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.selectedAnnotationUnavailable());
      }, 500);
    });

    it('doesn\'t show a message if the document isn\'t loaded yet', function () {
      // No search requests have been sent yet.
      searchClients = [];
      // There is a selection but the selected annotation isn't available.
      store.selectAnnotations(['missing']);
      $scope.$digest();

      setTimeout(function() {
        assert.isFalse(ctrl.selectedAnnotationUnavailable());
      }, 500);
    });

    it('shows logged out message if selection is available', function () {
      addFrame();
      ctrl.auth = {
        status: 'logged-out',
      };
      store.addAnnotations([{id: '123'}]);
      store.selectAnnotations(['123']);
      $scope.$digest();
      setTimeout(function() {
        assert.isTrue(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });

    it('does not show loggedout message if selection is unavailable', function () {
      addFrame();
      ctrl.auth = {
        status: 'logged-out',
      };
      store.selectAnnotations(['missing']);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });

    it('does not show loggedout message if there is no selection', function () {
      addFrame();
      ctrl.auth = {
        status: 'logged-out',
      };
      store.selectAnnotations([]);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });

    it('does not show loggedout message if user is not logged out', function () {
      addFrame();
      ctrl.auth = {
        status: 'logged-in',
      };
      store.addAnnotations([{id: '123'}]);
      store.selectAnnotations(['123']);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });

    it('does not show loggedout message if not a direct link', function () {
      addFrame();
      ctrl.auth = {
        status: 'logged-out',
      };
      delete fakeSettings.annotations;
      store.addAnnotations([{id: '123'}]);
      store.selectAnnotations(['123']);
      $scope.$digest();
      setTimeout(function() {
        assert.isFalse(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });

    it('does not show loggedout message if using third-party accounts', function () {
      fakeSettings.services = [{ authority: 'publisher.com' }];
      addFrame();
      ctrl.auth = { status: 'logged-out' };
      store.addAnnotations([{id: '123'}]);
      store.selectAnnotations(['123']);
      $scope.$digest();

      setTimeout(function() {
        assert.isFalse(ctrl.shouldShowLoggedOutMessage());
      }, 500);
    });
  });

  describe('deferred websocket connection', function () {
    it('should connect the websocket the first time the sidebar opens', function () {
      $rootScope.$broadcast('sidebarOpened');
      assert.called(fakeStreamer.connect);
    });

    describe('when logged in user changes', function () {
      it('should not reconnect if the sidebar is closed', function () {
        $rootScope.$broadcast(events.USER_CHANGED);
        assert.calledOnce(fakeStreamer.reconnect);
      });

      it('should reconnect if the sidebar is open', function () {
        $rootScope.$broadcast('sidebarOpened');
        fakeStreamer.connect.reset();
        $rootScope.$broadcast(events.USER_CHANGED);
        assert.called(fakeStreamer.reconnect);
      });
    });
  });

  describe('#forceVisible', function () {
    it('shows the thread', function () {
      var thread = {id: '1'};
      ctrl.forceVisible(thread);
      assert.deepEqual(store.getState().forceVisible, {1: true});
    });

    it('uncollapses the parent', function () {
      var thread = {
        id: '2',
        parent: {id: '3'},
      };
      assert.equal(store.getState().expanded[thread.parent.id], undefined);
      ctrl.forceVisible(thread);
      assert.equal(store.getState().expanded[thread.parent.id], true);
    });
  });

  describe('#visibleCount', function () {
    it('returns the total number of visible annotations or replies', function () {
      fakeRootThread.thread.returns({
        children: [{
          id: '1',
          visible: true,
          children: [{ id: '3', visible: true, children: [] }],
        },{
          id: '2',
          visible: false,
          children: [],
        }],
      });
      $scope.$digest();
      assert.equal(ctrl.visibleCount(), 2);
    });
  });
});
