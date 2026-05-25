import { useEffect } from "react";
import { Outlet, useNavigate, Link, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { Button } from "./ui/button";
import { LogOut, Package, ClipboardList } from "lucide-react";

export default function Layout() {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate("/login");
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return null;
  }

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header / Navigation bar */}
      <header className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-8">
              <div className="flex items-center gap-2">
                <div className="size-10 rounded-full bg-blue-600 flex items-center justify-center">
                  <Package className="size-5 text-white" />
                </div>
                <h1 className="text-xl font-semibold text-gray-900">
                  Loan Management
                </h1>
              </div>

              {/* Navigation */}
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
                Welcome, <span className="font-medium text-gray-900">{user?.nombre}</span>
              </span>
              <Button variant="outline" onClick={handleLogout} className="gap-2">
                <LogOut className="size-4" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}
