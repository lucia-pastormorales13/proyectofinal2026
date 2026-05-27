import { createRoot } from "react-dom/client";
import App from "./App.jsx"; // 👈 Asegúrate de que apunte a tu nuevo App.jsx
import "./styles/index.css";

// Quitamos el "!" del getElementById porque en JavaScript no se usa
createRoot(document.getElementById("root")).render(<App />);