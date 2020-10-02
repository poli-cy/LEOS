'use strict';

var events = require('../events');

module.exports = {
  controllerAs: 'vm',
  //@ngInject
  controller: function ($rootScope, store) {
    this.onNewNoteBtnClick = function(){
      var topLevelFrame = store.frames().find(f=>!f.id);
      var annot = {
        target: [],
        uri: topLevelFrame.uri,
      };

      $rootScope.$broadcast(events.BEFORE_ANNOTATION_CREATED, annot);
    };
  },
  bindings: {
  },
  template: require('../templates/new-note-btn.html'),
};
