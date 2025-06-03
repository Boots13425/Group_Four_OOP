// --- Helper: Toast Notification ---
function showToast(message, type = "info", duration = 3100) {
  const toast = document.getElementById("toast");
  toast.className = "toast " + type;
  toast.textContent = message;
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), duration);
}

// --- Modal (for future use) ---
function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// --- Login Form Handling ---
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  if (!loginForm) return;

  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    // Clear errors
    document.getElementById("usernameError").textContent = "";
    document.getElementById("passwordError").textContent = "";
    document.getElementById("userRoleError").textContent = "";

    const username = loginForm.username.value.trim();
    const password = loginForm.password.value;
    const userRole = loginForm.userRole.value;

    let valid = true;
    if (!username) {
      document.getElementById("usernameError").textContent = "Username required";
      valid = false;
    }
    if (!password) {
      document.getElementById("passwordError").textContent = "Password required";
      valid = false;
    }
    if (!userRole) {
      document.getElementById("userRoleError").textContent = "Please select a user role";
      valid = false;
    }

    if (!valid) return;

    try {
      // API call to backend for login
      const res = await fetch('/api/login', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, userRole })
      });
      const data = await res.json();
      if (res.ok) {
        showToast("Login successful! Redirecting...", "success");
        // Redirect based on user role (or as returned by backend)
        setTimeout(() => {
          if (data.redirectUrl) {
            window.location.href = data.redirectUrl;
          } else if (userRole === "student") {
            window.location.href = "student.html";
          } else if (userRole === "professor") {
            window.location.href = "professor.html";
          } else if (userRole === "admin") {
            window.location.href = "admin.html";
          } else {
            window.location.href = "/";
          }
        }, 1200);
      } else {
        // Backend should return an error message
        showToast(data.message || "Invalid credentials or role. Please try again.", "error");
      }
    } catch (err) {
      showToast("Network error. Please try again.", "error");
    }
  });
});