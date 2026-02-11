// (function checkAuth() {
//
//     const token = sessionStorage.getItem("token");
//
//     // If token missing → force redirect to login
//     if (!token || token === "" || token === "null") {
//         sessionStorage.clear();      // clear anything leftover
//         window.location.replace("/login.html");  // replace() prevents going back
//     }
//
// })();
//
//

(function checkAuth() {

    /* ===============================
       AUTH CHECK
    ================================ */
    const token = sessionStorage.getItem("token");

    if (!token || token === "" || token === "null") {
        sessionStorage.clear();
        window.location.replace("/login.html");
        return;
    }

    /* ===============================
       SESSION TIMER CONFIG
    ================================ */
    const SESSION_TIMEOUT = 2 * 60; // 2 minutes
    let remainingSeconds = SESSION_TIMEOUT;
    let timerStarted = false;

    /* ===============================
       INIT AFTER SIDEBAR LOAD
    ================================ */
    document.addEventListener("DOMContentLoaded", initSession);

    function initSession() {
        const timeEl = document.getElementById("time");
        const logoutBtn = document.getElementById("logoutBtn");

        if (!timeEl || !logoutBtn) {
            setTimeout(initSession, 100);
            return;
        }

        logoutBtn.addEventListener("click", logout);

        if (!timerStarted) {
            startTimer(timeEl);
            timerStarted = true;
        }

        // 👇 RESET TIMER ON USER ACTIVITY — HERE
        ["mousemove", "keydown", "click", "scroll", "touchstart"]
            .forEach(event => {
                document.addEventListener(event, resetTimer, true);
            });
    }

    /* ===============================
       TIMER LOGIC
    ================================ */
    function startTimer(timeEl) {
        const timerBox = document.querySelector(".session-timer");

        setInterval(() => {
            const minutes = Math.floor(remainingSeconds / 60);
            const seconds = remainingSeconds % 60;

            timeEl.textContent =
                `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;

            // 🔥 Visual states
            if (remainingSeconds <= 30) {
                timerBox.classList.add("danger");
                timerBox.classList.remove("warning");
            } else if (remainingSeconds <= 60) {
                timerBox.classList.add("warning");
                timerBox.classList.remove("danger");
            } else {
                timerBox.classList.remove("warning", "danger");
            }

            if (remainingSeconds <= 0) {
                logout();
            }

            remainingSeconds--;
        }, 1000);
    }

    function resetTimer() {
        remainingSeconds = SESSION_TIMEOUT;
    }

    /* ===============================
       LOGOUT
    ================================ */
    function logout() {
        sessionStorage.clear();
        window.location.replace("/login.html");
    }

})();
