// Global state management
let currentUser = null
let currentRole = null

// Demo users for authentication
const demoUsers = {
  student: { username: "student", password: "password", role: "student", name: "NgwaAgbor" },
  professor: { username: "professor", password: "password", role: "professor", name: "Dr. Smith" },
  admin: { username: "admin", password: "password", role: "admin", name: "Administrator" },
}

// Sample data
const sampleData = {
  courses: [
    { id: "cs101", name: "Computer Science 101", professor: "Dr. Smith", credits: 3, enrolled: 45, capacity: 50 },
    { id: "cs301", name: "Advanced Programming", professor: "Dr. Johnson", credits: 4, enrolled: 38, capacity: 40 },
    { id: "cs350", name: "Database Systems", professor: "Dr. Davis", credits: 4, enrolled: 32, capacity: 35 },
  ],
  students: [
    { id: "STU001", name: "NgwaAgbor", email: "Ngwa.agbor@university.edu", gpa: 3.7 },
    { id: "STU002", name: "Sirri Babash", email: "sirri.babash@university.edu", gpa: 3.9 },
  ],
  grades: [
    { studentId: "STU001", courseId: "cs101", grade: "A", points: 4.0 },
    { studentId: "STU001", courseId: "math201", grade: "B+", points: 3.3 },
    { studentId: "STU002", courseId: "cs101", grade: "A-", points: 3.7 },
  ],
}

// Initialize the application
document.addEventListener("DOMContentLoaded", () => {
  initializeApp()
  setupEventListeners()
})

function initializeApp() {
  // Check if user is already logged in
  const savedUser = localStorage.getItem("currentUser")
  if (savedUser) {
    currentUser = JSON.parse(savedUser)
    currentRole = currentUser.role
    showDashboard(currentRole)
  } else {
    showScreen("loginScreen")
  }
}

function setupEventListeners() {
  // Login form submission
  const loginForm = document.getElementById("loginForm")
  if (loginForm) {
    loginForm.addEventListener("submit", handleLogin)
  }

  // Real-time form validation
  const usernameInput = document.getElementById("username")
  const passwordInput = document.getElementById("password")

  if (usernameInput) {
    usernameInput.addEventListener("input", validateUsername)
  }

  if (passwordInput) {
    passwordInput.addEventListener("input", validatePassword)
  }

  // Search functionality
  setupSearchListeners()

  // Keyboard shortcuts
  document.addEventListener("keydown", handleKeyboardShortcuts)
}

function handleLogin(event) {
  event.preventDefault()

  const username = document.getElementById("username").value.trim()
  const password = document.getElementById("password").value
  const userRole = document.getElementById("userRole").value

  // Clear previous errors
  clearErrors()

  // Validate inputs
  if (!validateLoginForm(username, password, userRole)) {
    return
  }

  // Check credentials
  const user = demoUsers[userRole]
  if (user && user.username === username && user.password === password) {
    currentUser = user
    currentRole = userRole

    // Save to localStorage
    localStorage.setItem("currentUser", JSON.stringify(user))

    // Show success message
    showToast("Login successful!", "success")

    // Navigate to appropriate dashboard
    setTimeout(() => {
      showDashboard(userRole)
    }, 1000)
  } else {
    showError("Invalid credentials. Please try again.")
  }
}

function validateLoginForm(username, password, userRole) {
  let isValid = true

  if (!username) {
    showFieldError("usernameError", "Username is required")
    isValid = false
  }

  if (!password) {
    showFieldError("passwordError", "Password is required")
    isValid = false
  }

  if (!userRole) {
    showError("Please select a user role")
    isValid = false
  }

  return isValid
}

function validateUsername() {
  const username = document.getElementById("username").value.trim()
  const errorElement = document.getElementById("usernameError")

  if (username.length < 3) {
    errorElement.textContent = "Username must be at least 3 characters"
    return false
  } else {
    errorElement.textContent = ""
    return true
  }
}

function validatePassword() {
  const password = document.getElementById("password").value
  const errorElement = document.getElementById("passwordError")

  if (password.length < 6) {
    errorElement.textContent = "Password must be at least 6 characters"
    return false
  } else {
    errorElement.textContent = ""
    return true
  }
}

function showFieldError(elementId, message) {
  const errorElement = document.getElementById(elementId)
  if (errorElement) {
    errorElement.textContent = message
  }
}

function clearErrors() {
  const errorElements = document.querySelectorAll(".error-message")
  errorElements.forEach((element) => {
    element.textContent = ""
  })
}

