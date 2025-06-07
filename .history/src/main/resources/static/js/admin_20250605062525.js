// Admin dashboard functionality
let currentSection = "overview"

document.addEventListener("DOMContentLoaded", () => {
  loadUsers()
  loadCourses()
  loadStudentsAndCourses()
})

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
      loadEnrollments()
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
                        <td>
                            <span class="status-badge ${user.active ? "active" : "inactive"}">
                                ${user.active ? "Active" : "Inactive"}
                            </span>
                        </td>
                        <td>
                            <button class="btn btn-secondary" onclick="editUser(${user.id})">
                                <i class="fas fa-edit"></i> Edit
                            </button>
                            ${
                              user.active
                                ? `<button class="btn btn-warning" onclick="deactivateUser(${user.id})">
                                    <i class="fas fa-ban"></i> Deactivate
                                </button>`
                                : `<button class="btn btn-success" onclick="activateUser(${user.id})">
                                    <i class="fas fa-check"></i> Activate
                                </button>`
                            }
                            <button class="btn btn-danger" onclick="deleteUser(${user.id})">
                                <i class="fas fa-trash"></i> Delete
                            </button>
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
                            <span><i class="fas fa-credit-card"></i> Credits: ${course.credits}</span>
                            <span><i class="fas fa-building"></i> Department: ${course.department}</span>
                        </div>
                        <div class="course-info">
                            <span><i class="fas fa-user"></i> Professor: ${course.professor ? course.professor.fullName : "Not assigned"}</span>
                            <span><i class="fas fa-users"></i> Capacity: ${course.capacity || "Unlimited"}</span>
                        </div>
                        <div class="course-info">
                            <span><i class="fas fa-calendar"></i> ${course.semester} ${course.academicYear}</span>
                            <span class="status-badge ${course.active ? "active" : "inactive"}">
                                ${course.active ? "Active" : "Inactive"}
                            </span>
                        </div>
                        <div class="course-actions" style="margin-top: 15px;">
                            <button class="btn btn-secondary" onclick="editCourse(${course.id})">
                                <i class="fas fa-edit"></i> Edit
                            </button>
                            ${
                              course.active
                                ? `<button class="btn btn-warning" onclick="deactivateCourse(${course.id})">
                                    <i class="fas fa-ban"></i> Deactivate
                                </button>`
                                : `<button class="btn btn-success" onclick="activateCourse(${course.id})">
                                    <i class="fas fa-check"></i> Activate
                                </button>`
                            }
                            <button class="btn btn-danger" onclick="deleteCourse(${course.id})">
                                <i class="fas fa-trash"></i> Delete
                            </button>
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

function loadEnrollments() {
  fetch("/admin/api/enrollments")
    .then((response) => response.json())
    .then((enrollments) => {
      // You can add a table to display enrollments if needed
      console.log("Enrollments loaded:", enrollments)
    })
    .catch((error) => {
      console.error("Error loading enrollments:", error)
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
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Add User
                </button>
                <button type="button" class="btn btn-secondary" onclick="closeModal()">
                    <i class="fas fa-times"></i> Cancel
                </button>
            </div>
        </form>
    `

  document.getElementById("modal").style.display = "block"

  document.getElementById("addUserForm").addEventListener("submit", (e) => {
    e.preventDefault()
    const formData = new FormData(e.target)
    const userData = Object.fromEntries(formData)
    userData.active = true

    fetch("/admin/api/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("User added successfully")
        closeModal()
        loadUsers()
      })
      .catch((error) => {
        showToast("Error adding user: " + error.message, "error")
      })
  })
}

function editUser(userId) {
  fetch(`/admin/api/users`)
    .then((response) => response.json())
    .then((users) => {
      const user = users.find((u) => u.id === userId)
      if (!user) {
        showToast("User not found", "error")
        return
      }

      const modalBody = document.getElementById("modalBody")
      modalBody.innerHTML = `
        <h3>Edit User</h3>
        <form id="editUserForm">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" value="${user.username}" required>
            </div>
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" name="fullName" value="${user.fullName}" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${user.email}" required>
            </div>
            <div class="form-group">
                <label>Password (leave blank to keep current)</label>
                <input type="password" name="password">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="role" required>
                    <option value="ROLE_STUDENT" ${user.role === "ROLE_STUDENT" ? "selected" : ""}>Student</option>
                    <option value="ROLE_PROFESSOR" ${user.role === "ROLE_PROFESSOR" ? "selected" : ""}>Professor</option>
                    <option value="ROLE_ADMIN" ${user.role === "ROLE_ADMIN" ? "selected" : ""}>Admin</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Update User
                </button>
                <button type="button" class="btn btn-secondary" onclick="closeModal()">
                    <i class="fas fa-times"></i> Cancel
                </button>
            </div>
        </form>
      `

      document.getElementById("modal").style.display = "block"

      document.getElementById("editUserForm").addEventListener("submit", (e) => {
        e.preventDefault()
        const formData = new FormData(e.target)
        const userData = Object.fromEntries(formData)
        userData.active = user.active

        fetch(`/admin/api/users/${userId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(userData),
        })
          .then((response) => response.json())
          .then((data) => {
            if (data.error) {
              throw new Error(data.error)
            }
            showToast("User updated successfully")
            closeModal()
            loadUsers()
          })
          .catch((error) => {
            showToast("Error updating user: " + error.message, "error")
          })
      })
    })
}

