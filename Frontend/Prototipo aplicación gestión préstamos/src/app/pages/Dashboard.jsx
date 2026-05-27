import React, { useState, useEffect } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Trash2, Package, ArrowDownToLine } from "lucide-react";
import { toast } from "sonner";
import AddArticuloModal from "../components/AddArticuloModal";

export default function Dashboard() {
  const [articulos, setArticulos] = useState([]);
  const [prestamosRecibidos, setPrestamosRecibidos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    await Promise.all([loadArticulos(), loadPrestamosRecibidos()]);
    setLoading(false);
  };

  const loadArticulos = async () => {
    try {
      const token = localStorage.getItem("token");
      const usuarioId = localStorage.getItem("usuarioId");
      if (!usuarioId) return;

      const response = await fetch(`http://localhost:8080/api/articulos/usuario/${usuarioId}`, {
        headers: { "Authorization": `Bearer ${token}` }
      });
      
      if (response.ok) {
        const data = await response.json();
        setArticulos(Array.isArray(data) ? data : []);
      }
    } catch (error) {
      console.error("Error cargando artículos:", error);
    }
  };

  const loadPrestamosRecibidos = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/api/prestamos/mis-prestamos", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setPrestamosRecibidos(Array.isArray(data) ? data : []);
      }
    } catch (error) {
      console.error("Error cargando préstamos recibidos:", error);
    }
  };

  const handleDeleteArticulo = async (id) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/api/articulos/${id}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (response.ok) {
        toast.success("Artículo eliminado con éxito");
        await loadData();
      } else {
        toast.error("No se pudo eliminar el artículo.");
      }
    } catch (error) {
      toast.error("Error de conexión");
    }
  };

  if (loading) return <div className="flex items-center justify-center min-h-[400px]"><p className="text-gray-500 animate-pulse">Cargando...</p></div>;

  return (
   
    <div className="space-y-12">
      {/* 1. SECCIÓN: MIS ARTÍCULOS */}
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <p className="text-gray-500">Gestiona todos tus objetos registrados.</p>
          </div>
          <AddArticuloModal onAdd={loadData} />
        </div>

        {/* ... (el resto del código sigue igual) */}

        {/* FILTRADO: Mostramos solo artículos que NO están en prestamosRecibidos */}
        {articulos.filter(a => !prestamosRecibidos.some(p => p.articulo?.id === a.id)).length === 0 ? (
          <Card className="border-dashed border-2">
            <CardContent className="flex flex-col items-center justify-center py-12 text-center">
              <Package className="size-12 text-gray-300 mb-4" />
              <CardTitle>No tienes artículos registrados</CardTitle>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {articulos
              .filter(a => !prestamosRecibidos.some(p => p.articulo?.id === a.id))
              .map((articulo) => (
                <Card key={articulo.id} className="hover:shadow-md transition-shadow">
                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <CardTitle className="text-lg">{articulo.nombre}</CardTitle>
                        <Badge variant={articulo.estado === "DISPONIBLE" ? "default" : "secondary"} className="mt-2">
                          {articulo.estado}
                        </Badge>
                      </div>
                      <Button variant="ghost" size="icon" onClick={() => handleDeleteArticulo(articulo.id)} className="text-red-600 hover:bg-red-50">
                        <Trash2 className="size-4" />
                      </Button>
                    </div>
                  </CardHeader>
                  <CardContent><CardDescription>{articulo.descripcion}</CardDescription></CardContent>
                </Card>
            ))}
          </div>
        )}
      </div>

      {/* 2. SECCIÓN: ITEMS QUE ME HAN PRESTADO */}
      {prestamosRecibidos.length > 0 && (
        <div className="space-y-6">
          <h2 className="text-2xl font-bold tracking-tight">Items que me han prestado</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {prestamosRecibidos.map((p) => (
              <Card key={p.id} className="bg-blue-50/50 border-blue-100">
                <CardHeader>
                  <div className="flex items-center gap-2">
                    <ArrowDownToLine className="size-5 text-blue-600" />
                    <CardTitle className="text-lg">{p.articulo?.nombre}</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-gray-600">Prestado por: {p.usuarioPropietario?.nombre || "N/A"}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}