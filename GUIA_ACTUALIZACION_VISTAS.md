# Guía de Actualización de Formularios HTML

## 📋 Cambios Necesarios en Vistas

Las vistas HTML necesitan actualizar los nombres de los campos para mapear correctamente a los DTOs.

## 1. Formulario de Registro (`registro.html`)

### ✅ Campos correctos para `UsuarioDTO`:

```html
<form method="post" action="/registro">
    <!-- ✅ Campo: nombre -->
    <input type="text" name="nombre" required>
    
    <!-- ✅ Campo: correo -->
    <input type="email" name="correo" required>
    
    <!-- ✅ Campo: contrasena -->
    <input type="password" name="contrasena" minlength="6" required>
    
    <!-- ✅ Opcional: telefono -->
    <input type="tel" name="telefono">
    
    <!-- ✅ Opcional: direccion -->
    <input type="text" name="direccion">
    
    <button type="submit">Registrarse</button>
</form>
```

## 2. Formulario de Perfil (`perfil.html`)

### ✅ Campos correctos para actualización de perfil:

```html
<form method="post" action="/perfil">
    <!-- ✅ Mostrar datos actuales -->
    <p>Nombre: ${usuario.nombre}</p>
    <p>Correo: ${usuario.correo}</p>
    
    <!-- ✅ Campos editables -->
    <label>Dirección:</label>
    <input type="text" name="direccion" value="${usuario.direccion}">
    
    <label>Teléfono:</label>
    <input type="tel" name="telefono" value="${usuario.telefono}">
    
    <label>Cultivos:</label>
    <input type="text" name="cultivos" value="${usuario.cultivos}">
    
    <button type="submit">Actualizar Perfil</button>
</form>
```

## 3. Formulario de Registro de Maquinaria (`maquinaria_registrar.html`)

### ✅ Campos correctos para `MaquinariaDTO`:

```html
<form method="post" action="/maquinaria/registrar">
    <!-- ✅ Campo: tipo (obligatorio) -->
    <label>Tipo de Maquinaria:</label>
    <input type="text" name="tipo" required>
    
    <!-- ✅ Campo: marca (obligatorio) -->
    <label>Marca:</label>
    <input type="text" name="marca" required>
    
    <!-- ✅ Campo: ubicacion (obligatorio) -->
    <label>Ubicación:</label>
    <input type="text" name="ubicacion" required>
    
    <!-- ✅ Campo: fechaDisponible (CAMBIO DE NOMBRE - era fecha_disponible) -->
    <label>Fecha Disponible:</label>
    <input type="date" name="fechaDisponible" required>
    
    <!-- ✅ Campo: precio (obligatorio, mínimo 0) -->
    <label>Precio:</label>
    <input type="number" name="precio" min="0" required>
    
    <!-- ✅ Campo: anioFabricacion (CAMBIO DE NOMBRE - era anio_fabricacion) -->
    <label>Año de Fabricación:</label>
    <input type="number" name="anioFabricacion" min="1900" max="2100" required>
    
    <!-- ✅ Campo: capacidad (obligatorio) -->
    <label>Capacidad:</label>
    <input type="text" name="capacidad" required>
    
    <!-- ✅ Campo: mantenciones (opcional) -->
    <label>Mantenciones:</label>
    <textarea name="mantenciones"></textarea>
    
    <!-- ✅ Campo: condiciones (opcional) -->
    <label>Condiciones:</label>
    <textarea name="condiciones"></textarea>
    
    <!-- ✅ Campo: mediosPago (CAMBIO DE NOMBRE - era medios_pago) -->
    <label>Medios de Pago:</label>
    <input type="text" name="mediosPago" required>
    
    <button type="submit">Registrar Maquinaria</button>
</form>

<!-- ✅ Mostrar mensajes de éxito/error -->
<div th:if="${exito}" class="alert alert-success">
    <span th:text="${exito}"></span>
</div>
<div th:if="${error}" class="alert alert-danger">
    <span th:text="${error}"></span>
</div>
```

## 4. Formulario de Búsqueda de Maquinaria (`maquinaria_buscar.html`)

### ✅ Campos de búsqueda (mantienen nombres):

```html
<form method="get" action="/maquinaria/buscar">
    <!-- ✅ Campos de búsqueda -->
    <input type="text" name="tipo" placeholder="Tipo">
    <input type="text" name="ubicacion" placeholder="Ubicación">
    <input type="date" name="fecha">
    <input type="number" name="precio" placeholder="Precio máximo">
    
    <button type="submit">Buscar</button>
</form>

<!-- ✅ Mostrar resultados con campos actualizados -->
<div th:each="maquinaria : ${maquinarias}">
    <h3 th:text="${maquinaria.tipo}"></h3>
    <p>Marca: <span th:text="${maquinaria.marca}"></span></p>
    <p>Ubicación: <span th:text="${maquinaria.ubicacion}"></span></p>
    <p>Precio: $<span th:text="${maquinaria.precio}"></span></p>
    <p>Año: <span th:text="${maquinaria.anioFabricacion}"></span></p>
    <p>Capacidad: <span th:text="${maquinaria.capacidad}"></span></p>
    <p>Propietario: <span th:text="${maquinaria.usuarioNombre}"></span></p>
    <a th:href="@{/maquinaria/detalle/{id}(id=${maquinaria.id})}">Ver Detalle</a>
</div>
```

## 5. Formulario de Reserva (`maquinaria_reserva.html`)

### ✅ Campos correctos para `ReservaDTO`:

