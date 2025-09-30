import { Users, Calendar, FileText, Bell, Plus, UserPlus } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { BottomNav } from "@/components/bottom-nav"

export default function AdminDashboard() {
  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <header className="bg-[#2a2a2a] text-white p-4">
        <h1 className="text-sm font-medium">Dashboard Administrador(a)</h1>
      </header>

      <div className="max-w-md mx-auto p-4 space-y-6">
        {/* Welcome Section */}
        <div className="bg-white rounded-2xl p-6 shadow-sm">
          <h2 className="text-xl font-bold text-foreground mb-1">Panel Administrativo</h2>
          <p className="text-sm text-muted-foreground">Bienvenido, Nombre usuario</p>
        </div>

          {/* Panel de Empleado */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-primary">
            <h3 className="text-lg font-semibold text-primary mb-2 flex items-center gap-2">
              <Users className="w-5 h-5" /> Panel de Empleado
            </h3>
            <div className="flex items-center gap-4">
              <img src="/placeholder-user.jpg" alt="Empleado" className="w-12 h-12 rounded-full border" />
              <div>
                <p className="text-base font-medium text-foreground">Juan Pérez</p>
                <p className="text-xs text-muted-foreground">Empleado(a)</p>
                <p className="text-xs text-muted-foreground">Próximo turno: 00:00 - 00:00</p>
              </div>
              <Button size="sm" className="h-8 px-4 bg-black hover:bg-black/90 text-white rounded-full text-xs">Activo</Button>
            </div>
            <div className="mt-4 flex gap-2">
              <Button variant="outline" size="sm">Ver detalles</Button>
              <Button variant="secondary" size="sm">Configurar</Button>
            </div>
          </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-4">
          <Card className="p-4 rounded-2xl shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs text-muted-foreground mb-1">Empleados</p>
                <p className="text-2xl font-bold">00</p>
              </div>
              <div className="w-10 h-10 bg-secondary rounded-full flex items-center justify-center">
                <Users className="w-5 h-5" />
              </div>
            </div>
          </Card>

          <Card className="p-4 rounded-2xl shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs text-muted-foreground mb-1">Horarios</p>
                <p className="text-2xl font-bold">00</p>
              </div>
              {/* ...existing code... */}
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
