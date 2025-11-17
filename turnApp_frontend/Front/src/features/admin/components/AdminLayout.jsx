import { Outlet } from 'react-router-dom';
import AdminBottomNav from './AdminBottomNav';

export default function AdminLayout() {
  return (
    <div className="min-h-screen bg-[#f7f8fa] font-sans">
      {/* El Outlet renderiza el componente de la ruta actual (ej: AdminDashboard) */}
      <main className="pb-24">
        <Outlet />
      </main>

      {/* La barra de navegación fija del Admin */}
      <AdminBottomNav />
    </div>
  );
}
