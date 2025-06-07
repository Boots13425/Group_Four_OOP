// Professor dashboard functionality
let currentSection = "overview"
let currentCourses = []
let currentStudents = []
let csrfToken = null

document.addEventListener("DOMContentLoaded", () => {
  // Get CSRF token from meta tag
  const csrfMeta = document.querySelector('meta[name="_csrf"]')
  if (csrfMeta) {
    csrfToken = csrfMeta.getAttribute("content")
  }

  loadProfessorCourses()
  setupFormHandlers()
})

function showProfessorSection(section) {
  // Hide all sections
  document.querySelectorAll(".content-section").forEach((s) => {
    s.classList.remove("active")
  })

  // Show selected section
  document.getElementById("professor" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  // Update menu
  document.querySelectorAll(".professor-menu a").forEach((a) => {
    a.classList.remove("active")
  })
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  currentSection = section

  // Load section-specific data
  switch (section) {
    case "upload":
      loadCoursesForDropdown()
      break
    case "update":
      loadCoursesForFilter()
      break
    case "gpa":
      loadStudentsForGPA()
      break
  }
}

function loadProfessorCourses() {
  fetch("/professor/api/courses")
    .then((response) => response.json())
    .then((courses) => {
      currentCourses = courses
      // Update course list in overview if needed
      const courseList = document.getElementById("courseList")
      if (courseList && courses.length > 0) {
        courseList.innerHTML = courses
          .map(
            (course) => `
              <div class="course-card">
                <h4>${course.courseCode} - ${course.title}</h4>
                <p>${course.description || "No description available"}</p>
                <div class="course-info">
                  <span>Credits: ${course.credits}</span>
                  <span>Department: ${course.department}</span>
                  <span>Semester: ${course.semester}</span>
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

function loadCoursesForDropdown() {
  const courseSelect = document.getElementById("courseSelect")
  if (courseSelect) {
    fetch("/professor/api/courses")
      .then((response) => response.json())
      .then((courses) => {
        currentCourses = courses
        courseSelect.innerHTML =
          '<option value="">Choose Course</option>' +
          courses
            .map((course) => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
            .join("")
      })
      .catch((error) => {
        console.error("Error loading courses for dropdown:", error)
        showToast("Error loading courses", "error")
      })
  }
}

function loadCoursesForFilter() {
  const courseFilter = document.getElementById("courseFilter")
  if (courseFilter) {
    fetch("/professor/api/courses")
      .then((response) => response.json())
      .then((courses) => {
        currentCourses = courses
        courseFilter.innerHTML =
          '<option value="">All Courses</option>' +
          courses
            .map((course) => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
            .join("")
      })
      .catch((error) => {
        console.error("Error loading courses for filter:", error)
        showToast("Error loading courses", "error")
      })
  }
}

function loadStudentsForGPA() {
  // Load all students from professor's courses
  const promises = currentCourses.map((course) =>
    fetch(`/professor/api/courses/${course.id}/students`).then((response) => response.json()),
  )

  Promise.all(promises)
    .then((results) => {
      const allStudents = []
      results.forEach((students) => {
        students.forEach((student) => {
          if (!allStudents.find((s) => s.studentId === student.studentId)) {
            allStudents.push(student)
          }
        })
      })

      currentStudents = allStudents

      const gpaStudentSelect = document.getElementById("gpaStudent")
      if (gpaStudentSelect) {
        gpaStudentSelect.innerHTML =
          '<option value="">Choose Student</option>' +
          allStudents.map((student) => `<option value="${student.studentId}">${student.name}</option>`).join("")
      }
    })
    .catch((error) => {
      console.error("Error loading students:", error)
    })
}

function loadStudentsForGrading() {
  const courseId = document.getElementById("courseSelect").value
  const assignmentType = document.getElementById("assignmentType").value

  if (!courseId || !assignmentType) {
    showToast("Please select course and assignment type first", "error")
    return
  }

  fetch(`/professor/api/courses/${courseId}/students`)
    .then((response) => response.json())
    .then((students) => {
      const container = document.getElementById("manualGradeEntries")
      container.innerHTML = students
        .map(
          (student) => `
                <div class="grade-entry">
                    <span>${student.name}</span>
                    <input type="number" 
                           name="score_${student.enrollmentId}" 
                           placeholder="Score" 
                           min="0" 
                           max="100" 
                           step="0.1">
                    <input type="text" 
                           name="assignment_${student.enrollmentId}" 
                           placeholder="Assignment Name" 
                           required>
                    <input type="hidden" 
                           name="enrollmentId_${student.enrollmentId}" 
                           value="${student.enrollmentId}">
                </div>
            `,
        )
        .join("")
    })
    .catch((error) => {
      console.error("Error loading students:", error)
      showToast("Error loading students", "error")
    })
}

function setupFormHandlers() {
  // Marks upload form
  const marksForm = document.getElementById("marksUploadForm")
  if (marksForm) {
    marksForm.addEventListener("submit", (e) => {
      e.preventDefault()

      const courseId = document.getElementById("courseSelect").value
      const assignmentType = document.getElementById("assignmentType").value

      if (!courseId || !assignmentType) {
        showToast("Please select course and assignment type", "error")
        return
      }

      // Collect all grade entries
      const gradeEntries = document.querySelectorAll(".grade-entry")
      const promises = []

      gradeEntries.forEach((entry) => {
        const enrollmentId = entry.querySelector('input[name^="enrollmentId_"]').value
        const score = entry.querySelector('input[name^="score_"]').value
        const assignmentName = entry.querySelector('input[name^="assignment_"]').value

        if (score && assignmentName) {
          const formData = new FormData()
          formData.append("enrollmentId", enrollmentId)
          formData.append("assignmentType", assignmentType)
          formData.append("assignmentName", assignmentName)
          formData.append("score", score)

          promises.push(
            fetch("/professor/api/grades", {
              method: "POST",
              headers: {
                "X-CSRF-TOKEN": csrfToken,
              },
              body: formData,
            }),
          )
        }
      })

      if (promises.length === 0) {
        showToast("No grades to submit", "error")
        return
      }

      Promise.all(promises)
        .then((responses) => {
          const successCount = responses.filter((r) => r.ok).length
          showToast(`${successCount} grades submitted successfully`)
          marksForm.reset()
          document.getElementById("manualGradeEntries").innerHTML = ""
        })
        .catch((error) => {
          showToast("Error submitting grades", "error")
        })
    })
  }
}

function searchGrades() {
  const studentName = document.getElementById("studentSearch").value
  const courseId = document.getElementById("courseFilter").value

  const params = new URLSearchParams()
  if (studentName) params.append("studentName", studentName)
  if (courseId) params.append("courseId", courseId)

  fetch(`/professor/api/grades/search?${params}`)
    .then((response) => response.json())
    .then((results) => {
      const tbody = document.getElementById("studentsTableBody")
      if (tbody) {
        if (results.length === 0) {
          tbody.innerHTML =
            '<tr><td colspan="5" class="no-grades">No grades found matching your search criteria.</td></tr>'
          return
        }

        tbody.innerHTML = results
          .map(
            (result) => `
                    <tr>
                        <td>${result.studentId}</td>
                        <td>${result.studentName}</td>
                        <td>${result.course}</td>
                        <td>${result.currentGrade ? result.currentGrade.toFixed(1) + "%" : "N/A"}</td>
                        <td>
                            <button class="btn btn-secondary" onclick="editGrade(${result.enrollmentId}, '${result.studentName}', '${result.course}')">Edit</button>
                        </td>
                    </tr>
                `,
          )
          .join("")
      }
    })
    .catch((error) => {
      console.error("Error searching grades:", error)
      showToast("Error searching grades", "error")
    })
}

function calculateGPA() {
  const studentId = document.getElementById("gpaStudent").value

  if (!studentId) {
    showToast("Please select a student", "error")
    return
  }

  fetch(`/professor/api/students/${studentId}/gpa`)
    .then((response) => response.json())
    .then((result) => {
      document.getElementById("gpaValue").textContent = result.gpa.toFixed(2)
      document.getElementById("gpaResults").style.display = "block"

      // Show breakdown
      const breakdownContainer = document.getElementById("breakdownContainer")
      breakdownContainer.innerHTML = `
                <h5>Grade Breakdown for ${result.studentName}</h5>
                <table style="width: 100%; margin-top: 15px;">
                    <thead>
                        <tr>
                            <th>Course</th>
                            <th>Credits</th>
                            <th>Average</th>
                            <th>Letter Grade</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${result.breakdown
                          .map(
                            (item) => `
                            <tr>
                                <td>${item.course}</td>
                                <td>${item.credits}</td>
                                <td>${item.average ? item.average.toFixed(1) + "%" : "N/A"}</td>
                                <td>${item.letterGrade || "N/A"}</td>
                            </tr>
                        `,
                          )
                          .join("")}
                    </tbody>
                </table>
            `
    })
    .catch((error) => {
      console.error("Error calculating GPA:", error)
      showToast("Error calculating GPA", "error")
    })
}

function editGrade(enrollmentId, studentName, courseName) {
  // Fetch existing grades for this enrollment
  fetch(`/professor/api/grades/enrollment/${enrollmentId}`)
    .then((response) => response.json())
    .then((grades) => {
      const modalBody = document.getElementById("modalBody")
      modalBody.innerHTML = `
        <h3>Edit Grades for ${studentName}</h3>
        <h4>${courseName}</h4>
        <form id="editGradeForm">
          <input type="hidden" name="_csrf" value="${csrfToken}">
          <div class="grades-list">
            ${grades.length === 0 ? "<p>No grades found for this enrollment.</p>" : ""}
            ${grades
              .map(
                (grade, index) => `
              <div class="grade-edit-item">
                <div class="form-group">
                  <label>Assignment Name</label>
                  <input type="text" name="name_${grade.id}" value="${grade.assignmentName}" required>
                </div>
                <div class="form-group">
                  <label>Type</label>
                  <select name="type_${grade.id}">
                    <option value="exam" ${grade.assignmentType === "exam" ? "selected" : ""}>Exam</option>
                    <option value="assignment" ${grade.assignmentType === "assignment" ? "selected" : ""}>Assignment</option>
                    <option value="quiz" ${grade.assignmentType === "quiz" ? "selected" : ""}>Quiz</option>
                    <option value="project" ${grade.assignmentType === "project" ? "selected" : ""}>Project</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>Score</label>
                  <input type="number" name="score_${grade.id}" value="${grade.score}" min="0" max="100" step="0.1" required>
                </div>
                <div class="form-group">
                  <label>Comments</label>
                  <textarea name="comments_${grade.id}">${grade.comments || ""}</textarea>
                </div>
                <input type="hidden" name="gradeId_${index}" value="${grade.id}">
                <button type="button" class="btn btn-danger" onclick="deleteGrade(${grade.id})">Delete</button>
              </div>
            `,
              )
              .join("")}
          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button type="submit" class="btn btn-primary">Save Changes</button>
          </div>
        </form>
        <div class="add-grade-section">
          <h4>Add New Grade</h4>
          <form id="addGradeForm">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <input type="hidden" name="enrollmentId" value="${enrollmentId}">
            <div class="form-row">
              <div class="form-group">
                <label>Assignment Name</label>
                <input type="text" name="assignmentName" required>
              </div>
              <div class="form-group">
                <label>Type</label>
                <select name="assignmentType" required>
                  <option value="">Select Type</option>
                  <option value="exam">Exam</option>
                  <option value="assignment">Assignment</option>
                  <option value="quiz">Quiz</option>
                  <option value="project">Project</option>
                </select>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Score</label>
                <input type="number" name="score" min="0" max="100" step="0.1" required>
              </div>
              <div class="form-group">
                <label>Comments</label>
                <textarea name="comments"></textarea>
              </div>
            </div>
            <button type="submit" class="btn btn-primary">Add Grade</button>
          </form>
        </div>
      `

      document.getElementById("modal").style.display = "block"

      // Setup form handlers
      const editGradeForm = document.getElementById("editGradeForm")
      if (editGradeForm) {
        editGradeForm.addEventListener("submit", (e) => {
          e.preventDefault()

          const formData = new FormData(e.target)
          const promises = []

          // Process each grade
          grades.forEach((grade) => {
            const updatedGrade = {
              id: grade.id,
              assignmentName: formData.get(`name_${grade.id}`),
              assignmentType: formData.get(`type_${grade.id}`),
              score: formData.get(`score_${grade.id}`),
              comments: formData.get(`comments_${grade.id}`),
            }

            promises.push(
              fetch(`/professor/api/grades/${grade.id}`, {
                method: "PUT",
                headers: {
                  "Content-Type": "application/json",
                  "X-CSRF-TOKEN": csrfToken,
                },
                body: JSON.stringify(updatedGrade),
              }),
            )
          })

          Promise.all(promises)
            .then((responses) => {
              const allSuccessful = responses.every((r) => r.ok)
              if (allSuccessful) {
                showToast("Grades updated successfully")
                closeModal()
                searchGrades() // Refresh the grades list
              } else {
                showToast("Some grades failed to update", "error")
              }
            })
            .catch((error) => {
              showToast("Error updating grades", "error")
            })
        })
      }

      const addGradeForm = document.getElementById("addGradeForm")
      if (addGradeForm) {
        addGradeForm.addEventListener("submit", (e) => {
          e.preventDefault()

          const formData = new FormData(e.target)

          fetch("/professor/api/grades", {
            method: "POST",
            headers: {
              "X-CSRF-TOKEN": csrfToken,
            },
            body: formData,
          })
            .then((response) => {
              if (response.ok) {
                showToast("Grade added successfully")
                closeModal()
                searchGrades() // Refresh the grades list
              } else {
                return response.text().then((text) => {
                  throw new Error(text)
                })
              }
            })
            .catch((error) => {
              showToast("Error adding grade: " + error.message, "error")
            })
        })
      }
    })
    .catch((error) => {
      console.error("Error fetching grades:", error)
      showToast("Error fetching grades", "error")
    })
}

function deleteGrade(gradeId) {
  if (confirm("Are you sure you want to delete this grade? This action cannot be undone.")) {
    fetch(`/professor/api/grades/${gradeId}`, {
      method: "DELETE",
      headers: {
        "X-CSRF-TOKEN": csrfToken,
      },
    })
      .then((response) => {
        if (response.ok) {
          showToast("Grade deleted successfully")
          closeModal()
          searchGrades() // Refresh the grades list
        } else {
          throw new Error("Failed to delete grade")
        }
      })
      .catch((error) => {
        showToast("Error deleting grade", "error")
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
