const CommentApp = (() => {
    const commentTemplate = Handlebars.compile(template.comment);
    const commentSubTemplate = Handlebars.compile(template.subComment);
    const commentArea = Handlebars.compile(template.commentArea);

    const CommentController = function () {
        const commentService = new CommentService();

        const add = () => {
            const saveBtn = document.getElementById('article-list');
            saveBtn.addEventListener('keydown', commentService.add);
        };

        const showModal = () => {
            const articleList = document.getElementById('article-list');
            articleList.addEventListener('click', commentService.showModal);
        };

        const update = () => {
            const commentUpdateBtn = document.getElementById('comment-update-btn');
            commentUpdateBtn.addEventListener('click', commentService.update);
        };

        const remove = () => {
            const articleList = document.getElementById('article-list');
            articleList.addEventListener('click', commentService.remove);
        };

        const read = () => {
            const articleList = document.getElementById('article-list');
            articleList.addEventListener('click', commentService.read);
        };

        const subRead = () => {
            const articleList = document.getElementById('article-list');
            articleList.addEventListener('click', commentService.subRead);
        };

        const init = () => {
            add();
            showModal();
            update();
            remove();
            read();
            subRead();
        };

        return {
            init: init,
        }
    };

    const CommentService = function () {
        const commentApi = new CommentApi();

        const renderComment = (articleId, commentId, commentList, template) => {
            commentApi.render(articleId, commentId)
                .then(response => response.json())
                .then(data => {
                    data.forEach(comment => {
                        commentList
                            .insertAdjacentHTML('beforeend', template({
                                "id": comment.id,
                                "user-name": comment.authorName,
                                "comment-contents": comment.content.contents,
                                "updatedTime": TimeApi.pretty(comment.updatedTime),
                            }));
                        ReactionApp.service().showGoodCount('comment', comment.id);
                        CommentApp.service().showCommentCount(articleId);
                    })
                })
                .catch(error => console.log("error: " + error));
        };

        const read = (event) => {
            const target = event.target;
            const article = target.closest('div[data-object="article"]');
            const articleId = article.getAttribute('data-article-id');
            const commentList = document.getElementById('comment-list-' + articleId);
            const commentId = "";

            if (target.closest('li[data-list="comment-list"]')) {
                if (commentList.hasChildNodes()) {
                    while (commentList.hasChildNodes()) {
                        commentList.removeChild(commentList.firstChild);
                    }
                    return;
                }

                renderComment(articleId, commentId, commentList, commentTemplate)
            }
        };

        const subRead = (event) => {
            const target = event.target;

            if (target.getAttribute('data-comment-list')) {
                const article = target.closest('div[data-object="article"');
                const articleId = article.getAttribute('data-article-id');
                const comment = target.closest('li[data-object="comment"');
                const commentId = comment.getAttribute('data-comment-id');
                const inputArea = comment.querySelector('.sub-comment-input-area');
                const commentList = document.getElementById('comment-sublist-' + commentId);

                if (inputArea.hasChildNodes()) {
                    while (inputArea.hasChildNodes()) {
                        inputArea.removeChild(inputArea.firstChild);
                    }
                }

                if (commentList.hasChildNodes()) {
                    while (commentList.hasChildNodes()) {
                        commentList.removeChild(commentList.firstChild);
                    }
                    return;
                }

                inputArea.innerHTML = commentArea({id: commentId});
                inputArea.addEventListener('keydown', add);

                renderComment(articleId, commentId, commentList, commentSubTemplate);
            }
        };

        const add = (event) => {
            event.stopPropagation();

            const target = event.target;

            if (event.which === 13 && target.value !== '' && target.classList.contains('comment-save')) {
                const article = target.closest('div[data-object="article"]');
                const articleId = article.getAttribute('data-article-id');
                const commentId = target.getAttribute('data-parents-comment-id') ? target.getAttribute('data-parents-comment-id') : '';
                const commentList = commentId === "" ? document.getElementById('comment-list-' + articleId) :
                                                        document.getElementById('comment-sublist-' + commentId);
                const template = commentId === "" ? commentTemplate : commentSubTemplate;

                if (!event.shiftKey) {
                    const data = {
                        contents: target.value,
                    };

                    commentApi.add(articleId, commentId, data)
                        .then(() => {
                            if (commentList.hasChildNodes()) {
                                while (commentList.hasChildNodes()) {
                                    commentList.removeChild(commentList.firstChild);
                                }
                            }

                            renderComment(articleId, commentId, commentList, template)
                        })
                        .then(() => {
                            target.value = "";
                            target.blur();
                        });
                }
            }
        };

        const showModal = (event) => {
            const target = event.target;
            if (target.closest('li[data-btn="comment-update"]')) {
                const article = target.closest('div[data-object="article"]');
                const articleId = article.getAttribute('data-article-id');
                const comment = target.closest('li[data-object="comment"]');
                const commentId = comment.getAttribute('data-comment-id');
                const updateArea = document.getElementById('comment-update-contents');
                const showModalBtn = document.getElementById('show-comment-modal-btn');
                const articleHiddenId  = document.getElementById('hidden-article-id');
                const commentHiddenId  = document.getElementById('hidden-comment-id');

                updateArea.value = comment.querySelector('span[data-object="comment-contents"]').innerText;
                articleHiddenId.value = articleId;
                commentHiddenId.value = commentId;

                showModalBtn.click();
            }
        };

        const update = () => {
            const articleHiddenId  = document.getElementById('hidden-article-id');
            const commentHiddenId  = document.getElementById('hidden-comment-id');
            const updateArea = document.getElementById('comment-update-contents');
            const contents = document.getElementById('comment-contents-' + commentHiddenId.value);
            const data = {
                contents: updateArea.value,
            };

            commentApi.update(articleHiddenId.value, commentHiddenId.value, data)
                .then(response => response.json())
                .then(comment => {
                    if (comment.hasOwnProperty('errorMessage')) {
                        alert(comment.errorMessage);
                    } else {
                        contents.innerText = comment.content.contents;
                    }
                });
        };

        const remove = (event) => {
            const target = event.target;
            if (target.closest('li[data-btn="comment-delete"]')) {
                const article = target.closest('div[data-object="article"]');
                const articleId = article.getAttribute('data-article-id');
                const comment = target.closest('li[data-object="comment"]');
                const commentId = comment.getAttribute('data-comment-id');

                commentApi.remove(articleId, commentId)
                    .then(response => response.json())
                    .then((json) => {
                        if (json.hasOwnProperty('errorMessage')) {
                            alert(json.errorMessage);
                        } else {
                            comment.remove();
                            CommentApp.service().showCommentCount(articleId);
                        }
                    });
            }
        };

        const showCommentCount = (articleId) => {
            const commentSpan = document.getElementById(`article-${articleId}-comment-size`);
            commentApi.size(articleId)
                .then(data => {
                    return data.json();
                }).then(commentCount => {
                    commentSpan.innerText = commentCount;
            });

        };

        return {
            add: add,
            read: read,
            subRead: subRead,
            update: update,
            showModal: showModal,
            remove: remove,
            showCommentCount: showCommentCount,
        }
    };

    const CommentApi = function () {
        const add = (articleId, commentId, data) => {
            return Api.post(`/api/articles/${articleId}/comments/${commentId}`, data)
        };

        const update = (articleId, commentId, data) => {
            return Api.put(`/api/articles/${articleId}/comments/${commentId}`, data);
        };

        const remove = (articleId, commentId) => {
            return Api.delete(`/api/articles/${articleId}/comments/${commentId}`);
        };

        const render = (articleId, commentId) => {
            return Api.get(`/api/articles/${articleId}/comments/${commentId}`);
        };

        const size = (articleId) => {
            return Api.get(`/api/articles/${articleId}/comments/size`);
        };

        return {
            add: add,
            update: update,
            remove: remove,
            render: render,
            size: size,
        };
    };

    const init = () => {
        const commentController = new CommentController();
        commentController.init();
    };

    return {
        init: init,
        service: CommentService,
    }
})();

CommentApp.init();