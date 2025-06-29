// Variables para almacenar datos
let categorias = [];
let productos = [];
let etiquetas = [];

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', () => {
    cargarCategorias();
    cargarProductos();
    cargarEtiquetas();
});

// Funciones para Categorías
async function cargarCategorias() {
    try {
        const response = await fetch('/api/categorias');
        categorias = await response.json();
        actualizarSelectCategorias();
        actualizarTablaCategorias();
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        mostrarAlerta('Error al cargar categorías', 'danger');
    }
}

function actualizarSelectCategorias() {
    const select = document.getElementById('categoria-select');
    if (!select) return;

    select.innerHTML = '<option value="">Seleccione una categoría</option>';
    categorias.forEach(categoria => {
        const option = document.createElement('option');
        option.value = categoria.id;
        option.textContent = categoria.nombre;
        select.appendChild(option);
    });
}

function actualizarTablaCategorias() {
    const tablaCategorias = document.getElementById('tabla-categorias');
    if (!tablaCategorias) return;

    const tbody = tablaCategorias.querySelector('tbody');
    tbody.innerHTML = '';

    categorias.forEach(categoria => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${categoria.id}</td>
            <td>${categoria.nombre}</td>
            <td>
                <button class="btn btn-sm btn-warning editar-categoria" data-id="${categoria.id}">Editar</button>
                <button class="btn btn-sm btn-danger eliminar-categoria" data-id="${categoria.id}">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Agregar eventos a los botones
    document.querySelectorAll('.editar-categoria').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            cargarCategoriaParaEditar(id);
        });
    });

    document.querySelectorAll('.eliminar-categoria').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            confirmarEliminarCategoria(id);
        });
    });
}

async function guardarCategoria() {
    const form = document.getElementById('categoria-form');
    const categoriaId = form.getAttribute('data-id');
    const nombre = document.getElementById('categoria-nombre').value;

    if (!nombre) {
        mostrarAlerta('El nombre de la categoría es obligatorio', 'danger');
        return;
    }

    const categoria = { nombre };
    let url = '/api/categorias';
    let method = 'POST';

    if (categoriaId) {
        url += `/${categoriaId}`;
        method = 'PUT';
    }

    try {
        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(categoria)
        });

        const data = await response.json();

        if (response.ok) {
            mostrarAlerta(categoriaId ? 'Categoría actualizada exitosamente' : 'Categoría creada exitosamente', 'success');
            resetearFormularioCategoria();
            cargarCategorias();
        } else {
            mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error desconocido'}`, 'danger');
        }
    } catch (error) {
        console.error('Error al guardar categoría:', error);
        mostrarAlerta('Error al guardar categoría', 'danger');
    }
}

async function cargarCategoriaParaEditar(id) {
    try {
        const response = await fetch(`/api/categorias/${id}`);
        const categoria = await response.json();

        const form = document.getElementById('categoria-form');
        form.setAttribute('data-id', categoria.id);
        document.getElementById('categoria-nombre').value = categoria.nombre;
        document.getElementById('btn-guardar-categoria').textContent = 'Actualizar Categoría';
        document.getElementById('categoria-form-title').textContent = 'Editar Categoría';

        // Desplazarse al formulario
        form.scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error al cargar categoría:', error);
        mostrarAlerta('Error al cargar categoría para editar', 'danger');
    }
}

function resetearFormularioCategoria() {
    const form = document.getElementById('categoria-form');
    form.removeAttribute('data-id');
    form.reset();
    document.getElementById('btn-guardar-categoria').textContent = 'Guardar Categoría';
    document.getElementById('categoria-form-title').textContent = 'Nueva Categoría';
}

async function confirmarEliminarCategoria(id) {
    if (confirm('¿Está seguro de que desea eliminar esta categoría?')) {
        try {
            const response = await fetch(`/api/categorias/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                mostrarAlerta('Categoría eliminada exitosamente', 'success');
                cargarCategorias();
            } else {
                const data = await response.json();
                mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error al eliminar'}`, 'danger');
            }
        } catch (error) {
            console.error('Error al eliminar categoría:', error);
            mostrarAlerta('Error al eliminar categoría', 'danger');
        }
    }
}

// Funciones para Productos
async function cargarProductos() {
    try {
        const response = await fetch('/api/productos');
        productos = await response.json();
        actualizarTablaProductos();
    } catch (error) {
        console.error('Error al cargar productos:', error);
        mostrarAlerta('Error al cargar productos', 'danger');
    }
}

