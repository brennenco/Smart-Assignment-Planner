const endpoints = {
    me: "/api/auth/me",
    users: "/api/users",
    courses: "/api/courses",
    assignments: "/api/assignments"
};

function getCsrfToken() {
    const m = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return m ? decodeURIComponent(m[1]) : "";
}

async function apiFetch(url, options = {}) {
    const opts = { credentials: "include", ...options };
    const method = (opts.method || "GET").toUpperCase();
    const headers = new Headers(opts.headers || {});
    if (!headers.has("Content-Type") && opts.body && typeof opts.body === "string") {
        headers.set("Content-Type", "application/json");
    }
    if (["POST", "PUT", "DELETE", "PATCH"].includes(method)) {
        const t = getCsrfToken();
        if (t) {
            headers.set("X-XSRF-TOKEN", t);
        }
    }
    opts.headers = headers;
    return fetch(url, opts);
}

const state = {
    currentUser: null,
    users: [],
    courses: [],
    assignments: [],
    /** @type {{ year: number, month: number }} month is 0-11 */
    calendarMonth: (() => {
        const n = new Date();
        return { year: n.getFullYear(), month: n.getMonth() };
    })(),
    /** @type {string | null} YYYY-MM-DD */
    selectedDateIso: null
};

const elements = {
    totalUsers: document.getElementById("totalUsers"),
    totalCourses: document.getElementById("totalCourses"),
    totalAssignments: document.getElementById("totalAssignments"),
    completedAssignments: document.getElementById("completedAssignments"),
    statUsersCard: document.getElementById("statUsersCard"),
    usersSection: document.getElementById("usersSection"),
    usersBody: document.getElementById("usersBody"),
    coursesBody: document.getElementById("coursesBody"),
    assignmentsBody: document.getElementById("assignmentsBody"),
    statusText: document.getElementById("statusText"),
    refreshBtn: document.getElementById("refreshBtn"),
    logoutBtn: document.getElementById("logoutBtn"),
    userGreeting: document.getElementById("userGreeting"),
    roleBadge: document.getElementById("roleBadge"),
    btnAddCourse: document.getElementById("btnAddCourse"),
    btnAddAssignment: document.getElementById("btnAddAssignment"),
    modalCourse: document.getElementById("modalCourse"),
    modalAssignment: document.getElementById("modalAssignment"),
    formCourse: document.getElementById("formCourse"),
    formAssignment: document.getElementById("formAssignment"),
    maCourse: document.getElementById("maCourse"),
    statusFilter: document.getElementById("statusFilter"),
    searchInput: document.getElementById("searchInput"),
    viewDashboard: document.getElementById("view-dashboard"),
    viewCalendar: document.getElementById("view-calendar"),
    viewTabs: document.querySelectorAll(".view-tab"),
    calendarMonthLabel: document.getElementById("calendarMonthLabel"),
    calendarGrid: document.getElementById("calendarGrid"),
    calendarSelectedDay: document.getElementById("calendarSelectedDay"),
    calPrevBtn: document.getElementById("calPrevBtn"),
    calNextBtn: document.getElementById("calNextBtn"),
    calTodayBtn: document.getElementById("calTodayBtn")
};

function updateUserHeader() {
    if (!state.currentUser) return;
    const u = state.currentUser;
    const role = u.role || "";
    const scope =
        role === "ADMIN"
            ? " — viewing all users (admin)"
            : "";
    if (elements.userGreeting) {
        elements.userGreeting.textContent = `Signed in as ${u.name} (${u.email})${scope}`;
    }
    if (elements.roleBadge) {
        elements.roleBadge.hidden = false;
        elements.roleBadge.textContent = role === "ADMIN" ? "Admin" : "Student";
    }
    if (elements.statUsersCard) {
        elements.statUsersCard.style.display = role === "ADMIN" ? "" : "none";
    }
    if (elements.usersSection) {
        elements.usersSection.hidden = role !== "ADMIN";
    }
    document.body.classList.toggle("admin-view", role === "ADMIN");
}

