const IndexApp = (() => {
    const selectTemplate = (data) => {
        return `<option value="${data.value}">${data.name}</option>`;
    };

    const IndexController = function () {
        const indexService = new IndexService();

        const login = () => {
            const loginBtn = document.getElementById('login-btn');
            loginBtn.addEventListener('click', indexService.login);
            const loginPassword = document.getElementById('login-password');
            loginPassword.addEventListener('keydown', indexService.keyDownLogin);
        };

        const signUp = () => {
            const signUpBtn = document.getElementById('signup-btn');
            signUpBtn.addEventListener('click', indexService.signUp);
            const signUpArea = document.getElementById('contentwrapper');
            signUpArea.addEventListener('keydown', indexService.keyDownSignUp);
        };

        const init = () => {
            login();
            signUp();
        };

        return {
            init: init,
        };
    };

    const IndexService = function () {
        const indexApi = new IndexApi();

        const login = (event) => {
            event.preventDefault();

            const email = document.getElementById('login-email');
            const password = document.getElementById('login-password');

            if (String(email.value).length > 50 ) {
                alert("이메일이 너무 깁니다.");
                return;
            }

            const data = {
                userEmail: email.value,
                userPassword: password.value
            };

            indexApi.login(data)
            .then(response => {
                return response.json();
            }).then(json => {
                if (json.hasOwnProperty('errorMessage')) {
                    alert(json.errorMessage);
                } else {
                    location.href='/newsfeed';
                }
            })
        };

        const signUp = (event) => {
            event.preventDefault();

            if(AppStorage.check('sign-up-run')) return;
            AppStorage.set('sign-up-run', true);

            const firstName = document.getElementById('signup-first-name');
            const lastName = document.getElementById('signup-last-name');
            const email = document.getElementById('signup-email');
            const password = document.getElementById('signup-password');

            const data = {
                userName: {
                    firstName: firstName.value,
                    lastName: lastName.value,
                },
                userEmail: email.value,
                userPassword: password.value
            };

            indexApi.signUp(data)
                .then(response => {
                    return response.json();
                }).then(json => {
                    if (json.hasOwnProperty('errorMessage')) {
                        alert(json.errorMessage);
                    } else {
                        firstName.value = "";
                        lastName.value = "";
                        email.value = "";
                        password.value = "";
                        alert('가입을 완료했습니다. 로그인 하세요.');
                    }
                    AppStorage.set('sign-up-run', false);
                })
        };

        const keyDownLogin = (event) => {
            event.stopPropagation();
            const loginBtn = document.getElementById('login-btn');

            if (event.which === 13) {
                loginBtn.click();
            }
        };

        const keyDownSignUp = (event) => {
            event.stopPropagation();
            const signUpBtn = document.getElementById('signup-btn');

            if (event.which === 13) {
                signUpBtn.click();
            }
        };

        return {
            login: login,
            signUp: signUp,
            keyDownLogin: keyDownLogin,
            keyDownSignUp: keyDownSignUp,
        };
    };

    const IndexApi = function () {
        const login = (data) => {
            return Api.post('/api/signin', data);
        };

        const signUp = (data) => {
            return Api.post('/api/users/signup', data);
        };

        return {
            login: login,
            signUp: signUp,
        }
    };

    const init = () => {
        const indexController = new IndexController();
        indexController.init();
    };

    return {
        init: init,
    };
})();

IndexApp.init();