function actualizarTablaProductos() {
    const tablaProductos = document.getElementById('tabla-productos');
    if (!tablaProductos) return;

    const tbody = tablaProductos.querySelector('tbody');
    tbody.innerHTML = '';

    productos.forEach(producto => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${producto.id}</td>
            <td>${producto.nombre}</td>
            <td>${producto.precio.toFixed(2)}</td>
            <td>${producto.categoria ? producto.categoria.nombre : 'Sin categoría'}</td>
            <td>
                <button class="btn btn-sm btn-info ver-etiquetas" data-id="${producto.id}">Ver Etiquetas</button>
                <button class="btn btn-sm btn-warning editar-producto" data-id="${producto.id}">Editar</button>
                <button class="btn btn-sm btn-danger eliminar-producto" data-id="${producto.id}">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Agregar eventos a los botones
    document.querySelectorAll('.editar-producto').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            cargarProductoParaEditar(id);
        });
    });

    document.querySelectorAll('.eliminar-producto').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            confirmarEliminarProducto(id);
        });
    });

    document.querySelectorAll('.ver-etiquetas').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            mostrarEtiquetasProducto(id);
        });
    });
}

async function guardarProducto() {
    const form = document.getElementById('producto-form');
    const productoId = form.getAttribute('data-id');
    const nombre = document.getElementById('producto-nombre').value;
    const precio = document.getElementById('producto-precio').value;
    const categoriaId = document.getElementById('categoria-select').value;

    if (!nombre || !precio || !categoriaId) {
        mostrarAlerta('Todos los campos son obligatorios', 'danger');
        return;
    }

    const producto = {
        nombre,
        precio: parseFloat(precio),
        categoria: { id: parseInt(categoriaId) }
    };

    let url = '/api/productos';
    let method = 'POST';

    if (productoId) {
        url += `/${productoId}`;
        method = 'PUT';
    }

    try {
        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(producto)
        });

        const data = await response.json();

        if (response.ok) {
            mostrarAlerta(productoId ? 'Producto actualizado exitosamente' : 'Producto creado exitosamente', 'success');
            resetearFormularioProducto();
            cargarProductos();
        } else {
            mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error desconocido'}`, 'danger');
        }
    } catch (error) {
        console.error('Error al guardar producto:', error);
        mostrarAlerta('Error al guardar producto', 'danger');
    }
}

async function cargarProductoParaEditar(id) {
    try {
        const response = await fetch(`/api/productos/${id}`);
        const producto = await response.json();

        const form = document.getElementById('producto-form');
        form.setAttribute('data-id', producto.id);
        document.getElementById('producto-nombre').value = producto.nombre;
        document.getElementById('producto-precio').value = producto.precio;
        document.getElementById('categoria-select').value = producto.categoria ? producto.categoria.id : '';
        document.getElementById('btn-guardar-producto').textContent = 'Actualizar Producto';
        document.getElementById('producto-form-title').textContent = 'Editar Producto';

        // Desplazarse al formulario
        form.scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error al cargar producto:', error);
        mostrarAlerta('Error al cargar producto para editar', 'danger');
    }
}

function resetearFormularioProducto() {
    const form = document.getElementById('producto-form');
    form.removeAttribute('data-id');
    form.reset();
    document.getElementById('btn-guardar-producto').textContent = 'Guardar Producto';
    document.getElementById('producto-form-title').textContent = 'Nuevo Producto';
}

async function confirmarEliminarProducto(id) {
    if (confirm('¿Está seguro de que desea eliminar este producto?')) {
        try {
            const response = await fetch(`/api/productos/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                mostrarAlerta('Producto eliminado exitosamente', 'success');
                cargarProductos();
            } else {
                const data = await response.json();
                mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error al eliminar'}`, 'danger');
            }
        } catch (error) {
            console.error('Error al eliminar producto:', error);
            mostrarAlerta('Error al eliminar producto', 'danger');
        }
    }
}

// Funciones para Etiquetas
async function cargarEtiquetas() {
    try {
        const response = await fetch('/api/etiquetas');
        etiquetas = await response.json();
        actualizarTablaEtiquetas();
        actualizarCheckboxEtiquetas();
    } catch (error) {
        console.error('Error al cargar etiquetas:', error);
        mostrarAlerta('Error al cargar etiquetas', 'danger');
    }
}

