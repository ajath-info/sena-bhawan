  // Load sidebar
  fetch("sidebar.html")
      .then(res => res.text())
      .then(html => {
          document.getElementById("sidebar").innerHTML = html;

          // Attach logout after sidebar loads
          document.getElementById("logoutBtn")
              .addEventListener("click", function () {
                  sessionStorage.clear();
                  window.location.replace("/login.html");
              });

          (function () {
            const currentPage = location.pathname.split('/').pop();
            const menuLinks = document.querySelectorAll('.sidebar-nav a');

            menuLinks.forEach(link => {
                const linkPath = link.getAttribute('href').split('/').pop();
                if (currentPage === linkPath) {
                    link.classList.add('active');
                }
            });
        })();

      });
