function submitProfile() {
    const userId = localStorage.getItem("userId"); // Make sure this is stored after login

    const profileData = {
        name: document.getElementById("name").value,
        email: document.getElementById("email").value,
        bio: document.getElementById("bio").value,
        user: {
            id: parseInt(userId)
        }
    };

    fetch("http://localhost:8080/api/profile/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(profileData)
    })
    .then(response => response.json())
    .then(data => {
        alert("Profile created successfully!");
        console.log(data);
    })
    .catch(error => {
        console.error("Error:", error);
        alert("Failed to create profile.");
    });
}
