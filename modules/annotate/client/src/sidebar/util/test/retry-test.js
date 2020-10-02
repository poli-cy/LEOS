'use strict';

var retryUtil = require('../retry');
var toResult = require('../../../shared/test/promise-util').toResult;

describe('sidebar.util.retry', function () {
  describe('.retryPromiseOperation', function () {
    it('should return the result of the operation function', function () {
      var operation = sinon.stub().returns(Promise.resolve(42));
      var wrappedOperation = retryUtil.retryPromiseOperation(operation);
      return wrappedOperation.then(function (result) {
        assert.equal(result, 42);
      });
    });

    it('should retry the operation if it fails', function () {
      var results = [new Error('fail'), 'ok'];
      var operation = sinon.spy(function () {
        var nextResult = results.shift();
        if (nextResult instanceof Error) {
          return Promise.reject(nextResult);
        } else {
          return Promise.resolve(nextResult);
        }
      });
      var wrappedOperation = retryUtil.retryPromiseOperation(operation, {
        minTimeout: 1,
      });
      return wrappedOperation.then(function (result) {
        assert.equal(result, 'ok');
      });
    });

    it('should return the error if it repeatedly fails', function () {
      var error = new Error('error');
      var operation = sinon.spy(function () {
        return Promise.reject(error);
      });
      var wrappedOperation = retryUtil.retryPromiseOperation(operation, {
        minTimeout: 3,
        retries: 2,
      });
      return toResult(wrappedOperation).then(function (result) {
        assert.equal(result.error, error);
      });
    });
  });
});