async function fetchJson(url) {
    const response = await apiFetch(url);
    if (response.status === 401) {
        window.location.replace("/login.html");
        throw new Error("Unauthorized");
    }
    if (!response.ok) {
        throw new Error(`${url} responded with ${response.status}`);
    }
    return response.json();
}

function setStatus(message, isError = false) {
    if (!elements.statusText) return;
    elements.statusText.textContent = message;
    elements.statusText.style.color = isError ? "#b91c1c" : "#6b7280";
}

function getFilteredAssignments() {
    const filterValue = elements.statusFilter.value;
    const searchValue = elements.searchInput.value.trim().toLowerCase();

    return state.assignments.filter(item => {
        if (filterValue === "complete" && item.status !== true) {
            return false;
        }
        if (filterValue === "incomplete" && item.status !== false) {
            return false;
        }

        if (!searchValue) {
            return true;
        }

        const title = normalizeDisplayText(item.title ?? "").toLowerCase();
        const description = normalizeDisplayText(item.description ?? "").toLowerCase();
        const course = normalizeDisplayText(item.courseName ?? "").toLowerCase();
        return (
            title.includes(searchValue) ||
            description.includes(searchValue) ||
            course.includes(searchValue)
        );
    });
}

function assignmentDateKey(item) {
    const d = item.dueDate;
    if (d == null) return null;
    const s = String(d);
    return s.length >= 10 ? s.slice(0, 10) : s;
}

/** Build map dateStr -> assignments[] from filtered list */
function assignmentsByDate() {
    const map = new Map();
    for (const a of getFilteredAssignments()) {
        const key = assignmentDateKey(a);
        if (!key) continue;
        if (!map.has(key)) map.set(key, []);
        map.get(key).push(a);
    }
    return map;
}

function updateStats() {
    const completedCount = state.assignments.filter(item => item.status === true).length;
    if (elements.totalUsers) {
        elements.totalUsers.textContent =
            state.currentUser && state.currentUser.role === "ADMIN"
                ? String(state.users.length)
                : "—";
    }
    elements.totalCourses.textContent = String(state.courses.length);
    elements.totalAssignments.textContent = String(state.assignments.length);
    elements.completedAssignments.textContent = String(completedCount);
}

function renderUsers() {
    if (!state.currentUser || state.currentUser.role !== "ADMIN") {
        elements.usersBody.innerHTML = "<tr><td colspan='4'>Sign in as admin to see all users.</td></tr>";
        return;
    }
    if (state.users.length === 0) {
        elements.usersBody.innerHTML = "<tr><td colspan='4'>No users found.</td></tr>";
        return;
    }

    const rows = state.users
        .map(user => `
            <tr>
                <td>${user.userId ?? ""}</td>
                <td>${escapeHtml(user.name)}</td>
                <td>${escapeHtml(user.email)}</td>
                <td>${escapeHtml(user.role ?? "")}</td>
            </tr>
        `)
        .join("");
    elements.usersBody.innerHTML = rows;
}

function renderCourses() {
    if (state.courses.length === 0) {
        elements.coursesBody.innerHTML = "<tr><td colspan='5'>No courses found.</td></tr>";
        return;
    }

    const rows = state.courses
        .map(course => {
            const id = course.courseId;
            return `
            <tr>
                <td>${id ?? ""}</td>
                <td class="col-owner">${course.userId ?? "—"}</td>
                <td>${escapeHtml(course.courseName)}</td>
                <td>${course.totalPoints ?? ""}</td>
                <td><button type="button" class="btn-secondary btn-small" data-edit-course data-id="${id}">Edit</button></td>
            </tr>`;
        })
        .join("");
    elements.coursesBody.innerHTML = rows;
}

