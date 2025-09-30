import { Clock, Settings, FileText } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { BottomNav } from "@/components/bottom-nav"

export default function EmpleadoDashboard() {
  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <header className="bg-[#2a2a2a] text-white p-4">
        <h1 className="text-sm font-medium">Dashboard Empleado(a)</h1>
      </header>

      <div className="max-w-md mx-auto p-4 space-y-6">
        {/* Welcome Section */}
        <div className="bg-white rounded-2xl p-6 shadow-sm">
          <h2 className="text-xl font-bold text-foreground mb-1">Panel empleado</h2>
          <p className="text-base text-foreground">¡Hola, Juan Pérez!</p>
          <p className="text-sm text-muted-foreground">Empleado(a)</p>
        </div>

        {/* Next Shift */}
        <Card className="p-6 rounded-2xl shadow-sm">
          <div className="flex items-start gap-4 mb-4">
            <Clock className="w-8 h-8 text-foreground" />
            <div className="flex-1">
              <p className="text-sm text-foreground mb-1">Tu próximo turno es hoy a las 00:00</p>
              <p className="text-xs text-muted-foreground">00:00 - 00:00</p>
              <p className="text-xs text-muted-foreground">Descanso: 00:00 - 00:00</p>
            </div>
            <Button size="sm" className="h-8 px-4 bg-black hover:bg-black/90 text-white rounded-full text-xs">
              Activo
            </Button>
          </div>
        </Card>

        {/* Stats */}
        <div className="grid grid-cols-2 gap-4">
          <Card className="p-6 rounded-2xl shadow-sm border-2 border-primary">
            <p className="text-3xl font-bold text-center mb-2">00h</p>
            <p className="text-sm text-center text-muted-foreground">Esta semana</p>
          </Card>

          <Card className="p-6 rounded-2xl shadow-sm border-2 border-primary">
            <p className="text-3xl font-bold text-center mb-2">00</p>
            <p className="text-sm text-center text-muted-foreground">Próximos turnos</p>
          </Card>
        </div>

        {/* Quick Actions */}
        <div className="space-y-3">
          <h3 className="text-sm font-semibold text-foreground">Acciones rápidas</h3>

          <Button
            variant="outline"
            className="w-full h-14 justify-start gap-3 bg-white rounded-2xl shadow-sm border-border"
          >
            <div className="w-8 h-8 bg-secondary rounded-lg flex items-center justify-center">
              <Settings className="w-5 h-5" />
            </div>
            <span className="text-sm font-medium">Configuración</span>
          </Button>

          <Button
            variant="outline"
            className="w-full h-14 justify-start gap-3 bg-white rounded-2xl shadow-sm border-border"
          >
            <div className="w-8 h-8 bg-secondary rounded-lg flex items-center justify-center">
              <FileText className="w-5 h-5" />
            </div>
            <span className="text-sm font-medium">Solicitudes</span>
          </Button>
        </div>
      </div>

      <BottomNav activeTab="inicio" role="empleado" />
    </div>
  )
}
