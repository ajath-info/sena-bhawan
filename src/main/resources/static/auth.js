(function checkAuth() {

    /* ===============================
       AUTH CHECK - Check both storages
    ================================ */
    let token = sessionStorage.getItem("token");

    // If token not in sessionStorage, try to get from localStorage
    if (!token || token === "" || token === "null") {
        const userData = localStorage.getItem("userData");
        if (userData) {
            try {
                const parsedData = JSON.parse(userData);
                token = parsedData.token;
                // Sync to sessionStorage for existing functionality
                if (token) {
                    sessionStorage.setItem("token", token);
                    sessionStorage.setItem("userId", parsedData.userId);
                    sessionStorage.setItem("username", parsedData.username);
                    sessionStorage.setItem("appointment", parsedData.appointment);
                }
            } catch(e) {
                console.error("Error parsing userData:", e);
            }
        }
    }

    // If still no token → force redirect to login
    if (!token || token === "" || token === "null") {
        sessionStorage.clear();
        localStorage.removeItem("userData"); // Clear localStorage too
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
       LOGOUT - Clear both storages
    ================================ */
    function logout() {
        sessionStorage.clear();
        localStorage.removeItem("userData"); // Clear localStorage user data

        // Clear any sidebar cached data
        const keysToRemove = [];
        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key && key.startsWith('sidebar_')) {
                keysToRemove.push(key);
            }
        }
        keysToRemove.forEach(key => localStorage.removeItem(key));

        window.location.replace("/login.html");
    }

})();