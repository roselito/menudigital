importScripts('https://www.gstatic.com/firebasejs/12.7.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/12.7.0/firebase-messaging-compat.js');

const firebaseConfig = {
    apiKey: "AIzaSyC0PdUVe7ouuZgUUffy6HiK-vI-qVnEiyE",
    authDomain: "lu-mandalas.firebaseapp.com",
    projectId: "lu-mandalas",
    storageBucket: "lu-mandalas.appspot.com",
    messagingSenderId: "555973831215",
    appId: "1:555973831215:web:680ecfba650edd731c34da",
    measurementId: "G-TZF5LS36NM"
};
firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();