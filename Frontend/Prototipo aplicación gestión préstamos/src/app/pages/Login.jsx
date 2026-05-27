import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from "../components/ui/card";
import { Package } from "lucide-react";
import { toast } from "sonner";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, password: password }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Credenciales incorrectas");
      }

      const data = await response.json();

      // ✅ LOGIN CORRECTO
      if (data && data.token) {
        localStorage.setItem("token", data.token);

        // 1. Guardar el nombre del usuario (ajusta 'nombre' si tu backend lo devuelve como 'name' o 'userName')
        localStorage.setItem("nombreUsuario", data.nombre || "Usuario"); 

        // 2. Obtener el ID del usuario
        let usuarioIdFinal = null;
        try {
          const payload = JSON.parse(atob(data.token.split(".")[1]));
          usuarioIdFinal = payload.id;
        } catch (e) {
          console.error("Error al decodificar token:", e);
        }

        if (!usuarioIdFinal) {
          usuarioIdFinal = data.id || data.userId || data.usuarioId;
        }

        if (!usuarioIdFinal) {
          throw new Error("El servidor no proporcionó el identificador del usuario.");
        }

        localStorage.setItem("usuarioId", usuarioIdFinal);
        toast.success("¡Inicio de sesión correcto!");
        window.location.href = "/";

      } else {
        throw new Error("El servidor no devolvió un token válido.");
      }
    } catch (err) {
      console.error("Error en Login:", err);
      toast.error(err.message || "Error al iniciar sesión.");
    } finally {
      setLoading(false);
    }
  };
  

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-2 text-blue-600">
            <Package className="size-10" />
          </div>
          <CardTitle className="text-2xl font-bold">
            Login to Your Account
          </CardTitle>
          <CardDescription>
            Enter your credentials to access the platform
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="tu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Authenticating..." : "Login"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Don't have an account?{" "}
              <Link to="/register" className="text-blue-600 hover:underline">
                Register here
              </Link>
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}