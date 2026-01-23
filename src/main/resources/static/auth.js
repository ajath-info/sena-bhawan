(function checkAuth() {

    const token = sessionStorage.getItem("token");

    // If token missing → force redirect to login
    if (!token || token === "" || token === "null") {
        sessionStorage.clear();      // clear anything leftover
        window.location.replace("/login.html");  // replace() prevents going back
    }

})();
