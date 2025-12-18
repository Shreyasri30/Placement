async function api(path, opts = {}) {
  const res = await fetch(path, {
    headers: { "Content-Type": "application/json", ...(opts.headers || {}) },
    credentials: "include",
    ...opts
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || "Request failed");
  return data;
}

function qs(k) { return new URLSearchParams(location.search).get(k); }

/* Toast */
function ensureToastWrap() {
  let w = document.getElementById("toastWrap");
  if (!w) {
    w = document.createElement("div");
    w.id = "toastWrap";
    w.className = "toast-wrap";
    document.body.appendChild(w);
  }
  return w;
}
function toast(title, message, type = "info", ms = 2400) {
  const wrap = ensureToastWrap();
  const t = document.createElement("div");
  t.className = `toast ${type === "ok" ? "ok" : type === "err" ? "err" : ""}`;
  t.innerHTML = `<div class="dot"></div><div><p class="t">${title}</p><p class="m">${message}</p></div>`;
  wrap.appendChild(t);
  setTimeout(() => { t.style.opacity = "0"; t.style.transition = "opacity .25s"; }, ms);
  setTimeout(() => { t.remove(); }, ms + 280);
}
function toastOnce(title, message, type = "info") {
  sessionStorage.setItem("toast_once", JSON.stringify({ title, message, type }));
}
function showToastOnceIfAny() {
  const raw = sessionStorage.getItem("toast_once");
  if (!raw) return;
  sessionStorage.removeItem("toast_once");
  try {
    const { title, message, type } = JSON.parse(raw);
    toast(title, message, type || "info");
  } catch (_) { }
}

/* Auth helpers */
async function isLoggedIn() {
  try { await api("/api/auth/me"); return true; }
  catch (e) { return false; }
}
async function requireAdminOrRedirect() {
  try {
    const me = await api("/api/auth/me");
    if (me.role !== 'ADMIN') throw new Error("Not admin");
  }
  catch (e) {
    toastOnce("Admin access required", "Please login as administrator.", "err");
    location.href = "login.html";
    throw e;
  }
}
async function requireAuthOrRedirect() {
  try {
    const me = await api("/api/auth/me");
    if (!me || !me.userId) throw new Error("Not logged in");
  } catch (e) {
    toastOnce("Login required", "Please sign in to access this page.", "err");
    location.href = "login.html";
    throw e;
  }
}
async function logout() {
  try {
    await api("/api/auth/logout", { method: "POST" });
    toastOnce("Signed out", "You have been logged out.", "ok");
    location.href = "index.html";
  } catch (e) {
    toast("Error", e.message, "err");
  }
}

/* Static header for all pages */
function headerHTML(isAuth, active = "", role = "USER") {
  const linkClass = (k) => active === k ? "nav-active" : "";
  const isAdmin = role === 'ADMIN';

  return `
  <nav class="navbar glass">
    <a href="/" class="brand">Reclaim.</a>
    <div class="nav-links">
        ${isAdmin ? `
          <a class="${linkClass("admin-dash")}" href="admin-dashboard.html">Admin Dash</a>
          <a class="${linkClass("browse")}" href="browse.html">Browse App</a>
          <button class="btn-link" onclick="logout()">Logout</button>
        ` : `
          ${isAuth ? `
            <a class="${linkClass("home")}" href="/">Home</a>
            <a class="${linkClass("browse")}" href="browse.html">Browse</a>
            <a class="${linkClass("dashboard")}" href="dashboard.html">Dashboard</a>
            <a class="${linkClass("post")}" href="post.html">Post Item</a>
            <button class="btn-link" onclick="logout()">Logout</button>
          ` : `
            <a class="${linkClass("login")}" href="login.html">Login</a>
            <a href="register.html" class="btn btn-primary" style="margin-left: 20px; color: white;">Get Started</a>
          `}
        `}
    </div>
  </nav>`;
}

async function renderHeader(active = "") {
  const top = document.getElementById("top");
  if (!top) return;
  let auth = false;
  let role = "USER";
  try {
    const me = await api("/api/auth/me");
    auth = true;
    role = me.role;
  } catch (_) { auth = false; }
  top.innerHTML = headerHTML(auth, active, role);
}

/* UI helpers */
function thumb(imgPath, fallbackText = "") {
  const src = imgPath ? imgPath : autoImageFor(fallbackText);
  return `<div class="thumb"><img src="${src}" alt="item image"></div>`;
}

function badges(item) {
  const t = item.type || "";
  const s = item.status || "";
  const b1 = `<span class="badge">${t}</span>`;
  const b2 = (s === "ACTIVE")
    ? `<span class="badge ok">ACTIVE</span>`
    : `<span class="badge warn">RESOLVED</span>`;
  return `<div class="badges">${b1}${b2}</div>`;
}

/* Public preview items */
function demoItems(type) {
  const lost = [
    { id: "demo1", type: "LOST", status: "ACTIVE", title: "Black wallet with ID card", category: "Documents", location: "Library entrance", eventDate: "2025-12-10", description: "Leather wallet with ID and 2 cards." },
    { id: "demo2", type: "LOST", status: "ACTIVE", title: "Blue earbuds case", category: "Electronics", location: "Canteen", eventDate: "2025-12-12", description: "Small blue case, no brand visible." }
  ];
  const found = [
    { id: "demo3", type: "FOUND", status: "ACTIVE", title: "Keys with red keychain", category: "Accessories", location: "Parking area", eventDate: "2025-12-11", description: "Two keys and a red keychain." },
    { id: "demo4", type: "FOUND", status: "ACTIVE", title: "Calculator (Casio)", category: "Electronics", location: "Block B corridor", eventDate: "2025-12-09", description: "Found near classroom corridor." }
  ];
  return (type === "FOUND") ? found : lost;
}

function iconSVG(name) {
  const common = `width="100%" height="100%" viewBox="0 0 420 240" xmlns="http://www.w3.org/2000/svg"`;
  const bg = `
    <defs>
      <linearGradient id="g" x1="0" x2="1">
        <stop offset="0" stop-color="rgba(59,130,246,0.30)"/>
        <stop offset="1" stop-color="rgba(34,197,94,0.20)"/>
      </linearGradient>
    </defs>
    <rect x="0" y="0" width="420" height="240" rx="18" fill="rgba(11,18,32,0.70)" stroke="rgba(35,48,79,0.95)"/>
    <circle cx="320" cy="70" r="70" fill="url(#g)"/>
    <circle cx="90" cy="160" r="62" fill="rgba(59,130,246,0.16)"/>
  `;

  const wallet = `
    <rect x="120" y="92" width="190" height="86" rx="14" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)" />
    <rect x="150" y="114" width="140" height="18" rx="9" fill="rgba(233,238,252,0.12)" />
    <rect x="240" y="142" width="70" height="24" rx="12" fill="rgba(59,130,246,0.25)" stroke="rgba(59,130,246,0.30)"/>
    <circle cx="265" cy="154" r="6" fill="rgba(233,238,252,0.18)"/>
  `;

  const phone = `
    <rect x="168" y="52" width="92" height="160" rx="18" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)"/>
    <rect x="182" y="74" width="64" height="112" rx="12" fill="rgba(233,238,252,0.08)"/>
    <circle cx="214" cy="196" r="7" fill="rgba(233,238,252,0.16)"/>
  `;

  const keys = `
    <circle cx="190" cy="128" r="34" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)"/>
    <circle cx="190" cy="128" r="12" fill="rgba(233,238,252,0.10)"/>
    <path d="M222 128h86l-16 16h-18l-10 10h-16l-8 8" stroke="rgba(233,238,252,0.18)" stroke-width="10" stroke-linecap="round" stroke-linejoin="round"/>
  `;

  const idcard = `
    <rect x="120" y="88" width="200" height="96" rx="16" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)"/>
    <circle cx="168" cy="136" r="20" fill="rgba(59,130,246,0.22)"/>
    <rect x="206" y="120" width="92" height="12" rx="6" fill="rgba(233,238,252,0.12)"/>
    <rect x="206" y="144" width="70" height="10" rx="5" fill="rgba(233,238,252,0.10)"/>
  `;

  const bag = `
    <path d="M150 104c0-22 16-38 38-38h44c22 0 38 16 38 38" stroke="rgba(233,238,252,0.16)" stroke-width="10" stroke-linecap="round"/>
    <rect x="134" y="104" width="152" height="104" rx="18" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)"/>
    <rect x="168" y="140" width="84" height="14" rx="7" fill="rgba(34,197,94,0.18)"/>
  `;

  const generic = `
    <rect x="140" y="84" width="140" height="110" rx="20" fill="rgba(15,25,48,0.9)" stroke="rgba(233,238,252,0.12)"/>
    <rect x="170" y="112" width="80" height="14" rx="7" fill="rgba(233,238,252,0.12)"/>
    <rect x="170" y="140" width="60" height="12" rx="6" fill="rgba(233,238,252,0.10)"/>
  `;

  const map = { wallet, phone, keys, idcard, bag, generic };
  const body = map[name] || map.generic;

  const svg = `<svg ${common}>${bg}${body}</svg>`;
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
}

function autoImageFor(text) {
  const t = (text || "").toLowerCase();
  if (t.includes("wallet") || t.includes("purse")) return iconSVG("wallet");
  if (t.includes("phone") || t.includes("mobile")) return iconSVG("phone");
  if (t.includes("key")) return iconSVG("keys");
  if (t.includes("id") || t.includes("card") || t.includes("passport")) return iconSVG("idcard");
  if (t.includes("bag") || t.includes("backpack")) return iconSVG("bag");
  return iconSVG("generic");
}

/* Footer Injection */
function renderFooter() {
  if (document.getElementById("reclaim-footer")) return; // already exists

  const html = `
    <footer id="reclaim-footer" style="margin-top: 80px; padding-bottom: 20px;">
        <div class="container">
            <div class="glass" style="padding: 40px; border-radius: 20px;">
                <div class="grid grid-3">
                    <div>
                        <h3 style="margin-bottom:16px; font-size: 1.5rem;">Reclaim.</h3>
                        <p style="font-size: 0.9rem;">The trusted community-driven platform to report lost items and find what matters most to you. Simple, fast, and secure.</p>
                    </div>
                    <div>
                        <h4 style="margin-bottom:16px; color:white;">Quick Links</h4>
                        <div style="display:flex; flex-direction:column; gap:8px;">
                            <a href="/" style="color:var(--text-muted); text-decoration:none; font-size:0.9rem;">Home</a>
                            <a href="browse.html" style="color:var(--text-muted); text-decoration:none; font-size:0.9rem;">Browse Items</a>
                            <a href="dashboard.html" style="color:var(--text-muted); text-decoration:none; font-size:0.9rem;">Dashboard</a>
                            <a href="post.html" style="color:var(--text-muted); text-decoration:none; font-size:0.9rem;">Post Item</a>
                        </div>
                    </div>
                    <div>
                        <h4 style="margin-bottom:16px; color:white;">Contact & Legal</h4>
                        <div style="display:flex; flex-direction:column; gap:8px; font-size:0.9rem; color:var(--text-muted);">
                            <span>support@reclaim.app</span>
                            <span>+1 (555) 123-4567</span>
                            <div style="margin-top:8px;">
                                <a href="#" style="color:var(--text-muted); margin-right:10px;">Privacy</a>
                                <a href="#" style="color:var(--text-muted);">Terms</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div style="text-align:center; margin-top:40px; padding-top:20px; border-top:1px solid rgba(255,255,255,0.1); color:var(--text-muted); font-size:0.85rem;">
                    &copy; ${new Date().getFullYear()} Reclaim App. All rights reserved.
                </div>
            </div>
        </div>
    </footer>
  `;
  document.body.insertAdjacentHTML('beforeend', html);
}

// Auto-inject footer on load
window.addEventListener('DOMContentLoaded', () => {
  renderFooter();
});
