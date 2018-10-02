this.onpush = function(event) {
    console.log('onpush: ' + event.data.text());

    const title = 'webpush-testing-service';
    const data = event.data.json();

    console.log('onpush: ');

    event.waitUntil(self.registration.showNotification(title, data));
};
