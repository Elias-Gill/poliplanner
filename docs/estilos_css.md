# Documentación del Framework CSS Minimalista

## Visión general

Un sistema de estilos minimalista basado en HTML semántico que proporciona estilos consistentes
y responsivos sin necesidad de clases CSS adicionales en la mayoría de casos.

El framework está diseñado para:

- Funcionar con HTML estándar sin requerir clases CSS
- Ser completamente responsive (mobile-first)
- Incluir soporte para modo claro/oscuro
- Mantener consistencia visual en todos los componentes
- Requerir muy poco JavaScript para su funcionamiento básico (ej:
  burguer.js)

De requerirse codigo css o javascript custom para ciertos componentes de una pagina especifica,
entonces se escribe directamente dentro de la plantilla.
Se debe evitar lo mas posible el uso de css custom dentro de las paginas.

La filosofia es de utilidad y facilidad de mantenimientos por sobre frontend complejo y
saturado.
Valoramos la utilidad, el performance y facilidad de uso por sobre estilos visuales
extravagantes y librerias de frontend pesadas.

## Estructura básica

### Variables CSS
El sistema utiliza variables CSS para una fácil personalización:

```css
:root {
    /* Colores base */
    --color-bg: #f8f8f8;
    --color-text: #333333;
    --color-border: #e0e0e0;

    /* Colores de componentes */
    --color-primary: #2563eb;
    --color-secondary: #6b7280;

    /* Espaciado */
    --space-xs: 0.25rem;
    --space-sm: 0.5rem;
    --space-md: 1rem;

    /* Tipografía */
    --font-base: 'Inter', sans-serif;
}
```

## Componentes soportados

### 1. Estructura de página

#### Contenedores principales
```html
<header>, <main>, <footer>
```
- Ancho máximo de 1200px
- Padding consistente
- Centrado automático

```html
<section>
```
- Margin arriba y abajo
- Ampliamente utilizado para generar espacios de separacion en la vista

#### Sistema de navegación
```html
<nav>
    <ul>
        <li><a href="#">Inicio</a></li>
        <li><a href="#">Productos</a></li>
    </ul>
</nav>
```
- Horizontal en desktop
- Colapsa a menú hamburguesa en móvil
- Estilo minimalista con borde inferior

### 2. Componentes básicos

#### Botones
```html
<button>Primario</button>
<button class="secondary">Secundario</button>
<button class="success">Éxito</button>
<button class="error">error</button>
<button class="warning">warning</button>
```

Características:
- Estilo consistente con variables CSS
- Estados hover y focus incluidos
- Tipos:
  primary, secondary, success, error, warning

#### Formularios
```html
<form>
    <input type="text">
    <select>
        <option>Opción 1</option>
    </select>
    <textarea></textarea>
    <label><input type="checkbox"> Acepto términos</label>
</form>
```

Estilos:
- Bordes sutiles
- Focus state visible
- Diseño responsive

### 3. Componentes de contenido

#### Tarjetas
```html
<article>
    <h2>Título</h2>
    <p>Contenido</p>
</article>
```

Características:
- Borde sutil
- Padding consistente
- Fondo que cambia en modo oscuro

#### Tablas
```html
<!-- ojo, necesario el contenedor para soporte de scroll horizontal -->
<div class="table-container">
    <table>
        <tr>
            <th>Nombre</th>
            <th>Email</th>
        </tr>
    </table>
</div>
```

Comportamiento:
- Scroll horizontal en móvil
- Bordes limpios
- Encabezados destacados

## Modo oscuro
El framework incluye soporte para modo oscuro mediante la clase `.dark-mode` en el elemento
`<body>`:

```javascript
document.body.classList.toggle('dark-mode');
```

Los componentes automáticamente ajustan sus colores basados en las variables CSS para modo
oscuro.

## Personalización
Para modificar el diseño:

1. Ajustar variables CSS en `:root`
2. Sobreescribir estilos específicos cuando sea necesario
3. Para componentes especiales, usar el atributo `style` directamente en el HTML
4. Usar siempre variables definidas dentro del `main.css`

## Ejemplo completo

```html
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prueba Framework CSS</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500&display=swap">
    <link rel="stylesheet" th:href="@{/css/framework.css}">
  </head>
  <body>

    <nav>
      <!-- Botón hamburguesa (solo móvil) -->
      <button class="burguer" aria-label="Menú" hidden>
        ☰
      </button>
      <ul>
        <li><a href="#">Inicio</a></li>
        <li><a href="#">Productos</a></li>
        <li><a href="#">Contacto</a></li>
      </ul>
    </nav>

    <!-- Contenedor principal -->
    <main>
      <!-- Tarjetas -->
      <article>
        <h2>Tarjeta estándar</h2>
        <p>Contenido de ejemplo con un <a href="#">enlace</a>.</p>
        <button>Botón primario</button>
        <button type="secondary">Secundario</button>
      </article>

      <!-- Tabla -->
      <section>
        <h2>Tabla</h2>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Email</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Ana López</td>
                <td>ana@example.com</td>
                <td>Admin</td>
              </tr>
              <tr>
                <td>Carlos Ruiz</td>
                <td>carlos@example.com</td>
                <td>Usuario</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Formulario -->
      <section>
        <h2>Formulario</h2>
        <form>
          <label for="nombre">Nombre:</label>
          <input type="text" id="nombre" placeholder="Escribe tu nombre">
          <label for="email">Email:</label>
          <input type="email" id="email">
          <label for="rol">Rol:</label>

          <!-- SELECT  -->
          <select id="rol">
            <option>Usuario</option>
            <option>Admin</option>
          </select>

          <!-- checkbox -->
          <label>
            <input type="checkbox"> Acepto los términos
          </label>

          <!-- tipos de botones -->
          <button type="submit">Enviar</button>
          <button type="secondary">Cancelar</button>
        </form>
      </section>
      <!-- Alertas/Burbujas -->
      <aside role="alert">
        <p>Esta es una alerta importante</p>
      </aside>
      <!-- Botones extra -->
      <section>
        <h2>Botones adicionales</h2>
        <button type="success">Éxito</button>
        <button type="error">Error</button>
        <button disabled>Deshabilitado</button>
      </section>

      <!-- Imagen responsive -->
      <section>
        <h2>Imagen</h2>
        <img src="https://i.pinimg.com/originals/38/aa/79/38aa797cc03e83b0006270ae45485df7.jpg" alt="Placeholder">
        <h2>Con image container</h2>
        <div class="image-container">
          <img src="https://i.pinimg.com/originals/38/aa/79/38aa797cc03e83b0006270ae45485df7.jpg" alt="Placeholder">
        </div>
      </section>

      <!-- dropdown menu -->
      <div class="dropdown">
          <button class="dropdown-toggle">Menú de acciones</button>
          <ul class="dropdown-menu" hidden>
              <li><a href="#">Editar</a></li>
              <li><a href="#">Eliminar</a></li>
              <li><a href="#">Compartir</a></li>
          </ul>
      </div>

      <!-- Botón para toggle modo oscuro (ejemplo) -->
      <button onclick="document.body.classList.toggle('dark-mode')">Toggle Dark Mode</button>
    </main>

    <!-- Footer -->
    <footer>
      <p>© 2023 Mi App Minimalista</p>
    </footer>
  </body>

  <script th:src="@{/js/burguer.js}"></script>
  <script th:src="@{/js/dropdown.js}"></script>
</html>
```