function activateUser(userId) {
  if (confirm("Are you sure you want to activate this user?")) {
    fetch(`/admin/api/users/${userId}/activate`, {
      method: "PUT",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("User activated successfully")
        loadUsers()
      })
      .catch((error) => {
        showToast("Error activating user: " + error.message, "error")
      })
  }
}

function deactivateUser(userId) {
  if (confirm("Are you sure you want to deactivate this user?")) {
    fetch(`/admin/api/users/${userId}/deactivate`, {
      method: "PUT",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("User deactivated successfully")
        loadUsers()
      })
      .catch((error) => {
        showToast("Error deactivating user: " + error.message, "error")
      })
  }
}

function deleteUser(userId) {
  if (confirm("Are you sure you want to delete this user? This action cannot be undone.")) {
    fetch(`/admin/api/users/${userId}`, {
      method: "DELETE",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("User deleted successfully")
        loadUsers()
      })
      .catch((error) => {
        showToast("Error deleting user: " + error.message, "error")
      })
  }
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
                    <div class="form-row">
                        <div class="form-group">
                            <label>Course Code</label>
                            <input type="text" name="courseCode" required>
                        </div>
                        <div class="form-group">
                            <label>Credits</label>
                            <input type="number" name="credits" min="1" max="6" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Title</label>
                        <input type="text" name="title" required>
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea name="description" rows="3"></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Department</label>
                            <select name="department" required>
                                <option value="">Select Department</option>
                                <option value="Computer Science">Computer Science</option>
                                <option value="Mathematics">Mathematics</option>
                                <option value="Physics">Physics</option>
                                <option value="Chemistry">Chemistry</option>
                                <option value="Biology">Biology</option>
                                <option value="Engineering">Engineering</option>
                                <option value="Business">Business</option>
                                <option value="Literature">Literature</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Professor</label>
                            <select name="professor.id">
                                <option value="">Select Professor</option>
                                ${professors.map((prof) => `<option value="${prof.id}">${prof.fullName}</option>`).join("")}
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Capacity</label>
                            <input type="number" name="capacity" min="1" max="200">
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
                    </div>
                    <div class="form-group">
                        <label>Academic Year</label>
                        <input type="text" name="academicYear" placeholder="2023-2024" required>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Add Course
                        </button>
                        <button type="button" class="btn btn-secondary" onclick="closeModal()">
                            <i class="fas fa-times"></i> Cancel
                        </button>
                    </div>
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
          } else if (key !== "professor.id") {
            courseData[key] = value
          }
        }

        // Convert numeric fields
        if (courseData.credits) courseData.credits = Number.parseInt(courseData.credits)
        if (courseData.capacity) courseData.capacity = Number.parseInt(courseData.capacity)
        courseData.active = true

        fetch("/admin/api/courses", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(courseData),
        })
          .then((response) => response.json())
          .then((data) => {
            if (data.error) {
              throw new Error(data.error)
            }
            showToast("Course added successfully")
            closeModal()
            loadCourses()
          })
          .catch((error) => {
            showToast("Error adding course: " + error.message, "error")
          })
      })
    })
}