function actualizarTablaEtiquetas() {
    const tablaEtiquetas = document.getElementById('tabla-etiquetas');
    if (!tablaEtiquetas) return;

    const tbody = tablaEtiquetas.querySelector('tbody');
    tbody.innerHTML = '';

    etiquetas.forEach(etiqueta => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${etiqueta.id}</td>
            <td>${etiqueta.nombre}</td>
            <td>
                <button class="btn btn-sm btn-warning editar-etiqueta" data-id="${etiqueta.id}">Editar</button>
                <button class="btn btn-sm btn-danger eliminar-etiqueta" data-id="${etiqueta.id}">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Agregar eventos a los botones
    document.querySelectorAll('.editar-etiqueta').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            cargarEtiquetaParaEditar(id);
        });
    });

    document.querySelectorAll('.eliminar-etiqueta').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            confirmarEliminarEtiqueta(id);
        });
    });
}

function actualizarCheckboxEtiquetas() {
    const etiquetasContainer = document.getElementById('etiquetas-container');
    if (!etiquetasContainer) return;

    etiquetasContainer.innerHTML = '';

    etiquetas.forEach(etiqueta => {
        const div = document.createElement('div');
        div.className = 'form-check';
        div.innerHTML = `
            <input class="form-check-input" type="checkbox" value="${etiqueta.id}" id="etiqueta-${etiqueta.id}">
            <label class="form-check-label" for="etiqueta-${etiqueta.id}">
                ${etiqueta.nombre}
            </label>
        `;
        etiquetasContainer.appendChild(div);
    });
}

async function guardarEtiqueta() {
    const form = document.getElementById('etiqueta-form');
    const etiquetaId = form.getAttribute('data-id');
    const nombre = document.getElementById('etiqueta-nombre').value;

    if (!nombre) {
        mostrarAlerta('El nombre de la etiqueta es obligatorio', 'danger');
        return;
    }

    const etiqueta = { nombre };
    let url = '/api/etiquetas';
    let method = 'POST';

    if (etiquetaId) {
        url += `/${etiquetaId}`;
        method = 'PUT';
    }

    try {
        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(etiqueta)
        });

        const data = await response.json();

        if (response.ok) {
            mostrarAlerta(etiquetaId ? 'Etiqueta actualizada exitosamente' : 'Etiqueta creada exitosamente', 'success');
            resetearFormularioEtiqueta();
            cargarEtiquetas();
        } else {
            mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error desconocido'}`, 'danger');
        }
    } catch (error) {
        console.error('Error al guardar etiqueta:', error);
        mostrarAlerta('Error al guardar etiqueta', 'danger');
    }
}

async function cargarEtiquetaParaEditar(id) {
    try {
        const response = await fetch(`/api/etiquetas/${id}`);
        const etiqueta = await response.json();

        const form = document.getElementById('etiqueta-form');
        form.setAttribute('data-id', etiqueta.id);
        document.getElementById('etiqueta-nombre').value = etiqueta.nombre;
        document.getElementById('btn-guardar-etiqueta').textContent = 'Actualizar Etiqueta';
        document.getElementById('etiqueta-form-title').textContent = 'Editar Etiqueta';

        // Desplazarse al formulario
        form.scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error al cargar etiqueta:', error);
        mostrarAlerta('Error al cargar etiqueta para editar', 'danger');
    }
}

function resetearFormularioEtiqueta() {
    const form = document.getElementById('etiqueta-form');
    form.removeAttribute('data-id');
    form.reset();
    document.getElementById('btn-guardar-etiqueta').textContent = 'Guardar Etiqueta';
    document.getElementById('etiqueta-form-title').textContent = 'Nueva Etiqueta';
}

async function confirmarEliminarEtiqueta(id) {
    if (confirm('¿Está seguro de que desea eliminar esta etiqueta?')) {
        try {
            const response = await fetch(`/api/etiquetas/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                mostrarAlerta('Etiqueta eliminada exitosamente', 'success');
                cargarEtiquetas();
            } else {
                const data = await response.json();
                mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error al eliminar'}`, 'danger');
            }
        } catch (error) {
            console.error('Error al eliminar etiqueta:', error);
            mostrarAlerta('Error al eliminar etiqueta', 'danger');
        }
    }
}

