import { useState, useEffect } from "react";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "./ui/dialog";

import { Button } from "./ui/button";
import { Label } from "./ui/label";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./ui/select";

import {
  RadioGroup,
  RadioGroupItem,
} from "./ui/radio-group";

import { Input } from "./ui/input";

import { Plus } from "lucide-react";

import { toast } from "sonner";

export default function AddPrestamoModal({ onAdd }) {

  const [open, setOpen] = useState(false);

  const [articulos, setArticulos] = useState([]);

  const [usuarios, setUsuarios] = useState([]);

  const [articuloId, setArticuloId] = useState("");

  const [tipoPrestatario, setTipoPrestatario] =
    useState("usuario");

  const [usuarioId, setUsuarioId] = useState("");

  const [nombrePrestatario, setNombrePrestatario] =
    useState("");

  const [loading, setLoading] = useState(false);

  useEffect(() => {

    if (open) {
      loadData();
    }

  }, [open]);

  const loadData = async () => {

    try {

      const token =
        localStorage.getItem("token");

      const headers = {
        Authorization: `Bearer ${token}`,
      };

      const usuarioId =
        localStorage.getItem("usuarioId") || 1;

      console.log("USUARIO ID:", usuarioId);

      const [resArticulos, resUsuarios] =
        await Promise.all([

          fetch(
            `http://localhost:8080/api/articulos/usuario/${usuarioId}`,
            { headers }
          ),

          fetch(
            "http://localhost:8080/api/usuarios",
            { headers }
          ),
        ]);

      let articulosData = [];

      let usuariosData = [];

      if (resArticulos.ok) {

        articulosData =
          await resArticulos.json();

        console.log(
          "ARTICULOS RECIBIDOS:",
          articulosData
        );
      }

      if (resUsuarios.ok) {

        usuariosData =
          await resUsuarios.json();
      }

      const articulosDisponibles =
        articulosData.filter(
          (a) => a.estado === "DISPONIBLE"
        );

      console.log(
        "ARTICULOS DISPONIBLES:",
        articulosDisponibles
      );

      setArticulos(articulosDisponibles);

      setUsuarios(usuariosData);

    } catch (error) {

      console.error(
        "ERROR CARGANDO DATOS:",
        error
      );

      toast.error(
        "Error cargando datos"
      );
    }
  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (!articuloId) {

      toast.error(
        "Selecciona un artículo"
      );

      return;
    }

    if (
      tipoPrestatario === "usuario" &&
      !usuarioId
    ) {

      toast.error(
        "Selecciona un usuario"
      );

      return;
    }

    if (
      tipoPrestatario === "nombre" &&
      !nombrePrestatario.trim()
    ) {

      toast.error(
        "Introduce un nombre"
      );

      return;
    }

    setLoading(true);

    try {

      const response =
        await fetch(
          "http://localhost:8080/api/prestamos",
          {
            method: "POST",

            headers: {
              "Content-Type":
                "application/json",

              Authorization:
                `Bearer ${localStorage.getItem("token")}`,
            },

            body: JSON.stringify({

              articuloId:
                parseInt(articuloId),

              usuarioReceptorId:
                tipoPrestatario === "usuario"
                  ? parseInt(usuarioId)
                  : null,

              nombreReceptor:
                tipoPrestatario === "nombre"
                  ? nombrePrestatario
                  : null,
            }),
          }
        );

      const texto =
        await response.text();

      console.log(
        "RESPUESTA BACK:",
        texto
      );

      if (!response.ok) {

        throw new Error(texto);
      }

      toast.success(
        "Préstamo creado"
      );

      resetForm();

      setOpen(false);

      onAdd();

    } catch (error) {

      console.error(error);

      toast.error(
        "No se pudo crear el préstamo"
      );

    } finally {

      setLoading(false);
    }
  };

  const resetForm = () => {

    setArticuloId("");

    setTipoPrestatario("usuario");

    setUsuarioId("");

    setNombrePrestatario("");
  };

  return (

    <Dialog
      open={open}
      onOpenChange={setOpen}
    >

      <DialogTrigger asChild>

        <Button className="gap-2">

          <Plus className="size-4" />

          Crear préstamo

        </Button>

      </DialogTrigger>

      <DialogContent>

        <DialogHeader>

          <DialogTitle>
            New Loan
          </DialogTitle>

          <DialogDescription>
            Select item and borrower
          </DialogDescription>

        </DialogHeader>

        <form
          onSubmit={handleSubmit}
          className="space-y-4"
        >

          <div className="space-y-2">

            <Label htmlFor="articulo">
              Item
            </Label>

            <Select
              value={articuloId}
              onValueChange={setArticuloId}
            >

              <SelectTrigger id="articulo">

                <SelectValue
                      placeholder="Select item"
                    />

              </SelectTrigger>

              <SelectContent>

                {articulos.length === 0 ? (

                  <div className="p-2 text-sm text-gray-500">

                    There are no articles

                  </div>

                ) : (

                  articulos.map((articulo) => (

                    <SelectItem
                      key={articulo.id}
                      value={articulo.id.toString()}
                    >

                      {articulo.nombre}

                    </SelectItem>
                  ))
                )}

              </SelectContent>

            </Select>

          </div>

          <div className="space-y-3">

            <Label>
              Lend to:
            </Label>

            <RadioGroup
              value={tipoPrestatario}
              onValueChange={setTipoPrestatario}
            >

              <div className="flex items-center space-x-2">

                <RadioGroupItem
                  value="usuario"
                  id="usuario"
                />

                <Label htmlFor="usuario">
                  Registered User
                </Label>

              </div>

              <div className="flex items-center space-x-2">

                <RadioGroupItem
                  value="nombre"
                  id="nombre"
                />

                <Label htmlFor="nombre">
                  Manual Name
                </Label>

              </div>

            </RadioGroup>

          </div>

          {tipoPrestatario === "usuario" ? (

            <div className="space-y-2">

              <Label>
                User
              </Label>

              <Select
                value={usuarioId}
                onValueChange={setUsuarioId}
              >

                <SelectTrigger>

                  <SelectValue
                    placeholder="Select user"
                  />

                </SelectTrigger>

                <SelectContent>

                  {usuarios.length === 0 ? (

                    <div className="p-2 text-sm text-gray-500">

                      There are no users

                    </div>

                  ) : (

                    usuarios.map((usuario) => (

                      <SelectItem
                        key={usuario.id}
                        value={usuario.id.toString()}
                      >

                        {usuario.nombre}
                        {" "}
                        ({usuario.email})

                      </SelectItem>
                    ))
                  )}

                </SelectContent>

              </Select>

            </div>

          ) : (

            <div className="space-y-2">

              <Label>
                Name
              </Label>

              <Input
                value={nombrePrestatario}
                onChange={(e) =>
                  setNombrePrestatario(
                    e.target.value
                  )
                }
                placeholder="Ej: Juan"
              />

            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">

            <Button
              type="button"
              variant="outline"
              onClick={() => setOpen(false)}
            >

              Cancel

            </Button>

            <Button
              type="submit"
              disabled={loading}
            >

              {loading
                ? "Creating..."
                : "Create Loan"}

            </Button>

          </div>

        </form>

      </DialogContent>

    </Dialog>
  );
}