function showError(message) {
  showToast(message, "error")
}

function showDashboard(role) {
  hideAllScreens()

  switch (role) {
    case "student":
      showScreen("studentDashboard")
      break
    case "professor":
      showScreen("professorDashboard")
      break
    case "admin":
      showScreen("adminDashboard")
      break
  }
}

function showScreen(screenId) {
  hideAllScreens()
  const screen = document.getElementById(screenId)
  if (screen) {
    screen.classList.add("active")
  }
}

function hideAllScreens() {
  const screens = document.querySelectorAll(".screen")
  screens.forEach((screen) => {
    screen.classList.remove("active")
  })
}

function logout() {
  if (confirm("Are you sure you want to logout?")) {
    currentUser = null
    currentRole = null
    localStorage.removeItem("currentUser")
    showScreen("loginScreen")
    showToast("Logged out successfully", "success")

    // Reset forms
    const loginForm = document.getElementById("loginForm")
    if (loginForm) {
      loginForm.reset()
    }
  }
}

// Student Dashboard Functions
function showStudentSection(sectionId) {
  hideAllContentSections()
  const section = document.getElementById("student" + capitalizeFirst(sectionId))
  if (section) {
    section.classList.add("active")
  }
  updateActiveNavLink("student", sectionId)
}

function registerCourse(courseId) {
  showConfirmModal("Course Registration", `Are you sure you want to register for this course?`, () => {
    showToast("Successfully registered for course!", "success")
    closeModal()
  })
}

function showSemester(semester) {
  // Update tab appearance
  const tabs = document.querySelectorAll(".tab-btn")
  tabs.forEach((tab) => tab.classList.remove("active"))
  event.target.classList.add("active")

  // Here you would typically load different semester data
  showToast(`Showing ${semester} semester data`, "info")
}

function exportData(format) {
  showToast(`Exporting data as ${format.toUpperCase()}...`, "info")

  // Simulate export process
  setTimeout(() => {
    showToast(`${format.toUpperCase()} export completed!`, "success")

    // Create a dummy download
    const link = document.createElement("a")
    link.href = "#"
    link.download = `grades.${format}`
    link.click()
  }, 2000)
}

// Professor Dashboard Functions
function showProfessorSection(sectionId) {
  hideAllContentSections()
  const section = document.getElementById("professor" + capitalizeFirst(sectionId))
  if (section) {
    section.classList.add("active")
  }
  updateActiveNavLink("professor", sectionId)
}

function uploadCSV() {
  const fileInput = document.getElementById("csvFile")
  const courseSelect = document.getElementById("courseSelect")
  const assignmentType = document.getElementById("assignmentType")

  if (!fileInput.files[0]) {
    showToast("Please select a CSV file", "error")
    return
  }

  if (!courseSelect.value || !assignmentType.value) {
    showToast("Please select course and assignment type", "error")
    return
  }

  showToast("Uploading grades...", "info")

  // Simulate upload process
  setTimeout(() => {
    showToast("Grades uploaded successfully!", "success")
  }, 2000)
}

function addGradeEntry() {
  const container = document.querySelector(".student-grades")
  const newEntry = document.createElement("div")
  newEntry.className = "grade-entry"
  newEntry.innerHTML = `
        <input type="text" placeholder="Student ID" class="student-id">
        <input type="number" placeholder="Grade" class="grade-input" min="0" max="100">
        <button type="button" class="btn btn-sm btn-danger" onclick="removeGradeEntry(this)">
            <i class="fas fa-trash"></i>
        </button>
    `
  container.appendChild(newEntry)
}

function removeGradeEntry(button) {
  button.parentElement.remove()
}

function editGrade(studentId) {
  showModal(
    "Edit Grade",
    `
        <form onsubmit="updateGrade('${studentId}', event)">
            <div class="form-group">
                <label>Student ID: ${studentId}</label>
            </div>
            <div class="form-group">
                <label for="newGrade">New Grade</label>
                <input type="number" id="newGrade" min="0" max="100" required>
            </div>
            <div class="form-group">
                <label for="gradeComment">Comment (optional)</label>
                <textarea id="gradeComment" rows="3"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Update Grade</button>
        </form>
    `,
  )
}

function updateGrade(studentId, event) {
  event.preventDefault()
  const newGrade = document.getElementById("newGrade").value
  showToast(`Grade updated for student ${studentId}`, "success")
  closeModal()
}

