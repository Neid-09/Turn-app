import { Users, Calendar, FileText, Bell, Plus, UserPlus } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { BottomNav } from "@/components/bottom-nav"
// 1
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
              <div className="w-10 h-10 bg-secondary rounded-full flex items-center justify-center">
                <Calendar className="w-5 h-5" />
              </div>
            </div>
          </Card>

          <Card className="p-4 rounded-2xl shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs text-muted-foreground mb-1">Solicitudes</p>
                <p className="text-2xl font-bold">00</p>
              </div>
              <div className="w-10 h-10 bg-secondary rounded-full flex items-center justify-center">
                <FileText className="w-5 h-5" />
              </div>
            </div>
          </Card>

          <Card className="p-4 rounded-2xl shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs text-muted-foreground mb-1">Avisos</p>
                <p className="text-2xl font-bold">00</p>
              </div>
              <div className="w-10 h-10 bg-secondary rounded-full flex items-center justify-center">
                <Bell className="w-5 h-5" />
              </div>
            </div>
          </Card>
        </div>

        {/* Quick Actions */}
        <div className="space-y-3">
          <h3 className="text-sm font-semibold text-foreground">Acciones rápidas</h3>

          <Button
            variant="outline"
            className="w-full h-14 justify-start gap-3 bg-white rounded-2xl shadow-sm border-border"
          >
            <div className="w-8 h-8 bg-black rounded-lg flex items-center justify-center">
              <Plus className="w-5 h-5 text-white" />
            </div>
            <span className="text-sm font-medium">Crear nuevo horario</span>
          </Button>

          <Button
            variant="outline"
            className="w-full h-14 justify-start gap-3 bg-white rounded-2xl shadow-sm border-border"
          >
            <div className="w-8 h-8 bg-black rounded-lg flex items-center justify-center">
              <UserPlus className="w-5 h-5 text-white" />
            </div>
            <span className="text-sm font-medium">Gestionar empleados</span>
          </Button>
        </div>

        {/* Recent Schedules */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Horarios recientes</h3>
            <Button
              variant="ghost"
              size="sm"
              className="h-7 px-3 bg-black text-white hover:bg-black/90 rounded-full text-xs"
            >
              Ver todos
            </Button>
          </div>

          <div className="space-y-2">
            {[1, 2, 3].map((item) => (
              <Card key={item} className="p-4 rounded-2xl shadow-sm">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <p className="text-sm font-medium text-foreground">Horario Turno Mañana</p>
                    <p className="text-xs text-muted-foreground">Departamento · 00 empleados</p>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    className="h-8 px-4 rounded-full text-xs border-border bg-transparent"
                  >
                    {item === 1 ? "Publicado" : item === 2 ? "Borrador" : "Pendiente"}
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </div>

      <BottomNav activeTab="inicio" role="admin" />
    </div>
  )
}
