// --- Toast Helper ---
function showToast(message, type = "info", duration = 3100) {
  const toast = document.getElementById("toast");
  toast.className = "toast " + type;
  toast.textContent = message;
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), duration);
}

// --- Logout ---
function logout() {
  fetch('/api/logout', { method: 'POST', credentials: 'include' })
    .then(() => {
      showToast("Logging out...", "info");
      setTimeout(() => window.location.href = "login.html", 1000);
    });
}

// --- Section Navigation ---
function showAdminSection(section) {
  document.querySelectorAll('.content-section').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.admin-menu a').forEach(link => link.classList.remove('active'));
  const sectionMap = {
    overview: "adminOverview",
    users: "adminUsers",
    courses: "adminCourses",
    enrollment: "adminEnrollment",
    policy: "adminPolicy",
    config: "adminConfig"
  };
  document.getElementById(sectionMap[section]).classList.add('active');
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add('active');
  // Load section data
  if (section === "overview") loadOverview();
  if (section === "users") loadUsers();
  if (section === "courses") loadCourses();
  if (section === "enrollment") loadEnrollment();
  if (section === "policy") loadPolicy();
  if (section === "config") loadConfigTab('general');
}

// --- Overview Data ---
async function loadOverview() {
  try {
    const res = await fetch('/api/admin/overview', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load overview');
    const data = await res.json();
    document.getElementById("totalStudents").textContent = data.students || 0;
    document.getElementById("totalProfessors").textContent = data.professors || 0;
    document.getElementById("activeCourses").textContent = data.courses || 0;
    document.getElementById("averageGpa").textContent = data.averageGpa || "0.0";
    // System status
    const grid = document.getElementById("systemStatusGrid");
    grid.innerHTML = "";
    (data.status || []).forEach(s => {
      grid.innerHTML += `
        <div class="status-item">
          <i class="fas fa-${s.icon} status-${s.status.toLowerCase()}"></i>
          <span>${s.label}: ${s.status}</span>
        </div>
      `;
    });
    document.getElementById("welcomeMessage").textContent = `Welcome, ${data.adminName || "Administrator"}`;
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Users ---
async function loadUsers() {
  try {
    const role = document.getElementById("userRoleFilter").value;
    const search = document.getElementById("userSearch").value;
    let url = '/api/admin/users';
    const params = [];
    if (role) params.push(`role=${encodeURIComponent(role)}`);
    if (search) params.push(`search=${encodeURIComponent(search)}`);
    if (params.length) url += "?" + params.join("&");
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load users');
    const users = await res.json();
    const tbody = document.getElementById("usersTableBody");
    tbody.innerHTML = "";
    users.forEach(user => {
      tbody.innerHTML += `
        <tr>
          <td>${user.id}</td>
          <td>${user.name}</td>
          <td>${user.email}</td>
          <td><span class="role ${user.role}">${capitalize(user.role)}</span></td>
          <td><span class="status ${user.status.toLowerCase()}">${capitalize(user.status)}</span></td>
          <td>
            <button class="btn btn-sm btn-primary" onclick="editUser('${user.id}')">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteUser('${user.id}')">
              <i class="fas fa-trash"></i>
            </button>
          </td>
        </tr>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

// User actions (modals, editing, deleting)
function showAddUserModal() { showToast("Show add user modal", "info"); }
function editUser(userId) { showToast("Edit user: " + userId, "info"); }
function deleteUser(userId) { showToast("Deleted user: " + userId, "success"); }

// --- Courses ---
async function loadCourses() {
  try {
    const dept = document.getElementById("departmentFilter").value;
    const search = document.getElementById("courseSearchAdmin").value;
    let url = '/api/admin/courses';
    const params = [];
    if (dept) params.push(`department=${encodeURIComponent(dept)}`);
    if (search) params.push(`search=${encodeURIComponent(search)}`);
    if (params.length) url += "?" + params.join("&");
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load courses');
    const courses = await res.json();
    const grid = document.getElementById("coursesGrid");
    grid.innerHTML = "";
    courses.forEach(course => {
      grid.innerHTML += `
        <div class="course-card-admin">
          <h4>${course.title}</h4>
          <p><strong>Code:</strong> ${course.code}</p>
          <p><strong>Credits:</strong> ${course.credits}</p>
          <p><strong>Professor:</strong> ${course.professor}</p>
          <p><strong>Enrolled:</strong> ${course.enrolled}/${course.capacity}</p>
          <div class="course-actions">
            <button class="btn btn-sm btn-primary" onclick="editCourse('${course.code}')">
              <i class="fas fa-edit"></i> Edit
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteCourse('${course.code}')">
              <i class="fas fa-trash"></i> Delete
            </button>
          </div>
        </div>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}
function showAddCourseModal() { showToast("Show add course modal", "info"); }
function editCourse(courseId) { showToast("Edit course: " + courseId, "info"); }
function deleteCourse(courseId) { showToast("Deleted course: " + courseId, "success"); }

// --- Enrollment ---
async function loadEnrollment() {
  try {
    // Manual enrollment students/courses
    const studentsRes = await fetch('/api/admin/students', { credentials: 'include' });
    const coursesRes = await fetch('/api/admin/courses', { credentials: 'include' });
    if (!studentsRes.ok || !coursesRes.ok) throw new Error('Failed to load enrollment data');
    const students = await studentsRes.json();
    const courses = await coursesRes.json();
    const enrollStudent = document.getElementById("enrollStudent");
    enrollStudent.innerHTML = '<option value="">Select Student</option>';
    students.forEach(s => {
      enrollStudent.innerHTML += `<option value="${s.id}">${s.name}</option>`;
    });
    const enrollCourse = document.getElementById("enrollCourse");
    enrollCourse.innerHTML = '<option value="">Select Course</option>';
    courses.forEach(c => {
      enrollCourse.innerHTML += `<option value="${c.code}">${c.title}</option>`;
    });
    // Enrollment stats
    const statsRes = await fetch('/api/admin/enrollment-stats', { credentials: 'include' });
    if (!statsRes.ok) throw new Error('Failed to load enrollment stats');
    const stats = await statsRes.json();
    const tbody = document.getElementById("enrollmentStatsBody");
    tbody.innerHTML = "";
    stats.forEach(row => {
      tbody.innerHTML += `
        <tr>
          <td>${row.course}</td>
          <td>${row.capacity}</td>
          <td>${row.enrolled}</td>
          <td>${row.available}</td>
          <td>${row.waitlist}</td>
        </tr>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

function bulkEnrollment() {
  const fileInput = document.getElementById("enrollmentFile");
  if (!fileInput.files.length) {
    showToast("Please select a CSV file.", "error");
    return;
  }
  const formData = new FormData();
  formData.append("file", fileInput.files[0]);
  fetch('/api/admin/bulk-enrollment', {
    method: 'POST',
    credentials: 'include',
    body: formData
  })
    .then(res => {
      if (!res.ok) throw new Error('Bulk enrollment failed');
      showToast("Bulk enrollment uploaded!", "success");
      loadEnrollment();
    })
    .catch(e => showToast(e.message, "error"));
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("manualEnrollmentForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const studentId = document.getElementById("enrollStudent").value;
    const courseCode = document.getElementById("enrollCourse").value;
    if (!studentId || !courseCode) {
      showToast("Select student and course.", "error");
      return;
    }
    fetch('/api/admin/enroll', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ studentId, courseCode })
    })
      .then(res => {
        if (!res.ok) throw new Error('Manual enrollment failed');
        showToast("Student enrolled!", "success");
        loadEnrollment();
      })
      .catch(e => showToast(e.message, "error"));
  });
});

// --- Grades Policy ---
async function loadPolicy() {
  try {
    const res = await fetch('/api/admin/policy', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load policy');
    const data = await res.json();
    // Grade scale
    const scaleDiv = document.getElementById("gradeScaleInputs");
    scaleDiv.innerHTML = "";
    (data.gradeScale || []).forEach(range => {
      scaleDiv.innerHTML += `
        <div class="grade-range">
          <label>${range.label}: </label>
          <input type="number" value="${range.min}" min="${range.min}" max="${range.max}" /> - ${range.max}
        </div>
      `;
    });
    // GPA settings
    document.getElementById("includePlusMinus").checked = data.includePlusMinus;
    document.getElementById("weightByCredits").checked = data.weightByCredits;
    document.getElementById("includeTransfer").checked = data.includeTransfer;
    // Submission settings
    document.getElementById("midtermDeadline").value = data.midtermDeadline;
    document.getElementById("finalDeadline").value = data.finalDeadline;
    document.getElementById("automaticReminders").checked = data.automaticReminders;
  } catch (e) {
    showToast(e.message, "error");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("policyForm").addEventListener("submit", function(e) {
    e.preventDefault();
    // Gather settings
    const gradeScale = Array.from(document.querySelectorAll("#gradeScaleInputs .grade-range")).map(div => ({
      label: div.querySelector("label").innerText.replace(":", ""),
      min: parseInt(div.querySelector("input").value),
      max: parseInt(div.innerText.split('-').pop())
    }));
    const includePlusMinus = document.getElementById("includePlusMinus").checked;
    const weightByCredits = document.getElementById("weightByCredits").checked;
    const includeTransfer = document.getElementById("includeTransfer").checked;
    const midtermDeadline = document.getElementById("midtermDeadline").value;
    const finalDeadline = document.getElementById("finalDeadline").value;
    const automaticReminders = document.getElementById("automaticReminders").checked;
    fetch('/api/admin/policy', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        gradeScale, includePlusMinus, weightByCredits, includeTransfer, midtermDeadline, finalDeadline, automaticReminders
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to save policy');
        showToast("Grades policy saved!", "success");
      })
      .catch(e => showToast(e.message, "error"));
  });
});

// --- System Config Tabs ---
function showConfigTab(tab) {
  document.querySelectorAll('.config-tab').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.config-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
  if (tab === 'general') {
    document.getElementById('generalConfig').classList.add('active');
    document.querySelectorAll('.config-tabs .tab-btn')[0].classList.add('active');
    loadConfigTab('general');
  } else if (tab === 'security') {
    document.getElementById('securityConfig').classList.add('active');
    document.querySelectorAll('.config-tabs .tab-btn')[1].classList.add('active');
    loadConfigTab('security');
  } else if (tab === 'backup') {
    document.getElementById('backupConfig').classList.add('active');
    document.querySelectorAll('.config-tabs .tab-btn')[2].classList.add('active');
    loadConfigTab('backup');
  }
}

function loadConfigTab(tab) {
  if (tab === "general") loadGeneralConfig();
  if (tab === "security") loadSecurityConfig();
  if (tab === "backup") loadBackupConfig();
}

// --- General Settings ---
async function loadGeneralConfig() {
  try {
    const res = await fetch('/api/admin/config/general', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load general config');
    const data = await res.json();
    document.getElementById("universityName").value = data.universityName || "";
    const academicYear = document.getElementById("academicYear");
    academicYear.innerHTML = "";
    (data.academicYears || []).forEach(y => {
      academicYear.innerHTML += `<option${y === data.selectedAcademicYear ? " selected" : ""}>${y}</option>`;
    });
    const currentSemester = document.getElementById("currentSemester");
    currentSemester.innerHTML = "";
    (data.semesters || []).forEach(s => {
      currentSemester.innerHTML += `<option${s === data.selectedSemester ? " selected" : ""}>${s}</option>`;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("generalConfigForm").addEventListener("submit", function(e) {
    e.preventDefault();
    fetch('/api/admin/config/general', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        universityName: document.getElementById("universityName").value,
        academicYear: document.getElementById("academicYear").value,
        currentSemester: document.getElementById("currentSemester").value
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to save general config');
        showToast("Configuration saved!", "success");
      })
      .catch(e => showToast(e.message, "error"));
  });
});

// --- Security Settings ---
async function loadSecurityConfig() {
  try {
    const res = await fetch('/api/admin/config/security', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load security config');
    const data = await res.json();
    const checks = document.getElementById("passwordPolicyChecks");
    checks.innerHTML = "";
    (data.passwordPolicy || []).forEach(p => {
      checks.innerHTML += `
        <label>
          <input type="checkbox" ${p.enabled ? "checked" : ""} /> ${p.label}
        </label>
      `;
    });
    document.getElementById("sessionTimeout").value = data.sessionTimeout || 30;
    document.getElementById("failedLogins").value = data.failedLogins || 5;
  } catch (e) {
    showToast(e.message, "error");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("securityConfigForm").addEventListener("submit", function(e) {
    e.preventDefault();
    // Gather password policy
    const checks = Array.from(document.querySelectorAll("#passwordPolicyChecks input[type='checkbox']"))
      .map((input, i) => ({ label: input.parentElement.textContent.trim(), enabled: input.checked }));
    fetch('/api/admin/config/security', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        passwordPolicy: checks,
        sessionTimeout: document.getElementById("sessionTimeout").value,
        failedLogins: document.getElementById("failedLogins").value
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to save security config');
        showToast("Configuration saved!", "success");
      })
      .catch(e => showToast(e.message, "error"));
  });
});

// --- Backup Settings ---
async function loadBackupConfig() {
  try {
    const res = await fetch('/api/admin/config/backup', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load backup config');
    const data = await res.json();
    const freq = document.getElementById("backupFrequency");
    freq.innerHTML = "";
    (data.frequencies || []).forEach(f => {
      freq.innerHTML += `<option${f === data.selectedFrequency ? " selected" : ""}>${f}</option>`;
    });
    document.getElementById("backupTime").value = data.backupTime || "02:00";
    document.getElementById("retentionPeriod").value = data.retentionPeriod || 30;
  } catch (e) {
    showToast(e.message, "error");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("backupConfigForm").addEventListener("submit", function(e) {
    e.preventDefault();
    fetch('/api/admin/config/backup', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        backupFrequency: document.getElementById("backupFrequency").value,
        backupTime: document.getElementById("backupTime").value,
        retentionPeriod: document.getElementById("retentionPeriod").value
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to save backup config');
        showToast("Configuration saved!", "success");
      })
      .catch(e => showToast(e.message, "error"));
  });
});
function runBackup() {
  fetch('/api/admin/run-backup', { method: 'POST', credentials: 'include' })
    .then(res => {
      if (!res.ok) throw new Error('Backup failed');
      showToast("Backup started!", "info");
    })
    .catch(e => showToast(e.message, "error"));
}

// --- Modal ---
function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// --- Initial ---
document.addEventListener("DOMContentLoaded", () => {
  // Search/filter events
  document.getElementById("userRoleFilter").onchange = loadUsers;
  document.getElementById("userSearch").oninput = loadUsers;
  document.getElementById("departmentFilter").onchange = loadCourses;
  document.getElementById("courseSearchAdmin").oninput = loadCourses;
  showAdminSection('overview');
});