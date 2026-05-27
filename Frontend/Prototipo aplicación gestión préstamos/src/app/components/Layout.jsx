import React, { useEffect, useState } from "react";
import { Outlet, useNavigate, Link, useLocation } from "react-router-dom";
import { Button } from "./ui/button";
import { LogOut, Package, ClipboardList } from "lucide-react";
import logo from "../Assets/Logotexto.png";

function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem("token");
  const [nombreUsuario, setNombreUsuario] = useState(localStorage.getItem("nombreUsuario") || "Usuario");

  useEffect(() => {
    if (!token) {
      navigate("/login");
    }
    
    // ✅ Refrescamos el nombre por si cambió en el login
    const nombreGuardado = localStorage.getItem("nombreUsuario");
    if (nombreGuardado) {
      setNombreUsuario(nombreGuardado);
    }
  }, [token, navigate]);

  if (!token) {
    return null;
  }

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("nombreUsuario"); // Limpiamos también el nombre al salir
    navigate("/login");
    window.location.reload();
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-8">
              <div className="flex items-center gap-2">
                <div className="size-10 rounded-full bg-blue-600 flex items-center justify-center">
                  <img src={logo} alt="Logotexto" className="h-6 w-6 object-contain" />
                </div>
                <h1 className="text-xl font-semibold text-gray-900">
                  Bumeran
                </h1>
              </div>

              <nav className="flex gap-1">
                <Link to="/">
                  <Button
                    variant={location.pathname === "/" ? "default" : "ghost"}
                    className="gap-2"
                  >
                    <Package className="size-4" />
                    My Items
                  </Button>
                </Link>
                <Link to="/prestamos">
                  <Button
                    variant={location.pathname === "/prestamos" ? "default" : "ghost"}
                    className="gap-2"
                  >
                    <ClipboardList className="size-4" />
                    Loans
                  </Button>
                </Link>
              </nav>
            </div>

            <div className="flex items-center gap-4">
              <span className="text-sm text-gray-600">
                Welcome to Bumeran!
              </span>
              <Button variant="outline" onClick={handleLogout} className="gap-2">
                <LogOut className="size-4" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;