// Professor dashboard functionality
let currentSection = "overview"
let currentCourses = []
const currentStudents = []

document.addEventListener("DOMContentLoaded", () => {
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
    })
    .catch((error) => {
      console.error("Error loading courses:", error)
      showToast("Error loading courses", "error")
    })
}

function loadCoursesForDropdown() {
  const courseSelect = document.getElementById("courseSelect")
  if (courseSelect && currentCourses.length > 0) {
    courseSelect.innerHTML =
      '<option value="">Choose Course</option>' +
      currentCourses
        .map((course) => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
        .join("")
  }
}

function loadCoursesForFilter() {
  const courseFilter = document.getElementById("courseFilter")
  if (courseFilter && currentCourses.length > 0) {
    courseFilter.innerHTML =
      '<option value="">All Courses</option>' +
      currentCourses
        .map((course) => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
        .join("")
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
        tbody.innerHTML = results
          .map(
            (result) => `
                    <tr>
                        <td>${result.studentId}</td>
                        <td>${result.studentName}</td>
                        <td>${result.course}</td>
                        <td>${result.currentGrade ? result.currentGrade.toFixed(1) + "%" : "N/A"}</td>
                        <td>
                            <button class="btn btn-secondary" onclick="editGrade(${result.enrollmentId})">Edit</button>
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

function editGrade(enrollmentId) {
  showToast("Edit grade functionality not fully implemented yet", "info")
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
