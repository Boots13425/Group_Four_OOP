// Admin dashboard functionality
let currentSection = "overview"
let csrfToken = null

document.addEventListener("DOMContentLoaded", () => {
  // Get CSRF token from meta tag
  const csrfMeta = document.querySelector('meta[name="_csrf"]')
  if (csrfMeta) {
    csrfToken = csrfMeta.getAttribute("content")
  }

  loadUsers()
  loadCourses()
  loadStudentsAndCourses()
})

// Add CSRF token to all AJAX requests
function addCsrfToRequest(request) {
  if (csrfToken) {
    if (request.headers) {
      request.headers["X-CSRF-TOKEN"] = csrfToken
    } else {
      request.headers = {
        "X-CSRF-TOKEN": csrfToken,
      }
    }
  }
  return request
}

function showAdminSection(section) {
  // Hide all sections
  document.querySelectorAll(".content-section").forEach((s) => {
    s.classList.remove("active")
  })

  // Show selected section
  document.getElementById("admin" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  // Update menu
  document.querySelectorAll(".admin-menu a").forEach((a) => {
    a.classList.remove("active")
  })
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  currentSection = section

  // Load section-specific data
  switch (section) {
    case "users":
      loadUsers()
      break
    case "courses":
      loadCourses()
      break
    case "enrollment":
      loadStudentsAndCourses()
      break
  }
}

function loadUsers() {
  fetch("/admin/api/users")
    .then((response) => response.json())
    .then((users) => {
      const tbody = document.getElementById("usersTableBody")
      if (tbody) {
        tbody.innerHTML = users
          .map(
            (user) => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.fullName}</td>
                        <td>${user.email}</td>
                        <td>${user.role.replace("ROLE_", "")}</td>
                        <td>${user.active ? "Active" : "Inactive"}</td>
                        <td>
                            <button class="btn btn-secondary" onclick="editUser(${user.id})">Edit</button>
                            <button class="btn btn-danger" onclick="deleteUser(${user.id})">Delete</button>
                        </td>
                    </tr>
                `,
          )
          .join("")
      }
    })
    .catch((error) => {
      console.error("Error loading users:", error)
      showToast("Error loading users", "error")
    })
}

function loadCourses() {
  fetch("/admin/api/courses")
    .then((response) => response.json())
    .then((courses) => {
      const grid = document.getElementById("coursesGrid")
      if (grid) {
        grid.innerHTML = courses
          .map(
            (course) => `
                    <div class="course-card">
                        <h4>${course.courseCode} - ${course.title}</h4>
                        <p>${course.description || "No description"}</p>
                        <div class="course-info">
                            <span>Credits: ${course.credits}</span>
                            <span>Department: ${course.department}</span>
                            <span>Professor: ${course.professor ? course.professor.fullName : "Not assigned"}</span>
                        </div>
                        <div class="course-actions">
                            <button class="btn btn-secondary" onclick="editCourse(${course.id})">Edit</button>
                            <button class="btn btn-danger" onclick="deleteCourse(${course.id})">Delete</button>
                        </div>
                    </div>
                `,
          )
          .join("")
      }
    })
    .catch((error) => {
      console.error("Error loading courses:", error)
      showToast("Error loading courses", "error")
    })
}

function loadStudentsAndCourses() {
  // Load students for enrollment dropdown
  fetch("/admin/api/users/student")
    .then((response) => response.json())
    .then((students) => {
      const select = document.getElementById("enrollStudent")
      if (select) {
        select.innerHTML =
          '<option value="">Select Student</option>' +
          students.map((student) => `<option value="${student.id}">${student.fullName}</option>`).join("")
      }
    })

  // Load courses for enrollment dropdown
  fetch("/admin/api/courses/active")
    .then((response) => response.json())
    .then((courses) => {
      const select = document.getElementById("enrollCourse")
      if (select) {
        select.innerHTML =
          '<option value="">Select Course</option>' +
          courses
            .map((course) => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
            .join("")
      }
    })
}

function showAddUserModal() {
  const modalBody = document.getElementById("modalBody")
  modalBody.innerHTML = `
        <h3>Add New User</h3>
        <form id="addUserForm">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" required>
            </div>
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" name="fullName" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required>
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="role" required>
                    <option value="">Select Role</option>
                    <option value="ROLE_STUDENT">Student</option>
                    <option value="ROLE_PROFESSOR">Professor</option>
                    <option value="ROLE_ADMIN">Admin</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary">Add User</button>
        </form>
    `

  document.getElementById("modal").style.display = "block"

  document.getElementById("addUserForm").addEventListener("submit", (e) => {
    e.preventDefault()
    const formData = new FormData(e.target)
    const userData = Object.fromEntries(formData)

    fetch("/admin/api/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify(userData),
    })
      .then((response) => {
        if (response.ok) {
          showToast("User added successfully")
          closeModal()
          loadUsers()
        } else {
          return response.text().then((text) => {
            throw new Error(text)
          })
        }
      })
      .catch((error) => {
        showToast("Error adding user: " + error.message, "error")
      })
  })
}

function showAddCourseModal() {
  // Load professors for the dropdown
  fetch("/admin/api/users/professor")
    .then((response) => response.json())
    .then((professors) => {
      const modalBody = document.getElementById("modalBody")
      modalBody.innerHTML = `
                <h3>Add New Course</h3>
                <form id="addCourseForm">
                    <input type="hidden" name="_csrf" value="${csrfToken}">
                    <div class="form-group">
                        <label>Course Code</label>
                        <input type="text" name="courseCode" required>
                    </div>
                    <div class="form-group">
                        <label>Title</label>
                        <input type="text" name="title" required>
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea name="description"></textarea>
                    </div>
                    <div class="form-group">
                        <label>Credits</label>
                        <input type="number" name="credits" min="1" max="6" required>
                    </div>
                    <div class="form-group">
                        <label>Department</label>
                        <select name="department" required>
                            <option value="">Select Department</option>
                            <option value="Computer Science">Computer Science</option>
                            <option value="Mathematics">Mathematics</option>
                            <option value="Physics">Physics</option>
                            <option value="Chemistry">Chemistry</option>
                            <option value="Biology">Biology</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Professor</label>
                        <select name="professor.id">
                            <option value="">Select Professor</option>
                            ${professors.map((prof) => `<option value="${prof.id}">${prof.fullName}</option>`).join("")}
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Capacity</label>
                        <input type="number" name="capacity" min="1" max="100">
                    </div>
                    <div class="form-group">
                        <label>Semester</label>
                        <select name="semester" required>
                            <option value="">Select Semester</option>
                            <option value="Fall">Fall</option>
                            <option value="Spring">Spring</option>
                            <option value="Summer">Summer</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Academic Year</label>
                        <input type="text" name="academicYear" placeholder="2023-2024" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Add Course</button>
                </form>
            `

      document.getElementById("modal").style.display = "block"

      document.getElementById("addCourseForm").addEventListener("submit", (e) => {
        e.preventDefault()
        const formData = new FormData(e.target)
        const courseData = {}

        // Handle nested professor object
        for (const [key, value] of formData.entries()) {
          if (key === "professor.id" && value) {
            courseData.professor = { id: Number.parseInt(value) }
          } else if (key !== "professor.id" && key !== "_csrf") {
            courseData[key] = value
          }
        }

        // Convert numeric fields
        if (courseData.credits) courseData.credits = Number.parseInt(courseData.credits)
        if (courseData.capacity) courseData.capacity = Number.parseInt(courseData.capacity)

        fetch("/admin/api/courses", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken,
          },
          body: JSON.stringify(courseData),
        })
          .then((response) => {
            if (response.ok) {
              showToast("Course added successfully")
              closeModal()
              loadCourses()
            } else {
              throw new Error("Failed to add course")
            }
          })
          .catch((error) => {
            showToast("Error adding course: " + error.message, "error")
          })
      })
    })
}

// Handle manual enrollment form
document.addEventListener("DOMContentLoaded", () => {
  const enrollmentForm = document.getElementById("manualEnrollmentForm")
  if (enrollmentForm) {
    enrollmentForm.addEventListener("submit", (e) => {
      e.preventDefault()
      const studentId = document.getElementById("enrollStudent").value
      const courseId = document.getElementById("enrollCourse").value

      if (!studentId || !courseId) {
        showToast("Please select both student and course", "error")
        return
      }

      fetch("/admin/api/enrollments", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          "X-CSRF-TOKEN": csrfToken,
        },
        body: `studentId=${studentId}&courseId=${courseId}`,
      })
        .then((response) => {
          if (response.ok) {
            showToast("Student enrolled successfully")
            enrollmentForm.reset()
          } else {
            return response.text().then((text) => {
              throw new Error(text)
            })
          }
        })
        .catch((error) => {
          showToast("Error enrolling student: " + error.message, "error")
        })
    })
  }
})

function editUser(userId) {
  showToast("Edit user functionality not implemented yet", "info")
}

function deleteUser(userId) {
  if (confirm("Are you sure you want to delete this user?")) {
    fetch(`/admin/api/users/${userId}`, {
      method: "DELETE",
      headers: {
        "X-CSRF-TOKEN": csrfToken,
      },
    })
      .then((response) => {
        if (response.ok) {
          showToast("User deleted successfully")
          loadUsers()
        } else {
          throw new Error("Failed to delete user")
        }
      })
      .catch((error) => {
        showToast("Error deleting user: " + error.message, "error")
      })
  }
}

function editCourse(courseId) {
  showToast("Edit course functionality not implemented yet", "info")
}

function deleteCourse(courseId) {
  if (confirm("Are you sure you want to delete this course?")) {
    fetch(`/admin/api/courses/${courseId}`, {
      method: "DELETE",
      headers: {
        "X-CSRF-TOKEN": csrfToken,
      },
    })
      .then((response) => {
        if (response.ok) {
          showToast("Course deleted successfully")
          loadCourses()
        } else {
          throw new Error("Failed to delete course")
        }
      })
      .catch((error) => {
        showToast("Error deleting course: " + error.message, "error")
      })
  }
}

function showToast(message, type = "success") {
  const toast = document.getElementById("toast")
  if (toast) {
    toast.textContent = message
    toast.className = `toast ${type}`
    toast.style.display = "block"

    setTimeout(() => {
      toast.style.display = "none"
    }, 3000)
  }
}

function closeModal() {
  const modal = document.getElementById("modal")
  if (modal) {
    modal.style.display = "none"
  }
}
