function saveUserRole(event) {
    event.preventDefault();
    var role = document.getElementById("role").value;
    if (!role) {
        alert("Please select a role before proceeding.");
        return;
    }
    localStorage.setItem("userRole", role);
    window.location.href = "login.html";
}

function togglePassword() {
    const passwordField = document.getElementById("password");
    const toggleButton = document.querySelector(".toggle-password");
    if (passwordField.type === "password") {
        passwordField.type = "text";
        toggleButton.textContent = "Hide";
    } else {
        passwordField.type = "password";
        toggleButton.textContent = "Show";
    }
}
