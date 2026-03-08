// sidebar.js

const keywordEmojis = [
    { key: "dashboard", icon: "📊" }, { key: "stat", icon: "📈" },
    { key: "report", icon: "📄" }, { key: "chart", icon: "📉" },
    { key: "command", icon: "🧭" }, { key: "personnel", icon: "👤" },
    { key: "officer", icon: "🎖️" }, { key: "unit", icon: "🏢" },
    { key: "location", icon: "📍" }, { key: "course", icon: "📚" },
    { key: "training", icon: "🏋️" }, { key: "schedule", icon: "🗓️" },
    { key: "attendance", icon: "📝" }, { key: "posting", icon: "📌" },
    { key: "transfer", icon: "🔁" }, { key: "master", icon: "⚙️" },
    { key: "setting", icon: "⚙️" }, { key: "config", icon: "🧩" },
    { key: "export", icon: "📤" }, { key: "delete", icon: "🗑️" },
    { key: "edit", icon: "✏️" }, { key: "add", icon: "➕" },
    { key: "create", icon: "✍️" }, { key: "view", icon: "👁️" },
    { key: "history", icon: "📜" }, { key: "print", icon: "🖨️" }
];

function autoEmoji(label, moduleName) {
    const text = (label + " " + moduleName).toLowerCase();
    for (const item of keywordEmojis) {
        if (text.includes(item.key)) return item.icon;
    }
    return "📁";
}

function buildSidebarHtml(modulesData) {
    let sidebarHtml = `
        <div class="sidebar-header">
            <div class="logo-section">
                <div class="logo-icon">🛡️</div>
                <div class="logo-text">
                    <h1>ARMY HRMS</h1>
                    <p>भारतीय सेना</p>
                </div>
            </div>
        </div>
        <nav class="sidebar-nav" id="dynamicSidebar">
    `;

    modulesData.modules.forEach(module => {
        const allowed = module.permissions.filter(p => p.allowed === true);
        if (allowed.length === 0) return;

        sidebarHtml += `<div class="nav-section-title">${module.moduleName}</div>`;

        allowed.forEach(perm => {
            let page = perm.url ? perm.url : "#";
            let icon = autoEmoji(perm.label, module.moduleName);
            sidebarHtml += `
                <a href="${page}" class="nav-item" data-page="${page}">
                    <div class="nav-icon">${icon}</div>
                    <span>${perm.label}</span>
                </a>
            `;
        });
    });

    const username = sessionStorage.getItem("username") || 'U';
    const appointment = sessionStorage.getItem("appointment") || 'User';

    sidebarHtml += `
        </nav>
        <div class="sidebar-footer">
            <div class="user-profile">
                <div class="user-avatar" id="avatar">${username.charAt(0).toUpperCase()}</div>
                <div class="user-info">
                    <a href="#" class="logout-btn" id="logoutBtn">Logout</a>
                    <div class="role">${appointment}</div>
                </div>
            </div>
        </div>
    `;

    return sidebarHtml;
}

function highlightActivePage() {
    const current = location.pathname.split('/').pop().split('?')[0];
    document.querySelectorAll(".nav-item").forEach(item => {
        item.classList.remove('active');
        const page = item.getAttribute("href").split('/').pop();
        if (current === page) item.classList.add("active");
    });
}

// In sidebar.js - this should already be there
function logout() {
    const userId = sessionStorage.getItem("userId");

    // Clear session storage
    sessionStorage.clear();

    // Clear cache for this user
    if (userId) {
        localStorage.removeItem('sidebar_' + userId);
        localStorage.removeItem('sidebar_time_' + userId);
    }

    // Clear any other sidebar caches
    const keysToRemove = [];
    for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith('sidebar_')) {
            keysToRemove.push(key);
        }
    }
    keysToRemove.forEach(key => localStorage.removeItem(key));

    // Redirect to login
    window.location.replace("/login.html");
}

function attachSidebarEvents() {
    highlightActivePage();

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", function(e) {
            e.preventDefault();
            logout();
        });
    }
}

// 🔥 FIXED: Main function to load sidebar with caching
async function loadSidebar() {
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("token");

    console.log("Loading sidebar for user:", userId);

    if (!userId) {
        window.location.replace("/login.html");
        return;
    }

    const sidebarContainer = document.getElementById("sidebarContainer");
    if (!sidebarContainer) {
        console.error("Sidebar container not found!");
        return;
    }

    // Check if sidebar is already in DOM and matches current user
    const existingSidebar = sidebarContainer.innerHTML;
    const existingUserId = sidebarContainer.getAttribute('data-user-id');

    // If sidebar exists and is for current user, just attach events
    if (existingSidebar && existingSidebar.trim() !== '' && existingUserId === userId) {
        console.log("Sidebar already loaded for this user");
        attachSidebarEvents();
        return;
    }

    // Show loading state
    sidebarContainer.innerHTML = `
        <div style="color: white; padding: 20px; text-align: center;">
            Loading menu...
        </div>
    `;

    // Check cache in localStorage
    const cachedSidebar = localStorage.getItem('sidebar_' + userId);
    const cacheTime = localStorage.getItem('sidebar_time_' + userId);
    const cacheDuration = 5 * 60 * 1000; // 5 minutes

    // Use cache if it exists and is not expired
    if (cachedSidebar && cacheTime && (Date.now() - parseInt(cacheTime)) < cacheDuration) {
        console.log("Loading sidebar from cache");
        sidebarContainer.innerHTML = cachedSidebar;
        sidebarContainer.setAttribute('data-user-id', userId);
        attachSidebarEvents();
        return;
    }

    // Fetch fresh data from API
    try {
        console.log("Fetching sidebar data from API");
        const res = await fetch(`http://localhost:6060/api/users/${userId}/permissions`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!res.ok) {
            if (res.status === 401) {
                logout();
                return;
            }
            throw new Error(`HTTP error! status: ${res.status}`);
        }

        const data = await res.json();

        const sidebarHtml = buildSidebarHtml(data);

        // Cache in localStorage
        localStorage.setItem('sidebar_' + userId, sidebarHtml);
        localStorage.setItem('sidebar_time_' + userId, Date.now().toString());

        // Load into page
        sidebarContainer.innerHTML = sidebarHtml;
        sidebarContainer.setAttribute('data-user-id', userId);
        attachSidebarEvents();

    } catch (error) {
        console.error("Error loading sidebar:", error);

        // Fallback to expired cache if available
        if (cachedSidebar) {
            console.log("API failed, loading expired cache");
            sidebarContainer.innerHTML = cachedSidebar;
            sidebarContainer.setAttribute('data-user-id', userId);
            attachSidebarEvents();
        }
    }
}

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", loadSidebar);