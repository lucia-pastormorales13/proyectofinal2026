// Service to connect with backend at http://localhost:8080
// With fallback to mock data if backend is not available

const API_URL = "http://localhost:8080/api";

// Mock data for development without backend
const mockArticulos = [
  {
    id: 1,
    nombre: "Dell XPS 15 Laptop",
    descripcion: "High-end laptop for development",
    estado: "DISPONIBLE",
    propietarioId: 1,
  },
  {
    id: 2,
    nombre: "Canon EOS R6 Camera",
    descripcion: "Professional camera with 24-70mm lens",
    estado: "PRESTADO",
    propietarioId: 1,
  },
  {
    id: 3,
    nombre: "Sony WH-1000XM4 Headphones",
    descripcion: "Noise-canceling headphones",
    estado: "DISPONIBLE",
    propietarioId: 1,
  },
];

const mockPrestamos = [
  {
    id: 1,
    articuloId: 2,
    articuloNombre: "Canon EOS R6 Camera",
    prestatarioId: 2,
    prestatarioNombre: "John Doe",
    fechaInicio: "2026-05-20T10:00:00",
    fechaDevolucion: null,
    estado: "EN_CURSO",
  },
  {
    id: 2,
    articuloId: 1,
    articuloNombre: "Dell XPS 15 Laptop",
    prestatarioId: 3,
    prestatarioNombre: "Mary Smith",
    fechaInicio: "2026-05-15T14:30:00",
    fechaDevolucion: "2026-05-22T09:00:00",
    estado: "DEVUELTO",
  },
];

const mockUsuarios = [
  {
    id: 1,
    nombre: "Admin",
    email: "admin@example.com",
  },
  {
    id: 2,
    nombre: "John Doe",
    email: "john@example.com",
  },
  {
    id: 3,
    nombre: "Mary Smith",
    email: "mary@example.com",
  },
  {
    id: 4,
    nombre: "Carlos Lopez",
    email: "carlos@example.com",
  },
];

function getToken(): string | null {
  return localStorage.getItem("token");
}

async function fetchWithAuth(url: string, options: RequestInit = {}) {
  const token = getToken();
  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  };

  return fetch(url, { ...options, headers });
}

// Items API
export const articulosAPI = {
  getAll: async () => {
    try {
      const response = await fetchWithAuth(`${API_URL}/articulos`);
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error fetching items");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      return mockArticulos;
    }
  },

  create: async (articulo: { nombre: string; descripcion: string }) => {
    try {
      const response = await fetchWithAuth(`${API_URL}/articulos`, {
        method: "POST",
        body: JSON.stringify(articulo),
      });
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error creating item");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      const newArticulo = {
        id: mockArticulos.length + 1,
        ...articulo,
        estado: "DISPONIBLE",
        propietarioId: 1,
      };
      mockArticulos.push(newArticulo);
      return newArticulo;
    }
  },

  delete: async (id: number) => {
    try {
      const response = await fetchWithAuth(`${API_URL}/articulos/${id}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new Error("Error deleting item");
      }
    } catch (error) {
      console.warn("Backend not available, using mock data");
      const index = mockArticulos.findIndex((a) => a.id === id);
      if (index > -1) {
        mockArticulos.splice(index, 1);
      }
    }
  },
};

// Loans API
export const prestamosAPI = {
  getAll: async () => {
    try {
      const response = await fetchWithAuth(`${API_URL}/prestamos`);
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error fetching loans");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      return mockPrestamos;
    }
  },

  create: async (prestamo: { articuloId: number; prestatarioId: number | null; prestatarioNombre: string | null }) => {
    try {
      const response = await fetchWithAuth(`${API_URL}/prestamos`, {
        method: "POST",
        body: JSON.stringify(prestamo),
      });
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error creating loan");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      const articulo = mockArticulos.find((a) => a.id === prestamo.articuloId);
      const usuario = prestamo.prestatarioId
        ? mockUsuarios.find((u) => u.id === prestamo.prestatarioId)
        : null;

      const newPrestamo = {
        id: mockPrestamos.length + 1,
        articuloId: prestamo.articuloId,
        articuloNombre: articulo?.nombre || "Unknown item",
        prestatarioId: prestamo.prestatarioId || 0,
        prestatarioNombre: prestamo.prestatarioNombre || usuario?.nombre || "Unknown",
        fechaInicio: new Date().toISOString(),
        fechaDevolucion: null,
        estado: "EN_CURSO" as const,
      };
      mockPrestamos.push(newPrestamo);

      // Actualizar estado del artículo
      if (articulo) {
        articulo.estado = "PRESTADO";
      }

      return newPrestamo;
    }
  },

  devolver: async (id: number) => {
    try {
      const response = await fetchWithAuth(`${API_URL}/prestamos/${id}/devolver`, {
        method: "PUT",
      });
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error returning loan");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      const prestamo = mockPrestamos.find((p) => p.id === id);
      if (prestamo) {
        prestamo.estado = "DEVUELTO";
        prestamo.fechaDevolucion = new Date().toISOString();

        // Actualizar estado del artículo
        const articulo = mockArticulos.find((a) => a.id === prestamo.articuloId);
        if (articulo) {
          articulo.estado = "DISPONIBLE";
        }
      }
      return prestamo;
    }
  },
};

// Users API
export const usuariosAPI = {
  getAll: async () => {
    try {
      const response = await fetchWithAuth(`${API_URL}/usuarios`);
      if (response.ok) {
        return await response.json();
      }
      throw new Error("Error fetching users");
    } catch (error) {
      console.warn("Backend not available, using mock data");
      return mockUsuarios;
    }
  },
};