function calculateGPA() {
  const student = document.getElementById("gpaStudent").value
  const semester = document.getElementById("gpaSemester").value

  if (!student) {
    showToast("Please select a student", "error")
    return
  }

  // Simulate GPA calculation
  const resultsDiv = document.getElementById("gpaResults")
  resultsDiv.style.display = "block"

  showToast("GPA calculated successfully!", "success")
}

// Admin Dashboard Functions
function showAdminSection(sectionId) {
  hideAllContentSections()
  const section = document.getElementById("admin" + capitalizeFirst(sectionId))
  if (section) {
    section.classList.add("active")
  }
  updateActiveNavLink("admin", sectionId)
}

function showAddUserModal() {
  showModal(
    "Add New User",
    `
        <form onsubmit="addUser(event)">
            <div class="form-group">
                <label for="newUserName">Full Name</label>
                <input type="text" id="newUserName" required>
            </div>
            <div class="form-group">
                <label for="newUserEmail">Email</label>
                <input type="email" id="newUserEmail" required>
            </div>
            <div class="form-group">
                <label for="newUserRole">Role</label>
                <select id="newUserRole" required>
                    <option value="">Select Role</option>
                    <option value="student">Student</option>
                    <option value="professor">Professor</option>
                    <option value="admin">Administrator</option>
                </select>
            </div>
            <div class="form-group">
                <label for="newUserPassword">Temporary Password</label>
                <input type="password" id="newUserPassword" required>
            </div>
            <button type="submit" class="btn btn-primary">Add User</button>
        </form>
    `,
  )
}

function addUser(event) {
  event.preventDefault()
  const name = document.getElementById("newUserName").value
  const email = document.getElementById("newUserEmail").value
  const role = document.getElementById("newUserRole").value

  showToast(`User ${name} added successfully!`, "success")
  closeModal()
}

function editUser(userId) {
  showModal(
    "Edit User",
    `
        <form onsubmit="updateUser('${userId}', event)">
            <div class="form-group">
                <label for="editUserName">Full Name</label>
                <input type="text" id="editUserName" value="Current Name" required>
            </div>
            <div class="form-group">
                <label for="editUserEmail">Email</label>
                <input type="email" id="editUserEmail" value="current@email.com" required>
            </div>
            <div class="form-group">
                <label for="editUserStatus">Status</label>
                <select id="editUserStatus" required>
                    <option value="active">Active</option>
                    <option value="inactive">Inactive</option>
                    <option value="suspended">Suspended</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary">Update User</button>
        </form>
    `,
  )
}

function updateUser(userId, event) {
  event.preventDefault()
  showToast(`User ${userId} updated successfully!`, "success")
  closeModal()
}

function deleteUser(userId) {
  showConfirmModal(
    "Delete User",
    `Are you sure you want to delete user ${userId}? This action cannot be undone.`,
    () => {
      showToast(`User ${userId} deleted successfully!`, "success")
      closeModal()
    },
  )
}

function showAddCourseModal() {
  showModal(
    "Add New Course",
    `
        <form onsubmit="addCourse(event)">
            <div class="form-group">
                <label for="newCourseName">Course Name</label>
                <input type="text" id="newCourseName" required>
            </div>
            <div class="form-group">
                <label for="newCourseCode">Course Code</label>
                <input type="text" id="newCourseCode" required>
            </div>
            <div class="form-group">
                <label for="newCourseCredits">Credits</label>
                <input type="number" id="newCourseCredits" min="1" max="6" required>
            </div>
            <div class="form-group">
                <label for="newCourseProfessor">Professor</label>
                <select id="newCourseProfessor" required>
                    <option value="">Select Professor</option>
                    <option value="prof1">Dr. Smith</option>
                    <option value="prof2">Dr. Johnson</option>
                </select>
            </div>
            <div class="form-group">
                <label for="newCourseCapacity">Capacity</label>
                <input type="number" id="newCourseCapacity" min="1" required>
            </div>
            <button type="submit" class="btn btn-primary">Add Course</button>
        </form>
    `,
  )
}

function addCourse(event) {
  event.preventDefault()
  const courseName = document.getElementById("newCourseName").value
  showToast(`Course "${courseName}" added successfully!`, "success")
  closeModal()
}

