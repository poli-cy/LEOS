'use strict';

var serviceConfig = require('./service-config');

/**
 * Function that returns apiUrl from the settings object.
 *
 * @param {object} settings - The settings object
 * @returns {string} The apiUrl from the service or the default apiUrl from the settings
 * @throws {Error} If the settings has a service but the service doesn't have an apiUrl
 *
 */
function getApiUrl(settings) {
  var service = serviceConfig(settings);

  if (service) {
    // If the host page contains a service setting then the client should default to
    // using that apiUrl.
    if (service.apiUrl) {
      return service.apiUrl;
    } else {
      throw new Error('Service should contain an apiUrl value.');
    }
  }
  return settings.apiUrl;
}

function getWSApiUrl(settings) {
    var service = serviceConfig(settings);

    if (service) {
        // If the host page contains a service setting then the client should default to
        // using that websocketUrl.
        if (service.websocketUrl) {
            return service.websocketUrl;
        } else {
            throw new Error('Service should contain an websocketUrl value.');
        }
    }
    return settings.websocketUrl;
}
module.exports = {
  getApiUrl:getApiUrl,
  getWSApiUrl:getWSApiUrl
};
