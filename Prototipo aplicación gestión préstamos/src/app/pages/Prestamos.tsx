import { useState, useEffect } from "react";
import { prestamosAPI } from "../services/api";
import AddPrestamoModal from "../components/AddPrestamoModal";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { ClipboardList, RotateCcw } from "lucide-react";
import { toast } from "sonner";

interface Prestamo {
  id: number;
  articuloId: number;
  articuloNombre: string;
  prestatarioId: number;
  prestatarioNombre: string;
  fechaInicio: string;
  fechaDevolucion: string | null;
  estado: "EN_CURSO" | "DEVUELTO";
}

export default function Prestamos() {
  const [prestamos, setPrestamos] = useState<Prestamo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPrestamos();
  }, []);

  const loadPrestamos = async () => {
    try {
      const data = await prestamosAPI.getAll();
      setPrestamos(data);
    } catch (error) {
      toast.error("Error loading loans");
    } finally {
      setLoading(false);
    }
  };

  const handleDevolver = async (id: number) => {
    try {
      await prestamosAPI.devolver(id);
      setPrestamos(
        prestamos.map((p) =>
          p.id === id
            ? { ...p, estado: "DEVUELTO", fechaDevolucion: new Date().toISOString() }
            : p
        )
      );
      toast.success("Item returned successfully");
    } catch (error) {
      toast.error("Error returning item");
    }
  };

  const calcularDiasPrestado = (fechaInicio: string, fechaDevolucion: string | null) => {
    const inicio = new Date(fechaInicio);
    const fin = fechaDevolucion ? new Date(fechaDevolucion) : new Date();
    const diffTime = Math.abs(fin.getTime() - inicio.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const formatFecha = (fecha: string) => {
    return new Date(fecha).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-gray-600">Loading loans...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Section header */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-3xl font-semibold text-gray-900">Loan Management</h2>
          <p className="text-gray-600 mt-1">
            View and manage the loan history of your items
          </p>
        </div>
        <AddPrestamoModal onAdd={loadPrestamos} />
      </div>

      {/* Quick statistics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader className="pb-3">
            <CardDescription>Total Loans</CardDescription>
            <CardTitle className="text-3xl">{prestamos.length}</CardTitle>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader className="pb-3">
            <CardDescription>Active Loans</CardDescription>
            <CardTitle className="text-3xl text-blue-600">
              {prestamos.filter((p) => p.estado === "EN_CURSO").length}
            </CardTitle>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader className="pb-3">
            <CardDescription>Returned Loans</CardDescription>
            <CardTitle className="text-3xl text-green-600">
              {prestamos.filter((p) => p.estado === "DEVUELTO").length}
            </CardTitle>
          </CardHeader>
        </Card>
      </div>

      {/* Loans Table */}
      <Card>
        <CardHeader>
          <CardTitle>Loan History</CardTitle>
          <CardDescription>
            Complete list of all loans made
          </CardDescription>
        </CardHeader>
        <CardContent>
          {prestamos.length === 0 ? (
            <div className="text-center py-12">
              <ClipboardList className="size-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900">No loans</h3>
              <p className="text-gray-600 mt-1">
                Loans you make will appear here
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Loaned Item</TableHead>
                    <TableHead>Borrower</TableHead>
                    <TableHead>Start Date</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Days Loaned</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {prestamos.map((prestamo) => (
                    <TableRow key={prestamo.id}>
                      <TableCell className="font-medium">
                        {prestamo.articuloNombre}
                      </TableCell>
                      <TableCell>{prestamo.prestatarioNombre}</TableCell>
                      <TableCell className="text-sm text-gray-600">
                        {formatFecha(prestamo.fechaInicio)}
                      </TableCell>
                      <TableCell>
                        <Badge
                          variant={prestamo.estado === "EN_CURSO" ? "default" : "secondary"}
                        >
                          {prestamo.estado === "EN_CURSO" ? "Active" : "Returned"}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <span className="font-medium">
                          {calcularDiasPrestado(prestamo.fechaInicio, prestamo.fechaDevolucion)} days
                        </span>
                      </TableCell>
                      <TableCell className="text-right">
                        {prestamo.estado === "EN_CURSO" ? (
                          <Button
                            onClick={() => handleDevolver(prestamo.id)}
                            variant="default"
                            size="sm"
                            className="gap-2 bg-green-600 hover:bg-green-700"
                          >
                            <RotateCcw className="size-4" />
                            Return
                          </Button>
                        ) : (
                          <span className="text-sm text-gray-500">
                            Returned on {prestamo.fechaDevolucion && formatFecha(prestamo.fechaDevolucion)}
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
