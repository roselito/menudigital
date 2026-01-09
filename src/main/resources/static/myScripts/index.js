//            import * as fb from "./myScripts/myFirebase.js";
//            function enviarMensagem() {
//                fb.inicializarFirebase();
//                fb.obterToken();
//                fb.pedirPermissaoParaReceberNotificacoes();
//                $.ajax({
//                    type: 'GET',
//                    url: '/mensagem',
//                    success: function (htmlContent) {
//                        console.log("mensagem enviada: ");
//                    }
//                });
//            }
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
            
            auth.onAuthStateChanged(auth, (user) => {
                if (user) {// O usuário está logado                    
                    console.log("Usuário logado:", user);
                    const uid = user.uid;
                    const email = user.email;
                    const displayName = user.displayName;
                    const photoURL = user.photoURL;
                    console.log(user.email);
                    console.log(user.accessToken);
                } else {
                    console.log("Usuário deslogado");
                }
            });
//            auth.languageCode = 'pt_br';
            var ui = new firebaseui.auth.AuthUI(auth);
            var uiConfig = {
//                signInSuccessUrl: '#',
                signInOptions: [
                    // List of providers
                    firebase.auth.EmailAuthProvider.PROVIDER_ID,
                    firebase.auth.GoogleAuthProvider.PROVIDER_ID
                            // Add other providers here
                ]
            };
//            ui.start('#firebaseui-auth-container', uiConfig);

