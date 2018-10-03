const base64decode = function(s) {
    return Uint8Array.from(atob(s), function(c) { return c.charCodeAt(0) });
};

var applicationServerKey;

const manager = function() {
    return navigator.serviceWorker.register("serviceworker.js").then(
        function(serviceWorkerRegistration) {
            return serviceWorkerRegistration.pushManager;
        }
    )
};

const subscription = function() {
    return manager().then(
        function(m) {
            return m.getSubscription();
        }
    );
};

window.addEventListener('load', function() {
    applicationServerKey = document.getElementById('application-server-key').value;

    subscription().then(
        function(s) {
            reset(s);
        }
    ).catch(function(e) { console.log(e) });

    document.getElementById('subscribe').onclick = function() {
        manager().then(
            function(m) {
                const options = {
                    applicationServerKey: base64decode(applicationServerKey),
                    userVisibleOnly: true
                };
                return m.subscribe(options).then(
                    function(s) {
                        console.log(s);
                        reset(s);
                    }
                );
            }
        ).catch(function(e) { console.log(e) });
    };

    document.getElementById('unsubscribe').onclick = function() {
        subscription().then(
            function(s) {
                return s.unsubscribe().then(
                    function(ok) {
                        console.log(ok);
                        reset(null);
                    }
                );
            }
        ).catch(function(e) { console.log(e) });
    };

    document.getElementById('test').onclick = function() {
        subscription().then(
            function(s) {
                var data = {endpoint: '', auth: '', p256dh: ''};
                
                if (s != null) {
                    data.endpoint = s.endpoint;
                    data.p256dh = base64encode(s.getKey('p256dh'));
                    data.auth = base64encode(s.getKey('auth'));
                }

                fetch('/test', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(data)
                });
            }
        ).catch(function(e) { console.log(e) });
    };
});