```html
<form method="post" action="/maquinaria/reserva">
    <!-- ✅ Campo: maquinariaId -->
    <label>Seleccionar Maquinaria:</label>
    <select name="maquinariaId" required>
        <option value="">Seleccione...</option>
        <option th:each="maq : ${maquinarias}" 
                th:value="${maq.id}" 
                th:text="${maq.tipo + ' - ' + maq.marca}"></option>
    </select>
    
    <!-- ✅ Campo: fechaReserva (CAMBIO DE NOMBRE - era fecha_reserva) -->
    <label>Fecha de Reserva:</label>
    <input type="date" name="fechaReserva" required>
    
    <button type="submit">Reservar</button>
</form>

<!-- ✅ Mostrar mensajes -->
<div th:if="${exito}" class="alert alert-success">
    <span th:text="${exito}"></span>
</div>
<div th:if="${error}" class="alert alert-danger">
    <span th:text="${error}"></span>
</div>
```

## 6. Formulario de Avisos (`maquinaria_avisos.html`)

### ✅ Campos correctos para `AvisoDTO`:

```html
<form method="post" action="/maquinaria/avisos">
    <!-- ✅ Campo: maquinariaId -->
    <label>Seleccionar Maquinaria:</label>
    <select name="maquinariaId" required>
        <option value="">Seleccione...</option>
        <option th:each="maq : ${maquinarias}" 
                th:value="${maq.id}" 
                th:text="${maq.tipo + ' - ' + maq.marca}"></option>
    </select>
    
    <!-- ✅ Campo: destacado (opcional) -->
    <label>
        <input type="checkbox" name="destacado" value="true">
        Marcar como destacado
    </label>
    
    <button type="submit">Publicar Aviso</button>
</form>

<!-- ✅ Mostrar mensajes -->
<div th:if="${exito}" class="alert alert-success">
    <span th:text="${exito}"></span>
</div>
<div th:if="${error}" class="alert alert-danger">
    <span th:text="${error}"></span>
</div>
```

## 7. Detalle de Maquinaria (`maquinaria_detalle.html`)

### ✅ Mostrar datos del DTO:

```html
<div th:if="${maquinaria}">
    <h1 th:text="${maquinaria.tipo}"></h1>
    
    <div class="detalles">
        <p><strong>Marca:</strong> <span th:text="${maquinaria.marca}"></span></p>
        <p><strong>Ubicación:</strong> <span th:text="${maquinaria.ubicacion}"></span></p>
        <p><strong>Precio:</strong> $<span th:text="${maquinaria.precio}"></span></p>
        
        <!-- ✅ CAMBIO: anioFabricacion en lugar de anio_fabricacion -->
        <p><strong>Año:</strong> <span th:text="${maquinaria.anioFabricacion}"></span></p>
        
        <p><strong>Capacidad:</strong> <span th:text="${maquinaria.capacidad}"></span></p>
        
        <!-- ✅ CAMBIO: fechaDisponible en lugar de fecha_disponible -->
        <p><strong>Disponible desde:</strong> 
           <span th:text="${#dates.format(maquinaria.fechaDisponible, 'dd/MM/yyyy')}"></span></p>
        
        <p><strong>Mantenciones:</strong> <span th:text="${maquinaria.mantenciones}"></span></p>
        <p><strong>Condiciones:</strong> <span th:text="${maquinaria.condiciones}"></span></p>
        
        <!-- ✅ CAMBIO: mediosPago en lugar de medios_pago -->
        <p><strong>Medios de Pago:</strong> <span th:text="${maquinaria.mediosPago}"></span></p>
        
        <!-- ✅ Información del propietario desde DTO -->
        <p><strong>Propietario:</strong> <span th:text="${maquinaria.usuarioNombre}"></span></p>
    </div>
</div>
```

## 📝 Resumen de Cambios de Nombres

### ❗ Campos que CAMBIARON de nombre:

| Antes (snake_case)    | Después (camelCase)    |
|-----------------------|------------------------|
| `fecha_disponible`    | `fechaDisponible`      |
| `anio_fabricacion`    | `anioFabricacion`      |
| `medios_pago`         | `mediosPago`           |
| `fecha_reserva`       | `fechaReserva`         |

### ✅ Campos que NO cambiaron:

- `tipo`
- `marca`
- `ubicacion`
- `precio`
- `capacidad`
- `mantenciones`
- `condiciones`
- `nombre`
- `correo`
- `contrasena`
- `telefono`
- `direccion`
- `cultivos`

## 🎨 Mostrar Mensajes de Validación

Para mostrar errores de validación del servidor:

```html
<!-- En el formulario, agregar: -->
<div th:if="${error}" class="alert alert-danger">
    <ul>
        <li th:each="err : ${error}" th:text="${err}"></li>
    </ul>
</div>
```

## 🔍 Verificación

Para verificar que los formularios funcionan:

1. ✅ Abrir el formulario en el navegador
2. ✅ Enviar con campos vacíos → debe mostrar errores de validación
3. ✅ Llenar correctamente → debe guardar y redirigir
4. ✅ Verificar en la base de datos que los datos se guardaron

## ⚠️ Importante

- Todos los formularios deben usar `method="post"` para crear/actualizar
- Los formularios de búsqueda pueden usar `method="get"`
- Agregar `th:if="${exito}"` y `th:if="${error}"` para mostrar mensajes
- Usar `required` en campos obligatorios del HTML para validación del navegador

---

**Nota**: Estos cambios aseguran que los formularios HTML mapeen correctamente a los DTOs del backend.
