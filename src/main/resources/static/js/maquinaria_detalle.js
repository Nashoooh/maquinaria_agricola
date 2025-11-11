// Event listeners para CSP compliance
document.addEventListener('DOMContentLoaded', function() {
    // Event listener para el buscador
    document.getElementById('buscador').addEventListener('keyup', filtrarMaquinarias);
    
    // Event listener para el select - redirige con el ID seleccionado
    document.getElementById('maquinariaSelect').addEventListener('change', function() {
        const id = this.value;
        if (id) {
            window.location.href = '/maquinaria/detalle?id=' + id;
        }
    });
    
    // Event listener para el botón limpiar
    document.getElementById('limpiarBtn').addEventListener('click', limpiarSeleccion);
});

function filtrarMaquinarias() {
    const texto = document.getElementById('buscador').value.toLowerCase();
    const select = document.getElementById('maquinariaSelect');
    for (let i = 1; i < select.options.length; i++) {
        const option = select.options[i];
        option.style.display = option.text.toLowerCase().includes(texto) ? '' : 'none';
    }
}

function limpiarSeleccion() {
    // Redirigir a la página sin parámetros para limpiar
    window.location.href = '/maquinaria/detalle';
}