function editCourse(courseId) {
  Promise.all([
    fetch("/admin/api/courses").then((r) => r.json()),
    fetch("/admin/api/users/professor").then((r) => r.json()),
  ]).then(([courses, professors]) => {
    const course = courses.find((c) => c.id === courseId)
    if (!course) {
      showToast("Course not found", "error")
      return
    }

    const modalBody = document.getElementById("modalBody")
    modalBody.innerHTML = `
      <h3>Edit Course</h3>
      <form id="editCourseForm">
          <div class="form-row">
              <div class="form-group">
                  <label>Course Code</label>
                  <input type="text" name="courseCode" value="${course.courseCode}" required>
              </div>
              <div class="form-group">
                  <label>Credits</label>
                  <input type="number" name="credits" value="${course.credits}" min="1" max="6" required>
              </div>
          </div>
          <div class="form-group">
              <label>Title</label>
              <input type="text" name="title" value="${course.title}" required>
          </div>
          <div class="form-group">
              <label>Description</label>
              <textarea name="description" rows="3">${course.description || ""}</textarea>
          </div>
          <div class="form-row">
              <div class="form-group">
                  <label>Department</label>
                  <select name="department" required>
                      <option value="Computer Science" ${course.department === "Computer Science" ? "selected" : ""}>Computer Science</option>
                      <option value="Mathematics" ${course.department === "Mathematics" ? "selected" : ""}>Mathematics</option>
                      <option value="Physics" ${course.department === "Physics" ? "selected" : ""}>Physics</option>
                      <option value="Chemistry" ${course.department === "Chemistry" ? "selected" : ""}>Chemistry</option>
                      <option value="Biology" ${course.department === "Biology" ? "selected" : ""}>Biology</option>
                      <option value="Engineering" ${course.department === "Engineering" ? "selected" : ""}>Engineering</option>
                      <option value="Business" ${course.department === "Business" ? "selected" : ""}>Business</option>
                      <option value="Literature" ${course.department === "Literature" ? "selected" : ""}>Literature</option>
                  </select>
              </div>
              <div class="form-group">
                  <label>Professor</label>
                  <select name="professor.id">
                      <option value="">Select Professor</option>
                      ${professors
                        .map(
                          (prof) =>
                            `<option value="${prof.id}" ${course.professor && course.professor.id === prof.id ? "selected" : ""}>${prof.fullName}</option>`,
                        )
                        .join("")}
                  </select>
              </div>
          </div>
          <div class="form-row">
              <div class="form-group">
                  <label>Capacity</label>
                  <input type="number" name="capacity" value="${course.capacity || ""}" min="1" max="200">
              </div>
              <div class="form-group">
                  <label>Semester</label>
                  <select name="semester" required>
                      <option value="Fall" ${course.semester === "Fall" ? "selected" : ""}>Fall</option>
                      <option value="Spring" ${course.semester === "Spring" ? "selected" : ""}>Spring</option>
                      <option value="Summer" ${course.semester === "Summer" ? "selected" : ""}>Summer</option>
                  </select>
              </div>
          </div>
          <div class="form-group">
              <label>Academic Year</label>
              <input type="text" name="academicYear" value="${course.academicYear}" required>
          </div>
          <div class="form-actions">
              <button type="submit" class="btn btn-primary">
                  <i class="fas fa-save"></i> Update Course
              </button>
              <button type="button" class="btn btn-secondary" onclick="closeModal()">
                  <i class="fas fa-times"></i> Cancel
              </button>
          </div>
      </form>
    `

    document.getElementById("modal").style.display = "block"

    document.getElementById("editCourseForm").addEventListener("submit", (e) => {
      e.preventDefault()
      const formData = new FormData(e.target)
      const courseData = {}

      // Handle nested professor object
      for (const [key, value] of formData.entries()) {
        if (key === "professor.id" && value) {
          courseData.professor = { id: Number.parseInt(value) }
        } else if (key !== "professor.id") {
          courseData[key] = value
        }
      }

      // Convert numeric fields
      if (courseData.credits) courseData.credits = Number.parseInt(courseData.credits)
      if (courseData.capacity) courseData.capacity = Number.parseInt(courseData.capacity)
      courseData.active = course.active

      fetch(`/admin/api/courses/${courseId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(courseData),
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.error) {
            throw new Error(data.error)
          }
          showToast("Course updated successfully")
          closeModal()
          loadCourses()
        })
        .catch((error) => {
          showToast("Error updating course: " + error.message, "error")
        })
    })
  })
}

function activateCourse(courseId) {
  if (confirm("Are you sure you want to activate this course?")) {
    fetch(`/admin/api/courses/${courseId}/activate`, {
      method: "PUT",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("Course activated successfully")
        loadCourses()
      })
      .catch((error) => {
        showToast("Error activating course: " + error.message, "error")
      })
  }
}

function deactivateCourse(courseId) {
  if (confirm("Are you sure you want to deactivate this course?")) {
    fetch(`/admin/api/courses/${courseId}/deactivate`, {
      method: "PUT",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("Course deactivated successfully")
        loadCourses()
      })
      .catch((error) => {
        showToast("Error deactivating course: " + error.message, "error")
      })
  }
}

function deleteCourse(courseId) {
  if (confirm("Are you sure you want to delete this course? This action cannot be undone.")) {
    fetch(`/admin/api/courses/${courseId}`, {
      method: "DELETE",
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.error) {
          throw new Error(data.error)
        }
        showToast("Course deleted successfully")
        loadCourses()
      })
      .catch((error) => {
        showToast("Error deleting course: " + error.message, "error")
      })
  }
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
        },
        body: `studentId=${studentId}&courseId=${courseId}`,
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.error) {
            throw new Error(data.error)
          }
          showToast("Student enrolled successfully")
          enrollmentForm.reset()
          loadEnrollments()
        })
        .catch((error) => {
          showToast("Error enrolling student: " + error.message, "error")
        })
    })
  }
})

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

// Add CSS for status badges
const style = document.createElement("style")
style.textContent = `
  .status-badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
  }
  .status-badge.active {
    background-color: #d4edda;
    color: #155724;
  }
  .status-badge.inactive {
    background-color: #f8d7da;
    color: #721c24;
  }
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
  }
  .form-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    margin-top: 20px;
  }
  .course-actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  .course-actions .btn {
    font-size: 12px;
    padding: 6px 12px;
  }
`
document.head.appendChild(style)
