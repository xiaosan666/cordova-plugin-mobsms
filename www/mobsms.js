/*!
 * Module dependencies.
 */

var exec = cordova.require('cordova/exec');

/**
 * mobsms constructor.
 *
 * @param {Object} options to initiate Push Notifications.
 * @return {mobsms} instance that can be monitored and cancelled.
 */

var mobsms = function(options) {
    this._handlers = {
        'registration': [],
        'notification': [],
        'error': []
    };

    // require options parameter
    if (typeof options === 'undefined') {
        throw new Error('The options argument is required.');
    }

    // store the options to this object instance
    this.options = options;

    // triggered on registration and notification
    var that = this;
    var success = function(result) {
        if (result && typeof result.registrationId !== 'undefined') {
            that.emit('registration', result);
        } else if (result && result.additionalData && typeof result.additionalData.callback !== 'undefined') {
            var executeFunctionByName = function(functionName, context /*, args */) {
                var args = Array.prototype.slice.call(arguments, 2);
                var namespaces = functionName.split('.');
                var func = namespaces.pop();
                for (var i = 0; i < namespaces.length; i++) {
                    context = context[namespaces[i]];
                }
                return context[func].apply(context, args);
            };

            executeFunctionByName(result.additionalData.callback, window, result);
        } else if (result) {
            that.emit('notification', result);
        }
    };

    // triggered on error
    var fail = function(msg) {
        var e = (typeof msg === 'string') ? new Error(msg) : msg;
        that.emit('error', e);
    };

    // wait at least one process tick to allow event subscriptions
    setTimeout(function() {
        exec(success, fail, 'mobsms', 'INITIALIZE', [options]);
    }, 10);
};

mobsms.prototype.RequestVerifyCode = function(successCallback, errorCallback, PhoneNumber) {
    if (!errorCallback) { errorCallback = function() {}; }

    if (typeof errorCallback !== 'function')  {
        console.log('mobsms.RequestVerifyCode failure: failure parameter not a function');
        return;
    }

    if (typeof successCallback !== 'function') {
        console.log('mobsms.RequestVerifyCode failure: success callback parameter must be a function');
        return;
    }

    exec(successCallback, errorCallback, 'mobsms', 'RequestVerifyCode', [{PhoneNumber: PhoneNumber}]);
};

mobsms.prototype.SubmitVerifyCode = function(successCallback, errorCallback, PhoneNumber, VerifyCode) {
    if (!errorCallback) { errorCallback = function() {}; }

    if (typeof errorCallback !== 'function')  {
        console.log('mobsms.SubmitVerifyCode failure: failure parameter not a function');
        return;
    }

    if (typeof successCallback !== 'function') {
        console.log('mobsms.SubmitVerifyCode failure: success callback parameter must be a function');
        return;
    }

    exec(successCallback, errorCallback, 'mobsms', 'SubmitVerifyCode', [{PhoneNumber: PhoneNumber, VerifyCode: VerifyCode}]);
};


mobsms.prototype.on = function(eventName, callback) {
    if (this._handlers.hasOwnProperty(eventName)) {
        this._handlers[eventName].push(callback);
    }
};

mobsms.prototype.off = function (eventName, handle) {
    if (this._handlers.hasOwnProperty(eventName)) {
        var handleIndex = this._handlers[eventName].indexOf(handle);
        if (handleIndex >= 0) {
            this._handlers[eventName].splice(handleIndex, 1);
        }
    }
};

mobsms.prototype.emit = function() {
    var args = Array.prototype.slice.call(arguments);
    var eventName = args.shift();

    if (!this._handlers.hasOwnProperty(eventName)) {
        return false;
    }

    for (var i = 0, length = this._handlers[eventName].length; i < length; i++) {
        var callback = this._handlers[eventName][i];
        if (typeof callback === 'function') {
            callback.apply(undefined,args);
        } else {
            console.log('event handler: ' + eventName + ' must be a function');
        }
    }

    return true;
};

mobsms.prototype.finish = function(successCallback, errorCallback, id) {
    if (!successCallback) { successCallback = function() {}; }
    if (!errorCallback) { errorCallback = function() {}; }
    if (!id) { id = 'handler'; }

    if (typeof successCallback !== 'function') {
        console.log('finish failure: success callback parameter must be a function');
        return;
    }

    if (typeof errorCallback !== 'function')  {
        console.log('finish failure: failure parameter not a function');
        return;
    }

    exec(successCallback, errorCallback, 'mobsms', 'finish', [id]);
};

/*!
 * Push Notification Plugin.
 */

module.exports = {
    /**
     * Register for Push Notifications.
     *
     * This method will instantiate a new copy of the mobsms object
     * and start the registration process.
     *
     * @param {Object} options
     * @return {mobsms} instance
     */

    init: function(options) {
        return new mobsms(options);
    },

//    hasPermission: function(successCallback, errorCallback) {
//        exec(successCallback, errorCallback, 'mobsms', 'hasPermission', []);
//    },

    /**
     * mobsms Object.
     *
     * Expose the mobsms object for direct use
     * and testing. Typically, you should use the
     * .init helper method.
     */

    mobsms: mobsms
};