function renderAssignments() {
    const filtered = getFilteredAssignments();

    if (filtered.length === 0) {
        elements.assignmentsBody.innerHTML =
            "<tr><td colspan='10'>No assignments match the current filters.</td></tr>";
        renderCalendar();
        return;
    }

    const rows = filtered
        .sort((a, b) => String(a.dueDate ?? "").localeCompare(String(b.dueDate ?? "")))
        .map(item => {
            const statusClass = item.status ? "complete" : "incomplete";
            const statusLabel = item.status ? "Complete" : "Incomplete";
            const earned = item.pointsEarned ?? "-";
            const possible = item.pointsPossible ?? "-";
            const type = item.assignmentType ?? "ASSIGNMENT";
            const id = item.assignmentId;
            const courseLabel = item.courseName ?? (item.courseId != null ? `Course #${item.courseId}` : "—");
            return `
                <tr>
                    <td>${id ?? ""}</td>
                    <td>${escapeHtml(courseLabel)}</td>
                    <td>${escapeHtml(item.title)}</td>
                    <td>${escapeHtml(type)}</td>
                    <td>${item.dueDate ?? ""}</td>
                    <td>${item.priority ?? ""}</td>
                    <td><span class="pill ${statusClass}">${statusLabel}</span></td>
                    <td>${earned} / ${possible}</td>
                    <td><button type="button" class="btn-secondary btn-small" data-edit-assignment data-id="${id}">Edit</button></td>
                    <td><button type="button" class="btn-danger btn-small" data-del="assignment" data-id="${id}">Delete</button></td>
                </tr>
            `;
        })
        .join("");

    elements.assignmentsBody.innerHTML = rows;
    renderCalendar();
}

async function deleteAssignment(id) {
    if (!confirm("Delete this assignment?")) return;
    const res = await apiFetch(`${endpoints.assignments}/${id}`, { method: "DELETE" });
    if (res.status === 401) {
        window.location.replace("/login.html");
        return;
    }
    if (!res.ok) {
        alert("Could not delete assignment.");
        return;
    }
    await loadAllData();
}

/**
 * Fixes common UTF-8 vs Latin-1/Windows-1252 mojibake (e.g. "ÔÇô" instead of "–")
 * and normalizes Unicode dash punctuation to a plain hyphen for display/storage.
 */
function normalizeDisplayText(value) {
    if (value == null) return "";
    let s = String(value);
    // UTF-8 en dash (E2 80 93) mis-decoded as three Latin-1-ish chars (common in DB dumps)
    s = s.replace(/\u00D4\u00C7\u00F4/g, "-"); // ÔÇô
    s = s.replace(/ÔÇô/g, "-");
    // UTF-8 en/em dash bytes read as Windows-1252 (â€¦ style)
    s = s.replace(/\u00E2\u20AC\u201C/g, "-");
    s = s.replace(/\u00E2\u20AC\u201D/g, "-");
    s = s.replace(/\u00E2\u20AC\u2122/g, "-");
    // Unicode dash punctuation → ASCII hyphen (safe everywhere)
    s = s.replace(/[\u2013\u2014\u2011\u2010]/g, "-");
    return s;
}

