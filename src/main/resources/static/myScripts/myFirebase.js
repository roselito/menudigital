import { initializeApp } from 'https://www.gstatic.com/firebasejs/12.7.0/firebase-app.js';
import { getMessaging, getToken, onMessage  } from "https://www.gstatic.com/firebasejs/12.7.0/firebase-messaging.js";
import { getAuth,
        signInWithEmailAndPassword,
        createUserWithEmailAndPassword,
        GoogleAuthProvider,
        signInWithPopup } from "https://www.gstatic.com/firebasejs/12.7.0/firebase-auth.js";

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);
const firebaseConfig = {
    apiKey: "AIzaSyC0PdUVe7ouuZgUUffy6HiK-vI-qVnEiyE",
    authDomain: "lu-mandalas.firebaseapp.com",
    projectId: "lu-mandalas",
    storageBucket: "lu-mandalas.appspot.com",
    messagingSenderId: "555973831215",
    appId: "1:555973831215:web:680ecfba650edd731c34da",
    measurementId: "G-TZF5LS36NM"
};
const auth = getAuth();
auth.useDeviceLanguage();
const provider = new GoogleAuthProvider();

export const inicializarFirebase = async () => {
    const app = initializeApp(firebaseConfig);
    await navigator.serviceWorker
            .register("./firebase-messaging-sw.js")
            .then(async function (registration) {
                console.log("registrado", registration);
            }).catch((error) => {
        console.log("erro register", error);
    });
};

export const obterToken = async () => {
    await getToken(getMessaging(), {vapidKey: "BAIbE1FBPt9_gDcFXJyy9lMKK3MWtLieemxtk4j99rrJalYAz4YVQ4TVtbl8KIgZET2D-VJtjjgw5C2NSeHsAaA"})
            .then((token) => {
                $.ajax({
                    type: 'GET',
                    url: '/token/' + token,
                    success: function (htmlContent) {
                        console.log("token: ", token);
                    }
                });
            }).catch((error) => {
        console.log("erro gettoken", error);
    });
};

export const verificarRegistration = async () => {
    await navigator.serviceWorker.getRegistration().then(function (reg) {
        console.log("registration ok:", reg);
    }).catch((err) => {
        console.log('erro no getregistration. ', err);
    });
};

export const pedirPermissaoParaReceberNotificacoes = async () => {
    await Notification.requestPermission().then(permission => {
        console.log(permission);
    });
};



onMessage(getMessaging(), (payload) => {
    console.log('Message received. ', payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body
    };
    $('#messageToast').toast('show');
    $('#messageTitle').text(payload.notification.title);
    $('#messageBody').text(payload.notification.body);
    console.log(payload.notification.title);
    console.log(payload.notification.body);
    displayNotification(notificationTitle, notificationOptions);
});

export const enviarNotificacao = () => {
    const registrationToken = 'cDwu1FYUNiTe-3nlGpVDp4:APA91bH3CJfK_QeWueG6-qFdKOsjUbzIIsC3SDgEQDIeHxur0FGZAYHThKTpJXcRcc-yBGWtk6AbYSVyeedOspv6g0bqbVPy_P_ArLMgzgRln0E4InbMgF0';

const message = {
  notification: {
    title: 'Teste envio',
    body: 'para o chrome',
    image: 'https://localhost/images/ic_launcher.png'
  },
  token: registrationToken
};

// Send a message to the device corresponding to the provided
// registration token.
messaging.send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
};



function displayNotification(title, options) {
    if (!("Notification" in window)) {
        alert("Este navegador não suporta notificações de desktop.");
    } else {
        Notification.requestPermission().then(permission => {
            if (permission === 'granted') {
                navigator.serviceWorker.getRegistration().then(function (reg) {
                    if (reg) {
                        reg.showNotification(title, options);
                        console.log(reg);
                    } else {
                        console.error("Service Worker registration not found.");
                    }
                }).catch((err) => {
                    console.log('erro no getregistration. ', err);
                });
            } else {
                console.log("Permissão para notificações negada.");
            }
        }).catch((err) => {
            console.log('erro no requestpermission. ', err);
        });
    }
}

async function logar() {
    await signInWithPopup(auth, provider).then((result) => {
        const credential = GoogleAuthProvider.credentialFromResult(result);
        const token = credential.accessToken;
        const user = result.user;
        console.log(user.displayName);
        console.log(user.email);
        return true;
    }).catch((error) => {
        const errorCode = error.code;
        const errorMessage = error.message;
        const email = error.customData.email;
        const credential = GoogleAuthProvider.credentialFromError(error);
        alert(errorMessage);
        return false;
    });
}