function editCourse(courseId) {
  showModal(
    "Edit Course",
    `
        <form onsubmit="updateCourse('${courseId}', event)">
            <div class="form-group">
                <label for="editCourseName">Course Name</label>
                <input type="text" id="editCourseName" value="Current Course Name" required>
            </div>
            <div class="form-group">
                <label for="editCourseCapacity">Capacity</label>
                <input type="number" id="editCourseCapacity" value="50" min="1" required>
            </div>
            <div class="form-group">
                <label for="editCourseStatus">Status</label>
                <select id="editCourseStatus" required>
                    <option value="active">Active</option>
                    <option value="inactive">Inactive</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary">Update Course</button>
        </form>
    `,
  )
}

function updateCourse(courseId, event) {
  event.preventDefault()
  showToast(`Course ${courseId} updated successfully!`, "success")
  closeModal()
}

function deleteCourse(courseId) {
  showConfirmModal(
    "Delete Course",
    `Are you sure you want to delete course ${courseId}? This will affect all enrolled students.`,
    () => {
      showToast(`Course ${courseId} deleted successfully!`, "success")
      closeModal()
    },
  )
}

function bulkEnrollment() {
  const fileInput = document.getElementById("enrollmentFile")

  if (!fileInput.files[0]) {
    showToast("Please select a CSV file", "error")
    return
  }

  showToast("Processing bulk enrollment...", "info")

  setTimeout(() => {
    showToast("Bulk enrollment completed successfully!", "success")
  }, 3000)
}

function showConfigTab(tabName) {
  // Hide all config tabs
  const tabs = document.querySelectorAll(".config-tab")
  tabs.forEach((tab) => tab.classList.remove("active"))

  // Show selected tab
  const selectedTab = document.getElementById(tabName + "Config")
  if (selectedTab) {
    selectedTab.classList.add("active")
  }

  // Update tab buttons
  const tabButtons = document.querySelectorAll(".config-tabs .tab-btn")
  tabButtons.forEach((btn) => btn.classList.remove("active"))
  event.target.classList.add("active")
}

function runBackup() {
  showToast("Starting system backup...", "info")

  setTimeout(() => {
    showToast("System backup completed successfully!", "success")
  }, 5000)
}

// Utility Functions
function hideAllContentSections() {
  const sections = document.querySelectorAll(".content-section")
  sections.forEach((section) => {
    section.classList.remove("active")
  })
}

function updateActiveNavLink(role, sectionId) {
  const navLinks = document.querySelectorAll(`#${role}Dashboard .nav-menu a`)
  navLinks.forEach((link) => {
    link.classList.remove("active")
  })

  const activeLink = document.querySelector(`#${role}Dashboard .nav-menu a[onclick*="${sectionId}"]`)
  if (activeLink) {
    activeLink.classList.add("active")
  }
}

function capitalizeFirst(str) {
  return str.charAt(0).toUpperCase() + str.slice(1)
}

function showToast(message, type = "info") {
  const toast = document.getElementById("toast")
  toast.textContent = message
  toast.className = `toast show ${type}`

  // Auto hide after 3 seconds
  setTimeout(() => {
    toast.classList.remove("show")
  }, 3000)
}

function showModal(title, content) {
  const modal = document.getElementById("modal")
  const modalBody = document.getElementById("modalBody")

  modalBody.innerHTML = `
        <h3>${title}</h3>
        ${content}
    `

  modal.style.display = "block"

  // Close modal when clicking outside
  modal.onclick = (event) => {
    if (event.target === modal) {
      closeModal()
    }
  }
}

function showConfirmModal(title, message, onConfirm) {
  const modal = document.getElementById("modal")
  const modalBody = document.getElementById("modalBody")

  modalBody.innerHTML = `
        <h3>${title}</h3>
        <p>${message}</p>
        <div style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
            <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button class="btn btn-danger" onclick="confirmAction()">Confirm</button>
        </div>
    `

  modal.style.display = "block"

  // Store the confirm action
  window.confirmAction = () => {
    onConfirm()
  }
}

function closeModal() {
  const modal = document.getElementById("modal")
  modal.style.display = "none"

  // Clean up confirm action
  if (window.confirmAction) {
    delete window.confirmAction
  }
}

function setupSearchListeners() {
  // Course search
  const courseSearch = document.getElementById("courseSearch")
  if (courseSearch) {
    courseSearch.addEventListener("input", function () {
      const searchTerm = this.value.toLowerCase()
      filterCourses(searchTerm)
    })
  }

  // Student search
  const studentSearch = document.getElementById("studentSearch")
  if (studentSearch) {
    studentSearch.addEventListener("input", function () {
      const searchTerm = this.value.toLowerCase()
      filterStudents(searchTerm)
    })
  }

  // User search
  const userSearch = document.getElementById("userSearch")
  if (userSearch) {
    userSearch.addEventListener("input", function () {
      const searchTerm = this.value.toLowerCase()
      filterUsers(searchTerm)
    })
  }
}

