import React, { useState, useEffect } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../components/ui/table";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { RotateCcw, Plus } from "lucide-react";
import { toast } from "sonner";
// ➕ Importamos el modal
import AddPrestamoModal from "../components/AddPrestamoModal";

const getUsuarioIdDesdeToken = () => {
  const token = localStorage.getItem("token");
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window.atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const payload = JSON.parse(jsonPayload);
    return payload.id || payload.userId || payload.idUsuario || payload.sub || 1;
  } catch (error) {
    console.error("Error al decodificar el token:", error);
    return null;
  }
};

export default function Prestamos() {
  const [prestamos, setPrestamos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPrestamos();
  }, []);

  const loadPrestamos = async () => {
    try {
      const token = localStorage.getItem("token");
      const usuarioId = localStorage.getItem("usuarioId");

      const response = await fetch(`http://localhost:8080/api/prestamos/usuario/${usuarioId}`, {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (response.ok) {
        const data = await response.json();
        setPrestamos(Array.isArray(data) ? data : []);
      } else {
        setPrestamos([]);
      }
    } catch (error) {
      console.error("Error cargando préstamos:", error);
      setPrestamos([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDevolver = async (id) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/api/prestamos/${id}/devolver`, {
        method: "PUT",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (response.ok) {
        toast.success("¡Artículo devuelto correctamente!");
        loadPrestamos(); 
      } else {
        toast.error("El backend rechazó la devolución");
      }
    } catch (error) {
      console.error(error);
      toast.error("Error al procesar la devolución");
    }
  };

  const formatFecha = (fechaArray) => {
    if (!fechaArray) return "-";
    if (Array.isArray(fechaArray)) {
      return `${fechaArray[2]}/${fechaArray[1]}/${fechaArray[0]}`;
    }
    return new Date(fechaArray).toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-gray-500 animate-pulse">Cargando historial de préstamos...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Loans</h1>
          <p className="text-gray-500">History of borrowed items.</p>
        </div>
        {/* 💡 AQUÍ ESTÁ EL BOTÓN QUE AÑADIMOS */}
        <AddPrestamoModal onAdd={loadPrestamos} />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Active Records</CardTitle>
          <CardDescription>Complete list of saved transactions.</CardDescription>
        </CardHeader>
        <CardContent>
          {prestamos.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              There are no records of loans at this time.
            </div>
          ) : (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Item</TableHead>
                    <TableHead>Borrower</TableHead>
                    <TableHead>Start Date</TableHead>
                    <TableHead>State</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {prestamos.map((prestamo) => (
                    <TableRow key={prestamo.id}>
                      <TableCell className="font-medium">
                        {prestamo.articulo?.nombre || "Item"}
                      </TableCell>
                      <TableCell>
                        {prestamo.nombreReceptor || "External Friend"}
                      </TableCell>
                      <TableCell>{formatFecha(prestamo.fechaInicio)}</TableCell>
                      <TableCell>
                        <Badge variant={prestamo.fechaDevuelto ? "secondary" : "default"}>
                          {prestamo.fechaDevuelto ? "Devuelto" : "En Curso"}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        {!prestamo.fechaDevuelto ? (
                          <Button
                            onClick={() => handleDevolver(prestamo.id)}
                            variant="default"
                            size="sm"
                            className="gap-2 bg-green-600 hover:bg-green-700"
                          >
                            <RotateCcw className="size-4" />
                            Marcar Devolución
                          </Button>
                        ) : (
                          <span className="text-xs text-gray-400">
                            Finalizado
                          </span>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}