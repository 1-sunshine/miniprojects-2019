const Api = {
    get: function (url) {
        return fetch(url, {
            method: 'GET',
        });
    },
    post: function (url, data) {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
    },
    put: function (url, data) {
        return fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
    },
    delete: function (url) {
        return fetch(url, {
            method: 'DELETE',
        });
    },

    postImage: function (url, data) {
        return fetch(url, {
            method: 'POST',
            body: data,
            headers: {
            },
        });
    },
};

const AppStorage = (function() {
    let storage = {};

    const get = function(keyName, defaultValue) {
        if(storage.hasOwnProperty(keyName)) {
            return storage[keyName];
        }
        storage[keyName] = defaultValue;
        return storage[keyName];
    };

    const set = function(keyName, value) {
        storage[keyName] = value;
    };

    const check = function(keyName) {
        return get(keyName, false);
    };

    return {
        get: get,
        set: set,
        check: check,
    };
})();

const TimeApi = {
    pretty : function (time) {
        let date = new Date(time);
        let secondDiff = Math.floor((Date.now() - date) / 1000);

        secondDiff = secondDiff - 32400;

        if (secondDiff < 0) secondDiff = 0;

        let day_diff = Math.floor(secondDiff / 86400);

        if ( isNaN(day_diff) || day_diff < 0 )
            return;

        return day_diff === 0 && (

            secondDiff < 60 && "방금전" ||

            secondDiff < 120 && "1분전" ||

            secondDiff < 3600 && Math.floor( secondDiff / 60 ) + " 분전" ||

            secondDiff < 7200 && "1 시간전" ||

            secondDiff < 86400 && Math.floor( secondDiff / 3600 ) + " 시간전") ||

            day_diff === 1 && "어제" ||

            day_diff < 7 && day_diff + " 일전" ||

            day_diff < 31 && Math.floor( day_diff / 7 ) + " 주전" ||

            day_diff < 360 && Math.floor( day_diff / 30 ) + " 개월 전" ||

            day_diff >= 360 && (Math.floor( day_diff / 360 )===0?1:Math.floor( day_diff / 360 )) + " 년 전"

    },
};

const LoadingApi = {
    loading: function () {
        const htmlAttr = document.documentElement;
        const header = document.getElementById('header');
        const loader = document.getElementById('loader');

        header.style.display = "none";
        loader.style.display = "block";
        htmlAttr.classList.add('overlay-dark');
    },

    loadingDone: function () {
        const htmlAttr = document.documentElement;
        const header = document.getElementById('header');
        const loader = document.getElementById('loader');

        header.style.display = "block";
        loader.style.display = "none";
        htmlAttr.classList.remove('overlay-dark');
    },
};