# Documentación del Framework CSS Minimalista - Versión Simplificada

## Visión general

Un sistema de estilos minimalista basado en HTML semántico que proporciona estilos consistentes
y responsivos sin necesidad de clases CSS adicionales en la mayoría de casos.

## Variables CSS globales

El sistema utiliza estas variables CSS para una fácil personalización:

```css
:root {
    --color-bg: oklch(0.96 0.01 240);
    --color-secondary-bg: oklch(0.92 0.01 240);
    --color-text: oklch(0.15 0.02 240);
    --color-border: oklch(0.79 0.02 240);

    --color-primary: oklch(0.4 0.1 240);
    --color-muted: oklch(0.4 0.02 240);
    --color-success: oklch(0.55 0.12 160);
    --color-error: oklch(0.5 0.14 20);
    --color-warning: oklch(0.75 0.12 90);

    /* Espaciado (basado en rem) */
    --space-xs: 0.25rem;
    --space-sm: 0.5rem;
    --space-md: 1rem;
    --space-lg: 1.5rem;
    --space-xl: 2rem;

    /* Tipografía */
    --font-base: 'Urbanist', sans-serif;

    --font-size-base: 1rem;
    --font-size-h1: 1.25rem;
    --font-size-h2: 1.125rem;
    --font-size-h3: 1.0625rem;
    --font-size-h4: 0.9375rem;
    --font-size-h5: 0.875rem;
    --font-size-h6: 0.8125rem;
    --font-size-small: 0.75rem;

    --font-weight-light: 300;
    --font-weight-normal: 400;
    --font-weight-medium: 500;
    --font-weight-semibold: 600;
    --font-weight-bold: 700;

    --line-height: 1.4;
}
```

## Componentes soportados

### 1. Estructura de página

#### Contenedores principales
```html
<header>, <main>, <footer>
```

```html
<section>
```

#### Sistema de navegación
```html
<nav>
    <ul>
        <li><a href="#">Inicio</a></li>
        <li><a href="#">Productos</a></li>
    </ul>
</nav>
```

### 2. Componentes básicos

#### Botones
```html
<button>Primario</button>
<button class="secondary">Secundario</button>
<button class="success">Éxito</button>
<button class="error">error</button>
<button class="warning">warning</button>
```

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

### 3. Componentes de contenido

#### Tarjetas
```html
<article>
    <h2>Título</h2>
    <p>Contenido</p>
</article>
```

#### Tablas
```html
<div class="table-container">
    <table>
        <tr>
            <th>Nombre</th>
            <th>Email</th>
        </tr>
    </table>
</div>
```

## Modo oscuro
El framework incluye soporte para modo oscuro mediante la clase `.dark-mode` en el elemento
`<body>`:

```javascript
document.body.classList.toggle('dark-mode');
```

## Ejemplo completo

```html
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prueba Framework CSS</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500&display=swap">
    <link rel="stylesheet" href="/css/framework.css">
  </head>
  <body>

    <nav>
      <button class="burguer" aria-label="Menú" hidden>
        ☰
      </button>
      <ul>
        <li><a href="#">Inicio</a></li>
        <li><a href="#">Productos</a></li>
        <li><a href="#">Contacto</a></li>
      </ul>
    </nav>

    <main>
      <article>
        <h2>Tarjeta estándar</h2>
        <p>Contenido de ejemplo con un <a href="#">enlace</a>.</p>
        <button>Botón primario</button>
        <button type="secondary">Secundario</button>
      </article>

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

      <section>
        <h2>Formulario</h2>
        <form>
          <label for="nombre">Nombre:</label>
          <input type="text" id="nombre" placeholder="Escribe tu nombre">
          <label for="email">Email:</label>
          <input type="email" id="email">
          <label for="rol">Rol:</label>

          <select id="rol">
            <option>Usuario</option>
            <option>Admin</option>
          </select>

          <label>
            <input type="checkbox"> Acepto los términos
          </label>

          <button type="submit">Enviar</button>
          <button type="secondary">Cancelar</button>
        </form>
      </section>
      
      <aside role="alert">
        <p>Esta es una alerta importante</p>
      </aside>
      
      <section>
        <h2>Botones adicionales</h2>
        <button type="success">Éxito</button>
        <button type="error">Error</button>
        <button disabled>Deshabilitado</button>
      </section>

      <section>
        <h2>Imagen</h2>
        <img src="imagen.jpg" alt="Placeholder">
        <h2>Con image container</h2>
        <div class="image-container">
          <img src="imagen.jpg" alt="Placeholder">
        </div>
      </section>

      <div class="dropdown">
          <button class="dropdown-toggle">Menú de acciones</button>
          <ul class="dropdown-menu" hidden>
              <li><a href="#">Editar</a></li>
              <li><a href="#">Eliminar</a></li>
              <li><a href="#">Compartir</a></li>
          </ul>
      </div>

      <button onclick="document.body.classList.toggle('dark-mode')">Toggle Dark Mode</button>
    </main>

    <footer>
      <p>© 2023 Mi App Minimalista</p>
    </footer>

    <script src="/js/burguer.js"></script>
    <script src="/js/dropdown.js"></script>
  </body>
</html>
```
