// Login form handling
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm")

  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      // Basic form validation
      const username = document.getElementById("username").value
      const password = document.getElementById("password").value

      if (!username || !password) {
        e.preventDefault()
        showToast("Please fill in all fields", "error")
        return false
      }
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
