const UsersApp = (() => {
    const templateProfileUpdate = Handlebars.compile(template.friends.usersBtn.profileUpdate);
    const templateNone = Handlebars.compile(template.friends.usersBtn.none);
    const templateAdd = Handlebars.compile(template.friends.usersBtn.add);
    const templateRequested = Handlebars.compile(template.friends.usersBtn.requested);
    const templateFriend = Handlebars.compile(template.friends.usersBtn.friend);

    const UserController = function () {
        const userService = new UserService();

        const render = () => {
            userService.renderUserStatusBtn();
        };

        const update = () => {
            const userUpdateBtn = document.getElementById('user-update-btn');
            userUpdateBtn.addEventListener('click', userService.update);
        };

        const showUpdateModal = () => {
            const userUpdateModalBtn = document.getElementById('user-update-form-btn');
            !!userUpdateModalBtn && userUpdateModalBtn.addEventListener('click', userService.showUserUpdateModal);
        };

        const init = () => {
            render();
            showUpdateModal();
            update();
        };

        return {
            init: init,
        };
    };

    const UserService = function () {
        const userApi = new UserApi();
        const friendApi = FriendsApp.api;
        const userBtn = document.getElementById('users-btn');
        const userId = window.location.pathname.split("/")[2];

        const drawBtn = (relationship) => {
            const userData = {'userId': userId};

            if (relationship === 'NONE') {
                userBtn.innerHTML = templateNone(userData);
                const userFriendBtn = document.getElementById('friend-add-btn');
                userFriendBtn.addEventListener('click', friendAdd);
            } else if (relationship === 'ADD'){
                userBtn.innerHTML = templateAdd(userData);
                const userFriendBtn = document.getElementById('friend-requesting-btn');
                userFriendBtn.addEventListener('click', friendRequesting);
            } else if (relationship === 'REQUESTED') {
                userBtn.innerHTML = templateRequested(userData);

                const approveBtn = document.getElementById('friend-approve-btn');
                approveBtn.addEventListener('click', friendApprove);
                const rejectBtn = document.getElementById('friend-reject-btn');
                rejectBtn.addEventListener('click', friendReject);
            } else if (relationship === 'FRIEND') {
                userBtn.innerHTML = templateFriend(userData);
                const userFriendBtn = document.getElementById('friend-ing-btn');
                userFriendBtn.addEventListener('click', friendRemove);
            }
        };

        const renderUserStatusBtn = () => {

            console.log('userId :: ', userId);
            HeaderApp.loginUser()
                .then(loginUser => {
                    console.log('loginUser response :: ', loginUser);
                    console.log(String(loginUser.id) === userId);

                    if (String(loginUser.id) === userId) {
                        userBtn.innerHTML = templateProfileUpdate();
                        const userUpdateModalBtn = document.getElementById('user-update-form-btn');
                        userUpdateModalBtn.addEventListener('click', showUserUpdateModal);
                    } else {
                        friendApi.relation(userId).then(relation => {
                            console.log('relation response :: ', relation);
                            const relationship = relation.relationship;
                            drawBtn(relationship);

                        });
                    }
                })
        };

        const friendAdd = (event) => {
            const target = event.target;
            const toId = target.getAttribute('data-friend-id');
            friendApi.add(toId)
                .then(response => response.json())
                .then(relation => {
                    console.log('relation response :: ', relation);
                    if (relation.hasOwnProperty('errorMessage')) {
                        alert(relation.errorMessage);
                    } else {
                        drawBtn(relation.relationship);
                    }
                });
        };

        const friendApprove = (event) => {
            const target = event.target;
            const toId = target.getAttribute('data-friend-id');
            friendApi.ok(toId)
                .then(response => response.json())
                .then(relation => {
                    console.log('relation response :: ', relation);
                    if (relation.hasOwnProperty('errorMessage')) {
                        alert(relation.errorMessage);
                    } else {
                        drawBtn(relation.relationship);
                    }
                });
        };

        const friendReject = (event) => {
            const target = event.target;
            const toId = target.getAttribute('data-friend-id');
            friendApi.no(toId)
                .then(response => response.json())
                .then(relation => {
                    console.log('relation response :: ', relation);
                    if (relation.hasOwnProperty('errorMessage')) {
                        alert(relation.errorMessage);
                    } else {
                        drawBtn(relation.relationship);
                    }
                });
        };

        const friendRemove = (event) => {
            const response = confirm('친구를 삭제 하시겠습니까?');
            if (response === true) {
                const target = event.target;
                const toId = target.getAttribute('data-friend-id');
                friendApi.remove(toId)
                    .then(response => response.json())
                    .then(relation => {
                        console.log('relation response :: ', relation);
                        if (relation.hasOwnProperty('errorMessage')) {
                            alert(relation.errorMessage);
                        } else {
                            drawBtn(relation.relationship);
                        }
                    });
            } else {
                return;
            }
        };

        const friendRequesting = (event) => {
            const response = confirm('친구 요청을 취소 하시겠습니까?');
            if (response === true) {
                const target = event.target;
                const toId = target.getAttribute('data-friend-id');
                friendApi.no(toId)
                    .then(response => response.json())
                    .then(relation => {
                        console.log('relation response :: ', relation);
                        if (relation.hasOwnProperty('errorMessage')) {
                            alert(relation.errorMessage);
                        } else {
                            drawBtn(relation.relationship);
                        }
                    });
            } else {
                return;
            }
        };

        const showUserUpdateModal = () => {
            const email = document.getElementById('user-update-email');
            const lastName = document.getElementById('user-update-last-name');
            const firstName = document.getElementById('user-update-first-name');

            const loginUser = AppStorage.get('login-user');
            email.value = loginUser.userEmail.email;
            lastName.value = loginUser.userName.name;
            firstName.value = loginUser.userName.name;

            const showModalBtn = document.getElementById('show-user-update-modal-btn')
            showModalBtn.click();
        };

        const update = (event) => {
            event.preventDefault();

            const storageKeyName = 'user-update-btn-run';
            if(AppStorage.check(storageKeyName)) return;
            AppStorage.set(storageKeyName, true);

            const nowPassword = document.getElementById('user-update-now-password');
            const email = document.getElementById('user-update-email');
            const lastName = document.getElementById('user-update-last-name');
            const firstName = document.getElementById('user-update-first-name');
            const changePassword = document.getElementById('user-update-change-password');

            const data = {
                userName: lastName.value + firstName.value,
                userEmail: email.value,
                userPassword: nowPassword.value,
                changePassword: changePassword.value
            };

            console.log('회원 정보 수정 요청 ::', data);
            userApi.update(data)
                .then(response => {
                    return response.json();
                }).then(json => {
                    if (json.hasOwnProperty('errorMessage')) {
                        alert(json.errorMessage);
                    } else {
                        console.log('회원 정보 수정 응답 ::', json);
                        nowPassword.value = "";
                        email.value = "";
                        lastName.value = "";
                        firstName.value = "";
                        changePassword.value = "";

                        HeaderApp.reRender();

                        alert('회원 정보를 수정 했습니다.');
                    }
                    AppStorage.set(storageKeyName, false);
                })
        };

        return {
            showUserUpdateModal: showUserUpdateModal,
            update: update,
            renderUserStatusBtn: renderUserStatusBtn,
        };
    };

    const UserApi = function () {
        const update = (data) => {
            return Api.put('/api/users', data);
        };

        return {
            update: update,
        }
    };

    const init = () => {
        const userController = new UserController();
        userController.init();
    };

    return {
        init: init,
    };
})();

UsersApp.init();