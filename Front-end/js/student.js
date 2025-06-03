// --- Helper: Toast Notification ---
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

// --- Get Student Dashboard Data ---
async function loadStudentDashboard() {
  try {
    const res = await fetch('/api/student/dashboard', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load dashboard');
    const data = await res.json();

    // Update stats
    document.getElementById("enrolledCourses").textContent = data.enrolledCourses || 0;
    document.getElementById("currentGpa").textContent = data.gpa || "0.0";
    document.getElementById("averageGrade").textContent = (data.averageGrade || 0) + "%";
    // Update welcome message
    document.getElementById("welcomeMessage").textContent = `Welcome, ${data.name || "Student"}`;
    // Update recent grades
    const gradeList = document.getElementById("recentGradesList");
    gradeList.innerHTML = "";
    (data.recentGrades || []).forEach(g => {
      gradeList.innerHTML += `
        <div class="grade-item">
          <span class="course">${g.course}</span>
          <span class="grade ${formatGradeClass(g.grade)}">${g.grade}</span>
        </div>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Helper: Format grade class ---
function formatGradeClass(grade) {
  if (!grade) return "";
  if (grade.startsWith("A")) return "grade-a";
  if (grade.startsWith("B")) return "grade-b";
  if (grade.startsWith("C")) return "grade-c";
  return "";
}

// --- Get Courses for Registration ---
async function loadCourseRegistration(query = "") {
  try {
    let url = '/api/student/courses/available';
    if (query) url += `?search=${encodeURIComponent(query)}`;
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load courses');
    const courses = await res.json();
    const courseGrid = document.getElementById("courseGrid");
    courseGrid.innerHTML = "";
    courses.forEach(c => {
      courseGrid.innerHTML += `
        <div class="course-card">
          <h4>${c.title}</h4>
          <p>${c.code} - ${c.professor}</p>
          <p>Credits: ${c.credits} | Schedule: ${c.schedule}</p>
          <button class="btn btn-primary" onclick="registerCourse('${c.code}')">
            <i class="fas fa-plus"></i> Register
          </button>
        </div>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Register Course ---
async function registerCourse(courseCode) {
  try {
    const res = await fetch('/api/student/courses', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ courseCode })
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || 'Registration failed');
    }
    showToast(`Registered for ${courseCode}!`, "success");
    loadCourseRegistration();
    loadStudentDashboard();
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Get Grades ---
async function loadGrades(semester = "current") {
  try {
    const res = await fetch(`/api/student/grades?semester=${semester}`, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load grades');
    const grades = await res.json();
    const tbody = document.getElementById("gradesTableBody");
    tbody.innerHTML = "";
    grades.forEach(g => {
      tbody.innerHTML += `
        <tr>
          <td>${g.course}</td>
          <td>${g.credits}</td>
          <td><span class="grade ${formatGradeClass(g.grade)}">${g.grade}</span></td>
          <td>${g.points}</td>
          <td><span class="status ${g.status === 'Completed' ? 'completed' : 'in-progress'}">${g.status}</span></td>
        </tr>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Export Data ---
async function exportData(type) {
  try {
    const res = await fetch(`/api/student/export?type=${type}`, { credentials: 'include' });
    if (!res.ok) throw new Error('Export failed');
    const blob = await res.blob();
    // Download the file
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `grades.${type}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    showToast(`Exported grades as ${type.toUpperCase()}!`, "success");
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Section Navigation ---
function showStudentSection(section) {
  document.querySelectorAll('.content-section').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.student-menu a').forEach(link => link.classList.remove('active'));
  const sectionMap = {
    overview: "studentOverview",
    courses: "studentCourses",
    grades: "studentGrades",
    export: "studentExport"
  };
  document.getElementById(sectionMap[section]).classList.add('active');
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add('active');
  // Load data for the section
  if (section === "overview") loadStudentDashboard();
  if (section === "courses") loadCourseRegistration();
  if (section === "grades") loadGrades(currentSemesterTab);
}

let currentSemesterTab = "current";
function showSemester(tab) {
  document.querySelectorAll('.semester-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
  if (tab === 'current') {
    document.querySelector('.semester-tabs .tab-btn').classList.add('active');
  } else if (tab === 'previous') {
    document.querySelectorAll('.semester-tabs .tab-btn')[1].classList.add('active');
  }
  currentSemesterTab = tab;
  loadGrades(tab);
}

// --- Modal ---
function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// --- Course Search ---
document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("courseSearchBtn").addEventListener("click", function () {
    const query = document.getElementById("courseSearch").value;
    loadCourseRegistration(query);
  });
  document.getElementById("courseSearch").addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
      e.preventDefault();
      loadCourseRegistration(this.value);
    }
  });
  // Initial load
  showStudentSection('overview');
});