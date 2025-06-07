// Student dashboard functionality
let currentSection = "overview"
let csrfToken = null

document.addEventListener("DOMContentLoaded", () => {
  // Get CSRF token from meta tag
  const csrfMeta = document.querySelector('meta[name="_csrf"]')
  if (csrfMeta) {
    csrfToken = csrfMeta.getAttribute("content")
  }

  loadStudentData()
})

function showStudentSection(section) {
  // Hide all sections
  document.querySelectorAll(".content-section").forEach((s) => {
    s.classList.remove("active")
  })

  // Show selected section
  document.getElementById("student" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  // Update menu
  document.querySelectorAll(".student-menu a").forEach((a) => {
    a.classList.remove("active")
  })
  document.getElementById("menu" + section.charAt(0).toUpperCase() + section.slice(1)).classList.add("active")

  currentSection = section

  // Load section-specific data
  switch (section) {
    case "courses":
      loadAvailableCourses()
      break
    case "grades":
      loadStudentGrades()
      break
  }
}

function loadStudentData() {
  // Load student's GPA
  fetch("/student/api/gpa")
    .then((response) => response.json())
    .then((data) => {
      // GPA data is already displayed via Thymeleaf, but we can update it here if needed
    })
    .catch((error) => {
      console.error("Error loading GPA:", error)
    })
}

function loadAvailableCourses() {
  fetch("/student/api/courses/available")
    .then((response) => response.json())
    .then((courses) => {
      const courseGrid = document.getElementById("courseGrid")
      if (courseGrid) {
        courseGrid.innerHTML = courses
          .map(
            (course) => `
                    <div class="course-card">
                        <h4>${course.courseCode} - ${course.title}</h4>
                        <p>${course.description || "No description available"}</p>
                        <div class="course-info">
                            <span>Credits: ${course.credits}</span>
                            <span>Department: ${course.department}</span>
                        </div>
                        <div class="course-info">
                            <span>Professor: ${course.professor}</span>
                            <span>Enrolled: ${course.enrolled}/${course.capacity || "Unlimited"}</span>
                        </div>
                        <button class="btn btn-primary" onclick="enrollInCourse(${course.id})">
                            <i class="fas fa-plus"></i> Enroll
                        </button>
                    </div>
                `,
          )
          .join("")
      }
    })
    .catch((error) => {
      console.error("Error loading courses:", error)
      showToast("Error loading available courses", "error")
    })
}

function loadStudentGrades() {
  fetch("/student/api/grades")
    .then((response) => response.json())
    .then((grades) => {
      const tbody = document.getElementById("gradesTableBody")
      if (tbody) {
        tbody.innerHTML = grades
          .map(
            (grade) => `
                    <tr>
                        <td>${grade.courseCode}</td>
                        <td>${grade.assignmentName}</td>
                        <td>${grade.assignmentType}</td>
                        <td>${grade.score ? grade.score.toFixed(1) + "%" : "N/A"}</td>
                        <td>${grade.letterGrade || "N/A"}</td>
                        <td>${grade.gradedDate ? new Date(grade.gradedDate).toLocaleDateString() : "N/A"}</td>
                    </tr>
                `,
          )
          .join("")
      }
    })
    .catch((error) => {
      console.error("Error loading grades:", error)
      showToast("Error loading grades", "error")
    })
}

function enrollInCourse(courseId) {
  if (confirm("Are you sure you want to enroll in this course?")) {
    fetch(`/student/api/courses/${courseId}/enroll`, {
      method: "POST",
      headers: {
        "X-CSRF-TOKEN": csrfToken,
      },
    })
      .then((response) => {
        if (response.ok) {
          showToast("Successfully enrolled in course")
          loadAvailableCourses() // Refresh the course list
        } else {
          return response.text().then((text) => {
            throw new Error(text)
          })
        }
      })
      .catch((error) => {
        showToast("Error enrolling in course: " + error.message, "error")
      })
  }
}

function exportData(format) {
  window.open(`/student/api/export/${format}`, "_blank")
  showToast(`Downloading ${format.toUpperCase()} file...`)
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
