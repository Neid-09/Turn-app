import { Users } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"

export default function EmpleadosPanel() {
  return (
    <div className="max-w-md mx-auto p-4 space-y-6">
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
    </div>
  )
}
