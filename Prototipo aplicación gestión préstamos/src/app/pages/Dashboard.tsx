import { useState, useEffect } from "react";
import { articulosAPI } from "../services/api";
import AddArticuloModal from "../components/AddArticuloModal";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Trash2, Package } from "lucide-react";
import { toast } from "sonner";

interface Articulo {
  id: number;
  nombre: string;
  descripcion: string;
  estado: "DISPONIBLE" | "PRESTADO";
  propietarioId: number;
}

export default function Dashboard() {
  const [articulos, setArticulos] = useState<Articulo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadArticulos();
  }, []);

  const loadArticulos = async () => {
    try {
      const data = await articulosAPI.getAll();
      setArticulos(data);
    } catch (error) {
      toast.error("Error loading items");
    } finally {
      setLoading(false);
    }
  };

  const handleAddArticulo = async (articulo: { nombre: string; descripcion: string }) => {
    try {
      const newArticulo = await articulosAPI.create(articulo);
      setArticulos([...articulos, newArticulo]);
      toast.success("Item added successfully");
    } catch (error) {
      toast.error("Error adding item");
    }
  };

  const handleDeleteArticulo = async (id: number) => {
    if (!confirm("Are you sure you want to delete this item?")) {
      return;
    }

    try {
      await articulosAPI.delete(id);
      setArticulos(articulos.filter((a) => a.id !== id));
      toast.success("Item deleted successfully");
    } catch (error) {
      toast.error("Error deleting item");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-gray-600">Loading items...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Section header */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-3xl font-semibold text-gray-900">My Items</h2>
          <p className="text-gray-600 mt-1">
            Manage the items you have available for loan
          </p>
        </div>
        <AddArticuloModal onAdd={handleAddArticulo} />
      </div>

      {/* Items Cards Grid */}
      {articulos.length === 0 ? (
        <Card className="text-center py-12">
          <CardContent className="flex flex-col items-center gap-4">
            <Package className="size-16 text-gray-400" />
            <div>
              <h3 className="text-lg font-medium text-gray-900">No items</h3>
              <p className="text-gray-600 mt-1">
                Start by adding your first item
              </p>
            </div>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {articulos.map((articulo) => (
            <Card key={articulo.id} className="hover:shadow-md transition-shadow">
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <CardTitle className="text-lg">{articulo.nombre}</CardTitle>
                    <Badge
                      variant={articulo.estado === "DISPONIBLE" ? "default" : "secondary"}
                      className="mt-2"
                    >
                      {articulo.estado === "DISPONIBLE" ? "Available" : "On Loan"}
                    </Badge>
                  </div>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => handleDeleteArticulo(articulo.id)}
                    className="text-red-600 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="size-4" />
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <CardDescription className="line-clamp-3">
                  {articulo.descripcion}
                </CardDescription>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
