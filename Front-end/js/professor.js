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

// --- Section Navigation ---
function showProfessorSection(section) {
  document.querySelectorAll('.content-section').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.professor-menu a').forEach(link => link.classList.remove('active'));
  const sectionMap = {
    overview: "professorOverview",
    upload: "professorUpload",
    update: "professorUpdate",
    gpa: "professorGpa",
    notifications: "professorNotifications"
  };
  document.getElementById(sectionMap[section]).classList.add('active');
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add('active');
  // Load data for the section
  if (section === "overview") loadProfessorOverview();
  if (section === "upload") loadUploadCourses();
  if (section === "update") loadUpdateMarks();
  if (section === "gpa") loadGPAStudents();
  if (section === "notifications") loadNotificationCourses();
}

// --- Professor Overview Data ---
async function loadProfessorOverview() {
  try {
    const res = await fetch('/api/professor/overview', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load overview');
    const data = await res.json();
    document.getElementById("welcomeMessage").textContent = `Welcome, ${data.name || 'Professor'}`;
    document.getElementById("totalStudents").textContent = data.totalStudents || '0';
    document.getElementById("coursesTeaching").textContent = data.coursesTeaching || '0';
    document.getElementById("gradesSubmitted").textContent = (data.gradesSubmitted || 0) + "%";
    // Render courses
    const courseList = document.getElementById("courseList");
    courseList.innerHTML = "";
    (data.courses || []).forEach(c => {
      courseList.innerHTML += `
        <div class="course-item">
          <h4>${c.title}</h4>
          <p>${c.studentCount} students enrolled</p>
          <div class="course-actions">
            <button class="btn btn-sm btn-primary" onclick="viewStudents('${c.id}')">
              View Students
            </button>
            <button class="btn btn-sm btn-secondary" onclick="showGradeBook('${c.id}')">Grade Book</button>
          </div>
        </div>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Upload Marks: Load Courses ---
async function loadUploadCourses() {
  try {
    const res = await fetch('/api/professor/courses', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load courses');
    const courses = await res.json();
    const courseSelect = document.getElementById("courseSelect");
    courseSelect.innerHTML = '<option value="">Choose Course</option>';
    courses.forEach(c => {
      courseSelect.innerHTML += `<option value="${c.id}">${c.title}</option>`;
    });
    // Clear manual entries
    document.getElementById("manualGradeEntries").innerHTML = "";
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Handle Upload CSV ---
async function uploadCSV() {
  const courseId = document.getElementById("courseSelect").value;
  const assignmentType = document.getElementById("assignmentType").value;
  const fileInput = document.getElementById("csvFile");
  if (!courseId || !assignmentType || !fileInput.files.length) {
    showToast("Please select course, assignment type and choose a file.", "error");
    return;
  }
  const formData = new FormData();
  formData.append("courseId", courseId);
  formData.append("assignmentType", assignmentType);
  formData.append("file", fileInput.files[0]);
  try {
    const res = await fetch('/api/professor/upload-marks-csv', {
      method: 'POST',
      credentials: 'include',
      body: formData
    });
    if (!res.ok) throw new Error('CSV upload failed');
    showToast("CSV file uploaded and processed!", "success");
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Manual Grade Entry ---
function addGradeEntry() {
  const container = document.getElementById("manualGradeEntries");
  const entry = document.createElement('div');
  entry.className = "grade-entry";
  entry.innerHTML = `
    <input type="text" placeholder="Student ID" class="student-id" />
    <input type="number" placeholder="Grade" class="grade-input" min="0" max="100" />
    <button type="button" class="btn btn-sm btn-danger" onclick="this.parentNode.remove()"><i class="fas fa-trash"></i></button>
  `;
  container.appendChild(entry);
}

// --- Save Manual Grades ---
document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("marksUploadForm").addEventListener("submit", async function (e) {
    e.preventDefault();
    const courseId = document.getElementById("courseSelect").value;
    const assignmentType = document.getElementById("assignmentType").value;
    const entries = Array.from(document.querySelectorAll("#manualGradeEntries .grade-entry"));
    const grades = entries.map(entry => ({
      studentId: entry.querySelector(".student-id").value,
      grade: entry.querySelector(".grade-input").value
    })).filter(g => g.studentId && g.grade);
    if (!courseId || !assignmentType || !grades.length) {
      showToast("Please fill all fields and add at least one grade.", "error");
      return;
    }
    try {
      const res = await fetch('/api/professor/upload-marks-manual', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ courseId, assignmentType, grades })
      });
      if (!res.ok) throw new Error('Failed to save manual grades');
      showToast("Grades saved successfully!", "success");
      document.getElementById("manualGradeEntries").innerHTML = "";
    } catch (e) {
      showToast(e.message, "error");
    }
  });
});

// --- Update Marks: Load Courses and Students ---
async function loadUpdateMarks() {
  try {
    // Load courses for filter
    const coursesRes = await fetch('/api/professor/courses', { credentials: 'include' });
    if (!coursesRes.ok) throw new Error('Failed to load courses');
    const courses = await coursesRes.json();
    const courseFilter = document.getElementById("courseFilter");
    courseFilter.innerHTML = '<option value="">All Courses</option>';
    courses.forEach(c => {
      courseFilter.innerHTML += `<option value="${c.id}">${c.title}</option>`;
    });

    // Load students (optionally filter)
    await loadUpdateMarksStudents();

    // Add filter event
    courseFilter.onchange = loadUpdateMarksStudents;
    document.getElementById("studentSearch").oninput = loadUpdateMarksStudents;
  } catch (e) {
    showToast(e.message, "error");
  }
}

async function loadUpdateMarksStudents() {
  const courseId = document.getElementById("courseFilter").value;
  const query = document.getElementById("studentSearch").value;
  let url = '/api/professor/students';
  const params = [];
  if (courseId) params.push(`courseId=${encodeURIComponent(courseId)}`);
  if (query) params.push(`search=${encodeURIComponent(query)}`);
  if (params.length) url += "?" + params.join("&");
  try {
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load students');
    const students = await res.json();
    const tbody = document.getElementById("studentsTableBody");
    tbody.innerHTML = "";
    students.forEach(s => {
      tbody.innerHTML += `
        <tr>
          <td>${s.id}</td>
          <td>${s.name}</td>
          <td>${s.course}</td>
          <td>${s.grade}</td>
          <td>
            <button class="btn btn-sm btn-primary" onclick="editGrade('${s.id}')">
              <i class="fas fa-edit"></i> Edit
            </button>
          </td>
        </tr>
      `;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Edit Grade Modal (Simple prompt for demo) ---
function editGrade(studentId) {
  const newGrade = prompt("Enter new grade:");
  if (newGrade === null) return;
  // Send to backend
  fetch('/api/professor/update-grade', {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ studentId, grade: newGrade })
  })
    .then(res => {
      if (!res.ok) throw new Error('Failed to update grade');
      showToast("Grade updated!", "success");
      loadUpdateMarksStudents();
    })
    .catch(e => showToast(e.message, "error"));
}

// --- GPA Calculator: Load Students ---
async function loadGPAStudents() {
  try {
    const res = await fetch('/api/professor/students', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load students');
    const students = await res.json();
    const gpaStudent = document.getElementById("gpaStudent");
    gpaStudent.innerHTML = '<option value="">Choose Student</option>';
    students.forEach(s => {
      gpaStudent.innerHTML += `<option value="${s.id}">${s.name} (${s.id})</option>`;
    });
    document.getElementById("gpaResults").style.display = "none";
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- GPA Calculation ---
async function calculateGPA() {
  const studentId = document.getElementById("gpaStudent").value;
  const semester = document.getElementById("gpaSemester").value;
  if (!studentId) {
    showToast("Select a student.", "error");
    return;
  }
  try {
    const res = await fetch(`/api/professor/calculate-gpa?studentId=${studentId}&semester=${semester}`, { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to calculate GPA');
    const data = await res.json();
    document.getElementById("gpaValue").textContent = data.gpa || "0.00";
    // Breakdown
    const breakdown = data.breakdown || [];
    const breakdownContainer = document.getElementById("breakdownContainer");
    breakdownContainer.innerHTML = `<h5>Grade Breakdown</h5>`;
    breakdown.forEach(item => {
      breakdownContainer.innerHTML += `
        <div class="breakdown-item">
          <span>${item.label}</span>
          <span>${item.creditHours} credit hours</span>
        </div>
      `;
    });
    document.getElementById("gpaResults").style.display = "block";
    showToast("GPA calculated!", "success");
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Notifications: Load Courses ---
async function loadNotificationCourses() {
  try {
    const res = await fetch('/api/professor/courses', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to load courses');
    const courses = await res.json();
    const notificationCourse = document.getElementById("notificationCourse");
    notificationCourse.innerHTML = '<option value="">Choose Course</option>';
    courses.forEach(c => {
      notificationCourse.innerHTML += `<option value="${c.id}">${c.title}</option>`;
    });
  } catch (e) {
    showToast(e.message, "error");
  }
}

// --- Send Notification ---
document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("notificationForm").addEventListener("submit", async function (e) {
    e.preventDefault();
    const courseId = document.getElementById("notificationCourse").value;
    const notificationType = document.getElementById("notificationType").value;
    const message = document.getElementById("notificationMessage").value;
    const recipients = document.querySelector('input[name="recipients"]:checked').value;
    if (!courseId || !notificationType || !message) {
      showToast("Please fill all fields.", "error");
      return;
    }
    try {
      const res = await fetch('/api/professor/send-notification', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ courseId, notificationType, message, recipients })
      });
      if (!res.ok) throw new Error('Failed to send notification');
      showToast("Notification sent!", "success");
      document.getElementById("notificationMessage").value = "";
    } catch (e) {
      showToast(e.message, "error");
    }
  });
});

// --- Modal ---
function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// --- Course Actions ---
function viewStudents(courseId) {
  // You may open a modal or navigate to a students list page
  showToast(`View students for course ${courseId}`, "info");
}
function showGradeBook(courseId) {
  // Open modal or navigate to grade book
  showToast(`Open grade book for course ${courseId}`, "info");
}

// --- On DOM Ready ---
document.addEventListener("DOMContentLoaded", () => {
  showProfessorSection('overview');
});