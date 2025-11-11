document.getElementById('loginForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const correo = document.getElementById('correo').value;
    const contrasena = document.getElementById('contrasena').value;

    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ correo, contrasena })
    });

    const data = await response.json();

    if (response.ok) {
        document.getElementById('msg').innerText = "Login exitoso, redirigiendo...";
        setTimeout(() => window.location.href = '/home', 1000);
    } else {
        document.getElementById('msg').innerText = data.error || "Error en el login";
    }
});
