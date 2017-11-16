"use strict";

function newButton() {
    localStorage.currentId = null;
}

function cancelButton() {
    redirectToHome();
}

function searchButton() {
    var kw = document.getElementById("keyword").value;
    if (!kw) {
        return;
    }
    //ugly solution here...
    document.getElementById("blogList").innerHTML = "    <blog-grid id=\"bloggrid\"\n" +
        "               :data=\"gridData\"\n" +
        "               :columns=\"gridColumns\"\n" +
        "               :filter-key=\"searchQuery\">\n" +
        "    </blog-grid>";
    sendHttpRequest("GET", "post/search?keyword=" + kw, null, showList);
}

function saveButton() {
    var title = document.getElementById("title").value;
    var content = document.getElementById("content").value;
    if (!(title && content)) {
        return;
    }
    var data = JSON.stringify({user: {username: "admin"}, title: title, content: content});
    var id = localStorage.currentId;
    localStorage.currentId = null;
    if (id !== null && id !== "null") {
        sendHttpRequest("PUT", "post/edit?id=" + id, data, redirectToHome);
    } else {
        sendHttpRequest("POST", "post/add", data, redirectToHome)
    }
}

function deleteButton(id) {
    sendHttpRequest("DELETE", "post/delete?id=" + id, null, getAllPost);
    location.reload();
}


function getPostToEdit(id) {
    localStorage.currentId = id;
    location.replace("editPost.html");
}

function getAllPost() {
    sendHttpRequest("GET", "post/get/all", null, showList);
}

function redirectToHome() {
    window.location.replace("index.html");
}

function sendHttpRequest(action, url, data, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.status === 200) {
            callback(xmlHttp.responseText);
        }
    };
    xmlHttp.open(action, url, true);
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlHttp.send(data);
}

function initEditor() {
    new Vue({
        el: '#editor',
        data: {
            content: '# Write your blog in markdown'
        },
        computed: {
            compiledMarkdown: function () {
                return marked(this.content, {sanitize: true})
            }
        },
        methods: {
            update: _.debounce(function (e) {
                this.content = e.target.value
            }, 300)
        }
    });
    var id = localStorage.currentId;
    if (id !== null) {
        sendHttpRequest("GET", "post/get?id=" + id, null, updateEditor);
    }
}

function updateEditor(json) {
    var message = JSON.parse(json);
    var postList = message.posts;
    document.getElementById("title").value = postList[0].title;
    document.getElementById("content").value = postList[0].content;
}

function showList(json) {
    registerComponent();

    var message = JSON.parse(json);
    var postList = message.posts;
    var data = [];
    for (var i = 0; i < postList.length; i++) {
        var postId = postList[i].id;
        data.push({
            postId: postId,
            title: "<a onclick=\"getPostToEdit(" + postId + ")\">" + postList[i].title + "</a>",
            action: "<button onclick=deleteButton(" + postId + ")>Delete</button>"
        });
    }
    new Vue({
        el: '#blogList',
        data: {
            searchQuery: '',
            gridColumns: ['postId', 'title', 'action'],
            gridData: data
        }
    });
}

function registerComponent() {
    Vue.component('blog-grid', {
        template: '#grid-template',
        props: {
            data: Array,
            columns: Array,
            filterKey: String
        },
        data: function () {
            var sortOrders = {};
            this.columns.forEach(function (key) {
                sortOrders[key] = 1
            });
            return {
                sortKey: '',
                sortOrders: sortOrders
            }
        },
        computed: {
            filteredData: function () {
                var sortKey = this.sortKey;
                var filterKey = this.filterKey && this.filterKey.toLowerCase();
                var order = this.sortOrders[sortKey] || 1;
                var data = this.data;
                if (filterKey) {
                    data = data.filter(function (row) {
                        return Object.keys(row).some(function (key) {
                            return String(row[key]).toLowerCase().indexOf(filterKey) > -1
                        })
                    })
                }
                if (sortKey) {
                    data = data.slice().sort(function (a, b) {
                        a = a[sortKey];
                        b = b[sortKey];
                        return (a === b ? 0 : a > b ? 1 : -1) * order
                    })
                }
                return data
            }
        },
        filters: {
            capitalize: function (str) {
                return str.charAt(0).toUpperCase() + str.slice(1)
            }
        },
        methods: {
            sortBy: function (key) {
                this.sortKey = key;
                this.sortOrders[key] = this.sortOrders[key] * -1
            }
        }
    });
}
