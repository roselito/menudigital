
const firebaseConfig = {
    apiKey: "AIzaSyC0PdUVe7ouuZgUUffy6HiK-vI-qVnEiyE",
    authDomain: "lu-mandalas.firebaseapp.com",
    projectId: "lu-mandalas",
    storageBucket: "lu-mandalas.appspot.com",
    messagingSenderId: "555973831215",
    appId: "1:555973831215:web:680ecfba650edd731c34da",
    measurementId: "G-TZF5LS36NM"
};
const app = firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const messaging = firebase.messaging();
var deviceToken = null;

async function workerServiceRegistered() {
    try {
        const registration = await navigator.serviceWorker
                .register("./firebase-messaging-sw.js");
        console.log("registrado", registration);
        return true;
    } catch (error) {
        console.log("erro register", error);
        return false;
    }
}

async function grantedNotificationsReceiving() {
    try
    {
        const permission = await Notification.requestPermission();
        console.log("permissão para notificações:", permission);
        return true;
    } catch (error) {
        console.log('erro requestpermission. ', error);
        return false;
    }

}

async function tokenObtido() {
    try {
        const token = await messaging.getToken(
                {vapidKey:
                            "BAIbE1FBPt9_gDcFXJyy9lMKK3MWtLieemxtk4j99rrJalYAz4YVQ4TVtbl8KIgZET2D-VJtjjgw5C2NSeHsAaA"
                });
        console.log("token: ", token);
        deviceToken = token;
        return true;
    } catch (error) {
        console.log("erro gettoken", error);
        return false;
    }
}


async function receberNotificacoes() {
    var retorno = false;
    if (await workerServiceRegistered()) {
        if (await grantedNotificationsReceiving())
            if (await tokenObtido())
                retorno = true;
    }
    return retorno;
}

messaging.onMessage((payload) => {
    console.log('Message received. ', payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body || '',
        icon: payload.notification.icon ,
        badge: payload.notification.badge ,
        image: payload.notification.image,
        data: {
            url: payload.notification.data?.url || '/'
        },
        tag: payload.notification.tag || 'default',
        renotify: false,
        requireInteraction: false,
        silent: false,
        actions: payload.notification.actions || [
            {
                action: 'open',
                title: 'Abrir'
            }
        ]
    };
    $('#messageToast').toast('show');
    $('#messageTitle').text(payload.notification.title);
    $('#messageBody').text(payload.notification.body);
    displayNotification(notificationTitle, notificationOptions);
});

async function displayNotification(title, options) {
    try {
        const reg = await navigator.serviceWorker.ready;
        if (reg) {
            reg.showNotification(title, options);
            console.log(reg);
        } else {
            console.error("Service Worker registration not found.");
        }
    } catch (err) {
        console.log('erro no getregistration. ', err);
    }
}