function escapeHtml(value) {
    const raw = normalizeDisplayText(value ?? "");
    return raw
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

function formatMonthLabel(year, month) {
    const d = new Date(year, month, 1);
    return d.toLocaleString(undefined, { month: "long", year: "numeric" });
}

function todayIsoLocal() {
    const n = new Date();
    const y = n.getFullYear();
    const m = String(n.getMonth() + 1).padStart(2, "0");
    const day = String(n.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
}

function buildMonthGrid(year, monthIndex) {
    const first = new Date(year, monthIndex, 1);
    const startPad = first.getDay();
    const daysInMonth = new Date(year, monthIndex + 1, 0).getDate();
    const cells = [];

    for (let i = 0; i < startPad; i++) {
        cells.push({ kind: "pad" });
    }

    for (let d = 1; d <= daysInMonth; d++) {
        const iso = `${year}-${String(monthIndex + 1).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
        cells.push({ kind: "day", day: d, iso });
    }

    while (cells.length % 7 !== 0) {
        cells.push({ kind: "pad" });
    }

    while (cells.length < 42) {
        cells.push({ kind: "pad" });
    }

    return cells;
}

function renderCalendar() {
    if (!elements.calendarGrid || !elements.calendarMonthLabel) return;

    const { year, month } = state.calendarMonth;
    elements.calendarMonthLabel.textContent = formatMonthLabel(year, month);

    const byDate = assignmentsByDate();
    const today = todayIsoLocal();
    const cells = buildMonthGrid(year, month);

    const html = cells
        .map(cell => {
            if (cell.kind === "pad") {
                return `<div class="calendar-cell calendar-cell--pad" aria-hidden="true"></div>`;
            }

            const list = byDate.get(cell.iso) ?? [];
            const sorted = [...list].sort((a, b) => String(a.title ?? "").localeCompare(String(b.title ?? "")));
            const maxShow = 3;
            const show = sorted.slice(0, maxShow);
            const chips = show
                .map((a, idx) => {
                    const st = a.status ? "complete" : "incomplete";
                    const title = escapeHtml(a.title ?? "(no title)");
                    const id = a.assignmentId ?? idx;
                    return `<button type="button" class="cal-chip ${st}" data-iso="${cell.iso}" data-aid="${id}" title="${title}">${title}</button>`;
                })
                .join("");

            const more = sorted.length > maxShow
                ? `<div class="cal-chip more" title="${sorted.length - maxShow} more">+${sorted.length - maxShow} more</div>`
                : "";

            const isToday = cell.iso === today;
            const todayCls = isToday ? " calendar-cell--today" : "";

            return `
                <div class="calendar-cell${todayCls}" data-date="${cell.iso}" role="gridcell">
                    <span class="calendar-day-num">${cell.day}</span>
                    ${chips}${more}
                </div>
            `;
        })
        .join("");

    elements.calendarGrid.innerHTML = html;

    elements.calendarGrid.querySelectorAll(".calendar-cell[data-date]").forEach(el => {
        el.addEventListener("click", ev => {
            const t = ev.target;
            if (t && t.closest && t.closest("button.cal-chip[data-aid]")) return;
            const iso = el.getAttribute("data-date");
            if (iso) selectCalendarDay(iso);
        });
    });

    elements.calendarGrid.querySelectorAll("button.cal-chip[data-aid]").forEach(btn => {
        btn.addEventListener("click", e => {
            e.stopPropagation();
            const iso = btn.getAttribute("data-iso");
            if (iso) selectCalendarDay(iso);
        });
    });

    if (state.selectedDateIso) {
        const still = state.assignments.some(a => assignmentDateKey(a) === state.selectedDateIso);
        if (!still) {
            state.selectedDateIso = null;
        }
    }

    renderSelectedDayDetail();
}

function selectCalendarDay(iso) {
    state.selectedDateIso = iso;
    renderSelectedDayDetail();
}

function renderSelectedDayDetail() {
    if (!elements.calendarSelectedDay) return;

    const iso = state.selectedDateIso;
    if (!iso) {
        elements.calendarSelectedDay.innerHTML =
            "<p class=\"muted-inline\">Click a day or an assignment chip to see details for that date.</p>";
        return;
    }

    const byDate = assignmentsByDate();
    const list = byDate.get(iso) ?? [];
    const pretty = new Date(iso + "T12:00:00").toLocaleDateString(undefined, {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric"
    });

    if (list.length === 0) {
        elements.calendarSelectedDay.innerHTML = `<h3>${escapeHtml(pretty)}</h3><p>No assignments match filters for this day.</p>`;
        return;
    }

    const items = [...list]
        .sort((a, b) => String(a.title ?? "").localeCompare(String(b.title ?? "")))
        .map(a => {
            const st = a.status ? "Complete" : "Incomplete";
            const typ = a.assignmentType ?? "ASSIGNMENT";
            const pts = `${a.pointsEarned ?? "-"} / ${a.pointsPossible ?? "-"}`;
            return `<li><strong>${escapeHtml(a.title ?? "")}</strong> — ${escapeHtml(typ)} — ${escapeHtml(st)} — ${escapeHtml(pts)} pts</li>`;
        })
        .join("");

    elements.calendarSelectedDay.innerHTML = `<h3>${escapeHtml(pretty)}</h3><ul>${items}</ul>`;
}

function setView(name) {
    const isDash = name === "dashboard";
    elements.viewDashboard.classList.toggle("hidden", !isDash);
    elements.viewCalendar.classList.toggle("hidden", isDash);

    elements.viewTabs.forEach(tab => {
        const active = tab.getAttribute("data-view") === name;
        tab.classList.toggle("active", active);
        tab.setAttribute("aria-selected", active ? "true" : "false");
    });

    if (name === "calendar") {
        renderCalendar();
    }
}

function shiftCalendarMonth(delta) {
    let { year, month } = state.calendarMonth;
    month += delta;
    while (month < 0) {
        month += 12;
        year -= 1;
    }
    while (month > 11) {
        month -= 12;
        year += 1;
    }
    state.calendarMonth = { year, month };
    renderCalendar();
}

function goCalendarToday() {
    const n = new Date();
    state.calendarMonth = { year: n.getFullYear(), month: n.getMonth() };
    state.selectedDateIso = todayIsoLocal();
    renderCalendar();
}

/** @returns {Promise<boolean>} true if data loaded */
async function loadAllData() {
    setStatus("Loading courses and assignments...");
    if (elements.refreshBtn) elements.refreshBtn.disabled = true;

    try {
        const [courses, assignments] = await Promise.all([
            fetchJson(endpoints.courses),
            fetchJson(endpoints.assignments)
        ]);

        state.courses = Array.isArray(courses) ? courses : [];
        state.assignments = Array.isArray(assignments) ? assignments : [];

        if (state.currentUser && state.currentUser.role === "ADMIN") {
            const users = await fetchJson(endpoints.users);
            state.users = Array.isArray(users) ? users : [];
        } else {
            state.users = [];
        }

        updateStats();
        renderUsers();
        renderCourses();
        renderAssignments();
        setStatus("Data loaded successfully.");
        return true;
    } catch (error) {
        setStatus(`Failed to load data: ${error.message}`, true);
        return false;
    } finally {
        if (elements.refreshBtn) elements.refreshBtn.disabled = false;
    }
}

function openModal(el) {
    if (el) el.classList.remove("hidden");
}

function closeModal(el) {
    if (el) el.classList.add("hidden");
}

function resetCourseModal() {
    const hid = document.getElementById("mcEditId");
    if (hid) hid.value = "";
    if (elements.formCourse) elements.formCourse.reset();
    const mp = document.getElementById("mcPoints");
    if (mp) mp.value = "1000";
    const h = document.getElementById("modalCourseHeading");
    if (h) h.textContent = "New course";
    const b = document.getElementById("btnCourseSubmit");
    if (b) b.textContent = "Create";
}

function openCourseModalForEdit(course) {
    resetCourseModal();
    document.getElementById("mcEditId").value = String(course.courseId ?? "");
    document.getElementById("mcName").value = normalizeDisplayText(course.courseName ?? "");
    document.getElementById("mcPoints").value = String(course.totalPoints ?? 1000);
    document.getElementById("modalCourseHeading").textContent = "Edit course";
    document.getElementById("btnCourseSubmit").textContent = "Save";
    openModal(elements.modalCourse);
}

function resetAssignmentModal() {
    const hid = document.getElementById("maEditId");
    if (hid) hid.value = "";
    if (elements.formAssignment) elements.formAssignment.reset();
    const mp = document.getElementById("maPriority");
    if (mp) mp.value = "2";
    const mposs = document.getElementById("maPossible");
    if (mposs) mposs.value = "100";
    const h = document.getElementById("modalAssignmentHeading");
    if (h) h.textContent = "New assignment / exam";
    const b = document.getElementById("btnAssignmentSubmit");
    if (b) b.textContent = "Create";
}

function openAssignmentModalForEdit(item) {
    resetAssignmentModal();
    document.getElementById("maEditId").value = String(item.assignmentId ?? "");
    populateCourseSelect(item.courseId);
    document.getElementById("maTitle").value = normalizeDisplayText(item.title ?? "");
    const desc = document.getElementById("maDesc");
    if (desc) desc.value = normalizeDisplayText(item.description ?? "");
    const due = document.getElementById("maDue");
    if (due) {
        const raw = item.dueDate;
        due.value = raw != null && String(raw).length >= 10 ? String(raw).slice(0, 10) : "";
    }
    const pr = document.getElementById("maPriority");
    if (pr) pr.value = String(item.priority ?? 2);
    const po = document.getElementById("maPossible");
    if (po) po.value = String(item.pointsPossible ?? 100);
    const pe = document.getElementById("maEarned");
    if (pe) pe.value = item.pointsEarned != null ? String(item.pointsEarned) : "";
    const maType = document.getElementById("maType");
    if (maType) maType.value = item.assignmentType ?? "ASSIGNMENT";
    const done = document.getElementById("maDone");
    if (done) done.checked = item.status === true;
    document.getElementById("modalAssignmentHeading").textContent = "Edit assignment";
    document.getElementById("btnAssignmentSubmit").textContent = "Save";
    openModal(elements.modalAssignment);
}

async function doLogout() {
    const res = await apiFetch("/api/auth/logout", { method: "POST" });
    if (res.ok || res.status === 204 || res.status === 401) {
        window.location.href = "/login.html";
    } else {
        window.location.href = "/login.html";
    }
}

if (elements.refreshBtn) elements.refreshBtn.addEventListener("click", loadAllData);
if (elements.logoutBtn) elements.logoutBtn.addEventListener("click", doLogout);
if (elements.btnAddCourse) {
    elements.btnAddCourse.addEventListener("click", () => {
        resetCourseModal();
        openModal(elements.modalCourse);
    });
}
if (elements.btnAddAssignment) {
    elements.btnAddAssignment.addEventListener("click", () => {
        if (!state.courses.length) {
            alert("Create a course first.");
            return;
        }
        resetAssignmentModal();
        populateCourseSelect();
        openModal(elements.modalAssignment);
    });
}

if (elements.coursesBody) {
    elements.coursesBody.addEventListener("click", e => {
        const edBtn = e.target.closest("button[data-edit-course]");
        if (!edBtn) return;
        const id = Number(edBtn.getAttribute("data-id"));
        const course = state.courses.find(c => c.courseId === id);
        if (course) openCourseModalForEdit(course);
    });
}

if (elements.assignmentsBody) {
    elements.assignmentsBody.addEventListener("click", e => {
        const delBtn = e.target.closest('button[data-del="assignment"]');
        if (delBtn) {
            const id = Number(delBtn.getAttribute("data-id"));
            if (id) deleteAssignment(id);
            return;
        }
        const edBtn = e.target.closest("button[data-edit-assignment]");
        if (edBtn) {
            const id = Number(edBtn.getAttribute("data-id"));
            const item = state.assignments.find(a => a.assignmentId === id);
            if (item) openAssignmentModalForEdit(item);
        }
    });
}

document.querySelectorAll(".modal [data-close]").forEach(el => {
    el.addEventListener("click", () => closeModal(el.closest(".modal")));
});

function populateCourseSelect(selectedCourseId) {
    if (!elements.maCourse) return;
    elements.maCourse.innerHTML = state.courses
        .map(c => `<option value="${c.courseId}">${escapeHtml(c.courseName)} (${c.courseId})</option>`)
        .join("");
    if (selectedCourseId != null && selectedCourseId !== "") {
        elements.maCourse.value = String(selectedCourseId);
    }
}

if (elements.formCourse) {
    elements.formCourse.addEventListener("submit", async e => {
        e.preventDefault();
        const courseName = normalizeDisplayText(document.getElementById("mcName").value.trim());
        const totalPoints = Number(document.getElementById("mcPoints").value);
        const editId = document.getElementById("mcEditId").value.trim();
        const url = editId ? `${endpoints.courses}/${editId}` : endpoints.courses;
        const method = editId ? "PUT" : "POST";
        const res = await apiFetch(url, {
            method,
            body: JSON.stringify({ courseName, totalPoints })
        });
        if (res.status === 401) {
            window.location.replace("/login.html");
            return;
        }
        if (!res.ok) {
            alert(editId ? "Could not update course." : "Could not create course.");
            return;
        }
        closeModal(elements.modalCourse);
        resetCourseModal();
        await loadAllData();
    });
}

if (elements.formAssignment) {
    elements.formAssignment.addEventListener("submit", async e => {
        e.preventDefault();
        const courseId = Number(elements.maCourse.value);
        const title = normalizeDisplayText(document.getElementById("maTitle").value.trim());
        const description = normalizeDisplayText(
            (document.getElementById("maDesc") && document.getElementById("maDesc").value.trim()) || ""
        );
        const dueDate = document.getElementById("maDue").value;
        const priority = Number(document.getElementById("maPriority").value) || 2;
        const pointsPossible = Number(document.getElementById("maPossible").value);
        const earnedRaw = document.getElementById("maEarned").value;
        const pointsEarned = earnedRaw === "" ? null : Number(earnedRaw);
        const assignmentType = document.getElementById("maType").value;
        const status = document.getElementById("maDone").checked;

        const body = {
            courseId,
            title,
            description,
            dueDate,
            priority,
            pointsPossible,
            pointsEarned,
            status,
            assignmentType
        };

        const editId = document.getElementById("maEditId").value.trim();
        const url = editId ? `${endpoints.assignments}/${editId}` : endpoints.assignments;
        const method = editId ? "PUT" : "POST";

        const res = await apiFetch(url, {
            method,
            body: JSON.stringify(body)
        });
        if (res.status === 401) {
            window.location.replace("/login.html");
            return;
        }
        if (!res.ok) {
            alert(editId ? "Could not update assignment." : "Could not create assignment.");
            return;
        }
        closeModal(elements.modalAssignment);
        resetAssignmentModal();
        await loadAllData();
    });
}

elements.statusFilter?.addEventListener("change", renderAssignments);
elements.searchInput?.addEventListener("input", renderAssignments);

elements.viewTabs.forEach(tab => {
    tab.addEventListener("click", () => {
        const view = tab.getAttribute("data-view");
        if (view === "dashboard" || view === "calendar") setView(view);
    });
});

if (elements.calPrevBtn) {
    elements.calPrevBtn.addEventListener("click", () => shiftCalendarMonth(-1));
}
if (elements.calNextBtn) {
    elements.calNextBtn.addEventListener("click", () => shiftCalendarMonth(1));
}
if (elements.calTodayBtn) {
    elements.calTodayBtn.addEventListener("click", goCalendarToday);
}

(async function bootstrap() {
    try {
        const me = await apiFetch(endpoints.me);
        if (me.status === 401) {
            window.location.replace("/login.html");
            return;
        }
        if (!me.ok) {
            if (elements.userGreeting) {
                elements.userGreeting.textContent =
                    "Could not load your session. Try signing in again (use the same host as the app, e.g. http://localhost:8080).";
            }
            setStatus(`Session error (${me.status}).`, true);
            return;
        }
        state.currentUser = await me.json();
        updateUserHeader();
        const dataOk = await loadAllData();
        setView(dataOk ? "calendar" : "dashboard");
    } catch (err) {
        console.error(err);
        if (elements.userGreeting) {
            elements.userGreeting.textContent =
                "Something went wrong loading your account. Refresh the page or sign in again.";
        }
        setStatus(err instanceof Error ? err.message : "Unexpected error", true);
    }
})();
