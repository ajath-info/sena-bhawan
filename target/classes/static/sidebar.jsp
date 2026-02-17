<<<<<<<< HEAD:target/classes/static/sidebar.jsp
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">

<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }

    html, body {
      height: 100%;
      overflow: hidden;
    }

    body {
      font-family: "Inter", sans-serif;
      background: #f1f3f5;
      color: #1f2937;
    }

    .container {
      display: flex;
      height: 100vh;
      overflow: hidden;
    }

    /* ================= SIDEBAR ================= */
    .sidebar {
      width: 256px;
      background: linear-gradient(180deg, #1b4332 0%, #081c15 100%);
      color: white;
      display: flex;
      flex-direction: column;

      height: 100vh;
      overflow-y: auto; /* ✅ sidebar scroll */
      overflow-x: hidden;
      flex-shrink: 0;
    }

    .sidebar-header {
      padding: 20px;
      border-bottom: 1px solid rgba(255,255,255,0.1);
      display: flex;
      align-items: center;
      min-height: 72px;
    }

    .logo-section {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .logo-icon {
      width: 32px;
      height: 32px;
      background: #ffb703;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
    }

    .logo-text h1 {
      font-size: 18px;
      font-weight: 700;
    }

    .logo-text p {
      font-size: 11px;
      color: #52b788;
    }

    .sidebar-nav {
      flex: 1;
      padding: 16px 0;
    }

    .nav-section-title {
      padding: 16px 20px 8px;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      color: #52b788;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 20px;
      color: white;
      text-decoration: none;
      border-left: 4px solid transparent;
    }

    .nav-item.active {
      background: rgba(255,255,255,0.1);
      border-left-color: #ffb703;
      font-weight: 600;
    }

    .nav-icon {
      font-size: 18px;
    }

    .sidebar-footer {
      padding: 16px;
      border-top: 1px solid rgba(255,255,255,0.1);
    }

    .user-profile {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: #2d6a4f;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
    }

    .user-info .name {
      font-size: 14px;
      font-weight: 600;
    }

    .user-info .role {
      font-size: 12px;
      color: #52b788;
    }
    /* ================= MAIN ================= */
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .header {
      background: white;
      border-bottom: 1px solid #e5e7eb;
      padding: 16px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header h2 {
      font-size: 22px;
      font-weight: 700;
    }

    .header p {
      font-size: 14px;
      color: #6b7280;
      margin-top: 4px;
    }

    .header-right input {
      padding: 8px 12px;
      border-radius: 8px;
      border: 1px solid #d1d5db;
      width: 240px;
    }

    .content-area {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
    }

    /* ================= STATS ================= */
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 20px;
      margin-bottom: 24px;
    }

    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      border: 1px solid #e5e7eb;
    }

    .stat-title {
      font-size: 14px;
      color: #6b7280;
      margin-bottom: 6px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: #1b4332;
    }

    .stat-change {
      font-size: 12px;
      margin-top: 6px;
      color: #16a34a;
    }

    /* ================= SECTIONS ================= */
    .section {
      background: white;
      border-radius: 12px;
      border: 1px solid #e5e7eb;
      padding: 20px;
      margin-bottom: 24px;
    }

    .section-title {
      font-size: 18px;
      font-weight: 700;
      margin-bottom: 16px;
      color: #1b4332;
    }

    /* ================= PROGRESS ================= */
    .rank-row {
      margin-bottom: 12px;
    }

    .rank-label {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      margin-bottom: 4px;
    }

    .progress {
      height: 10px;
      background: #e5e7eb;
      border-radius: 999px;
      overflow: hidden;
    }

    .progress-bar {
      height: 100%;
      background: #22c55e;
    }

    /* ================= ALERTS ================= */
    .alert {
      padding: 12px;
      border-radius: 8px;
      font-size: 13px;
      margin-bottom: 10px;
    }

    .alert.green { background: #ecfdf5; color: #065f46; }
    .alert.yellow { background: #fffbeb; color: #92400e; }
    .alert.red { background: #fef2f2; color: #991b1b; }

    /* ================= ACTIVITY ================= */
    .activity-item {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      padding: 10px 0;
      border-bottom: 1px solid #e5e7eb;
    }

    .activity-item:last-child {
      border-bottom: none;
    }

    /* ================= QUICK ACTIONS ================= */
    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 16px;
    }
    .logout-btn{
    cursor:pointer;
    color: #ffffff;
    font-weight: bold;
    text-decoration: none;}

    .action-card {
      border: 1px dashed #d1d5db;
      padding: 20px;
      text-align: center;
      border-radius: 12px;
      font-weight: 600;
      cursor: pointer;
      background: #fafafa;
    }
    </style>

<script src="auth.js"></script>

<div class="container">

  <!-- SIDEBAR -->
  <aside class="sidebar">
    <div class="sidebar-header">
      <div class="logo-section">
        <div class="logo-icon">🛡️</div>
        <div class="logo-text">
          <h1>ARMY HRMS</h1>
          <p>भारतीय सेना</p>
        </div>
      </div>
    </div>
    <nav class="sidebar-nav">
      <div class="nav-section-title">Main Menu</div>

      <a href="/dashboard-new.html" class="nav-item">
        <div class="nav-icon">🏠</div>
        <span>Dashboard</span>
      </a>

      <a href="/dashboard-officers.html" class="nav-item">
        <div class="nav-icon">🏠</div>
        <span>Dashboard Officers</span>
      </a>

      <a href="/dashboard-training.html" class="nav-item">
        <div class="nav-icon">🏠</div>
        <span>Dashboard Training</span>
      </a>

      <a href="/filter.html" class="nav-item">
        <div class="nav-icon">🔍</div>
        <span>Master Filter & Search</span>
      </a>

      <div class="nav-section-title">Administration</div>

      <a href="/role-permmissoon.html" class="nav-item">
        <div class="nav-icon">🔐</div>
        <span>Role & Permissions</span>
      </a>

      <!-- <a href="/user-management.html" class="nav-item">
        <div class="nav-icon">👥</div>
        <span>User Management</span>
      </a> -->

      <div class="nav-section-title">Masters</div>

      <a href="/course-master.html" class="nav-item">
        <div class="nav-icon">📚</div>
        <span>Course Master</span>
      </a>

      <a href="/grade-awards.html" class="nav-item">
        <div class="nav-icon">📝</div>
        <span>Grade & Awards</span>
      </a>

      <a href="/course-schedule.html" class="nav-item">
        <div class="nav-icon">📅</div>
        <span>Course Schedule</span>
      </a>

      <a href="/location-master.html" class="nav-item">
        <div class="nav-icon">📍</div>
        <span>Location Master</span>
      </a>

      <!-- <a href="/command-master.html" class="nav-item">
        <div class="nav-icon">🚩</div>
        <span>Command Master</span>
      </a> -->

      <a href="/rank-master.html" class="nav-item">
        <div class="nav-icon">🏅</div>
        <span>Rank Master</span>
      </a>

      <a href="/unit-master.html" class="nav-item">
        <div class="nav-icon">🛡️</div>
        <span>Unit Master</span>
      </a>

      <div class="nav-section-title">Forms</div>

      <a href="/personnel-form.html" class="nav-item">
        <div class="nav-icon">👤</div>
        <span>Personnel Data Form</span>
      </a>

      <a href="/coursepanel.html" class="nav-item">
        <div class="nav-icon">📋</div>
        <span>Course Panel Generation</span>
      </a>

      <a href="/orbat.html" class="nav-item">
        <div class="nav-icon">📋</div>
        <span>Orbit Generation Form</span>
      </a>

      <a href="/add-new-user.html" class="nav-item">
        <div class="nav-icon">📋</div>
        <span>New User Form</span>
      </a>

      <a href="/legal-disciplinary.html" class="nav-item">
        <div class="nav-icon">📋</div>
        <span>Legal And Disciplinary Form</span>
      </a>

      <a href="/course-eligibility-form.html" class="nav-item">
        <div class="nav-icon">📋</div>
        <span>Course Eligibility Form</span>
      </a>

      <a href="/course-posting-form.html" class="nav-item">
        <div class="nav-icon">📝</div>
        <span>Course & Posting Form</span>
      </a>
    </nav>

    <div class="sidebar-footer">
      <div class="user-profile">
        <div class="user-avatar">AD</div>
        <div class="user-info">
          <div class="name">Admin User</div>
          <div class="role">HQ Command</div>
        </div>
        <div>🚪</div>
      </div>
    </div>
    <div class="sidebar-footer">
      <div class="user-profile">
        <div class="user-avatar" id="avatar">U</div>
        <div class="user-info">

          <a href = "#" class="logout-btn" onclick="logout()"> Logout </a>
          <div class="role">User</div>
        </div>
        </div>
    </div>
  </aside>
</div>

<script>
    (function () {
    const currentPage = location.pathname.split('/').pop(); // Get the filename from the URL
    const menuLinks = document.querySelectorAll('.sidebar-nav a'); // Select all menu links

    menuLinks.forEach(link => {
      const linkPath = link.getAttribute('href').split('/').pop(); // Extract the filename from the link
      console.log (currentPage );
      console.log (linkPath );
      if (currentPage === linkPath) {
        link.classList.add('active'); // Add 'active' class directly to <a>
      }
    });
  })();

    function logout() {
      sessionStorage.clear();
      window.location.replace("/login.html"); // prevent back button
  }

</script>
========
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Location Master - ARMY HRMS</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">

  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }

    html, body {
      height: 100%;
      overflow: hidden;
    }

    body {
      font-family: "Inter", sans-serif;
      background: #f1f3f5;
      color: #1f2937;
    }

    .container {
      display: flex;
      height: 100vh;
      overflow: hidden;
    }

    /* ================= SIDEBAR ================= */
    .sidebar {
      width: 256px;
      background: linear-gradient(180deg, #1b4332 0%, #081c15 100%);
      color: white;
      display: flex;
      flex-direction: column;
      height: 100vh;
      overflow-y: auto;
      overflow-x: hidden;
      flex-shrink: 0;
    }

    .sidebar-header {
      padding: 20px;
      border-bottom: 1px solid rgba(255,255,255,0.1);
      display: flex;
      align-items: center;
      min-height: 72px;
    }

    .logo-section {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .logo-icon {
      width: 32px;
      height: 32px;
      background: #ffb703;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
    }

    .logo-text h1 {
      font-size: 18px;
      font-weight: 700;
    }

    .logo-text p {
      font-size: 11px;
      color: #52b788;
    }

    .sidebar-nav {
      flex: 1;
      padding: 16px 0;
    }

    .nav-section-title {
      padding: 16px 20px 8px;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      color: #52b788;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 20px;
      color: white;
      text-decoration: none;
      border-left: 4px solid transparent;
    }

    .nav-item.active {
      background: rgba(255,255,255,0.1);
      border-left-color: #ffb703;
      font-weight: 600;
    }

    .nav-icon {
      font-size: 18px;
    }

    .sidebar-footer {
      padding: 16px;
      border-top: 1px solid rgba(255,255,255,0.1);
    }

    .user-profile {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: #2d6a4f;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
    }

    .user-info .name {
      font-size: 14px;
      font-weight: 600;
    }

    .user-info .role {
      font-size: 12px;
      color: #52b788;
    }

    /* ================= MAIN ================= */
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      height: 100vh;
      overflow: hidden;
    }

    .header {
      background: white;
      border-bottom: 1px solid #e5e7eb;
      padding: 16px 24px;
      min-height: 72px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex-shrink: 0;
    }

    .header-left h2 {
      font-size: 22px;
      font-weight: 700;
    }

    .header-left p {
      font-size: 14px;
      color: #6b7280;
      margin-top: 4px;
    }

    .search-box input {
      width: 240px;
      padding: 10px 14px;
      border-radius: 8px;
      border: 1px solid #d1d5db;
      font-size: 14px;
    }

    .search-box input:focus {
      outline: none;
      border-color: #1b4332;
      box-shadow: 0 0 0 3px rgba(27, 67, 50, 0.1);
    }

    .content-area {
      flex: 1;
      padding: 24px;
      overflow-y: auto;
      background: #f1f3f5;
    }

    .card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      border: 1px solid #e5e7eb;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }

    .badge {
      background: #d1fae5;
      color: #065f46;
      padding: 6px 12px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;
    }

    .btn-primary {
      background: #1b4332;
      color: white;
      border: none;
      padding: 10px 16px;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
    }

    table {
      width: 100%;
      border-collapse: collapse;
    }

    th, td {
      padding: 12px;
      border-bottom: 1px solid #e5e7eb;
      text-align: left;
    }

    th {
      font-size: 12px;
      text-transform: uppercase;
      color: #6b7280;
      background: #f9fafb;
    }

    .actions {
      display: flex;
      gap: 8px;
    }

    .icon-btn {
      width: 32px;
      height: 32px;
      border-radius: 6px;
      border: 1px solid #e5e7eb;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      background: white;
    }

    .icon-btn.edit { color: #2563eb; border-color: #93c5fd; }
    .icon-btn.delete { color: #ef4444; border-color: #fecaca; }
  </style>
</head>

<body>

<script src="logout.js"></script>

<div class="container">

  <!-- SIDEBAR -->
<!--  <aside class="sidebar">-->
<!--    <div class="sidebar-header">-->
<!--      <div class="logo-section">-->
<!--        <div class="logo-icon">🛡️</div>-->
<!--        <div class="logo-text">-->
<!--          <h1>ARMY HRMS</h1>-->
<!--          <p>भारतीय सेना</p>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--    <nav class="sidebar-nav">-->
<!--      <div class="nav-section-title">Main Menu</div>-->

<!--      <a href="/dashboard-new.html" class="nav-item">-->
<!--        <div class="nav-icon">🏠</div>-->
<!--        <span>Dashboard</span>-->
<!--      </a>-->

<!--      <a href="/dashboard-officers.html" class="nav-item">-->
<!--        <div class="nav-icon">🏠</div>-->
<!--        <span>Dashboard Officers</span>-->
<!--      </a>-->

<!--      <a href="/dashboard-training.html" class="nav-item">-->
<!--        <div class="nav-icon">🏠</div>-->
<!--        <span>Dashboard Training</span>-->
<!--      </a>-->

<!--      <a href="/filter.html" class="nav-item">-->
<!--        <div class="nav-icon">🔍</div>-->
<!--        <span>Master Filter & Search</span>-->
<!--      </a>-->

<!--      <div class="nav-section-title">Administration</div>-->

<!--      <a href="/role-permmissoon.html" class="nav-item">-->
<!--        <div class="nav-icon">🔐</div>-->
<!--        <span>Role & Permissions</span>-->
<!--      </a>-->

<!--      &lt;!&ndash; <a href="/user-management.html" class="nav-item">-->
<!--        <div class="nav-icon">👥</div>-->
<!--        <span>User Management</span>-->
<!--      </a> &ndash;&gt;-->

<!--      <div class="nav-section-title">Masters</div>-->

<!--      <a href="/course-master.html" class="nav-item">-->
<!--        <div class="nav-icon">📚</div>-->
<!--        <span>Course Master</span>-->
<!--      </a>-->

<!--      <a href="/grade-awards.html" class="nav-item">-->
<!--        <div class="nav-icon">📝</div>-->
<!--        <span>Grade & Awards</span>-->
<!--      </a>-->

<!--      <a href="/course-schedule.html" class="nav-item">-->
<!--        <div class="nav-icon">📅</div>-->
<!--        <span>Course Schedule</span>-->
<!--      </a>-->

<!--      <a href="/location-master.html" class="nav-item active">-->
<!--        <div class="nav-icon">📍</div>-->
<!--        <span>Location Master</span>-->
<!--      </a>-->

<!--      &lt;!&ndash; <a href="/command-master.html" class="nav-item">-->
<!--        <div class="nav-icon">🚩</div>-->
<!--        <span>Command Master</span>-->
<!--      </a> &ndash;&gt;-->

<!--      <a href="/rank-master.html" class="nav-item">-->
<!--        <div class="nav-icon">🏅</div>-->
<!--        <span>Rank Master</span>-->
<!--      </a>-->

<!--      <a href="/unit-master.html" class="nav-item">-->
<!--        <div class="nav-icon">🛡️</div>-->
<!--        <span>Unit Master</span>-->
<!--      </a>-->

<!--      <div class="nav-section-title">Forms</div>-->

<!--      <a href="/personnel-form.html" class="nav-item">-->
<!--        <div class="nav-icon">👤</div>-->
<!--        <span>Personnel Data Form</span>-->
<!--      </a>-->

<!--      <a href="/coursepanel.html" class="nav-item">-->
<!--        <div class="nav-icon">📋</div>-->
<!--        <span>Course Panel Generation</span>-->
<!--      </a>-->

<!--      <a href="/orbat.html" class="nav-item">-->
<!--        <div class="nav-icon">📋</div>-->
<!--        <span>Orbit Generation Form</span>-->
<!--      </a>-->

<!--      <a href="/add-new-user.html" class="nav-item">-->
<!--        <div class="nav-icon">📋</div>-->
<!--        <span>New User Form</span>-->
<!--      </a>-->

<!--      <a href="/legal-disciplinary.html" class="nav-item">-->
<!--        <div class="nav-icon">📋</div>-->
<!--        <span>Legal And Disciplinary Form</span>-->
<!--      </a>-->

<!--      <a href="/course-posting-form.html" class="nav-item">-->
<!--        <div class="nav-icon">📝</div>-->
<!--        <span>Course & Posting Form</span>-->
<!--      </a>-->
<!--    </nav>-->

<!--    <div class="sidebar-footer">-->
<!--      <div class="user-profile">-->
<!--        <div class="user-avatar">AD</div>-->
<!--        <div class="user-info">-->
<!--          <div class="name">Admin User</div>-->
<!--          <div class="role">HQ Command</div>-->
<!--        </div>-->
<!--        <div>🚪</div>-->
<!--      </div>-->
<!--    </div>-->
<!--  </aside>-->

  <div id="sidebar"></div>

  <!-- MAIN -->
  <main class="main-content">

    <header class="header">
      <div class="header-left">
        <h2>Location Master</h2>
        <p>Manage Location Information</p>
      </div>

      <div class="search-box">
        <input type="text" placeholder="Search locations..." />
      </div>
    </header>

    <div class="content-area">
      <div class="card">

        <div class="card-header">
          <span id="locationCount" class="badge">0 Locations</span>
          <button class="btn-primary" onclick="openAddLocation()">+ Add Location</button>
        </div>

        <table>
          <thead>
            <tr>
              <th>S.No</th>
              <th>Location Name</th>
              <th>State</th>
              <th>Actions</th>
            </tr>
          </thead>


          <tbody id="locationTable">
          <tr><td colspan="4">Loading...</td></tr>
          </tbody>

        </table>

      </div>
    </div>

  </main>

</div>

<script>
  const tableBody = document.getElementById("locationTable");
  const locationCountEl = document.getElementById("locationCount");

  // API base for Location Master
  const API = "http://localhost:6060/api/location";

  // Load data when page opens
  window.onload = () => {
    loadLocationCount();
    loadLocationList();
  };

  // ===================== COUNT =====================
  function loadLocationCount() {
    fetch(API + "/count")
            .then(res => res.json())
            .then(count => {
              locationCountEl.textContent = count + " Locations";
            })
            .catch(() => {
              locationCountEl.textContent = "0 Locations";
            });
  }

  // ===================== LIST =====================
  function loadLocationList() {
  fetch(API)
    .then(res => res.json())
    .then(data => {
      tableBody.innerHTML = "";

      if (!data || data.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="4">No locations found</td></tr>`;
        return;
      }

      data.forEach((loc, index) => {
  tableBody.innerHTML += `
    <tr>
      <td>${index + 1}</td>
      <td>${loc.locationName || "-"}</td>
      <td>${loc.state || "-"}</td>
      <td class="actions">
        <div class="icon-btn edit" onclick="editLocation(${loc.srno})">✏️</div>
        <div class="icon-btn delete" onclick="deleteLocation(${loc.srno})">🗑️</div>
      </td>
    </tr>
  `;
});

    })
    .catch(err => {
      tableBody.innerHTML = `<tr><td colspan="4">Failed to load locations</td></tr>`;
    });
}


  /* ========== DELETE LOCATION ========== */
  function deleteLocation(srno) {
  if (!confirm("Are you sure you want to delete this location?")) return;

  fetch(`${API}/${srno}`, { method: "DELETE" })
    .then(res => {
      if (!res.ok) {
        alert("Failed to delete location");
        return;
      }
      alert("Location deleted successfully");
      loadLocationList();
      loadLocationCount();
    })
    .catch(() => alert("Server error while deleting"));
}





  /* ========== EDIT Location ========== */
  function editLocation(srno) {
  const newLocationName = prompt("Enter new Location Name:");
  const newState = prompt("Enter new State:");

  if (!newLocationName || !newState) {
    alert("All fields are required!");
    return;
  }

  const payload = {
    locationName: newLocationName,
    state: newState
  };

  fetch(`${API}/${srno}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
    .then(res => res.json())
    .then(() => {
      alert("Location updated successfully");
      loadLocationList();
    })
    .catch(() => alert("Failed to update Location"));
}



  // ===================== ADD LOCATION BUTTON =====================
  function openAddLocation() {
    window.location.href = "addlocation.html";  // not /addlocation.html
  }

</script>

</body>
</html>
>>>>>>>> dev-karan:target/classes/static/location-master.html
