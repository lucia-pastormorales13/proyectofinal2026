import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

// 📁 IMPORTACIÓN FORZADA CON LA EXTENSIÓN REAL .tsx
import Login from "./app/pages/Login.jsx";
import Register from "./app/pages/Register.jsx"; 
import Dashboard from "./app/pages/Dashboard.jsx";
import Prestamos from "./app/pages/Prestamos.jsx";
import Layout from "./app/components/Layout.jsx";
import { Toaster } from "./app/components/ui/sonner";

export default function App() {
  // Comprobamos si hay token en el localStorage para saber si está logueado
  const token = localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas Públicas: Autenticación */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Rutas Protegidas: Usan el menú lateral (Layout) si hay token */}
        <Route 
          path="/" 
          element={token ? <Layout /> : <Navigate replace to="/login" />}
        >
          {/* Al entrar a "/" carga el Dashboard */}
          <Route index element={<Dashboard />} />
          {/* Al entrar a "/prestamos" carga la pantalla de préstamos */}
          <Route path="prestamos" element={<Prestamos />} />
        </Route>

        {/* Redirección por si meten una ruta que no existe */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      
      {/* Componente para las notificaciones flotantes en verde/rojo */}
      <Toaster />
    </BrowserRouter>
  );
}