function filterCourses(searchTerm) {
  const courseCards = document.querySelectorAll(".course-card")
  courseCards.forEach((card) => {
    const courseName = card.querySelector("h4").textContent.toLowerCase()
    const courseDescription = card.querySelector("p").textContent.toLowerCase()

    if (courseName.includes(searchTerm) || courseDescription.includes(searchTerm)) {
      card.style.display = "block"
    } else {
      card.style.display = "none"
    }
  })
}

function filterStudents(searchTerm) {
  const studentRows = document.querySelectorAll(".students-table tbody tr")
  studentRows.forEach((row) => {
    const studentName = row.cells[1].textContent.toLowerCase()
    const studentId = row.cells[0].textContent.toLowerCase()

    if (studentName.includes(searchTerm) || studentId.includes(searchTerm)) {
      row.style.display = "table-row"
    } else {
      row.style.display = "none"
    }
  })
}

function filterUsers(searchTerm) {
  const userRows = document.querySelectorAll(".users-table tbody tr")
  userRows.forEach((row) => {
    const userName = row.cells[1].textContent.toLowerCase()
    const userEmail = row.cells[2].textContent.toLowerCase()
    const userId = row.cells[0].textContent.toLowerCase()

    if (userName.includes(searchTerm) || userEmail.includes(searchTerm) || userId.includes(searchTerm)) {
      row.style.display = "table-row"
    } else {
      row.style.display = "none"
    }
  })
}

function handleKeyboardShortcuts(event) {
  // Ctrl/Cmd + L for logout
  if ((event.ctrlKey || event.metaKey) && event.key === "l") {
    event.preventDefault()
    if (currentUser) {
      logout()
    }
  }

  // Escape to close modal
  if (event.key === "Escape") {
    closeModal()
  }

  // Ctrl/Cmd + S to save (prevent default browser save)
  if ((event.ctrlKey || event.metaKey) && event.key === "s") {
    event.preventDefault()
    showToast("Use the Save button to save changes", "info")
  }
}

// Form validation helpers
function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

function validateGrade(grade) {
  const numGrade = Number.parseFloat(grade)
  return !isNaN(numGrade) && numGrade >= 0 && numGrade <= 100
}

// Data persistence helpers
function saveToLocalStorage(key, data) {
  try {
    localStorage.setItem(key, JSON.stringify(data))
    return true
  } catch (error) {
    console.error("Error saving to localStorage:", error)
    return false
  }
}

function loadFromLocalStorage(key) {
  try {
    const data = localStorage.getItem(key)
    return data ? JSON.parse(data) : null
  } catch (error) {
    console.error("Error loading from localStorage:", error)
    return null
  }
}

// Initialize sample data if not exists
function initializeSampleData() {
  if (!loadFromLocalStorage("courses")) {
    saveToLocalStorage("courses", sampleData.courses)
  }
  if (!loadFromLocalStorage("students")) {
    saveToLocalStorage("students", sampleData.students)
  }
  if (!loadFromLocalStorage("grades")) {
    saveToLocalStorage("grades", sampleData.grades)
  }
}

// Call initialization
initializeSampleData()

// Export functions for global access
window.showStudentSection = showStudentSection
window.showProfessorSection = showProfessorSection
window.showAdminSection = showAdminSection
window.logout = logout
window.registerCourse = registerCourse
window.showSemester = showSemester
window.exportData = exportData
window.uploadCSV = uploadCSV
window.addGradeEntry = addGradeEntry
window.removeGradeEntry = removeGradeEntry
window.editGrade = editGrade
window.updateGrade = updateGrade
window.calculateGPA = calculateGPA
window.showAddUserModal = showAddUserModal
window.addUser = addUser
window.editUser = editUser
window.updateUser = updateUser
window.deleteUser = deleteUser
window.showAddCourseModal = showAddCourseModal
window.addCourse = addCourse
window.editCourse = editCourse
window.updateCourse = updateCourse
window.deleteCourse = deleteCourse
window.bulkEnrollment = bulkEnrollment
window.showConfigTab = showConfigTab
window.runBackup = runBackup
window.closeModal = closeModal
