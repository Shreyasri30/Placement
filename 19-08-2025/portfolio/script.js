const projects = [
  {
    title: "Weather App",
    description:
      "Responsive app that fetches real-time weather for any city using OpenWeather API. Displays temperature, humidity, wind, and icons.",
    tech: ["HTML", "CSS", "JavaScript", "OpenWeather API"],
    link: "#"
  },
  {
    title: "Student Registration System",
    description:
      "Full-stack CRUD app to add, update, and delete student records with form validation and REST APIs.",
    tech: ["Node.js", "Express", "MongoDB", "HTML", "CSS", "JavaScript"],
    link: "#"
  },
  {
    title: "Personal Portfolio Website",
    description:
      "Interactive portfolio showcasing skills, certifications, and projects with smooth nav and responsive design.",
    tech: ["HTML", "CSS", "JavaScript"],
    link: "#"
  },
  {
    title: "Subscription Management System",
    description:
      "Django + React system to add/extend/end subscriptions with live revenue report based on users and product costs.",
    tech: ["Django", "React", "SQLite", "Axios"],
    link: "#"
  },
  {
    title: "ShynTax - GST Invoice & Tax",
    description:
      "Flask dashboard for invoices, clients, GST calculations (CGST/SGST/IGST), analytics, PDF/Excel export.",
    tech: ["Flask", "SQLAlchemy", "Bootstrap", "Pandas", "XHTML2PDF"],
    link: "#"
  },
  {
    title: "ShynArch - Floorplan Generator",
    description:
      "Django REST + React tool to generate customizable 2D/3D floorplans with cost estimation and PDF export.",
    tech: ["Django REST", "React", "PostgreSQL"],
    link: "#"
  }
];

function renderProjects(filter = "") {
  const grid = document.getElementById("projectGrid");
  if (!grid) return;

  const q = filter.trim().toLowerCase();
  const filtered = projects.filter(p => {
    if (!q) return true;
    return (
      p.title.toLowerCase().includes(q) ||
      p.description.toLowerCase().includes(q) ||
      p.tech.join(" ").toLowerCase().includes(q)
    );
  });

  grid.innerHTML = filtered
    .map(
      (p) => `
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm project-card">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">${p.title}</h5>
            <p class="card-text flex-grow-1">${p.description}</p>
            <div class="d-flex flex-wrap gap-2 my-2">
              ${p.tech
                .map((t) => `<span class="badge text-bg-secondary">${t}</span>`)
                .join("")}
            </div>
            <a href="${p.link}" class="btn btn-outline-primary mt-auto" target="_blank" rel="noopener">
              <i class="bi bi-box-arrow-up-right me-1"></i>View
            </a>
          </div>
        </div>
      </div>`
    )
    .join("");

  if (filtered.length === 0) {
    grid.innerHTML = `<div class="col-12"><div class="alert alert-warning">No projects matched your search.</div></div>`;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const scrollSpy = new bootstrap.ScrollSpy(document.body, {
    target: "#topnav",
    offset: 100,
  });

  const yearEl = document.getElementById("year");
  if (yearEl) yearEl.textContent = new Date().getFullYear();

  renderProjects();

  const input = document.getElementById("searchInput");
  const clearBtn = document.getElementById("clearSearch");
  if (input) {
    input.addEventListener("input", () => renderProjects(input.value));
  }
  if (clearBtn) {
    clearBtn.addEventListener("click", () => {
      input.value = "";
      renderProjects("");
      input.focus();
    });
  }
});