async function mostrarEtiquetasProducto(productoId) {
    try {
        const response = await fetch(`/api/productos/${productoId}/etiquetas`);
        const etiquetasProducto = await response.json();

        const modal = document.getElementById('etiquetas-modal');
        const modalTitle = modal.querySelector('.modal-title');
        const modalBody = modal.querySelector('.modal-body');

        const producto = productos.find(p => p.id == productoId);
        modalTitle.textContent = `Etiquetas de ${producto ? producto.nombre : 'Producto'}`;

        // Contenedor para las etiquetas actuales
        const etiquetasActuales = document.createElement('div');
        etiquetasActuales.innerHTML = '<h5>Etiquetas actuales:</h5>';

        if (etiquetasProducto && etiquetasProducto.length > 0) {
            const ul = document.createElement('ul');
            etiquetasProducto.forEach(etiqueta => {
                const li = document.createElement('li');
                li.innerHTML = `${etiqueta.nombre} 
                    <button class="btn btn-sm btn-danger quitar-etiqueta" 
                    data-producto="${productoId}" data-etiqueta="${etiqueta.id}">Quitar</button>`;
                ul.appendChild(li);
            });
            etiquetasActuales.appendChild(ul);
        } else {
            etiquetasActuales.innerHTML += '<p>Este producto no tiene etiquetas</p>';
        }

        // Contenedor para agregar etiquetas
        const agregarEtiquetas = document.createElement('div');
        agregarEtiquetas.innerHTML = '<h5>Agregar etiquetas:</h5>';

        if (etiquetas && etiquetas.length > 0) {
            const etiquetasDisponibles = etiquetas.filter(etiqueta => 
                !etiquetasProducto || !etiquetasProducto.some(ep => ep.id === etiqueta.id)
            );

            if (etiquetasDisponibles.length > 0) {
                const ul = document.createElement('ul');
                etiquetasDisponibles.forEach(etiqueta => {
                    const li = document.createElement('li');
                    li.innerHTML = `${etiqueta.nombre} 
                        <button class="btn btn-sm btn-success agregar-etiqueta" 
                        data-producto="${productoId}" data-etiqueta="${etiqueta.id}">Agregar</button>`;
                    ul.appendChild(li);
                });
                agregarEtiquetas.appendChild(ul);
            } else {
                agregarEtiquetas.innerHTML += '<p>No hay etiquetas disponibles para agregar</p>';
            }
        } else {
            agregarEtiquetas.innerHTML += '<p>No hay etiquetas disponibles. Cree algunas primero.</p>';
        }

        modalBody.innerHTML = '';
        modalBody.appendChild(etiquetasActuales);
        modalBody.appendChild(agregarEtiquetas);

        // Bootstrap 5: Mostrar modal
        const bootstrapModal = new bootstrap.Modal(modal);
        bootstrapModal.show();

        // Agregar eventos a los botones
        document.querySelectorAll('.quitar-etiqueta').forEach(btn => {
            btn.addEventListener('click', () => {
                const productoId = btn.getAttribute('data-producto');
                const etiquetaId = btn.getAttribute('data-etiqueta');
                quitarEtiquetaDeProducto(etiquetaId, productoId);
                bootstrapModal.hide();
            });
        });

        document.querySelectorAll('.agregar-etiqueta').forEach(btn => {
            btn.addEventListener('click', () => {
                const productoId = btn.getAttribute('data-producto');
                const etiquetaId = btn.getAttribute('data-etiqueta');
                agregarEtiquetaAProducto(etiquetaId, productoId);
                bootstrapModal.hide();
            });
        });
    } catch (error) {
        console.error('Error al cargar etiquetas del producto:', error);
        mostrarAlerta('Error al cargar etiquetas del producto', 'danger');
    }
}

async function agregarEtiquetaAProducto(etiquetaId, productoId) {
    try {
        const response = await fetch(`/api/etiquetas/${etiquetaId}/productos/${productoId}`, {
            method: 'POST'
        });

        if (response.ok) {
            mostrarAlerta('Etiqueta agregada al producto exitosamente', 'success');
            cargarProductos();
        } else {
            const data = await response.json();
            mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error desconocido'}`, 'danger');
        }
    } catch (error) {
        console.error('Error al agregar etiqueta al producto:', error);
        mostrarAlerta('Error al agregar etiqueta', 'danger');
    }
}

async function quitarEtiquetaDeProducto(etiquetaId, productoId) {
    try {
        const response = await fetch(`/api/etiquetas/${etiquetaId}/productos/${productoId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            mostrarAlerta('Etiqueta removida del producto exitosamente', 'success');
            cargarProductos();
        } else {
            const data = await response.json();
            mostrarAlerta(`Error: ${data.error || data.mensaje || 'Error desconocido'}`, 'danger');
        }
    } catch (error) {
        console.error('Error al quitar etiqueta del producto:', error);
        mostrarAlerta('Error al quitar etiqueta', 'danger');
    }
}

// Funciones auxiliares
function mostrarAlerta(mensaje, tipo) {
    const alertaDiv = document.createElement('div');
    alertaDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
    alertaDiv.role = 'alert';
    alertaDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
    `;

    const alertasContainer = document.getElementById('alertas-container');
    if (alertasContainer) {
        alertasContainer.appendChild(alertaDiv);

        // Auto eliminar después de 5 segundos
        setTimeout(() => {
            alertaDiv.remove();
        }, 5000);
    }
}
